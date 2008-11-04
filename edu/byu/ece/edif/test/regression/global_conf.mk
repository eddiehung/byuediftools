#####################################################################
# Global Configuration file to accompany makefile regression test.
#
# User *must* edit this file to set environment variables
#
# Author: Derrick Gibelyou, James Carroll <jcarroll@byu.net>
#
# $Id$
#####################################################################

# Java runtime environment command (usually just `java') 
JAVA=java

# Your home folder (used for _______)
HOME="/fpga1/users/dsgib/ "

# byuediftools path, jar files
CLASSPATH="-cp $(HOME)/workspace/sf_edif/:$(HOME)/jars/JSAP.jar "

# Parameter options for JRE 
JAVA_OPTS="-Xmx512M "

# Modelsim executable director (usually ends with `/bin/')
MODELSIM="/fpga2/modeltech/bin/ "

