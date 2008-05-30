ROOT = ../../../..

EXE=byucc.edif.jhdl.test.JHDLTests

include ${ROOT}/byucc/edif/config.mk

CLASSPATH=${CLASSPATHALL}

JSRCS = ${wildcard *.java}
JCLASS = ${JSRCS:%.java=%.class}

#JHDLTests.class: JHDLTests.java

test: JHDLTests.class
	${JAVA} -classpath ${CLASSPATH} ${EXE}

include ${ROOT}/byucc/edif/common.mk



