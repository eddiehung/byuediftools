include $(GLOBALS)

PART=XC4VFX12SF363

DESIGN=testCountMult
PE_TMR=$(DESIGN)_tmr

TMR_ANALYSIS_OPTS= --full_tmr --part $(PART)
BUILD_OPTS= --blackboxes
STERILIZE_OPTS=

include $(MAKEFILE)
