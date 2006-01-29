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

import java.util.ArrayList;
import java.util.Hashtable;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.util.base.MessageDescription;

/**
 * This class represents a local context during checkBody
 * It follows the control flow and maintain informations about
 * variable (initialized, used, allocated), exceptions (thrown, catched)
 * It also verify that context is still reachable
 *
 * There is a set of utilities method to access fields, methods and class
 * with the name by clamping the parsing tree
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CBlockContext extends CBodyContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context, it must be different
   *				than null except if called by the top level
   */
  CBlockContext(CMethodContext parent, KjcEnvironment environment, int localVars) {
    super(parent, environment);

    this.localVars = localVars == 0 ? null : new ArrayList(localVars);
    this.localsPosition = 0;
    this.parentIndex = 0;
    this.localsIndex = 0;
    this.childBlocks = null;
  }

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context, it must be different
   *				than null except if called by the top level
   */
  public CBlockContext(CBodyContext parent, KjcEnvironment environment) {
    this(parent, environment, 5);
  }

  /**
   * Construct a block context, it supports local variable allocation
   * throw statement and return statement
   * @param	parent		the parent context, it must be different
   *				than null except if called by the top level
   */
  public CBlockContext(CBodyContext parent, KjcEnvironment environment, int predictedVars) {
    super(parent, environment);

    CBlockContext	parentBlock = parent.getBlockContext();

    this.localVars = new ArrayList(predictedVars);
    this.localsPosition = parentBlock.localsPosition();
    this.parentIndex = parentBlock.localsIndex();
    this.localsIndex = 0;
    this.childBlocks = null;

    parentBlock.registerChildBlock(this);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Verify everything is okay at the end of this context
   */
  public void close(TokenReference ref) {
    // default, no errors
    verifyLocalVarUsed();
    super.close(ref);
  }

  /**
   * check everything correct
   */
  private void verifyLocalVarUsed() {
    for (int i = 0; i < localsIndex; i++) {
      JLocalVariable	var = (JLocalVariable)localVars.get(i);

      if (!var.isUsed() && !var.getIdent().startsWith("_")) {
	MessageDescription		mesg = null;

	switch (var.getDescription()) {
	case JLocalVariable.DES_PARAMETER:
	  mesg = KjcMessages.UNUSED_PARAMETER;
 	  break;
	case JLocalVariable.DES_CATCH_PARAMETER:
	  mesg = KjcMessages.UNUSED_CATCH_PARAMETER;
 	  break;
	case JLocalVariable.DES_LOCAL_VAR:
	  mesg = KjcMessages.UNUSED_LOCALVAR;
 	  break;
	default:
	  continue;
	}
	reportTrouble(new CWarning(var.getTokenReference(),
				   mesg,
				   var.getIdent()));
      } else {
	if (var.getDescription() == JLocalVariable.DES_LOCAL_VAR && !var.isFinal()) {
	  reportTrouble(new CWarning(var.getTokenReference(),
				     KjcMessages.CONSTANT_VARIABLE_NOT_FINAL,
				     var.getIdent()));
	}
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * addLocal variable
   * @param	var		the name of the variable
   * @param	initialized	is the varaible already initialized
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void addVariable(JLocalVariable var) throws UnpositionedError {
    if (localVars == null) {
      localVars = new ArrayList();
    }

    // verify that the variable is not defined in this or an enclosing block
    check(lookupLocalVariable(var.getIdent()) == null,
	  KjcMessages.VARIABLE_REDECLARED, var.getIdent());

    var.setPosition(localsPosition);
    var.setIndex(localsIndex + parentIndex);

    localVars.add(var);
    verify(++localsIndex == localVars.size()); // $$$ 2 infos
    localsPosition += var.getType().getSize();
  }

  public void registerChildBlock(CBlockContext child) {
     if (childBlocks == null) {
	 childBlocks = new ArrayList();
     }
     childBlocks.add(child);
  }

  /**
   * Fix the position of local variables as a monitor variable has been
   * added to the stack.
   *
   * @param     incr     the update increment for local variables
   */
  public void fixVariablePositions(int increment) throws UnpositionedError {
    localsPosition += increment;
    parentIndex++;
    for (int i = 0; i < localVars.size(); i++) {
      JLocalVariable  localVar = (JLocalVariable) localVars.get(i);

      localVar.setPosition(localVar.getPosition()+increment);
      // fix 27.03.02 lackner
      // synthetic variable has no entry in CVariableInfo
      // not necessary think
      // localVar.setIndex(localVar.getIndex()+1);
    }

    fixChildBlockVariablePositions(increment);
  }


  /**
   * Fix the position of local variables of all child blocks of this block
   * as a monitor variable has been added to the stack.
   *
   * @param     incr     the update increment for local variables
   */
  private void fixChildBlockVariablePositions(int increment) throws UnpositionedError {
    if (childBlocks != null) {
      for (int i = 0; i < childBlocks.size(); i++) {
        CBlockContext        child = (CBlockContext)childBlocks.get(i);
        
        child.fixVariablePositions(increment);
      }
    }
  }

  /**
   * Adds the variable for the monitor of the synchronized statement to the
   * correct context.
   *
   * @param     var     monitor variable
   */
  public void addMonitorVariable(JLocalVariable var)  throws UnpositionedError {
    if (parent instanceof CMethodContext) {
      addVariableWithFix(var);
    } else {
      parent.addMonitorVariable(var);
    }
  }

  /**
   * Adds the variable for the monitor of the synchronized statement to the
   * correct context.
   *
   * @param     var     monitor variable
   */
  public void addVariableWithFix(JLocalVariable var)  throws UnpositionedError {
    int         increment = var.getType().getSize();

    addVariable(var);
    fixChildBlockVariablePositions(increment);
  }

  /**
   * lookupLocalVariable
   * @param	ident		the name of the variable
   * @return	a variable from an ident in current context
   */
  public JLocalVariable lookupLocalVariable(String ident) {
    if (localVars != null) {
      for (int i = 0; i < localsIndex; i++) {
	JLocalVariable	var = (JLocalVariable)localVars.get(i);

	if (var.getIdent() == ident) {
	  return var;
	}
      }
    }

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
    if (localVars != null && ident != null) {
      for (int i = 0; i < localsIndex; i++) {
	JLocalVariable	var = (JLocalVariable)localVars.get(i);

	if (var.getIdent() == ident) {
	  return false;
	}
      }
    }
    return parent.checkForLoop(ident);
  }  

  /**
   * addLocal variable
   * @param	var		the name of the variable
   * @param	initialized	is the varaible already initialized
   */
  public void addThisVariable() {
    localsPosition += 1;
  }

  public int localsPosition() {
    return localsPosition;
  }

  public int localsIndex() {
    return parentIndex + localsIndex;
  }

  public CBlockContext getBlockContext() {
    return this;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (TYPE DEFINITION)
  // ----------------------------------------------------------------------

  /**
   * addLocalClass
   * @param	clazz		the clazz to add
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void addClass(CClass clazz) throws UnpositionedError {
    if (localClasses == null) {
      localClasses = new Hashtable();
    }
    Object	old = localClasses.put(clazz.getIdent(), clazz);
    if (old != null) {
      throw new UnpositionedError(KjcMessages.CLAZZ_RENAME, clazz.getIdent());
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (LOOKUP)
  // ----------------------------------------------------------------------

  /**
   * lookupClass
   * search for a class with the provided type parameters
   * @param	caller		the class of the caller
   * @param	ident		the class name
   * @return	the class if found, null otherwise
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CClass lookupClass(CClass caller, String ident) throws UnpositionedError {
    // search local class first
    if (localClasses != null) {
      CClass	clazz;

      clazz = (CClass)localClasses.get(ident);
      if (clazz != null) {
        if (clazz.isAccessible(caller)) {
          return clazz;
        } else {
          throw new UnpositionedError(KjcMessages.CLASS_NOACCESS, clazz.getIdent());
        }
      }
    }

    return super.lookupClass(caller, ident);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Hashtable				localClasses;
  private ArrayList				localVars;
  private ArrayList				childBlocks;

  private /*final*/ int				parentIndex;
  private int					localsIndex;

  private int					localsPosition;
}
