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

package at.dms.bytecode.dis;

/**
 * Conversion from opcode to ksm name
 */
public class OpcodeNames implements at.dms.bytecode.classfile.Constants {

  /**
   * Return the ksm name for this instruction
   */
  /*package*/ static String getName(int opcode) {
    return opcodeNames[opcode];
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private static String[] opcodeNames = {
    "@nop",			//    "nop",
    "@aconst_null",		//    "aconst_null",
    "@const",			//    "iconst_m1",
    "@const",			//    "iconst_0",
    "@const",			//    "iconst_1",
    "@const",			//    "iconst_2",
    "@const",			//    "iconst_3",
    "@const",			//    "iconst_4",
    "@const",			//    "iconst_5",
    "@const",			//    "lconst_0",
    "@const",			//    "lconst_1",
    "@const",			//    "fconst_0",
    "@const",			//    "fconst_1",
    "@const",			//    "fconst_2",
    "@const",			//    "dconst_0",
    "@const",			//    "dconst_1",
    "@const",			//    "bipush",
    "@const",			//    "sipush",
    "@const",			//    "ldc",
    "@const",			//    "ldc_w",
    "@const",			//    "ldc2_w",
    "@iload",			//    "iload",
    "@lload",			//    "lload",
    "@fload",			//    "fload",
    "@dload",			//    "dload",
    "@aload",			//    "aload",
    "@iload",			//    "iload_0",
    "@iload",			//    "iload_1",
    "@iload",			//    "iload_2",
    "@iload",			//    "iload_3",
    "@lload",			//    "lload_0",
    "@lload",			//    "lload_1",
    "@lload",			//    "lload_2",
    "@lload",			//    "lload_3",
    "@fload",			//    "fload_0",
    "@fload",			//    "fload_1",
    "@fload",			//    "fload_2",
    "@fload",			//    "fload_3",
    "@dload",			//    "dload_0",
    "@dload",			//    "dload_1",
    "@dload",			//    "dload_2",
    "@dload",			//    "dload_3",
    "@aload",			//    "aload_0",
    "@aload",			//    "aload_1",
    "@aload",			//    "aload_2",
    "@aload",			//    "aload_3",
    "@iaload",			//    "iaload",
    "@laload",			//    "laload",
    "@faload",			//    "faload",
    "@daload",			//    "daload",
    "@aaload",			//    "aaload",
    "@baload",			//    "baload",
    "@caload",			//    "caload",
    "@saload",			//    "saload",
    "@istore",			//    "istore",
    "@lstore",			//    "lstore",
    "@fstore",			//    "fstore",
    "@dstore",			//    "dstore",
    "@astore",			//    "astore",
    "@istore",			//    "istore_0",
    "@istore",			//    "istore_1",
    "@istore",			//    "istore_2",
    "@istore",			//    "istore_3",
    "@lstore",			//    "lstore_0",
    "@lstore",			//    "lstore_1",
    "@lstore",			//    "lstore_2",
    "@lstore",			//    "lstore_3",
    "@fstore",			//    "fstore_0",
    "@fstore",			//    "fstore_1",
    "@fstore",			//    "fstore_2",
    "@fstore",			//    "fstore_3",
    "@dstore",			//    "dstore_0",
    "@dstore",			//    "dstore_1",
    "@dstore",			//    "dstore_2",
    "@dstore",			//    "dstore_3",
    "@astore",			//    "astore_0",
    "@astore",			//    "astore_1",
    "@astore",			//    "astore_2",
    "@astore",			//    "astore_3",
    "@iastore",			//    "iastore",
    "@lastore",			//    "lastore",
    "@fastore",			//    "fastore",
    "@dastore",			//    "dastore",
    "@aastore",			//    "aastore",
    "@bastore",			//    "bastore",
    "@castore",			//    "castore",
    "@sastore",			//    "sastore",
    "@pop",			//    "pop",
    "@pop2",			//    "pop2",
    "@dup",			//    "dup",
    "@dup_x1",			//    "dup_x1",
    "@dup_x2",			//    "dup_x2",
    "@dup2",			//    "dup2",
    "@dup2_x1",			//    "dup2_x1",
    "@dup2_x2",			//    "dup2_x2",
    "@swap",			//    "swap",
    "@iadd",			//    "iadd",
    "@ladd",			//    "ladd",
    "@fadd",			//    "fadd",
    "@dadd",			//    "dadd",
    "@isub",			//    "isub",
    "@lsub",			//    "lsub",
    "@fsub",			//    "fsub",
    "@dsub",			//    "dsub",
    "@imul",			//    "imul",
    "@lmul",			//    "lmul",
    "@fmul",			//    "fmul",
    "@dmul",			//    "dmul",
    "@idiv",			//    "idiv",
    "@ldiv",			//    "ldiv",
    "@fdiv",			//    "fdiv",
    "@ddiv",			//    "ddiv",
    "@irem",			//    "irem",
    "@lrem",			//    "lrem",
    "@frem",			//    "frem",
    "@drem",			//    "drem",
    "@ineg",			//    "ineg",
    "@lneg",			//    "lneg",
    "@fneg",			//    "fneg",
    "@dneg",			//    "dneg",
    "@ishl",			//    "ishl",
    "@lshl",			//    "lshl",
    "@ishr",			//    "ishr",
    "@lshr",			//    "lshr",
    "@iushr",			//    "iushr",
    "@lushr",			//    "lushr",
    "@iand",			//    "iand",
    "@land",			//    "land",
    "@ior",			//    "ior",
    "@lor",			//    "lor",
    "@ixor",			//    "ixor",
    "@lxor",			//    "lxor",
    "@iinc",			//    "iinc",
    "@i2l",			//    "i2l",
    "@i2f",			//    "i2f",
    "@i2d",			//    "i2d",
    "@l2i",			//    "l2i",
    "@l2f",			//    "l2f",
    "@l2d",			//    "l2d",
    "@f2i",			//    "f2i",
    "@f2l",			//    "f2l",
    "@f2d",			//    "f2d",
    "@d2i",			//    "d2i",
    "@d2l",			//    "d2l",
    "@d2f",			//    "d2f",
    "@i2b",			//    "i2b",
    "@i2c",			//    "i2c",
    "@i2s",			//    "i2s",
    "@lcmp",			//    "lcmp",
    "@fcmpl",			//    "fcmpl",
    "@fcmpg",			//    "fcmpg",
    "@dcmpl",			//    "dcmpl",
    "@dcmpg",			//    "dcmpg",
    "@ifeq",			//    "ifeq",
    "@ifne",			//    "ifne",
    "@iflt",			//    "iflt",
    "@ifge",			//    "ifge",
    "@ifgt",			//    "ifgt",
    "@ifle",			//    "ifle",
    "@if_icmpeq",		//    "if_icmpeq",
    "@if_icmpne",		//    "if_icmpne",
    "@if_icmplt",		//    "if_icmplt",
    "@if_icmpge",		//    "if_icmpge",
    "@if_icmpgt",		//    "if_icmpgt",
    "@if_icmple",		//    "if_icmple",
    "@if_acmpeq",		//    "if_acmpeq",
    "@if_acmpne",		//    "if_acmpne",
    "@goto",			//    "goto",
    "@jsr",			//    "jsr",
    "@ret",			//    "ret",
    "@switch",			//    "tableswitch",
    "@switch",			//    "lookupswitch",
    "@ireturn",			//    "ireturn",
    "@lreturn",			//    "lreturn",
    "@freturn",			//    "freturn",
    "@dreturn",			//    "dreturn",
    "@areturn",			//    "areturn",
    "@return",			//    "return",
    "@getstatic",		//    "getstatic",
    "@putstatic",		//    "putstatic",
    "@getfield",		//    "getfield",
    "@putfield",		//    "putfield",
    "@invokevirtual",		//    "invokevirtual",
    "@invokespecial",		//    "invokespecial",
    "@invokestatic",		//    "invokestatic",
    "@invokeinterface",		//    "invokeinterface",
    "@xxxunusedxxx",		//    "xxxunusedxxx",
    "@new",			//    "new",
    "@newarray",		//    "newarray",
    "@anewarray",		//    "anewarray",
    "@arraylength",		//    "arraylength",
    "@athrow",			//    "athrow",
    "@checkcast",		//    "checkcast",
    "@instanceof",		//    "instanceof",
    "@monitorenter",		//    "monitorenter",
    "@monitorexit",		//    "monitorexit",
    "@wide",			//    "wide",
    "@multianewarray",		//    "multianewarray",
    "@ifnull",			//    "ifnull",
    "@ifnonnull",		//    "ifnonnull",
    "@goto_w",			//    "goto_w",
    "@jsr_w"			//    "jsr_w"
  };
}
