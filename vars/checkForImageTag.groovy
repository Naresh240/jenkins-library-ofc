def call() {
    if ( CODE_CHANGED == true) {
        echo "Source code changes detected. Updating image..."
    } else {
        echo "No source code changes detected. Using the same image..."
    }
}