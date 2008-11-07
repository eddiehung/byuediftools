
################################################################################
# 
#  Copyright (c) 2008 Brigham Young University
#  
#  This file is part of the BYU EDIF Tools.
#  
#  BYU EDIF Tools is free software: you may redistribute it and/or modify it
#  under the terms of the GNU General Public License as published by the Free
#  Software Foundation, either version 2 of the License, or (at your option) any
#  later version.
#  
#  BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
#  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
#  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
#  details.
#  
#  A copy of the GNU General Public License is included with the BYU EDIF Tools.
#  It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
#  license at <http://www.gnu.org/licenses/>.
################################################################################
  
default: tmr 

query: $(DESIGN).jedif
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifQuery.JEdifQuery $<

tmr: $(DESIGN)_tmr.edf
cutset: $(DESIGN).ptmr $(DESIGN).jedif
analysis: $(DESIGN).ptsd $(DESIGN).iob
sterilize: $(DESIGN).jedif
build: $(DESIGN)_dirty.jedif

$(PE_TMR).edf: $(DESIGN).jedif $(DESIGN).ptmr
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifTMR $< --edif --ptmr \
	$(DESIGN).ptmr -o $(DESIGN)_tmr.edf

$(DESIGN).ptmr: $(DESIGN).jedif $(DESIGN).ptsd $(DESIGN).iob
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifCutset \
	$(DESIGN).jedif --ptmr $(DESIGN).ptsd -o \
	$(DESIGN).ptmr --iob_input $(DESIGN).iob 

$(DESIGN).iob: $(DESIGN).ptsd

$(DESIGN).ptsd: $(DESIGN).jedif
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifTMRAnalysis $< -o $@ \
	--iob_output $(DESIGN).iob $(TMR_ANALYSIS_OPTS)

$(DESIGN).jedif: $(DESIGN)_dirty.jedif
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifSterilize $< \
	--remove_fmaps -o $(DESIGN).jedif $(STERILIZE_OPTS)

$(DESIGN)_dirty.jedif: $(DESIGN).edf
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifBuild \
	$(DESIGN).edf -o $(DESIGN)_dirty.jedif $(BUILD_OPTS) 

jedif2edf:
	$(JAVA) $(CLASSPATH) $(JAVA_OPTS) \
	edu.byu.ece.edif.jedif.JEdifNetlist \
	$(DESIGN).jedif -o $@


clean:
	rm *.jedif
	rm *.ptmr
	rm *.ptsd
	rm *.iob
