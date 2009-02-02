/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * JLS 14.4: Local Variable Declaration Statement
 *
 * A local variable declaration statement declares one or more local variable names.
 */
public class JVariableDeclarationStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	vars		the variables declared by this statement
   */
  public JVariableDeclarationStatement(TokenReference where, JVariableDefinition[] vars, JavaStyleComment[] comments) {
    super(where, comments);

    this.vars = vars;
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	var		the variable declared by this statement
   */
  public JVariableDeclarationStatement(TokenReference where, JVariableDefinition var, JavaStyleComment[] comments) {
    super(where, comments);

    this.vars = new JVariableDefinition[] {var};
  }

  /**
   * Returns an array of variable definition declared by this statement
   */
  public JVariableDefinition[] getVars() {
    return vars;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Sets the variables to be for variables
   */
  public void setIsInFor() {
    for (int i = 0; i < this.vars.length; i++) {
      vars[i].setIsLoopVariable();
    }
  }

  /**
   * Unsets the variables to be for variables
   */
  public void unsetIsInFor() {
    for (int i = 0; i < this.vars.length; i++) {
      vars[i].unsetIsLoopVariable();
    }
  }

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    for (int i = 0; i < this.vars.length; i++) {
      try {
	context.getBlockContext().addVariable(vars[i]);
	vars[i].analyse(context);

	if (vars[i].hasInitializer()) {
	  context.setVariableInfo(vars[i].getIndex(), CVariableInfo.INITIALIZED);
	}
      } catch (UnpositionedError e) {
	throw new CLineError(getTokenReference(), e.getFormattedMessage());
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitVariableDeclarationStatement(this, vars);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    for (int i = 0; i < this.vars.length; i++) {
      if (vars[i].getValue() != null) {
	vars[i].getValue().genCode(context, false);
	vars[i].genStore(context);
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JVariableDefinition[]		vars;
}
