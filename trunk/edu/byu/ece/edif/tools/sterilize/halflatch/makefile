ROOT = ../../../..
#TEST_DESIGN = /fpga1/users/bhp7/jhdl/edif_test/ground_pass.edn
#TEST_DESIGN = /fpga1/users/bhp7/vhdl/fib/makeDir/testFib.edf
#TEST_DESIGN = /fpga1/users/bhp7/vhdl/lc2/final/lc2.edf
#TEST_DESIGN = /fpga1/users/bhp7/jhdl/synth/synthetic_selective_mini_xp1.edn
#TEST_DESIGN = /fpga1/users/bhp7/hlrtest/fib/testFib.edf
#TEST_DESIGN = /fpga1/users/bhp7/hlrtest/lc2/lc2test.edf
TEST_DESIGN = /fpga1/users/bhp7/hlrtest/synth/synthetic.edn
#TEST_DESIGN = /fpga1/users/bhp7/hlrtest/des/testDES.edn
MODE = -a     # -c for conservative, -a for aggressive

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

#CLASSPATH defined in common.mk

JSRCS = ${wildcard *.java}
JCLASS = $(JSRCS:%.java=%.class)

all: $(JCLASS)

run: jclass
	CLASSPATH="${CLASSPATH}" java -Xmx512M -Djava.library.path=../glpk/jni/c byucc.edif.tools.halflatch.HalfLatchRemoval ${TEST_DESIGN} ${MODE}

include ${ROOT}/byucc/edif/common.mk
