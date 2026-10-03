pipeline {
 agent any
 tools { maven 'mymaven' }
 environment {
   DOCKER_USER = 'REPLACE_DOCKERHUB_USERNAME'
   IMAGE_TAG = "${BUILD_NUMBER}"
 }
 stages {
   stage('Clean') { steps { cleanWs() } }
   stage('Code') { steps { checkout scm } }
   stage('Maven Build') { steps { sh 'mvn clean package' } }
   stage('SonarQube') { steps {
     script {
       def scannerHome = tool 'mysonar'
       withSonarQubeEnv('mysonar') {
         sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=InsurancePortal -Dsonar.sources=src/main/java -Dsonar.java.binaries=target/classes"
       }
     }
   } }
   stage('Quality Gate') { steps { timeout(time: 10, unit: 'MINUTES') { waitForQualityGate abortPipeline: true } } }
   stage('Nexus Artifact') { steps {
     nexusArtifactUploader artifacts: [[artifactId:'insurance',classifier:'',file:'target/insurance.war',type:'war']],
       credentialsId:'nexus',groupId:'com.practice',nexusUrl:'REPLACE_NEXUS_HOST:8081',
       nexusVersion:'nexus3',protocol:'http',repository:'myrepo',version:'1.0'
   } }
   stage('Prepare Docker Context') { steps { sh 'mkdir -p Docker-app/target && cp target/insurance.war Docker-app/target/' } }
   stage('Build Images') { steps {
     sh 'docker build -t "$DOCKER_USER/insurance-app:$IMAGE_TAG" Docker-app'
     sh 'docker build -t "$DOCKER_USER/insurance-db:$IMAGE_TAG" Docker-db'
   } }
   stage('Trivy Scan') { steps {
     sh 'trivy image "$DOCKER_USER/insurance-app:$IMAGE_TAG" > app-trivy.txt'
     sh 'trivy image "$DOCKER_USER/insurance-db:$IMAGE_TAG" > db-trivy.txt'
     archiveArtifacts artifacts:'*-trivy.txt'
   } }
   stage('Docker Hub Push') { steps {
     withCredentials([usernamePassword(credentialsId:'dockerhub',usernameVariable:'HUB_USER',passwordVariable:'HUB_TOKEN')]) {
       sh '''set -e
echo "$HUB_TOKEN" | docker login -u "$HUB_USER" --password-stdin
docker push "$DOCKER_USER/insurance-app:$IMAGE_TAG"
docker push "$DOCKER_USER/insurance-db:$IMAGE_TAG"
docker logout'''
     }
   } }
   stage('Swarm Deploy') { steps {
     withCredentials([
       string(credentialsId:'insurance-db-password',variable:'DB_PASSWORD'),
       string(credentialsId:'insurance-mysql-root-password',variable:'MYSQL_ROOT_PASSWORD')
     ]) { sh 'docker stack deploy -c compose.yml insurance' }
   } }
   stage('Verify') { steps { sh 'docker stack services insurance && docker stack ps insurance' } }
 }
}
