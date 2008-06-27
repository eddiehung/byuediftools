#!/bin/sh

#####################################################################
# Regression test bash script
#
# This script is used to test for non-deterministic behavior in the
# BYU EDIF Tools JEdif toolchain.
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
# Authors: James Carroll <jcarroll@byu.net>
# $Id: regression_test.sh 85 2008-05-30 16:42:16Z brianpratt $
# 
#####################################################################


user=`whoami`
email="${user}@localhost"


test_dir="./test"
golden_dir="./golden"
source_dir="./source"
rep_dir="./trunk"
script_dir=`pwd`

globals=" GLOBALS=${script_dir}/global_conf.mk "
makefile=" MAKEFILE=${script_dir}/makefile "
make_cmd=" make -f options.mk $globals $makefile ";

build="golden"
#build="local"
#build="repository"

#
# 1. Get a list of the files to test for the source directory
#
wd=`pwd`;
cd $source_dir
folders=`ls`;
cd $wd;


# Chose jar or working copy (golden or repository)
jar="${HOME}/jars/byuediftools-0.4.0.jar "
workspace="${HOME}/workspace/sf_edif/:/fpga2/jars/JSAP-2.1.jar "
repository="${script_dir}/trunk:/fpga2/jars/JSAP-2.1.jar "

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
	echo "You didn't specify the correct build: golden, local, or repository"
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
echo -n "Checking out latest source from SourceForge..."
sleep 5
echo -n "..."
if [ -d trunk ]; then
    cd trunk; svn update; cd ..
    echo
else
    svn checkout http://byuediftools.svn.sourceforge.net/svnroot/byuediftools/trunk
fi

#
# 3. Build with java, check for errors
#
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

