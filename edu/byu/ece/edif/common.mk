###########################################################################
#
# Common makefile 
#
# Contains:
# - base java rules
#
# Must define the following:
#
# - JAVAC		Java Compiler
# - JAVAC_FLAGS		Java compilation flags
# - JCLASS		Java class files (targets)
# - DIRS		Directories to traverse
#

########################################################
# Default Rules
#
# These rules are added to allow default rules for
# compiling .java files into .class files
#
# $< - dependent that is out of date
# $@ - full target name
#
########################################################
EDIFHOME = $(ROOT)/byucc/edif
PACKAGES = byucc.edif \
	byucc.edif.graph \
	byucc.edif.graph.dfs \
	byucc.edif.javacc \
	byucc.edif.jhdl \
	byucc.edif.libraries.xilinx \
	byucc.edif.tools \
	byucc.edif.tools.edifFlatten \
	byucc.edif.tools.fmap \
	byucc.edif.tools.halflatch \
	byucc.edif.tools.jsap \
	byucc.edif.tools.merge \
	byucc.edif.tools.power \
	byucc.edif.tools.retiming \
	byucc.edif.tools.tmr 
#GLPK_JAR = $(EDIFHOME)/glpk/jni/java/glpk.jar
#CLASSPATH=${ROOT}${CLASSPATHSEPARATOR}${PTOLEMY_JAR}${CLASSPATHSEPARATOR}$(GLPK_JAR)${CLASSPATHSEPARATOR}${BSH_JAR}${CLASSPATHSEPARATOR}${JHDL_JAR}${CLASSPATHSEPARATOR}${JACL_JAR}
CLASSPATH=${ROOT}${CLASSPATHSEPARATOR}${JHDL_JAR}${CLASSPATHSEPARATOR}${JSAP_JAR}

.SUFFIXES: .java .class .edn .ncd

# rule for compiling .java files into .class files
.java.class:
	echo compiling $<
	CLASSPATH="${CLASSPATH}" $(JAVAC) $(JAVAC_FLAGS) $<


jclass:	jarcheck $(JSRCS) $(JCLASS) subjclass

jarcheck:

javadoc: 
	$(JAVADOC) $(JAVADOC_FLAGS) $(JAVADOC_FILES) -d $(JAVADOC_LOC)

subjclass:
	@if [ "x$(DIRS)" != "x" ]; then \
		set $(DIRS); \
		for x do \
		    if [ -w $$x ] ; then \
			( cd $$x ; \
			echo making jclass in $(ME)/$$x ; \
			$(MAKE) $(MFLAGS) $(MAKEVARS) jclass ;\
			) \
		    fi ; \
		done ; \
	fi

CODEDOCDIRALL = $(EDIFHOME)/doc/codeDoc
CODEDOCDIR = ./doc/codeDoc

# Build the Java documentation.
docs: javadocs


docsall: $(CODEDOCDIRALL)/treeall.html



javadocs: $(CODEDOCDIR)/tree.html
#	@if [ "x$(DIRS)" != "x" ]; then \
#		set $(DIRS); \
#		for x do \
#		    if [ -w $$x ] ; then \
#			( cd $$x ; \
#			echo making javadocs in $(ME)/$$x ; \
#			$(MAKE) $(MFLAGS) $(MAKEVARS) javadocs ;\
#			) \
#		    fi ; \
#		done ; \
#	fi


jhtml: $(CODEDOCDIR)/tree.html
# $(DERIVED_JSRCS) is used in ptolemy/data/expr
$(CODEDOCDIRALL)/treeall.html:	$(JSRCS) $(OPTIONAL_JSRCS) $(DERIVED_JSRCS)
	@rm -rf $(CODEDOCDIRALL); \
	mkdir -p $(CODEDOCDIRALL); \
	   "$(JAVADOC)" -sourcepath $(ROOT) $(JDOCFLAGS) \
		-d $(CODEDOCDIRALL) \
		-classpath "$(CLASSPATH)"\
		$(PACKAGES); \
	for x in $(CODEDOCDIRALL)/*.html; do \
		echo "Fixing paths in $(ME)/$$x"; \
		sed -e 's|<a href="java|<a href="$(JAVAHTMLDIR)/java|g' \
			-e's|<img src="images/|<img src="$(JAVAHTMLDIR)/images/|g' \
			$$x > $$x.bak; \
		mv $$x.bak $$x; \
	done
# endif

$(CODEDOCDIR)/tree.html:	$(JSRCS) $(OPTIONAL_JSRCS) $(DERIVED_JSRCS)
	@rm -rf $(CODEDOCDIR); \
	mkdir -p $(CODEDOCDIR); \
	CLASSPATH="$(CLASSPATH)" \
	   "$(JAVADOC)" $(JDOCFLAGS) -d $(CODEDOCDIR) \
		$(JSRCS) $(OPTIONAL_JSRCS) $(DERIVED_JSRCS); \
	for x in $(CODEDOCDIR)/*.html; do \
		echo "Fixing paths in $(ME)/$$x"; \
		sed -e 's|<a href="java|<a href="$(JAVAHTMLDIR)/java|g' \
			-e's|<img src="images/|<img src="$(JAVAHTMLDIR)/images/|g' \
			$$x > $$x.bak; \
		mv $$x.bak $$x; \
	done

# rule for compling .class files into .ncd files
.class.edn:
	(cd test; $(JAVA) $(BIG_HEAP) tb_$(*F); mv $*.edn ..)

.edn.ncd:
	cp $*.edn netlist
	(cd netlist; ../$(SCRIPTS)/buildVirtex $*; rm $*.edn; cp $*_r.ncd ../$*.ncd)

########################################################
# Clean Rules
########################################################
CRUD= core *.class $(KRUFT)

clean:
	rm -f $(CRUD)
	@if [ "x$(DIRS)" != "x" ]; then \
		set $(DIRS); \
		for x do \
		    if [ -w $$x ] ; then \
			( cd $$x ; \
			echo making $@ in $(ME)/$$x ; \
			$(MAKE) $(MFLAGS) $(MAKEVARS) $@ ;\
			) \
		    fi ; \
		done ; \
	fi

# removes more than clean (bakup files, etc.)
EXTRACRUD = $(CRUD) *~ *.bak

extraclean:
	rm -f $(EXTRACRUD) $(EXTRAKRUD)
	@if [ "x$(DIRS)" != "x" ]; then \
		set $(DIRS); \
		for x do \
		    if [ -w $$x ] ; then \
			( cd $$x ; \
			echo making $@ in $(ME)/$$x ; \
			$(MAKE) $(MFLAGS) $(MAKEVARS) $@ ;\
			) \
		    fi ; \
		done ; \
	fi

# This section is for cleaning unwanted, temporary directories
dirsclean:
	@if [ "x$(DIRCRUD)" != "x" ]; then \
		echo "removing directories: $(DIRCRUD)"; \
		rm -rf $(DIRCRUD); \
	fi

