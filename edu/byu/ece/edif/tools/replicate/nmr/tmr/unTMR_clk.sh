#!/bin/bash

######################################################################
# $Id$
#
# Take an XDL file with a triplicated clk and untriplicate the clk,
# hence "unTMR_clk". See also unTMR_clk.pl.
#
# For usage, try `twr.sh --help'.
#
# Original Author: James Carroll <jcarroll@byu.net>
#
version="$Revision$"
date="$Date$"
######################################################################

this=$(basename $0)
# Get relative path to unTMR_clk.pl
unTMR_clk_pl=${0/${this}/unTMR_clk.pl}

#
# Print usage information. For detailed info, call "usage -v"
#
usage()
{
    # verbose usage information
    if [ $# != 0 ] && [ $1 = -v ]; then
	echo "Usage: $this INPUT_FILE

Given an XDL file with a triplicated clock line, untriplicate the
clock by removing the second and third instances and changing nets as
needed. If the input file is some.file.name.xdl, the default output
file will be some.file.name.unTMR_clk.xdl.

Usage:

  $this --help      display this help and exit
  $this --version   output version information and exit

EXAMPLES:

  Basic usage:

$this design.xdl

Report bugs to <jcarroll@byu.net>." 

    # concise usage information
    else
	echo "$this INPUT_FILE"
	echo "Try \`$this --help' for more information."
    fi
}

#
# Print version and copyright information.
#
version () {
    echo "$this, $version, $date
Copyright (C) 2006 Brigham Young University"
}

#
# Begin script
#

# Parse command-line parameters
while [ -n "$(echo $1 | grep ' -')" ]; do
    case $1 in
	--help ) usage -v ; exit 0 ;;
	--version ) version ; exit 0 ;;
	* ) echo "Error. Unknown parameter: $1" ; usage ; exit 1 ;;
    esac
    shift
done

# Check for missing parameters
if [ $# = 0 ]; then
    usage ; exit 1
fi

# Get input file name
in=$1

# Strip parent directories and .xdl extension (if present)
name=$(basename $in .xdl) 

# Get output filename or use default from input filename
out=${2:-${name}-unTMR_clk.xdl}      

# Warn user if output file already exists.
if [ -e $out ]; then
    echo "Warning: output file $out already exists."
fi 

#
# Handoff processing to the perl script
#
perl $unTMR_clk_pl $in > $out
