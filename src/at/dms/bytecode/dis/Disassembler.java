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
 * $Id: Disassembler.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.bytecode.dis;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import at.dms.bytecode.classfile.BadAccessorException;
import at.dms.bytecode.classfile.ClassInfo;
import at.dms.bytecode.classfile.ClassPath;
import at.dms.bytecode.classfile.CodeInfo;
import at.dms.bytecode.classfile.FieldInfo;
import at.dms.bytecode.classfile.HandlerInfo;
import at.dms.bytecode.classfile.Instruction;
import at.dms.bytecode.classfile.LocalVariableInfo;
import at.dms.bytecode.classfile.Member;
import at.dms.bytecode.classfile.MethodInfo;
import at.dms.compiler.base.CompilerMessages;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.InconsistencyException;
import at.dms.util.base.Utils;


/**
 * This class prints the classfile in ksm syntaxt
 */
public class Disassembler implements at.dms.bytecode.classfile.Constants, Constants {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Create a disassembler object from a classinfo
   */
   private Disassembler(ClassInfo classInfo, boolean optionSortMembers, boolean optionShowStack) {
     this.classInfo = classInfo;
     this.optionSortMembers = optionSortMembers;
     this.optionShowStack = optionShowStack;
   }


  /**
   * Disassembles a class file.
   */
  public static void disassemble(ClassPath classpath, String sourceFile, String destination, DisOptions options)
    throws UnpositionedError
  {
    ClassInfo		classInfo;

    if (sourceFile.endsWith(".class")) {
      try {
	classInfo = new ClassInfo(new DataInputStream(new BufferedInputStream(new FileInputStream(sourceFile), 2048)), false);
      } catch (Exception e) {
	e.printStackTrace();
	throw new UnpositionedError(DisMessages.FILE_NOT_FOUND, new Object[] { sourceFile });
      }
    } else {
      classInfo = classpath.loadClass(sourceFile.replace('.', '/'), false);
    }

    if (classInfo == null) {
      throw new UnpositionedError(DisMessages.CLASS_NOT_FOUND, new Object[] { sourceFile });
    }

    writeAssemblerFile(classInfo, destination, options);
  }

  /**
   * Creates a class file from class info
   */
  private static void writeAssemblerFile(ClassInfo classInfo, String destination, DisOptions options)
    throws UnpositionedError
  {
    String[]	classPath = Utils.splitQualifiedName(classInfo.getName());

    if (classPath[0] != null) {		// the class is part of a package
      String	classDir =
	classPath[0].replace('.', File.separatorChar).replace('/', File.separatorChar);

      if (destination != null) {
	destination += File.separator + classDir;
      }
    }

    if (!options.stdout && options.destination != null) {
      // check that destination exists or else create it
      File	destDir = new File(options.destination);

      if (! destDir.exists()) {
	destDir.mkdirs();
      }

      if (! destDir.isDirectory()) {
	throw new UnpositionedError(DisMessages.IO_EXCEPTION, new Object[] { options.destination });
      }
    }

    File		outputFile = null;
    try {
      PrintWriter	out;

      if (options.stdout) {
	outputFile = null;
	out = new PrintWriter(System.out);
      } else {
	outputFile = new File(destination, classPath[1] + ".ksm");
	out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
      }

      Disassembler	dis = new Disassembler(classInfo,
					       options.stack,
					       options.sorted);
      dis.writeClass(new IndentingWriter(out));

      out.flush();
      out.close();
    } catch (java.io.IOException e) {
      throw new UnpositionedError(DisMessages.IO_EXCEPTION, new Object[]{ outputFile.getPath(), e.getMessage() });
    }
  }

  /**
   * Prints the class file
   */
  private void writeClass(IndentingWriter out) {
    out.println("// compiler version: " + classInfo.getMajorVersion() + "." + classInfo.getMinorVersion());
    out.println();

    // Headers
    if (classInfo.getSourceFile() != null) {
      out.println("@source \"" + classInfo.getSourceFile() + "\"");
    }
    if (classInfo.isSynthetic()) {
      out.println("@synthetic");
    }
    if (classInfo.isDeprecated()) {
      out.println("@deprecated");
    }
    // extended JSR 41
    if (classInfo.getGenericSignature() != null) {
      out.println("@signature \"" + classInfo.getGenericSignature() + "\"");
    }
   // super is always set (and would print "synchronized")
    writeModifiers(out, classInfo.getModifiers() & ~ACC_SUPER);
    if ((classInfo.getModifiers() & ACC_INTERFACE) > 0) {
      out.print("interface ");
    } else {
      out.print("class ");
    }
    out.print(convertQualifiedName(classInfo.getName()));

    if (classInfo.getSuperClass() != null) {
      out.print(" extends ");
      out.print(convertQualifiedName(classInfo.getSuperClass()));
    }

    String[]	interfaces = classInfo.getInterfaces();
    if (interfaces != null && interfaces.length > 0) {
      out.print(" implements");
      for (int i = 0; i < interfaces.length; i++) {
	out.print((i == 0 ? " " : ", ") + convertQualifiedName(interfaces[i]));
      }
    }

    out.print(" {");

    FieldInfo[]	fields = classInfo.getFields();
    if (optionSortMembers) {
      sort(fields);
    }
    for (int i = 0; i < fields.length; i++) {
      writeField(out, fields[i]);
    }

    out.println();

    MethodInfo[]	methods = classInfo.getMethods();
    if (optionSortMembers) {
      sort(methods);
    }
    for (int i = 0; i < methods.length; i++) {
      writeMethod(out, methods[i]);
      if (i != methods.length - 1) {
	out.println();
      }
    }
    out.println();
    out.println("}");
  }

  /**
   * Prints fields
   */
  private void writeField(IndentingWriter out, FieldInfo info) {
    out.incrementLevel();
    out.println();

    out.println("/**");
    out.println(" * " + info.getName());
    out.println(" */");
    if (info.isSynthetic()) {
      out.println("@synthetic");
    }
    if (info.isDeprecated()) {
      out.println("@deprecated");
    }
    // extended JSR 41
    if (info.getGenericSignature() != null) {
      out.println("@signature \"" + info.getGenericSignature() + "\"");
    }
    writeModifiers(out, info.getModifiers());
    out.print(convertFieldSignature(info.getSignature()) + " ");
    out.print(info.getName());

    Object	value = info.getConstantValue();
    if (value != null) {
      out.print(" = " + convertLiteral(value));
    }
    out.print(";");
    out.decrementLevel();
  }

  /**
   * Prints methods
   */
  private void writeMethod(IndentingWriter out, MethodInfo info) {
    out.incrementLevel();
    out.println();
    out.println("/**");
    out.println(" * " + info.getName());
    out.println(" *"); 
    if (info.getCodeInfo() != null) {
      out.println(" * stack\t" + info.getCodeInfo().getMaxStack());
      out.println(" * locals\t" + info.getCodeInfo().getMaxLocals());
    }
    out.println(" */");
    if (info.isSynthetic()) {
      out.println("@synthetic");
    }
    if (info.isDeprecated()) {
      out.println("@deprecated");
    }
    if (info.getGenericSignature() != null) {
      out.println("@signature \"" + info.getGenericSignature() + "\"");
    }

    writeModifiers(out, info.getModifiers());
    String[]	type = convertMethodSignature(info.getSignature());
    out.print(type[1]);
    out.print(" ");
    out.print(info.getName());
    out.print(type[0]);

    String[]	exceptions = info.getExceptions();
    if (exceptions != null) {
      out.print(" throws");
      for (int i = 0; i < exceptions.length; i++) {
	out.print((i == 0 ? " " : ", ") + exceptions[i].replace('/', '.'));
      }
    }
    if (info.getCodeInfo() == null) {
      out.print(";");
    } else {
      out.print(" {");
      writeCodeInfo(out, info.getCodeInfo());
      out.println();
      out.print("}");
    }
    out.decrementLevel();
  }

  /**
   * Prints code
   */
  private void writeCodeInfo(IndentingWriter out, CodeInfo info) {
    Instruction[]		insns = info.getInstructions();

    InstructionHandle[]		handles = new InstructionHandle[insns.length];
    for (int i = 0; i < handles.length; i++) {
      handles[i] = new InstructionHandle(insns[i], i);
    }

    try {
      info.transformAccessors(new HandleCreator(insns, handles));
    } catch (BadAccessorException e) {
      throw new InconsistencyException(e.getMessage());
    }

    out.incrementLevel();
    for (int i = 0; i < handles.length; i++) {
      handles[i].write(out, optionShowStack);
    }
    writeHandlerInfo(out, info);
    writeLocalVariableInfo(out, info);
    out.decrementLevel();
  }

  private void writeModifiers(IndentingWriter out, int modifiers) {
    if ((modifiers & ACC_PUBLIC) != 0) {
      out.print("public ");
    } else if ((modifiers & ACC_PROTECTED) != 0) {
      out.print("protected ");
    } else if ((modifiers & ACC_PRIVATE) != 0) {
      out.print("private ");
    }

    if ((modifiers & ACC_STATIC) != 0) {
      out.print("static ");
    }
    if ((modifiers & ACC_ABSTRACT) != 0) {
      out.print("abstract ");
    }
    if ((modifiers & ACC_FINAL) != 0) {
      out.print("final ");
    }
    if ((modifiers & ACC_NATIVE) != 0) {
      out.print("native ");
    }
    if ((modifiers & ACC_SYNCHRONIZED) != 0) {
      out.print("synchronized ");
    }
    if ((modifiers & ACC_TRANSIENT) != 0) {
      out.print("transient ");
    }
    if ((modifiers & ACC_VOLATILE) != 0) {
      out.print("volatile ");
    }
    if ((modifiers & ACC_STRICT) != 0) {
      out.print("strictfp ");
    }
  }

  // --------------------------------------------------------------------

  /**
   * Prints exception handlers
   */
  private void writeHandlerInfo(IndentingWriter out, CodeInfo info) {
    HandlerInfo[]	handlers = info.getHandlers();

    for (int i = 0; i < handlers.length; i++) {
      out.println();
      out.print("@catch\t" + ((InstructionHandle)handlers[i].getHandler()).getLabel());
      if (handlers[i].getThrown() != null) {
	out.print(" " + handlers[i].getThrown().replace('/', '.'));
      }
      out.print(" [" + ((InstructionHandle)handlers[i].getStart()).getLabel() + ", " + ((InstructionHandle)handlers[i].getEnd()).getLabel() + "]");
    }
  }

  /**
   * Prints local variables
   */
  private void writeLocalVariableInfo(IndentingWriter out, CodeInfo info) {
    LocalVariableInfo[]	localVariables = info.getLocalVariables();

    if (localVariables == null) {
      return;
    }

    for (int i = 0; i < localVariables.length; i++) {
      if (i == 0) {
	out.println();
      }

      out.println("@var\t" + localVariables[i].getSlot()
		  + ": " + localVariables[i].getName()
		  + " " + convertFieldSignature(localVariables[i].getType())
		  + " [ " + ((InstructionHandle)localVariables[i].getStart()).getLabel()
		  + ", " + ((InstructionHandle)localVariables[i].getEnd()).getLabel()
		  + "]");
    }
  }

  // ----------------------------------------------------------------------
  // UTILITY METHODS WITH PACKAGE SCOPE
  // ----------------------------------------------------------------------

  /**
   * Converts a field signature into ksm syntax.
   *
   * @param	signature	the signature to convert.
   * @return	a string in ksm syntax.
   */
  /*package*/ static String convertFieldSignature(String signature) {
    int		brackets = 0;

    while (signature.charAt(brackets) == '[') {
      brackets++;
    }

    signature = signature.substring(brackets);

    if (signature.charAt(0) == 'Z') {
      signature = "boolean";
    } else if (signature.charAt(0) == 'B') {
      signature = "byte";
    } else if (signature.charAt(0) == 'C') {
      signature = "char";
    } else if (signature.charAt(0) == 'D') {
      signature = "double";
    } else if (signature.charAt(0) == 'F') {
      signature = "float";
    } else if (signature.charAt(0) == 'I') {
      signature = "int";
    } else if (signature.charAt(0) == 'J') {
      signature = "long";
    } else if (signature.charAt(0) == 'S') {
      signature = "short";
    } else if (signature.charAt(0) == 'V') {
      signature = "void";
    } else if (signature.charAt(0) == 'L') {
      signature = signature.substring(1, signature.length() - 1);
    } else {
      throw new InconsistencyException("UNEXPECTED TYPE " + signature.charAt(0));
    }
    for (; brackets > 0; brackets -= 1) {
      signature += "[]";
    }

    return signature.replace('/', '.');
  }

  /**
   * Converts a method signature into ksm syntax.
   *
   * @param	signature	the signature to convert.
   * @return	an array of two string in ksm syntax:
   *		element 0: the argument types
   *		element 1: the return type
   */
  /*package*/ static String[] convertMethodSignature(String signature) {
    if (signature.charAt(0) != '(') {
      throw new InconsistencyException("UNEXPECTED TYPE " + signature);
    }

    String method = "(";

    signature = signature.substring(1);
    while (signature.charAt(0) != ')') {
      int	len = 0;
      while (signature.charAt(len) == '[') {
	len += 1;
      }
      if (signature.charAt(len) != 'L') {
	len += 1;
      } else {
	while (signature.charAt(len) != ';') {
	  len += 1;
	}
	len += 1;
      }
      method += convertFieldSignature(signature.substring(0, len)) + (signature.charAt(len) != ')' ? ", " : "");
      signature = signature.substring(len);
    }
    method += ")";

    return new String[] {method, convertFieldSignature(signature.substring(1))};
  }

  /**
   * Converts a qualified identifier into ksm syntax.
   *
   * @param	ident		the identifier to convert.
   * @return	a string in ksm syntax.
   */
  /*package*/ static String convertQualifiedName(String ident) {
    StringTokenizer	token = new StringTokenizer(ident.replace('/', '.'), ".");
    String		result;

    result = token.nextToken();
    result = isJavaIdentifier(result) ? result : "\'" + result + "\'";
    while (token.hasMoreElements()) {
      String current = token.nextToken();
      result += "." + (isJavaIdentifier(current) ? current : "\'" + current + "\'");
    }
    return result;
  }

  private static boolean isJavaIdentifier(String ident) {
    if (ident.equals("<init>") || ident.equals("<clinit>")) {
      return true;
    } else if (!Character.isJavaIdentifierStart(ident.charAt(0))) {
      return false;
    } else {
      for (int i = 1; i < ident.length(); i++) {
	if (!Character.isJavaIdentifierPart(ident.charAt(i))) {
	  return false;
	}
      }
      return true;
    }
  }

  /**
   * Converts a literal into ksm syntax.
   */
  /*package*/ static String convertLiteral(Object t) {
    if (t instanceof String) {
      return convertStringLiteral((String)t);
    } else if (t instanceof Float) {
      return convertFloatLiteral((Float)t);
    } else if (t instanceof Double) {
      return convertDoubleLiteral((Double)t);
    } else if (t instanceof Long) {
      return convertLongLiteral((Long)t);
    } else {
      return t.toString();
    }
  }

  private static String convertDoubleLiteral(Double t) {
    if (t.isNaN()) {
      return "<NaN>D";
    } else if (t.isInfinite()) {
      return t.doubleValue() > 0 ? "<Inf>D" : "-<Inf>D";
    } else {
      return t + "D";
    }
  }

  private static String convertFloatLiteral(Float t) {
    if (t.isNaN()) {
      return "<NaN>F";
    } else if (t.isInfinite()) {
      return t.floatValue() > 0 ? "<Inf>F" : "-<Inf>F";
    } else {
      return t + "F";
    }
  }

  private static String convertLongLiteral(Long t) {
    return t + "L";
  }

  private static String convertStringLiteral(String t) {
    StringBuffer buffer = new StringBuffer();

    buffer.append("\"");
    for (int i = 0; i < t.length(); i++) {
      switch (t.charAt(i)) {
      case '\t':
	buffer.append("\\t");
	break;
      case '\r':
	buffer.append("\\r");
	break;
      case '\n':
	buffer.append("\\n");
	break;
      case '\"':
	buffer.append("\\\"");
	break;
      case '\\':
	buffer.append("\\\\");
	break;
      case '\'':
	buffer.append("\\'");
	break;
      default:
	buffer.append(t.charAt(i));
      }
    }
    buffer.append("\"");
    return buffer.toString();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Bubble sort an array of FieldInfo wrt the name
   */
  private static final void sort(Member[] data) {
    for (int i = data.length; --i >= 0;) {
      for (int j = 0; j < i; j++) {
	if (data[j].getName().compareTo(data[j + 1].getName()) > 0) {
	  Member	tmp = data[j];
	  data[j] = data[j+1];
	  data[j+1] = tmp;
	}
      }
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final ClassInfo	classInfo;
  private final boolean		optionSortMembers;
  private final boolean		optionShowStack;
}
