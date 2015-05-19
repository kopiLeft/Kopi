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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.UnpositionedError;

/**
 * JLS 14.11: While Statement
 *
 * The while statement executes an expression and a statement repeatedly
 * until the value of the expression is false.
 */
public class JEnhancedForStatement extends JForStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	param 		the formal parameter that will parse the collection
   * @param	expr		the expression that should be iterator or array
   * @param	body		the loop body.
   */
  public JEnhancedForStatement(TokenReference where, 
  		       JFormalParameter param,
		       JExpression expr,
		       JStatement body,
		       JavaStyleComment[] comments)
  {
    super(where, null, null, null, null, comments);
    this.param = param;
    this.expr = expr;
    this.body = body;
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
    CBlockContext       block = new CBlockContext (context, context.getEnvironment(), 2); 
    TypeFactory         factory = context.getTypeFactory();
    
    //param.analyse(block); // else you have to declare it before using it in the for loop
    expr = expr.analyse(new CExpressionContext(block, context.getEnvironment()));
  	
    CType                         exprType = expr.getType(factory);
    //    CType                         paramType = param.getType();
    JExpressionStatement          incr;
    JStatement                    init; // init of for
    JExpression                   cond;
    JStatement                    formalAssignStatement;
    JVariableDeclarationStatement exprVarDefinition;
		
    if (exprType.isArrayType()) {
      JVariableDefinition   	  iterator;
      JVariableDefinition   	  exprVariable;
  		   		
      iterator = new JVariableDefinition(getTokenReference(), 
                                         0, 
                                         factory.getPrimitiveType(Constants.TID_INT), 
                                         "#foreach#i" + toString() /* unique ID */, 
                                         new JIntLiteral(expr.getTokenReference(),0));
      exprVariable = new JVariableDefinition(getTokenReference(), 
                                             0, 
                                             new CArrayType(((CArrayType)exprType).getBaseType(),
                                                            ((CArrayType)exprType).getArrayBound()), 
                                             "#foreach#expr" + toString() /* unique ID */, 
                                             new JCheckedExpression(expr.getTokenReference(),
                                                                    expr));

      //exprVariable = expr;
      exprVarDefinition = new JVariableDeclarationStatement(expr.getTokenReference(), 
                                                            exprVariable, 
                                                            JavaStyleComment.EMPTY);
      
      // the init part for old For Statement 
      init = new JVariableDeclarationStatement(expr.getTokenReference(), 
                                               iterator, 
                                               JavaStyleComment.EMPTY);
      
      //the cond part for old For Statement   		
      cond = new JRelationalExpression(expr.getTokenReference(),
                                       Constants.OPE_LT,
                                       new JLocalVariableExpression(expr.getTokenReference(),
                                                                    iterator),
                                       new JArrayLengthExpression(expr.getTokenReference(),
                                                                  new JLocalVariableExpression(expr.getTokenReference(),
                                                                                               exprVariable)));
  		
      // constructing the incrementation expression to update the FOr loop 
  		
      JExpression               incrExpr;

      incrExpr = new JCompoundAssignmentExpression(expr.getTokenReference(), 
                                                   Constants.OPE_PLUS, 
                                                   new JLocalVariableExpression(expr.getTokenReference(),
                                                                                iterator), 
                                                   new JIntLiteral(expr.getTokenReference(), 1));
  		
      incr = new JExpressionStatement(expr.getTokenReference(),
                                      incrExpr,
                                      null);

      // constructing new body including the formal parameter assignement and the old body
      JExpression               exprArrayAccess;
  		
      exprArrayAccess = new JArrayAccessExpression(expr.getTokenReference(),
                                                   new JLocalVariableExpression(expr.getTokenReference(),
                                                                                exprVariable),
                                                   new JLocalVariableExpression(expr.getTokenReference(),
                                                                                iterator));
  		
      JVariableDefinition       formalAssign;

      formalAssign = new JVariableDefinition(getTokenReference(), 
                                             0,
                                             param.getType(),
                                             param.getIdent(), 
                                             exprArrayAccess);
      formalAssignStatement = new JVariableDeclarationStatement(param.getTokenReference(), 
                                                                formalAssign, 
                                                                JavaStyleComment.EMPTY);
    } else {
      try{
        check(block,	
              param.getType().isReference() 
              && exprType.isAssignableTo(context, 
                                         factory.createType("java/lang/Iterable", 
                                                            new CReferenceType[][]{new CReferenceType[] {(CReferenceType) param.getType()}},
                                                            false).checkType(block)), 
              KjcMessages.INCOMPATIBLE_TYPES, 
              exprType);
      } catch (UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }
  	    
      JVariableDefinition   	            iterator;
      
      JMethodCallExpression               callIterator;
      
      exprVarDefinition = null;
      callIterator = new JMethodCallExpression(expr.getTokenReference(),
                                               new JCheckedExpression(expr.getTokenReference(),
                                                                      expr),
                                               "iterator",
                                               JExpression.EMPTY);
      iterator = new JVariableDefinition(expr.getTokenReference(),  
                                         com.kopiright.bytecode.classfile.Constants.ACC_FINAL, // never changed  
                                         factory.createType("java/util/Iterator", 
                                                            new CReferenceType[][]{new CReferenceType[]{(CReferenceType) param.getType()}},
                                                            //CReferenceType.EMPTY_ARG,
                                                            false), 
                                         "#foreach#i" + toString() /* unique ID */, 
                                         callIterator);
      
      init = new JVariableDeclarationStatement(expr.getTokenReference(), 
                                               iterator, 
                                               JavaStyleComment.EMPTY);
      cond = new JMethodCallExpression(expr.getTokenReference(),
                                       new JLocalVariableExpression(expr.getTokenReference(),
                                                                    iterator),
                                       "hasNext",
                                       JExpression.EMPTY);
      
      incr = null;
      
      JExpression             callNext;
      JExpression             castExpr;
      JVariableDefinition     formalAssign;

      
      callNext = new JMethodCallExpression(expr.getTokenReference(),
                                           new JLocalVariableExpression(expr.getTokenReference(),
                                                                        iterator),
                                            "next",
                                           JExpression.EMPTY);
      castExpr = new JCastExpression(expr.getTokenReference(), callNext, param.getType());
      formalAssign = new JVariableDefinition(getTokenReference(), 
                                             0,
                                             param.getType(),
                                             param.getIdent(), 
                                             castExpr);
      formalAssignStatement = new JVariableDeclarationStatement(param.getTokenReference(), 
                                                                formalAssign, 
                                                                JavaStyleComment.EMPTY);
    }
    JStatement[]            newBody; 
    JBlock                  newbodyblock;
    
    newBody = new JStatement[] {formalAssignStatement, body};
    newbodyblock = new JBlock(expr.getTokenReference(),
                              newBody,
                              JavaStyleComment.EMPTY);
    forStatement = new JForStatement(getTokenReference(),
                                     init,
                                     cond,
                                     incr,
                                     newbodyblock,
                                     JavaStyleComment.EMPTY);
	
    if (exprVarDefinition != null) {
      forStatement = new JBlock(expr.getTokenReference(),
                                new JStatement[] {exprVarDefinition, forStatement},
                                JavaStyleComment.EMPTY);
    }
    
    forStatement.analyse(block);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    //    p.visitForStatement(expr, init, cond, incr, body);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    forStatement.genCode(context);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private JFormalParameter              param;		            
  private JExpression                   expr;
  private JStatement                    body;
  private JStatement                    forStatement;
}
