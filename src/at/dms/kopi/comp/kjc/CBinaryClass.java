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
 * $Id: CBinaryClass.java,v 1.2 2004/09/28 10:34:01 graf Exp $
 */

package at.dms.kopi.comp.kjc;

import java.util.Hashtable;
import java.util.Enumeration;

import at.dms.bytecode.classfile.ClassInfo;
import at.dms.bytecode.classfile.FieldInfo;
import at.dms.bytecode.classfile.InnerClassInfo;
import at.dms.bytecode.classfile.MethodInfo;

import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents the exported members of a binary class, i.e.
 * a class that has been compiled in a previous session.
 */
public class CBinaryClass extends CClass { 

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class export from file
   */
  public CBinaryClass(SignatureParser signatureParser,
                      ClassReader reader,
                      TypeFactory factory, 
                      ClassInfo classInfo) {
    super(getOwner(reader, factory, classInfo.getName(), classInfo.getInnerClasses()),
	  classInfo.getSourceFile(),
	  classInfo.getModifiers(),
	  getIdent(classInfo.getName()),
	  classInfo.getName(),
	  null,
	  classInfo.isDeprecated(),
          classInfo.isSynthetic());
    String              genericSignature =  classInfo.getGenericSignature();
    CReferenceType[]	interfaces;

    if (genericSignature == null) {
      setSuperClass(classInfo.getSuperClass() == null ? null : factory.createType(classInfo.getSuperClass(), true));
      interfaces = loadInterfaces(factory, classInfo.getInterfaces());
      setTypeVariables(CTypeVariable.EMPTY);
    } else {
      SignatureParser.ClassSignature    classSignature = signatureParser.parseClassSignature(factory, genericSignature);

      setSuperClass(classSignature.superType);
      interfaces = classSignature.interfaces;
      setTypeVariables(classSignature.typeVariables);      
    }

    FieldInfo[]         fields = classInfo.getFields();
    Hashtable           fields_H = null;
    boolean             hasOuterThis = false;

    fields_H = new Hashtable(fields.length + 1, 1.0f);

    for (int i = 0; i < fields.length; i++) {
      CBinaryField field = new CBinaryField(signatureParser, factory, this, fields[i]);

      if (field.getIdent().equals(JAV_OUTER_THIS)) {
	hasOuterThis = true;
      }
      fields_H.put(field.getIdent(), field);
    }

    setHasOuterThis(hasOuterThis);

    MethodInfo[]	methods = classInfo.getMethods();
    CBinaryMethod[]     methods_V = new CBinaryMethod[methods.length];

    for (int i = 0; i < methods.length; i++) {
      methods_V[i] = CBinaryMethod.create(signatureParser, factory, this, methods[i]);
    }

    setInnerClasses(loadInnerClasses(factory, classInfo.getInnerClasses()));

    close(interfaces, fields_H, methods_V);
  }

  public void checkTypes(CBinaryTypeContext context) throws UnpositionedError {
    CTypeVariable[]     typeVariables = getTypeVariables();
    CBinaryTypeContext  self;

    for (int i = 0; i < typeVariables.length; i++) {
      typeVariables[i] = (CTypeVariable) typeVariables[i].checkType(context);
    }
    setTypeVariables(typeVariables);

    self = new CBinaryTypeContext(context.getClassReader(), 
                                  context.getTypeFactory(),
                                  context,
                                  this);
    if (superClassType != null) {
        superClassType = (CReferenceType) superClassType.checkType(self);
    }

    CBinaryMethod[]     methods = (CBinaryMethod[]) getMethods();

    for (int i = 0; i < methods.length; i++) {
      methods[i].checkTypes(self);
    }

    for (Enumeration elems = fields.elements(); elems.hasMoreElements(); ) {
      ((CBinaryField) elems.nextElement()).checkTypes(self);
    }

    CReferenceType[]        interfaces =  getInterfaces();

    for (int i = 0; i < interfaces.length; i++) {
      interfaces[i] = (CReferenceType)interfaces[i].checkType(self);
    }

    CReferenceType[]        innerClasses =  getInnerClasses();

    for (int i = 0; i < innerClasses.length; i++) {
      innerClasses[i] = (CReferenceType)innerClasses[i].checkType(self);
    }
  }

  /**
   *
   */
  protected CReferenceType[] loadInterfaces(TypeFactory tf, String[] interfaces) {
    if (interfaces != null) {
      CReferenceType[]	ret;
      ret = new CReferenceType[interfaces.length];
      for (int i = 0; i < interfaces.length; i++) {
	ret[i] = tf.createType(interfaces[i], true);
      }
      return ret;
    } else {
      return CReferenceType.EMPTY;
    }
  }

  /**
   *
   */
  protected CReferenceType[] loadInnerClasses(TypeFactory factory, InnerClassInfo[] inners) {
    if (inners != null) {
      CReferenceType[]      innerClasses = new CReferenceType[inners.length];

      for (int i = 0; i < inners.length; i++) {
	innerClasses[i] = factory.createType(inners[i].getQualifiedName(), true);
      }
      return innerClasses;
    } else {
      return CReferenceType.EMPTY;
    }
  }

  private static CClass getOwner(ClassReader reader, TypeFactory factory, String name, InnerClassInfo[] innerClasses) {
    if ((innerClasses == null) || (innerClasses.length == 0)){
      return null;
    } else  {
      for (int i = 0; i < innerClasses.length; i++) {
        if (name == innerClasses[i].getQualifiedName()) {
          if (innerClasses[i].getOuterClass() == null) {
            continue;
          }
          return reader.loadClass(factory, innerClasses[i].getOuterClass());
        }
      }
      return null;
    } 
  }
}
