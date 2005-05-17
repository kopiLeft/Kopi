##
## Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
##
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

PACKAGEDIR =	at/dms

TEMPDIR =	/tmp
JARDIR =	/extra/kopi/jars
VERSION =	2.2C
ZIPDIR =	/extra/kopi/download

# where javadoc files are stored
JAVADOCROOT =	/tmp/javadocs

JAVADOC =	javadoc -source 1.4 -J-Xmx128m
#JAVADOC =	/opt/IBMJava2-13/bin/javadoc -J-Xmx128m
#JAVADOC =	/opt/jdk/bin/javadoc -J-mx128m
#JAVADOCFLAGS =	-package -use -windowtitle "Kopi Suite Version $(VERSION)"
JAVADOCFLAGS =	-package 

## ----------------------------------------------------------------------
## TOOLS

ZIP =		zip -r -q -9

JAR =		jar cvf

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
## DISTRIBUTION

release:
		$(MAKE) clean
		$(MAKE) distrib

distrib:	kjc_distrib

ANTLRRUNTIME =	ANTLRException ASTFactory LLkParser			\
		MismatchedTokenException NoViableAltException		\
		Parser ParserException ParserSharedInputState		\
		Token TokenBuffer TokenQueue TokenStream		\
		BaseAST CommonAST collections/impl/BitSet		\
		collections/AST TreeParser ASTNULLType

KJCJARFILES =	gnu $(ANTLRRUNTIME:%=antlr/%.class)

kjc_distrib:
		$(MAKE) distribution DISTRIB=kjc FILES='$(KJCFILES)'	\
		JARFILES='$(KJCJARFILES)' JAR='jar cvmf'		\
		MANIFEST=$(SOURCEDIR)/kjc/manifest

distribution:
		(							\
		  cd $(CLASSROOT);					\
		  $(JAR) $(MANIFEST) $(DISTRIB)-$(VERSION)-bin.jar	\
			$(FILES:%=$(PACKAGEDIR)/%) $(JARFILES);		\
		  cd $(TEMPDIR);					\
		  rm -rf kopi;						\
		  mkdir kopi;						\
		  cp $(CLASSROOT)/$(DISTRIB)-$(VERSION)-bin.jar kopi;	\
		  cp $(CLASSROOT)/$(DISTRIB)-$(VERSION)-bin.jar $(JARDIR);\
		  cp -rf $(FILES:%=$(SOURCEDIR)/%) kopi;		\
		  cp -rf $(DOCDIR) kopi;				\
		  rm -rf `find kopi -name 'CVS'`;			\
		  tar cf $(DISTRIB)-$(VERSION).tar kopi;			\
		  gzip -f $(DISTRIB)-$(VERSION).tar;			\
		  cp $(DISTRIB)-$(VERSION).tar.gz $(ZIPDIR);		\
		  rm -f $(DISTRIB)-$(VERSION).zip;			\
		  $(ZIP) $(DISTRIB)-$(VERSION).zip kopi;			\
		  cp $(DISTRIB)-$(VERSION).zip $(ZIPDIR);		\
		)

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
