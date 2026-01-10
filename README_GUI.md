# Fast-Repair - Interface Graphique Utilisateur

## Vue d'ensemble

Fast-Repair est une application de gestion de réparation d'appareils électroniques avec une interface graphique complète permettant différents niveaux d'accès selon les rôles utilisateurs.

## Architecture

### Technologies utilisées
- **Java Swing** pour l'interface graphique
- **JPA/Hibernate** pour la persistance des données
- **MySQL** comme base de données
- **Pattern Singleton** pour la gestion de la connexion BD

### Structure des fichiers
```
src/main/java/
├── presentation/           # Interface graphique
│   ├── MainWindow.java         # Fenêtre principale
│   ├── AuthentificationPanel.java  # Panel d'authentification
│   ├── AdminPanel.java         # Interface administrateur
│   ├── ReparateurPanel.java    # Interface réparateur
│   ├── ProprietairePanel.java  # Interface propriétaire
│   ├── SuiviReparationPanel.java # Suivi réparation (public)
│   └── TestClientPresentation.java # Tests console
├── metier/                 # Logique métier
│   ├── DatabaseConnection.java   # Singleton connexion BD
│   ├── Gestion*.java           # Gestionnaires CRUD
│   └── IGestion*.java          # Interfaces métier
├── dao/                    # Entités JPA
└── exception/              # Exceptions personnalisées
```

## Rôles utilisateurs

### 1. Administrateur (ADMIN)
**Accès complet à toutes les fonctionnalités :**
- Gestion complète de tous les clients
- Gestion des appareils, réparations, composants
- Gestion des réparateurs et propriétaires
- Gestion des boutiques et caisses
- Gestion des emprunts et reçus
- Dashboard avec statistiques complètes

**Identifiants de démonstration :**
- Email : `admin@fastrepair.com`
- Mot de passe : `admin123`

### 2. Réparateur (REPARATEUR)
**Fonctionnalités spécialisées :**
- Gestion de ses réparations en cours
- Consultation des appareils
- Gestion des composants utilisés
- Profil personnel et statistiques

### 3. Propriétaire (PROPRIETAIRE)
**Vue d'ensemble et gestion financière :**
- Dashboard avec statistiques financières
- Gestion des caisses et soldes
- Consultation des reçus et emprunts
- Gestion des boutiques
- Rapports financiers détaillés

### 4. Client (anonyme)
**Suivi de réparation sans connexion :**
- Recherche par code de suivi
- Consultation de l'état de la réparation
- Historique et commentaires
- Informations financières

## Installation et lancement

### Prérequis
- Java 8 ou supérieur
- MySQL Server
- Base de données `fast_repair` configurée

### Lancement de l'application

1. **Compiler et exécuter :**
```bash
# Depuis le répertoire racine du projet
javac -cp "lib/*:." src/main/java/presentation/MainWindow.java
java -cp "lib/*:src/main/java" presentation.MainWindow
```

2. **Ou utiliser l'IDE :**
   - Importer le projet dans Eclipse/IntelliJ
   - Exécuter la classe `MainWindow`

### Configuration de la base de données

L'application utilise automatiquement la configuration définie dans `context.xml` :
- **URL** : `jdbc:mysql://localhost:3306/fast_repair`
- **Utilisateur** : `root`
- **Mot de passe** : `""` (vide)

## Fonctionnalités principales

### Authentification
- Sélection du rôle (Admin/Réparateur/Propriétaire)
- Saisie email/mot de passe
- Comptes de démonstration disponibles
- Accès public au suivi de réparation

### Interface Administrateur
- **10 onglets principaux :**
  - 👥 Clients : CRUD complet
  - 📱 Appareils : Gestion des appareils
  - 🔧 Réparations : Suivi des réparations
  - 👷 Réparateurs : Gestion du personnel
  - 🏢 Propriétaires : Gestion des propriétaires
  - 🏪 Boutiques : Gestion des points de vente
  - 💰 Caisses : Gestion financière
  - 🔩 Composants : Stock et pièces
  - 💸 Emprunts : Gestion des emprunts
  - 🧾 Reçus : Gestion des paiements
  - 📊 Dashboard : Statistiques générales

### Interface Réparateur
- **4 onglets spécialisés :**
  - 🔧 Réparations : Gestion personnelle
  - 📱 Appareils : Consultation
  - 🔩 Composants : Utilisation en réparation
  - 👤 Profil : Informations personnelles

### Interface Propriétaire
- **5 onglets de supervision :**
  - 📊 Dashboard : Vue d'ensemble financière
  - 💰 Finances : Gestion détaillée
  - 🏪 Boutiques : Supervision
  - 📊 Rapports : Analyses détaillées
  - 👤 Profil : Informations personnelles

### Suivi Public (sans connexion)
- 🔍 Recherche par code de suivi
- 📋 État détaillé de la réparation
- 💬 Commentaires du réparateur
- 💰 Informations financières

## Sécurité et gestion d'erreurs

### Gestion des exceptions
- **Validation** des données saisies
- **Gestion des transactions** JPA avec rollback automatique
- **Messages d'erreur** informatifs pour l'utilisateur
- **Logs** des erreurs pour le débogage

### Sécurité
- **Authentification** par rôle
- **Accès contrôlé** selon les permissions
- **Validation** des entrées utilisateur
- **Protection** contre les injections SQL via JPA

## Développement et extension

### Architecture modulaire
- **Séparation claire** : Présentation / Métier / DAO
- **Interfaces** pour faciliter les tests
- **Singleton** pour la connexion BD
- **Événements Swing** pour l'interactivité

### Points d'extension
- Ajout de nouveaux rôles utilisateur
- Nouvelles fonctionnalités par rôle
- Intégration de rapports avancés
- API REST pour accès distant

## Dépannage

### Problèmes courants

1. **Connexion BD impossible :**
   - Vérifier que MySQL est démarré
   - Contrôler les identifiants dans `context.xml`
   - Vérifier que la BD `fast_repair` existe

2. **Erreur d'authentification :**
   - Vérifier les identifiants de démonstration
   - Contrôler que les utilisateurs existent en BD

3. **Interface ne s'affiche pas :**
   - Vérifier que Java Swing est disponible
   - Contrôler les droits d'affichage graphique

### Logs et débogage
- Les erreurs sont affichées dans la console
- Messages informatifs pour l'utilisateur final
- Stack traces complètes en cas d'erreur système

## État du développement

### ✅ Implémenté
- Architecture complète avec Singleton
- Interfaces pour tous les rôles
- Authentification fonctionnelle
- Navigation entre interfaces
- Gestion d'erreurs robuste
- Suivi public des réparations

### 🔄 À implémenter (placeholders présents)
- Fonctions CRUD détaillées dans AdminPanel
- Gestion avancée des réparations
- Rapports financiers détaillés
- Statistiques avancées
- Validation des formulaires

---

**Fast-Repair** - Système de gestion de réparation moderne avec interface graphique intuitive.
