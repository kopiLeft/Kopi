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
 * This class represents a java class in the syntax tree
 */
public class KopiStoreClassDeclaration extends JClassDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class declaration node in the syntax tree.
   * @param	isStatic        true if the postconditionmethod (and the 
   *                             original method) is static
   * @param	fields		the store fields 
   */
  public KopiStoreClassDeclaration(boolean isStatic,
                                   int index,
                                   JFieldDeclaration[] fields,
                                   TypeFactory factory)
  {
    super(TokenReference.NO_REF, 
          (isStatic) ? ACC_PROTECTED | ACC_STATIC : ACC_PROTECTED, 
          IDENT_STORE+index,
          CTypeVariable.EMPTY,
          null,
          new CReferenceType[0], 
          fields, 
          new JMethodDeclaration[0], 
          new JTypeDeclaration[0], 
          fields, 
          null, 
          null);
    //ctor without assertions!!
    setDefaultConstructor(new JConstructorDeclaration(getTokenReference(),
                                                      ACC_PROTECTED,
                                                      IDENT_STORE+index,
                                                      JFormalParameter.EMPTY,
                                                      CReferenceType.EMPTY,
                                                      new JConstructorBlock(getTokenReference(), null, new JStatement[0]),
                                                      null,
                                                      null,
                                                      factory));

  }
  protected void addDefaultInvariant(CClassContext context) throws PositionedError {
    // never add an invariant
  }
}
