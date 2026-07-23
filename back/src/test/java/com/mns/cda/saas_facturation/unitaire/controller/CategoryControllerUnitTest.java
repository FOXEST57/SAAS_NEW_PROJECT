package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.controller.CategoryController;
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
 * Tests unitaires du {@link CategoryController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link CategoryRequestDTO}</li>
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
@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ICategoryService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ICategoryService categoryService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link CategoryRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private CategoryDTO categoryDTO;
    private CategoryRequestDTO categoryRequestDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     */
    @BeforeEach
    void setUp() {

        categoryDTO = new CategoryDTO(
                1L,               // catId,
                "Informatique",       // catName,
                "informatique",      // catSlug,
                "Technologie",      // catParentName,
                List.of()          // children
        );

        categoryRequestDTO = new CategoryRequestDTO(
                "Informatique",       // catName,
                "informatique",               // catSlug,
                1L                    // children
        );
    }

    // ------------------------------------------------------------------
    // GET /category/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /category/list -> 200 et la liste des categories")
    void getCategory_devraitRetourner200EtLaListe() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(categoryService.findAll()).thenReturn(List.of(categoryDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/category/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].catId").value(1))
                .andExpect(jsonPath("$[0].catName").value("Informatique"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(categoryService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /category/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : la categorie demandée existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /category/{id} -> 200 quand la categorie existe")
    void getCategoryById_quandTrouvee_devraitRetourner200EtLAdresse() throws Exception {
        when(categoryService.findById(1L)).thenReturn(Optional.of(categoryDTO));

        // {addId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/category/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catId").value(1))
                .andExpect(jsonPath("$.catName").value("Informatique"));

        verify(categoryService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : la categorie n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code CategoryController#getCategoryById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /category/{id} -> 404 quand la categorie n'existe pas")
    void getCategoryById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(categoryService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/category/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /category
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec la categorie créée dans le corps.
     */
    @Test
    @DisplayName("POST /category -> 201 avec un corps valide")
    void createCategory_avecDonneesValides_devraitRetourner201EtCategorieCreee() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java categoryRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.catId").value(1));

        verify(categoryService, times(1)).create(any(CategoryRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeId} est obligatoire
     * ({@code @NotNull} dans {@link CategoryRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /category -> 400 quand catName est manquant")
    void createCategory_sansCatName_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {
                  "catSlug": "informatique",
                }
                """;

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(categoryService, never()).create(any());
    }

    @Test
    @DisplayName("POST /category -> 400 quand catSlug est manquant")
    void createCategory_sansCatSlug_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {
                  "catName": "Informatique",
                }
                """;

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(categoryService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /category/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'une categorie existante -> 200 + categorie modifiée.
     */
    @Test
    @DisplayName("PUT /category/{id} -> 200 avec la categorie modifiée")
    void updateCategory_devraitRetourner200EtLAdresseModifiee() throws Exception {
        when(categoryService.update(eq(1L), any(CategoryRequestDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(put("/category/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catId").value(1));

        verify(categoryService, times(1)).update(eq(1L), any(CategoryRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /category/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /category/{id} -> 204 sans corps")
    void deleteCategory_devraitRetourner204() throws Exception {

        // L'article existe
        when(categoryService.findById(1L)).thenReturn(Optional.of(categoryDTO));

        // delete est une méthode void
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/category/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService).findById(1L);
        verify(categoryService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /category/{id} -> 404 quand la categorie n'existe pas")
    void deleteArticle_Inexistant_devraitRetourner404() throws Exception {

        when(categoryService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/category/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(categoryService).findById(1L);
        verify(categoryService, never()).delete(anyLong());
    }
}
