#!/bin/bash
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
# This script generates the "golden" files for checking against. A
# second script (regression_test.sh) generates a new set of test
# files each time it is run and compares them against these golden
# files. Thus, the two copies of each design can
# be compared (using the `diff' utility) for any differences.  Note
# that the "timeStamp" is removed from all .edf files.
#
# This script is designed to create a new set of regression test
# files and to make sure everything is working correctly
#
# Outline:
#   1. Set up all the variables and options
#   2. For each design:
#      A. Run each of the tools in the tool chain and output to golden directory
#   3. Strip the timeStamp from all .edf files
# 
# Author: James Carroll <jcarroll@byu.net>
# $Id: regression_test.sh 4 2008-04-16 22:31:52Z mrspud $
# 
#####################################################################

#
# 1. Setup variables and command-line options
#
files="one_counter.edf lc2.edf shift_reg.edf counters128.edf testCountMult.edf"
#files="synth_th1_slaac.edf"

java_opts="-Xms512M -Xmx1024M -cp "

build=" edu.byu.ece.edif.jedif.JEdifBuild "
netlist=" edu.byu.ece.edif.jedif.JEdifNetlist"
sterilize=" edu.byu.ece.edif.jedif.JEdifSterilize"
TMRanalysis=" edu.byu.ece.edif.jedif.JEdifTMRAnalysis"
cutset=" edu.byu.ece.edif.jedif.JEdifCutset"
tmr=" edu.byu.ece.edif.jedif.JEdifTMR"

# Here, choose between the jar file and the working copy of the code.
jar="/home/brian/workspace/BLTMR.jar "
workspace="/home/brian/workspace/byuediftools:/fpga2/jars/JSAP-2.1.jar "
source=$jar

# Output directories
golden_dir=golden
mkdir -p $golden_dir

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


# Process each file with the golden code
# FIXME: Do not modify the following block of code; rather, create a bash function to encapsulate this!
dir=$golden_dir
echo "
build.sh: Workspace JEdif toolchain"
    java $java_opts $source $build source/$infile -o ${dir}/${build_out} 
    java $java_opts $source $netlist ${dir}/${build_out} -o ${dir}/${netlist_build_out} 

    java $java_opts $source $sterilize ${dir}/${build_out} -o ${dir}/${sterilize_out}
    java $java_opts $source $netlist ${dir}/${sterilize_out} -o ${dir}/${netlist_sterilize_out} 

    java $java_opts $source $TMRanalysis ${dir}/${sterilize_out} -o ${dir}/${TMRanalysis_ptmr_out} --iob_output ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $cutset ${dir}/${sterilize_out} -o ${dir}/${cutset_out} --ptmr ${dir}/${TMRanalysis_ptmr_out} --iob_input ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $tmr ${dir}/${sterilize_out} -o ${dir}/${tmr_out} --ptmr ${dir}/${cutset_out} 
    java $java_opts $source $netlist ${dir}/${tmr_out} -o ${dir}/${netlist_tmr_out}

done

#
# 3. Remove the timeStamp and tool version number from all EDIF files
#
sed -i 's/timeStamp.*//' $golden_dir/*edf
sed -i 's/JEdifNetlist.*version.*//' $golden_dir/*edf


# All done!
echo "
Creation of golden files for regression testing is complete."

echo "Created golden files for regression testing of EDIF tools on " `date` > $golden_dir/log.txt
