def call(Boolean configChanged) {
    if (configChanged) {
        echo "Config changes detected. Updating Helm package..."
    } else {
        echo "No config changes detected. Using the same Helm package..."
    }
}