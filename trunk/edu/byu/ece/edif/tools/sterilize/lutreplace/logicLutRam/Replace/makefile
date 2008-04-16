ROOT = ../../../../../..

include ${ROOT}/byucc/edif/config.mk

all: ramdir srldir jclass

jarcheck:
ifeq ($(JHDL_JAR),no)
	echo
	echo Error: JHDL.jar not present. If you wish to compile files in this directory, rerun ../configure with the --with-jhdl option
	exit 1
endif

#CLASSPATH defined in common.mk


JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

run: ramdir srldir jclass
	CLASSPATH="${CLASSPATH}" \
		java -Xmx512M byucc.edif.tools.LUTReplace.logicLutRam.Replace.replaceLutRam -design ${ROOT}/byucc/edif/tools/LUTReplace/logicLutRam/Replace/alljhdltest.edn

ramdir: 
	(cd ../RAM; make)

srldir:
	(cd ../SRL; make)

deepclean: clean
	(cd ../RAM; make deepclean)
	(cd ../SRL; make deepclean)

include ${ROOT}/byucc/edif/common.mk

