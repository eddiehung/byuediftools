ROOT = ../../../../../..

include ${ROOT}/byucc/edif/config.mk

all: jclass testdir

jarcheck:
ifeq ($(JHDL_JAR),no)
	echo
	echo Error: JHDL.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-jhdl option
	exit 1
endif

#CLASSPATH defined in common.mk


JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

run: jclass testdir

testdir: 
	(cd test; make)

deepclean: clean
	(cd test; make clean)

include ${ROOT}/byucc/edif/common.mk

