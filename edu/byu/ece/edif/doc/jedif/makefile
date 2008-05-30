JAVA=/opt/jdk/bin/java
HOME=/net/fpga1/users/dsgib
CLASSPATH= -cp $(HOME)/workspace/EDIF:$(HOME)/jars/JHDL.jar:@(HOME)/jars/JSAP.jar
DESIGN=hl_dut

default: tmr

clean: 
	rm *.jedif
	rm *.ptmr
	rm *.ptsd
	rm *.iob

query: $(DESIGN).jedif
	$(JAVA) $(CLASSPATH) -Xmx512M \
	    edu.byu.ece.edif.jedif.JEdifQuery.JEdifQuery $<

tmr:       $(DESIGN)_tmr.edf
cutset:    $(DESIGN).ptmr $(DESIGN).jedif
analysis:  $(DESIGN).ptsd $(DESIGN).iob  
sterilize: $(DESIGN).jedif
build:     $(DESIGN)_dirty.jedif 

$(DESIGN)_tmr.edf:  $(DESIGN).jedif $(DESIGN).ptmr
	$(JAVA) $(CLASSPATH)  -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifTMR $< --edif --ptmr $(DESIGN).ptmr \
	     -o $(DESIGN)_tmr.edf

$(DESIGN).ptmr: $(DESIGN).jedif $(DESIGN).ptsd $(DESIGN).iob
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	    edu.byu.ece.edif.jedif.JEdifCutset \
	    $(DESIGN).jedif --ptmr $(DESIGN).ptsd -o $(DESIGN).ptmr --iob_input $(DESIGN).iob

$(DESIGN).iob:  $(DESIGN).ptsd
$(DESIGN).ptsd: $(DESIGN).jedif
	$(JAVA)  $(CLASSPATH) -Xmx512M \
	     edu.byu.ece.edif.jedif.JEdifTMRAnalysis $< -o $@ --iob_output $(DESIGN).iob

$(DESIGN).jedif: $(DESIGN)_dirty.jedif
	$(JAVA) $(CLASSPATH) -Xmx512M \
	   edu.byu.ece.edif.jedif.JEdifSterilize $< \
	   --remove_hl --remove_fmaps -o $(DESIGN).jedif


$(DESIGN)_dirty.jedif: $(DESIGN).edf
	$(JAVA)  $(CLASSPATH) -Xmx512M edu.byu.ece.edif.jedif.JEdifBuild \
	    $(DESIGN).edf  -o $(DESIGN)_dirty.jedif

jedif2edf: 
	$(JAVA) $(CLASSPATH) -Xmx512M edu.byu.ece.edif.jedif.JEdifNetlist $(DESIGN).jedif -o $@

