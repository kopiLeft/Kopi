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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * Root class for all expressions
 */
public class JLocalVariableExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JLocalVariableExpression(TokenReference where, JLocalVariable variable) {
    super(where);
    this.variable = variable;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if this field accept assignment
   */
  public boolean isLValue(CExpressionContext context) {
    return !variable.isFinal() || !mayBeInitialized(context);
  }

  /**
   * Returns true if there must be exactly one initialization of the variable. 
   *
   * @return true if the variable is final.
   */
  public boolean isFinal() {
    return variable.isFinal();
  }
 
  /**
   * used to check, if a final variable/field is assigned
   * in a loop.
   * returns true, if it has to go through a loop to find the 
   * definition. 
   * @param     context context to start search
   */
  public boolean checkForLoop(CContext context) {
    return context.checkForLoop(variable.getIdent());
  }


  /**
   * Returns true if this field is already initialized
   */
  public boolean isInitialized(CExpressionContext context) {
    return CVariableInfo.isInitialized(context.getBodyContext().getVariableInfo(variable.getIndex()));
  }

  /**
   * Returns true if this field may be initialized (used for assignment) 
   */
  private boolean mayBeInitialized(CExpressionContext context) {
    return CVariableInfo.mayBeInitialized(context.getBodyContext().getVariableInfo(variable.getIndex()));
  }

  /**
   * Declares this variable to be initialized.
   *
   */
  public void setInitialized(CExpressionContext context) {
    context.getBodyContext().setVariableInfo(variable.getIndex(), CVariableInfo.INITIALIZED);
  }

  /**
   * Returns the position of this variable in the sets of local vars
   */
  public int getPosition() {
    return variable.getPosition();
  }

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return variable.getType();
  }

  public String getIdent() {
    return variable.getIdent();
  }

  /**
   * Tests whether this expression denotes a compile-time constant (JLS 15.28).
   *
   * @return	true iff this expression is constant
   */
  public boolean isConstant() {
    return variable.isConstant();
  }

  /**
   * Returns the literal value of this field
   */
  public JLiteral getLiteral() {
    return (JLiteral)variable.getValue();
  }

  public JLocalVariable getVariable() {
    return variable;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    if (!context.isLeftSide() || !context.discardValue()) {
      variable.setUsed();
    }
    if (context.isLeftSide()) {
      variable.setAssigned(getTokenReference(), context.getBodyContext());
    }

    check(context,
	  CVariableInfo.isInitialized(context.getBodyContext().getVariableInfo(variable.getIndex()))
	  || (context.isLeftSide() && context.discardValue()),
	  KjcMessages.UNINITIALIZED_LOCAL_VARIABLE, variable.getIdent());

    if (variable.isConstant() && !context.isLeftSide()) {
      return variable.getValue();
    }

    return this;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitLocalVariableExpression(this, variable.getIdent());
  }

  public boolean equals(Object o) {
    return (o instanceof JLocalVariableExpression) &&
      variable.equals(((JLocalVariableExpression)o).variable);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    if (! discardValue) {
      setLineNumber(code);
      variable.genLoad(context);
    }
  }

  /**
   * Generates JVM bytecode to store a value into the storage location
   * denoted by this expression.
   *
   * Storing is done in 3 steps :
   * - prefix code for the storage location (may be empty),
   * - code to determine the value to store,
   * - suffix code for the storage location.
   *
   * @param	code		the code list
   */
  public void genStartStoreCode(GenerationContext context) {
    // nothing to do here
  }

  /**
   * Generates JVM bytecode to for compound assignment, pre- and 
   * postfix expressions.
   *
   * @param	code		the code list
   */
  public void genStartAndLoadStoreCode(GenerationContext context, boolean discardValue) {
    genCode(context, discardValue);
  }

  /**
   * Generates JVM bytecode to store a value into the storage location
   * denoted by this expression.
   *
   * Storing is done in 3 steps :
   * - prefix code for the storage location (may be empty),
   * - code to determine the value to store,
   * - suffix code for the storage location.
   *
   * @param	code		the code list
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genEndStoreCode(GenerationContext context, boolean discardValue) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    if (!discardValue) {
      int	opcode;

      if (getType(factory).getSize() == 2) {
	opcode = opc_dup2;
      } else {
	opcode = opc_dup;
      }
      code.plantNoArgInstruction(opcode);
    }
    variable.genStore(context);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  JLocalVariable	variable;
}
