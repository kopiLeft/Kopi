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
import at.dms.compiler.base.JavaStyleComment;
import at.dms.compiler.base.TokenReference;
import at.dms.util.base.InconsistencyException;

/**
 * This class represents a Java method declaration in the syntax tree.
 */
public class JAccessorMethod extends JMemberDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a access for a private method.
   *
   * @param	owner		the owner of the method
   * @param	method		the method accessed with this accessor
   */
  public JAccessorMethod(TypeFactory factory, CSourceClass target, CMember member, boolean leftSide, boolean isSuper, int oper) {
    super(TokenReference.NO_REF, null, new JavaStyleComment[0]);

    JBlock              body;
    JFormalParameter[]  parameters;
    String              ident = createIdent(target.getNextSyntheticIndex());
    CType[]             methodParameters;
    int                 useInstance = 0;
    CType               returnType;
    CReferenceType[]        throwables;

    if (member instanceof CField) {
      methodParameters = leftSide ? new CType[]{((CField)member).getType()} : new CType[0];
      //      returnType =  leftSide ? typeFactory.getVoidType() : ((CField)member).getType();
      returnType = ((CField)member).getType();
      useInstance = member.isStatic() ? 0 : 1;
      throwables = CReferenceType.EMPTY;
    } else if (member instanceof CMethod) {
      CMethod           method = (CMethod) member;
      
      methodParameters = method.getParameters();
      throwables = method.getThrowables();

      if (method.isConstructor()) {
        returnType = null;
        // !!! to-do
      } else {
        useInstance = member.isStatic() ? 0 : 1;
        returnType = method.getReturnType();
      }
    } else {
      throw new InconsistencyException("no accessor for this type");
    }

    CType[]             parameterTypes = new CType[methodParameters.length+useInstance];

    parameters = new JFormalParameter[methodParameters.length+useInstance];

    if (useInstance != 0) {
      parameters[0] = new JFormalParameter(TokenReference.NO_REF,
                                           ACC_FINAL,
                                           new CClassOrInterfaceType(TokenReference.NO_REF, target, new CReferenceType[][]{target.getTypeVariables()}), 
                                           "instance",
                                           true);
      parameters[0].setPosition(0);
      parameterTypes[0] = parameters[0].getType();
    }

    for (int i = 0; i < methodParameters.length; i++) {
      int j = i+useInstance;
      parameters[j] = new JFormalParameter(TokenReference.NO_REF,
                                                       ACC_FINAL,
                                                       methodParameters[i], 
                                                       "par" + i,
                                                       true);
      parameters[j].setPosition(j);
      parameterTypes[j] = methodParameters[i];
    }    


    // Body
    JExpression[]       args = new JExpression[methodParameters.length];
    JStatement          statement; 

    for (int i = 0; i < args.length; i++) {
      args[i] = new JLocalVariableExpression(TokenReference.NO_REF,
                                             parameters[i+useInstance]);
    }

    JExpression        instanceref = useInstance == 0 ?
      null:
      (isSuper ? (JExpression)new JSuperExpression(TokenReference.NO_REF) :
       new JLocalVariableExpression(TokenReference.NO_REF,
                                   parameters[0]));
    if (member instanceof CMethod) {
      // methods and constructor
      JExpression         invokeMethod =
        new JMethodCallExpression(TokenReference.NO_REF,
                                  instanceref,
                                  (CMethod) member,
                                  args);
      if (returnType.getTypeID() == TID_VOID) {
        statement = new JExpressionStatement(TokenReference.NO_REF,
                                             invokeMethod,
                                             null);
      } else {
        statement = new JReturnStatement(TokenReference.NO_REF,
                                         invokeMethod, 
                                         null);
      }
      // !!! FIXME implement constructor
    } else {
      // fields
      if (leftSide) {
        JExpression      left = new JFieldAccessExpression(TokenReference.NO_REF,
                                                           instanceref,
                                                           (CField)member);
        JExpression      right = new JLocalVariableExpression(TokenReference.NO_REF,
                                                              parameters[useInstance]);
        statement = 
          new JReturnStatement(TokenReference.NO_REF,
                                   oper == OPE_SIMPLE 
                                   ?(JExpression) new JAssignmentExpression(TokenReference.NO_REF,
                                                             left,
                                                             right,
                                                             left.getType(factory))
                                   : new JCompoundAssignmentExpression(TokenReference.NO_REF,
                                                                       oper,
                                                                       left,
                                                                       right,
                                                                       left.getType(factory)),
                                   null);
      } else {
        statement = new JReturnStatement(TokenReference.NO_REF,
                                         new JFieldAccessExpression(TokenReference.NO_REF,
                                                                    instanceref,
                                                                    (CField)member),
                                         null);
      } 
    }
    body = new JBlock(TokenReference.NO_REF,
                      new JStatement[] {statement},
                      null);
    method = new CSourceMethod(target,
                               ACC_STATIC,
                               ident,
                               returnType,
                               parameterTypes,
                               throwables,
                               CTypeVariable.EMPTY,
                               false,
                               true, // synthetic
                               body);
    setInterface(method);
    target.addMethod(method);





   }

  private String createIdent(int index) {
    String idx = Integer.toString(index);
    return (JAV_ACCESSOR+"000".substring(0,3-idx.length())+idx).intern();
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
    return method;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param context the actual context of analyse
   * @return  a pure java expression including promote node
   * @exception PositionedError Error catched as soon as possible
   */
  public void checkBody1(CClassContext context) throws PositionedError {
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    // nothing to do
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    throw new InconsistencyException(); // nothing to do here
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  CSourceMethod         method;
}
