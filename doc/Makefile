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


default:		 kopi.pdf kopi.html			
kopi.xml:		kopi.adoc 
			asciidoc -d book -b docbook  kopi.adoc 
			
 kopi.html: ax2

			
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
						
			


kopi.pdf:		kopi.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf kopi.pdf -fo kopi.fo
			

clean:
			rm -rf  kopi.pdf kopi.fo kopi.xml *~

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

