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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import at.dms.bytecode.classfile.ClassConstant;
import at.dms.bytecode.classfile.ClassFileFormatException;
import at.dms.bytecode.classfile.InstructionOverflowException;
import at.dms.bytecode.classfile.ConstantPoolOverflowException;
import at.dms.bytecode.classfile.LocalVariableOverflowException;
import at.dms.bytecode.classfile.ClassInfo;
import at.dms.bytecode.classfile.CodeInfo;
import at.dms.bytecode.classfile.FieldInfo;
import at.dms.bytecode.classfile.FieldRefInstruction;
import at.dms.bytecode.classfile.InnerClassInfo;
import at.dms.bytecode.classfile.MethodInfo;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.util.base.Utils;

/**
 * This class represents the exported members of a class (inner classes, methods and fields)
 * It is build from a parsed files so values are accessibles differently after build and
 * after interface checked
 */
/**
 * @author taoufik
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSourceClass extends CClass {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class export from source
   */
  public CSourceClass(CClass owner,
                      TokenReference where,
                      int modifiers,
                      String ident,
                      String qualifiedName,
                      CTypeVariable[] typeVariables,
                      boolean deprecated,
                      boolean synthetic,
                      JTypeDeclaration decl)
  {
    super(owner, where.getFile(), modifiers, ident, qualifiedName, null, deprecated, synthetic);
    setTypeVariables(typeVariables);
    this.decl = decl;
  }

  /**
   * Ends the definition of this class
   */
  public void close(CReferenceType[] interfaces,
		    CReferenceType superClass,
		    Hashtable fields,
		    CMethod[] methods)
  {
    setSuperClass(superClass);
    super.close(interfaces, fields, methods);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * add synthetic parameters to method definition
   * @see #genInit
   * @see #genOuterSynthecicParams
   * @see #genOuterLocalAccess
   */
  public CType[] genConstructorArray(CType[] params) {
    boolean		hasOuterThis;
    
    hasOuterThis = isNested() && !isStatic() && hasOuterThis();

    addOuterFromDerived();

    if (!hasOuterThis && outers == null) { 
      return params;
    } else {
      CType[]		result;
      int		size;
      int		pos;


      size = params.length;
      if (hasOuterThis) {
        size += 1;
      }
      if (outers != null) {
        size += outers.size();
      }
      result = new CType[size];

      /*
       * JLS 15.9.5.1
       * The first formal parameter of the constructor of C
       * represents the value of the immediately enclosing instance of
       * i with respect to S.
       */
      pos = 0;

      if (hasOuterThis) {
        result[pos++] = getOwnerType();
      }

      for (int i = 0; i < params.length; pos++, i++) {
        result[pos] = params[i];
      }

      if (outers != null) {
        for (Enumeration elems = outers.keys(); elems.hasMoreElements(); ) {
          result[pos++] = ((JLocalVariable)elems.nextElement()).getType();
        }
      }
      
      return result;
    }
  }

  /**
   * if a local/anonymous class defined inside this class
   * uses a fianl local variable defined outside this class,
   * this class must provide the value of that local variable.
   */
  private void addOuterFromDerived() {
    if (derived) {
      return;
    }
    derived = true;

    CClass              superClass;
    Hashtable           superOuters;

    if (superClassType != null 
        && (superClass = superClassType.getCClass()) instanceof CSourceClass 
        && (superOuters = ((CSourceClass) superClass).outers) != null) {
      for (Enumeration elems = superOuters.keys(); elems.hasMoreElements(); ) {
        JLocalVariable	var = (JLocalVariable)elems.nextElement();
        OuterVarField   varField = ((OuterVarField) superOuters.get(var));

        if (outers == null || outers.get(var) == null) {
          createFieldForOuterVar(var, varField.clazz);
        }
      }
    }

    CReferenceType[]      innerClasses = getInnerClasses();

    for (int i = 0; i < innerClasses.length; i++) {
      CSourceClass      sourceClass = (CSourceClass) (innerClasses[i].getCClass());

      sourceClass.addOuterFromDerived();

      if (sourceClass.outers == null) {
        continue;
      }

      for (Enumeration elems = sourceClass.outers.keys(); elems.hasMoreElements(); ) {
        JLocalVariable	var = (JLocalVariable)elems.nextElement();
        OuterVarField   varField = ((OuterVarField)(sourceClass.outers.get(var)));

        if ((outers == null || outers.get(var) == null)
            && isDefinedInside(varField.clazz)) {
          createFieldForOuterVar(var, varField.clazz);
        }
      }
    }
    if (getOwner() != null) {
      ((CSourceClass)getOwner()).addOuterFromDerived();
    }
  }


  /** 
   * Creates a field which stores the value of an outer local final variable
   * which is accessed by an anonymous class or local class.
   */
  private CSourceField createFieldForOuterVar(JLocalVariable var, CClass clazz) {
    String		name;
    CSourceField	field;

    name = "var$" + var.getIdent();
    field = new CSourceField(this, ACC_PRIVATE | ACC_FINAL, name, var.getType(), false, true); // synthetic

    if (outers == null) {
      outers = new Hashtable();
    }
    outers.put(var, new OuterVarField(field, clazz)); // local vars are uniq

    addField(field);
    syntheticFieldCount++;

    return field;
  }


  /**
   * add synthetic parameters to method call
   * @see #genInit
   * @see #genOuterLocalAccess
   * @see #genConstructorArray
   */
  public void genOuterSyntheticParams(GenerationContext context) {
    genOuterSyntheticParams(context, -1);
  }

  public void genOuterSyntheticParams(GenerationContext context, int length) {
    if (outers != null) {
      for (Enumeration elems = outers.keys(); elems.hasMoreElements(); ) {
        JLocalVariable	var = (JLocalVariable)elems.nextElement();
        CSourceClass    owner = (CSourceClass)getOwner();
        Hashtable       ownerOuters = owner.outers;
        OuterVarField   varField;

        if (ownerOuters != null
            && (varField = (OuterVarField) (ownerOuters.get(var))) !=null 
            && varField.clazz != this) {
          new JFieldAccessExpression(TokenReference.NO_REF,
                                     new JThisExpression(TokenReference.NO_REF),
                                     ((OuterVarField) ((CSourceClass)getOwner()).outers.get(var)).field).genCode(context, false);
        } else {
          JLocalVariable        local;

          if (length >= 0) {
            // call for super constructor call
            int                 fieldPosition = ((OuterVarField) outers.get(var)).field.getPosition();
            final int           realPosition =  length + (hasOuterThis() ? 1 : 0) + fieldPosition - (getFieldCount() - syntheticFieldCount);


            local = new JGeneratedLocalVariable(null, 0, var.getType(), var.getIdent(), null) {
                /**
                 * @return the local index in context variable table
                 */
                public int getPosition() {
                  return 1 /*this*/ + realPosition;
                }
              };
          } else {
            local = var;
          }
          new JLocalVariableExpression(TokenReference.NO_REF, local).genCode(context, false);
        }
      }
    }
  }


  /**
   * Gets the code to access outer local vars
   * @see #genInit
   * @see #genOuterSyntheticParams
   * @see #genConstructorArray
   */
  public JExpression getOuterLocalAccess(TokenReference ref,
                                         JLocalVariable var,
                                         final CMethod constructor,
                                         CClass clazz)
  {
    CSourceField	field;
    OuterVarField       varField;

    if (outers == null) {
      outers = new Hashtable();
    }
    if ((varField = (OuterVarField) outers.get(var)) == null) {
      field = createFieldForOuterVar(var, clazz);
    } else {
      field = varField.field;
    }

    if (constructor != null) {
      final CSourceField	ffield = field;
      JGeneratedLocalVariable   local = new JGeneratedLocalVariable(null, 0, var.getType(), var.getIdent(), null) {
	  /**
	   * @return the local index in context variable table
	   */
	  public int getPosition() {
	    return 1 /*this*/ + constructor.getParameters().length 
              + (hasOuterThis() ? 1 : 0) + ffield.getPosition() 
              - (getFieldCount() - syntheticFieldCount);
	  }
	};

      return new JCheckedExpression(ref, new JLocalVariableExpression(ref, local));
    } else {
      return new JFieldAccessExpression(ref, new JOwnerExpression(ref, getCClass()), field.getIdent()) {
            public boolean isLValue(CExpressionContext context) {
              // these fields can ot be assigned by the progammer
              return false;
            }
        };
    }
  }

  /**
   * add synthetic parameters to method def
   * @see #genOuterSyntheticParams
   * @see #genOuterLocalAccess
   * @see #genConstructorArray
   */
  public void genInit(GenerationContext context, int countLocals) {
    CodeSequence code = context.getCodeSequence();

    countLocals++; // this

    if (isNested() && !isStatic() && hasOuterThis()) {
      JGeneratedLocalVariable           var;

      var = new JGeneratedLocalVariable(null, 0, getOwnerType(), JAV_OUTER_THIS, null); // $$$
      var.setPosition(1);
      countLocals += var.getType().getSize();
      code.plantLoadThis();
      var.genLoad(context);
      code.plantFieldRefInstruction(opc_putfield,
                                    getQualifiedName(),
                                    JAV_OUTER_THIS,
                                    getOwnerType().getSignature());
    }

    if (outers != null) {
      for (Enumeration elems = outers.keys(); elems.hasMoreElements(); ) {
        JLocalVariable                  ovar;
        JGeneratedLocalVariable         var;

        ovar = (JLocalVariable)elems.nextElement();
        var = new JGeneratedLocalVariable(null, 0, ovar.getType(), ovar.getIdent(), null);
        var.setPosition(countLocals);
        countLocals += var.getType().getSize();
        code.plantLoadThis();
        var.genLoad(context);
        code.plantFieldRefInstruction(opc_putfield,
                                      getQualifiedName(),
                                      "var$" + var.getIdent(),
                                      var.getType().getSignature());
      }
    }
  }

  /**
   * Returns the invariant method for this class or null if no invariant exists.
   */
  public CMethod getInvariant() {
    return invariantMethod;
  }

  /**
   * Sets the invariant method of this class.
   */
  public void setInvariant(CMethod method) {
    invariantMethod = method;
  }

  /**
   * @return true if this class is defines the condition methods of an interface
   */
  public boolean isAssertionClass() {
    return assertionClass;
  }

  public void setAssertionClass(boolean assertionClass) {
    this.assertionClass = assertionClass;
  }

  public void analyseConditions() throws PositionedError {
    if (decl != null) {
      JTypeDeclaration  tmp = decl;

      decl = null;
      tmp.analyseConditions();
    }
  }
  
  public void addInnerReference(CReferenceType ref) {
    if (innerRefs == null) {
      innerRefs = new ArrayList();
    } else 
      if (innerRefs.contains(ref)) {
        return;
    	}	
    innerRefs.add(ref);
  }
  
  private CReferenceType[] getInnerReferences() {
    if (innerRefs == null) {
      	return new CReferenceType[0];
    } else {
      return (CReferenceType[]) innerRefs.toArray(new CReferenceType[innerRefs.size()]);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generates a JVM class file for this class.
   *
   * @param	optimizer	the bytecode optimizer to use
   * @param	destination	the root directory of the class hierarchy
   */
  public void genCode(BytecodeOptimizer optimizer, String destination, TypeFactory factory)
    throws IOException, ClassFileFormatException, PositionedError
  {
    decl = null; // garbage

    String[]	classPath = Utils.splitQualifiedName(getSourceFile(), File.separatorChar);

    try {
      ClassInfo	classInfo = new ClassInfo((short)(getModifiers() & (~ACC_STATIC)),
					  getQualifiedName(),
					  getSuperClass() == null ? null : getSuperClass().getQualifiedName(),
					  genInterfaces(),
					  genFields(factory),
					  genMethods(optimizer, factory),
					  genInnerClasses(),
					  classPath[1],
                                          getSuperClass() == null ? null : getGenericSignature(),
					  isDeprecated(),
                                          isSynthetic());
      classInfo.write(destination);
    } catch (ConstantPoolOverflowException e) {
      throw new PositionedError(new TokenReference(classPath[1], 0), KjcMessages.CONSTANTPOOL_OVERFLOW);
    } catch (InstructionOverflowException e) {
      throw new PositionedError(new TokenReference(classPath[1], 0), KjcMessages.INSTRUCTION_OVERFLOW, e.getMethod());
    } catch (LocalVariableOverflowException e) {
      throw new PositionedError(new TokenReference(classPath[1], 0), KjcMessages.LOCAL_VARIABLE_OVERFLOW, e.getMethod());
    } catch (ClassFileFormatException e) {
      System.err.println("GenCode failure in source class: "+getQualifiedName());
      throw e;
    }
  }

  /**
   * Builds information about interfaces to be stored in a class file.
   */
  private ClassConstant[] genInterfaces() {
    CReferenceType[]	source = getInterfaces();
    ClassConstant[]	result;

    result = new ClassConstant[source.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new ClassConstant(source[i].getQualifiedName());
    }
    return result;
  }

  /**
   * Builds information about inner classes to be stored in a class file.
   */
  private InnerClassInfo[] genInnerClasses() {
    CReferenceType[]	source = getInnerClasses();
    CReferenceType[]	refs   = getInnerReferences();
    int			count;

    count = source.length + refs.length;
    if (isNested()) {
      count += 1;
    }

    if (count == 0) {
      return null;
    } else {
      InnerClassInfo[]	result;

      result = new InnerClassInfo[count];
      for (int i = 0; i < source.length; i++) {
        // add inners infos
        CClass	clazz = source[i].getCClass();

        result[i] = new InnerClassInfo(clazz.getQualifiedName(),
                                       getQualifiedName(),
                                       clazz.getIdent(),
                                       (short)clazz.getModifiers());
      }
      for (int i = 0; i < refs.length; i++) {
        // add inner refs infos
        CClass	clazz = refs[i].getCClass();

        result[i+source.length] = new InnerClassInfo(clazz.getQualifiedName(),
                                                     clazz.getOwner().getQualifiedName(),
                                                     clazz.getIdent(),
                                                     (short)clazz.getModifiers());
      }      
      if (isNested()) {
        // add outer class info
        result[result.length - 1] = new InnerClassInfo(getQualifiedName(),
                                                       getOwner().getQualifiedName(),
                                                       getIdent(),
                                                       (short)getModifiers());
      }
      return result;
    }
  }

  /**
   * Builds information about methods to be stored in a class file.
   *
   * @param	optimizer	the bytecode optimizer to use
   */
  private MethodInfo[] genMethods(BytecodeOptimizer optimizer, TypeFactory factory)
    throws ClassFileFormatException
    {
    CMethod[]		source = getMethods();
    MethodInfo[]	result;

    result = new MethodInfo[source.length];
    for (int i = 0; i < source.length; i++) {
      result[i] = ((CSourceMethod)source[i]).genMethodInfo(optimizer, factory);
    }
    return result;
  }

  /**
   * Builds information about fields to be stored in a class file.
   */
  private FieldInfo[] genFields(TypeFactory factory) {
    CField[]		source = getFields();
    FieldInfo[]		result;

    result = new FieldInfo[source.length];
    for (int i = 0; i < source.length; i++) {
      result[i] = ((CSourceField)source[i]).genFieldInfo(factory);
    }
    return result;
  }

  static class OuterVarField {
    final CSourceField          field;
    final CClass                clazz;

    OuterVarField (CSourceField field, CClass clazz) {
      this.field = field;
      this.clazz = clazz;
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean           derived = false; // inherit outer local vars?
  private boolean           assertionClass;
  private Hashtable         outers;
  private ArrayList         innerRefs; // used to store inner refs
  private int               syntheticFieldCount;
  private CMethod           invariantMethod;
  private JTypeDeclaration  decl;
}
