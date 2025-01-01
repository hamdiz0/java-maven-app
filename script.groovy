// build the application
def jar_build() {
    echo 'building the application ...'
    sh 'mvn clean package'
} 
// increment the version
def increment_version(){
    echo 'incrementing app version ...'
    // Increment minor version in pom.xml using Maven build helper plugin
    sh 'mvn build-helper:parse-version versions:set \
    -DnewVersion="\\\${parsedVersion.majorVersion}.\\\${parsedVersion.nextMinorVersion}" \
    versions:commit'
    // Read the new version from pom.xml
    def match = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = match[0][1]           // get the first match
    env.IMAGE_VERSION = "$version"      // set the version environment variable
}

// build and push the docker image
def image_build(String imageName , String ver , String credId , String dockerfilelocation) {
    withCredentials([
            usernamePassword(
                credentialsId: "$credId",
                usernameVariable: "USER",
                passwordVariable: "PASSWORD"
            )
    ]){
        sh "docker build $dockerfilelocation -t $imageName:$ver"
        sh "docker build $dockerfilelocation -t $imageName:latest"
        sh "echo $PASSWORD | docker login -u $USER --password-stdin"
        sh "docker push $imageName:$ver"
        sh "docker push $imageName:latest"
    }
}

// update the version in the yaml file
// the script must be in the same directory as the yaml file
def update_yaml_version(String path, String script_name ,String version){
    echo 'changing the image version in the YAML files ...'
    sh """
        cd $path
        chmod +x $script_name
        ./$script_name -v $version
    """
}

// deploy the application
def deploy(String path, String script_name, String kube_config_path){
    echo 'deploying the application ...'
    sh """
        export KUBECONFIG=$kube_config_path
        cd $path 
        chmod +x $script_name
        ./$script_name
    """
}

// push the version change to the git repository
def git_push(String url , String credId , String commitMsg, String toBranch){
    echo "pushing to $toBranch ..."
    withCredentials([
        usernamePassword(
            credentialsId:"$credId",
            usernameVariable:'USER',
            passwordVariable:'TOKEN'
        )]){
        sh "git remote set-url origin https://${USER}:${TOKEN}@$url"
        sh "git pull origin main --rebase"
        sh "git add ."
        sh "git commit -m \"${commitMsg}\"" 
        sh "git push origin HEAD:$toBranch"
    }
}

return this


