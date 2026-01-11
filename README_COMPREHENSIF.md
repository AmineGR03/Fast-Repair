# 🚀 Fast-Repair - Système de Gestion Complet

Un système de gestion avancé pour les boutiques de réparation d'appareils électroniques, développé en Java avec une architecture moderne utilisant JPA, Hibernate, Lombok, Maven et Swing.

## 📋 Table des Matières

- [Vue d'ensemble](#vue-densemble)
- [Technologies utilisées](#technologies-utilisées)
- [Architecture du projet](#architecture-du-projet)
- [Modèle de données JPA](#modèle-de-données-jpa)
- [Gestion des entités avec Lombok](#gestion-des-entités-avec-lombok)
- [Opérations CRUD](#opérations-crud)
- [Interface utilisateur Swing](#interface-utilisateur-swing)
- [Gestion des transactions](#gestion-des-transactions)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Configuration](#configuration)
- [Installation et exécution](#installation-et-exécution)

## 🎯 Vue d'ensemble

Fast-Repair est une application complète de gestion pour les boutiques de réparation d'appareils électroniques. Elle offre une interface graphique moderne et permet la gestion complète des clients, réparations, appareils, composants, et finances de la boutique.

### Fonctionnalités principales :
- ✅ Gestion des clients et appareils
- ✅ Suivi des réparations en temps réel
- ✅ Gestion des stocks de composants
- ✅ Gestion financière (caisse, reçus, emprunts)
- ✅ Authentification multi-rôles (Admin, Réparateur, Propriétaire)
- ✅ Interface utilisateur intuitive avec navigation fluide

## 🛠️ Technologies utilisées

### **Java Persistence API (JPA) & Hibernate**
- **EntityManager** : Gestion centralisée des entités persistantes
- **EntityTransaction** : Gestion des transactions ACID
- **JPQL** : Requêtes orientées objet pour les recherches avancées
- **Stratégie d'héritage JOINED** : Optimisation des performances et intégrité référentielle

### **Lombok**
- **@Data** : Génération automatique de getters/setters, toString, equals, hashCode
- **@Builder** : Pattern Builder pour la construction d'objets complexes
- **@SuperBuilder** : Support de l'héritage dans les builders
- **@AllArgsConstructor/@NoArgsConstructor** : Constructeurs automatiques
- **@Entity** : Annotations JPA pour le mapping objet-relationnel

### **Apache Maven**
- **Gestion des dépendances** : Hibernate Core, JPA API, MySQL Connector, Lombok
- **Build automatisé** : Compilation, packaging, exécution
- **Plugins** : Compiler, Exec, Surefire pour les tests

### **Swing (Java GUI)**
- **CardLayout** : Navigation fluide entre les interfaces
- **JFrame/JPanel** : Structure de l'interface utilisateur
- **Event-Driven Programming** : Gestion des actions utilisateur
- **MVC Pattern** : Séparation présentation/métier

### **MySQL Database**
- **InnoDB Engine** : Support des transactions et clés étrangères
- **Auto-incrémentation** : Génération automatique des IDs
- **UTF-8 Encoding** : Support international

## 🏗️ Architecture du projet

```
src/main/java/
├── dao/                    # Couche d'accès aux données (Entités JPA)
│   ├── Personne.java      # Classe abstraite avec héritage
│   ├── Client.java        # Hérite de Personne
│   ├── Reparateur.java    # Hérite de Personne
│   ├── Proprietaire.java  # Hérite de Personne
│   ├── Appareil.java      # Entité indépendante
│   ├── Reparation.java    # Entité métier
│   └── ...               # Autres entités
├── metier/               # Couche métier (Logique d'affaires)
│   ├── IGestion*.java    # Interfaces de gestion
│   ├── Gestion*.java     # Implémentations avec EntityManager
│   └── TestCRUD.java     # Tests des opérations
├── presentation/         # Couche présentation (Swing)
│   ├── MainWindow.java   # Fenêtre principale avec CardLayout
│   ├── AuthentificationPanel.java
│   ├── AdminPanel.java
│   ├── ReparateurPanel.java
│   ├── ProprietairePanel.java
│   └── SuiviReparationPanel.java
└── exception/            # Gestion d'erreurs personnalisée
    ├── DatabaseException.java
    ├── EntityNotFoundException.java
    ├── DuplicateEntityException.java
    └── InvalidParameterException.java
```

## 📊 Modèle de données JPA

### Héritage avec stratégie JOINED

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
public abstract class Personne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    protected String nom;
    protected String prenom;
}

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Client extends Personne {
    private String adresse;
    private int telephone;
}
```

**Avantages de cette architecture :**
- 🔄 **Réutilisabilité** : Champs communs définis une seule fois
- 🛠️ **Maintenabilité** : Modifications centralisées
- 🎯 **Polymorphisme** : Traitement générique des personnes
- 🔗 **Intégrité** : Contraintes de clés étrangères automatiques
- ⚡ **Performance** : Requêtes optimisées sur les champs communs

### Entités principales

| Entité | Description | Champs clés |
|--------|-------------|-------------|
| **Personne** | Classe abstraite | id, nom, prenom |
| **Client** | Hérite de Personne | + adresse, telephone |
| **Reparateur** | Hérite de Personne | + email, pourcentage |
| **Proprietaire** | Hérite de Personne | + email |
| **Appareil** | Appareil à réparer | idAppareil, imei, marque, modele |
| **Reparation** | Réparation en cours | code, date, etat, prix |
| **Boutique** | Information boutique | nom, adresse, telephone |
| **Composant** | Pièces détachées | nom, prix, quantite |
| **Caisse** | Gestion financière | solde, mouvements |
| **Emprunt** | Prêts/Emprunts | date, montant, type |

## 🔧 Gestion des entités avec Lombok

### Pattern Builder automatique

```java
// Grâce à @SuperBuilder et @Data, création simplifiée :
Client client = Client.builder()
    .nom("Dupont")
    .prenom("Jean")
    .adresse("123 Rue de la Paix")
    .telephone(123456789)
    .build();

// Getters/setters générés automatiquement :
String nom = client.getNom();
client.setAdresse("Nouvelle adresse");

// Méthodes utilitaires incluses :
System.out.println(client); // toString() automatique
Client clone = client.toBuilder().build(); // Clone builder
```

### Avantages de Lombok :
- 📝 **Code réduit** : -70% de code boilerplate
- 🐛 **Moins d'erreurs** : Génération automatique et cohérente
- 🔄 **Maintenance** : Modifications automatiques lors de changements
- 🎯 **Lisibilité** : Focus sur la logique métier

## 🔄 Opérations CRUD

### Architecture des classes de gestion

Chaque entité possède une interface et une implémentation :

```java
public interface IGestionClient {
    void ajouter(Client client) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
    void modifier(Client client) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
    void supprimer(int id) throws EntityNotFoundException, DatabaseException;
    Client rechercher(int id) throws DatabaseException;
    List<Client> lister() throws DatabaseException;
    // Méthodes de filtrage spécifiques...
}
```

### Implémentation avec EntityManager

```java
public class GestionClient implements IGestionClient {
    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionClient() {
        this.emf = Persistence.createEntityManagerFactory("FastRepairPU");
        this.em = emf.createEntityManager();
    }

    @Override
    public void ajouter(Client client) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Vérifications métier
            if (client == null) {
                throw new InvalidParameterException("Le client ne peut pas être null");
            }

            // Vérification d'unicité
            Client existingClient = em.find(Client.class, client.getId());
            if (existingClient != null) {
                throw new DuplicateEntityException("Client existe déjà");
            }

            em.persist(client);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de l'ajout", e);
        }
    }
}
```

### Requêtes JPQL avancées

```java
// Filtrage avec LIKE pour recherche partielle
@Query("SELECT c FROM Client c WHERE LOWER(c.nom) LIKE LOWER(:nom)")
List<Client> filtrerParNom(@Param("nom") String nom);

// Jointures implicites grâce à l'héritage
@Query("SELECT r FROM Reparation r JOIN r.appareil a WHERE a.marque = :marque")
List<Reparation> findByMarqueAppareil(@Param("marque") String marque);
```

## 🖥️ Interface utilisateur Swing

### Architecture CardLayout

```java
public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainWindow() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Ajout des panels
        contentPanel.add(new AuthentificationPanel(this), "AUTH");
        contentPanel.add(new AdminPanel(this), "ADMIN");
        contentPanel.add(new ReparateurPanel(this), "REPARATEUR");
        contentPanel.add(new ProprietairePanel(this), "PROPRIETAIRE");
        contentPanel.add(new SuiviReparationPanel(this), "SUIVI");
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
}
```

### Gestion des rôles utilisateur

```java
public void setCurrentUser(String role, String email) {
    this.currentUserRole = role;
    this.currentUserEmail = email;

    switch (role.toUpperCase()) {
        case "ADMIN":
            showPanel("ADMIN");
            break;
        case "REPARATEUR":
            showPanel("REPARATEUR");
            reparateurPanel.onUserLoggedIn();
            break;
        case "PROPRIETAIRE":
            showPanel("PROPRIETAIRE");
            proprietairePanel.onUserLoggedIn();
            break;
    }
}
```

### Navigation fluide

- 🔐 **Authentification** : Connexion avec vérification des rôles
- 👨‍💼 **Admin** : Gestion complète du système
- 🔧 **Réparateur** : Gestion des réparations et composants
- 🏢 **Propriétaire** : Vue d'ensemble et statistiques
- 📊 **Suivi** : Consultation du statut des réparations (public)

## 🔐 Gestion des transactions

### EntityTransaction - ACID complet

```java
EntityTransaction tx = null;
try {
    tx = em.getTransaction();
    tx.begin();

    // Opérations métier
    em.persist(entity);
    em.merge(updatedEntity);
    em.remove(entityToDelete);

    tx.commit(); // Validation atomique

} catch (Exception e) {
    if (tx != null && tx.isActive()) {
        tx.rollback(); // Annulation complète en cas d'erreur
    }
    throw new DatabaseException("Transaction échouée", e);
}
```

### Avantages :
- 🔒 **Atomicité** : Tout ou rien
- 📖 **Cohérence** : État cohérent après transaction
- 🛡️ **Isolation** : Transactions indépendantes
- 💾 **Durabilité** : Changements persistés

## ⚠️ Gestion des erreurs

### Exceptions personnalisées

```java
// Hiérarchie d'exceptions
public class DatabaseException extends Exception {
    // Erreurs de base de données générales
}

public class EntityNotFoundException extends Exception {
    // Entité non trouvée en base
}

public class DuplicateEntityException extends Exception {
    // Tentative d'ajout d'entité existante
}

public class InvalidParameterException extends Exception {
    // Paramètres invalides ou null
}
```

### Gestion centralisée

```java
try {
    gestionClient.ajouter(client);
} catch (DuplicateEntityException e) {
    JOptionPane.showMessageDialog(this, "Client déjà existant !");
} catch (InvalidParameterException e) {
    JOptionPane.showMessageDialog(this, "Données invalides !");
} catch (DatabaseException e) {
    JOptionPane.showMessageDialog(this, "Erreur base de données !");
}
```

## ⚙️ Configuration

### persistence.xml - Configuration JPA

```xml
<persistence-unit name="FastRepairPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

    <!-- Mapping des entités -->
    <class>dao.Personne</class>
    <class>dao.Client</class>
    <!-- ... autres entités ... -->

    <properties>
        <!-- Configuration MySQL -->
        <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/fast_repair"/>
        <property name="javax.persistence.jdbc.user" value="root"/>
        <property name="javax.persistence.jdbc.password" value=""/>

        <!-- Configuration Hibernate -->
        <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
        <property name="hibernate.show_sql" value="true"/>
    </properties>
</persistence-unit>
```

### pom.xml - Dépendances Maven

```xml
<dependencies>
    <!-- Hibernate ORM -->
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.6.15.Final</version>
    </dependency>

    <!-- JPA API -->
    <dependency>
        <groupId>javax.persistence</groupId>
        <artifactId>javax.persistence-api</artifactId>
        <version>2.2</version>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.28</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.42</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 🚀 Installation et exécution

### Prérequis
- ☕ **Java 8+** installé
- 🐬 **MySQL 5.7+** installé et démarré
- 📦 **Maven 3.6+** installé

### Lancement automatique
```bash
# Windows
run_tests.bat

# Linux/Mac
chmod +x run_tests.sh
./run_tests.sh
```

### Lancement manuel
```bash
# Compilation
mvn clean compile

# Exécution de l'interface graphique
mvn exec:java -Dexec.mainClass="presentation.MainWindow"

# Tests CRUD complets
mvn exec:java -Dexec.mainClass="metier.TestCRUD"
```

### Création du package
```bash
mvn package
```

## 🎯 Points forts de l'architecture

1. **🧱 Modulaire** : Séparation claire des responsabilités (DAO/Métier/UI)
2. **🔄 Réutilisable** : Héritage JPA et interfaces génériques
3. **🛡️ Robuste** : Gestion d'erreurs complète et transactions ACID
4. **🎨 Moderne** : Utilisation des dernières pratiques Java
5. **⚡ Performant** : Requêtes optimisées et cache EntityManager
6. **🔧 Maintenable** : Code généré par Lombok, configuration centralisée
7. **🎯 Extensible** : Pattern Builder, interfaces, architecture en couches

## 📈 Métriques du projet

- **10 entités JPA** avec héritage
- **21 classes de gestion** (interfaces + implémentations)
- **5 panels Swing** avec navigation CardLayout
- **4 types d'exceptions** personnalisées
- **50+ méthodes CRUD** avec filtrage avancé
- **Configuration automatique** base de données
- **Tests complets** pour toutes les fonctionnalités

---

**Fast-Repair** démontre une maîtrise des technologies Java modernes pour créer une application d'entreprise robuste, maintenable et évolutive.
