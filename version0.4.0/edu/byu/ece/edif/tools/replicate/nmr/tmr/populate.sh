#!/bin/bash

######################################################################
# $Id$
#
# Populate symbolic links to the Makefile, ucf file, and other files,
# in each directory. If no design is specified, the base deign name is
# guessed with the guessDesign.sh script. If a design is specified,
# then all folders with "$DESIGN_" in the name are used. This
# script is used in conjunction with multiple_tmr, map, and
# extract_data.
#
# For usage, try `populate.sh --help'.
#
# Original author: James Carroll <jcarroll@byu.net>
#
version="$Revision$"
date="$Date$"
######################################################################

# Name of executable (without path)
this=$(basename $0)
# Include guessDesign() function
source ${0/${this}/guessDesign.sh}

#
# Print usage information. For detailed info, call "usage -v"
#
usage()
{
    echo "Usage: $this [DESIGN]
Populates makefiles, ucf files, etc. in each directory

  -h, --help       Display this help and exit
  -v, --version    Display version information and exit

EXAMPLES

  Basic usage. The following will search for all subdirectories
  *_{asuf|uef|duf}_*, and populate the makefile, ucf, file, etc. in
  each:

$this

  Specify a design.  If the original EDIF file were myCounter.edf, use
  the following, which will search for all subdirectories
  myCounter_{asuf|duf|uef}_*, and populate each of them.

$this myCounter

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
# 1. Parse command-line parameters to this script
#
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-h | --help ) usage ; exit 0 ;;
	-v | --version ) version ; exit 0 ;;
	* ) echo "$this: Unknown parameter: $1"; usage ; exit 1 ;; # unknown parameter
    esac
    shift
done

# 1.a) Check for optional design parameter.
if [ $# -eq 0 ]; then
    design=$(guessDesign)
elif [ -n $1 ]; then
    design=$1
else
    usage ; exit 1
fi
folders=$(find -mindepth 1 -maxdepth 1 -type d -iname "${design}_[aud]*")

# 2. Actual work begins here.
for directory in $folders; do
    directory=${directory#*/} # change "./dir" to "dir"

    # 2.a) Populate the ucf file.
    ln -sf ../${design}_xp1.ucf $directory/${directory}_xp1.ucf

    # 2.b) Populate the Makefile.
    ln -sf ../Makefile $directory/Makefile

    # 2.c) Populate the do (ModelSim macro) file.
    ln -sf ../${design}.do $directory/${directory}.do

    # 2.d) Populate the .mpf (ModelSim project) file.
    mpf_file=${design}.mpf
    if [ -s $mpf_file ]; then
	cp -f $mpf_file $directory/${directory}.mpf
	sed -i -e 's/${design}/${directory}/g' $directory/${directory}.mpf
    fi
done

