# Consultant Tracking

*[English](README.md) ∙ [Français](docs/fr/README.md)*

## 📝 About

A web application for tracking and managing consultants, their assignments, and billing.

## 🚀 Features

- Consultant management
- Assignment tracking
- Billing management
- Analytics dashboard
- Responsive interface

## 🛠️ Technologies

- **Frontend**: Angular with Angular Material
- **Backend**: Spring Boot
- **Database**: MariaDB
- **Containerization**: Docker
- **CI/CD**: GitHub Actions

## 🏗️ Installation

1. Clone the repository:
```bash
git clone https://github.com/your-username/consultant-tracking.git
cd consultant-tracking
```

2. Copy the environment file:
```bash
cp .env.example .env
```

3. Modify the environment variables in `.env` according to your needs.

4. Launch the application with Docker Compose:
```bash
docker-compose up -d
```

The application will be accessible at:
- Frontend: http://app.localhost
- Backend API: http://api.localhost
- Adminer (DB management): http://adminer.localhost

## 🧪 Testing

### Backend
```bash
cd backend
./mvnw test
```

### Frontend
```bash
cd frontend
npm test
```

## 📖 Documentation

Detailed documentation is available in:
- [English](docs/en/README.md)
- [Français](docs/fr/README.md)

## 🤝 Contributing

Contributions are welcome! Please read our [contribution guidelines](docs/en/CONTRIBUTING.md) before getting started.

## 📜 Code of Conduct

We are committed to maintaining a welcoming and respectful environment for everyone. Please read our [Code of Conduct](docs/en/CODE_OF_CONDUCT.md) before contributing to the project.

## 📝 License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0). This means that:

- You can use this software for any purpose
- You can modify this software
- You can distribute this software
- You must include the license and copyright notice with each and every distribution
- You must include the complete source code of modifications
- You must state significant changes made to the software
- You must make available the complete source code to any network user
- This software comes with no warranties

For the full license text, see the [LICENSE](LICENSE) file or visit [GNU AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.en.html)

## 👥 Authors

- Strawbang - *Initial development*

## 🙏 Acknowledgments

- Angular Material for UI components
- Spring Boot for backend framework
- The open source community
