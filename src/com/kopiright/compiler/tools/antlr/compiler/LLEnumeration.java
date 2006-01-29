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

package com.kopiright.compiler.tools.antlr.compiler;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An enumeration of a LList.  Maintains a cursor through the list.
 * bad things would happen if the list changed via another thread
 * while we were walking this list.
 */
final class LLEnumeration implements Enumeration {
  LLCell cursor;
  LList list;


  /**
   * Create an enumeration attached to a LList
   */
  public LLEnumeration(LList l) {list = l; cursor=list.head;}
  /**
   * Return true/false depending on whether there are more
   * elements to enumerate.
   */
  public boolean hasMoreElements() {
    if ( cursor!=null ) {
	return true;
    } else {
	return false;
    }
  }
  /**
   * Get the next element in the enumeration.  Destructive in that
   * the returned element is removed from the enumeration.  This
   * does not affect the list itself.
   * @return the next object in the enumeration.
   */
  public Object nextElement() {
    if ( !hasMoreElements() ) {
	throw new NoSuchElementException();
    }
    LLCell p = cursor;
    cursor = cursor.next;
    return p.data;
  }
}
