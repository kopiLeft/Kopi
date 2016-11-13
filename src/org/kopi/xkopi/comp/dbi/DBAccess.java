/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.xkopi.comp.dbi;

import java.sql.SQLException;
import org.kopi.xkopi.lib.base.DBContext;

public interface DBAccess {

  // ----------------------------------------------------------------------
  // ACCESSORS (DBCONTEXT)
  // ----------------------------------------------------------------------

  /**
   * Returns the current DBContext
   */
  DBContext getDBContext();

  /**
   * Sets the DBContext
   */
  void setDBContext(DBContext ctxt);

  // ----------------------------------------------------------------------
  // ACCESSORS (DATABASE)
  // ----------------------------------------------------------------------

  /**
   * Returns a compilation unit from the current database
   */
  SCompilationUnit getCompilationUnit() throws SQLException;

  // ----------------------------------------------------------------------
  // TRANSFORMERS
  // ----------------------------------------------------------------------

  /**
   * Adds an index to the database.
   * @param	indexName		the name of the index to bs added
   * @param	hasUnique		The index is unique ?
   * @param	tableName		the name of the table
   * @param	type			the type of the index
   */
  int addIndex(String packageName, String indexName, boolean hasUnique, String tableName, byte type) throws SQLException;

  /**
   * Adds an index column to the database.
   */
  void addIndexColumn(int index, String packageName, String tableName, String columnName, byte position, boolean isDesc) throws SQLException;

  /**
   * Adds a table to the database.
   * @param	tableName		the name of table to be added
   */
  int addTable(String tableName, String packageName) throws SQLException;

  /**
   * Adds a column to the database.
   */
  int addColumn(int table, String columnName, boolean nullable, byte type, byte key, byte position) throws SQLException;

 /**
   * Adds a script in the datadict
   * @param	pack		the package name
   * @param	version		the version number
   * @param	script		the index to be added
   * @param	comment		a comment on this script
   * @param	developer	who made it
   */
  void addScript(String pack,
		 int version,
		 String script,
		 String comment,
		 String developer) throws SQLException;

  /**
   * Adds an index to the database.
   * @param	index		the index to be added
   */
  void addIntType(int column, int min, int max) throws SQLException;

 /**
   * Adds a short type to the database.
   */
  void addShortType(int column, short min, short max) throws SQLException;

 /**
   * Adds a byte type to the database.
   */
  void addByteType(int column, byte min, byte max) throws SQLException;

 /**
   * Adds a image type to the database.
   */
  void addImageType(int column, int width, int height) throws SQLException;

 /**
   * Adds a string type to the database.
   */
  void addStringType(int column, boolean fixed, int width, int height, byte convert) throws SQLException;

 /**
   * Adds a text type to the database.
   */
  void addTextType(int column, int width, int height) throws SQLException;

  /**
   * Adds a fixed type to the database.
   */
  void addFixedType(int column, int precision, int scale, int min, int max) throws SQLException;

  /**
   * Drops a view from the database.
   * @param	tableName		the name of the view to drop
   */
  void dropView(String packageName, String viewName) throws SQLException;

  /**
   * Drops a table from the database.
   * @param	tableName		the name of table to drop
   */
  void dropTable(String packageName, String tableName) throws SQLException;
}
