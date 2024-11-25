def call(String chartName, String artifactUrl, String username, String password) {
    sh """
        helm repo add helm-public-local ${artifactUrl} --username ${username} --password ${password}
    """

    def previousPackage = sh(
        script: "helm search repo ${chartName} -1 | sort -Vr | head -n 1 | awk '{print \$2}'",
        returnStdout: true
    ).trim()
    echo "Latest package: ${previousPackage}"

    def previousVersion = previousPackage.replace("${chartName}-", "").replace(".tgz", "")
    echo "Latest package version: ${previousVersion}"
    return [packageName: previousPackage, packageVersion: previousVersion]
}
