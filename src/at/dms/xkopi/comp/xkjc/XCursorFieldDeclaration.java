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
 * $Id: XCursorFieldDeclaration.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.JavadocComment;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class XCursorFieldDeclaration extends JFieldDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	variable	the variable definition
   * @param	deprecated	is this field deprecated
   */
  public XCursorFieldDeclaration(TokenReference where,
				 JVariableDefinition variable,
				 JavadocComment javadoc,
				 JavaStyleComment[] comments)
  {
    super(where, variable, javadoc, comments);
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void setPosition(int pos) {
    this.pos = pos;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param	v		a vector to collect fields
   * @return	true iff sub tree is correct enought to check code
   * @exception	PositionedError	Error catched as soon as possible
   */
  public CSourceField checkInterface(CClassContext context) throws PositionedError {
    int		modifiers = variable.getModifiers();

    if (! context.getCClass().isInterface()) {
      // JLS 8.3.1 : Class Field Modifiers

      // Syntactically valid field modifiers
      check(context,
	    CModifier.isSubsetOf(modifiers,
				 ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
				 | ACC_STATIC | ACC_FINAL | ACC_TRANSIENT
				 | ACC_VOLATILE),
	    KjcMessages.NOT_CLASS_FIELD_MODIFIERS,
	    CModifier.toString(CModifier.notElementsOf(modifiers,
						       ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
						       | ACC_STATIC | ACC_FINAL | ACC_TRANSIENT
						       | ACC_VOLATILE)));
    } else {
      // JLS 9.3 : Interface Field (Constant) Declarations

      // Every field declaration in the body of an interface is
      // implicitly public, static, and final.
      modifiers |= ACC_PUBLIC | ACC_STATIC | ACC_FINAL;

      // Syntactically valid interface field modifiers
      check(context,
	    CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_FINAL | ACC_STATIC),
	    KjcMessages.NOT_INTERFACE_FIELD_MODIFIERS,
	    CModifier.toString(CModifier.notElementsOf(modifiers,
						       ACC_PUBLIC | ACC_FINAL | ACC_STATIC)));
    }

    variable.checkInterface(context);
    setInterface(new XCursorField(context.getCClass(),
				  getVariable().getModifiers(),
				  getVariable().getIdent(),
				  variable.getType()));
    ((XCursorField)getField()).setPosition(pos);
    return (CSourceField)getField();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int		pos = -1;
}
