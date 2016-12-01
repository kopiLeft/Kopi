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

package org.kopi.bytecode.memcnt;

import org.kopi.bytecode.classfile.ClassInfo;
import org.kopi.bytecode.classfile.CodeInfo;
import org.kopi.bytecode.classfile.FieldInfo;
import org.kopi.bytecode.classfile.FieldRefInstruction;
import org.kopi.bytecode.classfile.Instruction;
import org.kopi.bytecode.classfile.MethodInfo;
import org.kopi.bytecode.classfile.MethodRefInstruction;
import org.kopi.bytecode.classfile.NoArgInstruction;
import org.kopi.bytecode.classfile.PushLiteralInstruction;

/**
 * This class instruments a class info.
 */
public class Instrumenter implements org.kopi.bytecode.classfile.Constants {

  // --------------------------------------------------------------------
  // UTILITIES
  // --------------------------------------------------------------------

  /**
   * Instruments a class info
   */
  public static ClassInfo instrument(ClassInfo code) {
    Instrumenter	ins;

    ins = new Instrumenter(code);
    return ins.addCounting() ? ins.getClassInfo() : null;
  }


  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Creates a new instrumenter instance.
   * @param	classInfo		the class to instrument
   */
  public Instrumenter(ClassInfo classInfo) {
    this.classInfo = classInfo;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns a new, optimized code info structure.
   */
  public ClassInfo getClassInfo() {
    return classInfo;
  }

  // --------------------------------------------------------------------
  // INSTRUMENT CLASS
  // --------------------------------------------------------------------

  /**
   * Instruments the class for instance creation counting.
   * Counting is not added if:
   * - the class is an interface, or
   * - the class is not public, or
   * - the class is already instrumented.
   * @return	true iff the class has been instrumented.
   */
  public boolean addCounting() {
    System.err.print("Processing " + classInfo.getName() + " ... "); System.err.flush();
    System.err.print("checked ... "); System.err.flush();
    if ((classInfo.getModifiers() & ACC_INTERFACE) != 0) {
      // it is an interface
      System.err.println(" is an interface."); System.err.flush();
      return false;
    } else if ((classInfo.getModifiers() & ACC_PUBLIC) == 0) {
      // it is not public
      System.err.println(" is not public."); System.err.flush();
      return false;
    } else if (findMethod("memcnt$incr") != null) {
      // it is already instrumented
      System.err.println(" is already instrumented."); System.err.flush();
      return false;
    } else {
      addCounterField();
      addIncrementMethod();
      instrumentInitializer();
      instrumentConstructors();
      System.err.println(" done!"); System.err.flush();
      return true;
    }
  }

  /*
   * Adds a field that holds the number of created instances.
   */
  private void addCounterField() {
    FieldInfo		counter;

    counter = new FieldInfo((short)(ACC_PUBLIC | ACC_STATIC),
			    "memcnt$totins",
			    "I",
                            null,
			    null,	// constant value
			    false,	// deprecated ?
			    false	// synthetic ?
			    );
    addField(counter);
  }

  private void addIncrementMethod() {
    MethodInfo		incr;
    CodeInfo		code;
    Instruction[]	insns;

    insns = new Instruction[] {
      new FieldRefInstruction(opc_getstatic, classInfo.getName() + "/memcnt$totins", "I"),
      new PushLiteralInstruction(1),
      new NoArgInstruction(opc_iadd),
      new FieldRefInstruction(opc_putstatic, classInfo.getName() + "/memcnt$totins", "I"),
      new NoArgInstruction(opc_return)
    };

    code = new CodeInfo(insns,
			null,		// handlers
			null,		// line numbers
			null		// local variables
			);

    incr = new MethodInfo((short)(ACC_PRIVATE | ACC_STATIC),
			  "memcnt$incr",
			  "()V",
                          null, // gen. signature
			  null,	// exceptions,
			  code,
			  false,	// deprecated ?
			  false	// synthetic ?
			  );

    addMethod(incr);
  }

  /**
   * Instruments the static initialializer with a call to the registry.
   */
  private void instrumentInitializer() {
    MethodInfo		init;
    CodeInfo		code;
    Instruction[]	insns;

    init = findMethod("<clinit>");
    if (init == null) {
      // no class initializer: create one
      code = new CodeInfo(new Instruction[]{ new NoArgInstruction(opc_return) },
			  null,		// handlers
			  null,		// line numbers
			  null		// local variables
			  );
      init = new MethodInfo((short)(ACC_STATIC),
			    "<clinit>",
			    "()V",
                            null,       // generic signature
			    null,	// exceptions,
			    code,
			    false,	// deprecated ?
			    false	// synthetic ?
			    );
      addMethod(init);
    }

    // insert call to registry at the beginning
    insns = new Instruction[] {
      new PushLiteralInstruction(classInfo.getName().replace('/', '.')),
      new MethodRefInstruction(opc_invokestatic, "org/kopi/bytecode/memcnt/Registry/register", "(Ljava/lang/String;)V")
    };
    insertInstructions(init, insns);
  }

  /**
   * Instruments the constructors with a call to the increment method.
   */
  private void instrumentConstructors() {
    MethodInfo[]	methods = classInfo.getMethods();

    if (methods != null) {
      for (int i = 0; i < methods.length; i++) {
	if (methods[i].getName().equals("<init>")) {
	  instrumentConstructor(methods[i]);
	}
      }
    }
  }

  /**
   * Instruments the specified constructor with a call to the increment method.
   */
  private void instrumentConstructor(MethodInfo cstr) {
    if (constructorToInstrument(cstr)) {
      insertInstructions(cstr,
			 new Instruction[] {
			   new MethodRefInstruction(opc_invokestatic,
						    classInfo.getName() + "/memcnt$incr",
						    "()V")
			     });
    }
  }

  /**
   * Returns true iff the constructor is to be instrumented.
   */
  private boolean constructorToInstrument(MethodInfo cstr) {
    if (cstr.getCodeInfo() == null) {
      return false;
    } else {
     
      return true;
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------------------------

  /**
   * Finds field with specified name.
   */
   /*** never used locally 
  /*private FieldInfo findField(String name) {
    FieldInfo[]	fields = classInfo.getFields();

    if (fields == null) {
      return null;
    } else {
      for (int i = 0; i < fields.length; i++) {
	if (fields[i].getName().equals(name)) {
	  return fields[i];
	}
      }
      return null;
    }
  }*/

  /**
   * Adds a field.
   */
  private void addField(FieldInfo field) {
    FieldInfo[]		oldFields;
    FieldInfo[]		newFields;

    oldFields = classInfo.getFields();
    if (oldFields == null) {
      newFields = new FieldInfo[1];
    } else {
      newFields = new FieldInfo[oldFields.length + 1];
      System.arraycopy(oldFields, 0, newFields, 0, oldFields.length);
    }
    newFields[newFields.length - 1] = field;
    classInfo.setFields(newFields);
  }

  /**
   * Adds a method.
   */
  private void addMethod(MethodInfo method) {
    MethodInfo[]	oldMethods;
    MethodInfo[]	newMethods;

    oldMethods = classInfo.getMethods();
    if (oldMethods == null) {
      newMethods = new MethodInfo[1];
    } else {
      newMethods = new MethodInfo[oldMethods.length + 1];
      System.arraycopy(oldMethods, 0, newMethods, 0, oldMethods.length);
    }
    newMethods[newMethods.length - 1] = method;
    classInfo.setMethods(newMethods);
  }

  /**
   * Finds method with specified name.
   */
  private MethodInfo findMethod(String name) {
    MethodInfo[]	methods = classInfo.getMethods();

    if (methods == null) {
      return null;
    } else {
      for (int i = 0; i < methods.length; i++) {
	if (methods[i].getName().equals(name)) {
	  return methods[i];
	}
      }
      return null;
    }
  }

  /**
   * Inserts specified instructions at beginning of code
   */
  private void insertInstructions(MethodInfo method, Instruction[] preamble) {
    CodeInfo		code;
    Instruction[]	oldInsns;
    Instruction[]	newInsns;
    int			oldLength;

    code = method.getCodeInfo();

    oldInsns = code.getInstructions();
    oldLength = oldInsns.length;
    while (oldInsns[oldLength - 1] == null) {
      oldLength -= 1;
    }

    newInsns = new Instruction[preamble.length + oldLength];
    System.arraycopy(preamble, 0, newInsns, 0, preamble.length);
    System.arraycopy(oldInsns, 0, newInsns, preamble.length, oldLength);

    for (int i = 0; i < newInsns.length; i++) {
      if (newInsns[i] == null) {
	System.err.println("*** instruction " + i + " of " + newInsns.length + " is null");
      }
    }

    method.setCodeInfo(new CodeInfo(newInsns,
				    code.getHandlers(),
				    code.getLineNumbers(),
				    code.getLocalVariables()));
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private ClassInfo		classInfo;
}
