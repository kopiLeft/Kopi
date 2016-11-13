##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
##
## This library is free software; you can redistribute it and/or
## modify it under the terms of the GNU Lesser General Public
## License version 2.1 as published by the Free Software Foundation.
##
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
## Lesser General Public License for more details.
##
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id$
##

TOPDIR =		../../../../..

NONSTANDARDBUILD =	true

TARGETDIR =		$(CLASSROOT)/org/kopi/vkopi/lib/ui/vaadin/resource

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

FREEGIFS =	list loading otherTab topRibbon calendar

OLDGIFS =


PNGFILES =	all bg_header block border bottomRibbon			\
		bread_crumb_separator break calendar			\
		collapsed_a collapsed_p collapsed copy delete desk	\
		detail_view detail down duke edit expanded_a expanded_p	\
		expanded exportCsv exportPdf exportXls flag-de flag-fr	\
		flag-gb flag-tn fold foldColumn footer_icon form_a	\
		form_p form_selected forms formula help home insert	\
		insertline list login_img logo_kopi			\
		logo_srd_excellence_operationelle logo_srd mail		\
		menuquery note nothing open options preview print quit	\
		ribbon save search searchop separator serialquery	\
		serviceoff serviceon slogan splash_image store suggest	\
		timeStamp topRibbon tri unfold unfoldColumn up

PNGNEW =

GIFFILES =	$(FREEGIFS) $(OLDGIFS)

PSFILES =	

LOCALIZATIONS =	

OTHERFILES =


FILES =		$(PNGFILES:%=%.png)					\
		$(PNGNEW:%=%.png)					\
		$(GIFFILES:%=%.gif)					\
		$(PSFILES:%=%.ps)					\
		$(LOCALIZATIONS:%=%-de_AT.xml)				\
		$(LOCALIZATIONS:%=%-fr_FR.xml)				\
		$(LOCALIZATIONS:%=%-en_GB.xml)				\
		$(LOCALIZATIONS:%=%-ar_TN.xml)				\
		$(OTHERFILES)

## ----------------------------------------------------------------------
## GOALS

default:	build

build:		$(TARGETDIR) $(FILES:%=$(TARGETDIR)/%)


$(TARGETDIR):
		mkdir $(TARGETDIR)

$(TARGETDIR)/%:	%
		cp $< $(TARGETDIR)

## ----------------------------------------------------------------------
## SPECIAL TARGETS


## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
