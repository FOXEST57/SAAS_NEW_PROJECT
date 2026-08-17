# La génération des PDF — le parcours complet

> Ce document suit **le chemin réel de l'exécution**, du clic de l'utilisateur
> jusqu'au fichier sur le disque. Chaque classe est présentée au moment où le
> programme y arrive.
>
> À lire dans l'ordre. On suit d'abord la facture de bout en bout, puis on
> regarde ce qui change pour le devis.

---

## Sommaire

**Avant de partir**
- [0. Pourquoi on fabrique un PDF](#0-pourquoi-on-fabrique-un-pdf)
- [0.1 La carte du voyage](#01-la-carte-du-voyage)

**Le parcours d'une facture**
- [Étape 1 — La requête arrive](#étape-1--la-requête-arrive)
- [Étape 2 — On crée la facture en base](#étape-2--on-crée-la-facture-en-base)
- [Étape 3 — Entrée dans la chaîne PDF](#étape-3--entrée-dans-la-chaîne-pdf)
- [Étape 4 — Le mapper transforme l'entité en DTO](#étape-4--le-mapper-transforme-lentité-en-dto)
- [Étape 5 — On récupère les infos de l'entreprise](#étape-5--on-récupère-les-infos-de-lentreprise)
- [Étape 6 — Le moteur fabrique le PDF](#étape-6--le-moteur-fabrique-le-pdf)
- [Étape 7 — Le stockage écrit le fichier](#étape-7--le-stockage-écrit-le-fichier)
- [Étape 8 — Retour au service métier](#étape-8--retour-au-service-métier)
- [Étape 9 — Plus tard : le téléchargement](#étape-9--plus-tard--le-téléchargement)

**Le parcours d'un devis**
- [Ce qui est identique](#ce-qui-est-identique)
- [Ce qui change](#ce-qui-change)

**Pour finir**
- [Ce qui traverse tout le parcours](#ce-qui-traverse-tout-le-parcours)
- [Tous les fichiers](#tous-les-fichiers)
- [Ce qui reste à faire](#ce-qui-reste-à-faire)
- [Les cinq idées à retenir](#les-cinq-idées-à-retenir)

---

## 0. Pourquoi on fabrique un PDF

Avant de suivre le code, il faut comprendre **pourquoi on ne se contente pas
d'imprimer la page web**.

C'est la première idée qui vient : le navigateur sait imprimer, `window.print()`
suffit. Sauf que non — et la raison n'est pas technique.

> Une facture est un document qui **fait foi**. Celui qu'on relit dans trois ans
> doit être identique à celui qu'on a envoyé au client.

Si on regénérait le document à chaque affichage, tout changerait avec le temps :
le logo, l'adresse du siège, une mention légale corrigée, le prix de l'article
au catalogue. Le document affiché ne serait plus celui reçu par le client. En
cas de litige, impossible de prouver quoi que ce soit.

D'où **la règle qui gouverne tout le reste** :

> Le PDF est fabriqué **une seule fois**, au moment de la validation métier,
> puis plus jamais retouché.

| Document | Moment de fabrication | Pourquoi ce moment |
|---|---|---|
| Facture | À l'émission | Elle n'existe que si elle est émise |
| Devis | À la transmission (`PENDING`) | Avant, c'est un brouillon encore modifiable |

Le téléchargement, lui, **relit un fichier**. Il ne fabrique rien.

---

## 0.1 La carte du voyage

Voici tout le trajet. Gardez-la sous les yeux : chaque étape qui suit est un
point de cette carte.

```
  [Utilisateur]
       │  POST /invoice
       ▼
  ┌─────────────────────┐
  │ InvoiceController   │  Étape 1 — porte d'entrée HTTP
  └─────────┬───────────┘
            ▼
  ┌─────────────────────┐
  │ InvoiceService      │  Étape 2 — logique métier, save()
  └─────────┬───────────┘
            ▼
  ┌─────────────────────┐
  │ InvoicePdfService   │  Étape 3 — chef d'orchestre PDF
  └─────────┬───────────┘
            │
            ├──▶ InvoicePdfMapper ──▶ InvoicePdfDTO      Étape 4  (entité → données)
            │         └──▶ DocumentFormat
            │
            ├──▶ ICompanyService ──▶ CompanyProperties   Étape 5  (nos coordonnées)
            │
            ├──▶ IPdfGenerator ──▶ invoice.html          Étape 6  (données → octets)
            │
            └──▶ IDocumentStorageService                 Étape 7  (octets → fichier)
                      │
                      ▼
            data/documents/invoices/2026/FAC-2026-0001.pdf
```

Quatre collaborateurs, appelés dans cet ordre. Chacun ignore le travail des
autres.

---

# Le parcours d'une facture

## Étape 1 — La requête arrive

L'utilisateur valide une commande. Le front appelle :

```
POST /invoice
{ "invoiceNumber": "FAC-2026-0001", "commandId": 7 }
```

### Le fichier : `cart/controller/InvoiceController.java`

```java
@PostMapping("")
public ResponseEntity<InvoiceDTO> create(@Valid @RequestBody InvoiceRequestDTO dto) {
    InvoiceDTO response = invoiceService.create(dto);
    return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
}
```

**Le rôle d'un contrôleur** : traduire du HTTP en appel Java, et rien d'autre.
Il ne calcule pas, ne décide pas. Trois lignes, c'est normal.

`@Valid` déclenche la validation du DTO d'entrée (`@NotBlank`, `@NotNull`) avant
même d'entrer dans la méthode.

---

## Étape 2 — On crée la facture en base

### Le fichier : `cart/service/InvoiceService.java`

C'est ici qu'est la logique métier. Le code, commenté pas à pas :

```java
@Override
@Transactional
public InvoiceDTO create(InvoiceRequestDTO invoiceRequestDTO) {

    // (a) On retrouve la commande à facturer
    Command command = commandRepository.findById(invoiceRequestDTO.commandId())
            .orElseThrow(() -> new ResourceNotFoundException("Commande non existante"));

    // (b) Une commande ne peut être facturée qu'une fois
    if (invoiceRepository.existsByCommand_CmdId(command.getCmdId())) {
        throw new ResourceAlreadyExistException("Une facture existe déjà pour cette commande.");
    }

    // (c) On construit la facture, avec un chemin PDF TEMPORAIRE
    Invoice invoice = new Invoice();
    invoice.setInvoiceNumber(invoiceRequestDTO.invoiceNumber());
    invoice.setInvoiceStatus(InvoiceStatus.CREATED);
    invoice.setCommand(command);
    invoice.setInvoicePathPDF(PDF_PENDING);

    // (d) On recopie les lignes du devis, figées
    command.getQuote().getQotLines().forEach(ql -> {
        InvoiceLine line = invoiceLineService.build(ql);
        line.setInvoice(invoice);
        invoice.getInvoiceLines().add(line);
    });

    // (e) Premier enregistrement
    Invoice saved = invoiceRepository.save(invoice);

    // (f) ICI on entre dans la chaîne PDF
    saved.setInvoicePathPDF(invoicePdfService.generate(saved));

    // (g) Second enregistrement, avec la vraie clé
    return invoiceMapper.toDTO(invoiceRepository.save(saved));
}
```

### Pourquoi deux `save()` ? La question qui revient toujours

C'est une contrainte JPA, pas un choix.

`invoiceCreatedDate` est annoté `@CreatedDate` : il est rempli **au moment du
`save()`**, par l'audit JPA. Avant, il vaut `null`.

Or le PDF en a besoin — trois fois :

- la date d'émission affichée sur le document
- l'échéance (date + 30 jours)
- l'année du dossier de rangement (`invoices/2026/…`)

Donc : il faut sauvegarder **avant** de générer.

Mais `invoicePathPDF` est `@NotBlank` et `nullable = false` : on ne peut pas
sauvegarder avec une valeur vide. D'où la valeur temporaire :

```java
private static final String PDF_PENDING = "en cours de génération";
```

Séquence : on enregistre avec un bouche-trou → la date existe → on génère → on
remplace par la vraie clé.

### Pourquoi les lignes sont recopiées (d)

`InvoiceLine` recopie la désignation, la référence, le prix et le taux de TVA
plutôt que de pointer vers l'`Article`.

C'est volontaire : si le prix du catalogue augmente demain, la facture d'hier ne
doit pas changer. On appelle ça **figer** les lignes. Le devis fait pareil avec
`QuoteLine`.

---

## Étape 3 — Entrée dans la chaîne PDF

### Le fichier : `cart/service/InvoicePdfService.java`

C'est le **chef d'orchestre**. La classe la plus courte du lot, et c'est normal :
elle ne fait qu'appeler les autres dans le bon ordre.

```java
@Override
@Transactional(readOnly = true)
public String generate(Invoice invoice) {

    // ÉTAPE 4 — entité → données prêtes à afficher
    InvoicePdfDTO document = invoicePdfMapper.toDTO(invoice);

    // ÉTAPE 5 — les variables que le template pourra utiliser
    Map<String, Object> model = Map.of(
            "invoice", document,
            "company", companyService.getCompany()
    );

    // ÉTAPE 6 — données → octets PDF
    byte[] pdf = pdfGenerator.generatePdf(TEMPLATE, model);

    // ÉTAPE 7 — octets → fichier, et on renvoie la clé
    return storageService.store(pdf, buildKey(invoice));
}
```

Quatre lignes utiles. Cette classe **ne sait pas** lire la base, ni dessiner un
PDF, ni écrire un fichier. Elle sait seulement dans quel ordre demander.

### Le détail le plus important du fichier

> **Le mapping est la toute première instruction.** Ce n'est pas un hasard.

Tant qu'on est dans la transaction, JPA peut encore charger les relations
(`invoice.getCommand().getQuote()…`). Après cette ligne, on ne travaille plus
que sur des données simples, et plus rien ne dépend de la base.

Si on inversait l'ordre, on obtiendrait une `LazyInitializationException` — la
fameuse erreur « no Session » quand on lit une relation hors transaction.

### La clé de stockage

```java
private String buildKey(Invoice invoice) {
    int year = invoice.getInvoiceCreatedDate().getYear();
    String safeNumber = invoice.getInvoiceNumber().replaceAll("[^A-Za-z0-9._-]", "_");
    return "invoices/" + year + "/" + safeNumber + ".pdf";
}
```

Résultat : `invoices/2026/FAC-2026-0001.pdf`

Deux précautions :

- **le classement par année** évite des dizaines de milliers de fichiers dans un
  seul dossier au bout de quelques années ;
- **le nettoyage du numéro** : il vient de l'extérieur et pourrait contenir des
  `../`. Le stockage se protège déjà (étape 7), mais on ne compte pas dessus
  pour autant.

---

## Étape 4 — Le mapper transforme l'entité en DTO

### Le fichier : `cart/mapper/InvoicePdfMapper.java`

C'est ici qu'est **toute la vraie logique**. Le mapper prend l'entité et produit
un objet contenant exactement ce que le document doit afficher.

### D'abord : pourquoi ne pas passer l'entité directement au template ?

Trois raisons, dans l'ordre d'importance.

**1. Le template planterait.** Pour afficher la ville du client, il faudrait
écrire :

```
invoice.command.quote.cart.customer.address.city.cityName
```

Sept niveaux. Un seul `null` et tout casse. Pire : à ce moment-là la transaction
est fermée, donc `LazyInitializationException`.

**2. Le template doit rester bête.** Il affiche, il ne calcule pas.

**3. Le DTO est exactement ce qui sera figé.** C'est la photo du document.

### Ce que produit le mapper : `cart/DTO/InvoicePdfDTO.java`

```java
public record InvoicePdfDTO(
        String invoiceNumber,
        String issuedDate,          // déjà écrit "05/08/2026"
        String dueDate,

        String customerName,
        String customerAddress,     // adresse déjà assemblée sur une ligne
        String customerEmail,
        String customerPhone,

        List<DocumentPdfLineDTO> lines,
        List<DocumentPdfTvaDTO> tvaBreakdown,

        BigDecimal totalHT,
        BigDecimal totalTVA,
        BigDecimal totalTTC
) { }
```

> **Pourquoi les dates sont des `String`** : formater une date dépend de la
> langue. C'est de la logique — et la logique n'a rien à faire dans un template.
>
> **Pourquoi les montants restent des `BigDecimal`** : c'est le seul type correct
> pour de l'argent. `double` fait des erreurs d'arrondi
> (`0.1 + 0.2 = 0.30000000000000004`). Le template sait les afficher.

### Les deux listes, et pourquoi elles sont séparées

```java
List<DocumentPdfLineDTO> lines,        // les articles
List<DocumentPdfTvaDTO> tvaBreakdown,  // le récapitulatif de TVA
```

Ce ne sont **pas les mêmes objets, ni en même nombre**. Une facture de 12
articles à trois taux produit :

- **12** `DocumentPdfLineDTO` — une par article
- **3** `DocumentPdfTvaDTO` — un par taux

La seconde liste est une **agrégation** de la première. Si on les fusionnait en
un seul record, il faudrait tous les champs des deux, dont six seraient `null`
la moitié du temps — et rien n'empêcherait de construire un objet incohérent.

> **La règle** : un DTO décrit une chose, pas plusieurs choses possibles. Dès
> qu'un champ n'a de sens que « dans certains cas », c'est qu'il y a deux types
> déguisés en un.

Ces deux DTO sont dans `document/DTO/`, pas dans `cart/` : une ligne imprimable
est identique pour un devis et pour une facture.

### Le code, et ses trois pièges

```java
public InvoicePdfDTO toDTO(Invoice invoice) {
    Customer customer = customerOf(invoice);              // PIÈGE 1
    List<InvoiceLine> sourceLines = ...;

    List<DocumentPdfLineDTO> lines = new ArrayList<>();
    Map<BigDecimal, BigDecimal> baseByRate = new TreeMap<>();   // PIÈGE 2
    BigDecimal totalHT = BigDecimal.ZERO;

    for (InvoiceLine line : sourceLines) {
        BigDecimal lineHT = DocumentFormat.money(             // PIÈGE 3
                line.getInvLnPriceHT().multiply(BigDecimal.valueOf(line.getInvLnQuantity()))
        );
        BigDecimal rate = line.getTvaRate();

        lines.add(new DocumentPdfLineDTO(
                DocumentFormat.upperCase(line.getArticleRef()),
                DocumentFormat.capitalize(line.getArticleName()),
                line.getInvLnQuantity(),
                line.getInvLnPriceHT(),
                DocumentFormat.tvaLabel(rate),
                lineHT
        ));

        totalHT = totalHT.add(lineHT);
        baseByRate.merge(rate, lineHT, BigDecimal::add);
    }
    // … puis on parcourt baseByRate pour construire tvaBreakdown
}
```

#### Piège 1 — retrouver le client

La facture ne connaît pas son client. Il faut remonter toute la chaîne :

```
Invoice → Command → Quote → Cart → Customer
```

Et si un maillon manque ?

```java
if (customer == null) {
    throw new PdfGenerationException(
            "Impossible de générer la facture " + invoice.getInvoiceNumber()
                    + " : aucun client n'est rattaché à ce document."
    );
}
```

On **échoue franchement**, avec un message clair. Une facture doit
obligatoirement identifier son destinataire : imprimer un trou serait pire que
ne rien imprimer.

#### Piège 2 — regrouper la TVA par taux

La loi impose de détailler la TVA **taux par taux**. En chauffage et
climatisation, une facture mélange couramment 5,5 %, 10 % et 20 %.

Le piège est subtil :

> `BigDecimal.equals()` compare **aussi le nombre de décimales**.
> `0.20` et `0.2` sont donc considérés comme **différents**.

Avec une `HashMap`, on obtiendrait deux lignes de TVA pour le même taux.

La solution — un `TreeMap`, qui compare avec `compareTo()` (insensible au nombre
de décimales) et trie les taux au passage :

```java
Map<BigDecimal, BigDecimal> baseByRate = new TreeMap<>();
baseByRate.merge(rate, lineHT, BigDecimal::add);
```

`merge` signifie : « si ce taux existe déjà, additionne ; sinon, crée ».

#### Piège 3 — arrondir avant d'additionner

Si le client additionne les lignes à la main sur le papier, il doit retrouver le
total imprimé.

On arrondit donc **chaque ligne**, puis on additionne des valeurs déjà
arrondies :

```java
BigDecimal lineHT = DocumentFormat.money(prix.multiply(quantité));
totalHT = totalHT.add(lineHT);   // on additionne du déjà-arrondi
```

L'inverse — additionner puis arrondir — donnerait un total juste
mathématiquement, mais **faux visuellement**. À un centime près : exactement
celui que le client remarquera.

### L'outil du mapper : `document/DocumentFormat.java`

Une classe utilitaire, méthodes `static` :

| Méthode | Exemple |
|---|---|
| `formatAddress()` | → `12 rue de la Paix, 69001 LYON` |
| `capitalize()` | `casque audio` → `Casque audio` |
| `upperCase()` | `ref-004` → `REF-004` |
| `tvaLabel()` | `0.055` → `5,5 %` |
| `money()` | arrondit à 2 décimales, `HALF_UP` |

**Pourquoi elle existe** : les articles et les villes sont enregistrés en
minuscules (à cause du `LowercaseConverter`, pratique pour les recherches). Sur
un document client, « casque audio » fait négligé. On corrige donc **à
l'affichage seulement** — la donnée en base n'est jamais touchée.

> **Pourquoi `static` et pas un bean Spring** : ce sont des fonctions pures.
> Même entrée, même sortie, aucun état, aucune dépendance. Les injecter
> n'ajouterait que de la cérémonie.

> **Quand cette classe a été créée** : pas au début. Seulement **après** avoir
> constaté que le devis avait besoin exactement des mêmes méthodes. On ne
> factorise pas avant d'avoir la preuve que c'est utile.

---

## Étape 5 — On récupère les infos de l'entreprise

De retour dans `InvoicePdfService` :

```java
Map<String, Object> model = Map.of(
        "invoice", document,
        "company", companyService.getCompany()   // ← ici
);
```

Nos coordonnées (nom, adresse, SIRET, TVA, IBAN) apparaissent sur tous les
documents. Elles ne sont donc **pas** dans `InvoicePdfDTO` — elles sont
identiques partout, ce serait les recopier pour rien.

### Trois fichiers pour ça

```
config/CompanyProperties.java              les valeurs
document/Iservice/ICompanyService.java     le contrat
document/service/PropertiesCompanyService.java   l'implémentation
```

**Les valeurs** — un `record` rempli par Spring depuis `application.properties` :

```java
@ConfigurationProperties(prefix = "app.company")
public record CompanyProperties(
        String name, String tagline, String address,
        String siret, String tvaNumber,
        String email, String phone, String iban
) { }
```

```properties
app.company.name=Klimafact SARL
app.company.siret=000 000 000 00000
app.company.tva-number=FR00000000000
```

> **À noter** : on écrit `tva-number` avec un tiret dans le `.properties`, et
> Spring le relie tout seul au champ `tvaNumber`. C'est le *relaxed binding*.

Une annotation sur la classe d'application active le tout :

```java
@SpringBootApplication
@ConfigurationPropertiesScan   // détecte les classes @ConfigurationProperties
public class SaasFacturationApplication { }
```

### Pourquoi une interface pour ça ?

```java
public interface ICompanyService {
    CompanyProperties getCompany();
}
```

Une seule méthode, une seule implémentation aujourd'hui. Pourquoi s'embêter ?

Parce qu'un jour, ces informations devront être **modifiables par
l'utilisateur** depuis un écran de réglages, donc stockées en base — sans quoi
il faut redéployer pour changer un IBAN.

Ce jour-là, on écrira `DatabaseCompanyService` qui implémente la même interface,
et **rien dans le code de génération ne changera**.

---

## Étape 6 — Le moteur fabrique le PDF

```java
byte[] pdf = pdfGenerator.generatePdf(TEMPLATE, model);   // TEMPLATE = "invoice"
```

### Le contrat : `document/Iservice/IPdfGenerator.java`

```java
public interface IPdfGenerator {
    byte[] generatePdf(String templateName, Map<String, Object> model);
}
```

Le `model` est une `Map` : la clé devient le **nom de la variable dans le
template**, la valeur est la donnée.

```java
Map.of("invoice", document, "company", …)
     ↓                        ↓
   ${invoice.…}            ${company.…}   dans invoice.html
```

### L'implémentation : `document/service/ThymeleafPdfGenerator.java`

Deux bibliothèques travaillent à la chaîne :

```java
public byte[] generatePdf(String templateName, Map<String, Object> model) {
    String html = renderHtml(templateName, model);   // Thymeleaf
    return convertToPdf(html);                       // openhtmltopdf
}
```

**Thymeleaf** : template + données → chaîne HTML

```java
private String renderHtml(String templateName, Map<String, Object> model) {
    Context context = new Context();
    context.setVariables(model);
    return templateEngine.process(templateName, context);
}
```

**openhtmltopdf** : chaîne HTML → octets PDF

```java
private byte[] convertToPdf(String html) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, "");
        builder.toStream(out);
        builder.run();
        return out.toByteArray();
    } catch (IOException e) {
        throw new PdfGenerationException("Impossible de générer le PDF", e);
    }
}
```

> **À noter** : on utilise Thymeleaf **programmatiquement**, pas comme moteur de
> vue Spring MVC. Tous nos contrôleurs restent des `@RestController` qui
> renvoient du JSON. Thymeleaf ne sert qu'à fabriquer une chaîne en mémoire.

### Le template : `resources/templates/invoice.html`

Le nom `"invoice"` suffit : Thymeleaf ajoute tout seul le dossier `templates/`
et l'extension `.html`.

#### Deux contraintes d'openhtmltopdf

Ce n'est **pas un navigateur**, il ne pardonne rien :

- **XHTML strict** — chaque balise fermée, y compris `<meta />` et `<br />`.
  Une balise oubliée = exception à la génération.
- **CSS 2.1** — pas de flexbox, pas de grid. La mise en page se fait avec des
  tableaux. C'est daté, mais c'est ce qui fonctionne.

Le format de page se déclare en CSS :

```css
@page {
    size: A4 portrait;
    margin: 18mm 16mm 20mm 16mm;
}
```

#### Les valeurs visibles ne sont pas « en dur »

```html
<span th:text="${quote.expirationDate}">05/11/2026</span>
```

`th:text` **remplace tout le contenu de la balise**. Le `05/11/2026`
n'apparaîtra jamais dans le PDF.

Alors pourquoi l'écrire ? Parce qu'on peut ouvrir `invoice.html` **directement
dans un navigateur**, sans lancer l'application, et voir un document réaliste
pour travailler la mise en page. C'est le principe des *natural templates*, la
particularité de Thymeleaf.

C'est aussi pourquoi on voit « Jean Dupont » ou « 2 500,00 € » un peu partout.

#### Afficher un nombre en français

```html
th:text="${#numbers.formatDecimal(line.totalHT, 1, 'WHITESPACE', 2, 'COMMA')} + ' €'"
```

Se lit : au moins 1 chiffre avant la virgule, espace comme séparateur de
milliers, exactement 2 décimales, virgule comme séparateur décimal.

Résultat : `1 250,00 €`

#### Un piège rencontré en vrai

Thymeleaf ne sait pas échapper une apostrophe dans une expression. Ceci **ne
fonctionne pas** :

```html
<div th:text="'Valable jusqu''au ' + ${quote.expirationDate}">
```

La bonne forme — le texte fixe reste en HTML, `th:text` ne sert qu'aux valeurs :

```html
<div>Valable jusqu'au <span th:text="${quote.expirationDate}">05/11/2026</span></div>
```

> **Règle générale** : plus un template contient de logique, plus il est
> fragile.

---

## Étape 7 — Le stockage écrit le fichier

```java
return storageService.store(pdf, buildKey(invoice));
```

### L'idée clé : la clé logique

Le stockage ne manipule **jamais de chemin physique**. Uniquement une chaîne
appelée *clé* :

```
invoices/2026/FAC-2026-0001.pdf
```

Ce n'est **pas** un chemin sur le disque : c'est un identifiant. Le service de
stockage se débrouille pour savoir à quoi ça correspond réellement.

Pourquoi c'est important : le jour où on passe au cloud, la clé reste la même.
Seule la classe qui la traduit change.

### Le contrat : `document/Iservice/IDocumentStorageService.java`

```java
public interface IDocumentStorageService {
    String store(byte[] content, String key);   // range ce fichier
    Resource retrieve(String key);              // rends-moi ce fichier
    boolean exists(String key);                 // ce fichier existe-t-il ?
}
```

### L'implémentation : `document/service/LocalFileStorageService.java`

```java
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type",
                       havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements IDocumentStorageService {
```

`@ConditionalOnProperty` signifie : « n'active cette classe que si
`app.storage.type = local` ». Et `matchIfMissing = true` : « en l'absence de
configuration, prends celle-ci par défaut ».

C'est ainsi qu'on bascule d'une implémentation à l'autre **sans toucher au
code** — une ligne dans la configuration suffit :

```properties
app.storage.type=local
app.storage.local-path=data/documents
```

Ces deux valeurs sont lues par `config/StorageProperties.java`, même mécanisme
que `CompanyProperties`.

### L'écriture

```java
public String store(byte[] content, String key) {
    Path target = resolve(key);
    try {
        Files.createDirectories(target.getParent());   // crée invoices/2026/ si besoin
        Files.write(target, content);
    } catch (IOException e) {
        throw new DocumentStorageException("Impossible d'écrire le fichier : " + key, e);
    }
    return key;
}
```

### La protection à connaître

```java
private Path resolve(String key) {
    Path base = Paths.get(properties.localPath()).toAbsolutePath().normalize();
    Path target = base.resolve(key).normalize();
    if (!target.startsWith(base)) {
        throw new DocumentStorageException("Clé de stockage invalide : " + key);
    }
    return target;
}
```

Si la clé contenait `../../etc/passwd`, on écrirait en dehors du dossier prévu.
C'est une faille classique, le *path traversal*. `normalize()` résout les `..`,
et on vérifie ensuite qu'on est resté à l'intérieur.

---

## Étape 8 — Retour au service métier

`generate()` renvoie la clé. De retour dans `InvoiceService.create()` :

```java
saved.setInvoicePathPDF(invoicePdfService.generate(saved));
return invoiceMapper.toDTO(invoiceRepository.save(saved));
```

La clé est enregistrée dans la colonne `invoicePathPDF`. C'est elle qui
permettra de retrouver le fichier plus tard.

**Le voyage est terminé.** Le client reçoit son `InvoiceDTO` en JSON, et le PDF
attend sur le disque.

### Et si quelque chose avait échoué ?

`PdfGenerationException` et `DocumentStorageException` sont des
`RuntimeException`. Elles **annulent toute la transaction**.

Conséquence assumée : **pas de PDF, pas de facture**. Une facture sans son
document serait un objet incomplet — mieux vaut échouer franchement.

C'est ce qu'on a observé lors du bug d'apostrophe : la génération a échoué, et
le devis est resté en `CREATED`. Rien à nettoyer.

---

## Étape 9 — Plus tard : le téléchargement

Trois jours après, l'utilisateur veut imprimer la facture.

```
GET /invoice/1/pdf
```

### Dans le contrôleur

```java
@GetMapping("/{id}/pdf")
public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
    Resource pdf = invoicePdfService.retrieve(id);
    String filename = pdf.getFilename() != null ? pdf.getFilename() : "facture.pdf";

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.inline().filename(filename).build().toString())
            .body(pdf);
}
```

> **`inline` ou `attachment`** : `inline` affiche le PDF dans un onglet du
> navigateur, avec son bouton d'impression. `attachment` forcerait le
> téléchargement. On a choisi `inline`, plus pratique pour imprimer.

### Dans le service

```java
@Override
@Transactional(readOnly = true)
public Resource retrieve(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));

    String key = invoice.getInvoicePathPDF();
    if (key == null || key.isBlank()) {
        throw new PdfGenerationException(
                "La facture " + invoice.getInvoiceNumber() + " n'a pas de PDF enregistré.");
    }

    return storageService.retrieve(key);
}
```

Trois temps : on lit la clé en base, on vérifie qu'elle existe, on demande le
fichier au stockage.

> **Aucune régénération.** Le mapper, le template, le moteur PDF ne sont **pas**
> appelés. C'est tout l'intérêt d'avoir généré immédiatement : le fichier renvoyé
> est exactement celui qu'a reçu le client.

---

# Le parcours d'un devis

Le trajet est le même. On ne refait donc que les différences.

## Ce qui est identique

- La structure en quatre collaborateurs (mapper, entreprise, moteur, stockage)
- `DocumentFormat`, `DocumentPdfLineDTO`, `DocumentPdfTvaDTO` — partagés
- `ICompanyService`, `IPdfGenerator`, `IDocumentStorageService` — les mêmes
- Les trois pièges du mapper — identiques
- Le principe du téléchargement

Les classes propres au devis sont les strictes jumelles de celles de la
facture :

| Facture | Devis |
|---|---|
| `InvoicePdfDTO` | `QuotePdfDTO` |
| `InvoicePdfMapper` | `QuotePdfMapper` |
| `IInvoicePdfService` | `IQuotePdfService` |
| `InvoicePdfService` | `QuotePdfService` |
| `invoice.html` | `quote.html` |

---

## Ce qui change

### 1. Le déclencheur

Pas une création, mais un **changement de statut** :

```java
// QuoteService.updateStatus()
if (qotStatus == QuoteStatus.PENDING && quote.getQotPathPDF() == null) {
    quote.setQotPathPDF(quotePdfService.generate(quote));
}
```

**Pourquoi `PENDING` et pas la création** : un devis en `CREATED` est un
brouillon, encore librement modifiable. Le figer n'aurait pas de sens. Dès
`PENDING`, le client en détient un exemplaire.

**Pourquoi tester `qotPathPDF == null`** : pour ne **jamais** refabriquer le
document. Un devis qui repasse en `PENDING` après un refus doit rester identique
à celui que le client a déjà entre les mains.

### 2. Un seul `save()`

Souvenez-vous des deux `save()` de la facture, à cause de `@CreatedDate`.

Ici, le problème n'existe pas : au moment de la transmission, `qotCreatedDate`
est renseigné depuis longtemps. Et `qotPathPDF` est **nullable** — pas de
`@NotBlank`, donc pas besoin de valeur temporaire.

Le champ passe directement de `null` à sa vraie valeur.

### 3. La chaîne vers le client est plus courte

```
Facture :  Invoice → Command → Quote → Cart → Customer
Devis   :  Quote → Cart → Customer
```

Un maillon de moins, même règle : si le client manque, on échoue franchement.

### 4. Les dates n'ont pas le même type

```java
private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
```

`qotCreatedDate` est un `LocalDateTime`, `qotExpirationDate` un `LocalDate`. Le
même formateur traite les deux — inutile d'en déclarer un second.

Et surtout : la date limite d'un devis est une **vraie donnée saisie**
(`qotExpirationDate`), pas une échéance calculée comme sur une facture (+30 j).

### 5. Le devis peut en remplacer un autre

```java
private String parentNumberOf(Quote quote) {
    return quote.getQotParent() != null ? quote.getQotParent().getQotNumber() : null;
}
```

Quand le client demande une modification, on ne retouche pas le devis déjà
transmis : on émet une **nouvelle version**, qui pointe vers la précédente.

Le template l'affiche, seulement si c'est le cas :

```html
<div class="revision-notice" th:if="${quote.replacesQuoteNumber != null}">
    Ce devis remplace et annule le devis
    <strong th:text="${quote.replacesQuoteNumber}">DEV-2026-0007</strong>.
</div>
```

Ça évite au client de se demander lequel des deux documents fait foi.

### 6. Les mentions légales

| Facture | Devis |
|---|---|
| Conditions de règlement, IBAN | Durée de validité |
| Pénalités de retard, indemnité 40 € | Cadre « bon pour accord » à signer |

### 7. Le message d'erreur du téléchargement

```java
throw new PdfGenerationException(
        "Le devis " + quote.getQotNumber()
                + " n'a pas encore de PDF : il n'a pas été transmis au client.");
```

Un devis en brouillon n'a pas de PDF — cas qui n'existe pas pour la facture. Le
message le dit explicitement plutôt que de renvoyer une erreur technique.

### Pourquoi deux mappers séparés plutôt qu'un seul

Ils se ressemblent beaucoup, c'est assumé. Mais un devis et une facture sont
deux documents **juridiquement différents**, qui évolueront séparément : les
mentions obligatoires ne sont pas les mêmes, et elles changent au gré de la loi.

Les fusionner créerait une classe pleine de `if (c'est une facture)`. Ce qu'ils
avaient réellement en commun — la mise en forme — a été extrait dans
`DocumentFormat`.

---

# Pour finir

## Ce qui traverse tout le parcours

### La transaction

```java
@Transactional          // sur InvoiceService.create()
@Transactional(readOnly = true)   // sur InvoicePdfService.generate()
```

Toute la génération se déroule **dans une seule transaction**. C'est ce qui
permet au mapper de parcourir les relations JPA, et c'est ce qui garantit que
tout est annulé en cas d'échec.

`readOnly = true` sur `generate()` : la méthode ne fait que lire. Quand elle est
appelée depuis `create()`, elle rejoint simplement la transaction existante.

### Les exceptions

Deux exceptions dédiées, dans le package commun `exception/` :

| Exception | Levée par |
|---|---|
| `PdfGenerationException` | Le mapper (client manquant), le moteur PDF |
| `DocumentStorageException` | Le stockage (écriture, lecture, clé invalide) |

Ce sont des `RuntimeException` : elles annulent la transaction, et sont
interceptées par `GlobalExceptionInterceptor` qui les traduit en réponse HTTP.

### Le rôle des interfaces

| Interface | Le point de bascule qu'elle prépare |
|---|---|
| `IDocumentStorageService` | Disque en dev → cloud en prod |
| `IPdfGenerator` | Changement de moteur de template |
| `ICompanyService` | Configuration → table en base |

**Mais attention** : une interface ne se justifie que s'il existe — ou existera
— plusieurs façons de faire.

Plusieurs abstractions ont été envisagées puis **écartées** :
`AbstractPdfService`, `PdfFactory`, `PdfStrategy`, une interface générique
`PdfService<T>`.

Pourquoi ? On a exactement **deux types de documents connus d'avance**. Ces
constructions n'auraient ajouté qu'une indirection, sans rien résoudre. C'est de
la sur-conception : résoudre un problème qu'on n'a pas.

> **Le bon réflexe** : on n'abstrait pas « au cas où ». On abstrait quand le
> deuxième cas apparaît vraiment — comme pour `DocumentFormat`.

---

## Tous les fichiers

### Configuration

```
config/StorageProperties.java     Où ranger les fichiers
config/CompanyProperties.java     Nos coordonnées
```

### Infrastructure réutilisable — `document/`

Ce package ne contient **que du générique**, commun au devis et à la facture.

```
Iservice/IDocumentStorageService.java          Contrat : ranger, relire
service/LocalFileStorageService.java           Implémentation disque
Iservice/IPdfGenerator.java                    Contrat : template → PDF
service/ThymeleafPdfGenerator.java             Implémentation Thymeleaf
Iservice/ICompanyService.java                  Contrat : nos coordonnées
service/PropertiesCompanyService.java          Implémentation par configuration
DocumentFormat.java                            Mise en forme partagée
DTO/DocumentPdfLineDTO.java                    Une ligne de tableau
DTO/DocumentPdfTvaDTO.java                     Une ligne de TVA
```

### Exceptions — `exception/`

```
DocumentStorageException.java     Le stockage a échoué
PdfGenerationException.java       La fabrication a échoué
```

### Spécifique aux documents — `cart/`

Le devis et la facture vivent déjà dans `cart/` : leurs classes PDF y restent.

```
DTO/InvoicePdfDTO.java              DTO/QuotePdfDTO.java
mapper/InvoicePdfMapper.java        mapper/QuotePdfMapper.java
Iservice/IInvoicePdfService.java    Iservice/IQuotePdfService.java
service/InvoicePdfService.java      service/QuotePdfService.java
```

### Templates

```
resources/templates/invoice.html
resources/templates/quote.html
```

---

## Ce qui reste à faire

**Le front n'est pas branché.** Le bouton « Imprimer / PDF » appelle encore
`window.print()`, qui imprime la page Angular — pas le PDF archivé. Les routes
`/quote/{id}/pdf` et `/invoice/{id}/pdf` existent, il n'y a plus qu'à les
appeler.

**Le bon de commande.** `Command` n'a pas de contenu propre, elle reprend les
lignes du devis. Un PDF a-t-il un sens, ou le devis signé suffit-il ? Question
ouverte.

**`InvoiceDTO` expose `invoicePathPDF`**, donc la clé de rangement interne du
serveur. Petite fuite d'information, sans utilité pour le front. `QuoteDTO`, lui,
ne l'expose pas — c'est le bon modèle à suivre.

**Le stockage cloud** n'est pas écrit. L'interface est prête, il ne manque que
l'implémentation S3.

---

## Les cinq idées à retenir

1. **Un PDF se fabrique une fois, à la validation métier.** Jamais à la demande,
   sinon le document change avec le temps.

2. **Quatre collaborateurs, appelés dans l'ordre** : calculer, récupérer les
   coordonnées, dessiner, ranger. Chacun ignore le travail des autres.

3. **Le template ne voit jamais une entité.** Un DTO est préparé pour lui, avec
   tout déjà calculé et mis en forme — parce qu'après le mapping, la transaction
   peut se fermer.

4. **Les interfaces servent aux points de bascule réels** — disque/cloud,
   config/base. Pas « au cas où ».

5. **On factorise après avoir constaté la duplication**, pas avant. C'est la
   différence entre factoriser et sur-concevoir.
