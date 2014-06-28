##
## Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

TOPDIR =        ../..

PACKAGE =       com/kopiright/bytecode/ssa

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

JAVAFILES +=    BasicBlock CFGEdge CodeGenerator                        \
CodeGeneratorBasicBlock CodeGeneratorMethod             \
ColorComputer ControlFlowGraph                          \
CopyPropagation DominatorComputer                       \
DominatorTreeNode Edge EdgeLabel                        \
ExceptionHandler GenerateQVar Graph                     \
InterferenceGraph LivenessComputer Main                 \
MethodOptimizer Node NodeVisitor Propagator             \
QANewArray QAbstractJumpInst QArrayLength               \
QAssignment QBinaryOperation QCallReturn                \
QCallVoid QCheckCast QConditionalJump                   \
QConstant QDeclareInitialised QExpression               \
QGetArray QGetField QInst                               \
QInstArray QInstArrayAccessor QInstanceOf               \
QJsr QJump QMethodReturn QMonitor                       \
QMultiArray QNew QNewArray QNewInitialized              \
QNop QOperand QOperandBox QPhi QPhiCatch                \
QPhiJoin QPhiNop QPhiReturn QPutArray                   \
QPutField QRet QReturn QSSAVar                          \
QSimpleExpression QSwitch QThrow                        \
QUnaryOperation QVar QVoidMethodCall                    \
QuadrupleGenerator SSAConstructor                       \
SSAConstructorInfo SSADestructor                        \
SSAMessages SSAOptions SSAVar                           \
SubRoutine UnusedComputer Optimizer

MESSAGES =      SSAMessages

OPTIONS =       SSAOptions

## ----------------------------------------------------------------------
## GOALS

default:        build

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
