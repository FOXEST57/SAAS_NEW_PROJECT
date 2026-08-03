package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.location.DTO.AddressDTO;
import com.mns.cda.saas_facturation.location.DTO.CityDTO;
import com.mns.cda.saas_facturation.location.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.location.Iservice.IAddressService;
import com.mns.cda.saas_facturation.location.controller.AddressController;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests unitaires du {@link AddressController}, réalisés avec {@link MockMvc}.
 *
 * <p><b>Pourquoi {@code MockMvc} plutôt qu'un appel direct au controller ?</b><br>
 * {@code MockMvc} simule une vraie requête HTTP (méthode, URL, headers, body JSON)
 * sans démarrer de serveur réel. Cela permet de vérifier non seulement la logique
 * du controller, mais aussi :</p>
 * <ul>
 *   <li>le routing ({@code @GetMapping}, {@code @PostMapping}, etc.)</li>
 *   <li>la désérialisation JSON du body en {@link AddressRequestDTO}</li>
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
@WebMvcTest(controllers = AddressController.class)
class AddressControllerUnitTest {

    /**
     * Client HTTP simulé injecté automatiquement par Spring grâce à {@code @WebMvcTest}.
     * Sert à envoyer des requêtes HTTP "fausses" vers les endpoints du controller.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Faux service métier injecté à la place du vrai {@link IAddressService} dans le
     * contexte Spring. {@code @WebMvcTest} ne connaît pas l'implémentation réelle du
     * service (pas de base de données ici), donc {@code @MockitoBean} fournit un mock
     * Mockito pour que le controller puisse quand même être instancié.
     */
    @MockitoBean
    private IAddressService addressService;

    /**
     * Outil Jackson permettant de convertir un objet Java en chaîne JSON (et
     * inversement). Utilisé ici pour transformer un {@link AddressRequestDTO} en
     * JSON avant de l'envoyer dans le corps d'une requête simulée.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /** Jeu de données réutilisé dans plusieurs tests, préparé avant chaque test. */
    private AddressDTO addressDTO;
    private AddressRequestDTO addressRequestDTO;

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
        PostalCodeDTO postalCodeDTO = mock(PostalCodeDTO.class);
        CityDTO cityDTO = mock(CityDTO.class);

        addressDTO = new AddressDTO(
                1L,                 // addId
                "10",               // addNumber
                "rue de la Paix",   // addStreet
                "Bâtiment A",       // addComplement
                postalCodeDTO,
                cityDTO
        );

        addressRequestDTO = new AddressRequestDTO(
                "10",               // addNumber
                "rue de la Paix",   // addStreet
                "Bâtiment A",       // addComplement
                1L,                 // pCodeId (obligatoire, @NotNull)
                1L                  // cityId (obligatoire, @NotNull)
        );
    }

    // ------------------------------------------------------------------
    // GET /address/list
    // ------------------------------------------------------------------

    /**
     * Vérifie que la route de listing retourne un statut 200 et le tableau JSON
     * attendu, en s'appuyant sur un service mocké qui renvoie une liste fixe.
     */
    @Test
    @DisplayName("GET /address/list -> 200 et la liste des adresses")
    void getAddresses_devraitRetourner200EtLaListe() throws Exception {
        // Arrange : on programme le mock du service pour qu'il renvoie une liste fixe
        when(addressService.findAll()).thenReturn(List.of(addressDTO));

        // Act + Assert : on simule un GET et on vérifie la réponse HTTP
        mockMvc.perform(get("/address/list"))
                .andExpect(status().isOk())
                // jsonPath("$") = racine du JSON. Ici la racine est un tableau.
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].addId").value(1))
                .andExpect(jsonPath("$[0].addStreet").value("rue de la Paix"));

        // Vérifie que le controller a bien délégué l'appel au service, une seule fois
        verify(addressService, times(1)).findAll();
    }

    // ------------------------------------------------------------------
    // GET /address/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : l'adresse demandée existe -> 200 + le corps JSON correspondant.
     */
    @Test
    @DisplayName("GET /address/{id} -> 200 quand l'adresse existe")
    void getAddressById_quandTrouvee_devraitRetourner200EtLAdresse() throws Exception {
        when(addressService.findById(1L)).thenReturn(Optional.of(addressDTO));

        // {addId} dans l'URL est remplacé par l'argument passé après le pattern
        mockMvc.perform(get("/address/{addId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addId").value(1))
                .andExpect(jsonPath("$.addNumber").value("10"));

        verify(addressService, times(1)).findById(1L);
    }

    /**
     * Cas d'erreur : l'adresse n'existe pas -> le controller renvoie lui-même un
     * 404 sans corps (voir {@code AddressController#getAddressById}, qui teste
     * l'Optional et construit une {@code ResponseEntity} 404 "à la main").
     */
    @Test
    @DisplayName("GET /address/{id} -> 404 quand l'adresse n'existe pas")
    void getAddressById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(addressService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/address/{addId}", 99L))
                .andExpect(status().isNotFound());

        verify(addressService, times(1)).findById(99L);
    }

    // ------------------------------------------------------------------
    // POST /address
    // ------------------------------------------------------------------

    /**
     * Cas nominal : un DTO valide est envoyé -> le service est appelé, et le
     * controller renvoie 201 Created avec l'adresse créée dans le corps.
     */
    @Test
    @DisplayName("POST /address -> 201 avec un corps valide")
    void createAddress_avecDonneesValides_devraitRetourner201EtLAdresseCreee() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(addressService.create(any(AddressRequestDTO.class))).thenReturn(addressDTO);

        mockMvc.perform(post("/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java addressRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addId").value(1));

        verify(addressService, times(1)).create(any(AddressRequestDTO.class));
    }

    /**
     * Cas d'erreur de validation : {@code pCodeId} est obligatoire
     * ({@code @NotNull} dans {@link AddressRequestDTO}). Si on l'omet, Bean
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
    @DisplayName("POST /address -> 400 quand pCodeId est manquant")
    void createAddress_sansPCodeId_devraitRetourner400() throws Exception {
        String jsonSansPCodeId = """
                {
                  "addNumber": "10",
                  "addStreet": "rue de la Paix",
                  "addComplement": "Bâtiment A",
                  "cityId": 1
                }
                """;

        mockMvc.perform(post("/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSansPCodeId))
                .andExpect(status().isBadRequest());

        // Le service ne doit JAMAIS être appelé : la requête est rejetée avant,
        // par la validation Bean Validation (@Valid), pas par la logique métier.
        verify(addressService, never()).create(any());
    }

    // ------------------------------------------------------------------
    // PUT /address/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : mise à jour d'une adresse existante -> 200 + adresse modifiée.
     */
    @Test
    @DisplayName("PUT /address/{id} -> 200 avec l'adresse modifiée")
    void updateAddress_devraitRetourner200EtLAdresseModifiee() throws Exception {
        when(addressService.update(eq(1L), any(AddressRequestDTO.class))).thenReturn(addressDTO);

        mockMvc.perform(put("/address/{addId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addId").value(1));

        verify(addressService, times(1)).update(eq(1L), any(AddressRequestDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /address/addId
    // ------------------------------------------------------------------

    /**
     * Cas nominal : suppression réussie -> 204 No Content, sans corps de réponse.
     */
    @Test
    @DisplayName("DELETE /address/{id} -> 204 sans corps")
    void deleteAddress_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(addressService).delete(1L);

        mockMvc.perform(delete("/address/{addId}", 1L))
                .andExpect(status().isNoContent());

        verify(addressService, times(1)).delete(1L);
    }
}
