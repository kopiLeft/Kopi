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

import java.util.Vector;
import at.dms.bytecode.classfile.AccessorContainer;
import at.dms.bytecode.classfile.AbstractInstructionAccessor;
import at.dms.bytecode.classfile.ClassConstant;
import at.dms.bytecode.classfile.ClassRefInstruction;
import at.dms.bytecode.classfile.FieldRefConstant;
import at.dms.bytecode.classfile.FieldRefInstruction;
import at.dms.bytecode.classfile.IincInstruction;
import at.dms.bytecode.classfile.Instruction;
import at.dms.bytecode.classfile.InterfaceConstant;
import at.dms.bytecode.classfile.InvokeinterfaceInstruction;
import at.dms.bytecode.classfile.JumpInstruction;
import at.dms.bytecode.classfile.LineNumberInfo;
import at.dms.bytecode.classfile.LocalVarInstruction;
import at.dms.bytecode.classfile.MethodRefConstant;
import at.dms.bytecode.classfile.MethodRefInstruction;
import at.dms.bytecode.classfile.MultiarrayInstruction;
import at.dms.bytecode.classfile.NewarrayInstruction;
import at.dms.bytecode.classfile.NoArgInstruction;
import at.dms.bytecode.classfile.PushLiteralInstruction;
import at.dms.bytecode.classfile.SwitchInstruction;

/**
 *
 */
class InstructionHandle extends AbstractInstructionAccessor {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Creates a new instruction handle.
   *
   * @param	instruction		the instruction
   * @param	address		the address of the instruction
   */
  public InstructionHandle(Instruction instruction, int address) {
    this.instruction = instruction;
    this.address = address;

    this.isTarget = false;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Notifies this handle that is has been attached to the specified container.
   */
  public void attachTo(AccessorContainer container) {
    if (container instanceof LineNumberInfo) {
      if (lineNumbers == null) {
	lineNumbers = new Vector();
      }
      lineNumbers.addElement(new Integer(((LineNumberInfo)container).getLine()));
    } else {
      // Declares this instruction to be target of a jump, handler, ...
      this.isTarget = true;
    }
  }

  /**
   * Returns an identifier for the instruction address.
   */
  public String getLabel() {
    return "_L" + address;
  }

  // --------------------------------------------------------------------
  // OUTPUT
  // --------------------------------------------------------------------

  /**
   * Prints a byte code instruction
   */
  public void write(IndentingWriter out, boolean optionShowStack) {
    if (lineNumbers != null) {
      for (int i = 0; i < lineNumbers.size(); i++) {
	out.println();
	out.print("@line\t" + (Integer)lineNumbers.elementAt(i));
      }
    }

    out.println();
    if (isTarget) {
      out.print(getLabel() + ":");
    }
    if (optionShowStack) {
      out.print("\t/* " + "N/A" + " */\t");
    }
    out.print("\t");
    writeInstruction(out, instruction);
  }

  /**
   * Prints an instruction (dispatches to instruction types).
   */
  private void writeInstruction(IndentingWriter out, Instruction insn) {
    if (insn instanceof ClassRefInstruction) {
      writeClassRefInstruction(out, (ClassRefInstruction)insn);
    } else if (insn instanceof FieldRefInstruction) {
      writeFieldRefInstruction(out, (FieldRefInstruction)insn);
    } else if (insn instanceof IincInstruction) {
      writeIincInstruction(out, (IincInstruction)insn);
    } else if (insn instanceof InvokeinterfaceInstruction) {
      writeInvokeinterfaceInstruction(out, (InvokeinterfaceInstruction)insn);
    } else if (insn instanceof JumpInstruction) {
      writeJumpInstruction(out, (JumpInstruction)insn);
    } else if (insn instanceof LocalVarInstruction) {
      writeLocalVarInstruction(out, (LocalVarInstruction)insn);
    } else if (insn instanceof MethodRefInstruction) {
      writeMethodRefInstruction(out, (MethodRefInstruction)insn);
    } else if (insn instanceof MultiarrayInstruction) {
      writeMultiarrayInstruction(out, (MultiarrayInstruction)insn);
    } else if (insn instanceof NewarrayInstruction) {
      writeNewarrayInstruction(out, (NewarrayInstruction)insn);
    } else if (insn instanceof NoArgInstruction) {
      writeNoArgInstruction(out, (NoArgInstruction)insn);
    } else if (insn instanceof PushLiteralInstruction) {
      writePushLiteralInstruction(out, (PushLiteralInstruction)insn);
    } else if (insn instanceof SwitchInstruction) {
      writeSwitchInstruction(out, (SwitchInstruction)insn);
    } else {
      writeOpcode(out, "@unknown");
      out.print("opcode=" + insn.getOpcode());
    }
  }

  /**
   * Prints the opcode.
   */
  private void writeOpcode(IndentingWriter out, String opcode) {
    out.print(opcode);
    out.print(opcode.length() < 8 ? "\t\t" : "\t");
  }

  /**
   * Prints the opcode.
   */
  private void writeOpcode(IndentingWriter out, int opcode) {
    writeOpcode(out, OpcodeNames.getName(opcode));
  }

  /**
   * Prints a ClassRefInstruction.
   */
  private void writeClassRefInstruction(IndentingWriter out, ClassRefInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printClassRef(out, insn.getClassConstant());
  }

  /**
   * Prints a FieldRefInstruction.
   */
  private void writeFieldRefInstruction(IndentingWriter out, FieldRefInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printFieldRef(out, insn.getFieldRefConstant());
  }

  /**
   * Prints a IincInstruction.
   */
  private void writeIincInstruction(IndentingWriter out, IincInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    out.print(insn.getVariable());
    out.print(" ");
    out.print(insn.getIncrement());
  }

  /**
   * Prints a InvokeinterfaceInstruction.
   */
  private void writeInvokeinterfaceInstruction(IndentingWriter out, InvokeinterfaceInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printInterfaceRef(out, insn.getInterfaceConstant());
    out.print(" ");
    out.print(insn.getNbArgs());
  }

  /**
   * Prints a JumpInstruction.
   */
  private void writeJumpInstruction(IndentingWriter out, JumpInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printInstructionHandle(out, (InstructionHandle)insn.getTarget());
  }

  /**
   * Prints a LocalVarInstruction.
   */
  private void writeLocalVarInstruction(IndentingWriter out, LocalVarInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    out.print(insn.getIndex());
  }

  /**
   * Prints a MethodRefInstruction.
   */
  private void writeMethodRefInstruction(IndentingWriter out, MethodRefInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printMethodRef(out, insn.getMethodRefConstant());
  }

  /**
   * Prints a MultiarrayInstruction.
   */
  private void writeMultiarrayInstruction(IndentingWriter out, MultiarrayInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    printFieldSignature(out, insn.getType());
    out.print(" ");
    out.print(insn.getDimension());
  }

  /**
   * Prints a NewarrayInstruction.
   */
  private void writeNewarrayInstruction(IndentingWriter out, NewarrayInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    out.print(insn.getType());
  }

  /**
   * Prints a NoArgInstruction.
   */
  private void writeNoArgInstruction(IndentingWriter out, NoArgInstruction insn) {
    writeOpcode(out, insn.getOpcode());
  }

  /**
   * Prints a PushLiteralInstruction.
   */
  private void writePushLiteralInstruction(IndentingWriter out, PushLiteralInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    out.print(Disassembler.convertLiteral(insn.getLiteral()));
  }

  /**
   * Prints a SwitchInstruction.
   */
  private void writeSwitchInstruction(IndentingWriter out, SwitchInstruction insn) {
    writeOpcode(out, insn.getOpcode());
    for (int i = 0; i < insn.getSwitchCount(); i++) {
      out.println();
      out.print("\t\t\t" + insn.getMatch(i) + ": ");
      printInstructionHandle(out, (InstructionHandle)insn.getTarget(i));
    }
    out.println();
    out.print("\t\t\t@default: ");
    printInstructionHandle(out, (InstructionHandle)insn.getTarget(-1));
  }

  /**
   * Prints a ClassConstant.
   */
  private void printClassRef(IndentingWriter out, ClassConstant ref) {
    String	name = ref.getName();

    if (name.charAt(0) == '[') {
      printFieldSignature(out, name);
    } else {
      printQualifiedName(out, name);
    }
  }

  private void printFieldRef(IndentingWriter out, FieldRefConstant ref) {
    printFieldSignature(out, ref.getType());
    out.print(" ");
    printQualifiedName(out, ref.getName());
  }

  private void printInterfaceRef(IndentingWriter out, InterfaceConstant ref) {
    printMethodSpec(out, ref.getName(), ref.getType());
  }

  private void printMethodRef(IndentingWriter out, MethodRefConstant ref) {
    printMethodSpec(out, ref.getName(), ref.getType());
  }

  private void printMethodSpec(IndentingWriter out, String name, String signature) {
    String[]	type = Disassembler.convertMethodSignature(signature);

    out.print(type[1] + " " + Disassembler.convertQualifiedName(name) + type[0]);
  }

  private void printFieldSignature(IndentingWriter out, String signature) {
    out.print(Disassembler.convertFieldSignature(signature));
  }

  private void printQualifiedName(IndentingWriter out, String ident) {
    out.print(Disassembler.convertQualifiedName(ident));
  }

  private void printInstructionHandle(IndentingWriter out, InstructionHandle handle) {
    out.print(handle.getLabel());
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final Instruction	instruction;
  private final int		address;

  private Vector		lineNumbers;
  private boolean		isTarget;
}
