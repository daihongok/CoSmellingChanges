#!/bin/bash

# Requires modules to be loaded:
# Java/11.0.2

# Constants
ASTRACKER_JAR="astracker.jar"
PROJECT="sonarlint-intellij"
INPUT_DATA="arcanoutput/${PROJECT}"
OUTPUT_DIR="astracker_output"
SOURCE_CODE_DIR="projects/${PROJECT}"


# Function for executing the astracker jar with the passed arguments.
astracker(){
    java -jar ${ASTRACKER_JAR} $@
}

# Run astracker
astracker -p $PROJECT -i $INPUT_DATA -o $OUTPUT_DIR -pC -rS -pCC -gitRepo $SOURCE_CODE_DIR