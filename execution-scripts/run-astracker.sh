#!/bin/bash

# Requires modules to be loaded:
# Java/11.0.2

# Constants
ASTRACKER_JAR="astracker.jar"
PROJECT="not_set"
DATA_FOLDER="not_set"
INPUT_DATA="/data/${DATA_FOLDER}/arcanoutput/${PROJECT}"
OUTPUT_DIR="/data/${DATA_FOLDER}/astracker_output"
SOURCE_CODE_DIR="/data/${DATA_FOLDER}/projects/${PROJECT}"


# Function for executing the astracker jar with the passed arguments.
astracker(){
    java -Xmx120g -jar ${ASTRACKER_JAR} $@
}

start_as_tracker_analysis() {
    INPUT_DATA="/data/${DATA_FOLDER}/arcanoutput/${PROJECT}"
    OUTPUT_DIR="/data/${DATA_FOLDER}/astracker_output"
    SOURCE_CODE_DIR="/data/${DATA_FOLDER}/projects/${PROJECT}"

    astracker -p $PROJECT -i $INPUT_DATA -o $OUTPUT_DIR -pC -rS -pCC -gitRepo $SOURCE_CODE_DIR
}

parse_args(){
    while [[ $# -gt 0 ]]; do

        arg=$1
        case $arg in 
            -p | --projectName)
            PROJECT=$2
            shift
            shift
            ;;

            -d | --dataFolder)
            DATA_FOLDER=$2
            
            shift
            shift
            ;;

            *)
            usage
            exit
            ;;

        esac

    done
}

#
# Start the program:
#
parse_args $@
start_as_tracker_analysis

#read -p "Press any key to continue" x