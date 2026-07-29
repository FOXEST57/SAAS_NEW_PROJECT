# Guide d'Installation - API Facturation SAAS

Bienvenue ! Ce guide vous explique comment installer et configurer l'API de facturation SAAS sur votre environnement local.

---

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java 25** ou supérieur
- **Maven 3.6+** (inclus via Maven wrapper)
- **Git**

### Vérifier les prérequis

```bash
# Vérifier la version de Java
java -version

# Vérifier Maven (devrait utiliser le wrapper inclus)
mvn --version
```

---

## 🚀 Installation Rapide

### 1. Cloner le repository

```bash
git clone <repository-url>
cd SAAS_NEW_PROJECT/back
```

### 2. Configurer la base de données

#### Option A : Utiliser la configuration par défaut (Recommandé pour débuter)

La configuration H2 par défaut fonctionnera immédiatement :

```bash
# Copier le fichier de configuration example (optionnel, déjà configuré)
cp src/main/resources/application-exemple.properties src/main/resources/application.properties
```

**Fichier `application.properties` (déjà prêt) :**
```properties
spring.application.name=saas_facturation

# Configuration Base de données H2
spring.datasource.url=jdbc:h2:file:./data/demo;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Configuration Hibernate
spring.jpa.defer-datasource-initialization=true
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.sql.init.mode=always

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2
```

#### Option B : Utiliser MariaDB/MySQL (Pour la production)

**Modifier `application.properties` :**

```properties
spring.application.name=saas_facturation

# Configuration Base de données MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/saas_facturation
spring.datasource.driverClassName=org.mariadb.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe

# Configuration Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.defer-datasource-initialization=true
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.sql.init.mode=always

# Console H2 (optionnel)
spring.h2.console.enabled=false
```

**Créer la base de données MariaDB :**

```sql
CREATE DATABASE saas_facturation;
USE saas_facturation;
```

---

## 🔧 Étapes de Configuration Détaillées

### Étape 1 : Cloner le projet

```bash
git clone <repository-url>
cd SAAS_NEW_PROJECT/back
```

### Étape 2 : Vérifier la structure du projet

```
back/
├── src/
│   ├── main/
│   │   ├── java/com/mns/cda/...
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-exemple.properties
│   └── test/
├── data/
│   └── demo.mv.db (base de données H2)
├── pom.xml
├── mvnw (Maven wrapper - Windows)
└── mvnw.cmd (Maven wrapper - Linux/Mac)
```

### Étape 3 : Nettoyer et compiler

```bash
# Nettoyer les builds précédents
./mvnw clean

# Compiler le projet
./mvnw compile

# ou complet (clean + compile + test + package)
./mvnw clean package
```

### Étape 4 : (Optionnel) Télécharger les dépendances

```bash
./mvnw dependency:download-sources
./mvnw dependency:download-javadocs
```

---

## ▶️ Lancer l'API

### Démarrer l'application

```bash
./mvnw spring-boot:run
```

ou directement avec Java :

```bash
./mvnw clean package
java -jar target/saas_facturation-0.0.1-SNAPSHOT.jar
```

### Logs de démarrage réussi

```
Started SaasFacturationApplication in X.XXX seconds (process running for X.XXX)
```

---

## 🌐 Accéder aux Services

### API Principal

**URL :** http://localhost:8080

### Documentation Swagger/OpenAPI

**URL :** http://localhost:8080/swagger-ui.html

### Console H2 (uniquement avec H2 activé)

**URL :** http://localhost:8080/h2

- **URL JDBC :** `jdbc:h2:file:./data/demo`
- **Login :** `sa`
- **Mot de passe :** `password`

---

## 📚 Guide Détaillé - Swagger/OpenAPI

### Qu'est-ce que Swagger ?

Swagger est une interface interactive pour tester et explorer votre API REST. Elle génère automatiquement la documentation à partir de votre code Java.

### Accéder à Swagger UI

1. **Démarrer l'application :**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Ouvrir votre navigateur** et accéder à :
   ```
   http://localhost:8080/swagger-ui.html
   ```

### À quoi ressemble Swagger ?

La page Swagger affiche :

```
┌─────────────────────────────────────────────┐
│ Saas Facturation - OpenAPI 3.0.0            │
│                                             │
│ 📌 Base URL: http://localhost:8080          │
│                                             │
│ ▼ Controllers                               │
│   ├─ User Controller                        │
│   │  ├─ GET    /api/users              │
│   │  ├─ POST   /api/users              │
│   │  ├─ GET    /api/users/{id}         │
│   │  ├─ PUT    /api/users/{id}         │
│   │  └─ DELETE /api/users/{id}         │
│   │                                    │
│   └─ Other Controllers...              │
│                                        │
│ Version: 0.0.1-SNAPSHOT                │
└────────────────────────────────────────┘
```

### Comment utiliser Swagger

#### 1️⃣ Explorez les endpoints disponibles

- Cliquez sur **chaque endpoint** pour voir les détails
- Vous verrez :
  - **Description** du endpoint
  - **Paramètres** requis/optionnels
  - **Réponse** attendue (format JSON)
  - **Codes HTTP** possibles

#### 2️⃣ Testez un endpoint

**Exemple : Créer un utilisateur avec POST**

```
1. Cliquez sur "POST /api/users" ➜ "Try it out"
2. Entrez le JSON dans le champ Request body:
   {
     "name": "John Doe",
     "email": "john@example.com",
     "phone": "+33612345678"
   }
3. Cliquez sur "Execute"
4. Consultez la réponse (Response)
```

#### 3️⃣ Consultez les réponses

Chaque réponse affiche :
- **Code HTTP** (200, 201, 400, 404, 500...)
- **Body** (réponse JSON)
- **Headers** (informations supplémentaires)

### Formats de Réponse

**Exemple de réponse réussie (200 OK) :**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+33612345678",
  "createdAt": "2026-07-29T08:00:00"
}
```

**Exemple d'erreur (400 Bad Request) :**
```json
{
  "timestamp": "2026-07-29T08:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email invalide",
  "path": "/api/users"
}
```

### URL Complète des Ressources Swagger

| Ressource | URL |
|-----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **API JSON** | http://localhost:8080/v3/api-docs |
| **API YAML** | http://localhost:8080/v3/api-docs.yaml |
| **Swagger UI HTML** | http://localhost:8080/swagger-ui/ |

### Conseils d'Utilisation

✅ **Consultez Swagger régulièrement** pour comprendre l'API  
✅ **Testez les endpoints** avant d'utiliser une vraie application client  
✅ **Lisez les descriptions** de chaque paramètre  
✅ **Vérifiez les codes d'erreur** possibles  

### Désactiver Swagger (optionnel)

Si vous ne voulez pas exposer Swagger en production, modifiez `application.properties` :

```properties
# Désactiver Swagger
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

---

## 🗄️ Guide Détaillé - Console H2

### Qu'est-ce que H2 ?

H2 est une **base de données SQL embarquée** (incluse dans l'application). Elle stocke les données dans un fichier local (pas besoin d'installer MySQL/MariaDB).

### Accéder à la Console H2

1. **Assurez-vous que l'application est démarrée :**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Ouvrir votre navigateur** et accéder à :
   ```
   http://localhost:8080/h2
   ```

### Écran de Connexion H2

```
┌──────────────────────────────────────────┐
│ H2 Console - Login                       │
│                                          │
│ Saved Settings:     [h2-demo]  ▼         │
│ Setting Name:       [h2-demo]            │
│                                          │
│ Driver:             org.h2.Driver        │
│ JDBC URL:           jdbc:h2:file:        │
│                     ./data/demo          │
│ User Name:          sa                   │
│ Password:           [••••••••]            │
│                                          │
│                          ┌──────┬─────┐  │
│                          │Test  │Login│  │
│                          └──────┴─────┘  │
└──────────────────────────────────────────┘
```

### Paramètres de Connexion

Copier-coller exactement ces paramètres :

| Champ | Valeur |
|-------|--------|
| **Driver Class** | `org.h2.Driver` |
| **JDBC URL** | `jdbc:h2:file:./data/demo` |
| **User Name** | `sa` |
| **Password** | `password` |

### Étapes de Connexion

1. **Remplissez les 4 champs** avec les valeurs ci-dessus
2. **Cliquez sur "Test Connection"** pour vérifier
3. **Cliquez sur "Login"** pour accéder à la console

### Interface de la Console H2

```
┌─────────────────────────────────────────────┐
│ H2 Console                                  │
│                                             │
│ ┌──────────────┐  ┌───────────────────┐   │
│ │ Database     │  │ SQL Query Editor  │   │
│ │ Tree:        │  │                   │   │
│ │              │  │ SELECT * FROM     │   │
│ │ ▼ H2         │  │ users;            │   │
│ │  ├─ PUBLIC   │  │                   │   │
│ │  │ ├─ USERS  │  │ ┌─────────────┐   │   │
│ │  │ ├─ ORDERS │  │ │   Run SQL   │   │   │
│ │  │ └─ ...    │  │ └─────────────┘   │   │
│ │  └─ ...      │  │                   │   │
│ └──────────────┘  │ Results:          │   │
│                   │ ID | NAME | EMAIL │   │
│                   │ 1  | John | j@... │   │
│                   │ 2  | Jane | j@... │   │
│                   └───────────────────┘   │
└─────────────────────────────────────────────┘
```

### Exemples de Requêtes SQL

#### 📌 Voir tous les utilisateurs

```sql
SELECT * FROM users;
```

#### 📌 Voir la structure d'une table

```sql
SELECT * FROM information_schema.columns 
WHERE table_name = 'USERS';
```

#### 📌 Compter les enregistrements

```sql
SELECT COUNT(*) as total FROM users;
```

#### 📌 Chercher un enregistrement spécifique

```sql
SELECT * FROM users WHERE email = 'john@example.com';
```

#### 📌 Voir toutes les tables disponibles

```sql
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'PUBLIC';
```

#### 📌 Insérer des données de test

```sql
INSERT INTO users (name, email, phone, created_at) 
VALUES ('Alice Smith', 'alice@example.com', '+33698765432', NOW());
```

### Édition Directe dans H2

✅ Vous pouvez **modifier**, **insérer** et **supprimer** des données directement  
⚠️ **Attention** : Ces modifications sont immédiates et permanentes

```sql
-- Modifier un enregistrement
UPDATE users SET email = 'newemail@example.com' 
WHERE id = 1;

-- Supprimer un enregistrement
DELETE FROM users WHERE id = 1;
```

### Exporter/Importer des Données

#### Exporter en CSV

1. Écrire une requête :
   ```sql
   SELECT * FROM users;
   ```

2. Cliquer sur **"Export"** → **"CSV"**
3. Télécharger le fichier

#### Importer depuis CSV

```sql
INSERT INTO users (name, email, phone) 
SELECT name, email, phone 
FROM CSVREAD('C:\path\to\file.csv');
```

### URL Complète pour H2

| Service | URL |
|---------|-----|
| **Console H2** | http://localhost:8080/h2 |
| **Accès direct** | http://localhost:8080/h2/ |

### Localisation du Fichier Données

La base H2 est stockée ici :

```
back/data/demo.mv.db
```

**⚠️ Important :**
- Ne pas supprimer ce fichier (perte de données)
- C'est là que sont stockées toutes vos données
- La structure du dossier doit être maintenue

### Vérifier l'État de H2

#### 1. Vérifier que H2 est activé

Dans `application.properties`, vérifiez :
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2
```

#### 2. Vérifier la connexion

```bash
# Dans un terminal, vérifiez que l'app est running:
curl http://localhost:8080/h2
```

### Désactiver H2 (production)

Pour la production, désactiver la console H2 dans `application.properties` :

```properties
spring.h2.console.enabled=false
```

### Conseils d'Utilisation

✅ Utilisez H2 pour **déboguer les données** pendant le développement  
✅ Testez vos **requêtes SQL** avant de les utiliser en code  
✅ Vérifiez les **migrations de schéma** après un redémarrage  
✅ Gardez une **sauvegarde** du fichier `demo.mv.db` en cas de besoin  
⚠️ Ne pas exposer H2 Console en production (risque sécurité)

---

## 📁 Dossiers Importants

| Dossier | Description |
|---------|-------------|
| `src/main/java/com/mns/cda/` | Code source de l'API |
| `src/main/resources/` | Fichiers de configuration et ressources |
| `src/test/` | Tests unitaires et d'intégration |
| `data/` | Données de la base de données H2 |
| `target/` | Fichiers compilés et build |

---

## 🔍 Vérification de l'Installation

### 1. Vérifier que l'application démarre

```bash
./mvnw spring-boot:run
```

### 2. Tester l'API (dans un autre terminal)

```bash
# Test simple
curl http://localhost:8080/actuator/health

# Résultat attendu:
# {"status":"UP"}
```

### 3. Vérifier la base de données

Accédez à http://localhost:8080/h2 et vérifiez que vous pouvez vous connecter.

---

## 🔗 Dépendances Principales

| Dépendance | Version | Rôle |
|------------|---------|------|
| Spring Boot | 3.5.15 | Framework principal |
| Spring Data JPA | - | Gestion de la persistance |
| Hibernate | - | ORM (inclus avec JPA) |
| H2 Database | - | Base de données embarquée |
| MariaDB/MySQL | 8.13.55 | Base de données optionnelle |
| Lombok | - | Annotations pour réduire le code |
| SpringDoc OpenAPI | 2.8.16 | Génération documentation Swagger |
| libphonenumber | 8.13.55 | Validation numéros téléphone |

Voir `pom.xml` pour la liste complète.

---

## 🛠️ Commandes Maven Utiles

```bash
# Nettoyer les builds précédents
./mvnw clean

# Compiler
./mvnw compile

# Tester
./mvnw test

# Builder sans tests
./mvnw clean package -DskipTests

# Builder avec tests
./mvnw clean package

# Lancer directement
./mvnw spring-boot:run

# Afficher les dépendances
./mvnw dependency:tree

# Vérifier les mises à jour disponibles
./mvnw versions:display-dependency-updates
```

---

## 🐛 Troubleshooting

### Erreur : "Port 8080 already in use"

Le port 8080 est déjà utilisé. Solutions :

```bash
# Option 1 : Utiliser un port différent
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Option 2 : Tuer le processus utilisant le port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :8080
kill -9 <PID>
```

### Erreur : "Could not find or load main class"

```bash
# Recompiler complètement
./mvnw clean compile
./mvnw clean package
```

### Erreur de connexion à la base de données (MariaDB)

```bash
# Vérifier que MariaDB est en cours d'exécution
# Vérifier les identifiants dans application.properties
# Vérifier que la base de données existe

# Créer la base si nécessaire:
mysql -u root -p
CREATE DATABASE saas_facturation;
```

### Erreur avec le fichier `application.properties`

```bash
# Vérifier que le fichier existe
ls -la src/main/resources/application.properties

# Si manquant, copier le fichier exemple:
cp src/main/resources/application-exemple.properties src/main/resources/application.properties
```

---

## 🔐 Configuration de Sécurité

### Points à sécuriser en production

1. **Changer les identifiants par défaut :**
   - Username : remplacer `sa`
   - Password : remplacer `password`

2. **Désactiver la console H2 :**
   ```properties
   spring.h2.console.enabled=false
   ```

3. **Utiliser une base de données sécurisée :**
   - Utiliser MariaDB/MySQL au lieu de H2
   - Configurer les permissions correctement

4. **Configurer CORS/HTTPS :**
   - Ajouter des certificats SSL
   - Configurer les origins autorisées

---

## 📝 Notes Importantes

- ✅ La base de données H2 est **embarquée** - aucune installation externe requise
- ✅ Tous les fichiers de configuration sont dans `src/main/resources/`
- ✅ Les migrations de schéma se font automatiquement via Hibernate (`ddl-auto=create`)
- ⚠️ La base H2 crée un fichier `demo.mv.db` - ne pas supprimer ce dossier `data/`
- ⚠️ En production, utiliser MariaDB au lieu de H2

---

## 📞 Support

Pour toute question ou problème :

1. Vérifier les logs en console
2. Consulter la [documentation Spring Boot](https://spring.io/projects/spring-boot)
3. Consulter la [documentation Hibernate](https://hibernate.org/)

---

## 📄 Version

- **Application :** v0.0.1-SNAPSHOT
- **Spring Boot :** 3.5.15
- **Java :** 25+
- **Date :** 2026

---

**Bon développement ! 🎉**
