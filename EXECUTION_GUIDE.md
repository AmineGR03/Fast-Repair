# 🚀 Guide d'Exécution - Fast-Repair

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :
- **Java 11+** (`java -version` pour vérifier)
- **MySQL 8.0+** (serveur démarré)
- **Maven 3.6+** (`mvn -version` pour vérifier)
- **Git** (`git --version` pour vérifier)
- **Eclipse IDE** avec plugins Maven et Git (EGit)

## 🏁 Étapes d'Exécution

### 🚀 Méthode Rapide - Depuis Eclipse (Recommandé)

#### 1. 📥 Importer le Projet depuis Git
```
File → Import → Git → Projects from Git (with smart import) → Clone URI
```

Dans la fenêtre **Source Git Repository** :
- **URI :** `https://github.com/AmineGR03/Fast-Repair.git`
- **Host :** `github.com`
- **Repository path :** `/AmineGR03/Fast-Repair.git`
- **Protocol :** `https`
- **Port :** (laisser vide)

Cliquez sur **Next >**

#### 2. 🔑 Authentification Git (si nécessaire)
- Si demandé, entrez vos credentials GitHub
- Ou configurez une clé SSH si vous en avez une

#### 3. 📁 Sélection du Branch
- Sélectionnez **master** (branche principale)
- Cliquez sur **Next >**

#### 4. 📂 Répertoire Local
- Choisissez où sauvegarder le projet sur votre machine
- Cliquez sur **Next >**

#### 5. 🔧 Import des Projets
Eclipse détectera automatiquement que c'est un projet Maven :
- Sélectionnez **Import existing Eclipse projects**
- Cliquez sur **Next >**
- Eclipse importera automatiquement le projet Maven

#### 6. ⚙️ Configuration du Projet
Une fois importé :
```
Clic droit sur le projet → Properties → Java Build Path
```
- Vérifiez que le **JRE System Library** pointe vers Java 11+
- Allez dans **Project Facets** et assurez-vous que :
  - ✅ **Java** : 11+
  - ✅ **Dynamic Web Module** : activé
  - ✅ **JavaServer Faces** : désactivé (pas nécessaire)

#### 7. 📦 Mise à Jour des Dépendances Maven
```
Clic droit sur le projet → Maven → Update Project
```
- Cochez **Force Update of Snapshots/Releases**
- Cliquez sur **OK**

### 2. 🔧 Configuration de la Base de Données

#### Créer la base de données MySQL :
Ouvrez **MySQL Workbench** ou votre client MySQL préféré et exécutez :

```sql
-- Créer la base de données
CREATE DATABASE fast_repair CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer l'utilisateur
CREATE USER 'fastrepair'@'localhost' IDENTIFIED BY 'password123';

-- Donner les permissions
GRANT ALL PRIVILEGES ON fast_repair.* TO 'fastrepair'@'localhost';
FLUSH PRIVILEGES;
```

#### Modifier la configuration dans Eclipse :
Ouvrez `src/main/resources/META-INF/context.xml` et ajustez si nécessaire :
```xml
<Resource name="jdbc/fastrepairDS"
    username="fastrepair"
    password="password123"
    url="jdbc:mysql://localhost:3306/fast_repair?useSSL=false&amp;serverTimezone=UTC"/>
```

### 3. 🚀 Lancement depuis Eclipse

#### Méthode 1 : Via Run Configurations
```
Run → Run Configurations → Java Application → New
```
- **Name :** `FastRepair Main`
- **Main class :** `presentation.MainWindow`
- **Classpath :** Assurez-vous que tous les projets Maven sont inclus

Cliquez sur **Run**

#### Méthode 2 : Via Maven dans Eclipse
```
Clic droit sur le projet → Run As → Maven build
```
- **Goals :** `exec:java -Dexec.mainClass="presentation.MainWindow"`
- **Name :** `Run FastRepair`

Cliquez sur **Run**

### 🎯 Utilisation de l'Application

### Comptes de Test Disponibles :

| Rôle | Email | Mot de Passe |
|------|-------|-------------|
| **Admin** | admin@fastrepair.com | admin123 |
| **Propriétaire** | proprio@fastrepair.com | proprio123 |
| **Réparateur** | reparateur@fastrepair.com | reparateur123 |

### Fonctionnalités Principales :

1. **🔐 Connexion** : Utilisez les comptes ci-dessus
2. **👨‍💼 Propriétaire** : Gérez boutiques, réparations, finances
3. **🔧 Réparateur** : Gérez réparations, stock, caisse
4. **👑 Admin** : Contrôle total du système

## 💻 Méthode Alternative - Via Terminal

### 1. 📥 Cloner le Repository

```bash
# Depuis votre terminal
git clone https://github.com/AmineGR03/Fast-Repair.git
cd Fast-Repair
```

### 2. 🔧 Configuration de la Base de Données

#### Créer la base de données MySQL :
```sql
-- Dans MySQL Workbench ou terminal MySQL :
CREATE DATABASE fast_repair;
CREATE USER 'fastrepair'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON fast_repair.* TO 'fastrepair'@'localhost';
FLUSH PRIVILEGES;
```

#### Modifier la configuration (optionnel) :
Ouvrez `src/main/resources/META-INF/context.xml` et ajustez si nécessaire :
```xml
<Resource name="jdbc/fastrepairDS" ...>
    <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/fast_repair"/>
    <property name="javax.persistence.jdbc.user" value="fastrepair"/>
    <property name="javax.persistence.jdbc.password" value="password123"/>
</Resource>
```

### 3. 📦 Compilation Maven

```bash
# Nettoyer et compiler
mvn clean compile

# Si vous voulez aussi exécuter les tests
mvn clean test
```

### 4. 🚀 Lancement de l'Application

#### Méthode 1 : Via Maven (recommandé)
```bash
mvn exec:java -Dexec.mainClass="presentation.MainWindow"
```

#### Méthode 2 : Via Java direct
```bash
# Compiler d'abord
mvn compile

# Puis exécuter
java -cp "target/classes;src/main/resources" presentation.MainWindow
```

## 🎯 Utilisation de l'Application

### Comptes de Test Disponibles :

| Rôle | Email | Mot de Passe |
|------|-------|-------------|
| **Admin** | admin@fastrepair.com | admin123 |
| **Propriétaire** | proprio@fastrepair.com | proprio123 |
| **Réparateur** | reparateur@fastrepair.com | reparateur123 |

### Fonctionnalités Principales :

1. **🔐 Connexion** : Utilisez les comptes ci-dessus
2. **👨‍💼 Propriétaire** : Gérez boutiques, réparations, finances
3. **🔧 Réparateur** : Gérez réparations, stock, caisse
4. **👑 Admin** : Contrôle total du système

## 🔄 Mise à Jour du Projet

### Depuis Eclipse (Recommandé) :
```
Clic droit sur le projet → Team → Pull
```

Pour mettre à jour les dépendances Maven :
```
Clic droit sur le projet → Maven → Update Project
```
- Cochez **Force Update of Snapshots/Releases**
- Cliquez sur **OK**

### Depuis Terminal :
```bash
# Récupérer les dernières modifications
git pull origin master

# Recompiler
mvn clean compile

# Relancer l'application
mvn exec:java -Dexec.mainClass="presentation.MainWindow"
```

### Mise à jour des Dépendances :
```bash
# Mettre à jour les dépendances Maven
mvn dependency:resolve

# Forcer le téléchargement des dernières versions
mvn dependency:purge-local-repository
```

## 📤 Pousser des Changements depuis Eclipse

### Ajouter et Commiter :
```
Clic droit sur le projet → Team → Add to Index
Clic droit sur le projet → Team → Commit...
```
- Écrivez votre message de commit
- Sélectionnez les fichiers à commiter
- Cliquez sur **Commit and Push**

### Pousser directement :
```
Clic droit sur le projet → Team → Push to Upstream
```

### Voir l'Historique :
```
Clic droit sur le projet → Team → Show in History
```

## 🐛 Dépannage

### Erreur de Connexion Base de Données :
```bash
# Vérifier que MySQL est démarré
sudo systemctl status mysql  # Linux
# ou via MySQL Workbench
```

### Erreur de Compilation :
```bash
# Nettoyer complètement
mvn clean

# Supprimer le cache local
rm -rf ~/.m2/repository

# Recompiler
mvn compile
```

### Port MySQL Occupé :
- Vérifiez qu'aucun autre service n'utilise le port 3306
- Modifiez le port dans `context.xml` si nécessaire

## 📞 Support

En cas de problème :
1. Vérifiez les prérequis (Java, MySQL, Maven)
2. Consultez les logs de l'application
3. Vérifiez la configuration de la base de données
4. Ouvrez une issue sur GitHub : https://github.com/AmineGR03/Fast-Repair/issues

---

**🎉 Prêt à utiliser Fast-Repair !**
