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
 * $Id: JTypeNameExpression.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * A 'int.class' expression
 */
public class JTypeNameExpression extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
//   public JTypeNameExpression(TokenReference where, String qualifiedName) {
//     super(where);

//     type = CReferenceType.lookup(qualifiedName);
//   }

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public JTypeNameExpression(TokenReference where, CReferenceType type) {
    super(where);

    this.type = type;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CReferenceType getClassType() {
    return type;
  }

  /**
   * Returns a qualified name for the type of this
   */
  public String getQualifiedName() {
    return type.getQualifiedName();
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
    try {
      type = (CReferenceType)type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    return this;
  }
  /**
   * Return true iff the node is itself a Expression 
   * (not only a part like JTypeName)
   */
  public boolean isExpression() {
    return false;
  }

  /**
   * Used in xkjc/XUtils. DO NOT USE ANYWHERE ELSE!!
   */
  public JExpression analyse(CBinaryTypeContext context) throws PositionedError {
    try {
      type = (CReferenceType)type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
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
    p.visitTypeNameExpression(this, type);
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
      // do nothing here
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	CReferenceType		type;
}
