package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.location.DTO.CountryDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.location.Iservice.ICountryService;
import com.mns.cda.saas_facturation.location.controller.CountryController;
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

@WebMvcTest(controllers = CountryController.class)
public class CountryControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICountryService countryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CountryDTO countryDTO;
    private CountryRequestDTO countryRequestDTO;

    @BeforeEach
    void setUp() {

        countryDTO = new CountryDTO(
                1L,
                "France"
        );

        countryRequestDTO = new CountryRequestDTO(
                "France"
        );
    }

    // ------------------------------------------------------------------
    // GET /country/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /country/list -> 200 et la liste des countries")
    void getCountryList_devraitRetourner200() throws Exception {
        when(countryService.findAll()).thenReturn(List.of(countryDTO));

        mockMvc.perform(get("/country/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cntId").value(1))
                .andExpect(jsonPath("$[0].cntName").value("France"));

        verify(countryService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /country/addId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /country/{id} -> 200 quand le pays existe")
    void getCountry_devraitRetourner200() throws Exception {
        when(countryService.findById(1L)).thenReturn(Optional.of(countryDTO));

        mockMvc.perform(get("/country/{cntId}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cntId").value(1))
                .andExpect(jsonPath("$.cntName").value("France"));
        verify(countryService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /country/{id} -> 404 quand le pays n'existe pas")
    void getCountryById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(countryService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/country/{cntId}", 99L))
                .andExpect(status().isNotFound());

        verify(countryService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /country
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /country -> 201 avec un corps valide")
    void createCountry_avecDonneesValides_devraitRetourner201EtLePaysCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(countryService.create(any(CountryRequestDTO.class))).thenReturn(countryDTO);

        mockMvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java countryRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(countryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cntId").value(1));

        verify(countryService, times(1)).create(any(CountryRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // PUT /country/cntId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /country/{id} -> 200 avec le pays modifié")
    void updateCountry_devraitRetourner200EtLePaysModifie() throws Exception {
        when(countryService.update(eq(1L), any(CountryRequestDTO.class))).thenReturn(countryDTO);

        mockMvc.perform(put("/country/{cntId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cntId").value(1));

        verify(countryService, times(1)).update(eq(1L), any(CountryRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /country/cntId
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /country/{id} -> 204 sans corps")
    void deleteCountry_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(countryService).delete(1L);

        mockMvc.perform(delete("/country/{cntId}", 1L))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).delete(1L);
    }
}
