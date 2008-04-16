#!/bin/bash

######################################################################
# $Id$
#
# Check if there is only one *.edf file in the current directory. If
# so, assume it is the design we want and print it to stdout. Used in
# map.sh, populate.sh, etc.
#
# Original Author: James Carroll <jcarroll@byu.net>
#
version="$Revision $"
date="$Date $"
######################################################################

function guessDesign() {
    count=$(ls *.edf | wc -l)
    if [ $count = 1 ]; then
	echo $(basename $(ls *.edf) .edf)
    fi
}
