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
## $Id: Makefile,v 1.11 2003/08/12 10:26:59 mike Exp $
##

TOPDIR =	../../..

PACKAGE =	at/dms/xkopi/comp/dbi

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

GRM1VOC =	Dbi
GRM1DEP =	$(TOPDIR)/xkopi/comp/sqlc/Sqlc
SCANNER1 =	Dbi

# Compiler classes
JAVAFILES +=	DefaultDbiVisitor Main DBAccess DbiScanner DbiVisitor	\
		DbiPrettyPrinter Constants DbiChecker DbiPhylum

# Syntax tree classes
JAVAFILES +=	AddTableConstraintStatement AddTableColumnsStatement	\
		AlterColumnStatement AlterSetDefaultColumnStatement	\
		AlterDropDefaultColumnStatement				\
		AlterNotNullColumnStatement				\
		BlobType BooleanType ByteType ClobType CodeBoolDesc	\
		CodeBoolType CodeDesc CodeFixedDesc			\
		CodeFixedType CodeLongDesc CodeLongType			\
		ColorType Column Console Constraint DateType		\
		DbCheck DbiStatement DbiType DelimSpec			\
		DropIndexStatement DropTableConstraintStatement		\
		DropTableStatement DropViewStatement EnumType		\
		FixedType GrantPrivilegeStatement			\
		GrantUserClassStatement ImageType			\
		IndexDefinition IndexElem IntType Key			\
		KopiDbiChecker MonthType NullDelimiterSpec		\
		NullSpec ReferencedTableAndColumns			\
		OracleDbiChecker					\
		PostgresDbiChecker					\
		ReferentialConstraintDefinition	SapdbDbiChecker		\
		SCompilationUnit ScriptHeadStatement			\
		SequenceDefinition DropSequenceStatement		\
		ShortType SpoolFileStatement				\
		SpoolTableStatement StringType TransbaseDbiChecker	\
		TableConstraint TableDefinition				\
		TablePrivilege TextType TimeType			\
		TimestampType UniqueConstraintDefinition		\
		ViewColumn ViewDefinition WeekType 

PROPERTIES =	dbi

OPTIONS =	DbiOptions  ConsoleOptions

MESSAGES =	DbiMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
