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

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.xkopi.lib.type.NotNullTimestamp;
import com.kopiright.xkopi.lib.type.Timestamp;

public class JdbcTimeStampLiteral extends Literal {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	where		the token reference for this statement
   * @param	image		the JDBC time stamp
   */
  public JdbcTimeStampLiteral(TokenReference where, String image)
    throws PositionedError
  {
    super(where);
    try {
      this.value = new NotNullTimestamp(image);
    } catch (IllegalArgumentException e) {
      throw new PositionedError(where, SqlcMessages.INVALID_TIMESTAMP_LITERAL, image);
    }
  }

  /**
   * Used to create default value.
   */
  private JdbcTimeStampLiteral() {
    super(TokenReference.NO_REF);
    this.value = Timestamp.now();
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
    if (otherLiteral instanceof JdbcTimeStampLiteral) {
      return value.equals(((JdbcTimeStampLiteral)otherLiteral).getValue());
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
    visitor.visitJdbcTimeStampLiteral(this, value);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final JdbcTimeStampLiteral	DEFAULT = new JdbcTimeStampLiteral();

  private final Timestamp      value;
}
