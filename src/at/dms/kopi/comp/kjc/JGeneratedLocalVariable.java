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
 * $Id: JGeneratedLocalVariable.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.TokenReference;
import at.dms.util.base.InconsistencyException;

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
