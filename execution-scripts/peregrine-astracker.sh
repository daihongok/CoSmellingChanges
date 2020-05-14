#!/bin/bash
#SBATCH --job-name=run_astracker-hibernate8gb
#SBATCH --mail-type=ALL
#SBATCH --time=2-12:00
#SBATCH --mail-user=r.j.scheedler@student.rug.nl
#SBATCH --output=job-%j.log
#SBATCH --partition=regular
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=128000

module load Java/11.0.2

srun ./run-astracker.sh -p hibernate-orm -d s2550709