def call(String credentialsId, Closure body) {
    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'USERNAME',
        passwordVariable: 'PASSWORD'
    )]) {
        body(USERNAME, PASSWORD)
    }
}
