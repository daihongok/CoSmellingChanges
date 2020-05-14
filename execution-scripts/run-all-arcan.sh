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
srun ./clone-project-run-arcan.sh -p jackson-databind -u https://github.com/FasterXML/jackson-databind.git -b master -startdate 2012-07-18 -d s2550709
srun ./clone-project-run-arcan.sh -p jenkins -u https://github.com/jenkinsci/jenkins.git -b master -startdate 2015-11-08 -d s2550709
srun ./clone-project-run-arcan.sh -p junit5 -u https://github.com/junit-team/junit5.git -b master -startdate 2015-10-31 -d s2550709

srun ./clone-project-run-arcan.sh -p lucene-solr -u https://github.com/apache/lucene-solr.git -b master -startdate 2016-07-12 -d s2550709
srun ./clone-project-run-arcan.sh -p mybatis-3 -u https://github.com/mybatis/mybatis-3.git -b master -startdate 2010-05-10 -d s2550709
srun ./clone-project-run-arcan.sh -p pdfbox -u https://github.com/apache/pdfbox.git -b trunk -startdate 2011-10-13 -d s2550709
srun ./clone-project-run-arcan.sh -p poi -u https://github.com/apache/poi.git -b trunk -startdate 2011-05-20 -d s2550709
srun ./clone-project-run-arcan.sh -p pgjdbc -u https://github.com/pgjdbc/pgjdbc.git -b master -startdate 2001-05-16 -d s2550709
srun ./clone-project-run-arcan.sh -p roboelectric -u https://github.com/robolectric/robolectric.git -b master -startdate 2015-01-06 -d s2550709
srun ./clone-project-run-arcan.sh -p RxJava -u https://github.com/ReactiveX/RxJava.git -b 3.x -startdate 2012-04-10 -d s2550709
srun ./clone-project-run-arcan.sh -p sonarlint-intellij -u https://github.com/SonarSource/sonarlint-intellij.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p swagger-core -u https://github.com/swagger-api/swagger-core.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p testng -u https://github.com/cbeust/testng.git -b master -d s2550709
srun ./clone-project-run-arcan.sh -p spring-framework -u https://github.com/spring-projects/spring-framework.git -b master -startdate 2015-04-12 -d s2550709
srun ./clone-project-run-arcan.sh -p xerces2-j -u https://github.com/apache/xerces2-j.git -b trunk -startdate 2004-01-22 -d s2550709