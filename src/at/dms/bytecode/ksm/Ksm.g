/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

// Import the necessary classes
header { package at.dms.bytecode.ksm; }

{
import java.util.Vector;

import at.dms.bytecode.classfile.*;
}


//-----------------------------------------------------------------------------
// THE PARSER STARTS HERE
//-----------------------------------------------------------------------------

class KsmParser extends Parser;

options {
  k = 2;				// two token lookahead
  exportVocab=Ksm;			// call the vocabulary
  codeGenMakeSwitchThreshold = 2;	// Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;		// Don't generate parser error handlers
  access = "private";			// Set default rule access
}

{
  private static String convertFieldSignature(String name, int dims) {
    StringBuffer	buffer = new StringBuffer();

    for (; dims > 0; dims -= 1) {
      buffer.append('[');
    }

    if (name.equals("boolean")) {
      buffer.append('Z');
    } else if (name.equals("byte")) {
      buffer.append('B');
    } else if (name.equals("char")) {
      buffer.append('C');
    } else if (name.equals("double")) {
      buffer.append('D');
    } else if (name.equals("float")) {
      buffer.append('F');
    } else if (name.equals("int")) {
      buffer.append('I');
    } else if (name.equals("long")) {
      buffer.append('J');
    } else if (name.equals("short")) {
      buffer.append('S');
    } else if (name.equals("void")) {
      buffer.append('V');
    } else {
      buffer.append('L');
      buffer.append(name.replace('.', '/'));
      buffer.append(';');
    }

    return buffer.toString();
  }
}

// --------------------------------------------------------------------
public aCompilationUnit []
  returns [ClassInfo self]
{
  String	sourceFile = null;
  boolean	isInterface;
  short		access;
  String	thisClass;
  String	superClass = null;
  String        genSignature = null;
  Vector	interfaces = new Vector();
  Vector	fields = new Vector();
  Vector	methods = new Vector();
  boolean	deprecated = false;	//!!!
  boolean	synthetic = false;	//!!!

  boolean       attrSource = false;
  boolean       attrSynthetic = false;
  boolean       attrDeprecated = false;
  boolean       attrSignature = false;
}
:
  ( 
    sourceFile = aSourceSpec[]
    { 
      if (attrSource) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_SOURCE);
      }
      attrSource = true;
    }
  |
    genSignature = aSignatureSpec[] 
    { 
      if (attrSignature) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_SIGNATURE);
      }
      attrSignature = true;
    }
  |
    deprecated = aDeprecatedSpec[] 
    { 
      if (attrDeprecated) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_DEPRECATED);
      }
      attrDeprecated = true;
    }
  |
    synthetic = aSyntheticSpec[] 
    { 
      if (attrSynthetic) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_SYNTHETIC);
      }
      attrSynthetic = true;
    }
  )*
  access = aAccess[] isInterface = aTypeSpec[] thisClass = aClassSignature[false]
  ( superClass = aSuperSpec[] )?
  ( aImplementsSpec[interfaces] )?
  LBRACE
  ( aMemberSpec[fields, methods] )*
  RBRACE
    {
      if (isInterface) {
	access |= Constants.ACC_INTERFACE;
      }

      self = new ClassInfo(access,
			   thisClass,
			   superClass,
			   interfaces,
			   fields,
			   methods,
			   null, /*!!!inner classes*/
			   sourceFile,
                           genSignature, /* generic Signature*/
			   deprecated,
                           synthetic);
    }
;


aSourceSpec []
  returns [String self]
:
  "@source" self = aStringLiteral[]
;

aSyntheticSpec []
  returns [boolean self = true]
:
  "@synthetic"
;

aDeprecatedSpec []
  returns [boolean self = true]
:
  "@deprecated"
;

// used for generic signature JSR 41
aSignatureSpec []
  returns [String self]
:
  "@signature" self = aStringLiteral[]
;


aTypeSpec []
  returns [boolean self]
:
  "class" { self = false; }
|
  "interface" { self = true; }
;

aAccess []
  returns [short self = 0]
:
  (
    "public" { self |= Constants.ACC_PUBLIC; }
  |
    "protected" { self |= Constants.ACC_PROTECTED; }
  |
    "private" { self |= Constants.ACC_PRIVATE; }
  |
    "static" { self |= Constants.ACC_STATIC; }
  |
    "abstract" { self |= Constants.ACC_ABSTRACT; }
  |
    "final" { self |= Constants.ACC_FINAL; }
  |
    "native" { self |= Constants.ACC_NATIVE; }
  |
    "strictfp" { self |= Constants.ACC_STRICT; }
  |
    "synchronized" { self |= Constants.ACC_SYNCHRONIZED; }
  |
    "transient" { self |= Constants.ACC_TRANSIENT; }
  |
    "volatile" { self |= Constants.ACC_VOLATILE; }
  )*
;

aSuperSpec []
  returns [String self]
:
  "extends" self = aClassSignature[false]
;

aImplementsSpec [Vector interfaces]
{
  String self;
}
:
  "implements"
    self = aClassSignature[false]
      { interfaces.addElement(self); }
    (
      COMMA
      self = aClassSignature[false]
        { interfaces.addElement(self); }
    )*
;

aMemberSpec[Vector fields, Vector methods]
{
  short		access;
  String	type;
  String	name;
  FieldInfo	fld;
  MethodInfo	mtd;
  String        genSignature = null;
  boolean	deprecated = false;	//!!!
  boolean	synthetic = false;	//!!!
  
  boolean       attrSynthetic = false;
  boolean       attrDeprecated = false;
  boolean       attrSignature = false;
}
:
  ( 
    genSignature = aSignatureSpec[] 
    { 
      if (attrSignature) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_SIGNATURE);
      }
      attrSignature = true;
    }
  |
    deprecated = aDeprecatedSpec[] 
    { 
      if (attrDeprecated) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_DEPRECATED);
      }
      attrDeprecated = true;
    }
  |
    synthetic = aSyntheticSpec[] 
    { 
      if (attrSynthetic) {
        throw new KsmError(null, KsmMessages.DOUBLE_ATTR_SYNTHETIC);
      }
      attrSynthetic = true;
    }
  )*

  access = aAccess[] type = aFieldSignature[]
  name = aName[]

  (
    fld = aFieldSpec[access, type, name, genSignature, synthetic, deprecated]
      { fields.addElement(fld); }
  |
    mtd = aMethodSpec[access, type, name, genSignature, synthetic, deprecated]
      { methods.addElement(mtd); }
  )
;

aFieldSpec [short access, String type, String name, String genSignature, boolean synthetic, boolean deprecated]
  returns [FieldInfo self]
{
  Object	value = null;
}
:
  ( value = aFieldValue[] )? SEMI
    {
      self = new FieldInfo(access,
			   name,
			   type,
                           genSignature,
			   value,
			   deprecated,
			   synthetic
			   );
    }
;

// constant value for a field
aFieldValue []
  returns [Object self]
:
  EQUAL self = aLiteral[]
;


aMethodSpec [short access, String type, String name, String genSignature, boolean synthetic, boolean deprecated]
  returns [MethodInfo self]
{
  String[]	exceptions;
  MethodBody	methodBody = null;
}
:
  type = aMethodSignature[type]
  exceptions = aThrowsSpec []
  (
    methodBody = aMethodBody[]
  |
    SEMI { methodBody = null; }
  )
    {
      CodeInfo		codeInfo;

      if (methodBody == null) {
	//!!! check is abstract
	codeInfo = null;
      } else {
	//!!! check is not abstract
	codeInfo = methodBody.genCodeInfo();
      }

      self = new MethodInfo(access,
			    name,
			    type,
                            genSignature,
			    exceptions,
			    codeInfo,
			    deprecated,
			    synthetic
			    );
    }
;


// .throws <class>
aThrowsSpec []
  returns [String[] self]
{
  Vector	vector = new Vector();
  String	item;
}
:
  (
    "throws" item = aClassSignature[false]
      { vector.addElement(item); }
    (
      COMMA item = aClassSignature[false]
        { vector.addElement(item); }
    )*
  )?
    { self = (String[])Utils.toArray(vector, String.class); }
;


aMethodBody []
  returns [MethodBody self]
{
  self = new MethodBody();
}
:
  LBRACE
  (
    aDirective[self]
  |
    aLabelDeclaration[self]
  |
    aInstruction[self]
  )+
  RBRACE
;


// Directives (.catch, etc.)

aDirective [MethodBody methodBody]
:
  aVariableDirective[methodBody]
|
  aLineDirective[methodBody]
|
  aCatchDirective[methodBody]
;


//
// .var <num> : <name> <sig> [ <StartLab>,  <EndLab> ]
//
aVariableDirective [MethodBody methodBody]
{
  int			slot;
  String		name;
  String		type;
  LabelReference	start = null;
  LabelReference	end = null;
}
:
  "@var" slot = aInteger[] COLON name = aIdentifier[] type = aFieldSignature[]
  LBRACK start = aLabelRef[] COMMA end = aLabelRef[] RBRACK
    {
      if (slot < 0 || slot >= Short.MAX_VALUE) {
	throw new RecognitionException("Slot " + slot + " outside of valid range");
      }

      methodBody.addVariable(name, type, start, end, (short)slot);
    }
;


// .line <num>
aLineDirective [MethodBody methodBody]
{
  int		value;
}
:
  "@line" value = aInteger[]
    { methodBody.addLineNumber(value); }
;

// .catch <branchlab> <class> [ <label1> , <label2> ]
aCatchDirective [MethodBody methodBody]
{
  LabelReference	handler;
  String		thrown = null;
  LabelReference	start;
  LabelReference	end;
}
:
  "@catch" handler = aLabelRef[]
  ( thrown = aClassSignature[false] )?
  LBRACK start = aLabelRef[] COMMA end = aLabelRef[] RBRACK
    { methodBody.addHandler(thrown, start, end, handler); }
;

aLabelDeclaration [MethodBody methodBody]
{
  String	name;
}
:
  name = aIdentifier[] COLON
    { methodBody.addLabel(name); }
;

aInstruction [MethodBody methodBody]
{
  Instruction	insn;
}
:
  ( "@wide" )? // ignored
  (
    insn = aNoArgInstruction[]
  |
    insn = aPushLiteralInstruction[]
  |
    insn = aFieldRefInstruction[]
  |
    insn = aMethodRefInstruction[]
  |
    insn = aClassRefInstruction[]
  |
    insn = aJumpInstruction[]
  |
    insn = aLocalVarInstruction[]
  |
    insn = aNewarrayInstruction[]
  |
    insn = aMultiarrayInstruction[]
  |
    insn = aInvokeinterfaceInstruction[]
  |
    insn = aIincInstruction[]
  |
    insn = aSwitchInstruction[]
  )
    { methodBody.addInstruction(insn); }
;

aNoArgInstruction []
  returns [Instruction self]
{
  int		opcode;
}
:
  opcode = aNoArgOpcode[]
    { self = new NoArgInstruction(opcode); }
;

aNoArgOpcode []
  returns [int self]
:
  "@aaload" { self = Constants.opc_aaload; }
|
  "@aastore" { self = Constants.opc_aastore; }
|
  "@aconst_null" { self = Constants.opc_aconst_null; }
|
  "@areturn" { self = Constants.opc_areturn; }
|
  "@arraylength" { self = Constants.opc_arraylength; }
|
  "@athrow" { self = Constants.opc_athrow; }
|
  "@baload" { self = Constants.opc_baload; }
|
  "@bastore" { self = Constants.opc_bastore; }
|
  "@caload" { self = Constants.opc_caload; }
|
  "@castore" { self = Constants.opc_castore; }
|
  "@d2f" { self = Constants.opc_d2f; }
|
  "@d2i" { self = Constants.opc_d2i; }
|
  "@d2l" { self = Constants.opc_d2l; }
|
  "@dadd" { self = Constants.opc_dadd; }
|
  "@daload" { self = Constants.opc_daload; }
|
  "@dastore" { self = Constants.opc_dastore; }
|
  "@dcmpg" { self = Constants.opc_dcmpg; }
|
  "@dcmpl" { self = Constants.opc_dcmpl; }
|
  "@ddiv" { self = Constants.opc_ddiv; }
|
  "@dmul" { self = Constants.opc_dmul; }
|
  "@dneg" { self = Constants.opc_dneg; }
|
  "@drem" { self = Constants.opc_drem; }
|
  "@dreturn" { self = Constants.opc_dreturn; }
|
  "@dsub" { self = Constants.opc_dsub; }
|
  "@dup" { self = Constants.opc_dup; }
|
  "@dup_x1" { self = Constants.opc_dup_x1; }
|
  "@dup_x2" { self = Constants.opc_dup_x2; }
|
  "@dup2" { self = Constants.opc_dup2; }
|
  "@dup2_x1" { self = Constants.opc_dup2_x1; }
|
  "@dup2_x2" { self = Constants.opc_dup2_x2; }
|
  "@f2d" { self = Constants.opc_f2d; }
|
  "@f2i" { self = Constants.opc_f2i; }
|
  "@f2l" { self = Constants.opc_f2l; }
|
  "@fadd" { self = Constants.opc_fadd; }
|
  "@faload" { self = Constants.opc_faload; }
|
  "@fastore" { self = Constants.opc_fastore; }
|
  "@fcmpg" { self = Constants.opc_fcmpg; }
|
  "@fcmpl" { self = Constants.opc_fcmpl; }
|
  "@fdiv" { self = Constants.opc_fdiv; }
|
  "@fmul" { self = Constants.opc_fmul; }
|
  "@fneg" { self = Constants.opc_fneg; }
|
  "@frem" { self = Constants.opc_frem; }
|
  "@freturn" { self = Constants.opc_freturn; }
|
  "@fsub" { self = Constants.opc_fsub; }
|
  "@i2b" { self = Constants.opc_i2b; }
|
  "@i2c" { self = Constants.opc_i2c; }
|
  "@i2d" { self = Constants.opc_i2d; }
|
  "@i2f" { self = Constants.opc_i2f; }
|
  "@i2l" { self = Constants.opc_i2l; }
|
  "@i2s" { self = Constants.opc_i2s; }
|
  "@iadd" { self = Constants.opc_iadd; }
|
  "@iaload" { self = Constants.opc_iaload; }
|
  "@iand" { self = Constants.opc_iand; }
|
  "@iastore" { self = Constants.opc_iastore; }
|
  "@idiv" { self = Constants.opc_idiv; }
|
  "@imul" { self = Constants.opc_imul; }
|
  "@ineg" { self = Constants.opc_ineg; }
|
  "@ior" { self = Constants.opc_ior; }
|
  "@irem" { self = Constants.opc_irem; }
|
  "@ireturn" { self = Constants.opc_ireturn; }
|
  "@ishl" { self = Constants.opc_ishl; }
|
  "@ishr" { self = Constants.opc_ishr; }
|
  "@isub" { self = Constants.opc_isub; }
|
  "@iushr" { self = Constants.opc_iushr; }
|
  "@ixor" { self = Constants.opc_ixor; }
|
  "@l2d" { self = Constants.opc_l2d; }
|
  "@l2f" { self = Constants.opc_l2f; }
|
  "@l2i" { self = Constants.opc_l2i; }
|
  "@ladd" { self = Constants.opc_ladd; }
|
  "@laload" { self = Constants.opc_laload; }
|
  "@land" { self = Constants.opc_land; }
|
  "@lastore" { self = Constants.opc_lastore; }
|
  "@lcmp" { self = Constants.opc_lcmp; }
|
  "@ldiv" { self = Constants.opc_ldiv; }
|
  "@lmul" { self = Constants.opc_lmul; }
|
  "@lneg" { self = Constants.opc_lneg; }
|
  "@lor" { self = Constants.opc_lor; }
|
  "@lrem" { self = Constants.opc_lrem; }
|
  "@lreturn" { self = Constants.opc_lreturn; }
|
  "@lshl" { self = Constants.opc_lshl; }
|
  "@lshr" { self = Constants.opc_lshr; }
|
  "@lsub" { self = Constants.opc_lsub; }
|
  "@lushr" { self = Constants.opc_lushr; }
|
  "@lxor" { self = Constants.opc_lxor; }
|
  "@monitorenter" { self = Constants.opc_monitorenter; }
|
  "@monitorexit" { self = Constants.opc_monitorexit; }
|
  "@nop" { self = Constants.opc_nop; }
|
  "@pop" { self = Constants.opc_pop; }
|
  "@pop2" { self = Constants.opc_pop2; }
|
  "@return" { self = Constants.opc_return; }
|
  "@saload" { self = Constants.opc_saload; }
|
  "@sastore" { self = Constants.opc_sastore; }
|
  "@swap" { self = Constants.opc_swap; }
;

aPushLiteralInstruction []
  returns [Instruction self]
{
  Object	lit;
}
:
  "@const"
  (
    lit = aDoubleLiteral[]
      { self = new PushLiteralInstruction(((Double)lit).doubleValue()); }
  |
    lit = aFloatLiteral[]
      { self = new PushLiteralInstruction(((Float)lit).floatValue()); }
  |
    lit = aIntegerLiteral[]
      { self = new PushLiteralInstruction(((Integer)lit).intValue()); }
  |
    lit = aLongLiteral[]
      { self = new PushLiteralInstruction(((Long)lit).longValue()); }
  |
    lit = aStringLiteral[]
      { self = new PushLiteralInstruction((String)lit); }
  )
;

aFieldRefInstruction []
  returns [Instruction self]
{
  int		opcode;
  String	name;
  String	type;
}
:
  opcode = aFieldRefOpcode[] type = aFieldSignature[] name = aName[]
    { self = new FieldRefInstruction(opcode, name, type); }
;

aFieldRefOpcode []
  returns [int self]
:
  "@getfield" { self = Constants.opc_getfield; }
|
  "@getstatic" { self = Constants.opc_getstatic; }
|
  "@putfield" { self = Constants.opc_putfield; }
|
  "@putstatic" { self = Constants.opc_putstatic; }
;

aMethodRefInstruction []
  returns [Instruction self]
{
  int		opcode;
  String	name;
  String	type;
}
:
  opcode = aMethodRefOpcode[]
  type = aFieldSignature[]
  name = aName[]
  type = aMethodSignature[type]
    { self = new MethodRefInstruction(opcode, name, type); }
;

aMethodRefOpcode []
  returns [int self]
:
  "@invokestatic" { self = Constants.opc_invokestatic; }
|
  "@invokespecial" { self = Constants.opc_invokespecial; }
|
  "@invokevirtual" { self = Constants.opc_invokevirtual; }
;

aClassRefInstruction []
  returns [Instruction self]
{
  int		opcode;
  String	name;
}
:
  opcode = aClassRefOpcode[] name = aClassSignature[true]
    { self = new ClassRefInstruction(opcode, name); }
;

aClassRefOpcode []
  returns [int self]
:
  "@checkcast" { self = Constants.opc_checkcast; }
|
  "@instanceof" { self = Constants.opc_instanceof; }
|
  "@new" { self = Constants.opc_new; }
|
  "@anewarray" { self = Constants.opc_anewarray; }
;

aJumpInstruction []
  returns [Instruction self]
{
  int			opcode;
  LabelReference	target;
}
:
  opcode = aJumpOpcode[] target = aLabelRef[]
    { self = new JumpInstruction(opcode, target); }
;

aJumpOpcode []
  returns [int self]
:
  "@goto" { self = Constants.opc_goto; }
|
  "@if_acmpeq" { self = Constants.opc_if_acmpeq; }
|
  "@if_acmpne" { self = Constants.opc_if_acmpne; }
|
  "@if_icmpeq" { self = Constants.opc_if_icmpeq; }
|
  "@if_icmpne" { self = Constants.opc_if_icmpne; }
|
  "@if_icmplt" { self = Constants.opc_if_icmplt; }
|
  "@if_icmpge" { self = Constants.opc_if_icmpge; }
|
  "@if_icmpgt" { self = Constants.opc_if_icmpgt; }
|
  "@if_icmple" { self = Constants.opc_if_icmple; }
|
  "@ifeq" { self = Constants.opc_ifeq; }
|
  "@ifne" { self = Constants.opc_ifne; }
|
  "@iflt" { self = Constants.opc_iflt; }
|
  "@ifge" { self = Constants.opc_ifge; }
|
  "@ifgt" { self = Constants.opc_ifgt; }
|
  "@ifle" { self = Constants.opc_ifle; }
|
  "@ifnonnull" { self = Constants.opc_ifnonnull; }
|
  "@ifnull" { self = Constants.opc_ifnull; }
|
  "@jsr" { self = Constants.opc_jsr; }
;

aLocalVarInstruction []
  returns [Instruction self]
{
  int		opcode;
  int		var;
}
:
  opcode = aLocalVarOpcode[] var = aInteger[]
    { self = new LocalVarInstruction(opcode, var); }
;

aLocalVarOpcode []
  returns [int self]
:
  "@iload" { self = Constants.opc_iload; }
|
  "@fload" { self = Constants.opc_fload; }
|
  "@aload" { self = Constants.opc_aload; }
|
  "@lload" { self = Constants.opc_lload; }
|
  "@dload" { self = Constants.opc_dload; }
|
  "@istore" { self = Constants.opc_istore; }
|
  "@fstore" { self = Constants.opc_fstore; }
|
  "@astore" { self = Constants.opc_astore; }
|
  "@lstore" { self = Constants.opc_lstore; }
|
  "@dstore" { self = Constants.opc_dstore; }
|
  "@ret" { self = Constants.opc_ret; }
;

aNewarrayInstruction []
  returns [Instruction self]
{
  String	name;
  byte		code;
}
:
  "@newarray" name = aIdentifier[]
    {
      if (name.equals("boolean")) {
	code = 4;
      } else if (name.equals("char")) {
	code = 5;
      } else if (name.equals("float")) {
	code = 6;
      } else if (name.equals("double")) {
	code = 7;
      } else if (name.equals("byte")) {
	code = 8;
      } else if (name.equals("short")) {
	code = 9;
      } else if (name.equals("int")) {
	code = 10;
      } else if (name.equals("long")) {
	code = 11;
      } else {
	throw new RecognitionException("Bad type " + name + " for newarray");
      }
      self = new NewarrayInstruction(code);
    }
;

aMultiarrayInstruction []
  returns [Instruction self]
{
  String	type;
  int		dims;
}
:
  "@multianewarray" type = aClassSignature[true] dims = aInteger[]
    { self = new MultiarrayInstruction(type, dims); }
;

aInvokeinterfaceInstruction []
  returns [Instruction self]
{
  String	name;
  String	type;
  int		args;
}
:
  "@invokeinterface"
  type = aFieldSignature[]
  name = aName[]
  type = aMethodSignature[type]
  args = aInteger[]
    { self = new InvokeinterfaceInstruction(name, type, args); }
;

aIincInstruction []
  returns [Instruction self]
{
  int		var;
  int		inc;
}
:
  "@iinc" var = aInteger[] inc = aInteger[]
    { self = new IincInstruction(var, inc); }
;

// switch
//     <value> : <label>
//     <value> : <label>
//     ...
//     default : <label>
aSwitchInstruction []
  returns [Instruction self]
{
  Vector		matches = new Vector();
  Vector		targets = new Vector();
  int			value;
  LabelReference	target;
}
:
  "@switch"
  (
    value = aInteger[] COLON target = aLabelRef[]
      {
	matches.addElement(new Integer(value));
	targets.addElement(target);
      }
  )*
  "@default" COLON target = aLabelRef[]
    { self = new SwitchInstruction(target, matches, targets); }
;

aLabelRef []
  returns [LabelReference self = null]
{
  String	name;
}
:
  name = aIdentifier[]
    { self = new LabelReference(name); }
;

aMethodSignature [String returnType]
  returns [String self]
{
  StringBuffer	buffer = new StringBuffer();
  String	type;
}
:
  LPAREN
    { buffer.append('('); }
  (
    type = aFieldSignature[]
      { buffer.append(type); }
    (
      COMMA type = aFieldSignature[]
        { buffer.append(type); }
    )*
  )?
  RPAREN
    {
      buffer.append(')');
      buffer.append(returnType);

      self = buffer.toString();
    }
;

aClassSignature [boolean arrayAllowed]
  returns [String self = null]
{
  String	name;
  int		dims = 0;
}
:
  name = aName[] ( LBRACK RBRACK { dims += 1; } )*
    {
      if (dims == 0) {
	self = name.replace('.', '/');
      } else {
	if (! arrayAllowed) {
	  throw new RecognitionException("arrays are not allowed here");
	}
	self = convertFieldSignature(name, dims);
      }
    }
;

aFieldSignature []
  returns [String self = null]
{
  String	name;
  int		dims = 0;
}
:
  name = aName[] ( LBRACK RBRACK { dims += 1; } )*
    { self = convertFieldSignature(name, dims); }
;

aName []
  returns [String self]
{
  String	ident;
}
:
  ident = aIdentifier[]
    { self = ident; }
  (
    DOT ident = aIdentifier[]
      { self += "/" + ident; }
  )*
;

aIdentifier []
  returns [String self = null]
:
  id1 : IDENT
    { self = id1.getText(); }
|
  id2 : QUOTED_IDENT
    { self = id2.getText().substring(0, id2.getText().length() - 1); }
|
  INIT
    { self = "<init>"; }
|
  CLINIT
    { self = "<clinit>"; }
;

aInteger []
  returns [int self]
{
  Integer	lit;
}
:
  lit = aIntegerLiteral[]
    { self = lit.intValue(); }
;

aLiteral []
  returns [Object self = null]
:
  self = aDoubleLiteral[]
|
  self = aFloatLiteral[]
|
  self = aIntegerLiteral[]
|
  self = aLongLiteral[]
|
  self = aStringLiteral[]
;

aDoubleLiteral []
  returns [Double self]
:
  token : NUM_DOUBLE
    {
      String	text = token.getText();

      if (text.startsWith("<NaN>")) {
	self = new Double(Double.NaN);
      } else if (text.startsWith("<Inf>")) {
	self = new Double(Double.POSITIVE_INFINITY);
      } else if (text.startsWith("-<Inf>")) {
	self = new Double(Double.NEGATIVE_INFINITY);
      } else {
	try {
	  self = new Double(text);
	} catch (NumberFormatException e) {
	  throw new RecognitionException(e.toString());
	}
      }
    }
;

aFloatLiteral []
  returns [Float self]
:
  token : NUM_FLOAT
    {
      String	text = token.getText();

      if (text.startsWith("<NaN>")) {
	self = new Float(Float.NaN);
      } else if (text.startsWith("<Inf>")) {
	self = new Float(Float.POSITIVE_INFINITY);
      } else if (text.startsWith("-<Inf>")) {
	self = new Float(Float.NEGATIVE_INFINITY);
      } else {
	try {
	  self = new Float(text);
	} catch (NumberFormatException e) {
	  throw new RecognitionException(e.toString());
	}
      }
    }
;

aIntegerLiteral []
  returns [Integer self]
:
  token : INT_LITERAL
    {
      try {
	self = new Integer(at.dms.compiler.base.NumberParser.decodeInt(token.getText()));
      } catch (NumberFormatException e) {
	throw new RecognitionException(e.toString());
      }
    }
;

aLongLiteral []
  returns [Long self]
:
  token : LONG_LITERAL
    {
      try {
	self = new Long(at.dms.compiler.base.NumberParser.decodeLong(token.getText().substring(0, token.getText().length() - 1)));
      } catch (NumberFormatException e) {
	throw new RecognitionException(e.toString());
      }
    }
;

aStringLiteral []
  returns [String self]
:
  token : STRING_LITERAL
    {
      self = token.getText();
      self = Utils.convertString(self);
    }
;

//!!! graf 990710: not used !!!
constant
	:	NUM_INT
	|	NUM_FLOAT
	;
//-----------------------------------------------------------------------------
// THE SCANNER STARTS HERE
//-----------------------------------------------------------------------------

class KsmLexer extends Lexer;

options {
  importVocab=Ksm;       // call the vocabulary
  testLiterals=false;    // don't automatically test for literals
  k=4;                   // four characters of lookahead
}

//DOT		: '.';
COLON		: ':';
COMMA		: ',';
EQUAL		: '=';
LBRACE		: '{';
LBRACK		: '[';
LPAREN		: '(';
RBRACE		: '}';
RBRACK		: ']';
RPAREN		: ')';
SEMI		: ';';
SLASH		: '/';

INIT		: "<init>";
CLINIT		: "<clinit>";

WS :
  (
    ' '
  |
    '\t'
  |
    '\f'
  |
    (
      "\r\n"  // Evil DOS
    |
      '\r'    // Macintosh
    |
      '\n'    // Unix (the right way)
    )
      { newline(); }
  )
    { _ttype = Token.SKIP; }
;

// Single-line comments
SL_COMMENT :
  "//" (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
    { _ttype = Token.SKIP; newline(); }
;

// multiple-line comments
ML_COMMENT :
  "/*"
  (
    { LA(2)!='/' }? '*'
  |
    '\n' { newline(); }
  |
    ~('*'|'\n')
  )*
  "*/"
    { _ttype = Token.SKIP; }
;


// character literals
QUOTED_IDENT :
  '\'' (ESC|~('\''|'\\'))* '\''
;

// string literals
STRING_LITERAL :
  '"' (ESC|~('"'|'\\'))* '"'
;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC :
  '\\'
  (
    'n'
  |
    'r'
  |
    't'
  |
    'b'
  |
    'f'
  |
    '"'
  |
    '\''
  |
    '\\'
  |
    ('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
  |
    ('0'..'3')
    (
      options { warnWhenFollowAmbig = false; } :
      ('0'..'9')
      (
        options { warnWhenFollowAmbig = false; } :
	'0'..'9'
      )?
    )?
    |
    ('4'..'7')
    (
       options { warnWhenFollowAmbig = false; } :
       ('0'..'9')
    )?
  )
;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT :
  ('0'..'9'|'A'..'F'|'a'..'f')
;


// a dummy rule to force vocabulary to be all characters (except special
//   ones that ANTLR uses internally (0 to 2)
protected
VOCAB :
  '\3'..'\377'
;


// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
IDENT
  options { testLiterals=true; }
:
  ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
  //	:	ULETTER ( ULETTER |  ('0'..'9'))*
;

// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
DUMMY
  options { testLiterals=true; }
:
  '@' ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
  //	:	ULETTER ( ULETTER |  ('0'..'9'))*
;

/*
// an unicode letter
protected
ULETTER
	:
	       	'\u0024' |
	       	'\u0041'..'\u005A' |
	       	'\u005F' |
	       	'\u0061'..'\u007A' |
	       	'\u00C0'..'\u00D6' |
	       	'\u00D8'..'\u00F6' |
	       	'\u00F8'..'\u00FF'
	;
*/

// a numeric literal
INT_LITERAL
{
  boolean	isDecimal = false;
}
:
  ( MINUS )?
  (
    '.' { _ttype = DOT; }
    (('0'..'9')+ (EXPONENT)? (FLOAT_SUFFIX)? { _ttype = NUM_FLOAT; })?
  |
    (
      '0' { isDecimal = true; } // special case for just '0'
      (
        ('x' | 'X')
	(
	  // hex
	  // the 'e'|'E' and float suffix stuff look
	  // like hex digits, hence the (...)+ doesn't
	  // know when to stop: ambig.  ANTLR resolves
	  // it correctly by matching immediately.  It
	  // is therefor ok to hush warning.
	  options { warnWhenFollowAmbig = false; } :
	  HEX_DIGIT
	)+
      |
	('0'..'7')+									// octal
      )?
    |
      ('1'..'9') ('0'..'9')*
        { isDecimal = true; }		// non-zero decimal
    )
    (
      ('l' | 'L')
        { _ttype = LONG_LITERAL; }
      |
	// only check to see if it's a float if looks like decimal so far
        { isDecimal }?
	  { _ttype = NUM_FLOAT; }
	(
	  '.' ('0'..'9')* (EXPONENT)? ( FLOAT_SUFFIX | DOUBLE_SUFFIX { _ttype = NUM_DOUBLE; } )?
	|
	  EXPONENT (FLOAT_SUFFIX | DOUBLE_SUFFIX { _ttype = NUM_DOUBLE; })?
	|
	  FLOAT_SUFFIX
        |
	  DOUBLE_SUFFIX { _ttype = NUM_DOUBLE; }
	)

    )?
  |
    INF { _ttype = NUM_FLOAT; }
    ( FLOAT_SUFFIX | DOUBLE_SUFFIX { _ttype = NUM_DOUBLE; } )?
  )
|
  NAN { _ttype = NUM_FLOAT; }
    ( FLOAT_SUFFIX | DOUBLE_SUFFIX { _ttype = NUM_DOUBLE; } )?
;

// a couple protected methods to assist in matching floating point numbers
protected
MINUS
:
  '-'
;

protected
EXPONENT
:
  ('e'|'E') ('+'|MINUS)? ('0'..'9')+
;

protected
FLOAT_SUFFIX
:
  'f'|'F'
;

protected
DOUBLE_SUFFIX
:
  'd'|'D'
;

protected
INF
:
  "<Inf>"
;

protected
NAN
:
  "<NaN>"
;
