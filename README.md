# CoCo
CoCo is a Java application that mines co-changes from Java sourcecode repositories. 
It uses the ```fuzzy overlap``` algorithm and outputs both co-changes as well as changes per file.
## Requirements
The project was developed with the Java 13 SDK, so this version is recommended.

Maven 3 or higher is also recommended.
## Installation
The source code of this project can be cloned using Git or downloaded from Github. 
## Configuration
All configuration required for CoCo is done in the file ```config.properties```, which is located under ```resources/```.
The following hyperparameters can be configured:
### ProjectsDirectory
Folder in which the source code of the project to be analyzed can be found. If the source code is not present at runtime,
it will be downloaded automatically from Github based on the rest of the configuration.
### ProjectName
Name of the project. Must match with the name it has on Github, as this is used to determine where to download the source code for a project.
### ProjectOwner
Owner of the project on Github. This is also used for automatically downloading source code.
### ProjectBranch
The Git branch that will be analyzed. For example:
```refs/heads/master```
### LastCommit
Hash of the last (in time) commit to analyze. From here, CoCo will backtrack the project's evolution.
### CoChanges.ConsiderCommitsOverTime
Boolean that enables fuzzy matching. Instead of files having to change in exactly the same commit, a certain amount of difference in time between them is allowed based on the other co-change parameters.
### CoChanges.MaxHoursBetweenCommits
Determines how many hours may be between commit times for them to still count as overlapping.
### CoChanges.MaxCommitsBetweenCommits
Determines the distance between two commits, measured in commits, allowed for them to count as overlapping.
### CoChanges.MaxAmountOfCommits
Amount of commits to analyze, starting from ```LastCommit``` going back in time.
### CoChanges.Threshold
Amount of (fuzzy) matches required between two files for them to marked as co-changing. 

## Cluster
In order to run the application on a server cluster, example scripts are provided in the ```execution-scripts``` folder.

The examples relate to the Peregrine cluster of the University of Groningen.

## Output
The system outputs all filechanges in the analyzed timespan, as well as all co-changes detected by the ```fuzzy overlap``` algorithm.
