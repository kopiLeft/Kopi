/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.kopi.comp.kjc;

import java.util.ArrayList;
import java.util.Iterator;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.JavadocComment;
import org.kopi.compiler.base.TokenReference;


public class CParseEnumContext extends CParseClassContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  public CParseEnumContext(TokenReference classReference,
                           CReferenceType classType, 
                           TypeFactory factory) {
        
    this.classReference = classReference;
    this.classType = classType;
    this.factory = factory;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------
  public void addEnumFieldDeclaration(TokenReference where,
                                      String ident,
                                      int count,
                                      JExpression[] args,
                                      JClassDeclaration inner,
                                      JavadocComment javadoc,
                                      JavaStyleComment[] comment) {

    JExpression[] fieldArgs = new JExpression[args.length+2];
    JExpression instance;
    JVariableDefinition var;
    JFieldDeclaration decl;
    
    fieldArgs[0] = new JStringLiteral(where, ident);
    fieldArgs[1] = new JIntLiteral(where, count);
    for (int i=0; i<args.length; i++) {
      fieldArgs[i+2] = args[i];
    }
    
    if (inner == null ) {
      instance = new JUnqualifiedInstanceCreation(where, classType, fieldArgs);
    } else {
      instance = new JUnqualifiedAnonymousCreation(where, classType, fieldArgs, inner);
    }
        
    var = new JVariableDefinition(where,
                                  org.kopi.bytecode.classfile.Constants.ACC_PUBLIC | org.kopi.bytecode.classfile.Constants.ACC_STATIC | org.kopi.bytecode.classfile.Constants.ACC_FINAL,
                                  classType,
                                  ident,
                                  instance);
    
    decl = new JFieldDeclaration(where, var, javadoc, comment);
        
    fields.add(decl);
    body.add(decl);
    enumValues.add(ident);
  }

  private void createValuesField() {

    StringBuffer valuesIdent = new StringBuffer("$VALUES");
      
    ArrayList    values = new ArrayList();
    Iterator     iterator = enumValues.iterator();
    JNewArrayExpression init;
    
    while (iterator.hasNext()) {
      values.add(new JFieldAccessExpression(classReference, 
                                            (String) iterator.next()));
    }      

    init = new JNewArrayExpression(classReference, 
                                   classType, 
                                   new JExpression[1], 
                                   new JArrayInitializer(classReference,
                                                         (JExpression[]) values.toArray(new JExpression[values.size()])));
    
    valuesField =  new JFieldDeclaration(classReference,
                                         new JVariableDefinition(classReference,
                                                                 org.kopi.bytecode.classfile.Constants.ACC_PRIVATE | org.kopi.bytecode.classfile.Constants.ACC_STATIC | org.kopi.bytecode.classfile.Constants.ACC_FINAL,
                                                                 new CArrayType(classType, 1),
                                                                 valuesIdent.toString(),
                                                                 init),
                                         null,
                                         null);  
     
    fields.add(valuesField);
    body.add(valuesField);
  }
  

  public void createDefaultMethods() {
    JEnumMethodValues valuesMethod;
    JEnumMethodValueOf valueOfMethod;
    
    if (valuesField == null) {
      createValuesField();
    } else {
      return;
    }
    valuesMethod = new JEnumMethodValues(classReference, 
                                         classType, 
                                         valuesField.getVariable().getIdent());
    valueOfMethod = new JEnumMethodValueOf(classReference, 
                                           classType, 
                                           factory,
                                           valuesField.getVariable().getIdent());
    
    methods.add(valuesMethod);
    methods.add(valueOfMethod);
  }
  
  public JConstructorCall createConstructorCall() {
    JExpression[] arguments = { 
        new JNameExpression(TokenReference.NO_REF, "#s"), 
        new JNameExpression(TokenReference.NO_REF, "#i") 
    };
    
    return new JConstructorCall(TokenReference.NO_REF, false, arguments);
  }

  public JFormalParameter[] newConstructorParameters(JFormalParameter[] parameters) {
  
    JFormalParameter[] newParameters = new JFormalParameter[parameters.length + 2];
    
    newParameters[0] = new JFormalParameter(TokenReference.NO_REF, 
                                         JLocalVariable.DES_PARAMETER,
                                         factory.createReferenceType(TypeFactory.RFT_STRING),
                                         "#s",
                                         false);
    newParameters[1] = new JFormalParameter(TokenReference.NO_REF, 
                                         JLocalVariable.DES_PARAMETER,
                                         factory.getPrimitiveType(TypeFactory.PRM_INT),
                                         "#i",
                                         false);
    
    for (int i=0; i<parameters.length; i++) {
      newParameters[i+2] = parameters[i];
    }
    
    return newParameters;
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------
  public JFieldDeclaration[] getFields() {
    if (valuesField == null) {
        createValuesField(); 
    }
    return (JFieldDeclaration[])fields.toArray(new JFieldDeclaration[fields.size()]);
  }

  public JFieldDeclaration getValuesField(){
    return valuesField;
  }
  
  public JMethodDeclaration[] getMethods() {
    createDefaultMethods();
    return (JMethodDeclaration[])methods.toArray(new JMethodDeclaration[methods.size()]);
  }

  public JPhylum[] getBody() {
    if (valuesField == null) {
      createValuesField(); 
    }
    return (JPhylum[])body.toArray(new JPhylum[body.size()]);
  }
  
  public CReferenceType getReferenceType() {
    return classType;    
  }

  public boolean hasAnonymousInners() {
    return hasAnonymousInners;    
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (SET)
  // ----------------------------------------------------------------------
  public void setHasAnonymousInners(boolean hasAnonymousInners) {
    this.hasAnonymousInners = hasAnonymousInners; 
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private TokenReference    classReference;
  private CReferenceType    classType;
  private TypeFactory       factory;
  private boolean           hasAnonymousInners = false;
  
  private ArrayList         enumValues = new ArrayList();
  private JFieldDeclaration valuesField;
}
