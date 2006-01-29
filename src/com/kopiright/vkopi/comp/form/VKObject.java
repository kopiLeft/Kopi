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

package com.kopiright.vkopi.comp.form;

import com.kopiright.vkopi.comp.base.VKPhylum;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.kopi.comp.kjc.JCompoundStatement;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents the root class for block elements
 */
public abstract class VKObject extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Create a new block element
   *
   * @param where	the token reference of this node
   * @param pos		the position of the object in the block
   */
  public VKObject(TokenReference where, VKPosition pos) {
    super(where);
    this.pos = pos;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * Returns the position of this object within block
   */
  public VKPosition getPosition() {
    return pos;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkInterface(VKBlock block) throws PositionedError;

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the object.
   */
  public abstract void analyse(VKContext context, VKBlock block) throws PositionedError;

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  //  /**
  //   * Check expression and evaluate and alter context
  //   * @exception	PositionedError	Error catched as soon as possible
  //   */
  //  public abstract JCompoundStatement genNewObject() throws PositionedError;

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompoundStatement genAddToBlock() {
    throw new InconsistencyException();
    //pos;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected VKPosition     pos;
}
