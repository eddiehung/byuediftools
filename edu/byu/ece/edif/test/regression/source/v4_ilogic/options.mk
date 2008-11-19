include $(GLOBALS)

PART=XC4VFX12SF363

DESIGN=4bitadder
PE_TMR=$(DESIGN)_tmr

TMR_ANALYSIS_OPTS= --full_tmr --part $(PART)\
									 --no_tmr_c dcm_adv\
									 --tmr_p answer,data1 

BUILD_OPTS= --blackboxes
STERILIZE_OPTS=

include $(MAKEFILE)
