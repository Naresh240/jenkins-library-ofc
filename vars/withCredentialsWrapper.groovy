def call(String credentialsId, Closure body) {
    withCredentials([
        usernamePassword(
            credentialsId: credentialsId,
            usernameVariable: 'JENKINS_USERNAME',
            passwordVariable: 'JENKINS_PASSWORD'
        )
    ]) {
        body()
    }
}