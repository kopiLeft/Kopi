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
## $Id: Makefile,v 1.10 2003/10/17 06:30:52 laurent Exp $
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/xkopi/lib/base

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	Connection Cursor					\
		DBConstraintException DBContext				\
		DBContextHandler DBCursorException DBDeadLockException 	\
		DBException DBInterruptionException			\
		DBInvalidDataException DBNoRowException			\
		DBRuntimeException DBTooManyRowsException		\
		DBUnspecifiedException DBDuplicateIndexException	\
		DBForeignKeyException DefaultDBContextHandler		\
		DriverInterface JdbcParser				\
		KopiSerializable KopiUtils				\
		Query XKjcRtUtils XInterruptProtectedException

JAVAFILES +=	As400DriverInterface					\
		Db2UdbDriverInterface					\
		KconnectDriverInterface					\
		MySqlDriverInterface					\
		OracleDriverInterface					\
		PostgresDriverInterface					\
		SapdbDriverInterface					\
		TbxDriverInterface

OPTIONS =	ConnectionOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

jar:
		(cd $(CLASSROOT);					\
		jar -cf xlib.jar com/kopiright/xkopi/lib/base/*.class

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
