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
 * $Id: XOperatorDeclaration.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class XOperatorDeclaration extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   * @param	modifiers	list of modifiers
   * @param	returnType	the return type of this method
   * @param	operatorToken	the token used to define this method, should be operator
   * @param	oper		the id of this oper
   * @param	parameters	the parameters of this method
   * @param	exceptions	the exceptions throw by this method
   * @param	body		the body of this method
   * @param	deprecated	is this method deprecated
   */
  public XOperatorDeclaration(TokenReference where,
			      int modifiers,
			      CType returnType,
			      String operatorToken,
			      int oper,
			      JFormalParameter[] parameters,
			      CReferenceType[] exceptions,
			      JBlock body,
			      boolean deprecated) {
    super(where,
	  modifiers,
          CTypeVariable.EMPTY,
	  returnType,
	  "operator$" + oper,
	  convertParameters(where, oper, parameters, returnType),
	  exceptions,
	  body,
	  null,
	  null);
    this.operatorToken = operatorToken;
  }

  private static JFormalParameter[] convertParameters(TokenReference where,
						      int oper,
						      JFormalParameter[] parameters,
						      CType returnType)
  {
    if ((oper == XConstants.OPE_CAST) || (oper == XConstants.OPE_SIMPLE)) {
      JFormalParameter[] newParameters = new JFormalParameter[parameters.length + 1];

      System.arraycopy(parameters, 0, newParameters, 1, parameters.length);
      newParameters[0] = new JFormalParameter(where, JLocalVariable.DES_PARAMETER, returnType, "dummy$cast", true);

      return newParameters;
    } else {
      return parameters;
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @return	true iff sub tree is correct enough to check code
   * @exception	PositionedError		Error catched as soon as possible
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    CSourceMethod meth = super.checkInterface(context);	// must be a true method

    check(context, operatorToken.equals("operator"), XKjcMessages.OPERATOR_IDENT);

    check(context, getMethod().isStatic(), XKjcMessages.OPERATOR_STATIC);
    check(context, getMethod().isPublic(), XKjcMessages.OPERATOR_PUBLIC);

    // !!! check also nb parameters and return type != void
    return meth;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String		operatorToken;
}
