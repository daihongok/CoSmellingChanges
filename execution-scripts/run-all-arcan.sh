#!/bin/bash
#SBATCH --job-name=run_arcan_analysis
#SBATCH --mail-type=ALL
#SBATCH --time=1-12:00
#SBATCH --mail-user=r.j.scheedler@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64000

module load Java/11.0.2
module load git/2.13.2-foss-2016a

srun ./clone-project-run-arcan.sh -p argouml -u https://github.com/argouml-tigris-org/argouml.git -b master
srun ./clone-project-run-arcan.sh -p cassandra -u https://github.com/apache/cassandra.git -b trunk
srun ./clone-project-run-arcan.sh -p elasticsearch -u https://github.com/elastic/elasticsearch.git -b master
srun ./clone-project-run-arcan.sh -p sonarlint-intellij -u https://github.com/SonarSource/sonarlint-intellij.git -b master
srun ./clone-project-run-arcan.sh -p swagger-core -u https://github.com/swagger-api/swagger-core.git -b master
srun ./clone-project-run-arcan.sh -p testng -u https://github.com/cbeust/testng.git -b master
