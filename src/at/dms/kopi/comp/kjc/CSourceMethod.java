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
 * $Id: CSourceMethod.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import java.util.ArrayList;

import at.dms.bytecode.classfile.ClassFileFormatException;
import at.dms.bytecode.classfile.MethodDescription;
import at.dms.bytecode.classfile.MethodInfo;
import at.dms.bytecode.classfile.CodeInfo;
import at.dms.bytecode.classfile.CodeEnv;
import at.dms.compiler.base.PositionedError;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.SimpleStringBuffer;
import at.dms.util.base.Utils;

/**
 * This class represents an exported member of a class (fields)
 */
public class CSourceMethod extends CMethod {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a method export.
   *
   * @param	owner		the owner of this method
   * @param	modifiers	the modifiers on this method
   * @param	ident		the ident of this method
   * @param	returnType	the return type of this method
   * @param	paramTypes	the parameter types of this method
   * @param	exceptions	a list of all exceptions in the throws list
   * @param	deprecated	is this method deprecated
   * @param	body		the source code
   */
  public CSourceMethod(CClass owner,
		       int modifiers,
		       String ident,
		       CType returnType,
		       CType[] paramTypes,
		       CReferenceType[] exceptions,
                       CTypeVariable[] typeVariables,
		       boolean deprecated,
                       boolean synthetic,
		       JBlock body)
  {
    super(owner, modifiers, ident, returnType, paramTypes, exceptions, typeVariables, deprecated, synthetic);
    this.body = body;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public boolean isUsed() {
    return used || !isPrivate() || isSynthetic(); //getIdent().indexOf("$") >= 0; // $$$
  }

  public void setUsed() {
    used = true;
  }

  public JBlock getBody() {
    return body;
  }

  void setBody(JBlock body) {
    this.body = body;
  }

  public void setSynthetic(boolean syn) {
    synthetic = syn;
  }
  // ----------------------------------------------------------------------
  // ACCESSORS (Extended)
  // ----------------------------------------------------------------------
  
  public boolean isInvariant() {
    return invariant;
  }

  public void setInvariant(boolean invariant) {
    this.invariant = invariant;
  }

  public boolean isPrecondition() {
    return precondition;
  }

  public void setPrecondition(boolean precondition) {
    this.precondition = precondition;
  }

  public boolean isPostcondition() {
    return postcondition;
  }

  public void setPostcondition(boolean postcondition) {
    this.postcondition = postcondition;
  }

  public CReferenceType getOldValueStore() {
    return oldValueStore;
  }

  public void setOldValueStore(CReferenceType oldValueStore) {
    this.oldValueStore = oldValueStore;
  }

  public CMethod getPreconditionMethod() {
    return preconditionMethod;
  }

  public void setPreconditionMethod(CMethod pre) {
    preconditionMethod = pre;
  }

  public CMethod getPostconditionMethod() {
    return postconditionMethod;
  }

  public void setPostconditionMethod(CMethod post) {
    this.postconditionMethod = post;
  }
  public void addSuperMethod(CMethod superMethod) {
    verify(superMethod != null);
    if(superMethods == null) {
      superMethods = new ArrayList();
    }
    superMethods.add(superMethod);
  }
  public CMethod[] getSuperMethods() {
    if (superMethods == null) {
      return CMethod.EMPTY;
    } else{
      return (CMethod[])superMethods.toArray(new CMethod[superMethods.size()]); 
    }
  }
 
  /**
   * Find out that pre/postcondition of supermethod is already checked by 
   * pre/postcondition of another method.
   */
  public boolean includesSuperMethod(CMethod method) {
    if (superMethods == null) {
      return false;
    } else{
      int         size = superMethods.size();
      
      for (int i=0; i < size; i++) {
        if ((superMethods.get(i) == method) || (((CMethod)superMethods.get(i)).includesSuperMethod(method))){
          return true;
        }
      }
      return false;
    }
  }
  // ----------------------------------------------------------------------
  // GENERATE CLASSFILE INFO
  // ----------------------------------------------------------------------

  /**
   * Generate the code in a class file
   *
   * @param	optimizer	the bytecode optimizer to use
   */
  public MethodInfo genMethodInfo(BytecodeOptimizer optimizer, TypeFactory factory) throws ClassFileFormatException {
    CReferenceType[]	excs = getThrowables();
    String[]		exceptions = new String[excs.length];

    for (int i = 0; i < excs.length; i++) {
      exceptions[i] = excs[i].getQualifiedName();
    }

    MethodInfo methodInfo =  new MethodInfo((short)getModifiers(),
                                            getIdent(),
                                            getSignature(),
                                            getGenericSignature(),
                                            exceptions,
                                            body != null ? genCode(factory): null, 
                                            isDeprecated(),
                                            isSynthetic());
    methodInfo = optimizer.run(methodInfo);

    if (isInvariant()) {
      methodInfo.setInvariant(true);
    }
    if (isPrecondition()) {
      methodInfo.setPrecondition(true);
    }
    if (isPostcondition()) {
      methodInfo.setPostcondition(oldValueStore == null ? null :  oldValueStore.getIdent());
    }
    if (preconditionMethod != null || preconditionMethod != null) {
      MethodDescription pre, post;

      if (preconditionMethod == null) {
        pre = null;
      } else {
        pre = new MethodDescription(preconditionMethod.getQualifiedName(),
                                    preconditionMethod.getSignature());
      }
      if (postconditionMethod == null) {
        post = null;
      } else {
        post = new MethodDescription(postconditionMethod.getQualifiedName(),
                                     postconditionMethod.getSignature());
      }
      methodInfo.setConditionMethods(pre, post);
    }
    return methodInfo;
  }

  /**
   * @return the type signature (JVM) of this method
   */
  public String getSignature() {
    CType[]     parameters = getParameters();

    if (getOwner().isNested() && isConstructor()) {
      parameters = ((CSourceClass)getOwner()).genConstructorArray(parameters);
    }

    SimpleStringBuffer	buffer;
    String		result;

    buffer = SimpleStringBuffer.request();
    buffer.append('(');
    for (int i = 0; i < parameters.length; i++) {
      parameters[i].appendSignature(buffer);
    }
    buffer.append(')');
    returnType.appendSignature(buffer);

    result = buffer.toString();
    SimpleStringBuffer.release(buffer);
    return result;
  }

  /**
   * @return the generic type siganture (attribute) of this method
   * @see #getSignature()
   */
  public String getGenericSignature() {
    CType[]             parameters = getParameters();
    CType               returntype = getReturnType();
    CReferenceType[]        exceptions = getThrowables();
    CTypeVariable[]     typeVariables = getTypeVariables();
    SimpleStringBuffer	buffer;
    String		result;

    buffer = SimpleStringBuffer.request();
    // type variable definitions
    if (typeVariables.length > 0) {
      buffer.append('<');
      for (int i = 0; i < typeVariables.length; i++) {
        typeVariables[i].appendDefinitionSignature(buffer);
      }
      buffer.append('>');
    }
    // parameter declaration
    buffer.append('(');
    for (int i = 0; i < parameters.length; i++) {
      parameters[i].appendGenericSignature(buffer);
    }
    buffer.append(')');
    // return type
    returntype.appendGenericSignature(buffer);
    // throws clause
    if ((exceptions != null) && (exceptions.length > 0)) {
      buffer.append('^');
      for (int i = 0; i < exceptions.length; i++) {
        exceptions[i].appendGenericSignature(buffer);
      }
    }
    result = buffer.toString();
    SimpleStringBuffer.release(buffer);
    return result;
  }


  private CodeInfo genByteCode(TypeFactory factory) {
    CodeSequence	code = CodeSequence.getCodeSequence();

    GenerationContext   context = new GenerationContext(factory, code);

    body.genCode(context);
    if (getReturnType().getTypeID() == TID_VOID) {
      code.plantNoArgInstruction(opc_return);
    }

    CodeInfo            info = new CodeInfo(code.getInstructionArray(),
                                            code.getHandlers(),
                                            code.getLineNumbers(),
                                            null);
    code.release();
    body = null;
    return info;
  }

  /**
   * Generates JVM bytecode for this method.
   */
  public CodeInfo genCode(TypeFactory factory) throws ClassFileFormatException {
    CodeInfo    info;
    CType[]	parameters = getParameters();
    int		paramCount = 0;

    // generate byte code
    info = genByteCode(factory);

    // set necessary additional information
    for (int i = 0; i < parameters.length; i++) {
      paramCount += parameters[i].getSize();
    }
    paramCount += getReturnType().getSize();
    paramCount += isStatic() ? 0 : 1;

    info.setParameterCount(paramCount);
    // set maxStack, maxLocals, ...
    CodeEnv.check(info);
    return info;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JBlock                body;
  private boolean               used;
  private CMethod               preconditionMethod;
  private CMethod               postconditionMethod;
  private CReferenceType        oldValueStore;
  private boolean               invariant;
  private boolean               precondition;
  private boolean               postcondition;
  private ArrayList             superMethods;
}
