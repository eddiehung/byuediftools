#!/bin/bash

################################################################################
# 
#  Copyright (c) 2008 Brigham Young University
#  
#  This file is part of the BYU EDIF Tools.
#  
#  BYU EDIF Tools is free software: you may redistribute it and/or modify it
#  under the terms of the GNU General Public License as published by the Free
#  Software Foundation, either version 2 of the License, or (at your option) any
#  later version.
#  
#  BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
#  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
#  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
#  details.
#  
#  A copy of the GNU General Public License is included with the BYU EDIF Tools.
#  It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
#  license at <http://www.gnu.org/licenses/>.
################################################################################

#####################################################################
# Regression test bash script
#
# This script is used to test for non-deterministic behavior in the
# BYU EDIF Tools JEdif toolchain.
#
# This script runs each of the test designs (listed in "files")
# through each of the tools in the JEdif Toolchain (JEdifBuild,
# JEdifNetList, JEdifSterilize, JEdifCutset, etc.). At each possible
# stopping point, the script produces an output file. 
#
# This script assumes that golden copies of each of these files has
# previously been created. Thus, the two copies of each design can
# be compared (using the `diff' utility) for any differences.  Note
# that the "timeStamp" is removed from all .edf files.
#
# Outline:
#   1. Set up all the variables and options
#   2. Checkout SVN source and build a new JAR to test against
#   3. For each design:
#      A. Run each of the tools in the tool chain and output to test directory
#   4. Strip the timeStamp from all .edf files
#   5. Print results to screen and to log files
#   6. Report success or failure (send e-mail on failure)
# 
# Author: James Carroll <jcarroll@byu.net>
# $Id$
# 
#####################################################################

#
# 1. Setup variables and command-line options
#
files="one_counter.edf lc2.edf shift_reg.edf counters128.edf testCountMult.edf synth_th1_slaac.edf"

email="youremailaddress"

java_opts="-Xms512M -Xmx1024M -cp "

build=" edu.byu.ece.edif.jedif.JEdifBuild "
netlist=" edu.byu.ece.edif.jedif.JEdifNetlist"
sterilize=" edu.byu.ece.edif.jedif.JEdifSterilize"
TMRanalysis=" edu.byu.ece.edif.jedif.JEdifTMRAnalysis"
cutset=" edu.byu.ece.edif.jedif.JEdifCutset"
tmr=" edu.byu.ece.edif.jedif.JEdifTMR"

sterilize_opts="--remove_fmaps"

# Here, choose between the jar file and the working copy of the code.
jar="./byuediftools.jar "
workspace="./trunk:/fpga2/jars/JSAP-2.1.jar "
source=$workspace

# Output directories
golden_dir=golden
test_dir=test
mkdir -p $test_dir
rm -f $test_dir/*

#
#   2. Checkout SVN source and build a new JAR to test against
#
echo -n "Checking out latest source from SourceForge..."
if [ -d trunk ]; then
    cd trunk; svn update; cd ..
    echo
else
    svn checkout http://byuediftools.svn.sourceforge.net/svnroot/byuediftools/trunk
fi
echo -n "Building from source..."
find . -name '*.class' -print | xargs rm
find . -name '*.java' -print | xargs javac -cp /fpga2/jars/JSAP-latest.jar:/fpga2/jars/JHDL.jar &> java_build.log
# Check for error in the build
if [ "$?" -eq 0 ]; then
  echo "Java build succeeded"
else
  echo "Java build failed!"
  cat java_build.log | /bin/mail -s "EDIF Regression Test FAILED due to Java build error" $email
  exit
fi
echo "done."
#echo -n "Creating new EDIF jar for testing..."
#source makeEDIFjar.sh
#echo "done."


#
# 3. Process each design in "files"
#
for infile in $files; do 
    # Output file names
    design=${infile%.edf}
    build_out=${design}.build.jedif
    sterilize_out=${design}.sterilize.jedif
    TMRanalysis_ptmr_out=${design}.ptmr
    TMRanalysis_iob_out=${design}.iob
    cutset_out=${design}.cutset.ptmr
    tmr_out=${design}.tmr.jedif
    netlist_build_out=${design}.build.edf
    netlist_sterilize_out=${design}.sterilize.edf
    netlist_tmr_out=${design}.tmr.edf


# Process each file with the current build of the code for testing
# FIXME: Do not modify the following block of code; rather, create a bash function to encapsulate this!
dir=$test_dir
echo "
build.sh: Workspace JEdif toolchain"
    java $java_opts $source $build source/$infile -o ${dir}/${build_out} 
    java $java_opts $source $netlist ${dir}/${build_out} -o ${dir}/${netlist_build_out} 

    java $java_opts $source $sterilize ${dir}/${build_out} $sterilize_opts -o ${dir}/${sterilize_out}
    java $java_opts $source $netlist ${dir}/${sterilize_out} -o ${dir}/${netlist_sterilize_out} 

    java $java_opts $source $TMRanalysis ${dir}/${sterilize_out} -o ${dir}/${TMRanalysis_ptmr_out} --iob_output ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $cutset ${dir}/${sterilize_out} -o ${dir}/${cutset_out} --ptmr ${dir}/${TMRanalysis_ptmr_out} --iob_input ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $tmr ${dir}/${sterilize_out} -o ${dir}/${tmr_out} --ptmr ${dir}/${cutset_out} 
    java $java_opts $source $netlist ${dir}/${tmr_out} -o ${dir}/${netlist_tmr_out}

done

#
# 4. Remove the timeStamp and tool version number from all EDIF files
#
sed -i 's/timeStamp.*//' $test_dir/*edf
sed -i 's/JEdifNetlist.*version.*//' $test_dir/*edf

# 
# 5. Print results: first detailed, and second summary
#
#echo '    ========================================================================  '
#echo '  ((                    test_results: detailed results                         ))'
#echo '    ========================================================================  '

diff -rs --exclude="*.jedif" $golden_dir $test_dir > test_results.detailed.log
#diff -rs $golden_dir $test_dir

#echo '    ========================================================================  '
#echo '  ((                    test_results: summarized results                      ))'
#echo '    ========================================================================  '

diff -qrs --exclude="*.jedif" $golden_dir $test_dir > test_results.summary.log
#diff -qrs $golden_dir $test_dir

#
# 6. Report success or failure
#
if [ "$?" -eq 0 ]; then
  echo "Regression test passed!"
else
  echo "Regression test failed!"
  cat test_results.summary.log | /bin/mail -s "EDIF Regression Test FAILED" $email
fi

# All done!
