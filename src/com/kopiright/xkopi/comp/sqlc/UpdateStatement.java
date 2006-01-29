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

package com.kopiright.xkopi.comp.sqlc;

import java.util.ArrayList;
import com.kopiright.kopi.comp.kjc.CTypeContext;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class UpdateStatement extends Statement implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   * @param	table
   * @param	listIdent
   */
  public UpdateStatement(TokenReference ref, TableName table, ArrayList listIdent) {
    super(ref);
    this.table = table;
    this.listIdent = listIdent;
    this.searchCondition = null;
    this.hasCondition = false;
  }

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   * @param	table
   * @param	listIdent
   * @param	searchCondition
   */
  public UpdateStatement(TokenReference ref,
			 TableName table,
			 ArrayList listIdent,
			 SearchCondition searchCondition)
  {
    super(ref);
    this.table = table;
    this.listIdent = listIdent;
    this.searchCondition = searchCondition;
    this.hasCondition = true;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor) throws PositionedError {
    parent = visitor.getContext();
    visitor.enter(this);
    visitor.visitUpdateStatement(this, this.table, listIdent, searchCondition, hasCondition);
    visitor.exit(this);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------


  /**
   * Returns the table reference with alias "alias"
   */
  public TableReference getTableFromAlias(String alias) {
    return alias.equals(table.getAlias()) ? table : null;
  }

  /**
   * Returns all tables defined in current context
   */
  public ArrayList getTables() {
    ArrayList	tables = new ArrayList();

    tables.add(table);
    return tables;
  }

  /**
   * Returns the parent context
   */
  public SqlContext getParentContext() {
    return parent;
  }

  /**
   * Add an error into the list and eat it
   * This method should be called after a try catch block after catching exception
   * or directly without exception thrown
   * @param	error		the error
   */
  public void reportTrouble(PositionedError trouble) {
    parent.reportTrouble(trouble);
  }

  /**
   * Returns the type context
   */
  public CTypeContext getTypeContext() {
    return parent.getTypeContext();
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private SqlContext		parent;

  private TableName		table;
  private ArrayList		listIdent;
  private SearchCondition	searchCondition;
  // WHERE CURRENT if hasCondition and searchCondition == null
  private boolean		hasCondition;
}
