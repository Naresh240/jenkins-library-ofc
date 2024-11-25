def call() {
    def latestCommit = sh(script: 'git log -1 --format=%H', returnStdout: true).trim()

    def codeChanges = sh(script: 'git log -1 --format=%H -- src', returnStdout: true).trim()
    if (codeChanges == latestCommit) {
        CODE_CHANGED = true
    } else {
        CODE_CHANGED = false
    }

    def configChanges = sh(script: 'git log -1 --format=%H -- config', returnStdout: true).trim()
    if (configChanges == latestCommit) {
        CONFIG_CHANGED = true
    } else {
        CONFIG_CHANGED = false
    }

    return [codeChanged: CODE_CHANGED, configChanged: CONFIG_CHANGED]
}
