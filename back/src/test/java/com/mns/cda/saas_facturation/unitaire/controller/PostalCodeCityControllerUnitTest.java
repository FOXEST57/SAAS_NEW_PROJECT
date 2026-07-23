package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.PostalCodeCityDTO;
import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.controller.PostalCodeCityController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitaires du {@link PostalCodeCityController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link PostalCodeCityRequestDTO}</li>
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
@WebMvcTest(controllers = PostalCodeCityController.class)
class PostalCodeCityControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link IPostalCodeCityService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private IPostalCodeCityService postalCodeCityService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link PostalCodeCityRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private PostalCodeCityDTO postalCodeCityDTO;
    private PostalCodeCityRequestDTO postalCodeCityRequestDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     *
     * <p>{@link PostalCodeDTO} et {@link CityDTO} sont ici de simples mocks Mockito :
     * on ne connaît/teste pas leur contenu dans ce fichier, seulement le fait que
     * {@code PostalCodeCityDTO} les transporte correctement. Remplace ces lignes par de
     * vraies instances si tu préfères vérifier des champs précis (ex.
     * {@code postalCode.pCodeValue()}).</p>
     */
    @BeforeEach
    void setUp() {
        PostalCodeDTO postalCodeDTO = mock(PostalCodeDTO.class);
        CityDTO cityDTO = mock(CityDTO.class);

        postalCodeCityDTO = new PostalCodeCityDTO(
                postalCodeDTO,
                cityDTO
        );

        postalCodeCityRequestDTO = new PostalCodeCityRequestDTO(
                1L,                 // pCodeId (obligatoire, @NotNull)
                1L                  // cityId (obligatoire, @NotNull)
        );
    }

    // ------------------------------------------------------------------
    // GET /postalcodecity/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /postalcodecity/list -> 200 et la liste des objets")
    void getPostalCodeCities_shouldReturn200AndList() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(postalCodeCityService.findAll()).thenReturn(List.of(postalCodeCityDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/postalcodecity/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].postalCode.pCodeId").value(postalCodeCityDTO.postalCode().pCodeId()))
                .andExpect(jsonPath("$[0].postalCode.pCodeName").value(postalCodeCityDTO.postalCode().pCodeName()))
                .andExpect(jsonPath("$[0].city.cityId").value(postalCodeCityDTO.city().cityId()))
                .andExpect(jsonPath("$[0].city.cityName").value(postalCodeCityDTO.city().cityName()))
                .andExpect(jsonPath("$[0].city.country").value(postalCodeCityDTO.city().country()));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(postalCodeCityService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /postalcodecity/pCodeId/cityId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : l'objet demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /postalcodecity/{pCodeId}/{cityId} -> 200 quand l'objet existe")
    void getPostalCodeCityById_whenFound_shouldReturn200AndObject() throws Exception {
        when(postalCodeCityService.findById(1L, 1L)).thenReturn(postalCodeCityDTO);

        // {pCodeId}/{cityId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/postalcodecity/{pCodeId}/{cityId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode.pCodeId").value(postalCodeCityDTO.postalCode().pCodeId()))
                .andExpect(jsonPath("$.city.cityId").value(postalCodeCityDTO.city().cityId()));

        verify(postalCodeCityService, times(1)).findById(1L, 1L);
    }

    /**
     * Cas d'erreur : l'objet n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code PostalCodeCityController#getPostalCodeCityById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /postalcodecity/{id} -> 404 quand l'objet n'existe pas")
    void getPostalCodeCityById_whenNotFound_shouldReturn404() throws Exception {
        when(postalCodeCityService.findById(99L, 99L)).thenThrow(new ResourceNotFoundException("Lien entre code postal et ville non existant"));

        mockMvc.perform(get("/postalcodecity/{pCodeId}/{cityId}", 99L, 99L))
                .andExpect(status().isNotFound());

        verify(postalCodeCityService, times(1)).findById(99L, 99L);
    }

    // ------------------------------------------------------------------
    // POST /postalCodeCity
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'objet créé dans le corps.
     */
    @Test
    @DisplayName("POST /postalcodecity -> 201 avec un corps valide")
    void createPostalCodeCity_withValidData_shouldReturn201AndCreatedObject() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(postalCodeCityService.create(any(PostalCodeCityRequestDTO.class))).thenReturn(postalCodeCityDTO);

        mockMvc.perform(post("/postalcodecity")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java postalCodeCityRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(postalCodeCityRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postalCode.pCodeId").value(postalCodeCityDTO.postalCode().pCodeId()))
                .andExpect(jsonPath("$.postalCode.pCodeName").value(postalCodeCityDTO.postalCode().pCodeName()))
                .andExpect(jsonPath("$.city.cityId").value(postalCodeCityDTO.city().cityId()))
                .andExpect(jsonPath("$.city.cityName").value(postalCodeCityDTO.city().cityName()))
                .andExpect(jsonPath("$.city.country").value(postalCodeCityDTO.city().country()));

        verify(postalCodeCityService, times(1)).create(any(PostalCodeCityRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeId} est obligatoire
     * ({@code @NotNull} dans {@link PostalCodeCityRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /postalcodecity -> 400 quand pCodeId est manquant")
    void createPostalCodeCity_withoutPCodeId_shouldReturn400() throws Exception {
        String jsonWithoutPCodeId = """
                {
                  "cityId": 1
                }
                """;

        mockMvc.perform(post("/postalcodecity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(postalCodeCityService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code cityId} est obligatoire
     * ({@code @NotNull} dans {@link PostalCodeCityRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /postalcodecity -> 400 quand cityId est manquant")
    void createPostalCodeCity_withoutCityId_shouldReturn400() throws Exception {
        String jsonWithoutCityId = """
                {
                  "pCodeId": 1
                }
                """;

        mockMvc.perform(post("/postalcodecity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutCityId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(postalCodeCityService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /postalcodecity/pCodeId/cityId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'un objet existant -> 200 + objet modifié.
     */
    @Test
    @DisplayName("PUT /postalcodecity/{pCodeId}/{cityId} -> 200 avec l'objet modifié")
    void updatePostalCodeCity_shouldReturn200AndModifiedObject() throws Exception {
        when(postalCodeCityService.update(eq(1L), eq(1L), any(PostalCodeCityRequestDTO.class))).thenReturn(postalCodeCityDTO);

        mockMvc.perform(put("/postalcodecity/{pCodeId}/{cityId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postalCodeCityRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postalCode.pCodeId").value(postalCodeCityDTO.postalCode().pCodeId()))
                .andExpect(jsonPath("$.postalCode.pCodeName").value(postalCodeCityDTO.postalCode().pCodeName()))
                .andExpect(jsonPath("$.city.cityId").value(postalCodeCityDTO.city().cityId()))
                .andExpect(jsonPath("$.city.cityName").value(postalCodeCityDTO.city().cityName()))
                .andExpect(jsonPath("$.city.country").value(postalCodeCityDTO.city().country()));

        verify(postalCodeCityService, times(1)).update(eq(1L), eq(1L), any(PostalCodeCityRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /postalcodecity/pCodeId/cityId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /postalcodecity/{pCodeId}/{cityId} -> 204 sans corps")
    void deletePostalCodeCity_shouldReturn204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(postalCodeCityService).delete(1L, 1L);

        mockMvc.perform(delete("/postalcodecity/{pCodeId}/{cityId}", 1L, 1L))
                .andExpect(status().isNoContent());

        verify(postalCodeCityService, times(1)).delete(1L, 1L);
    }
}
