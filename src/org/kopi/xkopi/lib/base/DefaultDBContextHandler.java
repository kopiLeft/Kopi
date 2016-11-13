/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.lib.base;

import java.sql.SQLException;

public class DefaultDBContextHandler implements DBContextHandler {

  public DefaultDBContextHandler() {
  }

  public DefaultDBContextHandler(DBContext context) {
    this.context = context;
  }

  /**
   * Sets the database context for this object.
   *
   * @param	context		a database context
   */
  public void setDBContext(DBContext context) {
    this.context = context;
  }

  /**
   * Returns the database context for this object.
   */
  public DBContext getDBContext() {
    return context;
  }

  /**
   * Starts a protected transaction.
   *
   * @param message  the message to be displayed
   */
  public void startProtected(String message) {
    isProtected = true;

    context.startWork();
  }


  /**
   * Commits a protected transaction.
   */
  public void commitProtected() {
    try {
      context.commitWork();
    } catch (SQLException e) {
      // !!! ignore it: are we sure that it is really committed ???
    }

    isProtected = false;
  }


  /**
   * Aborts a protected transaction.
   *
   * @param	interrupt       interrupt DB connection if true
   */
  public void abortProtected(boolean interrupt) {
    try {
      context.abortWork();
    } catch (SQLException e) {
      //!!! ignore it: are we sure that it is really aborted ???
    }

    isProtected = false;
  }

  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  public boolean retryableAbort(Exception reason) {
    return false;
  }

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  public boolean retryProtected() {
    return false;
  }

  /**
   * Returns whether this object handles a transaction at this time.
   */
  public boolean inTransaction() {
    return isProtected;
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private DBContext             context;
  private boolean		isProtected;
}
