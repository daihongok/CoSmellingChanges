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

srun ./clone-project-run-arcan.sh -p argouml -u https://github.com/argouml-tigris-org/argouml.git -b master -startdate 2004-09-17 -d s2550709
srun ./clone-project-run-arcan.sh -p cassandra -u https://github.com/apache/cassandra.git -b trunk -startdate 2010-10-02 -d s2550709
srun ./clone-project-run-arcan.sh -p druid -u https://github.com/apache/druid.git -b master -startdate 2013-01-02 -d s2550709
srun ./clone-project-run-arcan.sh -p elasticsearch -u https://github.com/elastic/elasticsearch.git -b master -startdate 2016-05-07 -d s2550709
srun ./clone-project-run-arcan.sh -p guava -u https://github.com/google/guava.git -b master -startdate 2011-05-11 -d s2550709
srun ./clone-project-run-arcan.sh -p hibernate-orm -u https://github.com/hibernate/hibernate-orm.git -b master -startdate 2007-12-07 -d s2550709

srun ./clone-project-run-arcan.sh -p roboelectric -u https://github.com/robolectric/robolectric.git -b master -startdate 2015-01-06 -d s2550709
srun ./clone-project-run-arcan.sh -p sonarlint-intellij -u https://github.com/SonarSource/sonarlint-intellij.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p swagger-core -u https://github.com/swagger-api/swagger-core.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p testng -u https://github.com/cbeust/testng.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p spring-framework -u https://github.com/spring-projects/spring-framework.git -b master -startdate 2015-04-12 -d s2550709
srun ./clone-project-run-arcan.sh -p xerces2-j -u https://github.com/apache/xerces2-j.git -b trunk -startdate 2004-01-22 -d s2550709