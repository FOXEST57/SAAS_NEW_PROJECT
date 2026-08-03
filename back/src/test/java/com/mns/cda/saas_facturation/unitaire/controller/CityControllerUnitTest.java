package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.location.DTO.CityDTO;
import com.mns.cda.saas_facturation.location.DTO.CountryDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.location.Iservice.ICityService;
import com.mns.cda.saas_facturation.location.controller.CityController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitaires du {@link CityController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link CityRequestDTO}</li>
 *   <li>le déclenchement de la validation Bean Validation ({@code @Valid})</li>
 *   <li>la sérialisation JSON de la réponse</li>
 * </ul>
 *
 * <p><b>Pourquoi {@code @WebMvcTest} ?</b><br>
 * Cette annotation ne charge que la couche web de Spring (controllers, filtres,
 * config MVC) et pas les couches service/repository/base de données. C'est donc
 * rapide, et cohérent avec l'objectif : tester uniquement le contrat HTTP du
 * controller, pas la logique métier réelle (qui est mockée).</p>
 */
@WebMvcTest(controllers = CityController.class)
class CityControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ICityService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ICityService cityService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link CityRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private CityDTO cityDTO;
    private CityRequestDTO cityRequestDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     */
    @BeforeEach
    void setUp() {
        
        CountryDTO countryDTO = mock(CountryDTO.class);

        cityDTO = new CityDTO(
                1L,  //cityId,
                "Metz",    //cityName,
                countryDTO //country
        );

        cityRequestDTO = new CityRequestDTO(
                "Metz",               // cityName
                1L                            // cntId
        );
    }

    // ------------------------------------------------------------------
    // GET /city/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /city/list -> 200 et la liste des villes")
    void getCities_devraitRetourner200EtLaListe() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(cityService.findAll()).thenReturn(List.of(cityDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/city/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cityId").value(1))
                .andExpect(jsonPath("$[0].cityName").value("Metz"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(cityService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /city/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : l'adresse demandée existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /city/{id} -> 200 quand l'adresse existe")
    void getAddressById_quandTrouvee_devraitRetourner200EtLAdresse() throws Exception {
        when(cityService.findById(1L)).thenReturn(Optional.of(cityDTO));

        // {addId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/city/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityId").value(1))
                .andExpect(jsonPath("$.cityName").value("Metz"));

        verify(cityService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : l'adresse n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code CityController#getAddressById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /city/{id} -> 404 quand l'adresse n'existe pas")
    void getAddressById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(cityService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/city/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(cityService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /city
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'adresse créée dans le corps.
     */
    @Test
    @DisplayName("POST /city -> 201 avec un corps valide")
    void createAddress_avecDonneesValides_devraitRetourner201EtLAdresseCreee() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(cityService.create(any(CityRequestDTO.class))).thenReturn(cityDTO);

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java cityRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(cityRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cityId").value(1));

        verify(cityService, times(1)).create(any(CityRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code cityName} est obligatoire
     * ({@code @NotNull} dans {@link CityRequestDTO}). Si on l'omet, Bean
     * Validation doit rejeter la requête avant même d'atteindre le service
     * (grâce à {@code @Valid} sur le paramètre du controller).
     *
     * <p>On construit ici directement une chaîne JSON à la main plutôt que de
     * sérialiser un DTO, pour représenter précisément une requête "cassée" que
     * l'{@code ObjectMapper} ne permettrait pas facilement de générer depuis un
     * record Java (les records n'autorisent pas de champ "manquant" à la
     * construction).</p>
     */
    @Test
    @DisplayName("POST /city -> 400 quand cityName est manquant")
    void createAddress_sansCtyName_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {
                    "ctnId": 1L
                }
                """;

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(cityService, never()).create(any());
    }

    @Test
    @DisplayName("POST /city -> 400 quand ctnId est manquant")
    void createAddress_sansCtnId_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {
                    "ctyName": "Metz"
                }
                """;

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(cityService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /city/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'une adresse existante -> 200 + adresse modifiée.
     */
    @Test
    @DisplayName("PUT /city/{id} -> 200 avec l'adresse modifiée")
    void updateAddress_devraitRetourner200EtLAdresseModifiee() throws Exception {
        when(cityService.update(eq(1L), any(CityRequestDTO.class))).thenReturn(cityDTO);

        mockMvc.perform(put("/city/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityId").value(1));

        verify(cityService, times(1)).update(eq(1L), any(CityRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /city/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /city/{id} -> 204 sans corps")
    void deleteAddress_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(cityService).delete(1L);

        mockMvc.perform(delete("/city/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).delete(1L);
    }
}
