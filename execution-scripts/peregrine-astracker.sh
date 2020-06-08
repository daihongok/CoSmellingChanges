#!/bin/bash
#SBATCH --job-name=run_astracker-argouml
#SBATCH --mail-type=ALL
#SBATCH --time=2-12:00
#SBATCH --mail-user=r.j.scheedler@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=000

module load Java/11.0.2

srun ./run-astracker.sh -p argouml -d s2550709