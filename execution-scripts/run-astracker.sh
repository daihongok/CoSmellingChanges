#!/bin/bash

# Requires modules to be loaded:
# Java/11.0.2

# Constants
ASTRACKER_JAR="astracker.jar"
PROJECT="Sonarlint-IntelliJ"
DATA_FOLDER="s2527227"
INPUT_DATA="/data/${DATA_FOLDER}/arcanoutput/${PROJECT}"
OUTPUT_DIR="/data/${DATA_FOLDER}/astracker_output"
SOURCE_CODE_DIR="/data/${DATA_FOLDER}/projects/${PROJECT}"


# Function for executing the astracker jar with the passed arguments.
astracker(){
    java -jar ${ASTRACKER_JAR} $@
}

# Run astracker
astracker -p $PROJECT -i $INPUT_DATA -o $OUTPUT_DIR -pC -rS -pCC -gitRepo $SOURCE_CODE_DIR