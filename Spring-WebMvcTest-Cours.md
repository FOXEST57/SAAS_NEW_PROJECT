# Comprendre les tests de contrôleurs Spring Boot avec `@WebMvcTest`

> **Ce document accompagne le fichier de test et explique chaque
> annotation, objet et ligne importante afin qu'un débutant puisse
> comprendre puis reproduire ce type de test.**

## Le code étudié

``` java
(Collez ici votre fichier AddressControllerTest complet afin de le lire en parallèle du cours.)
```

# 1. Architecture Spring

Une requête HTTP suit généralement ce chemin :

Client HTTP → DispatcherServlet → Controller → Service → Repository →
Base de données

Le **Controller** reçoit la requête, appelle le **Service**, puis
renvoie une réponse HTTP.

# 2. Pourquoi tester un Controller ?

Un test de Controller vérifie : - les routes (`@GetMapping`,
`@PostMapping`...) - la désérialisation JSON - la validation
(`@Valid`) - les codes HTTP (200, 201, 400, 404...) - le JSON renvoyé

Il **ne teste pas** la logique métier ni la base de données.

# 3. `@WebMvcTest`

``` java
@WebMvcTest(
    controllers = AddressController.class,
    excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
    }
)
```

Cette annotation démarre uniquement la couche Web.

Elle crée automatiquement : - DispatcherServlet - MockMvc - Jackson
(`ObjectMapper`) - Validation Bean Validation - Le controller

Elle ne crée pas : - les Repository - Hibernate - la DataSource - la
base de données

Les exclusions empêchent Spring d'essayer de démarrer JPA alors qu'on ne
teste que la couche Web.

# 4. `@Autowired MockMvc`

``` java
@Autowired
private MockMvc mockMvc;
```

Spring injecte un faux client HTTP.

Exemple :

``` java
mockMvc.perform(get("/address/list"));
```

équivaut à un navigateur qui appelle :

``` http
GET /address/list
```

sans démarrer Tomcat.

# 5. `@MockitoBean`

``` java
@MockitoBean
private IAddressService addressService;
```

Le controller dépend d'un service. Comme le vrai service n'est pas
chargé avec `@WebMvcTest`, Mockito crée un faux objet (mock).

On programme ensuite son comportement :

``` java
when(addressService.findAll()).thenReturn(List.of(addressDTO));
```

# 6. `ObjectMapper`

``` java
@Autowired
private ObjectMapper objectMapper;
```

Convertit un objet Java en JSON.

``` java
objectMapper.writeValueAsString(addressRequestDTO)
```

sert à envoyer un vrai corps JSON dans la requête.

# 7. `@BeforeEach`

Exécuté avant chaque test.

Il prépare des données communes pour éviter les duplications.

Les `mock(PostalCodeDTO.class)` et `mock(CityDTO.class)` créent de faux
objets car leur contenu n'est pas testé ici.

# 8. Structure d'un test

Tous les tests suivent le modèle :

-   Arrange : préparation (`when(...)`)
-   Act : appel HTTP (`mockMvc.perform(...)`)
-   Assert : vérifications (`status()`, `jsonPath()`, `verify()`)

# 9. Exemple GET

``` java
when(addressService.findAll()).thenReturn(List.of(addressDTO));

mockMvc.perform(get("/address/list"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", hasSize(1)));

verify(addressService).findAll();
```

Explications :

-   `when(...)` prépare le mock.
-   `perform(get(...))` simule la requête.
-   `status().isOk()` vérifie le code HTTP 200.
-   `jsonPath()` lit le JSON renvoyé.
-   `verify()` vérifie que le controller a appelé le service.

# 10. GET par identifiant

``` java
get("/address/{addId}",1L)
```

Spring remplace `{addId}` par `1`.

Le test avec `Optional.empty()` vérifie le cas où la ressource n'existe
pas et que le controller renvoie 404.

# 11. POST

``` java
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(addressRequestDTO))
```

-   `contentType` indique que le corps est du JSON.
-   `content()` contient le JSON envoyé.

Le service est préparé avec :

``` java
when(addressService.create(any(AddressRequestDTO.class)))
```

`any()` signifie : peu importe l'objet reçu.

# 12. Validation Bean Validation

Le test qui construit manuellement une chaîne JSON sans `pCodeId` montre
que la validation intervient avant l'appel du service.

``` java
verify(addressService, never()).create(any());
```

prouve que le service n'a jamais été appelé.

# 13. PUT

Même fonctionnement que POST.

La seule différence est la méthode HTTP.

# 14. DELETE

Comme `delete()` retourne `void`, on utilise :

``` java
doNothing().when(addressService).delete(1L);
```

puis

``` java
verify(addressService).delete(1L);
```

# 15. Mockito

-   `when()` : programme un résultat
-   `verify()` : vérifie un appel
-   `times(1)` : une seule fois
-   `never()` : jamais
-   `any()` : n'importe quel objet
-   `eq()` : valeur exacte

# 16. Cycle complet

Client HTTP

↓

MockMvc

↓

DispatcherServlet

↓

AddressController

↓

Service Mockito

↓

Réponse JSON

# Résumé

Chaque ligne du test participe à vérifier le **contrat HTTP** du
controller sans utiliser de base de données. C'est la responsabilité
d'un test `@WebMvcTest`.
