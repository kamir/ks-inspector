#!/bin/bash

# Quick Start Script for ks-inspector
# This script helps you get started quickly with the ks-inspector tool

echo "🚀 ks-inspector Quick Start"
echo "=========================="
echo

# Check if Java 21 is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "⚠️  Java version is $JAVA_VERSION. Java 21 or higher is recommended."
else
    echo "✅ Java $JAVA_VERSION is available"
fi

echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+."
    exit 1
else
    echo "✅ Maven is available"
fi

echo

# Build the project
echo "🔨 Building the project..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi

echo
echo "✅ Build successful!"
echo

# Run the example
echo "🏃 Running example analysis..."
mvn exec:java -Pp10-1

if [ $? -ne 0 ]; then
    echo "❌ Example run failed. Please check the error messages above."
    exit 1
fi

echo
echo "✅ Example analysis completed!"
echo

# Show output locations
echo "📂 Output files are located in:"
echo "   - insights/  (Dependency graphs in .dot format)"
echo "   - pdf/       (PDF renderings of graphs)"
echo

# Instructions for next steps
echo "📋 Next steps:"
echo "   1. Check the generated .dot files in the insights/ directory"
echo "   2. Convert .dot files to PDF using: dot -Tpdf insights/filename.dot -o pdf/filename.pdf"
echo "   3. Create your own Maven profile in pom.xml for custom analysis"
echo "   4. Refer to QUICKSTART.md for detailed instructions"
echo

echo "🎉 You're ready to use ks-inspector!"