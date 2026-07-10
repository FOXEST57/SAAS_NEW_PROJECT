package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.TvaResponseDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.ArticleUpdateDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.controller.ArticleController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
 * Tests unitaires du {@link ArticleController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link ArticleRequestDTO}</li>
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
@WebMvcTest(controllers = ArticleController.class)
class ArticleControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link com.mns.cda.saas_facturation.Iservice.IArticleService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private IArticleService articleService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link ArticleRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private ArticleDTO articleDTO;
    private ArticleRequestDTO articleRequestDTO;
    private ArticleUpdateDTO articleUpdateDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     *
     * <p>{@link PostalCodeDTO} et {@link CityDTO} sont ici de simples mocks Mockito :
     * on ne connaît/teste pas leur contenu dans ce fichier, seulement le fait que
     * {@code AddressDTO} les transporte correctement. Remplace ces lignes par de
     * vraies instances si tu préfères vérifier des champs précis (ex.
     * {@code postalCode.pCodeValue()}).</p>
     */
    @BeforeEach
    void setUp() {
        TvaResponseDTO tvaDTO = mock(TvaResponseDTO.class);

        LocalDateTime dateCreation = LocalDateTime.of(2026, 1, 15, 10, 30);
        LocalDateTime dateModification = LocalDateTime.of(2026, 1, 20, 14, 0);

        articleDTO = new ArticleDTO(
                1L,                  // artId
                "Référence",              // artReference
                "ArtName",                // artName
                "artDescription",         // artDescription
                BigDecimal.valueOf(1),    // artPriceExcludeTaxes
                1,                        // artStock
                tvaDTO,                   // tva
                BigDecimal.valueOf(1.2),  // artPriceTTC
                dateCreation,             // artCreatedDate
                dateModification,         // artUpdatedDate
                List.of(),                // categories
                List.of(),                // suppliers
                List.of()                 // makers
        );

        articleUpdateDTO = new ArticleUpdateDTO(
                "Référence",   // artReference
                "ArtName",                // artName
                "artDescription",         // artDescription
                BigDecimal.valueOf(1),    // artPriceExcludeTaxes
                1,                        // artStock
                1L,                       // tva
                List.of()                 // categories
        );

        articleRequestDTO = new ArticleRequestDTO(
                "Référence",              // artReference
                "ArtName",                // artName
                "artDescription",         // artDescription
                BigDecimal.valueOf(1),    // artPriceExcludeTaxes
                1,                        // artStock
                1L,                       // tvaId
                List.of(),                // categorieId
                List.of()                 // suppliers
        );


    }


    // ------------------------------------------------------------------
    // GET /article/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /article/list -> 200 et la liste des articles")
    void getArticle_devraitRetourner200EtLaListe() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(articleService.findAll()).thenReturn(List.of(articleDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/article/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].artId").value(1))
                .andExpect(jsonPath("$[0].artReference").value("Référence"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(articleService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /article/{artId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : l'article demandée existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /article/{id} -> 200 quand l'article existe")
    void getAddressById_quandTrouvee_devraitRetourner200EtLAdresse() throws Exception {
        when(articleService.findById(1L)).thenReturn(Optional.of(articleDTO));

        // {artId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/article/{artId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artId").value(1))
                .andExpect(jsonPath("$.artReference").value("Référence"));

        verify(articleService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : l'article n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code ArticleController#getArticleById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /article/{id} -> 404 quand l'article n'existe pas")
    void getArticleById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(articleService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/article/{artId}", 99L))
                .andExpect(status().isNotFound());

        verify(articleService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /article
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'article créé dans le corps.
     */
    @Test
    @DisplayName("POST /article -> 201 avec un corps valide")
    void createArticle_avecDonneesValides_devraitRetourner201EtLArticleCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(articleService.create(any(ArticleRequestDTO.class))).thenReturn(articleDTO);

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java articleRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(articleRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artId").value(1));

        verify(articleService, times(1)).create(any(ArticleRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeId} est obligatoire
     * ({@code @NotNull} dans {@link ArticleRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /article -> 400 quand artReference est manquant")
    void createArticle_sansArtReference_devraitRetourner400() throws Exception {

        String json = """
            {
              "artName": "Article",
              "artDescription": "Description",
              "artPriceExcludeTaxes": 10.5,
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand artName est manquant")
    void createArticle_sansArtName_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artDescription": "Description",
              "artPriceExcludeTaxes": 10.5,
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand artDescription est manquant")
    void createArticle_sansArtDescription_devraitRetourner400() throws Exception {

        String json = """
            {
              "artName" : "Test"
              "artReference": "REF001",
              "artPriceExcludeTaxes": 10.5,
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand artPriceExcludeTaxes est manquant")
    void createArticle_sansPrixHT_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artName": "Article",
              "artDescription": "Description",
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand le prix HT est négatif")
    void createArticle_prixNegatif_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artName": "Article",
              "artDescription": "Description",
              "artPriceExcludeTaxes": -1,
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand le prix HT est égal à 0")
    void createArticle_prixZero_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artName": "Article",
              "artDescription": "Description",
              "artPriceExcludeTaxes": 0,
              "artStock": 5,
              "tvaId": 1
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand tvaId est manquant")
    void createArticle_sansTvaId_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artName": "Article",
              "artDescription": "Description",
              "artPriceExcludeTaxes": 10.5,
              "artStock": 5
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    @Test
    @DisplayName("POST /article -> 400 quand tvaId égal à 0")
    void createArticle_TvaIdZero_devraitRetourner400() throws Exception {

        String json = """
            {
              "artReference": "REF001",
              "artName": "Article",
              "artDescription": "Description",
              "artPriceExcludeTaxes": 10.5,
              "artStock": 5
              "tvaId": 0
            }
            """;

        mockMvc.perform(post("/article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /article/{artId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'un article existant -> 200 + article modifiée.
     */
    @Test
    @DisplayName("PUT /article/{id} -> 200 avec l'article modifiée")
    void updateAddress_devraitRetourner200EtLAdresseModifiee() throws Exception {
        when(articleService.update(eq(1L), any(ArticleUpdateDTO.class))).thenReturn(articleDTO);

        mockMvc.perform(put("/article/{artId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artId").value(1));

        verify(articleService, times(1)).update(eq(1L), any(ArticleUpdateDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /article/{artId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /article/{id} -> 204 sans corps")
    void deleteArticle_devraitRetourner204() throws Exception {

        // L'article existe
        when(articleService.findById(1L)).thenReturn(Optional.of(articleDTO));

        // delete est une méthode void
        doNothing().when(articleService).delete(1L);

        mockMvc.perform(delete("/article/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(articleService).findById(1L);
        verify(articleService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /article/{id} -> 404 quand l'article n'existe pas")
    void deleteArticle_Inexistant_devraitRetourner404() throws Exception {

        when(articleService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/article/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(articleService).findById(1L);
        verify(articleService, never()).delete(anyLong());
    }
}
