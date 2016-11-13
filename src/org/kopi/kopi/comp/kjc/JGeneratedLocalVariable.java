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

import org.kopi.compiler.base.TokenReference;
import org.kopi.util.base.InconsistencyException;

/**
 * This class represents a local variable declaration
 */
public class JGeneratedLocalVariable extends JLocalVariable {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a local variable definition
   * @param	modifiers	the modifiers on this variable
   * @param	name		the name of this variable
   * @param	type		the type of this variable
   * @param	value		the initial value
   * @param	where		the locztion of the declaration of this variable
   */
  public JGeneratedLocalVariable(TokenReference where,
				 int modifiers,
				 CType type,
				 String name,
				 JExpression value) {
    super(where, modifiers, DES_GENERATED, type, name, value);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    throw new InconsistencyException();
  }
}
