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
 * $Id: CContext.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.kopi.comp.kjc;

import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.util.base.MessageDescription;

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
public abstract class CContext extends at.dms.util.base.Utils implements CTypeContext, Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context, it must be different
   *				than null except if called by the top level
   */
  protected CContext(CContext parent, KjcEnvironment environment) {
    this.parent = parent;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (LOOKUP)
  // ----------------------------------------------------------------------

  /**
   * lookupClass
   * search for a class with the provided type parameters
   * @param	caller		the class of the caller
   * @param	name		method name
   * @return	the class if found, null otherwise
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
    return parent.lookupClass(caller, name);
  }

  /**
   * JLS 15.12.2 :
   * Searches the class or interface to locate method declarations that are
   * both applicable and accessible, that is, declarations that can be correctly
   * invoked on the given arguments. There may be more than one such method
   * declaration, in which case the most specific one is chosen.
   *
   * @param	caller		the class of the caller
   * @param	ident		method name
   * @param	actuals		method parameters
   * @return	the method or null if not found
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod lookupMethod(CTypeContext context, CClass caller, CType primary, String ident, CType[] actuals)
    throws UnpositionedError
  {
    return getClassContext().lookupMethod(context, caller, primary, ident, actuals);
  }

  /**
   * Searches the class or interface to locate declarations of fields that are
   * accessible.
   * 
   * @param	caller		the class of the caller
   * @param	ident		the simple name of the field
   * @return	the field definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CField lookupField(CClass caller, CClass primary, String ident)
    throws UnpositionedError
  {
    return getClassContext().lookupField(caller, primary, ident);
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
    return getMethodContext().lookupTypeVariable(ident);
  }
  /**
   * lookupLocalVariable
   * @param	ident		the name of the local variable
   * @return	a variable from an ident in current context
   */
  public JLocalVariable lookupLocalVariable(String ident) {
    return parent.lookupLocalVariable(ident);
  }

  /**
   * used to check, if a final variable/field is assigned
   * in a loop.
   * returns true, if it has to go through a loop to find the 
   * definition. 
   * @param     ident name of the variable of null if it is a field
   */
  public boolean checkForLoop(String ident) {
    return parent.checkForLoop(ident);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (INFOS)
  // ----------------------------------------------------------------------


  /** 
   * JLS 8.1.2: A statement or expression occurs in a static context if and 
   * only if the innermost method, constructor, instance initializer, static 
   * initializer, field initializer, or explicit constructor statement 
   * enclosing the statement or expression is a static method, a static 
   * initializer, the variable initializer of a static variable, or an 
   * explicit constructor invocation statement 
   *
   * @return true iff the context is static
   */
  public boolean isStaticContext() {
    if (parent == null) {
      return false;
    } else {
      return parent.isStaticContext();
    }
  }

  /**
   * Returns the field definition state.
   */
  public CVariableInfo getFieldInfo() {
    return parent.getFieldInfo();
  }

  /**
   * @param	field		the definition of a field
   * @return	a field from a field definition in current context
   */
  public int getFieldInfo(int index) {
    return parent.getFieldInfo(index);
  }

  /**
   * @param	field		the definition of a field
   * @return	a field from a field definition in current context
   */
  public void setFieldInfo(int index, int info) {
    parent.setFieldInfo(index, info);
  }

  /**
   * Returns the enviroment of the compiler
   *
   * @return the environment
   */
  public KjcEnvironment getEnvironment() {
    return environment;
  }

  /**
   * @return the TypeFactory
   */
  public TypeFactory getTypeFactory() {
    return environment.getTypeFactory();
  }

  /**
   * @return the Object used to read class files.
   */
  public ClassReader getClassReader() {
    return environment.getClassReader();
  }

  /**
   * Returns true if warnings should be provided if 
   * something deprecated is used.
   */
  public boolean showDeprecated() {
    return environment.showDeprecated();
  }

  /**
   * called if somewhere something deprecated used.
   */
  public void setDeprecatedUsed() {
    environment.setDeprecatedUsed();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (TREE HIERARCHY)
  // ----------------------------------------------------------------------

  /**
   * getParentContext
   * @return	the parent
   */
  public CContext getParentContext() {
    return parent;
  }

  /**
   * @return	the compilation unit
   */
  public CCompilationUnitContext getCompilationUnitContext() {
    return parent.getCompilationUnitContext();
  }

  /** 
   * getClassContext
   * @return	the near parent of type CClassContext
   */
  public CClassContext getClassContext() {
    return parent.getClassContext();
  }

  /**
   * getMethod
   * @return	the near parent of type CClassContext
   */
  public CMethodContext getMethodContext() {
    return parent.getMethodContext();
  }

  /**
   * Returns the nearest block context (Where yuo can define some local vars)
   */
  public CBlockContext getBlockContext() {
    return parent.getBlockContext();
  }

  // ----------------------------------------------------------------------
  // SYNCHRONIZED HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds the variable for the monitor of the synchronized statement to the 
   * correct context.
   *
   * @param     var     monitor variable 
   */
  public void addMonitorVariable(JLocalVariable var)  throws UnpositionedError {
    parent.addMonitorVariable(var);
  }

  // ----------------------------------------------------------------------
  // CLASS HANDLING
  // ----------------------------------------------------------------------

  /**
   * Adds a class to generate.
   */
  public void addSourceClass(CSourceClass clazz) {
    parent.addSourceClass(clazz);
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
    parent.reportTrouble(trouble);
  }

  // ----------------------------------------------------------------------
  // ERROR HANDLING
  // ----------------------------------------------------------------------

  /**
   * Throws a semantic error detected during analysis.
   *
   * @param	description	the message description
   * @param	parameters	the array of parameters
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void fail(MessageDescription description, Object[] parameters)
    throws UnpositionedError
  {
    throw new UnpositionedError(description, parameters);
  }

  /**
   * Signals a semantic error detected during analysis.
   *
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2	the second parameter
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void fail(MessageDescription description, Object parameter1, Object parameter2)
    throws UnpositionedError
  {
    fail(description, new Object[]{ parameter1, parameter2 });
  }

  /**
   * Verifies an assertion.
   *
   * @param	assertion	the assertion to verify
   * @param	description	the message description
   * @param	parameters	the array of parameters
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void check(boolean assertion,
		    MessageDescription description,
		    Object[] parameters)
    throws UnpositionedError
  {
    if (! assertion) {
      fail(description, parameters);
    }
  }

  /**
   * Verifies an assertion.
   *
   * @param	assertion	the assertion to verify
   * @param	description	the message description
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void check(boolean assertion, MessageDescription description)
    throws UnpositionedError
  {
    check(assertion, description, new Object[]{ null, null });
  }

  /**
   * Verifies an assertion.
   *
   * @param	assertion	the assertion to verify
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void check(boolean assertion,
		    MessageDescription description,
		    Object parameter1)
    throws UnpositionedError
  {
    check(assertion, description, new Object[]{ parameter1, null });
  }

  /**
   * Verifies an assertion.
   *
   * @param	assertion	the assertion to verify
   * @param	description	the message description
   * @param	parameter1	the first parameter
   * @param	parameter2		the second parameter
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void check(boolean assertion,
		    MessageDescription description,
		    Object parameter1,
		    Object parameter2)
    throws UnpositionedError
  {
    check(assertion, description, new Object[]{ parameter1, parameter2 });
  }

  // ----------------------------------------------------------------------
  // DEBUG
  // ----------------------------------------------------------------------

  /**
   * Dumps this context to standard error stream.
   */
  public void dumpContext(String text) {
    System.err.println("*** Dumping " + text);
    dumpContext(1);
    System.err.println("");
  }

  /**
   * Dumps this context to standard error stream.
   */
  public void dumpContext(int level) {
    dumpIndent(level);
    System.err.println(this);
    if (parent != null) {
      parent.dumpContext(level + 1);
    }
  }

  public void dumpIndent(int level) {
    for (int i = 0; i < level; i++) {
      System.err.print("  ");
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected final CContext      parent;
  private final KjcEnvironment  environment;
}
