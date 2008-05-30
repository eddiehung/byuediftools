#!/bin/bash

######################################################################
# $Id$
#
# Traverse all the subdirectories of the current directory and run the
# Xilinx trace tool (trce) as well as creating a back-annotated VHDL
# file. Used in conjunction with multiple_tmr, populate, and map.
#
# For usage, try `twr.sh --help'.
#
# Original author: James Carroll <jcarroll@byu.net>
#
date="$Date$"
version="$Revision$"
######################################################################

# Name of executable (without path)
this=$(basename $0)
# Include guessDesign() function
source ${0/${this}/guessDesign.sh}

#
# Print usage information.
#
usage()
{
    echo "Usage: twr.sh [-r] [-d DESIGN]
Traverses all the subdirectories of the current directory and runs the 
Xilinx trace tool (trce)

  -d, --design     Specify a design to do timing analysis for
  -r, --reverse    Reverse the order of the directories 

  -h, --help       Display this help and exit
  -v, --version    Display version information and exit

Report bugs to <jcarroll@byu.net>" 
}

#
# Print version and copyright information.
#
version () {
    echo "twr.sh, $version, $date
Copyright (C) 2006 Brigham Young University"
}

#
# Parse command-line parameters to this script
#
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-d | --design ) design=$2; shift ;;
	-h | --help ) usage ; exit 0 ;;
	-r | --reverse ) rev="-r" ;;
	-v | --version ) version ; exit 0 ;;
	* ) usage ; exit 1 ;; # unknown parameter
    esac
    shift
done

# Default values
if [ -z $design ]; then
    design=$(guessDesign)
fi
rev=${rev:-""}

dirs=$(ls -d ${design}_tmr_*[0-9][0-9]* | sort $rev)

for directory in $dirs; do

    # Heart of the script; this is where is actual work is done
    make -C $directory twr 

done
