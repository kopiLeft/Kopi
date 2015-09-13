##
## Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

TOPDIR =	../../..

PACKAGE =	com/kopiright/vkopi/comp/chart

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Chart
GRM1DEP =	../base/Base
SCANNER1 =	Chart

# Compiler classes
JAVAFILES +=	ChartScanner VCParseChartContext			\
		VCParseFieldContext

# Syntax tree classes
JAVAFILES +=	VCChart VCChartLocalizationWriter VCConstants		\
		VCDefinitionType VCField VCFieldType			\
		VCFieldTypeName VCDimension VCMeasure VCMultiMeasure	\
		VCMultiDimension

MESSAGES =	ChartMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
