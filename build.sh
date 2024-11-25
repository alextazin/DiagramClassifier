#!/bin/bash

SRC_DIR="src"
BUILD_DIR="build"
JAR_NAME="app.jar"
MAIN_CLASS="diagramclassifier.DiagramClassifier" 

echo "Cleaning up previous builds..."
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

echo "Compiling source code..."
javac -d "$BUILD_DIR" $(find "$SRC_DIR" -name "*.java")

if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting..."
    exit 1
fi

echo "Compilation successful."

echo "Creating JAR file..."
jar cfe "$JAR_NAME" "$MAIN_CLASS" -C "$BUILD_DIR" .

if [ $? -ne 0 ]; then
    echo "Failed to create JAR file. Exiting..."
    exit 1
fi

echo "JAR file created: $JAR_NAME"

echo "Build completed successfully."
