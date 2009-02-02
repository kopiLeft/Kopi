/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
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

package com.kopiright.bytecode.classfile;

/**
 * VMS 4 : Members.
 *
 * Root class for class members (fields, methods, inner classes and interfaces)
 *
 */
public abstract class Member implements Constants {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a member object.
   *
   * @param	modifiers	access permission to and properties of this member
   */
  public Member(short modifiers) {
    this.modifiers = modifiers;
  }

  /**
   * Constructs a member object.
   */
  Member() {
    this((short)0);
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the modifiers of this member
   */
  public short getModifiers() {
    return modifiers;
  }

  /**
   * Returns the modifiers of this member
   */
  public void setModifiers(short modifiers) {
    this.modifiers = modifiers;
  }

  /**
   * Returns the name of the this member
   */
  public abstract String getName();

  /**
   * Returns the type of the this member
   */
  public abstract String getSignature();

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private short			modifiers;
}
