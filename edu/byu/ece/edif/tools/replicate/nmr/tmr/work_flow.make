######################################################################
# $Id: work_flow.make,v 1.4 2007/05/29 14:38:42 brian Exp $
#
# Used to do map and par, as well as create back-annotated VHDL files.
#
# This makefile includes targets to create the xp1 and xp2 bit files
# for use in the SEU simulator on the Slaac board at Brigham Young
# University.  Also included are targets for creating each of the
# following:
#
#  * (No-TMR) .bit file
#  * Back-annotated .vhd file
#  * Timing report .twr file
#  * Power report .pwr file
#
# This makefile should be in the same directory as the design files
# (.edf, .ngo, etc.).
#
# If no design is specified, an attempt is made to guess the name of
# the design, using the name of the current directory. For example,
# my_counter.edf (or my_counter.ngo, or my_counter.ncd, etc.) is
# inside a folder called my_counter. In this case, this makefile can
# be run without specifying the design variable. Examples:
#
# make
# make xp1
# make vhdl
#
# If your directory structure does not follow this pattern, the
# variable DESIGN must be set, either in this file or on the command
# line. Examples:
#
# make DESIGN=<design>
# make xp1 DESIGN=<design>
# make vhdl DESIGN=<design>
#
# where <design> is the root name of your design edif file.
#
# For example, if I had an EDIF file of my design called counter.edf,
# then on the command line I could type any one of the following:
#
# make DESIGN=counter
# make xp1 DESIGN=counter
# make vhdl DESIGN=counter
# 
# Original authors: Keith Morgan, 
#                   James Carroll <jcarroll@byu.net>
######################################################################

# Attempt to get the correct design name from the directory
DESIGN=$(shell basename `pwd`)
PE_DESIGN=$(DESIGN)_xp1
PART=v1000fg680-5
# intstyle must be one of the following: {ise | xflow | silent}
INTSTYLE=ise
#PART=xc2v1000fg456-5

# These are the options passed to the different xilinx tools.
BITGEN_OPTS= -w -g StartupClk:Cclk -g persist:X8 -l -m -g DONE_cycle:6 -intstyle $(INTSTYLE) 
TABLE=1
PAR_OPTS= -ol high -w -t $(TABLE) -intstyle $(INTSTYLE) -nopad
# NOTE: The "-c 99" option tells the map program to compress the mapped design,
# using 99% of the chip rather than 100%. This is necessary for some designs
# in order to allow the half-latch constant LUT (from RadDRC) to be added.
MAP_OPTS=-pr b -p $(PART) $(CLBPACK) -detail -intstyle $(INTSTYLE) #-c 99 #-cm speed

# NOTE: Generally speaking, ngdbuild should not be run with the -aul
# option. It is used here for the synth_th1_slaac design to ensure a
# physical constraint on the rst_bufg instances. However, it shouldn't
# be necessary for future designs. See the Xilinx "Development System
# Reference Guide" (dev.pdf) for more information on ngdbuild options.
# -James Carroll 30 Jun 2006
#NGDBUILD_OPTS=-a -p $(PART) -intstyle $(INTSTYLE) -aul
NGDBUILD_OPTS=-a -p $(PART) -intstyle $(INTSTYLE)

NGDBUILD_LOG=ngdbuild.log
NETGEN_OPTS= -sim -w -ofmt vhdl -intstyle $(INTSTYLE)
TRCE_OPTS= -a -u -v 10 -intstyle $(INTSTYLE)
NCD2XDL_OPTS= -ncd2xdl -nopips
EDIF2NGD_OPTS= -intstyle $(INTSTYLE)
VHDL_DESIGN=root

HL_FIND_OPTS= -fix
RAM=512M

# SEU simulator variables
x0=../x0.bit
x1=$(DESIGN)_xp1-fixed.bit
x2=../$(DESIGN)_xp2.bit
seu_common_opts="-x0 $(x0) -x1 $(x1) -x2 $(x2)"
sensitivity_opts="-o ${sens_output_file} -fixedrand $seed"
# Set options for sensitivity test
seed=0
sensitivity_opts="-o ${sens_output_file} -fixedrand $(seed)"
# Set options for persistence test
reset_style=0
x=0
y=1000
z=10
persistance_opts="-p $(reset_style) $(x) $(y) $(z) $(pers_output_file) -fixedrand $(seed)"
seusim_elf=$(shell which seusim_elf)


#
# Psuedo targets
#

# The default rule is to build a bit-file for both xp1 and xp2.  The xp1
# bitfile should have half-latches removed.
all:	xp1 xp2

# The reason the following two rules are done with a 'call' to make,
# rather than a dependency is so that the variable PE_DESIGN can be
# set (and changed) depending on which processing element you are
# building for.
#
# The call to @$(MAKE) invokes a new instance of this makefile.  The
# next argument is the target to generate and following that can be
# any number of variables you would like to set.)
xp1: 
	$(MAKE) $(PE_DESIGN)-fixed.bit

xp2: 
	$(MAKE) $(DESIGN)_xp2.bit PE_DESIGN=$(DESIGN)_xp2

#
# Additional psuedo targets
#
seu:
	$(MAKE) seu_$(PE_DESIGN)_pers.txt
	$(MAKE) seu_$(PE_DESIGN)_sens.txt

bit:		
	$(MAKE) $(PE_DESIGN).bit

par.ncd:		
	$(MAKE) $(PE_DESIGN)-fixed.par.ncd

xdl:	
	$(MAKE) $(PE_DESIGN).xdl

# Timing report file
twr:	
	$(MAKE) $(DESIGN)_xp1-fixed.par.twr

# Back-annotated VHDL
vhd vhdl:	
	$(MAKE) $(DESIGN)_xp1-fixed.par.vhd

# Power report
pwr:
	$(MAKE) $(DESIGN)_xp1-fixed.pwr

# Simulation file
vcd:	
	$(MAKE) $(DESIGN)_xp1-fixed.vcd


#
# Begin Actual targets
#

# SEU simulator persistence report
seu_$(PE_DESIGN)_pers.txt:	$(DESIGN)_xp1-fixed.bit

# SEU simulator sensitivity report
seu_$(PE_DESIGN)_sens.txt:	$(DESIGN)_xp1-fixed.bit

# Timing report
$(DESIGN)_xp1-fixed.par.twr:	$(DESIGN)_xp1.par.ncd
	trce $(TRCE_OPTS) $(DESIGN)_xp1-fixed.par.ncd $(DESIGN)_xp1.pcf

# Power analysis
$(DESIGN)_xp1-fixed.pwr:	$(DESIGN)_xp1-fixed.vcd # $(DESIGN)_xp1.pcf
	xpwr $(DESIGN)_xp1-fixed.par.ncd $(XPWR_OPTS) -s $(DESIGN)_xp1-fixed.vcd -o $(DESIGN)_xp1-fixed.pwr

# VSim simulation file
$(DESIGN)_xp1-fixed.vcd:	 work/_info
	gunzip -f work/$(VHDL_DESIGN)/*.asm
	vsim -c -do "do $(DESIGN).do $(DESIGN)_xp1-fixed"
	gzip -f work/$(VHDL_DESIGN)/*.asm

# Necessary vlib file
work/_info:	$(DESIGN)_xp1-fixed.par.vhd
	vlib work

# Back-annotated VHDL file
$(DESIGN)_xp1-fixed.par.vhd:	$(DESIGN)_xp1-fixed.par.ncd
	netgen $(NETGEN_OPTS) $(DESIGN)_xp1-fixed.par.ncd

# *********
# This block of rules are for building a bit file with half-latches
# removed.  Note that they depend on the following set of rules from
# .xdl down
# *********

# The bitgen options are specific to the architecture you are using,
# which in this case happens to be virtex.
$(PE_DESIGN)-fixed.bit: $(PE_DESIGN)-fixed.par.ncd
	bitgen $(PE_DESIGN)-fixed.par.ncd $(PE_DESIGN)-fixed.bit $(BITGEN_OPTS)

# When building the xp1 bit-file, this is the second call to the place
# & route command (par). (The reason for the second invocation can be
# understood by reading the comment for the hl_find rule.)
#
# It (par) uses an output file of the original run of par called a
# physical constraints file (pcf) to guide its placement and routing.
# If this is not included, you will likely get errors in the bitgen
# process from the placer putting things where they shouldn't be.
$(PE_DESIGN)-fixed.par.ncd: $(PE_DESIGN)-fixed.ncd
	set XVKMA_CORE_LUT_PACK=TRUE
	set GUIDE_NEW=TRUE
	par $(PAR_OPTS) -gf $(PE_DESIGN).par.ncd $(PE_DESIGN)-fixed.ncd $(PE_DESIGN)-fixed.par.ncd $(PE_DESIGN).pcf

# XDL is used as an intermediate file format which Paul Graham's
# half-latch tool can parse and generate after half-latch removal.  To
# date, the only versions of the xilinx tools which properly handle
# xdl are 5.x (and earlier I believe).
$(PE_DESIGN)-fixed.ncd: $(PE_DESIGN)-fixed.xdl
	xdl -xdl2ncd $(PE_DESIGN)-fixed.xdl $(PE_DESIGN)-fixed.ncd

# hl_find is Paul Graham's half-latch removal tool.  It basically
# creates un-placed power nets to tie to 'constant' values in the
# circuit. Later, place and route (par) will be run again to place and
# route those nets.

# The current version of Paul's HL-removal tool doesn't require the
# domain report file IF the --removeHL option in the BLTmr tool. If
# you didn't use the --removeHL option, then comment the following
# line and uncomment the one after that.
$(PE_DESIGN)-fixed.xdl: $(PE_DESIGN).xdl
	java -Xmx$(RAM) hl_find $(HL_FIND_OPTS) $(PE_DESIGN).xdl 

#$(PE_DESIGN)-fixed.xdl: $(PE_DESIGN).xdl
#	java -Xmx$(RAM) hl_find $(HL_FIND_OPTS) $(DESIGN)_domain_report.txt $(PE_DESIGN).xdl 

# Apply domain segregation
#$(PE_DESIGN)-unTMR_clk.xdl: $(PE_DESIGN).xdl
#	unTMR_clk.sh $(PE_DESIGN).xdl

# (see comment for the first xdl rule above)
$(PE_DESIGN).xdl: $(PE_DESIGN).par.ncd
	xdl $(NCD2XDL_OPTS) $(PE_DESIGN).par.ncd $(PE_DESIGN).xdl

# ********* 
# Rules for building a bit file without half-latch removal (Note that
# some of these rules are used by the half-latch removal rules)
# *********

# (see comment for the first bitgen rule above)
$(PE_DESIGN).bit: $(PE_DESIGN).par.ncd
	bitgen $(PE_DESIGN).par.ncd $(PE_DESIGN).bit $(BITGEN_OPTS)

# Place and route (par) has a series of effort levels which can be
# varied depending on how hard you want the tool to try to place and
# route your design.
#
# Type par at the command line to get a description of the various
# options
$(PE_DESIGN).par.ncd: $(PE_DESIGN).ncd
	set XVKMA_CORE_LUT_PACK=TRUE
	set GUIDE_NEW=TRUE
	par $(PAR_OPTS) $(PE_DESIGN).ncd $(PE_DESIGN).par.ncd

# The map tool has the option of packing flip-flops into input,
# output, or both types of IOBs. Currently, the option is set to use
# both.
$(PE_DESIGN).ncd: $(PE_DESIGN).ngd
	set XVKMA_CORE_LUT_PACK=TRUE
	set GUIDE_NEW=TRUE
	map $(MAP_OPTS) $(PE_DESIGN)

# ngdbuild simply converts your ngo file to yet another xilinx
# propietary format
$(PE_DESIGN).ngd: $(DESIGN).ngo $(PE_DESIGN).ucf
	ngdbuild $(NGDBUILD_OPTS) -uc $(PE_DESIGN).ucf $(DESIGN) $(PE_DESIGN).ngd

# edif2ngd simply converts your edif file to a xilinx propietary
# format
$(DESIGN).ngo: $(DESIGN).edf
	edif2ngd $(EDIF2NGD_OPTS) $(DESIGN).edf

# *********
# Rule to clean up the tool-generated files. Note that this eliminates
# basically everything expect the .edf .ucf, and .log files
# *********
clean:
	rm -f *.alf *.areasrr *.bgn *.bgn *.bit *.bld *.csv *.dly *.drc *.fse *.ll *.lst *.mcs *.mrp *.msk *.nav *.ncd *.ncf *.nga *.ngd* *.ngm *.ngo *.out *.pad *.par *.par.* *.pcf *.prm *.rbb *.sdf *.srd *.srm *.srr *.srs *.tlg *par_pad.txt *.twr *.vhd *.vtc *.x86 *.xdl* *.xpi
#	rm -f *.log
	rm -r -f verif
	rm -r -f syntmp
	rm -r -f lec
