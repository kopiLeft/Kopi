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

package org.kopi.vkopi.comp.print;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CReferenceType;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JAssignmentExpression;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldAccessExpression;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.kopi.comp.kjc.JVariableDefinition;
import org.kopi.util.base.NotImplementedException;
import org.kopi.vkopi.comp.base.VKPrettyPrinter;

/**
 * This class represents the definition of a block in a page
 */
public class PRImportedBlock extends PRBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRImportedBlock(TokenReference where, String ident, boolean modeInnerBlock) {
    super(where, ident, null, null);

    this.modeInnerBlock = modeInnerBlock;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Returns the type of this block
   */
  public CType getType() {
    return CReferenceType.lookup(org.kopi.vkopi.lib.print.PBlock.class.getName().replace('.','/'));
  }

  /**
   * Print expression to output stream
   */
  public JExpression genConstructorCall() {
    TokenReference ref = getTokenReference();

    String	ident = getIdent();
    int		index = ident.lastIndexOf(".");
    String	prefix = index == -1 ? "" : ident.substring(0, index) + "/";
    String	name = index == -1 ? ident : ident.substring(index + 1).intern();
    if (modeInnerBlock) {
      JExpression	expr = new JUnqualifiedInstanceCreation(ref,
							CReferenceType.lookup(prefix + "BLOCK_" + name),
							JExpression.EMPTY);
      return new JAssignmentExpression(ref,
				       new JFieldAccessExpression(ref, name),
				       expr);
    } else {
      return new JFieldAccessExpression(ref, name);
    }
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    throw new NotImplementedException();
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JFieldDeclaration genFieldDeclaration() {
    if (!modeInnerBlock) {
      return null;
    }

    TokenReference	ref = getTokenReference();
    JVariableDefinition	def = new JVariableDefinition(ref,
						      ACC_PUBLIC,
						      getType(),
						      getLastIdent(),
						      null);


    return new JFieldDeclaration(ref, def, null, null);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean			modeInnerBlock;
}
