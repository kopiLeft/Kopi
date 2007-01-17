/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.dbi;

import java.sql.SQLException;

import com.kopiright.xkopi.comp.database.DatabaseColumn;

/**
 * This class represents a DbiType Interface
 */
public interface DbiType extends Constants {

  // ----------------------------------------------------------------------
  //
  // ----------------------------------------------------------------------

  /**
   * Returns the code for an insert in the DictColumns
   */
  byte getCode();

  /**
   * Insert the type of a column in the data dictionary
   */
  void makeDBSchema(DBAccess a, int column) throws SQLException;

  // ----------------------------------------------------------------------
  // methods used to create Database.k
  // ----------------------------------------------------------------------

  /**
   * Returns the type for the type-checking mechanism of dbi. This type
   * is used as type of the field in the Database.k class. 
   *
   * @return the type
   */
  DatabaseColumn getColumnInfo(boolean isNullable);
}
