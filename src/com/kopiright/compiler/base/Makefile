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
## $Id: Makefile,v 1.7 2001/06/04 11:48:35 graf Exp $
##

TOPDIR =	../..

PACKAGE =	com/kopiright/compiler/base

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	CWarning Compiler					\
		JavaStyleComment JavadocComment NumberParser		\
		Phylum PositionedError TabbedPrintWriter		\
		TokenReference UnpositionedError			\
		WarningFilter

# msggen not available at this point in the build process
ifndef TOOLS_BUILT
JAVAFILES +=	CompilerMessages
else
MESSAGES +=	CompilerMessages
endif

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
