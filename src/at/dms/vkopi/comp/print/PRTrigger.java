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

package at.dms.vkopi.comp.print;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.util.base.NotImplementedException;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.vkopi.comp.base.VKPhylum;
import at.dms.vkopi.comp.base.VKPrettyPrinter;
import at.dms.vkopi.lib.print.PSPrintException;

/**
 * This class represents the definition of a block in a page
 */
public class PRTrigger extends VKPhylum implements at.dms.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where		position in source code
   * @param parent		compilation unit where it is defined
   */
  public PRTrigger(TokenReference where, String ident, JFormalParameter[] params, PRSourceElement[] elems) {
    super(where);

    this.ident = ident;
    this.params = params;
    this.elems = elems;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the ident of this trigger
   */
  public String getIdent() {
    return ident;
  }
  public void setIdent(String str) {
    ident = str;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param page	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, PRTextBlock blk) throws PositionedError {
    for (int i = 0; i < elems.length; i++) {
      elems[i].checkCode(context, blk);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * List expression to output stream
   */
  public JMethodDeclaration genCode() {
    TokenReference	ref = getTokenReference();
    JStatement[]	stmts = new JStatement[elems.length + 2];

    stmts[0] = new JExpressionStatement(ref,
					new JMethodCallExpression(ref,
								  null,
								  "initTrigger",
								  JExpression.EMPTY),
					null);

    for (int i = 0; i < elems.length; i++) {
      stmts[1 + i] = elems[i].genCode();
    }

    stmts[stmts.length - 1] =
      new JExpressionStatement(ref,
			       new JMethodCallExpression(ref,
							 null,
							 "endTrigger",
							 JExpression.EMPTY),
			       null);

    return new JMethodDeclaration(ref,
				  ACC_PUBLIC,
                                  CTypeVariable.EMPTY,
				  CStdType.Void,
				  getIdent(),
				  params,
				  TRIGGER_EXCEPTION,
				  new JBlock(ref, stmts, null),
				  null,
				  null);
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the listwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    throw new NotImplementedException();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final CReferenceType[]	TRIGGER_EXCEPTION = new CReferenceType[]{CReferenceType.lookup("java/sql/SQLException"),
                                                                                 CReferenceType.lookup(PSPrintException.class.getName().replace('.', '/')),
                                                                                 CReferenceType.lookup("at/dms/vkopi/lib/visual/VException")};

  private JFormalParameter[]	params;
  private PRSourceElement[]	elems;
  private String		ident;
}
