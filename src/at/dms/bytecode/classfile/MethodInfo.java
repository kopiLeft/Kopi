/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package at.dms.bytecode.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

import at.dms.util.base.InconsistencyException;

/**
 * VMS 4.6: Methods.
 *
 * Each method, and each (class or instance) initialization method is
 * described by this structure.
 */
public class MethodInfo extends Member {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a method entry
   *
   * @param	modifiers	access permission to and properties of the method
   * @param	name		the simple name of the method (or <init> or <clinit>)
   * @param	type		the method signature
   * @param	exceptions	the checked exceptions the method may throw
   * @param	code		the virtual machine instructions and auxiliary infos
   * @param	deprecated	is this method deprecated ?
   * @param	synthetic	is this method synthesized by the compiler ?
   *
   * NOTE:
   * VMS 4.7.4: There must be exactly one Exceptions attribute in each method_info structure.
   */
  public MethodInfo(short modifiers,
		    String name,
		    String type,
                    String genericSignature,
		    String[] exceptions,
		    CodeInfo code,
		    boolean deprecated,
		    boolean synthetic) {
    super(modifiers);
    this.name = new AsciiConstant(name);
    this.type = new AsciiConstant(type);
    if (code != null && code.getParameterCount() == -1) {
      code.setParameterCount(getParameterCount());
    }

    this.attributes = new AttributeList(code,
					exceptions != null && exceptions.length != 0 ? new ExceptionsAttribute(exceptions) : null,
					genericSignature != null ? new SignatureAttribute(genericSignature) : null,
					synthetic ? new SyntheticAttribute() : null);
    if (deprecated) {
      attributes.add( new DeprecatedAttribute());
    }
  }

  /**
   * Constructs a method entry from a class file stream.
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   * @param	interfaceOnly	load only the interface, not the source code
   *
   * @exception	IOException	an io problem has occured
   * @exception	ClassFileFormatException	attempt to read a bad classfile
   */
  public MethodInfo(DataInput in, ConstantPool cp, boolean interfaceOnly)
    throws IOException, ClassFileFormatException
  {
    setModifiers((short)in.readUnsignedShort());
    this.name = (AsciiConstant)cp.getEntryAt(in.readUnsignedShort());
    this.type = (AsciiConstant)cp.getEntryAt(in.readUnsignedShort());
    this.attributes = new AttributeList(in, cp, interfaceOnly);

    CodeInfo	code = getCodeInfo();
    if (code != null && code.getParameterCount() == -1) {
      code.setParameterCount(getParameterCount());
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the name of this method
   */
  public String getName() {
    return name.getValue();
  }

  /**
   * Sets the name of the this method
   */
  public void setName(String name) {
    this.name = new AsciiConstant(name);
  }

  /**
   * Returns the type of the this method
   */
  public String getSignature() {
    return type.getValue();
  }

  /**
   * Returns the type of the this method
   */
  public void setSignature(String type) {
    this.type = new AsciiConstant(type);
  }

  /**
   * Returns the generic signature of this method
   */
  public String getGenericSignature() {
    Attribute  attr = attributes.get(Constants.ATT_SIGNATURE);

    return attr == null ? getSignature() : ((SignatureAttribute)attr).getSignature();
  }

  /**
   * Returns the generic signature of this method
   */
  public void setGenericSignature(String type) {
    attributes.add(new SignatureAttribute(type));
  }

  /**
   * Returns the exceptions of this method
   */
  public String[] getExceptions() {
    Attribute		attr = attributes.get(Constants.ATT_EXCEPTIONS);

    return attr == null ? null : ((ExceptionsAttribute)attr).getExceptions();
  }

  /**
   * Sets the exceptions
   */
  public void setExceptions(String[] exceptions) {
    if (exceptions != null) {
      attributes.add(new ExceptionsAttribute(exceptions));
    } else {
      attributes.remove(Constants.ATT_EXCEPTIONS);
    }
  }

  /**
   * Returns true if the field is deprecated
   */
  public boolean isDeprecated() {
    return attributes.get(Constants.ATT_DEPRECATED) != null;
  }

  /**
   * Sets the deprecated attribute of this field
   */
  public void setDeprecated(boolean deprecated) {
    if (deprecated) {
      attributes.add(new DeprecatedAttribute());
    } else {
      attributes.remove(Constants.ATT_DEPRECATED);
    }
  }

  /**
   * Returns true if the field is synthetic
   */
  public boolean isSynthetic() {
    return attributes.get(Constants.ATT_SYNTHETIC) != null;
  }

  /**
   * Returns true if the field is synthetic
   */
  public void setSynthetic(boolean synthetic) {
    if (synthetic) {
      attributes.add(new SyntheticAttribute());
    } else {
      attributes.remove(Constants.ATT_SYNTHETIC);
    }
  }

  /**
   * Returns true if this method is the invariant
   */
  public boolean isInvariant() {
    return attributes.get(Constants.ATT_INVARIANT) != null;
  }

  /**
   * Mark this method as invariant
   */
  public void setInvariant(boolean invariant) {
    if (invariant) {
      attributes.add(new InvariantAttribute());
    } else {
      attributes.remove(Constants.ATT_INVARIANT);
    }
  }

  /**
   * Returns true if this method is a precondition
   */
  public boolean isPrecondition() {
    return attributes.get(Constants.ATT_PRECONDITION) != null;
  }

  /**
   * (Un-)Mark this method as a precondition
   */
  public void setPrecondition(boolean precondition) {
    if (precondition) {
      attributes.add(new PreconditionAttribute());
    } else {
      attributes.remove(Constants.ATT_PRECONDITION);
    }
  }

  /**
   * Returns true if this method is a postcondition
   * @see #setPostcondition(String)
   * @see #getOldValueStore()
   */
  public boolean isPostcondition() {
    return attributes.get(Constants.ATT_POSTCONDITION) != null;
  }

  /**
   * Returns true if this method is a postcondition
   * @see #setPostcondition(String)
   */
  public String getOldValueStore() {
    Attribute		attr = attributes.get(Constants.ATT_POSTCONDITION);

    return attr == null ? null : ((PostconditionAttribute)attr).getOldValueStore();
  }

  /**
   * Mark this method as a postcondition. 
   *
   * @param store name of the inner class with old values or null;
   * @see #getOldValueStore()
   */
  public void setPostcondition(String store) {
    if (store == null) {
      attributes.add(new PostconditionAttribute());
    } else {
      attributes.add(new PostconditionAttribute(store));
    }
  }

  /**
   * Returns the precondition method of this method or null if 
   * there is no such method.
   */
  public MethodDescription getPreconditionMethod() {
    Attribute   attr = attributes.get(Constants.ATT_CONSTRAINTS);

    if (attr == null) {
      return null;
    } else {
      return ((ConstraintsAttribute) attr).getPrecondition();
    }
  }
 
 /**
   * Returns the postcondition method of this method or null if 
   * there is no such method.
   */
  public MethodDescription getPostconditionMethod() {
    Attribute   attr = attributes.get(Constants.ATT_CONSTRAINTS);

    if (attr == null) {
      return null;
    } else {
      return ((ConstraintsAttribute) attr).getPostcondition();
    }
  }

  /**
   * Set the pre- & postcondition method of this method 
   *
   * @param pre precondition method
   * @param post postcondition method
   */
  public void setConditionMethods(MethodDescription pre, MethodDescription post) {
    attributes.add(new ConstraintsAttribute(pre, post));
  }

  /**
   * Returns the code attribute associated with this method
   */
  public CodeInfo getCodeInfo() {
    Attribute		attr = attributes.get(Constants.ATT_CODE);

    return attr == null ? null : (CodeInfo)attr;
  }

  /**
   * Sets the code attribute associated with this method
   */
  public void setCodeInfo(CodeInfo info) {
    if (info != null) {
      info.setParameterCount(getParameterCount());
      attributes.add(info);
    } else {
      attributes.remove(Constants.ATT_CODE);
    }
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  public void resolveConstants(ConstantPool cp) throws ClassFileFormatException {
    try {
      cp.addItem(name);
      cp.addItem(type);
      attributes.resolveConstants(cp);
    } catch (ClassFileFormatException e) {
      throw e;
    }
  }

  /**
   * Write this class into the the file (out) getting data position from
   * the constant pool
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   * @exception	ClassFileFormatException	attempt to
   *					write a bad classfile info
   */
  public void write(ConstantPool cp, DataOutput out)
    throws IOException, ClassFileFormatException
  { 
    try {
      out.writeShort(getModifiers() & MODIFIER_MASK);
      out.writeShort(name.getIndex());
      out.writeShort(type.getIndex());
      attributes.write(cp, out);
    } catch (InstructionOverflowException e ) {
      e.setMethod(name.getValue()+getSignature());
      throw e;
    } catch (LocalVariableOverflowException e ) {
      e.setMethod(name.getValue()+getSignature());
      throw e;
    }
  }

  // --------------------------------------------------------------------
  // PRIVATE METHODS
  // --------------------------------------------------------------------

  /**
   * Computes the number of parameters.
   */
  protected int getParameterCount() {
    String	signature = getSignature();
    int		paramCnt = 0;

    if ((getModifiers() & ACC_STATIC) == 0) {
      // an instance method always passes "this" as first, hidden parameter
      paramCnt += 1;
    }

    if (signature.charAt(0) != '(') {
      throw new InconsistencyException("invalid signature " + signature);
    }

    int		pos = 1;

  _method_parameters_:
    for (;;) {
      switch (signature.charAt(pos++)) {
      case ')':
	break _method_parameters_;

      case '[':
	while (signature.charAt(pos) == '[') {
	  pos += 1;
	}
	if (signature.charAt(pos) == 'L') {
	  while (signature.charAt(pos) != ';') {
	    pos += 1;
	  }
	}
	pos += 1;

	paramCnt += 1;
	break;

      case 'L':
	while (signature.charAt(pos) != ';') {
	  pos += 1;
	}
	pos += 1;

	paramCnt += 1;
	break;

      case 'Z':
      case 'B':
      case 'C':
      case 'S':
      case 'F':
      case 'I':
	paramCnt += 1;
	break;

      case 'D':
      case 'J':
	paramCnt += 2;
	break;

      default:
	throw new InconsistencyException("invalid signature " + signature);
      }
    }

    return paramCnt;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  /**
   * Valid modifiers for methods.
   *
   * VMS 4.6 : All bits of the access_flags item not assigned in
   * Table 4.5 are reserved for future use. They should be set to zero
   * in generated class files.
   */
  private static final int		MODIFIER_MASK =
    ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_STATIC
    | ACC_FINAL | ACC_SYNCHRONIZED | ACC_NATIVE
    | ACC_ABSTRACT | ACC_STRICT;

  private AsciiConstant			name;
  private AsciiConstant			type;
  private AttributeList			attributes;
}
