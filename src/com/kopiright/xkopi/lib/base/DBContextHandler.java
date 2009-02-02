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

/**
 * Interface for object that executes transactions and query to databases.
 */
public interface DBContextHandler {

  /**
   * Sets the database context for this object
   *
   * @param	context		a database context
   */
  void setDBContext(DBContext context);

  /**
   * Returns the database context for this object.
   */
  DBContext getDBContext();

  /**
   * Starts a protected transaction.
   *
   * @param	message		the message to be displayed
   */
  void startProtected(String message);

  /**
   * Commits a protected transaction.
   */
  void commitProtected() throws SQLException;

  /**
   * Aborts a protected transaction.
   *
   * @param	interrupt       should interrupt the connection if true
   */
  void abortProtected(boolean interrupt);

  /**
   * Returns true if the exception allows a retry of the 
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  boolean retryableAbort(Exception reason);

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  boolean retryProtected();

  /**
   * Returns whether this object handles a transaction at this time.
   */
  boolean inTransaction();
}
