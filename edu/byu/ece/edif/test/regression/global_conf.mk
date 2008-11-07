
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

