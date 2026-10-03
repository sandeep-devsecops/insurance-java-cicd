# Insurance Java CI/CD practice
A new Java 8 Servlet + JDBC + MySQL WAR project with the same *pipeline layout* as the previous vprofile example. It is not a copy of the original Spring app.

## First run
1. Install Java 8 and Maven. Run `mvn clean package` to create `target/insurance.war`.
2. Push this project to a **new** GitHub repository.
3. Create Docker Hub repositories `insurance-app` and `insurance-db` (public for easy practice).
4. Set up Jenkins Maven tool `mymaven`, Sonar scanner tool `mysonar`, SonarQube server `mysonar` and its webhook `http://JENKINS_HOST:8080/sonarqube-webhook/`.
5. Create Nexus Maven hosted repository `myrepo`.
6. Add Jenkins credentials: `nexus` (username/password), `dockerhub` (username/access token), `insurance-db-password` (secret text), `insurance-mysql-root-password` (secret text).
7. Replace `REPLACE_DOCKERHUB_USERNAME` and `REPLACE_NEXUS_HOST` in Jenkinsfile.
8. Install Docker and Trivy on the Jenkins agent. Configure a trusted Jenkins agent running on the Docker Swarm manager; run `docker swarm init` there if needed.
9. Jenkins: New Item > Pipeline > Pipeline script from SCM > Git > new repo URL > branch > Jenkinsfile. Build Now.
10. Verify `docker stack services insurance`, `docker stack ps insurance` and browse `http://MANAGER_IP:1234`.

## Quick local Docker test
```bash
mvn clean package
mkdir -p Docker-app/target
cp target/insurance.war Docker-app/target/
docker build -t insurance-app:local Docker-app
docker build -t insurance-db:local Docker-db
docker network create insurance-local
docker run -d --name insurancedb --network insurance-local -e MYSQL_ROOT_PASSWORD=localroot -e MYSQL_DATABASE=insurance -e MYSQL_USER=insurance -e MYSQL_PASSWORD=insurancepass insurance-db:local
# Wait for DB initialization, then:
docker run -d --name insurance-app --network insurance-local -p 1234:8080 -e DB_HOST=insurancedb -e DB_USER=insurance -e DB_PASSWORD=insurancepass insurance-app:local
# Open http://localhost:1234
```
Notes: SQL init runs only when DB storage is empty. Swarm volume in this lab is local to the manager, not production HA. Trivy generates reports but does not block on findings. For private Docker Hub images, authenticate on manager and use `docker stack deploy --with-registry-auth`. Avoid putting real production DB credentials into environment variables; use Docker secrets in production.
