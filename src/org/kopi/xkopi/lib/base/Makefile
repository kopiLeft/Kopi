##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
## $Id$
##

TOPDIR =	../../..

PACKAGE =	org/kopi/xkopi/lib/base

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
		DBForeignKeyException DBUtils DefaultDBContextHandler	\
		DriverInterface JdbcParser				\
		KopiSerializable KopiUtils				\
		Query XInterruptProtectedException			\
		OpenEdgeDriverInterface

JAVAFILES +=	As400DriverInterface					\
		OracleDriverInterface					\
		PostgresDriverInterface					\
		SapdbDriverInterface

OPTIONS =	ConnectionOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

jar:
		(cd $(CLASSROOT);					\
		jar -cf xlib.jar org/kopi/xkopi/lib/base/*.class

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
