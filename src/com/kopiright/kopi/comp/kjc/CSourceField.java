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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;

/**
 * This class represents an exported member of a class (fields)
 */
public class CSourceField extends CField {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a field export
   * @param	owner		the owner of this field
   * @param     creator         creator of accessor (null if there is no need fo an accessor)
   * @param	modifiers	the modifiers on this field
   * @param	ident		the name of this field
   * @param	type		the type of this field
   * @param	deprecated	is this field deprecated
   */
  public CSourceField(CClass owner,
		      int modifiers,
		      String ident,
		      CType type,
		      boolean deprecated,
                      boolean synthetic)
  {
    super(owner, modifiers, ident, type, deprecated, synthetic);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true iff this field is used.
   */
  public boolean isUsed() {
    return used || !isPrivate() || getIdent().indexOf("$") >= 0; // $$$
  }

  /**
   * Declares this field to be used.
   */
  public void setUsed() {
    used = true;
  }

  public void setDeclarationOwner(JClassFieldDeclarator classFieldDecl) {
    this.classFieldDecl = classFieldDecl;
  }

  /**
   * @return	the value of initializer or null
   */
  public JExpression getValue() {
    if (classFieldDecl != null) {
      // lackner 04.10.2004
      // this does not what is should
      // the declaration is always already analysed
      //classFieldDecl.analyseDeclaration();
      
      // taoufik 28-05-2005
      // if a field is accessed before its analyse
      // we force the analyse
      try {
        classFieldDecl.analyse();
      } catch (PositionedError e) {
        // nothing
      }
    }
    return super.getValue();
  }

  /**
   * declare
   */
  public void setFullyDeclared(boolean fullyDeclared) {
    this.fullyDeclared = fullyDeclared && !isFinal();
  }

  /**
   *
   */
  public boolean isFullyDeclared() {
    return fullyDeclared;
  }

  /**
   *
   */
  public int getPosition() {
    return pos;
  }

  /**
   *
   */
  public void setPosition(int pos) {
    this.pos = pos;
  }



  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int			pos;
  private boolean		used;
  private boolean		fullyDeclared;
  private JClassFieldDeclarator classFieldDecl;
}
