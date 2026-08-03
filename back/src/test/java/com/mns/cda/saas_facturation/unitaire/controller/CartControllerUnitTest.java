package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.ICartService;
import com.mns.cda.saas_facturation.cart.controller.CartController;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitaires du {@link CartController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link CartRequestDTO}</li>
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
@WebMvcTest(controllers = CartController.class)
class CartControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ICartService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ICartService cartService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link CartRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private CartDTO cartDTO;
    private CartRequestDTO cartRequestDTO;

    LocalDateTime dateCreation = LocalDateTime.of(2026, 1, 15, 10, 30);
    LocalDateTime dateModification = LocalDateTime.of(2026, 1, 20, 14, 0);

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     */
    @BeforeEach
    void setUp() {
        CustomerDTO customerDTO = mock(CustomerDTO.class);

        cartDTO = new CartDTO(
                1L,           // crtId,
                "Référence",       // crtRef,
                dateCreation,      // crtCreateDate,
                dateModification,  // crtLastModifieDate,
                "VALIDE",          // crtStatus,
                customerDTO,       // customer,
                List.of()          // orderLines
        );

        cartRequestDTO = new CartRequestDTO(
                "Référence",       // crtRef,
                "VALIDE",                // crtStatus,
                1L,                      // customerId,
                List.of()                // orderLines
        );
    }

    // ------------------------------------------------------------------
    // GET /cart/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /cart/list -> 200 et la liste des paniers")
    void getCart_devraitRetourner200EtLaListe() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(cartService.findAll()).thenReturn(List.of(cartDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/cart/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].crtId").value(1))
                .andExpect(jsonPath("$[0].crtRef").value("Référence"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(cartService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /cart/cartId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : le panier demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /cart/{id} -> 200 quand le panier existe")
    void getCartById_quandTrouvee_devraitRetourner200EtLAdresse() throws Exception {
        when(cartService.findById(1L)).thenReturn(cartDTO);

        // {cartId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/cart/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crtId").value(1))
                .andExpect(jsonPath("$.crtRef").value("Référence"));

        verify(cartService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : le panier n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code CartController#getCartById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /cart/{id} -> 404 quand le panier n'existe pas")
    void getCartById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(cartService.findById(99L)).thenThrow(new ResourceNotFoundException("Le panier avec l'id n'existe pas"));

        mockMvc.perform(get("/cart/{cartId}", 99L))
                .andExpect(status().isNotFound());

        verify(cartService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /cart
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'adresse créée dans le corps.
     */
    @Test
    @DisplayName("POST /cart -> 201 avec un corps valide")
    void createCart_avecDonneesValides_devraitRetourner201EtPanierCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(cartService.create(any(CartRequestDTO.class))).thenReturn(cartDTO);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java cartRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(cartRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.crtId").value(1));

        verify(cartService, times(1)).create(any(CartRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeId} est obligatoire
     * ({@code @NotNull} dans {@link CartRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /cart -> 400 quand ctmId est manquant")
    void createCart_sansCtmId_devraitRetourner400() throws Exception {
        String jsonSansCtmId = """
                {
                  "crtRef": "Référence",
                  "crtStatus": "VALIDE",
                }
                """;

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansCtmId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(cartService, never()).create(any());
    }

    @Test
    @DisplayName("POST /cart -> 400 quand crtRef est manquant")
    void createCart_sansCrtReF_devraitRetourner400() throws Exception {
        String jsonSansCtmId = """
                {
                  "ctmId": 1L
                  "crtStatus": "VALIDE",
                }
                """;

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansCtmId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(cartService, never()).create(any());
    }

    @Test
    @DisplayName("POST /cart -> 400 quand crtStatut est manquant")
    void createCart_sansCrtStatut_devraitRetourner400() throws Exception {
        String jsonSansCtmId = """
                {
                  "ctmId": 1L
                  "crtRef": "Référence",
                }
                """;

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansCtmId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(cartService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /cart/cartId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'une adresse existante -> 200 + adresse modifiée.
     */
    @Test
    @DisplayName("PUT /cart/{id} -> 200 avec le panier modifié")
    void updateCart_devraitRetourner200EtPanierModifie() throws Exception {
        when(cartService.modify(eq(1L), any(CartRequestDTO.class))).thenReturn(cartDTO);

        mockMvc.perform(put("/cart/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crtId").value(1));

        verify(cartService, times(1)).modify(eq(1L), any(CartRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /cart/cartId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /cart/{id} -> 204 sans corps")
    void deleteCart_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(cartService).delete(1L);

        mockMvc.perform(delete("/cart/{cartId}", 1L))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).delete(1L);
    }
}
