# This Makefile was tested on Redhat 7.1
# Please assume you have all the necessary packages installed on your system.
# Here is a list of them :
#  - sgml-common
#  - openjade
#  - jadetex
#  - docbook-dtd3.1
#  - docbook-style-dsssl
#  - docbook-utils

MAIN_DBK_FILE="kopi.sgm"

DOCBOOK_FILES="intro.sgm classfile.sgm kjc.sgm xkjc.sgm vlib.sgm \
	othertools.sgm refreneces.sgm optimizations.sgm implementation.sgm ending.sgm"
OUTPUT_DIR=output

.PHONY: all all-format clean html html ps pdf rtf \
	prepare-output-dir prepare-output-dir-html


all:
	@echo "Please select one of the following targets to build Kopi documentation :"
	@echo "  - html : to generate documentation in HTML format"
	@echo "  - ps   : to generate documentation in Postscript format"
	@echo "  - pdf  : to generate documentation in PDF format"
	@echo "  - rtf  : to generate documentation in RTF format"
	@echo "  - dvi  : to generate documentation in DVI format"
	@echo "  - all-formats : to generate Kopi documentation in all available formats"
	@echo

all-formats: html ps pdf rtf dvi

html: prepare-output-dir-html
	@echo " --> Generating documentation in $(OUTPUT_DIR)/html"
	@docbook2html -o $(OUTPUT_DIR)/html $(MAIN_DBK_FILE)

ps: prepare-output-dir 
	@echo " --> Generating documentation in $@"
	@docbook2ps -o $(OUTPUT_DIR) $(MAIN_DBK_FILE)

pdf: prepare-output-dir 
	@echo " --> Generating documentation in $@"
	@docbook2pdf -o $(OUTPUT_DIR) $(MAIN_DBK_FILE)

rtf: prepare-output-dir 
	@echo " --> Generating documentation in $@"
	@docbook2rtf -o $(OUTPUT_DIR) $(MAIN_DBK_FILE)

dvi: prepare-output-dir 
	@echo " --> Generating documentation in $@"
	@docbook2dvi -o $(OUTPUT_DIR) $(MAIN_DBK_FILE)

release-html: html
	mkdir -p kopi/implementation;
	cp -r $(OUTPUT_DIR)/html/* kopi/documentation;
	tar cfz kopi-documentation.tar.gz kopi;
	rm -fr kopi;

prepare-output-dir:
	@mkdir -p $(OUTPUT_DIR)

prepare-output-dir-html: 
	@mkdir -p $(OUTPUT_DIR)/html

clean:
	rm -fr $(OUTPUT_DIR) *~ TAGS

remake: clean all-formats

