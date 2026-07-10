# PMT - Project Management Tool

Application full stack Angular + Spring Boot pour l'etude de cas PMT. Elle couvre l'inscription, la connexion, la creation de projets, l'invitation de membres, les roles projet, les taches Kanban, l'assignation, les notifications et l'historique des modifications.

## Stack

- Frontend : Angular 21, composants standalone, Vitest.
- Backend : Java 21, Spring Boot 4.1, Spring Web MVC, Spring Data JPA, Validation.
- Base de donnees : PostgreSQL.
- Industrialisation : Docker, Docker Compose, GitHub Actions, push Docker Hub.

## Comptes de demo

Le mot de passe des comptes inseres par `database/test-data.sql` est `Password123!`.

- `mathilde@example.com` : administratrice du projet PMT.
- `nicolas@example.com` : membre.
- `mariana@example.com` : observatrice.

## Lancement local avec Docker

```bash
docker compose up --build
```

Services exposes :

- Frontend : http://localhost:8081
- Backend : http://localhost:8080
- PostgreSQL : localhost:5432, base `pmt`, utilisateur `pmt`, mot de passe `pmt`

Les scripts `database/schema.sql` et `database/test-data.sql` sont executes automatiquement au premier demarrage du volume PostgreSQL.

## Lancement en developpement

Backend :

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Le profil `dev` utilise une base H2 en memoire et cree le schema automatiquement.

Frontend :

```bash
cd frontend
npm install
npm start
```

Le frontend consomme l'API `http://localhost:8080/api`.

## Tests et couverture

Backend :

```bash
cd backend
./mvnw verify
```

Rapport Jacoco : `backend/target/site/jacoco/index.html`.

Frontend :

```bash
cd frontend
npm run test -- --watch=false --coverage
```

Rapport Vitest : `frontend/coverage`.

Les seuils backend Jacoco sont configures a 60% sur instructions et branches, comme demande dans l'etude de cas.

## API principale

L'API utilise le header `X-User-Id` pour identifier l'utilisateur courant. Spring Security n'est volontairement pas ajoute, conformement a l'enonce.

- `POST /api/auth/register` : inscription.
- `POST /api/auth/login` : connexion.
- `GET /api/projects` : projets visibles par l'utilisateur.
- `POST /api/projects` : creation de projet, le createur devient `ADMIN`.
- `POST /api/projects/{projectId}/members` : invitation par e-mail, reservee aux administrateurs.
- `PATCH /api/projects/{projectId}/members/{memberId}/role` : changement de role.
- `GET /api/projects/{projectId}/tasks` : tableau des taches.
- `POST /api/projects/{projectId}/tasks` : creation de tache par `ADMIN` ou `MEMBER`.
- `PATCH /api/tasks/{taskId}` : mise a jour d'une tache.
- `POST /api/tasks/{taskId}/assignments` : assignation.
- `GET /api/tasks/{taskId}/history` : historique.
- `GET /api/notifications` : notifications de l'utilisateur.

## CI/CD

Le workflow `.github/workflows/ci-cd.yml` :

1. execute les tests backend et publie le rapport Jacoco en artefact ;
2. execute les tests frontend avec couverture et build Angular ;
3. construit et pousse les images Docker Hub sur `main`.

Secrets GitHub requis :

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

Images poussees :

- `${DOCKERHUB_USERNAME}/pmt-backend:latest`
- `${DOCKERHUB_USERNAME}/pmt-frontend:latest`

## Conception

Le schema relationnel est documente dans `docs/database-schema.md` et executable via `database/schema.sql`.
