#!/bin/sh

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
# Regression test bash script using makefiles
#
# This script is used to regression test the
# BYU EDIF Tools, especially the JEdif toolchain.
#
# This script runs each of the test designs 
# through each of the tools in the JEdif Toolchain (JEdifBuild,
# JEdifNetList, JEdifSterilize, JEdifCutset, etc.). At each possible
# stopping point, the script produces an output file. 
#
# The test designs are expected to be in a subfolder of "source"
# along with an "options.mk" file that specifies the options to send
# to the JEdif tools
#
# This script has the ability to create golden copies.
# Using the test and golden copy, we can quickly and easily find any
# differences by simply using the `diff' utility. 
#
# Outline:
#   0. Set up all the variables and options (REQUIRED)
#      A. Classpath
#      B. Build type 
#   1. Get a list of the files to test for the source directory
#   2. Checkout SVN source and build a new JAR to test against
#   3. Build with java, check for errors
#   4. For each design:
#      A. Run each of the tools in the tool chain 
#      B. Output to test directory
#   5. Strip the timeStamp from all .edf files
#   6. Print results to screen and to log files
#   7. Report success or failure (send e-mail on failure)
# 
# Authors: James Carroll <jcarroll@byu.net>, Derrick Gibelyou
# $Id$
# 
#####################################################################

# Enables running the script from other folders
script_dir=`pwd`

#####################################################################
# You MUST set the values below.  Defaults will NOT "just work".
# Each variable lists sample defaults
#####################################################################

#
# Email address for bad build notification
#

#user=`whoami`
#email="${user}@localhost"
email=jcarroll@byu.net
#email=user@domain.com

#
# What type of build do you want?
#

# golden: create golden files in the golden directory from the jar file
# local: create test files from local source code
# repository: create test files from the repository
#build=golden
#build=local
build=repository

#
# Classpath
#

# Dependencies
# JSAP: Java Simple Argument Parser (martiansoftware.com/jsap/)
jsap_jar="/fpga2/jars/JSAP-latest.jar"
#jsap_jar=

# JHDL (jhdl.org)
jhdl_jar="/fpga2/jars/JHDL.jar"
#jhdl_jar=


# Chose golden jar.
#jar="${HOME}/jars/byuediftools-0.4.0.jar "
jar="/fpga2/jars/byuediftools-0.4.0.jar:$jsap_jar"

#workspace="${HOME}/workspace/sf_edif/:/fpga2/jars/JSAP-2.1.jar "
workspace="~/Documents/career/fpga/workspace/byuediftools/:$jsap_jar"

repository="${script_dir}/trunk:$jsap_jar"
#repository=


#####################################################################
# Only change the following if you know what you are doing.
#####################################################################

test_dir="./test"
golden_dir="./golden"
source_dir="./source"
rep_dir="./trunk"

globals=" GLOBALS=${script_dir}/global_conf.mk "
makefile=" MAKEFILE=${script_dir}/makefile "
make_cmd=" make -f options.mk $globals $makefile ";

#
# 1. Get a list of the files to test for the source directory
#
wd=`pwd`;
cd $source_dir
folders=`ls`;
cd $wd;

echo
if [ $build = "golden" ]; then
	echo " Creating Golden tmr-ed files"
	classpath=$jar
	build_dir=$golden_dir
elif [ $build = "local" ]; then
	echo " Testing your local code"
	classpath=$workspace
	build_dir=$test_dir
elif [ $build = "repository" ]; then
	echo " Testing the Repository code"
	classpath=$repository
	build_dir=$test_dir
else
	echo "You didn't specify the correct build.\n Please set variable 'build' to one of {golden, local, or repository}."
	exit 1;
fi
echo
echo "trying these designs:"
echo "$folders"
echo
echo

mkdir -p $build_dir
rm -rf $build_dir/*

if [ $build = "repository" ]; then
#
# 2. Check out the current repository
#
sleep 1
echo -n "..."
if [ -d trunk ]; then
    echo -n "Updating source from SourceForge..."
    cd trunk; svn update; cd ..
    echo
else
    echo -n "Checking out latest source from SourceForge..."
    svn checkout http://byuediftools.svn.sourceforge.net/svnroot/byuediftools/trunk
fi

#
# 3. Build with java, check for errors
#
echo -n "Building from source..."
find . -name '*.class' -print | xargs rm
mv java_build.log java_build.log.1
for file in `find . -name '*.java' -print`; do javac -cp ./trunk:/fpga2/jars/JSAP-latest.jar:/fpga2/jars/JHDL.jar $file &>> java_build.log; done
# Check for error in the build
if [ "$?" -eq 0 ]; then
  echo "Java build succeeded"
else
  echo "Java build failed!"
  cat java_build.log | /bin/mail -s "EDIF Regression Test FAILED due to Java build error" $email
  exit
fi
echo "done."

fi #if we are not making the golden copy


#
# 4. Copy the edif files to the build directory, and tmr all of them
#
for folder in $folders; do
	#echo $folder;
	#cd $build_dir;
	cp -r $source_dir/$folder $build_dir;
	cd $build_dir/$folder;
	echo `pwd`;
	#make -f options.mk;
	echo "$make_cmd CLASSPATH=-cp ${classpath} "
	$make_cmd CLASSPATH="-cp ${classpath}" 
	#make -f options.mk CLASSPATH="-cp svn_trunk:JSAP"
	$make_cmd clean;
	cd ../..;
	#exit;
done;


#
# 5. Remove the timeStamp and tool version number from all EDIF files
#
sed -i 's/timeStamp.*//' $build_dir/*/*edf
sed -i 's/JEdifNetlist.*version.*//' $build_dir/*/*edf

if [ $build != "golden" ]; then
# 
# 6. Print results: first detailed, and second summary
#
#echo '    ========================================================================  '
#echo '  ((                    test_results: detailed results                         ))'
#echo '    ========================================================================  '

diff -rs --exclude="*.log" $golden_dir $test_dir > test_results.detailed.log
#diff -rs $golden_dir $test_dir

#echo '    ========================================================================  '
#echo '  ((                    test_results: summarized results                      ))'
#echo '    ========================================================================  '

diff -qrs --exclude="*.log" $golden_dir $test_dir > test_results.summary.log
#diff -qrs $golden_dir $test_dir

#
# 7. Report success or failure
#
if [ "$?" -eq 0 ]; then
  echo "Regression test passed!"
else
  echo "Regression test failed!"
	#diff including logs
	diff -qrs $golden_dir $test_dir > test_results_logs.summary.log
	diff -rs  $golden_dir $test_dir > test_results_logs.detailed.log
  cat test_results_logs.summary.log | /bin/mail -s "EDIF Regression Test FAILED" $email
fi

fi #test for building golden or repository

# All done!

