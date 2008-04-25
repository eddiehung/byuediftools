ROOT = ../..

include ${ROOT}/config.mk

EXTRAKRUD = ${JAVACC_BASENAME}.java ${JAVACC_BASENAME}TokenManager.java ${JAVACC_BASENAME}Constants.java JavaCharStream.java ParseException.java Token.java TokenMgrError.java SimpleCharStream.java Node.java SimpleNode.java AST*.java
JAVACC_BASENAME = EdifParserCore

JJFILE = edif.jj

all: ${JAVACC_BASENAME}.class

${JAVACC_BASENAME}.java: ${JJFILE}
	${JAVACC} $<


#CLASSPATH definition found in common.mk
# comment this out so that 'make eclipse' will work properly - this breaks
# the old way of building (ie, before we had eclipse)
include ${ROOT}/common.mk


