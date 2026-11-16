BOLD := `printf "\033[1m"`
RED := `printf "\033[31m"`
RESET := `printf "\033[0m"`

# List all targets
[private]
default:
    @just --list

# Install dependencies
[group('setup')]
install:
    @echo "{{RED}}{{BOLD}}Installing dependencies...{{RESET}}"
    # @brew install java ant jdtls jq
    @rm -rf lib/
    @mkdir lib
    @cd lib/ && for path in \
        "org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar" \
        "org/tinylog/tinylog-api/2.7.0/tinylog-api-2.7.0.jar" \
        "org/tinylog/tinylog-impl/2.7.0/tinylog-impl-2.7.0.jar" \
    ; do \
        curl --fail --remote-name "https://repo1.maven.org/maven2/$path"; \
    done

# Build project
[group('dev')]
build:
    @echo "{{RED}}{{BOLD}}Building project...{{RESET}}"
    @rm -rf build/
    @mkdir build
    @find src -type f -name "*.java" > sources
    @javac -d build/ -cp .:lib/* @sources -Xlint:unchecked
    @find test -type f -name "*Test.java" > testsources
    @if [ -s testsources ]; then \
        javac -d build/ -cp .:lib/*:build/ @testsources -Xlint:unchecked; \
    fi
    @rm sources testsources

# Run all tests
[group('dev')]
test: build
    @echo "{{RED}}{{BOLD}}Running tests...{{RESET}}"
    @java -ea -cp .:lib/*:build/ org.junit.platform.console.ConsoleLauncher execute \
        --scan-classpath

# Run the app
[group('dev')]
run: build
    @echo "{{RED}}{{BOLD}}Running the app...{{RESET}}"
    @java -ea -cp .:lib/*:build/ consistenthashing.Main
