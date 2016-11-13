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

/**
 * This class provides the contextual information for the semantic
 * analysis of an expression.
 */
public class CExpressionContext extends CContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs the context to analyse an expression semantically.
   *
   * @param	parent		the analysis context of the entity
   *				containing the expression
   * @param	isLeftSide	is the expression the left hand side
   *				of an assignment ?
   * @param	discardValue	will the result of the evaluation of
   *				the expression be discarded ?
   */
  public CExpressionContext(CBodyContext parent,
                            KjcEnvironment environment,
			    boolean isLeftSide,
			    boolean discardValue)
  {
    super(parent, environment);
    this.isLeftSide = isLeftSide;
    this.discardValue = discardValue;
    inOld = false;
    typeName = false;
  }

  /**
   * Constructs the context to analyse an expression semantically.
   *
   * @param	parent		the analysis context of the entity
   *				containing the expression
   */
  public CExpressionContext(CBodyContext parent, KjcEnvironment environment) {
    this(parent, environment, false, false);
  }

  /**
   * Constructs the context to analyse an expression semantically.
   *
   * @param	parent		the analysis context of the entity
   *				containing the expression
   * @param	isLeftSide	is the expression the left hand side
   *				of an assignment ?
   * @param	discardValue	will the result of the evaluation of
   *				the expression be discarded ?
   */
  public CExpressionContext(CExpressionContext parent,
                            KjcEnvironment environment,
			    boolean isLeftSide,
			    boolean discardValue)
  {
    super(parent.getBodyContext(), environment);
    this.isLeftSide = isLeftSide;
    this.discardValue = discardValue;
    this.inOld = parent.isInOld();
    this.typeName = parent.isTypeName();
 }

  /**
   * Constructs the context to analyse an expression semantically.
   *
   * @param	parent		the analysis context of the entity
   *				containing the expression
   */
  public CExpressionContext(CExpressionContext parent,
                            KjcEnvironment environment) {
    this(parent, environment, false, false);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the parent context.
   */
  public CBodyContext getBodyContext() {
    if (parent instanceof CExpressionContext) {
      return ((CExpressionContext)parent).getBodyContext();
    } else {
      return (CBodyContext)parent;
    }
  }

  /**
   * Returns true iff the expression is the left hand side of an assignment.
   */
  public boolean isLeftSide() {
    return isLeftSide;
  }

  /**
   * Returns true iff the result of the evaluation of the expression
   * will be discarded.
   */
  public boolean discardValue() {
    return discardValue;
  }

  /**
   * Set iff this expression is inside the old operator
   */
  public void setInOld(boolean inOld) {
    this.inOld = inOld;
  }
  /**
   * Returns true iff this expression is inside the old operator
   */
  public boolean isInOld() {
    return inOld;
  }
  /**
   * Set iff this expression is should evaluate to a type name
   */
  public void setIsTypeName(boolean typeName) {
    this.typeName = typeName;
  }
  /**
   * Returns true iff this expression is should evaluate to a type name
   */
  public boolean isTypeName() {
    return typeName;
  }
  // ----------------------------------------------------------------------
  // PRIVATE UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Is the expression the left hand side of an assignment ?
   */
  private final boolean		isLeftSide;

  /**
   * Will the result of the evaluation of the expression be discarded ?
   */
  private final boolean		discardValue;

  /**
   * Set iff this expression is inside the old operator
   */
  private  boolean		inOld;
  /**
   * Set iff this expression should be a type name
   */
  private  boolean		typeName;
}
