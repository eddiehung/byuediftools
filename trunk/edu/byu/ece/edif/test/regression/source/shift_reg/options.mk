include $(GLOBALS)

PART=XC4VFX12SF363

DESIGN=shift_reg
PE_TMR=$(DESIGN)_tmr

TMR_ANALYSIS_OPTS= --full_tmr --part $(PART)
BUILD_OPTS= --blackboxes
STERILIZE_OPTS=

include $(MAKEFILE)
