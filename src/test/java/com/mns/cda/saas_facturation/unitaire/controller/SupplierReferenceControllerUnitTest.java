package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.*;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateSupplierReferenceDTO;
import com.mns.cda.saas_facturation.Iservice.ISupplierReferenceService;
import com.mns.cda.saas_facturation.controller.SupplierReferenceController;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitaires du {@link SupplierReferenceController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link SupplierReferenceRequestDTO}</li>
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
@WebMvcTest(controllers = SupplierReferenceController.class)
class SupplierReferenceControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ISupplierReferenceService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ISupplierReferenceService supplierReferenceService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link SupplierReferenceRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private SupplierReferenceDTO supplierReferenceDTO;
    private SupplierReferenceRequestDTO supplierReferenceRequestDTO;
    private SupplierDTO supplierDTO;
    private ArticleResponseSupplierDTO articleDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     *
     * <p>{@link ArticleResponseSupplierDTO} et {@link SupplierResponseDTO} sont ici de simples mocks Mockito :
     * on ne connaît/teste pas leur contenu dans ce fichier, seulement le fait que
     * {@code SupplierReferenceDTO} les transporte correctement. Remplace ces lignes par de
     * vraies instances si tu préfères vérifier des champs précis (ex.
     * {@code postalCode.pCodeValue()}).</p>
     */
    @BeforeEach
    void setUp() {
        ArticleResponseSupplierDTO articleResponseSupplierDTO = mock(ArticleResponseSupplierDTO.class);
        SupplierResponseDTO supplierResponseDTO = mock(SupplierResponseDTO.class);
        AddressDTO addressDTO = mock(AddressDTO.class);

        supplierReferenceDTO = new SupplierReferenceDTO(
                articleResponseSupplierDTO,
                supplierResponseDTO,
                "référence",
                BigDecimal.valueOf(1),
                1
        );

        supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                1L,
                "référence",
                BigDecimal.valueOf(1),
                1
        );

        supplierDTO = new SupplierDTO(
                1L,                 // splId
                "Jean",               // splName
                "jean@jean.com",      // splEmail
                "+33586995411",       // splPhone
                addressDTO
        );

        articleDTO = new ArticleResponseSupplierDTO(
                1L,
                "référence",
                "name",
                "description",
                BigDecimal.valueOf(1),
                1,
                null,
                null,
                null
        );
    }

    // ------------------------------------------------------------------
    // GET /supplier-reference/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /supplier-reference/list -> 200 et la liste des objets")
    void getSupplierReferences_shouldReturn200AndList() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(supplierReferenceService.findAll()).thenReturn(List.of(supplierReferenceDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/supplier-reference/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].splRefReference").value(supplierReferenceDTO.splRefReference()))
                .andExpect(jsonPath("$[0].supplierPrice").value(supplierReferenceDTO.supplierPrice()))
                .andExpect(jsonPath("$[0].splRefStock").value(supplierReferenceDTO.splRefStock()));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(supplierReferenceService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /supplier-reference/articleId/supplierId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : l'objet demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /supplier-reference/{articleId}/{supplierId} -> 200 quand l'objet existe")
    void getSupplierReferenceById_whenFound_shouldReturn200AndObject() throws Exception {
        when(supplierReferenceService.findById(1L, 1L)).thenReturn(Optional.of(supplierReferenceDTO));

        // {articleId}/{supplierId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/supplier-reference/{articleId}/{supplierId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.splRefReference").value(supplierReferenceDTO.splRefReference()))
                .andExpect(jsonPath("$.supplierPrice").value(supplierReferenceDTO.supplierPrice()))
                .andExpect(jsonPath("$.splRefStock").value(supplierReferenceDTO.splRefStock()));

        verify(supplierReferenceService, times(1)).findById(1L, 1L);
    }

    /**
     * Cas d'erreur : l'objet n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code SupplierReferenceController#getSupplierReferenceById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /supplier-reference/{id} -> 404 quand l'objet n'existe pas")
    void getSupplierReferenceById_whenNotFound_shouldReturn404() throws Exception {
        when(supplierReferenceService.findById(99L, 99L)).thenThrow(new ResourceNotFoundException("Lien entre fournisseur et référence non existant"));

        mockMvc.perform(get("/supplier-reference/{articleId}/{supplierId}", 99L, 99L))
                .andExpect(status().isNotFound());

        verify(supplierReferenceService, times(1)).findById(99L, 99L);
    }

    /**
     * GET tous les fournisseurs correspondant à un article
     * Cas nominal : l'objet demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /supplier-reference/list-supplier/{articleId} -> 200 quand l'objet existe")
    void getSupplierReferenceByArticleId_whenFound_shouldReturn200AndObject() throws Exception {
        when(supplierReferenceService.findByArticleId(1L)).thenReturn(List.of(supplierDTO));

        // {articleId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/supplier-reference/list-supplier/{articleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].splId").value(1))
                .andExpect(jsonPath("$[0].splName").value("Jean"))
                .andExpect(jsonPath("$[0].splEmail").value("jean@jean.com"))
                .andExpect(jsonPath("$[0].splPhone").value("+33586995411"));

        verify(supplierReferenceService, times(1)).findByArticleId(1L);
    }

    /**
     * Cas d'erreur : l'article n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code SupplierReferenceController#getSupplierReferenceById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /supplier-reference/list-supplier/{articleId} -> 404 quand l'article n'existe pas")
    void getSupplierReferenceByArticleId_whenNotFound_shouldReturn404() throws Exception {
        when(supplierReferenceService.findByArticleId(99L)).thenThrow(new ResourceNotFoundException("Article non existant"));

        mockMvc.perform(get("/supplier-reference/list-supplier/{articleId}", 99L))
                .andExpect(status().isNotFound());

        verify(supplierReferenceService, times(1)).findByArticleId(99L);
    }

    /**
     * GET tous les articles correspondant à un fournisseur
     * Cas nominal : l'objet demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /supplier-reference/list-article/{supplierId} -> 200 quand l'objet existe")
    void getSupplierReferenceBySupplierId_whenFound_shouldReturn200AndObject() throws Exception {
        when(supplierReferenceService.findBySupplierId(1L)).thenReturn(List.of(articleDTO));

        // {supplierId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/supplier-reference/list-article/{supplierId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].artId").value(1))
                .andExpect(jsonPath("$[0].artReference").value("référence"))
                .andExpect(jsonPath("$[0].artName").value("name"));

        verify(supplierReferenceService, times(1)).findBySupplierId(1L);
    }

    /**
     * Cas d'erreur : le fournisseur n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code SupplierReferenceController#getSupplierReferenceById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /supplier-reference/list-article/{supplierId} -> 404 quand le fournisseur n'existe pas")
    void getSupplierReferenceBySupplierId_whenNotFound_shouldReturn404() throws Exception {
        when(supplierReferenceService.findBySupplierId(99L)).thenThrow(new ResourceNotFoundException("Fournisseur non existant"));

        mockMvc.perform(get("/supplier-reference/list-article/{supplierId}", 99L))
                .andExpect(status().isNotFound());

        verify(supplierReferenceService, times(1)).findBySupplierId(99L);
    }

    // ------------------------------------------------------------------
    // POST /supplier-reference
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'objet créé dans le corps.
     */
    @Test
    @DisplayName("POST /supplier-reference -> 201 avec un corps valide")
    void createSupplierReference_withValidData_shouldReturn201AndCreatedObject() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(supplierReferenceService.create(any(SupplierReferenceRequestDTO.class))).thenReturn(supplierReferenceDTO);

        mockMvc.perform(post("/supplier-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java supplierReferenceRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(supplierReferenceRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.splRefReference").value(supplierReferenceDTO.splRefReference()))
                .andExpect(jsonPath("$.supplierPrice").value(supplierReferenceDTO.supplierPrice()))
                .andExpect(jsonPath("$.splRefStock").value(supplierReferenceDTO.splRefStock()));

        verify(supplierReferenceService, times(1)).create(any(SupplierReferenceRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code articleId} est obligatoire
     * ({@code @NotNull} dans {@link SupplierReferenceRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier-reference -> 400 quand articleId est manquant")
    void createSupplierReference_withoutArticleId_shouldReturn400() throws Exception {
        String jsonWithoutArticleId = """
                {
                  "supplierId": 1,
                  "splRefReference": "référence",
                  "splRefSellPrice": BigDecimal.valusOf(1),
                  "splRefStock": 1
                }
                """;

        mockMvc.perform(post("/supplier-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutArticleId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierReferenceService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code supplierId} est obligatoire
     * ({@code @NotNull} dans {@link SupplierReferenceRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier-reference -> 400 quand supplier est manquant")
    void createSupplierReference_withoutSupplierId_shouldReturn400() throws Exception {
        String jsonWithoutSupplierId = """
                {
                  "articleId": 1,
                  "splRefReference": "référence",
                  "splRefSellPrice": BigDecimal.valusOf(1),
                  "splRefStock": 1
                }
                """;

        mockMvc.perform(post("/supplier-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutSupplierId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierReferenceService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code splRefReference} est obligatoire
     * ({@code @NotBlank} dans {@link SupplierReferenceRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier-reference -> 400 quand splRefReference est manquant")
    void createSupplierReference_withoutSplRefReference_shouldReturn400() throws Exception {
        String jsonWithoutSupplierId = """
                {
                  "articleId": 1,
                  "supplierId": 1,
                  "splRefSellPrice": BigDecimal.valusOf(1),
                  "splRefStock": 1
                }
                """;

        mockMvc.perform(post("/supplier-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutSupplierId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierReferenceService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code splRefSellPrice} est obligatoire
     * ({@code @NotNull} dans {@link SupplierReferenceRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier-reference -> 400 quand splRefSellPrice est manquant")
    void createSupplierReference_withoutSupplierPrice_shouldReturn400() throws Exception {
        String jsonWithoutSupplierId = """
                {
                  "articleId": 1,
                  "supplierId": 1,
                  "splRefReference": "référence",
                  "splRefStock": 1
                }
                """;

        mockMvc.perform(post("/supplier-reference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutSupplierId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierReferenceService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /supplier-reference/articleId/supplierId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'un objet existant -> 200 + objet modifié.
     */
    @Test
    @DisplayName("PUT /supplier-reference/{articleId}/{supplierId} -> 200 avec l'objet modifié")
    void updateSupplierReference_shouldReturn200AndModifiedObject() throws Exception {
        when(supplierReferenceService.update(eq(1L), eq(1L), any(UpdateSupplierReferenceDTO.class))).thenReturn(supplierReferenceDTO);

        mockMvc.perform(put("/supplier-reference/{articleId}/{supplierId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierReferenceRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.splRefReference").value(supplierReferenceDTO.splRefReference()))
                .andExpect(jsonPath("$.supplierPrice").value(supplierReferenceDTO.supplierPrice()))
                .andExpect(jsonPath("$.splRefStock").value(supplierReferenceDTO.splRefStock()));

        verify(supplierReferenceService, times(1)).update(eq(1L), eq(1L), any(UpdateSupplierReferenceDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /supplier-reference/articleId/supplierId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /supplier-reference/{articleId}/{supplierId} -> 204 sans corps")
    void deleteSupplierReference_shouldReturn204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        when(supplierReferenceService.findById(1L, 1L)).thenReturn(Optional.of(supplierReferenceDTO));
        org.mockito.Mockito.doNothing().when(supplierReferenceService).deleteById(1L, 1L);

        mockMvc.perform(delete("/supplier-reference/{articleId}/{supplierId}", 1L, 1L))
                .andExpect(status().isNoContent());

        verify(supplierReferenceService, times(1)).deleteById(1L, 1L);
    }
}
