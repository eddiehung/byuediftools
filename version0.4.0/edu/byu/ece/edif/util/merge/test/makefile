ROOT = ../../../../..

EXE=byucc.edif.tools.merge.EdifMergeParser

include ${ROOT}/byucc/edif/config.mk
include ${ROOT}/byucc/edif/common.mk


test:
	java -classpath ${ROOT} ${EXE} ${ROOT}/byucc/edif/edifsrc/lanl/ssra/SSRA.edf -L ${ROOT}/byucc/edif/edifsrc/lanl/ssra/netlist.tmp -o output.edf
	diff output.edf merge.edf

