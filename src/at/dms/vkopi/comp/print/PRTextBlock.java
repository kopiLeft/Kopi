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
 * $Id: PRTextBlock.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.vkopi.comp.base.VKContext;

/**
 * This class represents the definition of a block in a page
 */
public class PRTextBlock extends PRBlock {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param where position in source code
   * @param parent compilation unit where it is defined
   */
  public PRTextBlock(TokenReference where,
		     CParseClassContext context,
		     String ident,
		     PRPosition pos,
		     String style,
		     boolean rec,
		     PRTrigger[] triggers) {
    super(where, ident, pos, style);

    this.context = context;
    this.rec = rec;
    this.triggers = triggers;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param page	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, PRPage page) throws PositionedError {
    super.checkCode(context, page);

    for (int i = 0; i < triggers.length; i++) {
      triggers[i].checkCode(context, this);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param page	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JClassDeclaration genCode(TypeFactory factory) {
    TokenReference	ref = getTokenReference();

    for (int i = 0; i < triggers.length; i++) {
      context.addMethodDeclaration(triggers[i].genCode());
    }

    JConstructorCall	superCall;

    superCall = new JConstructorCall(getTokenReference(),
				     false,
				     new JExpression[] {
				       PRUtils.toExpression(getTokenReference(), getIdent()),
				       getPosition().genPosition(),
				       getPosition().genSize(),
				       getStyle() == null ? new JNullLiteral(getTokenReference()) : getStyle().getStyle(),
				       PRUtils.toExpression(getTokenReference(), rec)
				     });

    JConstructorDeclaration cstr = new JConstructorDeclaration(ref,
							       ACC_PUBLIC,
							       "BLOCK_" + getIdent(),
							       JFormalParameter.EMPTY,
							       CReferenceType.EMPTY,
							       new JConstructorBlock(ref,
                                                                                     superCall,
                                                                                     new JStatement[0]),
							       null,
							       null,
                                                               factory);
    context.addMethodDeclaration(cstr);

    return new JClassDeclaration(getTokenReference(),
				 ACC_PUBLIC,
				 "BLOCK_" + getIdent(), 
                                 CTypeVariable.EMPTY,
				 CReferenceType.lookup(getSuperName()),
				 CReferenceType.EMPTY,
				 context.getFields(),
				 context.getMethods(),
				 context.getInnerClasses(),
				 context.getBody(),
				 null,
				 null);
  }

  public String getSuperName() {
    return at.dms.vkopi.lib.print.PTextBlock.class.getName().replace('.','/');
  }

  public CType getType() {
    return CReferenceType.lookup(at.dms.vkopi.lib.print.PTextBlock.class.getName().replace('.','/'));
  }

  /**
   * Print expression to output stream
   */
  public JExpression genConstructorCall()  {
    TokenReference ref = getTokenReference();
    JExpression expr = new JUnqualifiedInstanceCreation(ref,
						CReferenceType.lookup("BLOCK_" + getIdent()),
						JExpression.EMPTY);
    JExpression left = new JFieldAccessExpression(ref, getIdent());

    return new JAssignmentExpression(ref, left, expr);
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpressionStatement genDefinition(JClassDeclaration decl) {
    return null;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean		rec;
  private PRTrigger[]		triggers;
  private CParseClassContext	context;
}
