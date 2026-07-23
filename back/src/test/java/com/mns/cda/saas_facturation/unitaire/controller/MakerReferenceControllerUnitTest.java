package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.Iservice.IMakerReferenceService;
import com.mns.cda.saas_facturation.controller.MakerReferenceController;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.model.MakerReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = MakerReferenceController.class)
public class MakerReferenceControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IMakerReferenceService makerReferenceService;

    @Autowired
    private ObjectMapper objectMapper;

    private MakerReferenceDTO makerReferenceDTO;
    private MakerReferenceRequestDTO makerReferenceRequestDTO;
    private UpdateMakerReferenceDTO updateMakerReferenceDTO;
    private ArticleResponseMakerReferenceDTO article;
    private MakerResponseDTO maker;

    @BeforeEach
    void setUp() {

        article = new ArticleResponseMakerReferenceDTO(
                1L,
                "ArticleName",
                "ArticleReference",
                List.of() // ou une liste de SupplierReferenceResponseDTO si tu veux
        );
        maker = new MakerResponseDTO(
                1L,
                "MakerName"
        );

        makerReferenceDTO = new MakerReferenceDTO(
                article,
                maker,
                "Ref-001",
                0,
                BigDecimal.valueOf(1)
        );

        makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "Ref-001",
                0,
                BigDecimal.valueOf(1)
        );

        updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "Ref-001",
                0,
                BigDecimal.valueOf(1)
        );

    }

    // ------------------------------------------------------------------
    // GET /maker-Reference/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /maker-reference/list -> 200 et la liste des reference constructeur")
    void getMakerReferenceList_devraitRetourner200() throws Exception {
        when(makerReferenceService.findAll()).thenReturn(List.of(makerReferenceDTO));


        mockMvc.perform(get("/maker-reference/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].maker.mkrId").value(1))
                .andExpect(jsonPath("$[0].article.artId").value(1))
                .andExpect(jsonPath("$[0].reference").value("Ref-001"));

        verify(makerReferenceService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /makerReference/artId/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /maker-reference/{artId}/{mkrId} -> 200 quand la reference constructeur existe")
    void getMakerReference_devraitRetourner200() throws Exception {
        when(makerReferenceService.findById(1L,1L)).thenReturn(makerReferenceDTO);

        mockMvc.perform(get("/maker-reference/{artId}/{mkrId}",1L,1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker.mkrId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1))
                .andExpect(jsonPath("$.reference").value("Ref-001"));
        verify(makerReferenceService, times(1)).findById(1L,1L);
    }

    @Test
    @DisplayName("GET /makerReference/{artId}/{mkrId} -> 404 quand la reference constructeur n'existe pas")
    void getMakerReferenceById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(makerReferenceService.findById(99L, 99L)).thenThrow(new ResourceNotFoundException("MakerReference not found"));

        mockMvc.perform(get("/maker-reference/{artId}/{mkrId}", 99L, 99L))
                .andExpect(status().isNotFound());

        verify(makerReferenceService, times(1)).findById(99L, 99L);
    }

    // ------------------------------------------------------------------
    // POST /makerReference
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /maker-reference -> 201 avec un corps valide")
    void createMakerReference_avecDonneesValides_devraitRetourner201EtLaReferenceConstructeurCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(makerReferenceService.create(any(MakerReferenceRequestDTO.class))).thenReturn(makerReferenceDTO);

        mockMvc.perform(post("/maker-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java makerReferenceRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(makerReferenceRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maker.mkrId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1))
                .andExpect(jsonPath("$.reference").value("Ref-001"));

        verify(makerReferenceService, times(1)).create(any(MakerReferenceRequestDTO.class));
    }


    // ------------------------------------------------------------------
    // PUT /maker-reference/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /maker-reference/{artId}/{mkrId} -> 200 avec la reference constructeur modifiée")
    void updateMakerReference_devraitRetourner200EtLaReferenceConstructeurModifiee() throws Exception {
        when(makerReferenceService.modify(eq(1L), eq(1L), any(UpdateMakerReferenceDTO.class))).thenReturn(makerReferenceDTO);

        mockMvc.perform(put("/maker-reference/{artId}/{mkrId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMakerReferenceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker.mkrId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1))
                .andExpect(jsonPath("$.reference").value("Ref-001"));

        verify(makerReferenceService, times(1)).modify(eq(1L), eq(1L), any(UpdateMakerReferenceDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /maker-reference/mkrId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /maker-reference/{artId}/{mkrId} -> 204 sans corps")
    void deleteMakerReference_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(makerReferenceService).delete(1L, 1L);

        mockMvc.perform(delete("/maker-reference/{artId}/{mkrId}", 1L, 1L))
                .andExpect(status().isNoContent());

        verify(makerReferenceService, times(1)).delete(1L, 1L);
    }
}
