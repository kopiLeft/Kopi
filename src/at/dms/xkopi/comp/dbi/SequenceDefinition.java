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
 * $Id: TableDefinition.java 4203 2004-07-28 18:43:29Z imad $
 */

package at.dms.xkopi.comp.dbi;

import java.util.ArrayList;
import java.sql.SQLException;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.kopi.comp.kjc.CTypeContext;
import at.dms.xkopi.comp.sqlc.Expression;
import at.dms.xkopi.comp.sqlc.ExpressionList;
import at.dms.xkopi.comp.sqlc.InsertStatement;
import at.dms.xkopi.comp.sqlc.SimpleIdentExpression;
import at.dms.xkopi.comp.sqlc.SqlContext;
import at.dms.xkopi.comp.sqlc.StringLiteral;
import at.dms.xkopi.comp.sqlc.TableReference;
import at.dms.xkopi.comp.sqlc.ValueList;
import at.dms.xkopi.comp.sqlc.ValueListInsertSource;

/**
 * This class represents a table definition
 */
public class SequenceDefinition extends DbiStatement implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param     sequenceName	the name of the table
   * @param	startvalue	the start value
   */
  public SequenceDefinition(TokenReference ref,
                            Expression sequenceName,
                            Integer startValue)
  {
    super(ref);
    this.sequenceName = sequenceName;
    this.startValue = startValue;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, String packageName) {
    // nothing to do
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    parent = visitor.getContext();
    visitor.enter(this);
    visitor.visitSequenceDefinition(this, sequenceName, startValue);
    visitor.exit(this);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------
  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

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

  public CTypeContext getTypeContext() {
    return getTypeContext();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private SqlContext		parent;


  Expression			sequenceName;
  Integer                       startValue;
}
