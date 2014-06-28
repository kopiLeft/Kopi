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

PACKAGE =	com/kopiright/vkopi/comp/form

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Form
GRM1DEP =	../base/Base
SCANNER1 =	Form

# Compiler classes
JAVAFILES +=	FormScanner VKFormLocalizationWriter			\
		VKParseFormContext					\
		VKParseBlockContext VKParseFieldContext

# Syntax tree classes
JAVAFILES +=	VKBlock VKBlockAlign VKBlockIndex VKBlockTable		\
		VKField	VKFieldColumn VKFieldColumns			\
		VKFieldTypeName VKForm VKFieldType			\
		VKDescriptionPosition VKCoordinatePosition		\
		VKPage VKPosition					\
		VKDefinitionType VKAliasType VKBlockInsert		\
		VKImportedBlock VKFormElement VKFormPrettyPrinter	\
		VKMultiField VKMultiFieldPosition

MESSAGES =	FormMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
