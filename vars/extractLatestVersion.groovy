def call(Map config = [:]) {
    def url = "${config.gcpArtifactoryUrl}/${config.gcpArtifactoryNamespace}/${config.chartName}/"
    def response = sh(
        script: """curl -v -u ${config.gcpArtUsername}:${config.gcpArtPassword} \
                     --proxy ${config.proxyUrl} ${url} -L -s""",
        returnStdout: true
    ).trim()

    // Extract versions using a regex
    def versions = []
    response.eachMatch(/>(\\d+\\.\\d+\\.\\d+-SNAPSHOT)\\//) { match ->
        versions << match[1]
    }
    versions = versions.collect { it.replaceAll(/[^\d.]/, '') }
    echo "Extracted versions: ${versions}"

    // Find the latest version
    def latestVersion = versions[0]
    versions.each { version ->
        def currentParts = version.tokenize('.-').collect { it as Integer }
        def latestParts = latestVersion.tokenize('.-').collect { it as Integer }
        for (int i = 0; i < currentParts.size(); i++) {
            if (currentParts[i] > latestParts[i]) {
                latestVersion = version
                break
            } else if (currentParts[i] < latestParts[i]) {
                break
            }
        }
    }

    def latestImgTag = "${latestVersion}-SNAPSHOT"
    echo "Latest image tag: ${latestImgTag} & image version: ${latestVersion}"

    // Export values
    env.IMAGE_VERSION = latestVersion
    env.IMAGE_TAG = latestImgTag
}