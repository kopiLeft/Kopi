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

package at.dms.bytecode.ssa;

import at.dms.bytecode.classfile.MethodInfo;
import at.dms.bytecode.classfile.CodeInfo;
import at.dms.bytecode.classfile.HandlerInfo;
import at.dms.bytecode.classfile.LineNumberInfo;
import at.dms.bytecode.classfile.LocalVariableInfo;
import at.dms.bytecode.classfile.Instruction;

/**
 * Optimize a method using the SSA framework.
 *
 * @author: Michael Fernandez
 */
public class MethodOptimizer {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------
  public MethodOptimizer(MethodInfo method, CodeInfo info) {
    cfg = new ControlFlowGraph(method, info);

    this.info = info;
  }

  CodeInfo info;

  public CodeInfo generateCode() {
    Instruction[] instructions = cfg.getInstructions();
    HandlerInfo[] handlers = cfg.getHandlerInfos(instructions);
    CodeInfo info  = new CodeInfo(instructions,
                                  handlers,
                                  new LineNumberInfo[0],
                                  new LocalVariableInfo[0]);
    //this.info.getLocalVariables());
    return info;

  }

  // -------------------------------------------------------------------
  // STATIC METHOD TO RUN OPTIMIZATIONS ON A CLASS
  // -------------------------------------------------------------------
  public static CodeInfo optimize(MethodInfo method, CodeInfo info, SSAOptions options) {
    MethodOptimizer methOptim = new MethodOptimizer(method, info);
    return methOptim.generateCode();
  }
  public static CodeInfo optimize(MethodInfo method, SSAOptions options) {
    return optimize(method, method.getCodeInfo(), options);
  }


  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  //the control flow graph of the method.
  protected ControlFlowGraph cfg;
}
