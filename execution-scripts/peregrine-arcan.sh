#!/bin/bash
#SBATCH --job-name=arcan-rxjava-attempt1
#SBATCH --mail-type=ALL
#SBATCH --time=3-12:00
#SBATCH --mail-user=r.j.scheedler@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64000

module load Java/11.0.2

srun ./clone-project-run-arcan.sh -p RxJava -u https://github.com/ReactiveX/RxJava.git -b 3.x -startdate 2012-04-10 -d s2550709
