##
## Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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
## $Id: Makefile,v 1.4 2002/05/31 20:25:47 lackner Exp $
##

TOPDIR =	../..

PACKAGE =	com/kopiright/bytecode/classfile

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=	AbstractInstructionAccessor AccessorContainer		\
		AccessorTransformer AsciiConstant Attribute		\
		AttributeList BadAccessorException ClassConstant	\
		ClassfileOptions ClassFileFormatException ClassInfo	\
		ClassPath						\
		ClassRefInstruction CodeEnv CodeInfo CodePosition	\
		ConstantPool ConstantValueAttribute Constants		\
		DeprecatedAttribute DoubleConstant ExceptionsAttribute	\
		FieldInfo FieldRefConstant FieldRefInstruction		\
		FloatConstant GenericAttribute HandleCreator		\
		HandlerInfo IincInstruction InnerClassInfo		\
		InnerClassTable Instruction InstructionAccessor		\
		InstructionHandle InstructionIO IntegerConstant		\
		InterfaceConstant					\
		InvokeinterfaceInstruction JumpInstruction		\
		LineNumberInfo LineNumberTable LocalVarInstruction	\
		LocalVariableInfo LocalVariableTable LongConstant	\
		Main Member MethodInfo MethodRefConstant		\
		MethodRefInstruction MultiarrayInstruction		\
		NameAndTypeConstant NewarrayInstruction			\
		NoArgInstruction OpcodeNames PooledConstant		\
		PushLiteralInstruction ReferenceConstant		\
		SkippedCodeInfo SourceFileAttribute StringConstant	\
		SwitchInstruction SyntheticAttribute UnresolvedConstant	\
		ConstraintsAttribute SignatureAttribute			\
		PreconditionAttribute PostconditionAttribute		\
		InvariantAttribute MethodDescription			\
		ConstantPoolOverflowException				\
		InstructionOverflowException				\
		LocalVariableOverflowException

MESSAGES =	ClassfileMessages

OPTIONS =	ClassfileOptions

## ----------------------------------------------------------------------
## GOALS

default:	build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
