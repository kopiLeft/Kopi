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

package at.dms.xkopi.comp.sqlc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.xkopi.lib.type.Time;
import at.dms.xkopi.lib.type.NotNullTime;

public class JdbcTimeLiteral extends Literal {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	where		           the token reference for this statement
   * @param	value	                   the JDBC time
   */
  public JdbcTimeLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    try {
      this.value = new NotNullTime(image);
    } catch (IllegalArgumentException e) {
      throw new PositionedError(where, SqlcMessages.INVALID_TIME_LITERAL, image);
    }
  }

  /**
   * Used to create default value.
   */
  private JdbcTimeLiteral() {
    super(TokenReference.NO_REF);
    this.value = Time.now();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of this literal (in Java)
   */
  public Object getValue() {
    return value;
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean equals(Object otherLiteral) {
    if (otherLiteral instanceof JdbcTimeLiteral) {
      return value.equals(((JdbcTimeLiteral)otherLiteral).getValue());
    }
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
    visitor.visitJdbcTimeLiteral(this, value);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final JdbcTimeLiteral	DEFAULT = new JdbcTimeLiteral();

  private Time      value;
}
