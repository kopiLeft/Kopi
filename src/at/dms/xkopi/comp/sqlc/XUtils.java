/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: XUtils.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

public class XUtils extends at.dms.util.base.Utils {

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

  private static DBChecker	checker;
}
