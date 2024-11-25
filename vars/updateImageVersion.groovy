def call(String currentVersion) {
    // Split the version string into major, minor, and patch parts
    def versionParts = currentVersion.split('\\.')
    def major = versionParts[0]
    def minor = versionParts[1]
    def patch = (versionParts[2] as int) + 1

    // Generate the new version and tag
    def version = "${major}.${minor}.${patch}"
    def newVersion = "${major}.${minor}.${patch}-SNAPSHOT"
    def newImageTag = "${newVersion}"

    // Output the results
    echo "New image tag: ${newImageTag} & image version: ${version}"

    // Return the new version and image tag
    return [imageTag: newImageTag, imageVersion: version]
}