/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import java.util.Hashtable;
import java.util.Enumeration;

import com.kopiright.bytecode.classfile.InvokeinterfaceInstruction;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents a class method.
 */
public abstract class CMethod extends CMember {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a method member.
   * @param	owner		the owner of this method
   * @param	modifiers	the modifiers on this method
   * @param	ident		the ident of this method
   * @param	returnType	the return type of this method
   * @param	parameters	the parameters type of this method
   * @param	exceptions	a list of all exceptions of the throws list
   * @param	deprecated	is this method deprecated
   */
  public CMethod(CClass owner,
                 int modifiers,
                 String ident,
                 CType returnType,
                 CType[] parameters,
                 CReferenceType[] exceptions,
                 CTypeVariable[] typeVariables,
                 boolean deprecated,
                 boolean synthetic)
  {
    super(owner, modifiers, ident, deprecated, synthetic);

    this.returnType = returnType;
    this.parameters = parameters;
    this.exceptions = exceptions;
    this.typeVariables = typeVariables;
    this.accessors = null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Checks whether this type is accessible from the specified class (JLS 6.6).
   *
   * @return	true iff this member is accessible
   */
  public boolean isAccessible(CType primary, CClass from) {
    if (!super.isAccessible(from)) {
      return false;
    } else {
    // JLS 6.6.2.1 
    // Access to a protected Member
      if (isProtected() 
          && !isStatic() 
          && !(getOwner().getPackage() == from.getPackage())
          && !(primary == null)) {
          // special case with clone of arrays JLS 10.7
        if (primary.isArrayType() && getIdent() == Constants.JAV_CLONE && getParameters().length == 0) {
          return true;
        } else {
          if (from.isNested() && primary.getCClass().descendsFrom(from.getOwner())) {
            return true;
          }
          return primary.getCClass().descendsFrom(from);
        }
      } else {
        return true;
      }
    }
  }
  /**
   * @return	the interface
   */
  public CMethod getMethod() {
    return this;
  }

  /**
   * @return the type of this field
   */
  public final CType getReturnType() {
    return returnType;
  }

  /**
   * @return the type of this field
   */
  public final CType[] getParameters() {
    return parameters;
  }
  /**
   * @return the type of this field
   */
  public final CTypeVariable[] getTypeVariables() {
    return typeVariables;
  }

  /**
   * @return the type of this field
   */
  public boolean isGenericMethod() {
    return typeVariables.length > 0;
  }
  /**
   * change the parameters of the method. Only allowed 
   * for postconditions.
   * @see #isPostconditon()
   */
  public void setParameters(CType[] parameters) {
    if (!isPostcondition()) {
      throw new InconsistencyException();
    }
    this.parameters = parameters;
  }

  /**
   * @return the type signature fo this method used in the JVM
   */
  public abstract String getSignature();

  /**
   * @return	the exceptions that can be thrown by this method
   */
  public CReferenceType[] getThrowables() {
    return exceptions;
  }

  /**
   * This method is used by initializers that knows throwables exceptions only
   * after body was checked.
   * @param	throwables	the exceptions that can be thrown by this method
   */
  public void setThrowables(Hashtable throwables) {
    if (throwables != null) {
      int		count = 0;

      exceptions = new CReferenceType[throwables.size()];
      for (Enumeration elems = throwables.elements(); elems.hasMoreElements(); ) {
	exceptions[count++] = ((CThrowableInfo)elems.nextElement()).getThrowable();
      }
    }
  }

  public void setThrowables(CReferenceType[] exceptions) {
    this.exceptions = exceptions;
  }
   
  CSourceMethod getAccessor(TypeFactory factory, CSourceClass target, boolean isSuper) {
    CSourceMethod       accessor;

    if (accessors == null) {
      accessors = new Hashtable();
      accessor = null;
    } else {
      accessor = (CSourceMethod) accessors.get(target);
    }
    if (accessor == null) {
      JAccessorMethod ac = new JAccessorMethod(factory, target, this, false, isSuper, -1);      
      accessor = (CSourceMethod) ac.getMethod();
      accessors.put(target, accessor);
    }
    return accessor; 
  }

  public CTypeVariable lookupTypeVariable(String ident) {
    for (int i = 0; i < typeVariables.length; i++) {
      if (ident == typeVariables[i].getIdent()) {
        return typeVariables[i];
      }
    }
    if (isStatic()) {
      return null;
    } else {
      return getOwner().lookupTypeVariable(ident);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (QUICK)
  // ----------------------------------------------------------------------

  /**
   * Returns true iff this method is native.
   */
  public boolean isNative() {
    return CModifier.contains(getModifiers(), ACC_NATIVE);
  }

  /**
   * Returns true iff this method is abstract.
   */
  public boolean isAbstract() {
    return CModifier.contains(getModifiers(), ACC_ABSTRACT);
  }

  /**
   * Returns true iff this method is a constructor.
   */
  public boolean isConstructor() {
    return getIdent() == JAV_CONSTRUCTOR;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (Extended)
  // ----------------------------------------------------------------------
  
  public abstract boolean isInvariant();
  public abstract boolean isPrecondition();
  public abstract boolean isPostcondition();
  public abstract CMethod getPreconditionMethod();
  public abstract CMethod getPostconditionMethod();
  public abstract CReferenceType getOldValueStore();

  /**
   * Find out that pre/postcondition of supermethod is already checked by 
   * pre/postcondition of another method.
   */
  public boolean includesSuperMethod(CMethod m) {
    return false;
  }
  // ----------------------------------------------------------------------
  // CHECK MATCHING
  // ----------------------------------------------------------------------

  /**
   * equals
   * search if two methods have same signature
   * @param	other		the other method
   */
  public boolean equals(CMethod other) {
    if (!getOwner().equals(other.getOwner())) {
      return false;
    } else if (getIdent() != other.getIdent()) {
      return false;
    } else if (parameters.length != other.parameters.length) {
      return false;
    } else {
      for (int i = 0; i < parameters.length; i++) {
	if (!parameters[i].equals(other.parameters[i])) {
	  return false;
	}
      }
      return true;
    }
  }

  /**
   * Is this method applicable to the specified invocation (JLS 15.12.2.1) ?
   * @param	ident		method invocation name
   * @param	actuals		method invocation arguments
   */
  public boolean isApplicableTo(CTypeContext context, String ident, CType[] actuals, CReferenceType[] substitution) {
    if (ident != getIdent()) {
      return false;
    } else if (actuals.length != parameters.length) {
      return false;
    } else {
      for (int i = 0; i < actuals.length; i++) {
	// method invocation conversion = assigment conversion without literal narrowing
	// we just look at the type and do not consider literal special case
	if (!actuals[i].isAssignableTo(context, parameters[i], substitution)) {
	  return false;
	}
      }
      return true;
    }
  }

  /**
   * Is this method more specific than the one given as argument (JLS 15.12.2.2) ?
   * @param	other		the method to compare to
   */
  public boolean isMoreSpecificThan(CTypeContext context, CMethod other, CReferenceType[] substitution) throws UnpositionedError{
    //    if (!getOwnerType().isAssignableTo(context,other.getOwnerType(), substitution)) {
    if (!getOwner().descendsFrom(other.getOwner())) {
      return false;
    } else if (parameters.length != other.parameters.length) {
      return false;
    } else {
      for (int i = 0; i < other.parameters.length; i++) {
	// method invocation conversion = assigment conversion without literal narrowing
	// we just look at the type and do not consider literal special case
	if (!parameters[i].getErasure(context).isAssignableTo(context, other.parameters[i].getErasure(context))) {
	  return false;
	}
      }

      return true;
    }
  }

  /**
   * Has this method the same signature as the one given as argument ?
   * NOTE: return type not considered
   * @param	other		the method to compare to
   */
  public boolean hasSameSignature(CMethod other, CReferenceType[] substitution) {
    if (parameters.length != other.parameters.length) {
      return false;
    } else {
      for (int i = 0; i < parameters.length; i++) {
	if (!parameters[i].equals(other.parameters[i], substitution)) {
	  return false;
	}
      }
      return true;
    }
  }
  /**
   * Has this method the same signature as the one given as argument ?
   * NOTE: return type not considered
   * @param	other		the method to compare to
   */
  public boolean hasSameSignature(CTypeContext context, CMethod other) throws UnpositionedError{
    if (parameters.length != other.parameters.length) {
      return false;
    } else {
      for (int i = 0; i < parameters.length; i++) {
	if (!other.parameters[i].getErasure(context).equals(parameters[i].getErasure(context))) {
	  return false;
	}
      }
      return true;
    }
  }

  /**
   * Checks that the inheritence of this two methods is allowed.
   *
   * @param	superMethod		method which it overrides
   * @exception	UnositionedError	the analysis detected an error
   */
  public void checkInheritence(CTypeContext context, CMethod superMethod, CReferenceType[] substitution) 
    throws UnpositionedError {
    // JLS 8.4.6.1 :
    // A compile-time error occurs if an instance method overrides a
    // static method.
    if (!this.isStatic() && superMethod.isStatic()) {
      throw new UnpositionedError(KjcMessages.METHOD_INSTANCE_OVERRIDES_STATIC,
                                  this, 
                                  superMethod.getOwner());
    }

    // JLS 8.4.6.2 :
    // A compile-time error occurs if a static method hides an instance method.
    if (this.isStatic() && !superMethod.isStatic()) {
      throw new UnpositionedError(KjcMessages.METHOD_STATIC_HIDES_INSTANCE,
                                  this, 
                                  superMethod.getOwner());
    }

    if (!context.getTypeFactory().isGenericEnabled()) {
      // JLS 8.4.6.3 :
      // If a method declaration overrides or hides the declaration of another
      // method, then a compile-time error occurs if they have different return
      // types or if one has a return type and the other is void.
      if (! returnType.equals(superMethod.getReturnType())) {
        throw new UnpositionedError(KjcMessages.METHOD_RETURN_DIFFERENT, this);
      }
    } else {
      // JSR 41 3.2
      if (returnType.isClassType()) {
        if (!returnType.isAssignableTo(context, superMethod.getReturnType(), substitution)) {
          throw new UnpositionedError(KjcMessages.METHOD_RETURN_NOT_SUBTYPE, new Object[] {this, superMethod, superMethod.getOwner(), returnType, superMethod.getReturnType()});
        }      
      } else {
        if (! returnType.equals(superMethod.getReturnType())) {
          throw new UnpositionedError(KjcMessages.METHOD_RETURN_DIFFERENT, this);
        }        
      }
    }

    // JLS 8.4.6.3 :
    // The access modifier of an overriding or hiding method must provide at
    // least as much access as the overridden or hidden method.
    boolean	moreRestrictive;

    if (superMethod.isPublic()) {
      moreRestrictive = !this.isPublic();
    } else if (superMethod.isProtected()) {
      moreRestrictive = !(this.isProtected() || this.isPublic());
    } else if (! superMethod.isPrivate()) {
      // default access
      moreRestrictive = this.isPrivate();
    } else {
      // a private method is not inherited
      throw new InconsistencyException("bad access: " + superMethod.getModifiers());
    }

    if (moreRestrictive) { 
      throw new UnpositionedError(KjcMessages.METHOD_ACCESS_DIFFERENT,
                                  this, 
                                  superMethod.getOwner());
    }
  }
  /**
   * Checks that overriding/hiding is correct.
   *
   * @param	superMethod		method which it overrides
   * @exception	UnositionedError	the analysis detected an error
   */
  public void checkOverriding(CTypeContext context, CMethod superMethod, CReferenceType[] substitution) 
    throws UnpositionedError 
  {
    // JLS 8.4.3.3 :
    // A method can be declared final to prevent subclasses from overriding
    // or hiding it. It is a compile-time error to attempt to override or
    // hide a final method.
    if (superMethod.isFinal()) {
      throw new UnpositionedError(KjcMessages.METHOD_OVERRIDE_FINAL, this);
    }

    // JLS 8.4.6.1 :
    // A compile-time error occurs if an instance method overrides a
    // static method.
    if (!this.isStatic() && superMethod.isStatic()) {
      throw new UnpositionedError(KjcMessages.METHOD_INSTANCE_OVERRIDES_STATIC,
                                  this, 
                                  superMethod.getOwner());
    }

    // JLS 8.4.6.2 :
    // A compile-time error occurs if a static method hides an instance method.
    if (this.isStatic() && !superMethod.isStatic()) {
      throw new UnpositionedError(KjcMessages.METHOD_STATIC_HIDES_INSTANCE,
                                  this, 
                                  superMethod.getOwner());
    }

    if (!context.getTypeFactory().isGenericEnabled()) {
      // JLS 8.4.6.3 :
      // If a method declaration overrides or hides the declaration of another
      // method, then a compile-time error occurs if they have different return
      // types or if one has a return type and the other is void.
      if (! returnType.equals(superMethod.getReturnType())) {
        throw new UnpositionedError(KjcMessages.METHOD_RETURN_DIFFERENT, this);
      }
    } else {
      // JSR 41 3.2
      if (returnType.isClassType()) {
        if (!returnType.isAssignableTo(context, superMethod.getReturnType(), substitution)) {
          throw new UnpositionedError(KjcMessages.METHOD_RETURN_NOT_SUBTYPE, new Object[] {this, superMethod, superMethod.getOwner(), returnType, superMethod.getReturnType()});
        }      
      } else {
        if (! returnType.equals(superMethod.getReturnType())) {
          throw new UnpositionedError(KjcMessages.METHOD_RETURN_DIFFERENT, this);
        }        
      }
    }

    // JLS 8.4.6.3 :
    // The access modifier of an overriding or hiding method must provide at
    // least as much access as the overridden or hidden method.
    boolean	moreRestrictive;

    if (superMethod.isPublic()) {
      moreRestrictive = !this.isPublic();
    } else if (superMethod.isProtected()) {
      moreRestrictive = !(this.isProtected() || this.isPublic());
    } else if (! superMethod.isPrivate()) {
      // default access
      moreRestrictive = this.isPrivate();
    } else {
      // a private method is not inherited
      throw new InconsistencyException("bad access: " + superMethod.getModifiers());
    }

    if (moreRestrictive) { 
      throw new UnpositionedError(KjcMessages.METHOD_ACCESS_DIFFERENT,
                                  this, 
                                  superMethod.getOwner());
    }

    // JLS 8.4.4 :
    // A method that overrides or hides another method, including methods that
    // implement abstract methods defined in interfaces, may not be declared to
    // throw more CHECKED exceptions than the overridden or hidden method.
    CReferenceType[]	exc = superMethod.getThrowables();

  _loop_:
    for (int i = 0; i < exceptions.length; i++) {
      if (exceptions[i].isCheckedException(context)) {
	for (int j = 0; j < exc.length; j++) {
	  if (exceptions[i].isAssignableTo(context, exc[j], substitution)) {
	    continue _loop_;
	  }
	}
	throw new UnpositionedError(KjcMessages.METHOD_THROWS_DIFFERENT, 
                                    this, 
                                    exceptions[i]);
      }
    }
  }


  /**
   * Returns a string representation of this method.
   */
  public String toString() {
    StringBuffer	buffer = new StringBuffer();

    buffer.append(returnType);
    buffer.append(" ");
    buffer.append(getOwner());
    buffer.append(".");
    buffer.append(getIdent());
    buffer.append("(");
    for (int i = 0; i < parameters.length; i++) {
      if (i != 0) {
	buffer.append(", ");
      }
      buffer.append(parameters[i]);
    }
    buffer.append(")");

    return buffer.toString();
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a sequence of bytecode
   * @param	code		the code sequence
   * @param	nonVirtual	force non-virtual dispatching
   */
  public void genCode(GenerationContext context, boolean nonVirtual) {
    genCode(context, getOwner(), nonVirtual);
  }
  /**
   * Generates a sequence of bytecode
   * @param	code		the code sequence
   * @param	nonVirtual	force non-virtual dispatching
   */
  public void genCode(GenerationContext context, CClass prefixClass, boolean nonVirtual) {
    CodeSequence        code = context.getCodeSequence();
    String              prefixName;

    if (getOwner().getQualifiedName() == JAV_OBJECT) {
      // java.lang.Object: 
      // JLS 13.1 * Given a method invocation expression in a class or 
      // interface C referencing a method named m declared in a (possibly 
      // distinct) class or interface D, we define the qualifying type of 
      // the method invocation as follows:
      // If D is Object then the qualifying type of the expression is Object
      prefixName = JAV_OBJECT;
    } else {
      prefixName = prefixClass.getQualifiedName();
    }

    if (prefixClass.isInterface() && !(prefixName == JAV_OBJECT)) {
      int		size = 0;

      for (int i = 0; i < parameters.length; i++) {
	size += parameters[i].getSize();
      }

      code.plantInstruction(new InvokeinterfaceInstruction(prefixName,
							   getIdent(),
							   getSignature(),
							   size + 1)); // this
    } else {
      int	opcode;

      if (isStatic()) {
	opcode = opc_invokestatic;
      } else if (nonVirtual || isPrivate()) {
	// JDK1.2 does not like ...|| isPrivate() || isFinal()
	opcode = opc_invokespecial;
      } else {
	opcode = opc_invokevirtual;
      }

      code.plantMethodRefInstruction(opcode,
				     prefixName,
				     getIdent(),
				     getSignature());
    }
  }

  /**
   * computes the place on the heap which the parameters of the method require
   */
  public int getHeapForParameter() {
    int         heap = 0;

    for (int i = 0; i < parameters.length; i++) {
      heap += parameters[i].getSize();
    }

    return (isStatic() ? 0 : 1) + heap;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected CType               returnType;
  private CType[]               parameters;
  private CReferenceType[]      exceptions;
  private CTypeVariable[]       typeVariables;
  private Hashtable             accessors;

  public static final CMethod[] EMPTY = new CMethod[0];    
}
