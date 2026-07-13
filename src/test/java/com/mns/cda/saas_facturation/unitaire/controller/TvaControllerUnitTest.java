package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.requestDTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.controller.TvaController;
import com.mns.cda.saas_facturation.model.Tva;
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
 * Tests unitaires du {@link TvaController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link TvaRequestDTO}</li>
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
@WebMvcTest(controllers = TvaController.class)
class TvaControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ITvaService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ITvaService tvaService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link TvaRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private Tva tva;
    private TvaRequestDTO tvaRequestDTO;
    private UpdateTvaTauxDTO tvaTauxDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     */
    @BeforeEach
    void setUp() {
        tva = new Tva(
                1L,                 // tvaId
                "name",                  // tvaName
                BigDecimal.valueOf(1)    // tvaTaux
        );

        tvaRequestDTO = new TvaRequestDTO(
                "name",          // tvaName
                BigDecimal.valueOf(1)    // tvaTaux
        );

        tvaTauxDTO = new UpdateTvaTauxDTO(
                BigDecimal.valueOf(99)    // tvaTaux
        );
    }

    // ------------------------------------------------------------------
    // GET /tva/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /tva/list -> 200 et la liste des tva")
    void getTvas_shouldReturn200AndList() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(tvaService.findAll()).thenReturn(List.of(tva));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/tva/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tvaId").value(1))
                .andExpect(jsonPath("$[0].tvaName").value("name"))
                .andExpect(jsonPath("$[0].tvaTaux").value(BigDecimal.valueOf(1)));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(tvaService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /tva/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : la tva demandée existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /tva/{id} -> 200 quand la tva existe")
    void getTvaById_whenFound_shouldReturn200AndTva() throws Exception {
        when(tvaService.findById(1L)).thenReturn(Optional.of(tva));

        // {id} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/tva/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tvaId").value(1))
                .andExpect(jsonPath("$.tvaName").value("name"))
                .andExpect(jsonPath("$.tvaTaux").value(BigDecimal.valueOf(1)));

        verify(tvaService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : la tva n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code TvaController#getTvaById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /tva/{id} -> 404 quand la tva n'existe pas")
    void getTvaById_whenNotFound_shouldReturn404() throws Exception {
        when(tvaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tva/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(tvaService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /tva
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec la tva créée dans le corps.
     */
    @Test
    @DisplayName("POST /tva -> 201 avec un corps valide")
    void createTva_withValidData_shouldReturn201AndCreatedTva() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(tvaService.create(any(TvaRequestDTO.class))).thenReturn(tva);

        mockMvc.perform(post("/tva")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java tvaRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(tvaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tvaId").value(1));

        verify(tvaService, times(1)).create(any(TvaRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code tvaName} est obligatoire
     * ({@code @NotBlank} dans {@link TvaRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /tva -> 400 quand tvaName est manquant")
    void createTva_withoutTvaName_shouldReturn400() throws Exception {
        String jsonWithoutTvaName = """
                {
                  "tvaTaux": BigDecimal.valueOf(1)
                }
                """;

        mockMvc.perform(post("/tva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutTvaName))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(tvaService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code tvaTaux} doit être positif
     * ({@code @DecimalMin(value = "0.0")} dans {@link TvaRequestDTO}). Si on met une valeur négative, Bean
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
    @DisplayName("POST /tva -> 400 quand tvaTaux est négatif")
    void createTva_withNegativeTvaTaux_shouldReturn400() throws Exception {
        String jsonWithWrongTvaTaux = """
                {
                  "tvaName": "name",
                  "tvaTaux": BigDecimal.valueOf(-1)
                }
                """;

        mockMvc.perform(post("/tva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithWrongTvaTaux))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(tvaService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /tva/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour totale d'une tva existante -> 200 + tva modifiée.
     */
    @Test
    @DisplayName("PUT /tva/{id} -> 200 avec la tva modifiée")
    void updateTva_shouldReturn200AndModifiedTva() throws Exception {
        when(tvaService.update(eq(1L), any(TvaRequestDTO.class))).thenReturn(tva);

        mockMvc.perform(put("/tva/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tvaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tvaId").value(1));

        verify(tvaService, times(1)).update(eq(1L), any(TvaRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // PATCH /tva/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour partielle d'une tva existante -> 200 + tva modifiée.
     */
    @Test
    @DisplayName("PATCH /tva/{id} -> 200 avec la tva modifiée")
    void patchTva_shouldReturn200AndModifiedTva() throws Exception {
        when(tvaService.findById(1L)).thenReturn(Optional.of(tva));
        when(tvaService.patchTaux(1L, BigDecimal.valueOf(99))).thenReturn(new Tva(1L, "name", BigDecimal.valueOf(99)));

        mockMvc.perform(patch("/tva/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tvaTauxDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tvaId").value(1))
                .andExpect(jsonPath("$.tvaTaux").value(99));

        verify(tvaService, times(1)).patchTaux(1L, BigDecimal.valueOf(99));
    }

    // ------------------------------------------------------------------
    // DELETE /tva/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /tva/{id} -> 204 sans corps")
    void deleteTva_shouldReturn204() throws Exception {
        when(tvaService.findById(1L)).thenReturn(Optional.of(tva));

        mockMvc.perform(delete("/tva/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(tvaService, times(1)).delete(1L);
    }
}
