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
 * $Id: DatabaseMember.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */
package at.dms.xkopi.comp.database;

import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.xkopi.comp.xkjc.XDatabaseMember;

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
