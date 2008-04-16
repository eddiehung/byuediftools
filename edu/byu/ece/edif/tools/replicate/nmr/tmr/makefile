# What does this makefile do? Please add a comment block here to 
# distinguish it from "Makefile" in this directory. -jFc
ROOT = ../../../..
GLPKROOT = ${ROOT}/byucc/edif/glpk
#TEST_DESIGN = ../edifsrc/state_machine.edf
#TEST_DESIGN = ../edifsrc/signalGen.edf
#TEST_DESIGN = ../edifsrc/fpga_a.edf
TEST_DESIGN = ../../edifsrc/test_slaac/xp_test_tmr.edn
TRIPLEINPORTS = true
TRIPLEOUTPORTS = true
CLOCK = singleClock=clk
#CLOCK = tripleClock=clk

include ${ROOT}/byucc/edif/config.mk

all: jclass

jarcheck:
ifeq ($(PTOLEMY_JAR),no)
	echo
	echo Error: ptolemy.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-ptolemy option
	exit 1
endif
ifeq ($(JHDL_JAR),no)
	echo
	echo Error: jhdl.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-jhdl option
	exit 1
endif

CLASSPATH=${JHDL_JAR}${CLASSPATHSEPARATOR}${ROOT}${CLASSPATHSEPARATOR}${PTOLEMY_JAR}${CLASSPATHSEPARATOR}${GLPKJAR}

JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

run: jclass
	CLASSPATH="${CLASSPATH}" java -Xmx512M -Djava.library.path=${GLPKROOT}/jni/c byucc.edif.tmr.XilinxTMR Virtex ${TEST_DESIGN} $(TRIPLEINPORTS) $(TRIPLEOUTPORTS) $(CLOCK)

include ${ROOT}/byucc/edif/common.mk

