/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

/**
 * This class provides the contextual information for the semantic
 * analysis loop statements.
 */
public class CLoopContext extends CBodyContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs the context to analyse a loop statement semantically.
   * @param	parent		the parent context
   * @param	stmt		the loop statement
   */
  CLoopContext(CBodyContext parent, KjcEnvironment environment,JLoopStatement stmt) {
    super(parent, environment);

    this.stmt = stmt;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the innermost statement which can be target of a break
   * statement without label.
   */
  public JStatement getNearestBreakableStatement() {
    return stmt;
  }

  /**
   * Returns the innermost statement which can be target of a continue
   * statement without label.
   */
  public JStatement getNearestContinuableStatement() {
    return stmt;
  }

  /**
   *
   */
  protected void addBreak(JStatement target,
			  CBodyContext context)
  {
    if (stmt == target) {
      if (breakContextSummary == null) {
	breakContextSummary = context.cloneContext();
      } else {
	breakContextSummary.merge(context);
      }
      breakContextSummary.setReachable(true);
    } else {
      ((CBodyContext)getParentContext()).addBreak(target, context);
    }
  }

  /**
   *
   */
  protected void addContinue(JStatement target,
			     CBodyContext context)
  {
    if (stmt == target) {
      if (continueContextSummary == null) {
	continueContextSummary = context.cloneContext();
      } else {
	continueContextSummary.merge(context);
      }
    } else {
      ((CBodyContext)getParentContext()).addContinue(target, context);
    }
  }

  /**
   * Checks whether this statement is target of a break statement.
   *
   * @return	true iff this statement is target of a break statement.
   */
  public boolean isBreakTarget() {
    return breakContextSummary != null;
  }

  /**
   * Returns the context state after break statements.
   */
  public CBodyContext getBreakContextSummary() {
    return breakContextSummary;
  }

  /**
   * Checks whether this statement is target of a continue statement.
   *
   * @return	true iff this statement is target of a continue statement.
   */
  public boolean isContinueTarget() {
    return continueContextSummary != null;
  }

  /**
   * Returns the context state after continue statements.
   */
  public CBodyContext getContinueContextSummary() {
    return continueContextSummary;
  }

  /**
   * used to check, if a final variable/field is assigned
   * in a loop.
   * returns true.
   * @param     ident name of the variable of null if it is a field
   */
  public boolean checkForLoop(String ident) {
    return true;
  }  

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final JLoopStatement		stmt;
  private CBodyContext			breakContextSummary;
  private CBodyContext			continueContextSummary;
}
