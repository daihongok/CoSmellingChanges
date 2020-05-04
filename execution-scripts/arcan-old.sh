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
ARCAN_OUTPUT_DIR="arcanoutput/${PROJECT_NAME}"

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
java -jar ${ARCAN_JAR} -p "./projects/${PROJECT_NAME}" -out ${ARCAN_OUTPUT_DIR} -git -branch "${PROJECT_BRANCH}" -nweeks 0
