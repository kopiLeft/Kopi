/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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
package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.CReferenceType;

/**
 * The type of a field in Database.k
 */
public interface XDatabaseMember {

  /**
   * Returns true if this field represents a table.
   *
   * @return true if it represents a table else false.
   */
  boolean isTable();
  /**
   * Returns true if this field represents a column.
   *
   * @return true if it represents a column else false.
   */
  boolean isColumn();

}
