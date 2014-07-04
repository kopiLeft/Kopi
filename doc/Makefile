##
## Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id$
##

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

## ----------------------------------------------------------------------
## GOALS


default:		form.pdf

form.xml:		form.adoc
			asciidoc -d book -b docbook  form.adoc 

form.fo:		form.xml
			xsltproc -o form.fo --stringparam l10n.gentext.language "fr"			\
			--stringparam body.font.family "concrete" --stringparam title.font.family "sansserif"	\
			--stringparam body.font.size "10" --stringparam body.font.master "8" 			\
			--stringparam toc.section.depth 4 --stringparam admon.graphics 1 			\
			--stringparam admon.graphics.path "/etc/asciidoc/images/icons/" 			\
			--stringparam default.image.width 200pt --stringparam body.start.indent 20pt 		\
			--stringparam  section.autolabel 1  --stringparam book.titlepage.color "#0268A4" 	\
			--stringparam  section.label.includes.component.label 1  				\
			--stringparam chapter.autolabel 1 -param shade.verbatim 1 -param ulink.show 0 		\
			$(STYLESHEETS_DIR)/fo/docbook.xsl form.xml

form.pdf:		form.fo
			fop -c $(STYLESHEETS_DIR)/../conf.xml -pdf form.pdf -fo form.fo

clean:
			rm -rf form.pdf form.fo  form.xml *~

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

