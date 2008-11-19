include $(GLOBALS)

PART=XC4VFX12SF363

DESIGN=synth_th1_slaac
PE_TMR=$(DESIGN)_tmr

TMR_ANALYSIS_OPTS= --full_tmr
BUILD_OPTS= --blackboxes
STERILIZE_OPTS=

include $(MAKEFILE)
