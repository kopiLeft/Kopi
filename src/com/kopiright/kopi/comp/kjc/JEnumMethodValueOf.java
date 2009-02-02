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

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.CTypeVariable;
import com.kopiright.kopi.comp.kjc.JBlock;
import com.kopiright.kopi.comp.kjc.JFormalParameter;

/**
 * This class represents a Java method declaration in the syntax tree.
 */
public class JEnumMethodValueOf extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a method declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param enumType    the enum class where this method is declared
   * @param valuesField the name of the values field in this class
   * */
  public JEnumMethodValueOf(TokenReference where,
 			               CType enumType,
 			               TypeFactory factory,
 			               String valuesField) {
    super(where,
          ACC_PUBLIC | ACC_STATIC,
          CTypeVariable.EMPTY,
          enumType,
          "valueOf",
          new JFormalParameter[] { 
           new JFormalParameter(where, 
                                JLocalVariable.DES_PARAMETER,
                                factory.createReferenceType(TypeFactory.RFT_STRING),
                                "s",
                                false) 
          },
          CReferenceType.EMPTY,
          null,
          null, 
          JavaStyleComment.EMPTY);

    this.factory = factory;
    super.body = createBody(where, valuesField, enumType);
  }

  
  private JBlock createBody(TokenReference where, String valuesField, CType enumType) {
      
      JExpression              accessField, length, forCond, ifCond, name;
      JVariableDefinition      array, arrayLength, iter, valueVar;
      JForStatement            forStatement;
      JStatement               forInit, forIncr, forBody;
      JIfStatement             ifStatement;
      JReturnStatement         returnStatment;
      JThrowStatement          throwStatement; 
      
      JExpression arrayExpr, arrayLengthExpr, iterExpr, valueVarExpr, throwExpr;
    
      // <EnumType> array[] = <valuesField>;
      accessField = new JFieldAccessExpression(where, valuesField);
      array = new JVariableDefinition(where, 
                                      0,
                                      new CArrayType(enumType, 1),
                                      "array",
                                      accessField);
      
      arrayExpr = new JNameExpression(where, "array");
      // int arrayLength = array.length;
      length = new JArrayLengthExpression(where, arrayExpr);
      arrayLength = new JVariableDefinition(where,
                                            0,
                                            factory.getPrimitiveType(TypeFactory.PRM_INT),
                                            "arrayLength",
                                            length);
      arrayLengthExpr = new JNameExpression(where, "arrayLength");
      
      // for (int var = 0; var < arrayLength; var++)
      // int var=0
      iter = new JVariableDefinition(where,
                                    0,
                                    factory.getPrimitiveType(TypeFactory.PRM_INT),
                                    "iter",
                                    new JIntLiteral(where, 0));
      iterExpr = new JNameExpression(where, "iter");
            
      forInit = new JVariableDeclarationStatement(where, iter, JavaStyleComment.EMPTY);
      // var < arrayLength
      forCond = new JRelationalExpression(where, 
                                          OPE_LT, 
                                          iterExpr, 
                                          arrayLengthExpr);
      // iter++
      forIncr = new JExpressionStatement(where, 
                                         new JPrefixExpression(where, 
                                                               Constants.OPE_PREINC, 
                                                               iterExpr),
                                         JavaStyleComment.EMPTY);
      
      // <EnumType> valueVar = array[iter];
      valueVar = new JVariableDefinition(where,
                                         0,
                                         enumType,
                                         "valueVar",
                                         new JArrayAccessExpression(where,
                                                                    arrayExpr,
                                                                    iterExpr));
      valueVarExpr = new JNameExpression(where, "valueVar");
      
      // if (valueVar.name().equals(s)) return valueVar;
      name = new JMethodCallExpression(where, 
                                     valueVarExpr,
                                     "name",
                                     JExpression.EMPTY);
      ifCond = new JMethodCallExpression(where, 
                                             name, 
                                             "equals", 
                                             new JExpression[] { new JNameExpression(where, "s")});
      returnStatment = new JReturnStatement(where, valueVarExpr, JavaStyleComment.EMPTY); 
      
      ifStatement = new JIfStatement(where, ifCond, returnStatment, null, JavaStyleComment.EMPTY);
      
      // forBody
      forBody = new JBlock(where, 
                           new JStatement[] { 
                             new JVariableDeclarationStatement(where, valueVar, null),
                             ifStatement 
                           }, 
                           JavaStyleComment.EMPTY);
      
      // for (int var = 0; var < arrayLength; var++) {forBody}
      forStatement = new JForStatement(where, forInit, forCond, forIncr, forBody, JavaStyleComment.EMPTY);
      
      
      // throw new IllegalArgumentException(s);
      throwExpr = new JUnqualifiedInstanceCreation(where,
                                                   factory.createType(where, 
                                                                      "IllegalArgumentException", 
                                                                      true),
                                                   new JExpression[] { 
                                                     new JNameExpression(where, "s")
                                                   });
      throwStatement = new JThrowStatement(where, throwExpr, JavaStyleComment.EMPTY);
      
      return new JBlock(where, 
                        new JStatement[] {
                          new JVariableDeclarationStatement(where, array, JavaStyleComment.EMPTY),
                          new JVariableDeclarationStatement(where, arrayLength, JavaStyleComment.EMPTY),
                          forStatement,
                          throwStatement
                        },
                        JavaStyleComment.EMPTY);
      
  }
 

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private TypeFactory factory;

}
