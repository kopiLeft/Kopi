/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

public class DBDuplicateIndexException extends DBConstraintException {

  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3814852539241442219L;

/**
   * Constructor
   *
   * @param	original		the original SQLException
   * @param	constraintName		the violated constraint
   */
  public DBDuplicateIndexException(SQLException original, String indexName) {
    super(original, indexName);
  }

  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraintName          the violated constraint
   */
  public DBDuplicateIndexException(String query, SQLException original, String indexName) {
    super(query, original, indexName);
  }
}
