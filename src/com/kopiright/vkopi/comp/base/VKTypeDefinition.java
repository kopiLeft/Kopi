/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents an TypeDefinition, ie a menu element with a name
 * and may be an icon, a shortcut and a help
 */
public class VKTypeDefinition
  extends VKDefinition
  implements com.kopiright.kopi.comp.kjc.Constants
{

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param ident		the menu item ident
   * @param menu		the menu name
   * @param item		the item name
   * @param icon		the icon name
   * @param key			the shorcut
   * @param help		the help
   */
  public VKTypeDefinition(TokenReference where,
			  String ident,
			  VKType type,
			  JFormalParameter[] params)
  {
    super(where, null, ident);

    this.type	= type;
    this.params = params;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns if the definition of this type is static
   */
  public JReturnStatement genCall(JExpression[] args) {
    TokenReference	ref = getTokenReference();
    JExpression		expr;

    expr = new JMethodCallExpression(ref,
				     access,
				     getIdent() + "_TYPE",
				     args);

    return new JReturnStatement(getTokenReference(), expr, null);
  }
  
  /**
   * Returns the action call of this type definition
   */
  public JReturnStatement genActionCall(JExpression[] args) {
    TokenReference	ref = getTokenReference();
    JExpression		expr;
    
    expr = new JMethodCallExpression(ref,
	                             access,
	                             getIdent() + "_TYPE_ACTION",
	                             args);

    return new JReturnStatement(getTokenReference(), expr, null);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param decl        the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    type.checkCode(context);
    
    if (type.getList() != null && params != null) {
      JExpression               access;
      JMethodDeclaration        decl;

      access = context.getAccess(getTokenReference(), context.isInsertFile());
      decl = genMethod(access, context.isInsertFile());
      
      if (decl != null) {
	context.getClassContext().addMethodDeclaration(decl);
      }
      
      if (type.getList().getAction() != null) {
	decl = type.getList().getAction().genMethod(isStatic, params, getIdent() + "_TYPE_ACTION");
	
	if (decl != null) {
	  context.getClassContext().addMethodDeclaration(decl);
	}
      }
    }
  }

  /**
   * Returns the defined type
   */
  public VKType getDef() {
    return type;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JMethodDeclaration genMethod(JExpression access, boolean isStatic) {
    if (hasMethod) {
      return null;
    }

    this.hasMethod = true;
    this.access = access;
    this.isStatic = isStatic;

    JExpression expr = new com.kopiright.xkopi.comp.xkjc.XSqlExpr(getTokenReference(), getDef().getList().getTable());
    JReturnStatement stmt = new JReturnStatement(getTokenReference(), expr, null);

    return new JMethodDeclaration(getTokenReference(),
				  ACC_PUBLIC | ACC_FINAL | (isStatic ? ACC_STATIC : 0), 
                                  CTypeVariable.EMPTY,
				  CStdType.String,
				  getIdent() + "_TYPE",
				  params,
				  CReferenceType.EMPTY,
				  new JBlock(getTokenReference(), new JStatement[]{stmt}, null),
				  null,
				  null);
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
    genComments(p);
    p.printVKTypeDefinition(getIdent(), params, type);
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genTypeDefinition(getIdent(), type);
  }
  
  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private VKType		type;
  private JFormalParameter[]	params;
  protected boolean		isStatic;
  protected boolean		hasMethod;
  protected JExpression		access;
}
