EDIF_ROOT=/home/brian/workspace/EDIF
JSAP=/home/brian/jars/JSAP.jar

DESIGN=shift_reg
DESIGN_PATH=/home/brian/test_designs/shift_reg/orig/synth


query: ${DESIGN}_sterilize.jedif
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.jedif.JEdifQuery $<


jediftmr : ${DESIGN}_jediftmr.edf

${DESIGN}_jediftmr.edf : ${DESIGN}_sterilize.jedif ${DESIGN}_sterilize.jedif.ptsd3
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.tmr.jedif.JEdifTMR $< --edif --ptmr ${DESIGN}_sterilize.jedif.ptsd3 -o ${DESIGN}_jediftmr.edf


voter_insertion : ${DESIGN}_sterilize.jedif.ptsd3

${DESIGN}_sterilize.jedif.ptsd3 : ${DESIGN}_sterilize.jedif ${DESIGN}_sterilize.jedif.ptsd2
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.tmr.jedif.JEdifMoreFrequentVoting ${DESIGN}_sterilize.jedif --ptmr ${DESIGN}_sterilize.jedif.ptsd2 --voter_threshold 100 -o ${DESIGN}_sterilize.jedif.ptsd3


feedback_cutset : ${DESIGN}_sterilize.jedif.ptsd2

${DESIGN}_sterilize.jedif.ptsd2 : ${DESIGN}_sterilize.jedif ${DESIGN}_sterilize.jedif.ptsd
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.tmr.jedif.JEdifCutset ${DESIGN}_sterilize.jedif --ptmr ${DESIGN}_sterilize.jedif.ptsd -o ${DESIGN}_sterilize.jedif.ptsd2 --iob_input ${DESIGN}.iob


tmr_analysis : ${DESIGN}_sterilize.jedif.ptsd

${DESIGN}_sterilize.jedif.ptsd : ${DESIGN}_sterilize.jedif
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.tmr.jedif.JEdifTMRAnalysis $< -o ${DESIGN}_sterilize.jedif.ptsd --iob_output ${DESIGN}.iob --full_tmr --tmr_inports --tmr_outports --no_tmr_p XP_PCLK,XP_RST --no_tmr_c bufg


sterilize : ${DESIGN}_sterilize.jedif

${DESIGN}_sterilize.jedif: ${DESIGN}.jedif
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.jedif.JEdifSterilize ${DESIGN}.jedif --remove_hl --remove_fmaps -o ${DESIGN}_sterilize.jedif


build : ${DESIGN}.jedif

${DESIGN}.jedif : ${DESIGN_PATH}/${DESIGN}.edf
	java -classpath "${EDIF_ROOT}:${JSAP}" -Xmx256M byucc.edif.tools.merge.JEdifBuild ${DESIGN_PATH}/${DESIGN}.edf -o ${DESIGN}.jedif

clean : 
	-rm *.ptsd* *.jedif
