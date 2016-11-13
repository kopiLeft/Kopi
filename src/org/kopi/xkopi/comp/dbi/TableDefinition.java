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

package org.kopi.xkopi.comp.dbi;

import java.util.ArrayList;
import java.sql.SQLException;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CTypeContext;
import org.kopi.xkopi.comp.sqlc.Expression;
import org.kopi.xkopi.comp.sqlc.ExpressionList;
import org.kopi.xkopi.comp.sqlc.InsertStatement;
import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.StringLiteral;
import org.kopi.xkopi.comp.sqlc.TableReference;
import org.kopi.xkopi.comp.sqlc.ValueList;
import org.kopi.xkopi.comp.sqlc.ValueListInsertSource;

/**
 * This class represents a table definition
 */
public class TableDefinition extends DbiStatement implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param     ref             the token reference for this clause
   * @param     tableName       the name of the table
   * @param     columns         the vector of columns
   * @param     key             the primary key of the table
   */
  public TableDefinition(TokenReference ref,
                         Expression tableName,
                         ArrayList columns,
                         Key key)
  {
    this(ref, tableName, columns, key, null);
  }

  /**
   * Constructor
   * @param     ref             the token reference for this clause
   * @param     tableName       the name of the table
   * @param     columns         the vector of columns
   * @param     key             the primary key of the table
   * @param     pragma          the pragma
   */
  public TableDefinition(TokenReference ref,
                         Expression tableName,
                         ArrayList columns,
                         Key key,
                         Pragma pragma)
  {
    super(ref);

    this.tableName = tableName;
    this.columns = columns;
    this.key = key;
    this.pragma = pragma;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getTableName
   */
  public Expression getTableName() {
    return tableName;
  }

  /**
   * getColumns
   */
  public ArrayList getColumns() {
    return columns;
  }

  /**
   *
   */
  public Key getKey() {
    return key;
  }

  /**
   * getForeignKey
   */
  public ArrayList getForeignKey() {
    return foreignKeys;
  }
 
  /**
   *
   */
  public Pragma getPragma() {
    return pragma;
  }
  
  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, String packageName) throws SQLException {
    int         table;

    table = a.addTable(((SimpleIdentExpression)this.getTableName()).getIdent(),
                       packageName);
    
    for (int i = 0; i < columns.size(); i++) {
      ((Column)columns.get(i)).makeDBSchema(a, table, (byte)i, key.getKeys());
    }
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean compareTo(TableDefinition otherTable) {
    // the expression tableName
    Expression          otherTableName;
    
    otherTableName = otherTable.getTableName();
    if (tableName instanceof SimpleIdentExpression
        && otherTableName instanceof SimpleIdentExpression) {
      if (((SimpleIdentExpression)tableName).compareTo((SimpleIdentExpression)otherTableName)) {
        // the vector of columns
        ArrayList       otherColumns;
        
        otherColumns = otherTable.getColumns();
        if (columns.size() == otherColumns.size()) {
          for (int i = 0; i < columns.size(); i++) {
            if ( !((Column)columns.get(i)).compareTo((Column)otherColumns.get(i))) {
              DbCheck.addError(new PositionedError(getTokenReference(),
                                                   DbiMessages.COLUMN_IN_TABLE_NOT_CORRECT,
                                                   ((Column)columns.get(i)).getIdent(),
                                                   ((SimpleIdentExpression)tableName).getIdent()));
              return false;
            }
          }
          // the key
          if (key != null && otherTable.getKey() != null) {
            return key.compareTo(otherTable.getKey());
          } else if (key == null && otherTable.getKey() == null) {
            return true;
          }
          DbCheck.addError(new PositionedError(getTokenReference(),
                                               DbiMessages.KEY_IN_TABLE_NOT_CORRECT,
                                               ((SimpleIdentExpression)tableName).getIdent()));
          return false;
        } else {
          DbCheck.addError(new PositionedError(getTokenReference(),
                                               DbiMessages.TOO_MUCH_COLUMNS_IN_TABLE,
                                               ((SimpleIdentExpression)tableName).getIdent()));
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * addForeignKey
   */
  public void addForeignKey(String foreignKeyName,
                            String tableReference,
                            String type)
  {
    ArrayList   values = new ArrayList();

    values.add(new StringLiteral(getTokenReference(),
                                 "'" + ((SimpleIdentExpression)tableName).getIdent() + "'"));
    values.add(new StringLiteral(getTokenReference(), "'" + foreignKeyName + "'"));
    values.add(new StringLiteral(getTokenReference(), "'" + tableReference + "'"));
    values.add(new StringLiteral(getTokenReference(), "'" + type + "'"));
    
    if (foreignKeys == null) {
      foreignKeys = new ArrayList();
    }

    foreignKeys.add(new InsertStatement(getTokenReference(),
                                        "REFERENZEN",
                                        null,
                                        new ValueListInsertSource(getTokenReference(),
                                                                  new ValueList(getTokenReference(),
                                                                                new ExpressionList(getTokenReference(),
                                                                                                   values)))));
  }
  
  
  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param     visitor                 the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    parent = visitor.getContext();
    visitor.enter(this);
    visitor.visitTableDefinition(this, tableName, columns, key, pragma);
    visitor.exit(this);
  }

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
   * @param     error           the error
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

  private SqlContext    parent;

  Expression            tableName;
  ArrayList             columns;
  ArrayList             foreignKeys;
  Key                   key;
  Pragma                pragma;
}
