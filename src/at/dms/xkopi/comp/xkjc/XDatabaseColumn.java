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
 * $Id: XDatabaseColumn.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */
package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CType;


/**
 * The type of a field in Database.k
 */
public interface XDatabaseColumn extends XDatabaseMember {

  int NULL_CHECK_NONE            = 0x3; /*WIDENING  and NARROWING*/ 
  int NULL_CHECK_WIDENING        = 0x1; /* select */
  int NULL_CHECK_NARROWING       = 0x2; /* insert */

  int TYPE_CHECK_NONE            = 0x3<<2; /*WIDENING  and NARROWING*/ 
  int TYPE_CHECK_WIDENING        = 0x1<<2;  
  int TYPE_CHECK_NARROWING       = 0x2<<2; 

  int CONSTRAINT_CHECK_NONE      = 0x1<<4; 

  /**
   * Returns true if other (e.g. from formular) is equivalent with the current 
   * column (e.g. from database). 
   *
   * @param other other column 
   * @param ckeck spezifies the check
   */
  boolean isEquivalentTo(XDatabaseColumn other, int ckeck);

  /**
   * Returns true if empty entries are allowed in this column.
   *
   * @return    true iff empty entries allowed
   */
  boolean isNullable();

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  CType getStandardType();
}
