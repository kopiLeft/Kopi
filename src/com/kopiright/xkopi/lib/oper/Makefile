##
## Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
##
## This library is free software; you can redistribute it and/or
## modify it under the terms of the GNU Lesser General Public
## License version 2.1 as published by the Free Software Foundation.
##
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
## Lesser General Public License for more details.
##
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id: Makefile,v 1.3 2001/05/24 19:49:04 graf Exp $
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/xkopi/lib/oper

NONSTANDARDCOMPILER =	true

## ----------------------------------------------------------------------
## TOOLS

XKJC =		$(JAVA) com.kopiright.xkopi.comp.xkjc.Main --verbose --nooo --xkjcpath /dev/null

## ----------------------------------------------------------------------
## FILES

XFILES =	XBoolean XByte XCharacter XDate XDouble			\
		XFixed XFloat XInteger XMonth XShort			\
		XString XTime XWeek

JFILES  =	NullValueInComparison NullValueInCastOperator		\
		NullValueInOperation

## ----------------------------------------------------------------------
## GOALS

default:	build

classes.stamp:	$(XFILES:%=%.x) $(JFILES:%=%.java)
		$(XKJC) -d $(CLASSROOT) $?
		touch classes.stamp

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
