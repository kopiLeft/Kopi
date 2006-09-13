##
## Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
##
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License version 2
## as published by the Free Software Foundation.
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
## $Id: Makefile,v 1.18 2004/06/09 17:30:25 lackner Exp $
##

TOPDIR =	../../..

PACKAGE =	com/kopiright/vkopi/lib/form

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	BlockAlignment Commands					\
		DBlock DColorField DField				\
		DForm DImage DImageField DLabel	DChartBlock		\
		DMultiBlock DObjectField DPage				\
		DTextEditor DTextField DateChooser			\
		ImageFileChooser KopiAlignment				\
		KopiFieldDocument KopiScanDocument 	                \
		KopiMultiBlockLayout KopiSimpleBlockLayout		\
		LatexPrintWriter ListDialog MultiFieldAlignment		\
		Triggers VBlock VBooleanCodeField			\
		VBooleanField VCodeField				\
		VColorField VConstants					\
		VDateField VDictionaryForm				\
		VDocGenerator VEnumField				\
		VField VFieldCommand VFieldException			\
		VFieldUI VFixedCodeField				\
		VFixedField VForm					\
		VHelpGenerator VImageField				\
		VImportedBlock VIntegerCodeField			\
		VIntegerField						\
		VMonthField VPosition BlockRecordListener		\
		VQueryNoRowException VQueryOverflowException		\
		VSkipRecordException					\
		VStringField VTextField					\
		VTimeField VTimestampField				\
		VWeekField KopiLayoutManager				\
		FieldListener FieldChangeListener			\
		BlockListener ViewBlockAlignment FormListener		\
		PredefinedValueHandler GUIPredefinedValueHandler	\
		KeyNavigator Environment Accessor ModelTransformer

## How to separte Model and Gui in 2 packages:
##JAVAFILES +=	BlockAlignment Commands					\
##		LatexPrintWriter ListDialog				\
##		Triggers VBlock						\
##		VBooleanCodeField					\
##		VBooleanField VCodeField				\
##		VColorField VConstants					\
##		VDateField VDictionaryForm				\
##		VDocGenerator VEnumField				\
##		VField VFieldException					\
##		VFixedCodeField						\
##		VFixedField VForm					\
##		VHelpGenerator VImageField				\
##		VImportedBlock VIntegerCodeField			\
##		VIntegerField						\
##		VMonthField VPosition					\
##		VQueryNoRowException VQueryOverflowException		\
##		VSkipRecordException					\
##		VStringField VTextField					\
##		VTimeField						\
##		VWeekField KopiLayoutManager FieldListener		\
##		BlockListener FormListener PredefinedValueHandler

##		MultiFieldAlignment					\
##		DateChooser ImageFileChooser				\
##		KopiAlignment	 VFieldCommand				\
##		KopiFieldDocument ModelTransformer			\
##		KopiMultiBlockLayout KopiSimpleBlockLayout		\
##		KopiScanDocument KopiTextField				\
##		ViewBlockAlignment					\
##		VFieldUI GUIPredefinedValueHandler			\
##		DBlock DColorField DField				\
##		DForm DImage DImageField DLabel				\
##		DMultiBlock DObjectField DPage				\
##		DTextEditor DTextField	

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
