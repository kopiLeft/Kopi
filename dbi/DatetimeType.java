/*
 * Copyright (C) 1990-99 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: DatetimeType.java,v 1.8 1999/08/18 12:32:53 graf Exp $
 */

package at.dms.dbi;

import at.dms.util.TokenReference;
import at.dms.util.UnpositionedError;
import at.dms.util.PositionedError;
import at.dms.kjc.JExpression;
import at.dms.sqlc.Type;
import at.dms.sqlc.SqlContext;

public class DatetimeType extends Type {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	left
   * @param	right
   */
  public DatetimeType(TokenReference ref, String left, String right) {
    super(ref);
    this.left = left;
    this.right = right;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getLeft
   */
  public String getLeft() {
    return left;
  }

  /**
   * getRight
   */
  public String getRight() {
    return right;
  }

  /**
   * return true if it is a month
   */
  public boolean isMonth() {
    return (left.equals("YY") && right.equals("MO"));
  }

  /**
   * return true if it is a time
   */
  public boolean isTime() {
    return (left.equals("HH") && (right.equals("SS") || right.equals("MI")));
  }

  // ----------------------------------------------------------------------
  // FUNCTION
  // ----------------------------------------------------------------------

  /**
   * equals
   */
  public boolean equals(Object o) {
    if (o instanceof DatetimeType) {
      return (left.equals(((DatetimeType)o).getLeft()) &&
	      right.equals(((DatetimeType)o).getRight()));
    } else {
      return false;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * This is the backend of our compiler, we expect to generate byte code here
   * @param p the buffered printwriter on which we write code
   */
  public void genSQLCode(SQLPrettyPrinter p) {
    p.printDateTime(left, right);
  }

  /**
   * Generate the code in pure java form
   * This is the backend of our compiler, we expect to generate byte code here
   * @param p the buffered printwriter on which we write code
   */
  public void genSQLCode(at.dms.sqlc.SQLPrettyPrinter p) {
    genSQLCode((SQLPrettyPrinter)p);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * Generates code.
   * @param	context		the sql context that holds the kjc Context
   * @param	left		the SQL expression to be built
   * @param	current		the current string literal to be added to left
   */
  public JExpression checkSQL(SqlContext context, JExpression left, StringBuffer current) {
    current.append("DATETIME[");
    current.append(this.left);
    current.append(":");
    current.append(right);
    current.append("]");
    return left;
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

  private String	left;
  private String	right;
}
