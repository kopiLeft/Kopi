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
 * $Id: SelectExpression.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

import java.util.ArrayList;
import at.dms.kopi.comp.kjc.CTypeContext;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

/**
 * This class represents an sub select
 */
public class SelectExpression extends SubTable implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  public SelectExpression(TokenReference ref,
			  String type,
			  ArrayList columns,
			  FromClause tableRefs,
			  WhereClause whereClause,
			  GroupByClause groupByClause,
			  HavingClause havingClause)
  {
    super(ref);
    this.type = type;
    this.columns = columns;
    this.tableRefs = tableRefs;
    this.whereClause = whereClause;
    this.groupByClause = groupByClause;
    this.havingClause = havingClause;
 }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getColumnNumber
   */
  public int getColumnNumber() {
    return columns.size();
  }

  public ArrayList getColumns() {
    return columns;
  }

  public void setColumns(ArrayList columns) {
    this.columns = columns;
  }

  public FromClause getTableRefs() {
    return tableRefs;
  }

  public WhereClause getWhereClause() {
    return whereClause;
  }

  public GroupByClause getGroupbyClause() {
    return groupByClause;
  }

  public HavingClause getHavingClause() {
    return havingClause;
  }

  public String getType() {
    return type;
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
    visitor.visitSelectExpression(this, type, columns, tableRefs, whereClause, groupByClause, havingClause);
    visitor.exit(this);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * Returns the table reference with alias "alias"
   */
  public TableReference getTableFromAlias(String alias) {
    TableReference table = tableRefs.getTableFromAlias(alias);
    if (table != null) {
      return table;
    } else {
      return parent.getTableFromAlias(alias);
    }
  }

  /**
   * Returns all tables defined in current context
   */
  public ArrayList getTables() {
    return tableRefs.getTables();
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

  private String		type;
  private ArrayList		columns;
  private FromClause		tableRefs;
  private WhereClause		whereClause;
  private GroupByClause		groupByClause;
  private HavingClause		havingClause;
}
