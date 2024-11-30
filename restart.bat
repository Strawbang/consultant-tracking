@echo off
echo Nettoyage des conteneurs et volumes...
docker compose down -v

echo.
echo Nettoyage des images...
docker compose rm -f

echo.
echo Reconstruction et demarrage des conteneurs en mode developpement...
docker compose up --build -d

echo.
echo Les services sont en cours de demarrage...
echo Pour voir les logs, utilisez: docker compose logs -f
echo Pour acceder a l'application:
echo - Frontend: http://localhost:4200
echo - API: http://localhost:8080/api
