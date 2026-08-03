package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.user.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.AccountTypeRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.IAccountTypeService;
import com.mns.cda.saas_facturation.user.controller.AccountTypeController;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AccountTypeController.class)
public class AccountTypeControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAccountTypeService AccountTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountTypeDTO AccountTypeDTO;
    private AccountTypeRequestDTO AccountTypeRequestDTO;

    @BeforeEach
    void setUp() {

        AccountTypeDTO = new AccountTypeDTO(
                1L,
                "Client"
        );

        AccountTypeRequestDTO = new AccountTypeRequestDTO(
                "User"
        );
    }

    // ------------------------------------------------------------------
    // GET /AccountType/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /AccountType/list -> 200 et la liste des Types de compte")
    void getAccountTypeList_devraitRetourner200() throws Exception {
        when(AccountTypeService.findAll()).thenReturn(List.of(AccountTypeDTO));

        mockMvc.perform(get("/AccountType/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accTypeId").value(1))
                .andExpect(jsonPath("$[0].accTypeLibelle").value("Client"));

        verify(AccountTypeService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /AccountType/accTypeId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /AccountType/{id} -> 200 quand le type de compte existe")
    void getAccountType_devraitRetourner200() throws Exception {
        when(AccountTypeService.findById(1L)).thenReturn(AccountTypeDTO);

        mockMvc.perform(get("/AccountType/{accTypeId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accTypeId").value(1))
                .andExpect(jsonPath("$.accTypeLibelle").value("Client"));
        verify(AccountTypeService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /AccountType/{id} -> 404 quand le type de compte n'existe pas")
    void getAccountTypeById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(AccountTypeService.findById(99L)).thenThrow(new ResourceNotFoundException("AccountType not found"));

        mockMvc.perform(get("/AccountType/{accTypeId}", 99L))
                .andExpect(status().isNotFound());

        verify(AccountTypeService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /AccountType
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /AccountType -> 201 avec un corps valide")
    void createAccountType_avecDonneesValides_devraitRetourner201EtConstructeurReferenceCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(AccountTypeService.create(any(AccountTypeRequestDTO.class))).thenReturn(AccountTypeDTO);

        mockMvc.perform(post("/AccountType")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java AccountTypeRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(AccountTypeRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accTypeId").value(1));

        verify(AccountTypeService, times(1)).create(any(AccountTypeRequestDTO.class));
    }


    // ------------------------------------------------------------------
    // PUT /AccountType/accTypeId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /AccountType/{id} -> 200 avec le constructeur modifié")
    void updateAccountType_devraitRetourner200EtLeConstructeurModifie() throws Exception {
        when(AccountTypeService.modify(eq(1L), any(AccountTypeRequestDTO.class))).thenReturn(AccountTypeDTO);

        mockMvc.perform(put("/AccountType/{mkrId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AccountTypeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accTypeId").value(1));

        verify(AccountTypeService, times(1)).modify(eq(1L), any(AccountTypeRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /AccountType/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /AccountType/{id} -> 204 sans corps")
    void deleteAccountType_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(AccountTypeService).delete(1L);

        mockMvc.perform(delete("/AccountType/{accTypeId}", 1L))
                .andExpect(status().isNoContent());

        verify(AccountTypeService, times(1)).delete(1L);
    }
}
