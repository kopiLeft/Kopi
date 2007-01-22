/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.xkopi.lib.base;

import java.sql.SQLException;

/**
 * This exception is thrown when the system try to access a value in the
 * database and the type is bad or the serialisation process failed.
 */
public class DBInvalidDataException extends DBException {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  

/**
   * Constructor
   *
   * @param	original		the original SQLException
   */
  public DBInvalidDataException(Exception original) {
    super(new SQLException(original.getMessage()));
  }

  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   */
  public DBInvalidDataException(String query, Exception original) {
    super(query, new SQLException(original.getMessage()));
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3750935745427151198L;
}
