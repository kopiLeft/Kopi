##
## Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
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
## $Id: Makefile,v 1.2 2001/05/24 19:28:23 graf Exp $
##

TOPDIR =	../../..

PACKAGE =	at/dms/vkopi/comp/form

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Form
GRM1DEP =	../base/Base
SCANNER1 =	Form

# Compiler classes
JAVAFILES +=	FormScanner VKParseFormContext				\
		VKParseBlockContext VKParseFieldContext

# Syntax tree classes
JAVAFILES +=	VKBlock VKBlockAlign VKBlockTable			\
		VKField	VKFieldColumn VKFieldColumns			\
		VKFieldTypeName VKForm VKFieldType			\
		VKDescriptionPosition VKCoordinatePosition		\
		VKPosition						\
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
