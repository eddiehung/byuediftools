#!/bin/bash

###############################################################################
# $Id$
#
# Run the SEU simulator for each of the BLTmr instances. Used in
# conjunction with multiple_tmr.sh, map.sh, and populate.sh.
#
# For usage, try `simulate.sh --help'.
#
# Original Author: James Carroll <jcarroll@byu.net>
#
version="\$Revision$"
date="\$Date$"
###############################################################################

# Name of executable (without path)
this=$(basename $0)
# Include guessDesign() function
source ${0/${this}/guessDesign.sh}

#
# Print usage information. 
#
usage()
{
    echo "Usage: $this [OPTIONS] [DIRECTORY ...]
Runs the simulator for each of the BLTmr instances. If given one or more design
names, it will assume each name is associated with a subdirectory of the current
directory and assume the bit file has the same basename as the directory. If no
files are given, it will traverse all the subdirectories of the current
directory and run the simulator for each one.

  -d, --design     Specify a base design name (omit '.edf')
  -m, --multiple   If an output file (*pers*.txt or *sens*.txt) already exists,
                   go ahead and simulate, creating another output file.
  -l, --log        Specify log file
  -r, --reverse    Reverse the order of the directories (no effect if given 
                   specific directories)
  --x0             Specify the x0 (controller) bit file (Default: ./x0.bit)
  --x2             Specify the x2 (golden) bit file (Default: $design_xp2.bit)

  -h, --help       Display this help and exit
  -v, --version    Display version information and exit

EXAMPLES

  Basic usage.

$this

  Explicitly specify directories which contain bit files to simulate:

$this dir1/ dir2/ dir3/ etc...

  Force the simulator to run, even if results already exist. New results will be
  stored in a new file.

$this -m

  Process the directories in reverse order:

$this -r

Report bugs to <jcarroll@byu.net>" 
}

#
# Print version and copyright information.
#
version () {
    echo "$this, $version, $date
Copyright (C) 2006 Brigham Young University"
}

#
# Parse command-line parameters to this script
#
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-d | --design ) design=$2 ; shift ;;
	-m | --multiple ) multiple=1 ;;
	-l | --log ) simulate_log=$2 ; shift ;;
	-r | --reverse ) reverse="-r" ;;
	-h | --help ) usage ; exit 0 ;;
	-v | --version ) version ; exit 0 ;;
	--x0 ) x0=$2 ; shift ;;
	--x2 ) x2=$2 ; shift ;;
	* ) echo "Unknown parameter: $1"; usage ; exit 1 ;; # unknown parameter
    esac
    shift
done

# Default values
if [ -z $design ]; then
    design=$(guessDesign)
fi
multiple=${multiple:-0}
reverse=${reverse:-""}
# Controller bit file
#x0=${x0:-$HOME/seu_simulator/users/ejohnson/rad_studies/virtex/simulator/firmware/x0/x0.bit}
x0=${x0:-x0.bit}
# Golden bit file
x2=${x2:-${design}_xp2.bit}
x2=${x2:-${design}/${design}_xp2.bit}
verbose=1

# If no command-line parameters given, print usage info and exit.
if [ $# -eq 0 ]; then
    dirs=$(ls -d ${design}_tmr_[aud]*[0-9]* | sort $reverse)   # [aud] for {asuf,uef,duf}
elif [ -n $1 ]; then
    dirs=$*
else
    usage ; exit 1
fi

this=$(basename $0)
simulate_log=${simulate_log:-simulate.log}

# Record the revision and date in the simulate log. (Revision number
# not working as of revision 1.6)
echo "Simulate version $version $(date)" >> $simulate_log

# Record the exact command used to invoke simulate.sh (not working as
# of revision 1.6)
echo "command:  $*" >> $simulate_log  

# Iterate through each of the directories to be processes
for directory in $dirs; do
    directory=${directory#./} # Remove leading dot-slash
    directory=${directory%/}  # Remove trailing slash

    # 1. Ensure .bit files exist
    # xp1
    if [ ! -e $directory/${directory}_xp1-fixed.bit ]; then
	echo "Warning: xp1 bitfile not found for BLTmr instance $directory ." | tee $simulate_log
	continue
    fi
    # xp2
    if [ ! -e $x2 ]; then
	echo "Error: x2 bitfile $x2 does not exist!" | tee $simulate_log
    fi
    # x0
    if [ ! -e $x0 ]; then
	echo "Error: x0 bitfile $x0 does not exist!" | tee $simulate_log
    fi

    # 2. Check if a simulation has already been run
    sens=""
    pers=""

    sens=$(ls $directory/*sens*.txt)
    pers=$(ls $directory/*pers*.txt)

    if [ $multiple -eq 0 ] && [ -n "$sens" ] && [ -n "$pers" ]; then
	echo "$this: Warning: output files already exist. Skipping design $directory
(Use \`$this -m ...' to run multiple simulations.)" | tee $simulate_log
	continue
    fi

    cd $directory

    # 3. Ensure unique output file names so we don't clobber previous results
    i=1
    sens=seu_${directory}_sens
    pers=seu_${directory}_pers
    while [ -s ${sens}_${i}.txt ] || [ -s ${pers}_${i}.txt ]; do
	((i++))
    done
    sens_output_file=${sens}_${i}.dat
    pers_output_file=${pers}_${i}.dat
    
    # Set options for both sensitivity and persistence test
    x1=${directory}_xp1-fixed.bit       # Unit under test
    common_opts="-x0 $x0 -x1 $x1 -x2 $x2"

    # Set options for sensitivity test
    seed=$i
    sensitivity_opts="-o ${sens_output_file} -fixedrand $seed"

    # Set options for persistence test
    reset_style=0
    x=0
    y=1000
    z=10
    persistance_opts="-p $reset_style $x $y $z $pers_output_file -fixedrand $seed"

    #
    # 4. Heart of the script: Run the simulator, both sensitivity and 
    # persistence, and get human-readible header data.
    #
    
    # 4.a) Persistence
    echo "$this: Starting persistance for BLTmr instance $directory with seed $seed..." >> $simulate_log
    cmd="$HOME/seusim_elf ${common_opts} ${persistance_opts}"
    if [ $verbose = 1 ]; then
	echo "Executing: $cmd"
    fi
    $cmd
    sed -n -e '/^%%%%/,/^%%%%/p' < ${pers_output_file} > ${pers_output_file%.dat}.txt

    # 4.b) Sensitivity
    echo "$this: Starting sensitivity for BLTmr instance $directory with seed $seed..." >> $simulate_log
    cmd="$HOME/seusim_elf ${common_opts} ${sensitivity_opts}"
    if [ $verbose = 1 ]; then
	echo "Executing: $cmd"
    fi
    $cmd
    sed -n -e '/^%%%%/,/^%%%%/p' < ${sens_output_file} > ${sens_output_file%.dat}.txt

    # 5. Compress the output files, forcing overwrite
    gzip -f *.dat 
##    gzip -f *.dat *.ncd *.xdl *.ngm *.ngd *.ngo *.hlrpt

    cd ..
done

echo "$this: Finished simulation of designs. If simulation completed 
successfully, you should now extract the data with 
extract_data.sh"
