##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

TOPDIR =	../../..

PACKAGE =	org/kopi/xkopi/comp/dbi

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
		AlterNotNullColumnStatement As400DbiChecker		\
		BlobType BooleanType ByteType ClobType CodeBoolDesc	\
		CodeBoolType CodeDesc CodeFixedDesc			\
		CodeFixedType CodeLongDesc CodeLongType			\
		ColorType Column Console Constraint DateType		\
		DbCheck DbiStatement DbiType DelimSpec			\
		DoubleType						\
		DropIndexStatement DropTableConstraintStatement		\
		DropTableStatement DropViewStatement EnumType		\
		FixedType FloatType GrantPrivilegeStatement		\
		GrantUserClassStatement ImageType			\
		IndexDefinition IndexElem IntType Key			\
		KopiDbiChecker MonthType NullDelimiterSpec		\
		NullSpec ReferencedTableAndColumns			\
		OracleDbiChecker					\
		PostgresDbiChecker Pragma				\
		ReferentialConstraintDefinition	SapdbDbiChecker		\
		SCompilationUnit ScriptHeadStatement			\
		SequenceDefinition DropSequenceStatement		\
		ShortType SpoolFileStatement				\
		SpoolTableStatement StringType				\
		TableConstraint TableDefinition				\
		TablePrivilege TextType TimeType			\
		TimestampType UniqueConstraintDefinition		\
		ViewColumn ViewDefinition WeekType			\
		OraSpoolerDbiChecker OpenEdgeDbiChecker

OPTIONS =	DbiOptions  ConsoleOptions

MESSAGES =	DbiMessages

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
