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
# stopping point, the script produces an output file. The script
# processes each of the input files twice, outputing the results to
# two separate directories.  Thus, the two copies of each design can
# be compared (using the `diff' utility) for any differences.  Note
# that the "timeStamp" is removed from all .edf files.
#
# Outline:
#   1. Set up all the variables and options
#   2. For each design:
#      A. Run each of the tools in the tool chain and output to dir_A
#      B. Run each of the tools in the tool chain and output to dir_B
#   3. Strip the timeStamp from all .edf files
#   4. Print results to screen and to log files
# 
# Author: James Carroll <jcarroll@byu.net>
# $Id$
# 
#####################################################################

#
# 1. Setup variables and command-line options
#
files="one_counter.edf lc2.edf shift_reg.edf counters128.edf testCountMult.edf"
#files="synth_th1_slaac.edf"

java_opts="-Xms512M -Xmx1024M -cp "

build=" edu.byu.edif.jedif.JEdifBuild "
netlist=" edu.byu.edif.jedif.JEdifNetlist"
sterilize=" edu.byu.edif.jedif.JEdifSterilize"
TMRanalysis=" edu.byu.edif.jedif.JEdifTMRAnalysis"
cutset=" edu.byu.edif.jedif.JEdifCutset"
tmr=" edu.byu.edif.jedif.JEdifTMR"

# Two versions of the source code: jar file and working copy of the code.
jar="/fpga1/users/jfc33/workspace/BLTmr.jar "
workspace="/fpga1/users/jfc33/workspace/ediftools:/fpga2/jars/JSAP-2.1.jar "

# Output directories
dir_A=dir_A
dir_B=dir_B

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


# A. Process each file with the code from the workspace
# FIXME: Do not modify the following block of code; rather, create a bash function to encapsulate this!
source=$workspace
dir=$dir_A
echo "
build.sh: Workspace JEdif toolchain"
    java $java_opts $source $build synth/$infile -o ${dir}/${build_out} 
    java $java_opts $source $netlist ${dir}/${build_out} -o ${dir}/${netlist_build_out} 

    java $java_opts $source $sterilize ${dir}/${build_out} -o ${dir}/${sterilize_out}
    java $java_opts $source $netlist ${dir}/${sterilize_out} -o ${dir}/${netlist_sterilize_out} 

    java $java_opts $source $TMRanalysis ${dir}/${sterilize_out} -o ${dir}/${TMRanalysis_ptmr_out} --iob_output ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $cutset ${dir}/${sterilize_out} -o ${dir}/${cutset_out} --ptmr ${dir}/${TMRanalysis_ptmr_out} --iob_input ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $tmr ${dir}/${sterilize_out} -o ${dir}/${tmr_out} --ptmr ${dir}/${cutset_out} 
    java $java_opts $source $netlist ${dir}/${tmr_out} -o ${dir}/${netlist_tmr_out}


# B. Process each file with the code from the jarfile
# FIXME: Do not modify the following block of code; rather, create a bash function to encapsulate this!
source=$jar
dir=$dir_B
echo "
build.sh: Jar JEdif toolchain"
    java $java_opts $source $build synth/$infile -o ${dir}/${build_out} 
    java $java_opts $source $netlist ${dir}/${build_out} -o ${dir}/${netlist_build_out} 

    java $java_opts $source $sterilize ${dir}/${build_out} -o ${dir}/${sterilize_out}
    java $java_opts $source $netlist ${dir}/${sterilize_out} -o ${dir}/${netlist_sterilize_out} 

    java $java_opts $source $TMRanalysis ${dir}/${sterilize_out} -o ${dir}/${TMRanalysis_ptmr_out} --iob_output ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $cutset ${dir}/${sterilize_out} -o ${dir}/${cutset_out} --ptmr ${dir}/${TMRanalysis_ptmr_out} --iob_input ${dir}/${TMRanalysis_iob_out} 
    java $java_opts $source $tmr ${dir}/${sterilize_out} -o ${dir}/${tmr_out} --ptmr ${dir}/${cutset_out} 
    java $java_opts $source $netlist ${dir}/${tmr_out} -o ${dir}/${netlist_tmr_out}

done

#
# 3. Remove the timeStamp from all EDIF files
#
strip="sed -i 's/timeStamp.*//' "
strip $dir_A/*edf $dir_B/*edf

# 
# 4. Print results: first detailed, and second summary
#
echo '    ========================================================================  '
echo '  ((                    Build_all: detailed results                         ))'
echo '    ========================================================================  '

diff -rs $dir_A $dir_B > build_all.detailed.log
diff -rs $dir_A $dir_B

echo '    ========================================================================  '
echo '  ((                    Build_all: summarized results                      ))'
echo '    ========================================================================  '

diff -qrs $dir_A $dir_B > build_all.summary.log
diff -qrs $dir_A $dir_B

# All done!
