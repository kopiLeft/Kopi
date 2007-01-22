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

public class DBRuntimeException extends RuntimeException {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  
/**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public DBRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param     query           the sql query which generated the exception
   * @param     message         the associated message
   */
  public DBRuntimeException(String query, String message) {
    super(message + ((query != null) ? 
                     "\n---- BEGIN QUERY TRACE ----\n" + query + "\n----  END QUERY TRACE   ----"
                     : ""));
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public DBRuntimeException(SQLException exc) {
    super(exc);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param	message		the associated message
   */
  public DBRuntimeException(String message, SQLException exc) {
    super(message, exc);
  }

  /**
   * Constructs an exception with a message.
   *
   * @param     query           the sql query which generated the exception
   * @param     message         the associated message
   */
  public DBRuntimeException(String query, String message, SQLException exc) {
    super(message + ((query != null) ?
                     "\n---- BEGIN QUERY TRACE ----\n" + query + "\n----  END QUERY TRACE   ----"
                     : ""),
          exc);
  }
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -799443900280710097L;

}
