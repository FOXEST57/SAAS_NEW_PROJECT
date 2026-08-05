# Klimafact — état des lieux pour reprendre le travail

Document de reprise. Il est écrit pour une session qui ne connaît rien de
l'historique : il énonce des faits et des décisions, pas le récit des échanges.

Dernière mise à jour : 3 août 2026.

---

## 1. Le projet en deux phrases

Application de devis et facturation pour un installateur de climatisation et
chauffage. Backend **Spring Boot 3.5 / Java 25 / MariaDB**, frontend **Angular 20**
(composants autonomes, signaux, Tailwind).

Le dépôt est `FOXEST57/SAAS_NEW_PROJECT`, avec `back/` et `front/` côte à côte.
Le travail frontend vit sur la branche **`feature-frontend-angular`**.

---

## 2. Comment me transmettre le code

**Les fichiers `.java` ne peuvent pas être lus** depuis une session exécutée dans
le cloud : le dépôt de fichiers refuse cette extension, avec un
`HTTP 400 adding session file`. Le diagnostic est établi — `package.json`,
`pom.xml` et `application.properties` passent, y compris à profondeur égale.

| Mode d'exécution | Accès au code Java |
| --- | --- |
| Dans le cloud | par archive `.zip` jointe au chat, ou fichier renommé en `.java.txt` |
| Sur votre ordinateur | accès direct, aucune manipulation |

Le mode se choisit **au démarrage d'une tâche** (sélecteur « Exécuter cette
tâche », en haut à droite de l'app de bureau). Une session en cours ne peut pas
changer de mode.

En mode cloud, le plus efficace reste de zipper `back/src/main/java` : quelques
centaines de kilo-octets pour l'ensemble du code métier.

---

## 3. Organisation du backend

Le backend a été **restructuré en packages métier** le 3 août. Toute
documentation antérieure citant `service/ArticleService.java` est caduque.

```
com.mns.cda.saas_facturation
├── cart/          Cart, OrderLine, Quote, QuoteLine
├── product/       Article, Category, Tva, Supplier, Maker, Inventory, StockService
├── user/          Customer, AccountType
├── location/      Address, City, Country, PostalCode
├── security/      JwtFilter, SecurityConfig, AppUserDetails, annotations de rôle
├── config/        GlobalExceptionInterceptor, LowercaseConverter, Swagger
├── enumeration/   DeliveryStatus, QuoteStatus
└── exception/     ResourceNotFound, InsufficientStock, SameAccount
```

Chaque domaine garde ses sous-dossiers `controller / service / repository /
model / mapper / DTO`.

---

## 4. Ce qui existe côté frontend

Seize commits sur `feature-frontend-angular`, non poussés — voir §8.

**Pilotage** — tableau de bord, pipeline commercial (glisser-déposer entre
panier, devis, commande, facture, payée), file « À traiter », écran
d'approvisionnement groupé par fournisseur.

**Documents** — éditeur avec calcul de marge à la ligne, aperçu imprimable A4,
création d'article à la volée (préfixe `HC-`), transitions de statut gardées.

**Catalogue et tiers** — CRUD complet sur articles, catégories, TVA, références,
clients, types de compte, fournisseurs, fabricants, et tout le référentiel
géographique.

**Authentification** — écran de connexion, jeton JWT en `localStorage`, garde de
route, intercepteur, compte affiché dans l'en-tête. Le rôle est décodé du jeton
faute de route `/me`.

**Inventaire** — relevés de stock datés, historique par article avec variations,
écart entre le stock compté et le stock annoncé.

**Devis émis** — `Quote` / `QuoteLine` à prix figés, chaîne de révisions par
indice (`DEV-2026-0001` → `-B` → `-C`), statuts brouillon / transmis / tranché,
modification de quantité sur brouillon uniquement.

**Exploitation** — le front peut être servi par Spring Boot sans Node
(`front/backend-patch/SpaConfig.java` + `apiBaseUrl` vide en production).

Tout est documenté dans **`front/README.md`**.

---

## 5. Défauts backend relevés

Les analyses complètes, avec correctifs et chemins à jour, sont dans
**`front/backend-patch/`** :

| Fichier | Contenu |
| --- | --- |
| `Quote-analyse.patch` | Devis : 2 bloquants corrigés, 4 points ouverts |
| `Inventory-blocages.patch` | Inventaire : 8 points, dont 3 bloquants |
| `ArticleService-categories.patch` | Collections immuables, 4 emplacements |
| `OrderLineService.java.patch` | Contrôle de stock (depuis commenté par vous) |

### Encore ouverts, par ordre d'importance

1. **`Inventory` n'a pas `@EntityListeners(AuditingEntityListener.class)`** →
   `@CreatedDate` est inerte, `invDate` reste nulle. Sans date, un relevé
   d'inventaire perd tout son sens. *(product/model/Inventory.java)*

2. **`@NotBlank` sur un `int`** dans `InventoryRequestDTO` → `UnexpectedTypeException`
   à chaque appel, `POST` et `PUT /inventory` répondent 500.
   *(product/DTO/requestDTO/InventoryRequestDTO.java)*

3. **`Quote.qotLines` reste un `@OneToMany` sans `mappedBy`** alors que
   `QuoteLine.quote` existe → JPA voit deux relations indépendantes, une table de
   jointure et une clé étrangère non synchronisées.
   `findByQuote_QotIdAndArticleRef` ne renverra donc jamais rien.
   *(cart/model/Quote.java)*

4. **`IllegalStateException` n'est pas interceptée** → les gardes de statut
   renvoient 500 au lieu de 409, indiscernable d'une panne.
   *(config/GlobalExceptionInterceptor.java)*

5. **`QuoteMapper.toDTO` s'appelle deux fois par niveau** → complexité en 2ⁿ sur
   la profondeur de la chaîne de révisions. Une ligne à changer.
   *(cart/mapper/QuoteMapper.java, ligne 55)*

6. **`QuoteService.create` n'est pas `@Transactional`**, et `delete` casse sur un
   devis servant de parent. *(cart/service/QuoteService.java)*

7. **Aucune route n'est protégée** : `SecurityConfig` n'a pas de
   `authorizeHttpRequests`, et aucune annotation de rôle n'est posée. Par
   ailleurs `hasRole('ROLE_admin')` réclame en réalité `ROLE_ROLE_admin`, le
   préfixe étant ajouté deux fois. Le jeton n'expire jamais.
   *(security/)*

8. **`InventoryMapper` inverse `artReference` et `artName`** — deux `String`
   adjacents, le compilateur ne peut rien signaler.
   *(product/mapper/InventoryMapper.java)*

---

## 6. Décisions en suspens

**Le cumul des totaux du parent.** `QuoteMapper` ajoute les totaux du devis
parent à ceux de l'enfant. Si une révision reprend toutes les lignes, ce cumul
double le montant ; si elle ne porte que les ajouts, il est juste. Le front
affiche les deux valeurs et signale l'écart, en attendant l'arbitrage.

**Jusqu'où figer les prix.** `QuoteLine` fige le devis, mais la facture est un
simple statut du panier et `OrderLine` ne porte aucun prix : une facture émise
aujourd'hui est recalculée aux tarifs du jour. Un devis signé à 899 € l'unité
peut donc être facturé à un autre montant. La question à trancher : **à quel
moment le prix devient-il définitif ?** À l'émission, à l'acceptation, ou à la
facturation.

**La matrice des droits par rôle.** Quatre rôles existent (`user`, `superuser`,
`admin`, `superadmin`), la hiérarchie est en place côté front via `hasAtLeast()`,
mais aucun écran n'est masqué. Tant que le backend ne protège rien, le masquage
resterait cosmétique.

**Améliorations UX identifiées, non construites** — par ordre de valeur :
protection contre la perte de saisie (aucun `CanDeactivate` dans le projet),
duplication de document, catalogue de l'éditeur tronqué à 40 sans le dire,
pipeline inutilisable sous 1024 px, filtres de liste perdus au retour, absence
de saisie au clavier, premier lancement sans guidage.

---

## 7. Méthode de travail

**Vérification.** Chaque changement est compilé (`npx ng build`) puis éprouvé au
navigateur avec Playwright — parcours réel, pas seulement un rendu. Les captures
servent de preuve.

**Le mock.** `mock-api.js` reproduit le contrat du backend **défauts compris** :
absence de champ, cumul des totaux parents, 409 sur devis figé. C'est délibéré —
tester contre un serveur plus clément que la réalité ne prouve rien. Il tourne
sur le port 8080, le front sur 4200.

**Les correctifs backend** sont livrés en fichiers `.patch` explicatifs, jamais
appliqués directement : le backend appartient à l'utilisateur.

**Aucune écriture sur la machine de l'utilisateur.** Tout est livré en archive et
en bundle git, qu'il applique lui-même.

---

## 8. Le dépôt

Seize commits sur `feature-frontend-angular`, **non poussés** : aucun identifiant
GitHub valable n'est disponible depuis le bac à sable, et l'API GitHub y répond
403. Le travail circule par `front-angular.bundle` :

```bash
git fetch /chemin/vers/front-angular.bundle feature-frontend-angular:feature-frontend-angular
git checkout feature-frontend-angular
git push -u origin feature-frontend-angular
```

Vérifié : le bundle s'importe proprement dans un clone neuf.

---

## 9. Premier geste d'une nouvelle session

1. Lire `front/README.md` — l'architecture y est documentée.
2. Lire `front/backend-patch/` — les défauts et leurs correctifs.
3. Demander une archive de `back/src/main/java` si le mode est « dans le cloud ».
4. Reprendre là où on en est : la relecture de la partie devis dans la nouvelle
   organisation en packages métier n'a pas encore été faite.
