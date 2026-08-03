package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.location.DTO.AddressDTO;
import com.mns.cda.saas_facturation.product.DTO.MakerDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.product.Iservice.IMakerService;
import com.mns.cda.saas_facturation.product.controller.MakerController;
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


@WebMvcTest(controllers = MakerController.class)
public class MakerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IMakerService makerService;

    @Autowired
    private ObjectMapper objectMapper;

    private MakerDTO makerDTO;
    private MakerRequestDTO makerRequestDTO;

    @BeforeEach
    void setUp() {
        AddressDTO addressDTO = mock(AddressDTO.class);

        makerDTO = new MakerDTO(
                1L,
                "Lenovo",
                "lenovo@gmail.com",
                "+33618765635",
                addressDTO
        );

        makerRequestDTO = new MakerRequestDTO(
                "Lenovo",
                "lenovo@gmail.com",
                "+33618765635",
                1L
        );
    }

    // ------------------------------------------------------------------
    // GET /maker/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /maker/list -> 200 et la liste des constructeurs")
    void getMakerList_devraitRetourner200() throws Exception {
        when(makerService.findAll()).thenReturn(List.of(makerDTO));

        mockMvc.perform(get("/maker/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].mkrId").value(1))
                .andExpect(jsonPath("$[0].mkrName").value("Lenovo"));

        verify(makerService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /maker/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /maker/{id} -> 200 quand le constructeur existe")
    void getMaker_devraitRetourner200() throws Exception {
        when(makerService.findById(1L)).thenReturn(makerDTO);

        mockMvc.perform(get("/maker/{mkrId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mkrId").value(1))
                .andExpect(jsonPath("$.mkrName").value("Lenovo"));
        verify(makerService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /maker/{id} -> 404 quand le constructeur n'existe pas")
    void getMakerById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(makerService.findById(99L)).thenThrow(new ResourceNotFoundException("Maker not found"));

        mockMvc.perform(get("/maker/{mkrId}", 99L))
                .andExpect(status().isNotFound());

        verify(makerService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /maker
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /maker -> 201 avec un corps valide")
    void createMaker_avecDonneesValides_devraitRetourner201EtConstructeurReferenceCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(makerService.create(any(MakerRequestDTO.class))).thenReturn(makerDTO);

        mockMvc.perform(post("/maker")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java makerRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(makerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mkrId").value(1));

        verify(makerService, times(1)).create(any(MakerRequestDTO.class));
    }


    // ------------------------------------------------------------------
    // PUT /maker/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /maker/{id} -> 200 avec le constructeur modifié")
    void updateMaker_devraitRetourner200EtLeConstructeurModifie() throws Exception {
        when(makerService.modify(eq(1L), any(MakerRequestDTO.class))).thenReturn(makerDTO);

        mockMvc.perform(put("/maker/{mkrId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(makerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mkrId").value(1));

        verify(makerService, times(1)).modify(eq(1L), any(MakerRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /maker/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /maker/{id} -> 204 sans corps")
    void deleteMaker_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(makerService).delete(1L);

        mockMvc.perform(delete("/maker/{mkrId}", 1L))
                .andExpect(status().isNoContent());

        verify(makerService, times(1)).delete(1L);
    }
}
