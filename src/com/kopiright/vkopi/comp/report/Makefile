##
## Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
## $Id: Makefile,v 1.2 2001/05/24 19:28:25 graf Exp $
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/vkopi/comp/report

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Report
GRM1DEP =	../base/Base
SCANNER1 =	Report

# Compiler classes
JAVAFILES +=	ReportScanner VRParseReportContext			\
		VRParseFieldContext

# Syntax tree classes
JAVAFILES +=	VRConstants VRReport VRField VRFieldType		\
		VRDefinitionType VRFieldTypeName VRSeparatorField	\
		VRSeparatorFieldType VRMultiField			\
		VRReportLocalizationWriter

MESSAGES =	ReportMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
