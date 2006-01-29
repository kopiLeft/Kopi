/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
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

package com.kopiright.bytecode.ssa;

import com.kopiright.bytecode.classfile.Constants;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Class containing informations for a variables during SSA transformation
 */
public class SSAConstructorInfo {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the SSA constructor information
   *
   * @param ssaConstructor the SSA Constructor
   * @param var the variable index.
   */
  public SSAConstructorInfo(SSAConstructor ssaConstructor, int var) {
    this.ssaConstructor = ssaConstructor;
    this.var = var;
    this.definitionBlocks = new HashSet();
    int graphSize = ssaConstructor.getBasicBlockArray().length;
    this.usesOperandBox = new LinkedList[graphSize];
    this.phis = new QPhi[graphSize];
    for (int i = 0; i < usesOperandBox.length; ++i) {
      usesOperandBox[i] = new LinkedList();
    }
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the variable register
   */
  public int getVariableRegister() {
    return var;
  }

  /**
   * Add a definition block for the variable
   *
   * @param def definition block
   */
  public void addDefinitionBlock(BasicBlock def) {
    definitionBlocks.add(new Integer(def.getIndex()));
  }

  /**
   * Add a use of a variable in the list for a given basic block
   * A use can be here a definition.
   *
   * @param bb use block
   * @param operand operand where the variable is used
   */
  public void addUse(BasicBlock bb, QOperandBox operand) {
    LinkedList uses = usesOperandBox[bb.getIndex()];
    uses.add(operand);
  }

  /**
   * Get all uses (and definition) of a variable in a basic block
   *
   * @param bb the basic block
   * @param the list of QOperandBox for the variable
   */
  public Iterator getUsesAtBlock(BasicBlock bb) {
    return usesOperandBox[bb.getIndex()].iterator();
  }

  /**
   * Get a set of index blocks which define the variable
   */
  public Set getDefinitionBlocks() {
    return definitionBlocks;
  }

  /**
   * Get the phi function defined for a basic block
   *
   * @param bb the basic block
   */
  public QPhi getPhiAtBlock(BasicBlock bb) {
    return phis[bb.getIndex()];
  }

  /**
   * Add a phi catch for a given basic block
   *
   * @param bb block
   */
  public void addPhiCatch(BasicBlock bb) {
    if (phis[bb.getIndex()] == null) {
      //the type is not used, the rigth type of all operands,
      //   will be defined when passing operands to SSA form.
      QVar variable = new QVar(var, Constants.TYP_REFERENCE);
      phis[bb.getIndex()] = new QPhiCatch(variable);
    }
  }

  /**
   * Add a phi return for a given basic block
   *
   * @param bb block
   * @param s the concerned sub-routine
   */
  public void addPhiReturn(BasicBlock bb, SubRoutine s) {
    if (phis[bb.getIndex()] == null) {
      // the type is not used, the rigth type of all operands,
      // will be defined when passing operands to SSA form.
      QVar variable = new QVar(var, Constants.TYP_REFERENCE);
      phis[bb.getIndex()] = new QPhiReturn(variable, s);
    }
  }

  /**
   * Add a phi join for a given basic block
   *
   * @param bb block
   */
  public void addPhiJoin(BasicBlock bb) {
    if (phis[bb.getIndex()] == null) {
      //the type is not used, the rigth type of all operands,
      //   will be defined when passing operands to SSA form.
      QVar variable = new QVar(var, Constants.TYP_REFERENCE);
      phis[bb.getIndex()] = new QPhiJoin(variable, bb);
    }
  }

  /**
   * Remove the phi function for a given basic block
   *
   * @param bb the basic block
   */
  public void removePhiAtBlock(BasicBlock bb) {
    phis[bb.getIndex()] = null;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected int var;
  protected Set definitionBlocks; //index of definition blocks
  protected LinkedList[] usesOperandBox;//uses for each block
  protected SSAConstructor ssaConstructor;
  protected QPhi[] phis; //phi function for each block
}
