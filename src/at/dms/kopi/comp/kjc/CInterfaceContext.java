/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents an interface context during check
 */
public class CInterfaceContext extends CClassContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * @param	parent		the parent context or null at top level
   * @param	clazz		the corresponding clazz
   */
  public CInterfaceContext(CContext parent, KjcEnvironment environment, CSourceClass clazz, JTypeDeclaration decl) {
    super(parent, environment, clazz, decl);
  }

  /**
   * Verify all final fields are initialized
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void close(JTypeDeclaration decl, CBodyContext virtual) throws UnpositionedError {
  }

  /**
   * Verify all final fields are initialized
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void close(JTypeDeclaration decl,
		    CVariableInfo staticC,
		    CVariableInfo instanceC,
		    CVariableInfo[] constructorsC)
    throws UnpositionedError
  {
    // getAbstractMethods test the consistence of abstract method
    self.testInheritMethods(this);
  }
}
