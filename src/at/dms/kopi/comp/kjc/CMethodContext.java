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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents a method context during check
 * @see CContext
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CConstructorContext
 * @see CInitializerContext
 * @see CBodyContext
 * @see CBlockContext
 */
public class CMethodContext extends CContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * CMethodContext
   * @param	parent		the parent context
   * @param	self		the corresponding method interface
   */
  CMethodContext(CClassContext parent, KjcEnvironment environment, CMethod self, JFormalParameter[] parameters) {
    super(parent, environment);
    this.self = self;
    this.parameters = parameters;
  }

  /**
   * Verify that all checked exceptions are defined in the throw list
   * and return types are valid
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public void close(TokenReference ref) throws PositionedError {
    CReferenceType[]	checked = self.getThrowables();
    boolean[]		used = new boolean[checked.length];

  loop:
    for (Enumeration elems = throwables.elements(); elems.hasMoreElements(); ) {
      CThrowableInfo	thrown = (CThrowableInfo)elems.nextElement();
      CReferenceType	type = thrown.getThrowable();

      // only checked exceptions need to be checked
      if (! type.isCheckedException(this)) {
	continue loop;
      }
      for (int j = 0; j < checked.length; j++) {
	if (type.isAssignableTo(this, checked[j])) {
	  used[j] = true;
	  continue loop;
	}
      }
      throw new PositionedError(thrown.getLocation().getTokenReference(),
				KjcMessages.METHOD_UNCATCHED_EXCEPTION,
				type,
				null);
    }

    for (int i = 0; i < checked.length; i++) {
      if (!checked[i].isCheckedException(this)) {
	reportTrouble(new CWarning(ref,
				   KjcMessages.METHOD_UNCHECKED_EXCEPTION,
				   checked[i],
				   null));
      } else if (!used[i]) {
	reportTrouble(new CWarning(ref,
				   KjcMessages.METHOD_UNTHROWN_EXCEPTION,
				   checked[i],
				   null));
      }
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
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
    if (self.isStatic()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * getClassContext
   * @return	the near parent of type CClassContext
   */
  public CClassContext getClassContext() {
    return getParentContext().getClassContext();
  }

  /**
   * getCMethod
   * @return	the near parent of type CMethodContext
   */
  public CMethod getCMethod() {
    return self;
  }

  /**
   * getMethod
   * @return	the near parent of type CMethodContext
   */
  public CMethodContext getMethodContext() {
    return this;
  }

  public int localsPosition() {
    return 0;
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
    CTypeVariable       tv = getCMethod().lookupTypeVariable(ident);

    if (tv != null) {
      return tv;
    } else {
      if (isStaticContext()) {
        return null;
      } else {
        return getClassContext().lookupTypeVariable(ident);
      }
    }
  }
  // ----------------------------------------------------------------------
  // ACCESSORS for kopi extensions
  // ----------------------------------------------------------------------

  public JFormalParameter[] getFormalParameter() {
    return parameters;
  }

  public int getNextStoreFieldIndex() {
    return storeFieldIndex++;
  }

  public void addStoreField(JFieldDeclaration field) {
    if (storeFields == null) {
      storeFields = new ArrayList(5);
    }
    storeFields.add(field);
  }


  /**
   * Returns an array of the store fields
   *
   */
  public JFieldDeclaration[] getStoreFields() {
    if (storeFields == null) {
      return JFieldDeclaration.EMPTY;
    } else {
      return (JFieldDeclaration[])storeFields.toArray(new JFieldDeclaration[storeFields.size()]);
    }
  }

  // ----------------------------------------------------------------------
  // THROWABLES
  // ----------------------------------------------------------------------

  /**
   * @param	throwable	the type of the new throwable
   */
  public void addThrowable(CThrowableInfo throwable) {
    throwables.put(throwable.getThrowable().toString(), throwable);
  }

  /**
   * @return the list of exception that may be thrown
   */
  public Hashtable getThrowables() {
    return throwables;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private	CMethod                 self;
  private final JFormalParameter[]      parameters;
  private       int                     storeFieldIndex = 0;
  private       ArrayList               storeFields = null;

  protected	Hashtable               throwables = new Hashtable();
  protected	Hashtable               labels;		// Hashtable<String, String>
}
