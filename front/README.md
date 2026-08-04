# Klimafact — Frontend Angular

Interface de gestion de **devis et factures** pour la vente et l'installation de
climatisation et de chauffage. Elle se branche sur le backend Spring Boot
`saas_facturation` fourni dans le dossier `back/`.

- **Angular 20 (LTS)** — composants standalone, signals, `OnPush`, lazy loading
- **Tailwind CSS 3** — design system maison, thème clair/sombre
- **API Adresse (BAN)** — autocomplétion officielle des adresses, sans clé d'API
- **Analyse de marge en temps réel** à partir des prix d'achat fournisseurs
- Aucune dépendance UI ni graphique tierce : le bundle initial pèse ~105 ko gzip

### Ce qui distingue cette application

| | |
| --- | --- |
| **Marge en temps réel** | Le coût de revient est lu dans `SupplierReference` : marge par ligne, par document et par client, affichée **pendant** la saisie du devis. Un logiciel de facturation classique vous dit ce que vous facturez ; celui-ci vous dit ce que vous gagnez. |
| **Pipeline en Kanban** | Colonnes Panier → Devis → Commande → Facture → Payée, glisser-déposer natif et pilotage clavier complet. |
| **File « À traiter »** | Factures échues, commandes à facturer, devis sans réponse, marges sous le seuil — chaque ligne porte l'action qui la fait disparaître. |
| **Saisonnalité CVC** | Le chiffre d'affaires ventilé climatisation / chauffage sur 12 mois : le métier est saisonnier, le tableau de bord le montre. |
| **Chiffrer sans stock** | On devise avant d'acheter. Un article en rupture, ou même absent du catalogue, s'ajoute librement à un devis ; le manque devient un besoin d'achat, pas un blocage. |
| **Approvisionnement automatique** | Au passage en commande, ce qui manque est consolidé par fournisseur, avec coût d'achat et documents à l'origine du besoin. |

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

Depuis le 4 août 2026, le backend possède de vraies entités `Quote`,
`Command`, `Invoice` (+ `InvoiceLine`, prix figé) en plus du `Cart` d'origine.
Un document commercial n'est donc plus une seule ligne de `Cart` dont on
relit le statut : c'est l'une de quatre entités, chacune avec son propre id
et son propre statut. Le front les compose en un seul portefeuille via
`CommerceStore` (`core/services/commerce-store.service.ts`) et les projette
sur un vocabulaire à 6 étapes, purement présentationnel :

```
PANIER ──▶ DEVIS ──▶ COMMANDE ──▶ FACTURE ──▶ PAYEE
   └─────────┴──────────┴───────────┴──────▶ ANNULE
```

- **PANIER** (`Cart` + `OrderLine`) : seule étape encore éditable en ligne,
  dans `cart-editor.component.ts`. Passer à l'étape suivante crée un `Quote`
  (`POST /quote`) — ce n'est plus un changement de champ.
- **DEVIS** (`Quote` + `QuoteLine`, prix figé) : la quantité reste modifiable
  tant que le devis est dans un statut ouvert (`CREATED`, `PENDING`,
  `REJECTED`), via `PATCH /quote/quantity/{id}`.
- **COMMANDE** (`Command`) : n'a pas de lignes propres, seulement un statut de
  suivi logistique et une référence vers le `Quote` d'origine. Plus rien n'y
  est modifiable côté front.
- **FACTURE / PAYEE** (`Invoice` + `InvoiceLine`, prix figé) : verrouillée par
  construction — `InvoiceLine` fige prix, TVA et totaux au moment de la
  création (`POST /invoice`), qui recopie les lignes du `Quote` d'origine via
  la `Command`.
- Chaque route de transition (`/quote`, `/command`, `/invoice`) est appelée
  depuis `CommerceStore` (`transitionToQuote`, `transitionToCommand`,
  `transitionToInvoice`, `markInvoicePaid`) — c'est le seul point d'entrée,
  utilisé aussi bien par le pipeline que par l'écran de détail.
- Les routes de détail portent désormais la nature du document :
  `/documents/:kind/:id` (`kind` = `cart | quote | command | invoice`), et
  `/documents/:kind/:id/impression` pour l'aperçu imprimable. Seul `cart`
  pointe vers un éditeur ; les trois autres pointent vers
  `document-print.component.ts`, qui sert aussi d'écran de détail (actions de
  transition comprises) faute d'éditeur dédié à chacun.
- Les statuts historiques du `data.sql` (`OPEN`, `VALIDATED`, `ABANDONED`) sur
  `Cart.crtStatus` restent reconnus par `normalizeStatus()` mais ne pilotent
  plus le cycle de vie : celui-ci se déduit désormais de la présence ou non
  d'un `Quote` / `Command` / `Invoice`, pas d'un texte libre.

**Une limite vient encore d'un DTO backend, pas d'un choix du front** — voir
les notes dans `core/models/api.models.ts` et
`core/services/commerce-store.service.ts` : `InvoiceDTO` n'expose aucun lien
vers sa `Command` d'origine (une commande facturée peut donc continuer
d'apparaître dans la colonne « Commande » du pipeline). `CommandDTO` portait
la même limite côté client, corrigée le 4 août 2026 par l'ajout de
`quoteId`.

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
| Pipeline commercial          | `/pipeline`                 | `/cart`                                   |
| À traiter                    | `/a-traiter`                | `/cart`                                   |
| Approvisionnement            | `/approvisionnement`        | — (calculé)                               |
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

## 3. Analyse de marge

Le MCD stocke le prix consenti par chaque fournisseur dans
`SupplierReference.supplierPrice`. Cette donnée, généralement inexploitée, permet
de calculer un **coût de revient réel**.

**Règle retenue : le meilleur prix d'achat connu** — le fournisseur le moins cher
référencé pour l'article, celui sur lequel un acheteur se positionnerait.

Un article sans référence fournisseur a un coût **inconnu**. Il est alors exclu du
calcul plutôt que compté à zéro, ce qui gonflerait artificiellement la marge. Le
*taux de couverture* affiché sous la jauge indique quelle part du chiffre
d'affaires repose sur un coût connu — une marge de 40 % couverte à 30 % n'a pas la
même valeur qu'une marge de 40 % couverte à 100 %.

La marge apparaît à quatre endroits :

- **dans le catalogue de l'éditeur**, avant même d'ajouter la ligne ;
- **par ligne**, via le bouton « Voir les coûts » ;
- **par document**, dans le panneau Rentabilité, avec alerte sous le seuil ;
- **par carte du pipeline**, pour arbitrer d'un coup d'œil.

Le seuil est porté par `CommerceStore.marginTarget` (25 % par défaut).

---

## 4. Chiffrer sans stock, et approvisionner

### Le principe

En CVC on chiffre avant d'acheter : personne ne stocke chaque modèle. Un devis
peut donc porter sur du matériel absent du dépôt, et même sur un article qui
n'existe pas encore au catalogue. Le manque n'est pas une erreur — c'est une
commande fournisseur à passer.

### Article hors catalogue

`OrderLine` porte une clé composite `(articleId, cartId)` et une clé étrangère
vers `Article` : **aucune ligne libre n'est possible**. Créer un article « à la
volée » crée donc un vrai article en base — mais **marqué** de deux façons :

- une catégorie dédiée **« Hors catalogue »**, créée automatiquement au besoin ;
- une référence préfixée **`HC-2026-0001`**, numérotée à l'année.

Votre catalogue reste ainsi navigable et filtrable, et un article qui se vend
régulièrement se promeut en article normal en changeant simplement sa catégorie.

Renseigner un fournisseur et un prix d'achat dans le formulaire express crée la
`SupplierReference` dans la foulée : l'article entre immédiatement dans le calcul
de marge et dans les besoins d'approvisionnement. La marge prévisionnelle
s'affiche pendant la saisie.

### L'écran Approvisionnement

Au passage d'un devis en commande, une confirmation liste ce qu'il faut acheter.
Le besoin est ensuite consolidé dans `/approvisionnement`, **regroupé par
fournisseur** — l'unité de commande réelle : on passe une commande par
fournisseur, pas une par article.

Deux points de méthode :

- le stock disponible est **affecté par ordre d'ancienneté**, engagements fermes
  d'abord. Sans cela, deux commandes portant sur le même article se croiraient
  chacune servie par le même stock ;
- les devis sont **exclus par défaut** — on ne commande pas sur un devis non
  signé — mais la case « Inclure les devis » permet d'anticiper les délais longs.

Les articles sans référence fournisseur sont isolés dans un groupe à part : on ne
peut ni savoir où commander, ni à quel prix.

### La limite du backend, et comment le front la gère

`OrderLineService.create()` et `update()` lèvent une `InsufficientStockException`
dès que la quantité dépasse `artStock`. `POST /cart` n'a pas ce contrôle. D'où
une asymétrie qu'il faut connaître :

| Situation | Résultat |
| --- | --- |
| Créer un **nouveau** document avec des articles en rupture | ✅ fonctionne |
| Ajouter un article en rupture à un document **déjà enregistré** | ❌ refusé par l'API |
| Augmenter la quantité au-delà du stock sur un document existant | ❌ refusé par l'API |
| Conserver une ligne déjà en base, même en rupture | ✅ fonctionne |

**Le front ne masque pas cette limite, il la rend visible :**

- un bandeau rouge apparaît **dès l'ajout**, sans attendre l'enregistrement, en
  nommant les lignes concernées et les deux issues immédiates (ajuster le stock
  au catalogue, ou créer un nouveau document) ;
- à l'enregistrement, chaque ligne est traitée indépendamment : **une ligne
  refusée n'emporte pas les autres**. Un message unique récapitule ce qui a été
  refusé et pourquoi, au lieu d'une notification par ligne.

**Pour lever définitivement la limite**, appliquez
`backend-patch/OrderLineService.java.patch`. Le fichier explique pourquoi ce
contrôle est doublement infondé : il intervient au mauvais moment — le devis
plutôt que la livraison — et s'appuie sur `artStock`, valeur qu'**aucune vente
ne décrémente** dans le backend actuel. Tant que ce n'est pas corrigé, le stock
affiché reflète votre dernière saisie manuelle, pas votre dépôt.

---

## 5. Visualisations

Aucune bibliothèque de graphiques : les quatre composants de
`shared/charts/` sont du SVG écrit à la main. Le gain n'est pas que la taille du
bundle, c'est le contrôle du rendu et de l'accessibilité.

Les couleurs ne sont **pas choisies à l'œil**. Elles ont été passées à un
validateur vérifiant la bande de clarté OKLCH, le plancher de chroma, la
séparation sous protanopie et deutéranopie, et le contraste sur la surface. Les
valeurs et leurs résultats sont documentés dans `shared/charts/viz-tokens.ts` —
**toute modification doit être revalidée**.

Points saillants :

- deux séries seulement (climatisation / chauffage), avec légende systématique et
  vue tabulaire accessible d'un clic : l'information ne dépend jamais de la seule
  couleur ;
- l'entonnoir utilise une rampe **ordinale** — une teinte, du clair au foncé — car
  ses étapes ont un ordre intrinsèque ;
- en thème sombre, la surface des graphiques est plus foncée que celle des cartes :
  sur la surface des cartes, l'orange tombait à 2,91:1, sous le seuil de 3:1 ;
- un seul axe des ordonnées, jamais deux.

---

## 6. Architecture

```
src/app/
├── core/
│   ├── api/              un service HTTP par contrôleur backend
│   ├── models/           DTO miroirs, cycle de vie, calculs commerciaux et marge
│   ├── services/         magasin commercial, BAN, adresses, toasts, thème
│   └── interceptors/     gestion des erreurs, barre de progression
├── shared/
│   ├── charts/           colonnes empilées, entonnoir, jauge, sparkline (SVG)
│   ├── ui/               modale, tableau, autocomplete, badges…
│   ├── pipes/            euro, taux, date FR, adresse, capitalisation
│   └── validators.ts     téléphone (miroir de @ValidPhoneNumber), code postal
├── layout/               shell applicatif (sidebar, topbar, thème)
└── features/             une vue par domaine métier, toutes en lazy loading
```

### Le magasin commercial

Tableau de bord, pipeline et file d'actions ont besoin du **même** jeu de données
enrichi : documents + lignes + catalogue. Le backend n'expose pas les lignes en
masse — il faut une requête par document. Sans mise en commun, chaque écran
referait ce N+1 à chaque navigation.

`CommerceStore` charge une fois, expose des `signal`, et les vues ne contiennent
que des `computed`. Le rafraîchissement reste explicite (`load()` au montage,
`reload()` après écriture) : pas de rechargement implicite, le comportement reste
prévisible.

Le pipeline n'a plus de mise à jour optimiste locale : avancer un document
crée une entité distincte (un `Quote` n'est pas un `Cart` renommé), donc
chaque transition (`transitionToQuote`, `transitionToCommand`,
`transitionToInvoice`, `markInvoicePaid`) attend la confirmation du serveur
avant de recharger le magasin — la carte ne bouge qu'une fois l'appel réseau
abouti.

### Correspondance avec les DTO

`core/models/api.models.ts` reproduit **exactement** les `record` Java, y compris
les incohérences de nommage du backend, qu'il ne faut donc pas « corriger » côté
front :

- `SupplierRequestDTO` utilise `name` / `email` / `phoneNumber`, alors que
  `SupplierDTO` renvoie `splName` / `splEmail` / `splPhone`
- `CommandDTO` portait son identifiant dans un champ nommé `cmfId` (coquille
  pour `cmdId`) — corrigé côté backend le 4 août 2026, reproduit sous
  `Command.cmdId` côté front
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

## 7. Personnalisation

**Identité de votre société** (en-tête et pied des devis et factures) :
`src/environments/environment.ts` et `environment.prod.ts`, objet `company`
(raison sociale, adresse, SIRET, numéro de TVA, IBAN).

**Couleurs** : `tailwind.config.js`. La palette `brand` (bleu froid) évoque la
climatisation, `heat` (ambre) le chauffage, `ink` sert de gris neutre.

**Icônes** : `shared/ui/icon.component.ts` contient un jeu de tracés SVG intégré.
Pour en ajouter une, insérez son tracé dans la constante `PATHS`.

**Seuil de marge** : `CommerceStore.marginTarget`, dans
`core/services/commerce-store.service.ts`.

**Délais des files d'actions** : les seuils de 7 jours (devis sans réponse) et
30 jours (facture échue) sont dans les `computed` du même fichier.

**Anticipation des achats** : `CommerceStore.forecastSupply` inclut les devis
dans les besoins d'approvisionnement (pilotable depuis l'écran).

**Marquage hors catalogue** : `AD_HOC_CATEGORY` et `AD_HOC_PREFIX`, dans
`core/services/ad-hoc-article.service.ts`.

---

## 8. Points d'attention

- **Le backend cible Java 25.** Vérifiez votre JDK avant de lancer `mvnw`.
- L'API Adresse est un service public : elle nécessite un accès Internet. En cas
  d'indisponibilité, la saisie manuelle des champs reste pleinement fonctionnelle.
- La police Inter est chargée depuis Google Fonts (`index.html`). En environnement
  fermé, supprimez ce `<link>` : la pile de polices système prend le relais.
- Aucune authentification n'est implémentée, le backend n'en expose pas encore.
  L'ajout d'un `HttpInterceptor` porteur de jeton se fera dans
  `core/interceptors/` le moment venu.

## Authentification

L'application est protégée par un écran de connexion. Le garde est posé sur la
route parente qui porte le gabarit : tous les écrans applicatifs en héritent,
sans avoir à le répéter. L'adresse initialement demandée est transmise en
paramètre `suite` et restaurée après identification — ouvrir un lien vers un
devis précis mène bien à ce devis.

| Élément | Rôle |
| --- | --- |
| `core/auth/auth.service.ts` | Session, connexion, déconnexion, lecture du jeton |
| `core/auth/auth.interceptor.ts` | Ajoute `Authorization: Bearer <jeton>` |
| `core/auth/auth.guard.ts` | `authGuard` protège, `guestGuard` évite de revenir sur la connexion |
| `features/auth/login.component.ts` | L'écran de connexion |

### Deux particularités du backend à connaître

`POST /logIn` renvoie le jeton **brut, en `text/plain`** — pas un objet JSON.
D'où le `responseType: 'text'` dans `AuthService.login()` : laisser Angular
tenter un `JSON.parse` ferait échouer *toute connexion réussie*.

Il n'existe **pas de route `/me`**. Le jeton est donc la seule source
d'information sur la session : le front lit sa charge utile pour afficher
l'utilisateur et son rôle. Ce décodage est un confort d'affichage, **pas une
preuve** — la charge utile d'un JWT est de la base64, lisible et falsifiable par
quiconque. Seule la signature vérifiée côté serveur fait foi.

### Ce que la sécurité actuelle ne fait pas

Trois points relevés à la lecture du backend, par ordre d'importance :

1. **Aucune route n'est protégée.** `SecurityConfig` ne contient pas de
   `authorizeHttpRequests`, et comme votre `SecurityFilterChain` remplace celle
   de Spring Boot, plus aucune règle d'accès par URL ne s'applique. Aucune des
   quatre annotations n'est par ailleurs posée sur un contrôleur —
   `ArticleController` importe `isUser` sans jamais s'en servir. **L'API entière
   répond sans jeton.** Tant que ce n'est pas corrigé, masquer des écrans côté
   front reste cosmétique.

2. **Les annotations ne fonctionneront pas telles quelles.**
   `@PreAuthorize("hasRole('ROLE_admin')")` : `hasRole` ajoute lui-même le
   préfixe `ROLE_`, l'expression réclame donc l'autorité `ROLE_ROLE_admin`
   alors que `AppUserDetails` accorde `ROLE_admin`. Écrire `hasRole('admin')`
   ou `hasAuthority('ROLE_admin')`.

3. **Le jeton n'expire jamais** — `Jwts.builder()` sans `setExpiration()`. Un
   jeton dérobé reste valable indéfiniment, et la déconnexion ne peut être que
   locale, faute de route de révocation. Par ailleurs `JwtFilter` fait un
   `substring(7)` sans vérifier le préfixe et parse sans `try/catch` : un jeton
   invalide produit une **500 plutôt qu'une 401**, ce qui empêche le front de
   déconnecter automatiquement sur jeton refusé.

Le secret de signature (`"Saas"`, en dur dans deux fichiers) mérite aussi de
passer en variable d'environnement.

### Rôles

Les quatre rôles — `user`, `superuser`, `admin`, `superadmin` — sont lus depuis
le claim `role` et exposés par `AuthService.hasAtLeast()`. **Aucun écran n'est
masqué à ce stade** : la hiérarchie est en place, la matrice des droits reste à
définir. Un libellé inconnu retombe sur `user`, le rôle le moins privilégié.
