def jar_build() {
    echo "building the application..."
    sh 'mvn package'
} 

def image_build() {
    echo "building the docker image..."
    withCredentials([usernamePassword(
        credentialsId: 'docker-repo', 
        usernameVariable: 'USER'
        passwordVariable: 'PASSWORD', 
        )]) {
        sh "docker build -t hamdiz0/java-maven-app:1.2 && \
        echo $PASSWORD | docker login -u $USER --password-stdin && \
        docker push hamdiz0/java-maven-app:1.2"
    }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this