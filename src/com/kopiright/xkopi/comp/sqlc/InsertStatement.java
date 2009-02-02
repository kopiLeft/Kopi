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

package com.kopiright.xkopi.comp.sqlc;

import java.util.ArrayList;
import com.kopiright.kopi.comp.kjc.CTypeContext;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents an insert statement
 */
public class InsertStatement extends Statement implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	ident		type ident of the table
   * @param	fields		the field name list
   * @param	soure		the source of data from clause
   */
  public InsertStatement(TokenReference ref,
			 String ident,
			 FieldNameList fields,
			 InsertSource source) {
    super(ref);
    this.ident = ident;
    this.fields = fields;
    this.source = source;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getIdent
   */
  public String getIdent() {
    return ident;
  }

  /**
   * getFields
   */
  public FieldNameList getFields() {
    return fields;
  }

  /**
   * getSource
   */
  public InsertSource getSource() {
    return source;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean compareTo(InsertStatement otherInsert) {
    if (ident.equals(otherInsert.getIdent())) {
      if (fields != null && otherInsert.getFields() != null) {
	if (fields.equals(otherInsert.getFields())) {
	  if (source.equals(otherInsert.getSource())) {
	    return true;
	  } else {
	    System.err.println("The values inserted into the table " + otherInsert.getIdent() + " are not correct");
	    return false;
	  }
	}
      } else if (fields == null && otherInsert.getFields() == null) {
	if (source.equals(otherInsert.getSource())) {
	  return true;
	} else {
	  System.err.println("The values inserted into the table " + otherInsert.getIdent() + " are not correct");
	  return false;
	}
      } else {
	System.err.println("The fields into the table " + otherInsert.getIdent() + " is not correct");
	return false;
      }
    }
    System.err.println("The insert into the table " + otherInsert.getIdent() + " is wrong");
    return false;
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
    visitor.visitInsertStatement(this, ident, fields, source);
    visitor.exit(this);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * Returns the table reference with alias "alias"
   */
  public TableReference getTableFromAlias(String alias) {
      return new TableName(getTokenReference(),
			   new SimpleIdentExpression(getTokenReference(), ident),
			   new TableAlias(getTokenReference(), null, null));
  }

  /**
   * Returns all tables defined in current context
   */
  public ArrayList getTables() {
    ArrayList tables = new ArrayList();
    TableReference ref = new TableName(getTokenReference(),
				       new SimpleIdentExpression(getTokenReference(), ident),
				       new TableAlias(getTokenReference(), null, null));
    tables.add(ref);
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

  private String	ident;
  private FieldNameList	fields;
  private InsertSource	source;
}
