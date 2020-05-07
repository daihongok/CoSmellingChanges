#!/bin/bash
#SBATCH --job-name=swagger-core-arcan-1
#SBATCH --mail-type=ALL
#SBATCH --time=3-12:00
#SBATCH --mail-user=r.m.kruizinga@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64000

module load Java/11.0.2
module load git/2.13.2-foss-2016a

srun ./clone-project-run-arcan.sh -p swagger-core -u https://github.com/swagger-api/swagger-core.git -b master
