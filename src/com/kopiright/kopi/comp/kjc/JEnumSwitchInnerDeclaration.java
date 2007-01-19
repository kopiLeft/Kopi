/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.util.Enumeration;
import java.util.Hashtable;

import com.kopiright.compiler.base.JavaStyleComment;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a java inner class in the syntax tree.
 * The need of this class is to garanty a binary compatibility
 * when using enum switchs. 
 */
public class JEnumSwitchInnerDeclaration extends JClassDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  JEnumSwitchInnerDeclaration(TokenReference where, String enumId, CClassContext context) {
    super(where,
         ACC_STATIC,
         context.getCClass().getQualifiedName() 
                       + "$" + context.getClassContext().getNextSyntheticIndex(),
         CTypeVariable.EMPTY, 
         null, 
         CReferenceType.EMPTY, 
         null, 
         JMethodDeclaration.EMPTY,
         JTypeDeclaration.EMPTY,
         JPhylum.EMPTY,
         null,
         JavaStyleComment.EMPTY);

    this.enumId = enumId;
  }
  
  private static JFieldDeclaration createMapField(TokenReference where, 
						  String enumId, 
						  CClassContext context)
  {
    JExpression         length;
    JExpression         init;
    JVariableDefinition var;
        
    // Code to create: new int[<Enum Ident>.values().length];
    length = new JArrayLengthExpression(where,
                                        new JMethodCallExpression(where,
                                                                  new JNameExpression(where, enumId),
                                                                  "values",
                                                                  JExpression.EMPTY));
    init = new JNewArrayExpression(where,
                                   context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT),
                                   new JExpression[] { length },
                                   null);

    // Code to create: "static final int $SwitchMap$<Enum Ident>[]"
    var = new JVariableDefinition(where,
                                  ACC_STATIC | ACC_FINAL,
                                  new CArrayType(context.getTypeFactory().getPrimitiveType(TypeFactory.PRM_INT), 1),
                                  "$SwitchMap$" + enumId,
                                  init);

    return new JFieldDeclaration(where, var, null, JavaStyleComment.EMPTY);
  }

  protected JInitializerDeclaration constructInitializers(boolean isStatic, boolean always, TypeFactory factory) {
    return null;
  }  

  public void init(CClassContext context) throws PositionedError {
    JFieldDeclaration field = createMapField(getTokenReference(), enumId, context);
    this.fields = new JFieldDeclaration[] { field };
    this.body = new JPhylum[2];
    this.body[0] = field;
    this.body[1] = null; //tmp placeholder: see close()
    this.generateInterface(context.getClassReader(), context.getCClass(), "");
    this.getCClass().setLocal(false);
    this.join(context);
    this.checkInterface(context);
  }
  
  public void close(CClassContext context) throws PositionedError {
    
    this.body[this.body.length-1] = new JClassBlock(getTokenReference(),
                                   true,
                                   constructInitializersStatments(context.getTypeFactory()), 
                                   JavaStyleComment.EMPTY);
    
    this.statInit = super.constructInitializers(true, true, context.getTypeFactory());
    
    if (this.statInit != null) {
      this.sourceClass.addMethod(this.statInit.checkInterface(self));
    }
   
    this.prepareInitializers(context);
    this.checkInitializers(context);
    this.checkTypeBody(context);
  }
  
  private JStatement[] constructInitializersStatments(TypeFactory factory) {
    
    JStatement[] stmts = new JStatement[map.size()];
    int i = 0;
    Enumeration e = map.keys();
    while (e.hasMoreElements()) {
      String key = (String)e.nextElement();
      int value = ((Integer)map.get(key)).intValue();
      JFieldAccessExpression prefix = new JFieldAccessExpression(getTokenReference(), 
                                                                 "$SwitchMap$" + enumId);
      JNameExpression enumClass = new JNameExpression(getTokenReference(), enumId);
      JFieldAccessExpression enumConst = new JFieldAccessExpression(getTokenReference(), 
                                                                    enumClass, key);
      JMethodCallExpression accessor = new JMethodCallExpression(getTokenReference(), enumConst, 
                                                                 "ordinal", JExpression.EMPTY);
      JArrayAccessExpression left = new JArrayAccessExpression(getTokenReference(), prefix, 
                                                               accessor);
      JIntLiteral right = new JIntLiteral(getTokenReference(), value);
      JAssignmentExpression assign = new JAssignmentExpression(getTokenReference(), left, right);
      JExpressionStatement init = new JExpressionStatement(getTokenReference(), assign, 
                                                           JavaStyleComment.EMPTY);
      
      JBlock tryClause = new JBlock(getTokenReference(), new JStatement[] { init }, 
                                    JavaStyleComment.EMPTY);
      
      JFormalParameter exception = new JFormalParameter(getTokenReference(),
                                                        JLocalVariable.DES_PARAMETER,
                                                        factory.createType("NoSuchFieldError", true),
                                                        "nosuchfielderror" + ((value == 1)? 
                                                            "" : new Integer(value+1).toString()),
                                                        false);
      
      JCatchClause catchClause = new JCatchClause(getTokenReference(), 
                                                  exception, 
                                                  new JBlock(getTokenReference(), 
                                                             JStatement.EMPTY, 
                                                             JavaStyleComment.EMPTY));
      
      stmts[i++] = new JTryCatchStatement(getTokenReference(), 
                                          tryClause, 
                                          new JCatchClause[] { catchClause }, 
                                          JavaStyleComment.EMPTY);
    }
    return stmts;
  } 

  public JExpression translateEnumSwitchExpr(CBodyContext context, 
                                             JExpression expr) throws PositionedError {
    
    JExpression method, field, newExpr;
      
    // Code To Create: <Inner Class>.$SwitchMap$Hello[expr.ordinal()]    
    method = new JMethodCallExpression(getTokenReference(), expr, "ordinal", JExpression.EMPTY);
    field = new JFieldAccessExpression(getTokenReference(), 
                                       new JNameExpression(getTokenReference(), this.ident),
                                       "$SwitchMap$" + enumId);
    newExpr = new JArrayAccessExpression(getTokenReference(), field, method);

    return newExpr.analyse(new CExpressionContext(context, context.getEnvironment()));
  }

  public int addMapField(JNameExpression expr, CBodyContext context) throws PositionedError {
    
    Integer value;
    JFieldAccessExpression field;

    String key = expr.getName(); 

    field = new JFieldAccessExpression(getTokenReference(), 
                                       new JNameExpression(getTokenReference(), enumId),
                                       key);
    
    field.analyse(new CExpressionContext(context, context.getEnvironment()));
    
    if ( (value = (Integer)map.get(key)) != null ) {
      return value.intValue();
    } else {
      map.put(key , new Integer(++count));
      return count;
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private String    enumId;
  private Hashtable map = new Hashtable();
  private int       count = 0;
  
}
