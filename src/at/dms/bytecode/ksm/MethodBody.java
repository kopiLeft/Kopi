/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: MethodBody.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.bytecode.ksm;

import java.util.Hashtable;
import java.util.Vector;

import at.dms.bytecode.classfile.AccessorContainer;
import at.dms.bytecode.classfile.AccessorTransformer;
import at.dms.bytecode.classfile.BadAccessorException;
import at.dms.bytecode.classfile.CodeInfo;
import at.dms.bytecode.classfile.HandlerInfo;
import at.dms.bytecode.classfile.Instruction;
import at.dms.bytecode.classfile.InstructionAccessor;
import at.dms.bytecode.classfile.LocalVariableInfo;
import at.dms.bytecode.classfile.LineNumberInfo;
import at.dms.bytecode.classfile.SwitchInstruction;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.Utils;

/**
 * This class represent the definition of a method body
 */
public class MethodBody {

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Add an instruction at the end of the instruction list
   */
  public void addInstruction(Instruction insn) {
    insns.addElement(insn);
  }

  /**
   * Add a label at the end of the instruction list
   *
   * @exception	at.dms.bytecode.ksm.KsmError	an assembler error
   */
  public void addLabel(String name) throws KsmError {
    if (labels.contains(name)) {
      throw new KsmError(null, KsmMessages.LABEL_REDEFINED, name);
    }

    labels.put(name, new Integer(insns.size()));
  }

  /**
   * Add an exception handler info
   */
  public void addHandler(String thrown,
			 LabelReference start,
			 LabelReference end,
			 LabelReference handler)
  {
    handlers.addElement(new HandlerInfo(start, end, handler, thrown));
  }

  /**
   * Add a line number info
   */
  public void addLineNumber(int line) {
    String	label;

    // create a unique label (syntactically illegal: cannot conflict with labels in source)
    label = "'" + lineNumberId++;
    try {
      addLabel(label);
    } catch (KsmError e) {
      throw new InconsistencyException();
    }
    lines.addElement(new LineNumberInfo((short)line, new LabelReference(label)));
  }

  /**
   * Add a local variable info
   */
  public void addVariable(String name,
			  String type,
			  LabelReference start,
			  LabelReference end,
			  short slot)
  {
    variables.addElement(new LocalVariableInfo(start, end, name, type, slot));
  }

  // --------------------------------------------------------------------
  // GENERATE A CODE INFO ATTRIBUTE
  // --------------------------------------------------------------------


  /**
   * Returns the instruction at specified label.
   * @param	name		the label name
   */
  public Instruction resolveLabel(String name) throws UnresolvableLabelException {
    Integer	addr = (Integer)labels.get(name);

    if (addr == null) {
      throw new UnresolvableLabelException(KsmMessages.UNDEFINED_LABEL, name);
    }
    if (addr.intValue() >= insns.size()) {
      throw new UnresolvableLabelException(KsmMessages.NO_CODE_AT_LABEL, name);
    }
    return (Instruction)insns.elementAt(addr.intValue());
  }

  /**
   * Generate a classfile code info, do not optimize
   *
   * @exception	at.dms.bytecode.ksm.KsmError	an assembler error
   */
  public CodeInfo genCodeInfo() throws KsmError {
    Instruction[]		insns;
    HandlerInfo[]		handlers;
    LineNumberInfo[]		lines;
    LocalVariableInfo[]		variables;

    insns = (Instruction[])Utils.toArray(this.insns, Instruction.class);
    handlers = (HandlerInfo[])Utils.toArray(this.handlers, HandlerInfo.class);
    lines = (LineNumberInfo[])Utils.toArray(this.lines, LineNumberInfo.class);
    variables = (LocalVariableInfo[])Utils.toArray(this.variables, LocalVariableInfo.class);

    CodeInfo		codeInfo = new CodeInfo(insns,
						handlers,
						lines,
						variables);
    try {
      AccessorTransformer	transformer = new AccessorTransformer() {
	  public InstructionAccessor transform(InstructionAccessor accessor,
					       AccessorContainer container)
	    throws UnresolvableLabelException
	  {
	    return resolveLabel(((LabelReference)accessor).getName());
	  }
	};

      codeInfo.transformAccessors(transformer);
    } catch (UnresolvableLabelException e) {
      throw new KsmError(null, e.getFormattedMessage());
    } catch (BadAccessorException e) {
      throw new KsmError(null, KsmMessages.UNEXPECTED_EXCEPTION, e.getMessage());
    }

    //!!! graf 990903 remove asap
    for (int i = 0; i < insns.length; i++) {
      if (insns[i] instanceof SwitchInstruction) {
	((SwitchInstruction)insns[i]).selectSwitchType();
      }
    }
    //!!! graf 990903 remove asap

    return codeInfo;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Vector		insns = new Vector();
  private Vector		handlers = new Vector();
  private Vector		lines = new Vector();
  private Vector		variables = new Vector();

  private Hashtable		labels = new Hashtable();

  private int			lineNumberId = 0;		// to create unique labels
}
