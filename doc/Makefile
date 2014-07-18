## Copyright (c) 1990-2014 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
## $Id$
##

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

## ----------------------------------------------------------------------
## GOALS


default:		form.pdf general.pdf report.pdf print.pdf kopi.pdf conclusion.pdf

general.xml:		general.adoc 
			asciidoc -d book -b docbook  general.adoc 

form.xml:		form.adoc 
			asciidoc -d book -b docbook  form.adoc 
			
print.xml:		print.adoc 
			asciidoc -d book -b docbook  print.adoc 
			
report.xml:		report.adoc 
			asciidoc -d book -b docbook  report.adoc 
			
kopi.xml:		kopi.adoc 
			asciidoc -d book -b docbook  kopi.adoc 
			
conclusion.xml:  	conclusion.adoc 
			asciidoc -d book -b docbook  conclusion.adoc 		
			
conclusion.fo:	 conclusion.xml
	          xsltproc -o conclusion.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  conclusion.xml
			
				

general.fo:	general.xml
	          xsltproc -o general.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  general.xml

form.fo:	form.xml
	          xsltproc -o form.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  form.xml
			
			
report.fo:	report.xml
	          xsltproc -o report.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  report.xml
			

print.fo:	print.xml
	          xsltproc -o print.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  print.xml
						
			
kopi.fo:	kopi.xml
	          xsltproc -o kopi.fo --stringparam l10n.gentext.language "fr"	\
		--stringparam body.font.family "concrete"				\
		--stringparam title.font.family "sansserif"				\
		--stringparam body.font.size "10"					\
		--stringparam body.font.master "8"					\
		--stringparam body.start.indent 20pt					\
		--stringparam section.autolabel 1					\
		--stringparam default.image.width 400pt					\
		--stringparam section.label.includes.component.label 1			\
		-param shade.verbatim 1 -param toc.section.depth 6 			\
		--stringparam variablelist.as.blocks 1					\
		--stringparam admon.graphics 1						\
		--param variablelist.term.break.after 1					\
		--stringparam book.titlepage.recto.logo "images/srd.png" 		\
		--param ulink.show 0 --stringparam book.titlepage.color "#0268A4"	\
		--stringparam table.header.color "#2e7fa1"				\
		--param table.row.alternate.color 1					\
		--stringparam table.row.odd.color "#ddd"				\
		--stringparam table.row.pair.color "#fff"				\
		--stringparam admon.graphics.path "/etc/asciidoc/images/icons/"		\
		--stringparam header.image.filename "images/srd.png"			\
		--param fop1.extensions 1 $(STYLESHEETS_DIR)/fo/docbook.xsl  kopi.xml
						
			

general.pdf:		general.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf general.pdf -fo general.fo


form.pdf:		form.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf form.pdf -fo form.fo
			
report.pdf:		report.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf report.pdf -fo report.fo
			
			
print.pdf:		print.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf print.pdf -fo print.fo

kopi.pdf:		kopi.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf kopi.pdf -fo kopi.fo
			
conclusion.pdf:	        conclusion.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf conclusion.pdf -fo conclusion.fo

clean:
			rm -rf form.pdf form.fo  form.xml general.pdf general.xml general.fo  report.pdf report.xml report.fo print.pdf print.fo print.xml  kopi.pdf kopi.fo kopi.xml conclusion.xml conclusion.pdf conclusion.fo *~

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

