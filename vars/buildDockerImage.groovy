def call(String latestVersion, String chartName) {
    // Print Maven version
    sh "mvn --version"

    // Build Docker image using the jib-maven-plugin
    sh """
        mvn compile com.google.cloud.tools:jib-maven-plugin:3.2.0:build \
            -Djib.from.image=artifactory.sdlc.ctl.gcp.db.com/dkr-all/com/db/fabric/managed-images/java-open-jdk:17.0.10.0 \
            -Djib.to.image=artifactory.sdlc.ctl.gcp.db.com/dkr-public-local/com/db/hotscan/hrsrl/${chartName}:${latestVersion}
    """
}