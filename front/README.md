# Klimafact — Frontend Angular

Interface de gestion de **devis et factures** pour la vente et l'installation de
climatisation et de chauffage. Elle se branche sur le backend Spring Boot
`saas_facturation` fourni dans le dossier `back/`.

- **Angular 20 (LTS)** — composants standalone, signals, `OnPush`, lazy loading
- **Tailwind CSS 3** — design system maison, thème clair/sombre
- **API Adresse (BAN)** — autocomplétion officielle des adresses, sans clé d'API
- Aucune dépendance UI tierce : le bundle initial pèse ~99 ko gzip

---

## 1. Démarrage

```bash
# à la racine du projet, à côté du dossier back/
cd front
npm install
npm start          # http://localhost:4200
```

Le backend doit tourner en parallèle sur `http://localhost:8080` :

```bash
cd ../back
./mvnw spring-boot:run
```

> **Windows** — pensez à adapter `spring.datasource.url` dans
> `back/src/main/resources/application.properties` si le chemin de la base H2
> ne correspond pas à votre poste.

### Proxy et CORS

En développement, `proxy.conf.json` réécrit `/api/**` vers
`http://localhost:8080/**`. Aucun problème de CORS ne peut donc survenir, même
si la configuration `@CrossOrigin` du backend change.

En production, renseignez l'URL publique de l'API dans
`src/environments/environment.prod.ts` (`apiBaseUrl`).

### Scripts

| Commande        | Effet                                                |
| --------------- | ---------------------------------------------------- |
| `npm start`     | Serveur de développement avec proxy vers le backend  |
| `npm run build` | Build de production dans `dist/front`                |
| `npm run watch` | Build incrémental                                    |
| `npm test`      | Tests unitaires (Karma)                              |

---

## 2. Fonctionnalités

### Documents commerciaux

Le backend ne possède pas d'entités `Devis` / `Facture` : un `Cart` porte un
champ texte `crtStatus`. Le front s'appuie dessus pour matérialiser le cycle de
vie commercial :

```
PANIER ──▶ DEVIS ──▶ FACTURE ──▶ PAYEE
   └────────┴──────────┴──────▶ ANNULE
```

- **PANIER / DEVIS** : lignes et client librement modifiables
- **FACTURE / PAYEE** : contenu verrouillé (règle métier appliquée côté front)
- Les statuts historiques du `data.sql` (`OPEN`, `VALIDATED`, `ABANDONED`) sont
  reconnus et normalisés automatiquement — voir
  `core/models/document-status.ts`

Chaque document dispose d'un **aperçu A4 imprimable** (`/documents/:id/impression`)
avec ventilation de la TVA par taux, et mentions légales de devis (validité
30 jours, bon pour accord) ou de facture (échéance, pénalités de retard,
indemnité forfaitaire de 40 €). L'export PDF passe par l'impression native du
navigateur — aucune bibliothèque supplémentaire.

### CRUD complet

Toutes les entités du MCD sont couvertes, création / lecture / modification /
suppression comprises :

| Écran                        | Route                       | Contrôleur backend                        |
| ---------------------------- | --------------------------- | ----------------------------------------- |
| Tableau de bord              | `/tableau-de-bord`          | —                                         |
| Devis & factures             | `/documents`                | `/cart`, `/order-line`                    |
| Articles                     | `/articles`                 | `/article`                                |
| Catégories (arborescence)    | `/categories`               | `/category`                               |
| Taux de TVA                  | `/tva`                      | `/tva` (dont `PATCH`)                     |
| Références fourn. / fabr.    | `/references`               | `/supplier-reference`, `/maker-reference` |
| Clients                      | `/clients`                  | `/customer`                               |
| Types de compte              | `/types-de-compte`          | `/AccountType`                            |
| Fournisseurs                 | `/fournisseurs`             | `/supplier`                               |
| Fabricants                   | `/fabricants`               | `/maker`                                  |
| Adresses                     | `/adresses`                 | `/address`                                |
| Villes                       | `/villes`                   | `/city`                                   |
| Codes postaux                | `/codes-postaux`            | `/postalcode`                             |
| Pays                         | `/pays`                     | `/country`                                |
| Associations CP / ville      | `/associations-cp-ville`    | `/postalcodecity`                         |

### Autocomplétion d'adresse

Le composant `AddressAutocompleteComponent` interroge l'**API Adresse de l'État
français** (Base Adresse Nationale, `api-adresse.data.gouv.fr`) : service public,
gratuit, sans clé ni quota bloquant.

Au fil de la frappe (débounce 250 ms, minimum 3 caractères), les adresses
officielles sont proposées ; la sélection remplit numéro, voie, code postal,
ville et pays. Chaque champ reste modifiable à la main pour les cas particuliers
(lieux-dits, adresses étrangères).

**Le point délicat, c'est l'enregistrement.** Le MCD impose une chaîne complète :
une `Address` référence un `PostalCode` **et** une `City`, et
`AddressService.create()` refuse la création si l'association `PostalCodeCity`
correspondante n'existe pas. Une `City` référence elle-même un `Country`.

`AddressResolverService` applique donc une stratégie *find-or-create* sur toute
la chaîne, en une seule action utilisateur :

```
Country ──▶ PostalCode ──▶ City ──▶ PostalCodeCity ──▶ Address
```

Les comparaisons sont insensibles à la casse et aux accents, car le backend
applique un `LowercaseConverter` sur `cntName`, `cityName` et `pCodeName`. Le
détail des opérations (créé / existant) est affiché à l'utilisateur après
enregistrement dans l'écran Adresses.

Ce mécanisme est réutilisé tel quel par les formulaires **client**,
**fournisseur** et **fabricant** : l'adresse est persistée d'abord, puis son
`addId` est transmis au tiers.

---

## 3. Architecture

```
src/app/
├── core/
│   ├── api/              un service HTTP par contrôleur backend
│   ├── models/           interfaces TS miroirs des DTO Java + cycle de vie
│   ├── services/         BAN, résolution d'adresse, toasts, thème, confirmation
│   └── interceptors/     gestion des erreurs, barre de progression
├── shared/
│   ├── ui/               composants réutilisables (modale, table, autocomplete…)
│   ├── pipes/            euro, taux, date FR, adresse, capitalisation
│   └── validators.ts     téléphone (miroir de @ValidPhoneNumber), code postal
├── layout/               shell applicatif (sidebar, topbar, thème)
└── features/             une vue par domaine métier, toutes en lazy loading
```

### Correspondance avec les DTO

`core/models/api.models.ts` reproduit **exactement** les `record` Java, y compris
les incohérences de nommage du backend, qu'il ne faut donc pas « corriger » côté
front :

- `SupplierRequestDTO` utilise `name` / `email` / `phoneNumber`, alors que
  `SupplierDTO` renvoie `splName` / `splEmail` / `splPhone`
- La route des types de compte est en PascalCase : `/AccountType`
- `PUT /article/{id}` attend un `ArticleUpdateDTO` **sans** le champ `suppliers`
  (les références se gèrent depuis l'écran dédié)
- `PUT /cart/{id}` ne met à jour que l'en-tête ; les lignes passent
  obligatoirement par `/order-line` — le `CartEditorComponent` calcule le delta
  (créations / modifications / suppressions) et émet les requêtes correspondantes
- Les taux de TVA sont stockés en fraction décimale (`0.2` = 20 %) ; la saisie se
  fait en pourcentage et la conversion est explicite

### Règles métier appliquées côté front

- Stock insuffisant signalé sur les lignes de document (le backend lève une
  `InsufficientStockException` que l'intercepteur affiche telle quelle)
- Suppression bloquée quand des dépendances existent (TVA rattachée à des
  articles, client rattaché à des documents)
- Clé composite non modifiable sur les références fournisseur / fabricant :
  il faut supprimer puis recréer
- Téléphones normalisés au format E.164 (`06 12 34 56 78` → `+33612345678`)
  avant envoi, pour satisfaire la validation libphonenumber du backend

---

## 4. Personnalisation

**Identité de votre société** (en-tête et pied des devis et factures) :
`src/environments/environment.ts` et `environment.prod.ts`, objet `company`
(raison sociale, adresse, SIRET, numéro de TVA, IBAN).

**Couleurs** : `tailwind.config.js`. La palette `brand` (bleu froid) évoque la
climatisation, `heat` (ambre) le chauffage, `ink` sert de gris neutre.

**Icônes** : `shared/ui/icon.component.ts` contient un jeu de tracés SVG intégré.
Pour en ajouter une, insérez son tracé dans la constante `PATHS`.

---

## 5. Points d'attention

- **Le backend cible Java 25.** Vérifiez votre JDK avant de lancer `mvnw`.
- L'API Adresse est un service public : elle nécessite un accès Internet. En cas
  d'indisponibilité, la saisie manuelle des champs reste pleinement fonctionnelle.
- La police Inter est chargée depuis Google Fonts (`index.html`). En environnement
  fermé, supprimez ce `<link>` : la pile de polices système prend le relais.
- Aucune authentification n'est implémentée, le backend n'en expose pas encore.
  L'ajout d'un `HttpInterceptor` porteur de jeton se fera dans
  `core/interceptors/` le moment venu.
