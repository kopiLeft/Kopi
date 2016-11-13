/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.kopi.comp.kjc;

import org.kopi.bytecode.classfile.PushLiteralInstruction;
import org.kopi.compiler.base.TokenReference;

/**
 * This class is an abstract root class for binary expressions
 */
public abstract class JBinaryExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	p1		left operand
   * @param	p2		right operand
   */
  public JBinaryExpression(TokenReference where,
			   JExpression left,
			   JExpression right)
  {
    super(where);
    this.left = left;
    this.right = right;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genBooleanResultCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    setLineNumber(code);

    CodeLabel		okayLabel = new CodeLabel();
    CodeLabel		nextLabel = new CodeLabel();

    genBranch(true, context, okayLabel);		//		LEFT CODE IFNE okay
    code.plantInstruction(new PushLiteralInstruction(0)); //	FALSE
    code.plantJumpInstruction(opc_goto, nextLabel);	//		GOTO next
    code.plantLabel(okayLabel);			//	okay:
    code.plantInstruction(new PushLiteralInstruction(1)); //	TRUE
    code.plantLabel(nextLabel);			//	next	...

    if (discardValue) {
      //!!! CHECKME : optimize ???
      code.plantPopInstruction(factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN));
    }
  }

  /**
   * Generates a sequence of bytescodes to branch on a label
   * This method helps to handle heavy optimizables conditions
   * @param	code		the code list
   */
  public void genBranch(boolean cond, GenerationContext context, CodeLabel label) {
    genBranch(left, right, cond, context, label);
  }

  /**
   * Optimize a bi-conditional expression
   */
  protected void genBranch(JExpression left,
			   JExpression right,
			   boolean cond,
			   GenerationContext context,
			   CodeLabel label)
  {
    CodeSequence code = context.getCodeSequence();
        
    genCode(context, false);
    code.plantJumpInstruction(cond ? opc_ifne : opc_ifeq, label);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	CType			type;
  protected	JExpression		left;
  protected	JExpression		right;
}
