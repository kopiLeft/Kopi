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
## $Id: Makefile,v 1.6 2003/09/02 12:22:53 lackner Exp $
##

TOPDIR =	../../..

PACKAGE =	at/dms/vkopi/comp/trig

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	GKjc
GRM1DEP =	$(TOPDIR)/kopi/comp/kjc/Kjc $(TOPDIR)/xkopi/comp/xkjc/XKjc
SCANNER1 =	GKjc

GRM2VOC =	GSqlc
GRM2DEP =	$(TOPDIR)/xkopi/comp/sqlc/Sqlc $(TOPDIR)/xkopi/comp/xkjc/XSqlc
SCANNER2 =	GSqlc

# Compiler classes
JAVAFILES +=	Main GVKAccess GStdType	GTypeFactory			\
		GCompoundAssignmentExpression GKopiInsertExpression	\
		GAssignmentExpression GProtectionStatementWrapper	\
		GKjcPrettyPrinter GExecSqlStatement			\
		GKjcScanner GKjcTypeFactory GSqlcScanner

MESSAGES =	GKjcMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
