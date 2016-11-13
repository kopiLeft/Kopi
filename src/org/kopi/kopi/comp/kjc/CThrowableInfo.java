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

package org.kopi.kopi.comp.kjc;

/**
 * This class represents a throw <throwable> information during check
 */
public class CThrowableInfo extends org.kopi.util.base.Utils {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs an informztion handler
   * @param	throwable		the type of exception
   * @param	location		the throw statement
   */
  public CThrowableInfo(CReferenceType throwable, JPhylum location) {
    this.throwable = throwable;
    this.location = location;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * getVar
   * @return	the variable definition
   */
  public CReferenceType getThrowable() {
    return throwable;
  }

  /**
   * Return the location of this throwable
   */
  public JPhylum getLocation() {
    return location;
  }

  /**
   * Sets this throwable to be cached or not
   */
  public void setCatched(boolean catched) {
    this.catched = catched;
  }

  /**
   * Return true if this throwable is catched
   */
  public boolean isCatched() {
    return catched;
  }

  // ----------------------------------------------------------------------
  // OPTIMIZATION
  // ----------------------------------------------------------------------

  public boolean equals(Object o) {
    return ((CThrowableInfo)o).throwable.getCClass() == throwable.getCClass();
  }

  public int hashCode() {
    return throwable.getCClass().hashCode();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final CReferenceType	throwable;
  private final JPhylum		location;
  private boolean		catched;
}
