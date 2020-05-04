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
END_DATE="30-10-2020"

clone_and_analyze(){
    # Create folder for source code of project(s) and for the output of arcan
    mkdir "projects"
    mkdir -p "${ARCAN_OUTPUT_DIR}"

    # Clone project(s)
    cd "projects"
    git clone ${PROJECT_URL}
    cd .. # Back in root

    # Fetch config files for arcan and place them in the source directory of the project.
    cp -R sourceproperties/${PROJECT_NAME}/. projects/${PROJECT_NAME}/


    # Run arcan on the cloned project
    java -jar ${ARCAN_JAR} -p "./projects/${PROJECT_NAME}" -out ${ARCAN_OUTPUT_DIR} -git -branch "${PROJECT_BRANCH}" -nDays 1
}

usage(){
    echo "./clone-project-run-arcan.sh [args]"
    echo "Arguments:"
    echo " -p  | --projectName          The name of the git-project to analyse. Must match exactly with Github."
    echo " -u  | --url                  The git url of the project used for cloning, ending with '.git'"
    echo " -b  | --branch               The branch to analyse. Often master or trunk."
    echo " -startdate  | --startdate    Startdate of the arcan analysis in yyyy-mm-dd."
    echo " -enddate  | --enddate        Enddate of the arcan analysis in yyyy-mm-dd."
}

parse_args(){
    while [[ $# -gt 0 ]]; do

        arg=$1
        case $arg in 
            -p | --projectName)
            PROJECT_NAME=$2
            ARCAN_OUTPUT_DIR="arcanoutput/${PROJECT_NAME}"
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

            -enddate | --enddate)
            END_DATE=$2
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

#read -p "Press any key to continue" x