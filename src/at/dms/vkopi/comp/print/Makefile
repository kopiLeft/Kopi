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
## $Id: Makefile,v 1.2 2001/05/24 19:28:24 graf Exp $
##

TOPDIR =	../../..

PACKAGE =	at/dms/vkopi/comp/print

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Print
GRM1DEP =	../base/Base
SCANNER1 =	Print

# Compiler classes
JAVAFILES +=	PrintScanner

# Syntax tree classes
JAVAFILES +=	PRParagraphStyle PRStyle PRPosition PRStyleRef		\
		PRPostscriptBlock PRTabStop PRBlock PRText PRBlockStyle \
		PRTextBlock PRTextStyle					\
		PRJavaExpression PRProlog				\
		PRRectangleBlock PRPage PRSourceElement			\
		PRDefinitionCollector PRUtils PRInsert PRListBlock	\
		PRTrigger PRTabRef PRRecursiveBlock PRConditionalSource	\
		PRHorizontalBlock PRPageNumber PRImportedBlock

MESSAGES =	PrintMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
