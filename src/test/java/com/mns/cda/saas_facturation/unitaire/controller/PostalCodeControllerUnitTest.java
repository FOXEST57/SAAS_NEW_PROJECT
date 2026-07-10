package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.controller.PostalCodeController;
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
 * Tests unitaires du {@link PostalCodeController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link PostalCodeRequestDTO}</li>
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
@WebMvcTest(controllers = PostalCodeController.class)
class PostalCodeControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link IPostalCodeService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private IPostalCodeService postalCodeService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link PostalCodeRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private PostalCodeDTO postalCodeDTO;
    private PostalCodeRequestDTO postalCodeRequestDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     *
     * <p>{@link PostalCodeDTO} et {@link CityDTO} sont ici de simples mocks Mockito :
     * on ne connaît/teste pas leur contenu dans ce fichier, seulement le fait que
     * {@code PostalCodeDTO} les transporte correctement. Remplace ces lignes par de
     * vraies instances si tu préfères vérifier des champs précis (ex.
     * {@code postalCode.pCodeValue()}).</p>
     */
    @BeforeEach
    void setUp() {
        postalCodeDTO = new PostalCodeDTO(
                1L,                 // pCodeId
                "10101"              // pCodeName
        );

        postalCodeRequestDTO = new PostalCodeRequestDTO(
                "10101"             // pCodeName
        );
    }

    // ------------------------------------------------------------------
    // GET /postalcode/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /postalcode/list -> 200 et la liste des adresses")
    void getPostalCodes_shouldReturn200AndList() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(postalCodeService.findAll()).thenReturn(List.of(postalCodeDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/postalcode/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pCodeId").value(1))
                .andExpect(jsonPath("$[0].pCodeName").value("10101"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(postalCodeService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /postalcode/{pCodeId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : le code postal demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /postalcode/{pCodeId} -> 200 quand le code postal existe")
    void getPostalCodeById_whenFound_shouldReturn200AndPostalCode() throws Exception {
        when(postalCodeService.findById(1L)).thenReturn(Optional.of(postalCodeDTO));

        // {pCodeId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/postalcode/{pCodeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pCodeId").value(1))
                .andExpect(jsonPath("$.pCodeName").value("10101"));

        verify(postalCodeService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : l'adresse n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code PostalCodeController#getPostalCodeById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /postalcode/{pCodeId} -> 404 quand le code postal n'existe pas")
    void getPostalCodeById_whenNotFound_shouldReturn404() throws Exception {
        when(postalCodeService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/postalcode/{pCodeId}", 99L))
                .andExpect(status().isNotFound());

        verify(postalCodeService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /postalcode
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec le code postal créé dans le corps.
     */
    @Test
    @DisplayName("POST /postalcode -> 201 avec un corps valide")
    void createPostalCode_withValidData_shouldReturn201AndCreatedPostalCode() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(postalCodeService.create(any(PostalCodeRequestDTO.class))).thenReturn(postalCodeDTO);

        mockMvc.perform(post("/postalcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java postalCodeRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(postalCodeRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pCodeId").value(1))
                .andExpect(jsonPath("$.pCodeName").value("10101"));

        verify(postalCodeService, times(1)).create(any(PostalCodeRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeName} est obligatoire
     * ({@code @NotBlank} dans {@link PostalCodeRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /postalcode -> 400 quand pCodeId est manquant")
    void createPostalCode_sansPCodeId_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {}
                """;

        mockMvc.perform(post("/postalcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(postalCodeService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /postalcode/{pCodeId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'un code postal existant -> 200 + code postal modifié.
     */
    @Test
    @DisplayName("PUT /postalcode/{pCodeId} -> 200 avec le code postal modifié")
    void updatePostalCode_shouldReturn200AndModifiedPostalCode() throws Exception {
        when(postalCodeService.update(eq(1L), any(PostalCodeRequestDTO.class))).thenReturn(postalCodeDTO);

        mockMvc.perform(put("/postalcode/{pCodeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postalCodeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pCodeId").value(1))
                .andExpect(jsonPath("$.pCodeName").value("10101"));

        verify(postalCodeService, times(1)).update(eq(1L), any(PostalCodeRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /postalcode/{pCodeId}
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /postalcode/{pCodeId} -> 204 sans corps")
    void deletePostalCode_shouldReturn204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(postalCodeService).delete(1L);

        mockMvc.perform(delete("/postalcode/{pCodeId}", 1L))
                .andExpect(status().isNoContent());

        verify(postalCodeService, times(1)).delete(1L);
    }
}
