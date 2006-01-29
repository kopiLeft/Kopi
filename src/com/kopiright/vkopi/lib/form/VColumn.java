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

package com.kopiright.vkopi.lib.form;

public class VColumn {

  /**
   * Constructor
   */
  public VColumn(int pos, String name, boolean key) {
    this.pos	= pos;
    this.name	= name;
    this.key	= key;
  }

  /**
   * Returns the position of the table in the array of tables
   * of the field's block
   */
  public int getTable() {
    return pos;
  }

  /**
   * Returns the name of the column.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the qualified name of the column (i.e. with correlation)
   */
  public String getQualifiedName() {
    return "T" + pos + "." + name;
  }

  /**
   * Returns wether ther column is a key 
   */
  public boolean isKey() {
    return key;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private int		pos;		// position of associated table
  private String	name;		// column name
  private boolean	key;		// column key of table ?
}
