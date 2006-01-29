/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.io.File;
import java.util.ArrayList;

import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents an undefined class (something that comes from a bad classfile)
 * This class is not usable to anything, so it will sooner or later produce a comprehensive error.
 */
public class CBadClass extends CClass {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class export from file
   */
  public CBadClass(String qualifiedName) {
    super(null, "undefined", 0, getIdent(qualifiedName), qualifiedName, null, false, true);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * descendsFrom
   * @param	from	an other CClass
   * @return	true if this class inherit from "from" or equals "from"
   */
  public boolean descendsFrom(CClass from) {
    return false;
  }

  /**
   * @param	ident		the name of the field
   * @return	the field
   */
  public final CField getField(String ident) {
    return null;
  }

  // ----------------------------------------------------------------------
  // LOOKUP
  // ----------------------------------------------------------------------

  /**
   * This can be used to see if a given class name is visible
   *    inside of this file.  This includes globally-qualified class names that
   *    include their package and simple names that are visible thanks to some
   *    previous import statement or visible because they are in this file.
   *    If one is found, that entry is returned, otherwise null is returned.
   * @param	caller		the class of the caller
   * @param	name		a TypeName (6.5.2)
   */
  public CClass lookupClass(CClass caller, String name) {
    return null;
  }

  /**
   * lookupMethod
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod lookupMethod(String name, CType[] params) {
    return null;
  }

  /**
   * lookupSuperMethod
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod[] lookupSuperMethod(String name, CType[] params) {
    return new CMethod[0];
  }

  /**
   * lookupField
   * search for a field
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CField lookupField(CClass caller, CClass primary, String name) {
    return null;
  }

  /**
   * @return	true if this member is accessible
   */
  public boolean isAccessible(CClass from) {
    return false;
  }

  /**
   * Returns a list of abstract methods
   */
  public CMethod[] getAbstractMethods(CTypeContext context, boolean test) {
    return new CMethod[0];
  }

  /**
   * collectInterfaceMethods
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   */
  public void collectInterfaceMethods(ArrayList v) {
  }

  /**
   * collectAbstractMethods
   * search for a matching method with the provided type parameters
   * look in parent hierarchy as needed
   * @param	name		method name
   * @param	params		method parameters
   */
  public void collectAbstractMethods(ArrayList v) {
  }

  // ----------------------------------------------------------------------
  // GENERATE CLASSFILE INFO
  // ----------------------------------------------------------------------

  /**
   * Generate the code in a class file
   * @param	classes		a vector to add inner classes
   */
  public void genClassFile(File destination) {
    throw new InconsistencyException();
  }
}
