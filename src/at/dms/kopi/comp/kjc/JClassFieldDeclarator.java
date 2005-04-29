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

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * JLS 8.3 : Class Field Declaration.
 * JLS 9.3 ; Field (Constant) Declaration.
 *
 */
public class JClassFieldDeclarator extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	decl		the declarator of this field
   */
  public JClassFieldDeclarator(TokenReference where, JFieldDeclaration decl) {
    super(where, null);
    this.decl = decl;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * check if the context is set
   * 
   * @return	true if the context is set
   */
  public boolean hasBodyContext() {
    return bodyContext != null;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * In this step we set only the context.
   * 
   * @param	context		the analysis context
   */
  public void analyse(CBodyContext context) { 
    bodyContext = context;
    ((CSourceField)decl.getField()).setDeclarationOwner(this);
  }

  /**
   * Analyses the statement (semantically).
   * 
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse() throws PositionedError {
    if (! decl.getField().isAnalysed()) {
      decl.analyse(bodyContext);
      
      decl.getField().setAnalysed(true); // mark as analysed
      if (decl.hasInitializer()) {
        ((CSourceField)decl.getField()).setValue(decl.getVariable().getValue());

        if  (((CSourceField)decl.getField()).isFinal() && !decl.getVariable().getValue().isConstant()) {
          simpleContext = new CSimpleBodyContext(bodyContext, bodyContext.getEnvironment(), bodyContext);
          ((CSourceField)decl.getField()).setDeclarationOwner(this);
        }
      }
    }
  }

  /**
   * 2nd part of analysation.
   */
  public void  analyseDeclaration(){
    if (simpleContext != null) {
      CExpressionContext	expressionContext = new CExpressionContext(simpleContext, simpleContext.getEnvironment());
      
      simpleContext = null;
      ((CSourceField)decl.getField()).setDeclarationOwner(null);
      try {
        ((CSourceField)decl.getField()).setValue(decl.getVariable().getValue().analyse(expressionContext));
      } catch (PositionedError e){
        // thrown in the first evaluation
      }
    }
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    // utility class for classfile
  }

  /**
   * Generates a sequence of bytescodes
   *
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence        code = context.getCodeSequence();
    TypeFactory         factory = context.getTypeFactory();

    if (decl.getField().getConstantValue(factory) == null) {
      decl.genCode(context);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JFieldDeclaration  decl;
  private CBodyContext       simpleContext;
  private CBodyContext       bodyContext;
}
