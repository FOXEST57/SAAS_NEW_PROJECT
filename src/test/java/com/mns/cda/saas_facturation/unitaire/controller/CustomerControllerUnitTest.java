package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.controller.CustomerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
public class CustomerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO customerDTO;
    private CustomerRequestDTO customerRequestDTO;

    @BeforeEach
    void setUp() {
        AddressDTO addressDTO = mock(AddressDTO.class);
        AccountTypeResponseDTO  accountTypeResponseDTO = mock(AccountTypeResponseDTO.class);

        customerDTO = new CustomerDTO(
                1L,
                "Jean",
                "Dupont",
                "dupont.jean@example.com",
                "+33123456789",
                addressDTO,
                accountTypeResponseDTO
        );

        customerRequestDTO = new CustomerRequestDTO(
                "Jean",
                "Dupont",
                "dupont.jean@example.com",
                "+33123456789",
                1L,
                3L
        );
    }

    // ------------------------------------------------------------------
    // GET /customer/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /customer/list -> 200 et la liste des customers")
    void getCustomerList_devraitRetourner200() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ctmId").value(1))
                .andExpect(jsonPath("$[0].ctmFirstName").value("Jean"));

        verify(customerService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /customer/ctmId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /customer/{id} -> 200 quand le customer existe")
    void getCustomer_devraitRetourner200() throws Exception {
        when(customerService.findById(1L)).thenReturn(Optional.of(customerDTO));

        mockMvc.perform(get("/customer/{ctmId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ctmId").value(1))
                .andExpect(jsonPath("$.ctmFirstName").value("Jean"));
        verify(customerService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /customer/{id} -> 404 quand le customer n'existe pas")
    void getCustomerById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(customerService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/{ctmId}", 99L))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /customer
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /customer -> 201 avec un corps valide")
    void createCustomer_avecDonneesValides_devraitRetourner201EtLeClientCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(customerService.create(any(CustomerRequestDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java customerRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ctmId").value(1));

        verify(customerService, times(1)).create(any(CustomerRequestDTO.class));
    }

    @Test
    @DisplayName("POST /customer -> 400 quand addId est manquant")
    void createCustomer_sansAddId_devraitRetourner400() throws Exception {
        String jsonSansAddId = """
                {
                  "ctmFirstName": "Jean",
                  "ctmLastName": "Dupont",
                  "ctmEmail": "jean.dupont@example.com",
                  "ctmPhone": "0123456789"
                }
                """;

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansAddId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(customerService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /customer/ctmId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /customer/{id} -> 200 avec le customer modifié")
    void updateCustomer_devraitRetourner200EtLeClientModifie() throws Exception {
        when(customerService.update(eq(1L), any(CustomerRequestDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(put("/customer/{ctmId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ctmId").value(1));

        verify(customerService, times(1)).update(eq(1L), any(CustomerRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /customer/ctmId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /customer/{id} -> 204 sans corps")
    void deleteCustomer_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(customerService).delete(1L);

        mockMvc.perform(delete("/customer/{ctmId}", 1L))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(1L);
    }
}
