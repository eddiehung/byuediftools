TARGET=ClockDomainParser

all: pdf-latex

dvi: $(TARGET).dvi
	dvips -t letter -P pdf $(TARGET).dvi -o $(TARGET).ps

pdf-latex:
	pdflatex ${TARGET}
	pdflatex ${TARGET}
#	bibtex ${TARGET}
#	pdflatex ${TARGET}
#	pdflatex ${TARGET}

pdf:	$(TARGET).dvi
	dvipdf ${TARGET}

ps:	$(TARGET).dvi
	dvips ${TARGET} -P pdf -o

$(TARGET).dvi: $(TARGET).tex
	latex $(TARGET)
	latex $(TARGET)
#	bibtex $(TARGET)
#	latex $(TARGET)
#	latex $(TARGET)

clean:
	rm -f *.aux *.dvi *.lof *.log *.lot *.toc *.out *.blg *.bbl

veryclean:
	rm -f *.aux *.dvi *.lof *.log *.lot *.toc *.out *.blg *.bbl
	rm -f ${TARGET}.ps
	rm -f ${TARGET}.pdf
