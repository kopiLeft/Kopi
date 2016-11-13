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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CClass;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldAccessExpression;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.util.base.InconsistencyException;
import org.kopi.util.base.Utils;

/**
 * A name within an expression
 */
public class XNameExpression extends JNameExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public XNameExpression(TokenReference where, JExpression prefix, String ident) {
    super(where, prefix, ident);
    if (prefix instanceof XNameExpression) {
      ((XNameExpression)prefix).setHasSuffix(true);
    }
  }

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public XNameExpression(TokenReference where, String ident) {
    this(where, null, ident);
  }

  /**
   * Constructs a sequence of name expressions for a qualified name.
   *
   * @param	where		the position of this node in the parsing tree
   * @param	name		the fully qualified name
   */
  public static JNameExpression build(TokenReference where, String name) {
    verify(name.indexOf('.') == -1);

    String[]		splitted;
    JExpression		prefix;

    splitted = Utils.splitQualifiedName(name, '/');
    if (splitted[0] == "") {
      prefix = null;
    } else {
      prefix = XNameExpression.build(where, splitted[0]);
    }
    return new XNameExpression(where, prefix, splitted[1]);
  }
  
  /**
   * Since class field may be overloaded in sub compiler, this method allow
   * you to specify the type of class field without needed to touch
   * the huge method above !
   */
  protected JFieldAccessExpression createClassField(TokenReference ref,
                                                    JExpression prefix,
                                                    String ident,
                                                    TypeFactory factory)
  {
    // FIX 020206 lackner 
    // prefix is already analysed
    if (prefix instanceof XCursorFieldExpression) {
      return new XCursorFieldExpression(ref, prefix, ident, hasSuffix);
    } else {
      CClass	access;

      try {
        access = prefix.getType(factory).getCClass();
      } catch (InconsistencyException e) {
        throw new InconsistencyException("no class: " + ident + " at " + ref);
      }

      if (access.getSuperClass().equals(factory.createReferenceType(XTypeFactory.RFT_CURSOR).getCClass())) {
	return new XCursorFieldExpression(getTokenReference(), prefix, ident, hasSuffix);
      } else {
	return new JFieldAccessExpression(ref, prefix, ident);
      }
    }
  }

  /**
   * Since class field may be overloaded in sub compiler, this method allow
   * you to specifie the type of class field without needed to touch
   * the huge method above !
   */
  protected JFieldAccessExpression createClassField(TokenReference ref,
						   String ident)
  {
    return new JFieldAccessExpression(ref, ident);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public void setHasSuffix(boolean suffix) {
    hasSuffix = suffix;
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  private	boolean	hasSuffix;
}
