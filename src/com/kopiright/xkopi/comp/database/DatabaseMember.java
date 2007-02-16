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
package com.kopiright.xkopi.comp.database;

import com.kopiright.kopi.comp.kjc.CReferenceType;
import com.kopiright.xkopi.comp.xkjc.XDatabaseMember;

/**
 * The type of a field in Database.k
 */
public abstract class DatabaseMember implements XDatabaseMember{

  /**
   * Checks whether this field represents a table.
   *
   * @return    true iff this field represents a table
   */
  public  boolean isTable() {
    return false;
  }
  /**
   * Checks whether this field represents a column.
   *
   * @return    true iff this field represents a column
   */
  public  boolean isColumn() {
    return false;
  }

  public static final CReferenceType TYPE   = 
    CReferenceType.lookup(DatabaseMember.class.getName().replace('.','/'));
}
