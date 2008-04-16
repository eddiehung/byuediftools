ROOT = ../../..

include ${ROOT}/byucc/edif/config.mk

all: jclass

jarcheck:
ifeq ($(JHDL_JAR),no)
	echo
	echo Error: JHDL.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-jhdl option
	exit 1
endif


#CLASSPATH definition found in common.mk

JSRCS = ${wildcard *.java}
JCLASS = ${JSRCS:%.java=%.class}

run: all
	@CLASSPATH="${CLASSPATH}"\
		${JAVA} byucc.edif.jhdl.BuildWrapper -t Virtex2 ../edifsrc/signalGen.edf

include ${ROOT}/byucc/edif/common.mk

