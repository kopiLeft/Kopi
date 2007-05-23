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
## $Id: Makefile,v 1.12 2004/04/29 15:18:55 lackner Exp $
##

TOPDIR =		../../..

NONSTANDARDBUILD =	true

TARGETDIR =		$(CLASSROOT)/com/kopiright/vkopi/lib/resource

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

FREEGIFS =

OLDGIFS =	all apply arrowleft arrowright article			\
		ask bkup bkup3 block block2 board bomb			\
		bookmark boxarrow break bw calculate			\
		cfolder clip collapsed collapsed_f			\
		collapsed_s collapsed_t collapsedb combo		\
		config convert copy delete deleteline			\
		desk detail done duke edit		\
		error expanded expanded_f expanded_s			\
		expanded_t expandedb export fax fold			\
		foldColumn form form_f form_s form_t			\
		forms forms_f forms_s forms_t fw gifIcon		\
		green guide help home index info info2			\
		insert insertline interrupt jpgIcon			\
		launch list lock login mail menuquery			\
		money moneycheck noIcon note nothing			\
		notice ofolder open options pageLeft			\
		pageRight phone preview print				\
		printoptions project quit red refresh			\
		reload report save search				\
		search2 searchop sec selected send			\
		serialquery serviceoff serviceon sort			\
		split standard stick stop store suggest			\
		todo top unfold unfoldColumn unstick			\
		users utils validate wait warning window		\
		yellow arrowfirst arrowlast


PNGFILES =	all apply  article					\
		ask bkup bkup3 block block2 board bomb			\
		bookmark boxarrow break bw clip				\
		config convert copy delete deleteline			\
		desk detail done duke edit				\
		error  export fold					\
		foldColumn detail_view chart_view			\
		 fw gifIcon						\
		help home calculate					\
		insert insertline  jpgIcon				\
		launch list lock login mail menuquery			\
		money  note nothing					\
		notice  open options pageLeft				\
		pageRight pageFirst pageLast phone preview print	\
		printoptions project quit  refresh			\
		reload report save search				\
		search2 searchop sec  send				\
		serialquery serviceoff serviceon sort			\
		split standard  stop store suggest			\
		todo top unfold unfoldColumn				\
		users utils validate wait warning forms checkbox

PNGNEW =	align_center align_justify align_left align_right bold	\
		cut ident italic paste redo unident underline undo      \
		border password zoomminus zoomwidth zoomheight		\
		zoomoptimal zoomplus

GIFFILES =	$(FREEGIFS) $(OLDGIFS)

PSFILES =	StoneGerman german report

LOCALIZATIONS =	General HelpViewer Preview Menu Window Messages VKMessages VlibProperties

OTHERFILES =	general.tex macros.tex splash.jpg			\
		titlepage.sty top.tex					\


FILES =		$(PNGFILES:%=%.png)					\
		$(PNGNEW:%=%.png)					\
		$(GIFFILES:%=%.gif)					\
		$(PSFILES:%=%.ps)					\
		$(LOCALIZATIONS:%=%-de_AT.xml)				\
		$(LOCALIZATIONS:%=%-fr_FR.xml)				\
		$(LOCALIZATIONS:%=%-en_GB.xml)				\
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

gallery.html:	$(GIFFILES:%=%.gif)
		sh makegallery.sh $^ > $@

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
