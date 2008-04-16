ROOT = ../..
#DIRS = analysis javacc jhdl tmr xilinx
#DIRCRUD = docs

include ${ROOT}/byucc/edif/config.mk

# CLASSPATH definition found in common.mk

JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

EDIFSRCS = byucc/edif/*.java byucc/edif/javacc/*.java
EDIFCLASS = ${EDIFSRCS:%.java=%.class}

EDIFPARSERSRCS = byucc/edif/*.java byucc/edif/javacc/*.java \
	byucc/edif/tools/merge/*.java
EDIFPARSERCLASS = ${EDIFPARSERSRCS:%.java=%.class}

EDIFXILINXSRCS = byucc/edif/*.java byucc/edif/javacc/*.java \
	byucc/edif/libraries/xilinx/*.java \
	byucc/edif/libraries/xilinx/test/*.java \
	byucc/edif/tools/merge/*.java
EDIFXILINXCLASS = ${EDIFXILINXSRCS:%.java=%.class}

ISISRCS = byucc/edif/*.java byucc/edif/javacc/*.java \
	byucc/edif/jhdl/*.java byucc/edif/libraries/xilinx/*.java \
	byucc/edif/libraries/xilinx/test/*.java \
	byucc/edif/tools/merge/*.java
ISICLASS = ${ISISRCS:%.java=%.class}

ISIMAKE = ${ISISRCS:%.java=%makefile} byucc/edif/common.mk byucc/edif/config.mk
EDIFMAKE = ${EDIFSRCS:%.java=%makefile} byucc/edif/common.mk byucc/edif/config.mk
EDIFPARSERMAKE = ${EDIFPARSERSRCS:%.java=%makefile} byucc/edif/common.mk byucc/edif/config.mk
EDIFXILINX = ${EDIFXILINXSRCS:%.java=%makefile} byucc/edif/common.mk byucc/edif/config.mk

README = byucc/edif/README.txt.release
# this ordering is important
all: javaccdir xilinxdir graphdir jclass analysisdir tmrdir jhdldir replacedir powerdir

eclipse:
	echo making Eclipse
	cp .classpath ../../
	(cd libraries/xilinx; make XilinxGenLib.java)
	(cd javacc; make)

buildall: xilinxdir javaccdir jclass jhdldir isijar EDIFonly EDIFparser EDIFxilinx docsall

isijar: xilinxdir javaccdir jclass jhdldir
	cd ../..;jar -cvf edif.jar ${ISISRCS} ${ISICLASS} byucc/edif/doc ${README} ${ISIMAKE}
	mv ../../edif.jar .
	cd ../..;jar -cvf ediflite.jar ${ISICLASS}
	mv ../../ediflite.jar .

EDIFonly: xilinxdir javaccdir jclass jhdldir
	cd ../..;jar -cvf edifonly.jar ${EDIFSRCS} ${EDIFCLASS} byucc/edif/doc ${README} ${EDIFMAKE}
	mv ../../edifonly.jar .
	cd ../..;jar -cvf edifonlylite.jar ${EDIFCLASS}
	mv ../../edifonlylite.jar .

EDIFparser: xilinxdir javaccdir jclass jhdldir
	cd ../..;jar -cvf edifparser.jar ${EDIFPARSERSRCS} ${EDIFPARSERCLASS} byucc/edif/doc ${README} ${EDIFPARSERMAKE}
	mv ../../edifparser.jar .
	cd ../..;jar -cvf edifparserlite.jar ${EDIFPARSERCLASS}
	mv ../../edifparserlite.jar .

EDIFxilinx: xilinxdir javaccdir jclass jhdldir
	cd ../..;jar -cvf edifxilinx.jar ${EDIFXILINXSRCS} ${EDIFXILINXCLASS} byucc/edif/doc ${README} ${EDIFXILINXMAKE}
	mv ../../edifxilinx.jar .
	cd ../..;jar -cvf edifxilinxlite.jar ${EDIFXILINXCLASS}
	mv ../../edifxilinxlite.jar .

javaccdir: #jclass
	echo making javaccdir
	(cd javacc; make)

xilinxdir: #javaccdir #jclass
	echo making xilinxdir
	(cd libraries/xilinx; make)

jhdldir: jclass javaccdir xilinxdir
	echo making jhdldir
	(cd jhdl; make)

analysisdir: jclass javaccdir xilinxdir
	echo making analysisdir
	(cd analysis; make)

tmrdir: jclass analysisdir javaccdir xilinxdir
	echo making tmrdir
	(cd tools/tmr; make -f makefile)

graphdir: jclass javaccdir xilinxdir
	echo making graphdir
	(cd graph; make)

replacedir: jclass javaccdir xilinxdir jhdldir
	echo making replacedir
	(cd tools/LUTReplace/logicLutRam/Replace; make)

powerdir: jclass javaccdir xilinxdir graphdir
	(cd tools/power; make)

deepclean: clean
	echo making deepclean
	(cd javacc; make clean)
	(cd libraries/xilinx; make clean)
	(cd jhdl; make clean)
	(cd analysis; make clean)
	(cd tools/tmr; make -f makefile clean)
	(cd graph; make clean)
	(cd tools/LUTReplace/logicLutRam/Replace; make deepclean)
	(cd tools/power; make clean)

include ${ROOT}/byucc/edif/common.mk
