#! /bin/sh
#rm AST*
#c:/usr/local/java/javacc-3.0/bin/jjtree vhdl.jjt
#c:/usr/local/java/javacc-3.0/bin/javacc vhdl.jj
#javac *.java

ROOT = ../../../..


include ${ROOT}/byucc/edif/config.mk

EXTRAKRUD = AST* VhdlParser*.java Token*.java ParseException.java SimpleCharStream.java Node.java JJTVhdlParserState.java vhdl.jj XilinxGenLib.java

#CLASSPATH definition found in common.mk

#JJTREE=${JAVACC_PATH}/bin/jjtree
#JAVACC=${JAVACC_PATH}/bin/javacc


JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

all: XilinxLibrary.class jclass

run: Vhdl.class unisim_VCOMP.vhd 
	CLASSPATH="${CLASSPATH}" ${JAVA} byucc.edif.libraries.xilinx.Vhdl unisim_VCOMP.vhd
	CLASSPATH="${CLASSPATH}" ${JAVAC} XilinxGenLib.java
	CLASSPATH="${CLASSPATH}" ${JAVAC} XilinxLibrary.java

XilinxLibrary.class: XilinxGenLib.java
	CLASSPATH="${CLASSPATH}" ${JAVAC} XilinxGenLib.java
	CLASSPATH="${CLASSPATH}" ${JAVAC} XilinxLibrary.java

XilinxGenLib.java: Vhdl.class unisim_VCOMP.vhd Vhdl2JavaVisitor.class
	CLASSPATH="${CLASSPATH}" ${JAVA} byucc.edif.libraries.xilinx.Vhdl unisim_VCOMP.vhd

Vhdl.class: vhdl.jj Vhdl.java
	CLASSPATH="${CLASSPATH}" ${JAVAC} Vhdl.java

vhdl.jj: vhdl.jjt
	${JJTREE} vhdl.jjt
	$(JAVACC) vhdl.jj

# comment this out so that 'make eclipse' will work properly - this breaks
# the old way of building (ie, before we had eclipse)
include ${ROOT}/byucc/edif/common.mk
