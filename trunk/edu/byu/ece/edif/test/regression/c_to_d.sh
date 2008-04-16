#!/bin/bash
######################################################################
# Simple script used to exercise the "C to D" error
#
# The "C to D" problem is this:
# 
#   1. Take your favorite EDIF file
#   2. Parse it with JEdifBuild, creating B.jedif
#   3. Output B.jedif to C.edf through JEdifNetlist
#   4. Parse C.edf again with JEdifBuild, creating D.edf 
#   5. ... if you get here, you have solved the C to D error.
#
# Thus, the C to D error is that which occurs when trying to convert
# C.edf to D.jedif.
#
# $Id$
# Author: James F. Carroll <jcarroll@byu.net>
#
######################################################################

# Use the code from the repository or the jarfile
source=repo
#source=jar

if [ $source == "jar" ]; then
	java_opts="-Xms512M -Xmx1024M -cp BLTmr.jar "
elif [ $source == "repo" ]; then
	java_opts="-Xms512M -Xmx1024M -cp /fpga1/users/jfc33/workspace/ediftools:/fpga2/jars/JSAP-2.1.jar "
else
	echo "Error: invalid source option!\n"
fi

build="$java_opts edu.byu.edif.jedif.JEdifBuild"
netlist="$java_opts edu.byu.edif.jedif.JEdifNetlist"

# 1. 
file=one_counter.edf
cp $file A.edf
# 2. 
java $build A.edf -o B.jedif
# 3. 
java $netlist B.jedif -o C.edf
# 4. 
java $build C.edf -o D.jedif
# 5. 
java $netlist D.jedif -o E.edf
# 6 - 9. Keep going!
java $build E.edf -o F.jedif
java $netlist F.jedif -o G.edf
java $build G.edf -o H.jedif
java $netlist H.jedif -o I.edf

# 10 - oo Never stop!

f1=A.edf
f2=B.jedif

while [ 1 ]; do
	java $build $f1 -o $f2
	java $netlist $f2 -o $f1
done
