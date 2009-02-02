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

import com.kopiright.compiler.base.UnpositionedError;

/**
 * This class represents a local context during checkBody
 * It follows the control flow and maintain informations about
 * variable (initialised, used, allocated), exceptions (thrown, catched)
 * It also verify that context is still reachable
 *
 * There is a set of utilities method to access fields, methods and class
 * with the name by clamping the parsing tree
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CSwitchGroupContext extends CBodyContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context
   */
  public CSwitchGroupContext(CSwitchBodyContext parent, KjcEnvironment environment, JSwitchStatement switchStmt) {
    // clone
    super(parent, environment, parent);
    this.switchStmt = switchStmt;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * add a default label to this switch
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void addDefault() throws UnpositionedError {
    ((CSwitchBodyContext)getParentContext()).addDefault();
  }

  /**
   * add a label to this switch and check it is a new one
   * @param	lit		the literal value of this label
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void addLabel(Integer value) throws UnpositionedError {
    ((CSwitchBodyContext)getParentContext()).addLabel(value);
  }

  /**
   * Returns the type of the switch expression.
   */
  public CType getType() {
    return ((CSwitchBodyContext)getParentContext()).getType();
  }

  public JEnumSwitchInnerDeclaration getInnerEnumMap() {
    return switchStmt.getInnerEnumMap();
  }
  
  private JSwitchStatement      switchStmt;
}
