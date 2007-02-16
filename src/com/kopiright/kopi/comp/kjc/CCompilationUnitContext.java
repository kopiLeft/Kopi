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

import java.util.Vector;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.InconsistencyException;

/**
 * This class represents a local context during checkBody
 * It follows the control flow and maintain informations about
 * variable (initialized, used, allocated), exceptions (thrown, catched)
 * It also verify that context is still reachable
 *
 * There is a set of utilities method to access fields, methods and class
 * with the name by clamping the parsing tree
 * @see CContext
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CBodyContext
 * @see CBlockContext
 */
public class CCompilationUnitContext extends CContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a compilation unit context.
   */
  CCompilationUnitContext(Compiler compiler,
                          KjcEnvironment environment,
			  CCompilationUnit cunit,
			  Vector classes)
  {
    super(null, environment);
    this.compiler = compiler;
    this.cunit = cunit;
    this.classes = classes;
  }

  /**
   * Constructs a compilation unit context.
   */
  CCompilationUnitContext(Compiler compiler,
                          KjcEnvironment environment, 
                          CCompilationUnit cunit) {
    this(compiler, environment, cunit, null);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (INFOS)
  // ----------------------------------------------------------------------

  /**
   * Returns the field definition state.
   */
  public CVariableInfo getFieldInfo() {
    return null;
  }

  /**
   * @param	field		the definition of a field
   * @return	a field from a field definition in current context
   */
  public int getFieldInfo(CField field) {
    return 0;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (LOOKUP)
  // ----------------------------------------------------------------------

  /**
   * @param	caller		the class of the caller
   * @return	a class according to imports or null if error occur
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
    return cunit.lookupClass(caller, name);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (TREE HIERARCHY)
  // ----------------------------------------------------------------------

  /**
   * getParentContext
   * @return	the parent
   */
  public CContext getParentContext() {
    throw new InconsistencyException();
  }

  /**
   * getClass
   * @return	the near parent of type CClassContext
   */
  public CClassContext getClassContext() {
    return null;
  }

  /**
   * getMethod
   * @return	the near parent of type CMethodContext
   */
  public CMethodContext getMethodContext() {
    return null;
  }

  /**
   * @return	the compilation unit
   */
  public CCompilationUnitContext getCompilationUnitContext() {
    return this;
  }

  public CBlockContext getBlockContext() {
    return null;
  }

  /**
   * Searches the class, interface and Method to locate declarations of TV's that are
   * accessible.
   * 
   * @param	ident		the simple name of the field
   * @return	the TV definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CTypeVariable lookupTypeVariable(String ident)
    throws UnpositionedError
  {
    return null;
  }
  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Reports a semantic error detected during analysis.
   *
   * @param	trouble		the error to report
   */
  public void reportTrouble(PositionedError trouble) {
    compiler.reportTrouble(trouble);
  }

  // ----------------------------------------------------------------------
  // CLASS HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds a class to generate
   */
  public void addSourceClass(CSourceClass clazz) {
    classes.addElement(clazz);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private /*final*/ Compiler		compiler;
  private /*final*/ Vector		classes;
  private /*final*/ CCompilationUnit	cunit;
}
