# Consultant Tracking Application

Application de suivi et de gestion des compétences des consultants développée avec Angular 18 et Spring Boot.

## 🚀 Fonctionnalités

- Gestion des compétences
  - Liste des compétences avec filtrage avancé
  - Catégorisation des compétences (Technique, Soft Skills, Projet, Management)
  - Niveaux de compétence (1-5)
  - Vue arborescente des compétences

- Évaluations
  - Suivi des évaluations des consultants
  - Historique des évaluations
  - Filtrage par type et date

## 🛠️ Technologies

### Frontend
- Angular 18
- Angular Material
- RxJS
- SCSS
- TypeScript

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA
- MariaDB
- Liquibase

## 📋 Prérequis

- Node.js (v18+)
- npm ou yarn
- Java 17+
- MariaDB

## 🔧 Installation

### Base de données
```bash
# Créer la base de données
mysql -u root -p
CREATE DATABASE consultant_tracking;

# Les migrations Liquibase s'exécuteront automatiquement au démarrage de l'application
```

### Backend
```bash
# Se placer dans le dossier backend
cd backend

# Compiler le projet
./mvnw clean install

# Lancer l'application
./mvnw spring-boot:run
```

### Frontend
```bash
# Se placer dans le dossier frontend
cd frontend

# Installer les dépendances
npm install

# Lancer l'application en mode développement
npm start
```

L'application sera accessible à l'adresse : `http://localhost:4200`

## 📁 Structure du Projet

```
consultant-tracking/
├── frontend/                # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/       # Services, modèles, guards
│   │   │   ├── features/   # Modules fonctionnels
│   │   │   └── shared/     # Composants partagés
│   │   ├── assets/         # Images, fonts, etc.
│   │   └── styles/         # Styles globaux
│   └── package.json
├── backend/                 # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/      # Code source Java
│   │   │   └── resources/ # Configuration
│   │   └── test/          # Tests
│   └── pom.xml
└── liquibase/              # Scripts de migration DB
```

## 🔐 Configuration

### Backend
Configurer les propriétés dans `backend/src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/consultant_tracking
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Frontend
Les variables d'environnement sont dans `frontend/src/environments/` :
- `environment.ts` pour le développement
- `environment.prod.ts` pour la production

## 🧪 Tests

### Backend
```bash
./mvnw test
```

### Frontend
```bash
npm test
```

## 📦 Déploiement

### Production Build
```bash
# Backend
./mvnw clean package

# Frontend
npm run build
```

Les fichiers de production seront générés dans :
- Backend : `target/consultant-tracking.jar`
- Frontend : `dist/consultant-tracking/`

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## 📝 License

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Auteurs

- Strawbang - *Développement initial*

## 🙏 Remerciements

- Angular Material pour les composants UI
- Spring Boot pour le framework backend
- La communauté open source
