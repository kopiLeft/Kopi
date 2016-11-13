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

package org.kopi.xkopi.comp.sqlc;

import java.util.ArrayList;

import org.kopi.kopi.comp.kjc.CTypeContext;
import org.kopi.compiler.base.Compiler;
import org.kopi.compiler.base.PositionedError;

/**
 * This class represents a local context during checkSql
 */
public class SCompilationUnitContext implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a compilation unit context.
   */
  public SCompilationUnitContext(Compiler compiler) {
    this.compiler = compiler;
  }

  /**
   * Returns the table reference with alias "alias"
   */
  public TableReference getTableFromAlias(String alias) {
    return null;
  }

  /**
   * Returns all tables defined in current context
   */
  public ArrayList getTables() {
    return null;
  }

  /**
   * Returns the parent context
   */
  public SqlContext getParentContext() {
    return null;
  }

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
   */
  public void reportTrouble(PositionedError trouble) {
    compiler.reportTrouble(trouble);
  }

  /**
   * Returns the type context
   */
  public CTypeContext getTypeContext() {
    return null;
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Compiler		compiler;
}
