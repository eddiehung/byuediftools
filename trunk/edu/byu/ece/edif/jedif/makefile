JAVA=/opt/jdk1.5.0_04/bin/java
HOME=/net/fpga1/users/whowes
CLASSPATH= -cp $(HOME)/workspace/EDIF:/net/fpga2/jars/JHDL.jar:/net/fpga2/jars/JSAP-latest.jar
DESIGN=counters
DWC_ARGS=--use_drc --persistent_comparators --register_detection --pack_detection_registers

default: tmr

clean: 
	rm *.jedif
	rm *.ptmr
	rm *.ptsd
	rm *.iob

query: $(DESIGN).jedif
	$(JAVA) $(CLASSPATH) -Xmx512M \
	    edu.byu.ece.edif.JEdifQuery $<

tmr:       $(DESIGN)_tmr.edf
dwc:	   $(DESIGN)_dwc.edf
cutset:    $(DESIGN).ptmr $(DESIGN).jedif
analysis:  $(DESIGN).ptsd $(DESIGN).iob  
sterilize: $(DESIGN).jedif
build:     $(DESIGN)_dirty.jedif 

$(DESIGN)_tmr.edf:  $(DESIGN).jedif $(DESIGN).ptmr
	$(JAVA) $(CLASSPATH)  -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifTMR $< --edif --ptmr $(DESIGN).ptmr \
	     -o $(DESIGN)_tmr.edf

$(DESIGN)_dwc.edf:  $(DESIGN).jedif $(DESIGN)_dwc.ptmr
	$(JAVA) $(CLASSPATH)  -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifDWC $< --edif --ptmr $(DESIGN)_dwc.ptmr \
	     -o $(DESIGN)_dwc.edf $(DWC_ARGS)

$(DESIGN).ptmr: $(DESIGN).jedif $(DESIGN).ptsd $(DESIGN).iob
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	    edu.byu.ece.edif.jedif.JEdifCutset \
	    $(DESIGN).jedif --ptmr $(DESIGN).ptsd -o $(DESIGN).ptmr --iob_input $(DESIGN).iob

$(DESIGN)_dwc.ptmr: $(DESIGN).jedif $(DESIGN)_dwc.ptsd $(DESIGN)_dwc.iob
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	    edu.byu.ece.edif.jedif.JEdifCutset \
	    $(DESIGN).jedif --ptmr $(DESIGN)_dwc.ptsd -o $(DESIGN)_dwc.ptmr --iob_input $(DESIGN)_dwc.iob

$(DESIGN).iob:  $(DESIGN).ptsd
$(DESIGN)_dwc.iob:  $(DESIGN)_dwc.ptsd

$(DESIGN).ptsd: $(DESIGN).jedif
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifTMRAnalysis $< -o $@ --iob_output $(DESIGN).iob

$(DESIGN)_dwc.ptsd: $(DESIGN).jedif
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifDWCAnalysis $< -o $@ --iob_output $(DESIGN)_dwc.iob

$(DESIGN).jedif: $(DESIGN)_dirty.jedif
	$(JAVA) $(CLASSPATH) -Xmx512M \
	   edu.byu.ece.edif.jedif.JEdifSterilize $< \
	   --remove_hl --remove_fmaps -o $(DESIGN).jedif

$(DESIGN)_dirty.jedif: $(DESIGN).edf
	$(JAVA)  $(CLASSPATH) -Xmx512M edu.byu.ece.edif.jedif.JEdifBuild \
	    $(DESIGN).edf  -o $(DESIGN)_dirty.jedif

jedif2edf: 
	$(JAVA) $(CLASSPATH) -Xmx512M edu.byu.ece.edif.jedif.JEdifNetlist $(DESIGN).jedif -o $@

