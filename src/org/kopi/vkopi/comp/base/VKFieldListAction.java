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

package org.kopi.vkopi.comp.base;

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CTypeVariable;
import org.kopi.kopi.comp.kjc.JBlock;
import org.kopi.kopi.comp.kjc.JCompoundStatement;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.JMethodDeclaration;
import org.kopi.kopi.comp.kjc.JStatement;

public class VKFieldListAction extends VKPhylum implements org.kopi.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a field field action.
   * 
   * @param where		the token reference of this node
   * @param stmt		The compound statement
   * @param name		the action name.
   * @param param		the formal parameters.
   */
  public VKFieldListAction(TokenReference where, JCompoundStatement stmt) {
    super(where);
    this.stmt = stmt;
  }
  
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  
  /**
   * Generates the field list action method.
   * @param isStatic Are we in a static context.
   * @param params The call parameters.
   * @param name The method name.
   * @return The list action method.
   */
  public JMethodDeclaration genMethod(boolean isStatic, JFormalParameter[] params, String name) {
    if (hasMethod) {
      return null;
    }
    
    this.hasMethod = true;
    return new JMethodDeclaration(getTokenReference(),
				  ACC_PUBLIC | ACC_FINAL | (isStatic ? ACC_STATIC : 0), 
                                  CTypeVariable.EMPTY,
                                  VKStdType.VDictionary,
				  name,
				  params,
				  VKUtils.TRIGGER_EXCEPTION,
				  new JBlock(getTokenReference(), new JStatement[] {stmt}, null),
				  null,
				  null);
  }

  public void genVKCode(VKPrettyPrinter p) {
    p.printBlockAction(stmt);
  }

  // ----------------------------------------------------------------------
  // Galite CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param visitor the visitor
   */
  @Override
  public void accept(VKVisitor visitor) {}

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the encapsulated statement.
   * @return The encapsulated statement.
   */
  public JCompoundStatement getStatement() {
    return stmt;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private boolean					hasMethod;
  private JCompoundStatement				stmt;
}
