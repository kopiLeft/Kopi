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

public class XUtils extends com.kopiright.util.base.Utils {

  // ----------------------------------------------------------------------
  // INITIALIZER
  // ----------------------------------------------------------------------

  /**
   *
   */
  public static void setChecker(DBChecker checker) {
    XUtils.checker = checker;
  }

  // ----------------------------------------------------------------------
  // DATABASE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Returns the database type of a Table/column
   */
  public static boolean tableExists(String table) {
    return checker == null ? true : checker.tableExists(table);
  }

  /**
   * Returns the database type of a Table/column
   */
  public static boolean columnExists(String table, String column) {
    return checker == null ? true : checker.columnExists(table, column);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private static DBChecker      checker;
}
