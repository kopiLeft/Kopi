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

import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.JavadocComment;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

public class KopiConstructorDeclaration extends JConstructorDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   * @param	modifiers	list of modifiers
   * @param	ident		the name of this method
   * @param	parameters	the parameters of this method
   * @param	exceptions	the exceptions throw by this method
   * @param	javadoc	is this constructor deprecated
   */
  public KopiConstructorDeclaration(TokenReference where, 
                                    int modifiers, 
                                    String ident, 
                                    JFormalParameter[] parameters,
                                    CReferenceType[] exceptions, 
                                    KopiConstructorBlock body,
                                    JavadocComment javadoc, 
                                    JavaStyleComment[] comments, 
                                    KopiPreconditionDeclaration precondition,
                                    KopiPostconditionDeclaration postcondition,
                                    TypeFactory factory) {
    super(where,
          modifiers,
          ident,
          parameters,
          exceptions,
          body,
          javadoc,
          comments,
          factory);
    this.precondition = precondition;
    this.postcondition = postcondition;
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @return true iff sub tree is correct enough to check code
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    CSourceMethod       method = super.checkInterface(context);

    if (precondition != null) {
      method.setPreconditionMethod(precondition.getMethod());
    }
    if (postcondition != null) {
      method.setPostconditionMethod(postcondition.getMethod());
    }
    return method;
  }


//   public void correctInterface() throws PositionedError {
//     if (context != null) {
//       //      CClassContext tmp = context;
//       checkOverriding(context);
//       context = null;
//     }
//     postcondition.correctTheInterface();
//   }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
//   public void genJavaCode(FjcPrettyPrinter p) {
//     if (decl != null) decl.accept(p);
//     if (bridge != null)
//       p.visitMethodDeclaration(this, modifiers, returnType, ident + "/*bridge*/", parameters, exceptions, body);
//     else
//       p.visitMethodDeclaration(this, modifiers, returnType, (bridges.size() == 0) ? ident : ident + "/*real*/",
//           parameters, exceptions, body);
//     for (Enumeration elements = bridges.elements(); elements.hasMoreElements(); ) {
//       ((FConstrainedMethodDeclaration)elements.nextElement()).accept(p);
//     }
//   }

//   public void genFKjcCode(FjcPrettyPrinter p) {
//     p.visitConstrainedMethodDeclaration(this, modifiers, returnType, ident, parameters, exceptions, body, ensure, require);
//   }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------
  private KopiPreconditionDeclaration     precondition;
  private KopiPostconditionDeclaration    postcondition;
}
