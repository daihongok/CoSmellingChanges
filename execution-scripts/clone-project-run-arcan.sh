#!/bin/bash

# Requires modules to be loaded:
# Java/11.0.2
# Git

# Currently configured to analyze just one project

# Constants
ARCAN_JAR="./arcan.jar"
PROJECT_NAME="xerces2-j"
PROJECT_URL="https://github.com/apache/xerces2-j.git"
PROJECT_BRANCH="trunk"
ARCAN_OUTPUT_DIR=""
START_DATE="2010-01-01"
DATA_FOLDER="s2550709"

clone_and_analyze(){
    # Establish what the output folder will be.
    ARCAN_OUTPUT_DIR="/data/${DATA_FOLDER}/arcanoutput/${PROJECT_NAME}"

    # Create folder for source code of project(s) and for the output of arcan
    mkdir -p "/data/${DATA_FOLDER}/projects"
    mkdir -p "${ARCAN_OUTPUT_DIR}"

    # Clone project(s)
    git clone ${PROJECT_URL} "/data/${DATA_FOLDER}/projects/${PROJECT_NAME}"


    # Fetch config files for arcan and place them in the source directory of the project.
    cp -R sourceproperties/${PROJECT_NAME}/. /data/${DATA_FOLDER}/projects/${PROJECT_NAME}/


    # Run arcan on the cloned project
    java -jar ${ARCAN_JAR} -p "/data/${DATA_FOLDER}/projects/${PROJECT_NAME}" -out ${ARCAN_OUTPUT_DIR} -git -branch "${PROJECT_BRANCH}" -startdate ${START_DATE} -nDays 1
}

usage(){
    echo "./clone-project-run-arcan.sh [args]"
    echo "Arguments:"
    echo " -p  | --projectName          The name of the git-project to analyse. Must match exactly with Github."
    echo " -u  | --url                  The git url of the project used for cloning, ending with '.git'"
    echo " -b  | --branch               The branch to analyse. Often master or trunk."
    echo " -startdate  | --startdate    Startdate of the arcan analysis in yyyy-mm-dd."
    echo " -d   | --dataFolder          Folder in /data that will contain arcan files. example: s2550709"
}

parse_args(){
    while [[ $# -gt 0 ]]; do

        arg=$1
        case $arg in 
            -p | --projectName)
            PROJECT_NAME=$2
            
            shift
            shift
            ;;

            -u | --url)
            PROJECT_URL=$2
            shift
            shift
            ;;

            -b | --branch)
            PROJECT_BRANCH=$2
            shift
            shift
            ;;

            -startdate | --startdate)
            START_DATE=$2
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
clone_and_analyze

read -p "Press any key to continue" x