/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.NotNullDate;

public class JdbcDateLiteral extends Literal {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	where		           the token reference for this statement
   * @param	value	                   the JDBC date
   */
  public JdbcDateLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    try {
      this.value = new NotNullDate(image);
    } catch (IllegalArgumentException e) {
      throw new PositionedError(where, SqlcMessages.INVALID_DATE_LITERAL, image);
    }
  }

  /**
   * Used to create default value.
   */
  private JdbcDateLiteral() {
    super(TokenReference.NO_REF);
    this.value = Date.now();
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
    if (otherLiteral instanceof JdbcDateLiteral) {
      return value.equals(((JdbcDateLiteral)otherLiteral).getValue());
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
    visitor.visitJdbcDateLiteral(this, value);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final JdbcDateLiteral	DEFAULT = new JdbcDateLiteral();

  private Date      value;
}
