# 🚀 Guide d'Exécution - Fast-Repair

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :
- **Java 11+** (`java -version` pour vérifier)
- **MySQL 8.0+** (serveur démarré)
- **Maven 3.6+** (`mvn -version` pour vérifier)
- **Git** (`git --version` pour vérifier)

## 🏁 Étapes d'Exécution

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

### Depuis GitHub :
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
