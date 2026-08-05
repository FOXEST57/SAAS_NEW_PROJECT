# Gestion du stock — remise à plat

> Note de conception, rédigée le 5 août 2026 à partir de l'état du code sur la
> branche `feature-fournisseur`. À lire avant de coder la réservation de stock
> à la validation d'un devis.

---

## 1. Ce que fait le code aujourd'hui

Il n'existe **aucune colonne `stock`** sur `Article`. Le stock n'est jamais
enregistré ni modifié : il est **recalculé** à chaque appel.

```
Stock actuel = dernier inventaire physique − ce qui est sorti depuis cette date
```

`Inventory` est un comptage daté, et `QuoteLineRepository.getSentQuantityByArticle()`
additionne les quantités parties depuis.

**C'est un bon choix de conception** : auditable, aucun compteur qui dérive, on
peut toujours expliquer un chiffre. Il faut simplement en avoir conscience avant
de vouloir « décrémenter » : dans ce modèle on n'écrit pas un solde, on
enregistre un mouvement.

---

## 2. Les problèmes constatés

### 2.1 — Deux définitions contradictoires du stock (le plus urgent)

`ArticleMapper.calculStock()` fait la somme de
`SupplierReference.splRefStock + MakerReference.artMkrStock` — donc les stocks
**détenus par les fournisseurs** — et l'expose dans le champ `artStock` de tous
les `ArticleDTO`.

Conséquence : `/article/list` et `/stock/list` renvoient **deux chiffres
différents pour le même article**. Le front affiche l'un ou l'autre selon
l'écran.

C'est le problème le plus dangereux du lot, parce qu'il est silencieux : rien
ne casse, les chiffres sont simplement faux à un endroit.

### 2.2 — Le code ne fait pas ce que le commentaire annonce

L'en-tête de `StockService` décrit six notions et précise
« Stock commandé : à partir du devis ». Or la requête filtre sur
`cmd.cmdStatus = 'DELIVERED'` — ni le devis, ni la facture, mais la commande
livrée.

Trois intentions différentes cohabitent dans le même fichier. La requête n'est
pas fausse (elle calcule bien une sortie physique), elle est rangée sous un nom
qui promet autre chose.

### 2.3 — Les notions manquantes

Seules `getActualStock()` et `getPendingStock()` existent. « Stock commandé »
(réservé par les devis) et « stock disponible » sont décrits en commentaire
mais n'ont jamais été codés.

**C'est précisément la brique attendue pour la réservation à la validation du
devis.**

### 2.4 — Les réceptions ne remontent jamais le stock

Dans `getActualStock()`, la ligne qui ajoutait les réceptions fournisseurs
(`DeliveryStatus.RECEIVED`) est commentée.

Concrètement : on reçoit une palette, le stock ne bouge pas tant que personne
ne refait un inventaire physique.

### 2.5 — Aucun contrôle de disponibilité

`InsufficientStockException` existe mais n'est levée **à aucun endroit** du
projet. Les deux contrôles dans `OrderLineService` sont en commentaire.

Rien n'empêche aujourd'hui de vendre 100 unités quand il y en a 2.

### 2.6 — Un piège technique

`getPendingStock()` appelle `calculStockByStatus(article, PENDING, null)`.
La branche `PENDING` ignore la date, donc ça passe aujourd'hui — mais la
branche `RECEIVED` fait `isAfter(inventoryDate)`.

Le jour où quelqu'un réactive la ligne commentée du point 2.4 avec une date
nulle : `NullPointerException`.

### 2.7 — Performance

`StockController.getAllActualStock()` fait, **par article**, un `findById` plus
une requête sur les lignes de devis. Avec 500 articles, plus de 1 000 requêtes
pour afficher un écran.

---

## 3. Le principe directeur

Un seul invariant à tenir :

> **Une notion = une définition = une méthode.**

Aujourd'hui « le stock » désigne trois choses selon l'endroit du code. C'est la
racine de tout le reste.

Et une distinction que le code actuel ne fait pas — il y a **deux natures
d'information**, qu'on ne peut pas calculer de la même façon :

- le **physique** — ce qui est dans l'entrepôt. Résultat de mouvements réels :
  on compte, on reçoit, on livre ;
- l'**engagement** — ce qui est promis, aux clients ou par les fournisseurs. Ça
  ne bouge aucune palette, ça se lit dans les documents commerciaux.

Mélanger les deux est ce qui rend la requête actuelle inexplicable.

---

## 4. Les six notions

| Notion | Définition | Source | Sert à |
|---|---|---|---|
| **Physique** | Ce qui est réellement en entrepôt | Mouvements | Inventaire, litiges |
| **Attendu** | Commandé aux fournisseurs, pas encore reçu | Lignes fournisseur `PENDING` / `ACCEPTED` | Suivi des appros |
| **Réservé** | Engagé auprès des clients, pas encore sorti | Devis acceptés non livrés | Ne pas vendre deux fois |
| **Disponible** | Physique − Réservé | Calcul | **Le chiffre du commercial** |
| **Théorique** | Physique + Attendu | Calcul | Ce qu'on aura si tout arrive |
| **Projeté** | Physique + Attendu − Réservé | Calcul | **Faut-il réapprovisionner ?** |

Deux seulement sont fondamentales — **physique** et **réservé**. Les quatre
autres en découlent par simple addition. Il n'y a donc que deux choses
difficiles à calculer juste.

Le chiffre à afficher partout dans l'interface de vente est **disponible**, pas
« physique ». Vendre le dernier climatiseur déjà promis à quelqu'un d'autre est
l'erreur que ce modèle doit rendre impossible.

---

## 5. Quel événement joue sur quoi

| Événement | Effet |
|---|---|
| Inventaire physique | Recale le physique |
| Réception fournisseur | Physique **+** |
| **Devis accepté** | **Réservé +** |
| Commande livrée | Physique **−**, Réservé **−** |
| Devis refusé / expiré / révisé | Réservé **−** |
| Facture | **Aucun** — acte comptable, pas logistique |

Ce tableau règle la question de départ :

> **Valider un devis ne décrémente pas le stock physique : ça crée une
> réservation.** La marchandise est encore là, elle est simplement promise.
> Elle ne sort qu'à la livraison.

C'est aussi ce qui explique pourquoi la requête actuelle sur
`cmdStatus = 'DELIVERED'` est correcte dans son calcul — elle mesure bien une
sortie physique.

---

## 6. Deux façons d'y arriver

### Option A — consolider l'existant

On garde le modèle dérivé, et on corrige :

1. supprimer `ArticleMapper.calculStock()` de `ArticleDTO`, ou le renommer
   `supplierStock` — ce n'est pas notre stock, c'est celui du fournisseur ;
2. réactiver la branche `RECEIVED`, en corrigeant le `null` qui la ferait
   planter ;
3. ajouter `getReservedStock()` et `getAvailableStock()` ;
4. remplacer les boucles de `StockController` par une requête groupée
   `GROUP BY articleRef`.

Pas de changement de schéma. Faisable dans la journée.

### Option B — une table de mouvements

Une entité `StockMovement` : article, quantité signée, type
(`INVENTAIRE`, `RÉCEPTION`, `LIVRAISON`, `CORRECTION`), date, document
d'origine.

Le physique devient alors **une seule requête** : `SUM(quantité)`. Une seule,
pour tous les articles, indexable. Et surtout on peut **expliquer** n'importe
quel chiffre en listant les mouvements — c'est ce que réclamera le premier
client qui contestera un stock.

Le réservé, lui, **reste dérivé des documents** même dans cette option : une
réservation n'est pas un mouvement physique. C'est la nuance qui fait que ce
modèle reste juste.

---

## 7. Recommandation

**A d'abord, B ensuite.**

A supprime les incohérences immédiates sans toucher au schéma ni casser le
travail en cours. B est la cible, et la migration est indolore : les mouvements
passés se regénèrent depuis les commandes livrées et les inventaires existants.

### Ordre de chantier

1. **Trancher le vocabulaire** — les six noms ci-dessus, écrits noir sur blanc,
   avant toute ligne de code. Seule étape vraiment indispensable.
2. Supprimer la double définition du stock (§ 2.1) — le point le plus dangereux
   parce qu'il est silencieux.
3. Ajouter `réservé` et `disponible`.
4. Brancher le contrôle de disponibilité **à la validation du devis**, contre le
   disponible — pas à l'ajout d'une ligne au panier, un panier n'engage rien.
   `InsufficientStockException` existe déjà, elle n'attend que d'être levée au
   bon endroit.
5. Regrouper les requêtes (§ 2.7).
