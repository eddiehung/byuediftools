#!/bin/bash

######################################################################
# $Id$
#
# Take the TMR'd EDIF output from multiple_tmr.sh and convert it to a
# bitstream, going through the necessary intermediate formats (using
# map, par, and other Xilinx tools, along with Paul's half-latch
# removal).  This should be run after multiple_tmr.sh.
#
# Also records how long the synthesis process took.
#
# For usage, try `map.sh --help'.
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
    echo "Usage: map.sh [-r]
Traverses all the subdirectories of the current directory and converts the EDIF
to a bitfile, going through the necessary intermediate formats (using map, par, 
and other tools)

  -d, --design     Specify a design to do synthesize
  -r, --reverse    Reverse the order of the directories 

  -h, --help       Display this help and exit
  -v, --version    Display version information and exit

Report bugs to <jcarroll@byu.net>" 
}

#
# Print version and copyright information.
#
version () {
    echo "map.sh, $version, $date
Copyright (C) 2006 Brigham Young University"
}

#
# 1. Parse command-line parameters to this script
#
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-d | --design ) design=$2 ; shift ;;
	-h | --help ) usage ; exit 0 ;;
	-r | --reverse ) rev="-r" ;;
	-v | --version ) version ; exit 0 ;;
	* ) echo "$this: Error: unknown parameter: $1" ; usage ; exit 1 ;;
    esac
    shift
done

# 1.a) Default values
rev=${rev:-""}
if [ -z $design ]; then
    design=$(guessDesign)
fi
dirs=$(ls -d ${design}_[adu]*_[0-9]* | sort $rev)
min_time=2  # Low threshold time-to-completion to flag a warning
map_log=${map_log:-map.sh.log}

# 2. Header for log entry
echo "
$this $version $(date)" >> $map_log

# 3. Loop across each of the directories
for directory in $dirs; do
    now=$(date)
    before=$(date +%s)

    # Heart of the script; this is where the actual work is done.
    # Call the makefile with targets: xp1, twr, and vhdl
    # (back-annotated vhdl).
    #cmd1="make -C $directory xdl"
    #$cmd1
    cmd1="make -C $directory xp1"
    $cmd1
    #cmd2="make -C $directory vhdl"
    #$cmd2
    # cmd3="make -C $directory twr"
    # $cmd3
    after=$(date +%s)
    time_elapsed=$(( after - before ))

    if [ $time_elapsed -gt $min_time ]; then
	echo -e "$time_elapsed second(s) '$cmd1', $now" >> make.time
    fi

    echo "Finished processing $directory" >> $map_log
done

