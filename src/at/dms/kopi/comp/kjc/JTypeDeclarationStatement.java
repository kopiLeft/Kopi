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
 * $Id: JTypeDeclarationStatement.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * JLS 14.3: Local Class Declaration
 *
 * A local type declaration declaration statement declares one type declaration in a body of a method.
 */
public class JTypeDeclarationStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	decl		the type declaration
   */
  public JTypeDeclarationStatement(TokenReference where, JTypeDeclaration decl) {
    super(where, null);
    this.decl = decl;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    CClass	owner = context.getClassContext().getCClass();
    String	prefix = owner.getQualifiedName() + "$" + context.getClassContext().getNextSyntheticIndex()+ "$" ;

    decl.generateInterface(context.getClassReader(), owner, prefix);
    decl.getCClass().setLocal(true);

    try {
      context.getBlockContext().addClass(decl.getCClass());
    } catch (UnpositionedError cue) {
      throw cue.addPosition(getTokenReference());
    }

    decl.join(context);
    decl.checkInterface(context);
    if (context.isStaticContext()) {
      decl.getCClass().setModifiers(decl.getCClass().getModifiers() | ACC_STATIC);
    } else {
      if (!decl.getCClass().isStatic()) {
        decl.addOuterThis();
      }
    }
    decl.checkInitializers(context);
    decl.checkTypeBody(context);
    context.getClassContext().getTypeDeclaration().addLocalTypeDeclaration(decl);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);
    p.visitTypeDeclarationStatement(this, decl);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    // nothing to do here
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JTypeDeclaration		decl;
}
