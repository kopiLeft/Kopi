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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.util.base.InconsistencyException;

/**
 * Storage for old values.
 */
public class KopiOldValueStoreCreation extends JUnqualifiedInstanceCreation {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param     postmethod      postcondition method where the storage is placed
   */
  public KopiOldValueStoreCreation(TokenReference where, CMethod postmethod)
  {
    super(where, null, JExpression.EMPTY);

    this.postmethod = postmethod;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return postmethod.getOldValueStore() == null ?
      factory.createReferenceType(TypeFactory.RFT_OBJECT) :
      postmethod.getOldValueStore();
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
    local = context.getClassContext().getCClass();
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
    // to do!!!!
    type = postmethod.getOldValueStore();
    try {
      constructor = type.getCClass().lookupMethod(null, local, null, JAV_CONSTRUCTOR, new CType[0], CReferenceType.EMPTY);
    } catch (UnpositionedError e) {
      throw new InconsistencyException("Old value store: no empty constructor found");
    }
    super.accept(p);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    type = postmethod.getOldValueStore();
    try {
      constructor = type.getCClass().lookupMethod(null, local, null, JAV_CONSTRUCTOR, new CType[0], CReferenceType.EMPTY);
    } catch (UnpositionedError e) {
      throw new InconsistencyException("Old value store: no empty constructor found");
    }
    super.genCode(context, discardValue);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CMethod		postmethod;  
}
