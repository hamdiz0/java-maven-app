def jar_build() {
    echo 'building the application ...'
    sh 'mvn clean package'
} 

def image_build(String imageName , String ver , String credId , String dockerfilelocation) {
    withCredentials([
            usernamePassword(
                credentialsId: "$credId",
                usernameVariable: "USER",
                passwordVariable: "PASSWORD"
            )
    ]){
        sh "docker build $dockerfilelocation -t $imageName:$ver "
        sh "echo $PASSWORD | docker login -u $USER --password-stdin"
        sh "docker push $imageName:$ver"
    }
}

def deploy() {
    echo 'deploying the application ...'
} 

def git_push(String url , String credId , String commitMsg, String toBranch){
    echo "pushing to $toBranch ..."
    withCredentials([
        usernamePassword(
            credentialsId:"$credId",
            usernameVariable:'USER',
            passwordVariable:'TOKEN'
        )]){
        sh "git remote set-url origin https://${USER}:${TOKEN}@$url"
        sh "git add ."
        sh "git commit -m \"${commitMsg}\"" 
        sh "git push origin HEAD:$toBranch"
    }
}

return this


