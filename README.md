# FastRepair - Système de Gestion de Réparation

## 🚀 Démarrage Rapide

### Prérequis
- **Java 8+** installé
- **MySQL 5.7+** installé et démarré

### Lancement Automatique
```bash
# Windows
run_tests.bat

# Linux/Mac
./run_tests.sh
```

### Ce que fait le script automatique
1. ✅ **Compilation** du projet Maven
2. ✅ **Création** de la base de données MySQL
3. ✅ **Création** des tables selon l'architecture JPA
4. ✅ **Initialisation** des données de test
5. ✅ **Exécution** de tous les tests CRUD

## 🏗️ Architecture

### Héritage JPA
- **Classe abstraite `Personne`** : champs communs (`id`, `nom`, `prenom`)
- **Stratégie JOINED** : tables séparées avec clés étrangères
- **Héritiers** : `Client`, `Reparateur`, `Proprietaire`

### Configuration JNDI
- **DataSource** configurée dans `META-INF/context.xml`
- **Connexion automatique** via EntityManager
- **Fallback** vers connexion directe si nécessaire

## 📊 Entités et Fonctionnalités

| Entité | CRUD | Filtrages |
|--------|------|-----------|
| Client | ✅ | nom, prénom, adresse, téléphone |
| Reparateur | ✅ | nom, prénom, email, pourcentage |
| Proprietaire | ✅ | nom, prénom, email |
| Appareil | ✅ | IMEI, marque, modèle, type |
| Reparation | ✅ | code, date, état, commentaire, prix |
| Boutique | ✅ | nom, adresse, téléphone, numéro P |
| Caisse | ✅ | solde, dernier mouvement |
| Composant | ✅ | nom, prix, quantité |
| Emprunt | ✅ | date, montant, type, commentaire |
| Recu | ✅ | date, montant |

## ⚙️ Configuration

### Base de Données
Modifiez `src/main/resources/META-INF/context.xml` :
```xml
username="votre_user"
password="votre_password"
url="jdbc:mysql://localhost:3306/fast_repair"
```

### Tests Individuels
```bash
# Test connexion uniquement
mvn exec:java -Dexec.mainClass="metier.DatabaseConnection"

# Initialisation données seulement
mvn exec:java -Dexec.mainClass="metier.TestDataInitializer"

# Tests CRUD seulement
mvn exec:java -Dexec.mainClass="metier.TestCRUD"
```

### Utilisation des Classes de Gestion

Toutes les classes de gestion utilisent maintenant EntityManager et EntityTransaction :

```java
// Exemple d'utilisation
GestionClient gestionClient = new GestionClient();

try {
    Client client = Client.builder()
        .nom("Dupont")
        .prenom("Jean")
        .adresse("123 Rue de la Paix")
        .telephone(123456789)
        .build();

    gestionClient.ajouter(client);

    // Recherche par ID hérité de Personne
    Client found = gestionClient.rechercher(client.getId());

    // Filtrage par attributs
    List<Client> clientsParNom = gestionClient.filtrerParNom("Dupont");

} catch (DuplicateEntityException | DatabaseException e) {
    // Gestion des erreurs
} finally {
    gestionClient.close();
}
```

### Avantages de cette Architecture

1. **Réutilisabilité** : Les champs communs (`id`, `nom`, `prenom`) sont définis une seule fois
2. **Maintenabilité** : Les modifications sur les champs communs se font au niveau de la classe parente
3. **Polymorphisme** : Possibilité de traiter les objets comme des `Personne`
4. **Intégrité référentielle** : Les contraintes de clés étrangères assurent la cohérence des données
5. **Performance** : La stratégie JOINED permet des requêtes efficaces sur les champs communs

### Exceptions Personnalisées

Le système utilise des exceptions personnalisées pour une meilleure gestion des erreurs :
- `EntityNotFoundException` : Entité non trouvée
- `DuplicateEntityException` : Tentative d'ajout d'entité existante
- `InvalidParameterException` : Paramètres invalides
- `DatabaseException` : Erreurs de base de données

### Configuration de la Base de Données

#### Prérequis
- **MySQL 5.7+** installé et démarré
- **Java 8+** avec support JNDI (disponible par défaut)

#### Configuration Automatique
La configuration utilise le fichier `META-INF/context.xml` pour définir la DataSource JNDI.

**Pour modifier la configuration de la base de données :**
1. Ouvrez `src/main/resources/META-INF/context.xml`
2. Modifiez les paramètres de connexion :
   - `username` : nom d'utilisateur MySQL
   - `password` : mot de passe MySQL
   - `url` : URL de connexion MySQL

#### Création Automatique
La base de données et les tables sont créées automatiquement lors du premier lancement.

### Compilation et Exécution

```bash
# Compiler le projet
mvn clean compile

# Tester la connexion à la base de données
mvn exec:java -Dexec.mainClass="metier.DatabaseConnection"

# Exécuter tous les tests CRUD
mvn exec:java -Dexec.mainClass="metier.TestCRUD"

# Scripts automatiques
# Windows:
run_tests.bat

# Linux/Mac:
chmod +x run_tests.sh
./run_tests.sh

# Créer le package
mvn package
```

### Tests Disponibles

#### Test de Connexion (`DatabaseConnection`)
- Tentative de connexion via JNDI (context.xml)
- Fallback vers connexion directe si nécessaire
- Création automatique de la base de données et des tables

#### Initialisation des Données (`TestDataInitializer`)
- Création d'exemples de données pour les tests
- Population automatique des tables avec des données fictives

#### Tests CRUD Complets (`TestCRUD`)
Tests exhaustifs de toutes les opérations pour chaque entité :
- **Client** : CREATE, READ, UPDATE, DELETE + 4 méthodes de filtrage
- **Reparateur** : CREATE, READ, UPDATE, DELETE + 5 méthodes de filtrage
- **Proprietaire** : CREATE, READ, UPDATE, DELETE + 4 méthodes de filtrage
- **Appareil** : CREATE, READ, UPDATE, DELETE + 4 méthodes de filtrage
- **Reparation** : CREATE, READ, UPDATE, DELETE + 5 méthodes de filtrage
- **Boutique** : CREATE, READ, UPDATE, DELETE + 4 méthodes de filtrage
- **Caisse** : CREATE, READ, UPDATE, DELETE + 2 méthodes de filtrage
- **Composant** : CREATE, READ, UPDATE, DELETE + 3 méthodes de filtrage
- **Emprunt** : CREATE, READ, UPDATE, DELETE + 4 méthodes de filtrage
- **Recu** : CREATE, READ, UPDATE, DELETE + 2 méthodes de filtrage

### Configuration Requise

- Java 8+
- MySQL 5.7+
- Maven 3.6+
- Hibernate 5.4+
- Lombok (pour la génération de code)
