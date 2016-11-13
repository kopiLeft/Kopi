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

package org.kopi.xkopi.comp.xkjc;

import java.util.ArrayList;

import org.kopi.compiler.base.PositionedError;
import org.kopi.kopi.comp.kjc.CBodyContext;
import org.kopi.kopi.comp.kjc.CTypeContext;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.xkopi.comp.sqlc.SqlContext;
import org.kopi.xkopi.comp.sqlc.TableReference;

/**
 * This class represents a java expression within a Sql context
 */
public class JavaSqlContext implements SqlContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public JavaSqlContext(CBodyContext context) {
    this.context = context;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the java body context
   */
  public CBodyContext getBodyContext() {
    return context;
  }

  /**
   * Adds a blob to the current request
   */
  public void addBlob(Object o) {
    if (blobs == null) {
      blobs = new ArrayList();
    }
    blobs.add(o);
  }

  /**
   * Returns the blobs of the statement
   */
  public JExpression[] getBlobs() {
    return blobs == null ? 
      JExpression.EMPTY : 
      (JExpression[])blobs.toArray(new JExpression[blobs.size()]); //org.kopi.util.base.Utils.toArray(blobs, JExpression.class);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF Sql CONTEXT
  // ----------------------------------------------------------------------

  /**
   * Returns the table reference with alias "alias"
   */
  public TableReference getTableFromAlias(String alias) {
    //throw new InconsistencyException();
    return null;
  }

  /**
   * Returns all tables defined in current context
   */
  public ArrayList getTables() {
    return new ArrayList(); // !!!
  }

  /**
   * Returns the parent context
   */
  public SqlContext getParentContext() {
    return null;
  }

  /**
   * Add an error into the list and eat it
   * This method should be called after a try catch block after catching exception
   * or directly without exception thrown
   * @param	error		the error
   */
  public void reportTrouble(PositionedError trouble) {
    context.reportTrouble(trouble);
  }

  public CTypeContext getTypeContext() {
    return context;
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CBodyContext		context;
  private ArrayList		blobs;
}
