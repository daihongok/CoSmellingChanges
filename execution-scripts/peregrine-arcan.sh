#!/bin/bash
#SBATCH --job-name=arcan-argouml-attempt3
#SBATCH --mail-type=ALL
#SBATCH --time=2-12:00
#SBATCH --mail-user=r.j.scheedler@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64000

module load Java/11.0.2

srun ./clone-project-run-arcan.sh -p argouml -u https://github.com/argouml-tigris-org/argouml.git -b master -startdate 2004-09-17 -d s2550709
