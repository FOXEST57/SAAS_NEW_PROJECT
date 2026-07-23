package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.controller.SupplierController;
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
 * Tests unitaires du {@link SupplierController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link SupplierRequestDTO}</li>
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
@WebMvcTest(controllers = SupplierController.class)
class SupplierControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link ISupplierService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private ISupplierService supplierService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link SupplierRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private SupplierDTO supplierDTO;
    private SupplierRequestDTO supplierRequestDTO;

    /**
     * Initialise un jeu de données commun avant chaque test, pour éviter de le
     * dupliquer dans chaque méthode de test.
     *
     * <p>{@link PostalCodeDTO} et {@link CityDTO} sont ici de simples mocks Mockito :
     * on ne connaît/teste pas leur contenu dans ce fichier, seulement le fait que
     * {@code SupplierDTO} les transporte correctement. Remplace ces lignes par de
     * vraies instances si tu préfères vérifier des champs précis (ex.
     * {@code postalCode.pCodeValue()}).</p>
     */
    @BeforeEach
    void setUp() {
        AddressDTO addressDTO = mock(AddressDTO.class);

        supplierDTO = new SupplierDTO(
                1L,                 // splId
                "Jean",               // splName
                "jean@jean.com",      // splEmail
                "+33586995411",       // splPhone
                addressDTO
        );

        supplierRequestDTO = new SupplierRequestDTO(
                "Jean",               // name
                "jean@jean.com",   // email
                "+33586995411",       // phoneNumber
                1L                  // addressId (obligatoire, @NotNull)
        );
    }

    // ------------------------------------------------------------------
    // GET /supplier/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /supplier/list -> 200 et la liste des fournisseurs")
    void getSuppliers_shouldReturn200AndList() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(supplierService.findAll()).thenReturn(List.of(supplierDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/supplier/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].splId").value(1))
                .andExpect(jsonPath("$[0].splName").value("Jean"))
                .andExpect(jsonPath("$[0].splEmail").value("jean@jean.com"))
                .andExpect(jsonPath("$[0].splPhone").value("+33586995411"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(supplierService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /supplier/splId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : le fournisseur demandé existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /supplier/{id} -> 200 quand le fournisseur existe")
    void getSupplierById_quandTrouvee_shouldReturn200AndSupplier() throws Exception {
        when(supplierService.findById(1L)).thenReturn(supplierDTO);

        // {id} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/supplier/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.splId").value(1))
                .andExpect(jsonPath("$.splName").value("Jean"))
                .andExpect(jsonPath("$.splEmail").value("jean@jean.com"))
                .andExpect(jsonPath("$.splPhone").value("+33586995411"));

        verify(supplierService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : le fournisseur n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code SupplierController#getSupplierById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /supplier/{id} -> 404 quand le fournisseur n'existe pas")
    void getSupplierById_quandIntrouvable_shouldReturn404() throws Exception {
        when(supplierService.findById(99L)).thenThrow(new ResourceNotFoundException("Fournisseur non existant"));

        mockMvc.perform(get("/supplier/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(supplierService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /supplier
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec le fournisseur créé dans le corps.
     */
    @Test
    @DisplayName("POST /supplier -> 201 avec un corps valide")
    void createSupplier_withValidData_shouldReturn201AndCreatedSupplier() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(supplierService.create(any(SupplierRequestDTO.class))).thenReturn(supplierDTO);

        mockMvc.perform(post("/supplier")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java supplierRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.splId").value(1))
                .andExpect(jsonPath("$.splName").value("Jean"))
                .andExpect(jsonPath("$.splEmail").value("jean@jean.com"))
                .andExpect(jsonPath("$.splPhone").value("+33586995411"));

        verify(supplierService, times(1)).create(any(SupplierRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code name} est obligatoire
     * ({@code @NotBlank} dans {@link SupplierRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier -> 400 quand name est manquant")
    void createSupplier_withoutName_shouldReturn400() throws Exception {
        String jsonWithoutName = """
                {
                  "email": "jean@jean.com",
                  "phoneNumber": "+33586995411",
                  "addressId": 1L
                }
                """;

        mockMvc.perform(post("/supplier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutName))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code email} est obligatoire
     * ({@code @NotBlank} dans {@link SupplierRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier -> 400 quand email est manquant")
    void createSupplier_withoutEmail_shouldReturn400() throws Exception {
        String jsonWithoutEmail = """
                {
                  "name": "Jean",
                  "phoneNumber": "+33586995411",
                  "addressId": 1L
                }
                """;

        mockMvc.perform(post("/supplier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutEmail))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code phoneNumber} est obligatoire
     * ({@code @NotBlank} dans {@link SupplierRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier -> 400 quand phoneNumber est manquant")
    void createSupplier_withoutPhoneNumber_shouldReturn400() throws Exception {
        String jsonWithoutPhoneNumber = """
                {
                  "name": "Jean",
                  "email": "jean@jean.com",
                  "addressId": 1L
                }
                """;

        mockMvc.perform(post("/supplier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutPhoneNumber))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierService, never()).create(any());
    }

    /**
     * Cas d'erreur de validation : {@code addressId} est obligatoire
     * ({@code @NotNull} dans {@link SupplierRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /supplier -> 400 quand addressId est manquant")
    void createSupplier_withoutAddressId_shouldReturn400() throws Exception {
        String jsonWithoutAddressId = """
                {
                  "name": "Jean",
                  "email": "jean@jean.com",
                  "phoneNumber": "+33586995411"
                }
                """;

        mockMvc.perform(post("/supplier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutAddressId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(supplierService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /supplier/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'un fournisseur existant -> 200 + fournisseur modifié.
     */
    @Test
    @DisplayName("PUT /supplier/{id} -> 200 avec le fournisseur modifié")
    void updateSupplier_shouldReturn200AndModifiedSupplier() throws Exception {
        when(supplierService.modify(eq(1L), any(SupplierRequestDTO.class))).thenReturn(supplierDTO);

        mockMvc.perform(put("/supplier/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.splId").value(1));

        verify(supplierService, times(1)).modify(eq(1L), any(SupplierRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /supplier/id
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /supplier/{id} -> 204 sans corps")
    void deleteSupplier_shouldReturn204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(supplierService).delete(1L);

        mockMvc.perform(delete("/supplier/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(supplierService, times(1)).delete(1L);
    }
}
