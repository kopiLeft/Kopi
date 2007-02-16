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

TOPDIR =	.

# THE NAME OF THIS DIRECTORY
SOURCEDIR =	$(shell pwd)

SUBDIRS =	util/base compiler bytecode kopi xkopi util vkopi drivers

PACKAGEDIR =	com/kopiright

TEMPDIR =	/tmp
VERSION =	2.3A

JAVADOC =	javadoc -source 1.4 -J-Xmx128m
JAVADOCFLAGS =	-package -use -windowtitle "Kopi Suite Version $(VERSION)"

## ----------------------------------------------------------------------
## FILES

KJCFILES =	README Makefile Make.Defs util compiler			\
		classfile optimize backend dis ksm kjc ikjc jperf	\
		lexgen kopi optgen msggen

## ----------------------------------------------------------------------
## RULES

default:	build

## ----------------------------------------------------------------------
## JAVADOC

javadoc:	javadocdirs javadocsrcs mdocu

mdocu:
		$(JAVADOC) $(JAVADOCFLAGS) -d $(JAVADOCROOT)/html	\
		-sourcepath $(JAVADOCROOT)/source			\
		`/bin/ls -1 $(JAVADOCROOT)/packages`

javadocdirs:
		mkdir -p $(JAVADOCROOT)/source
		mkdir -p $(JAVADOCROOT)/packages
		mkdir -p $(JAVADOCROOT)/html

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
