ROOT = ../../..
TESTEDIF=testGraph_Virtex.edn

include ${ROOT}/byucc/edif/config.mk

all: jclass

jarcheck:
ifeq ($(PTOLEMY_JAR),no)
	echo
	echo Error: ptolemy.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-ptolemy option
	exit 1
endif

EXTRAKRUD=*.dot

#CLASSPATH definition found in common.mk

JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

all: jclass

testgraph: jclass
	CLASSPATH="${CLASSPATH}" java byucc.edif.graph.EdifCellTopologyGraph ${TESTEDIF}


include ${ROOT}/byucc/edif/common.mk

