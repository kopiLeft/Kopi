/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.util;

import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;

/**
 * Local printer
 */
public abstract class GroupHandler {

  public void add(Object key, Object value) throws Exception {
    if (key == null) {
      throw new InconsistencyException("KEY IS NULL");
    }

    if (!key.equals(this.key)) {
      if (this.key != null) {
	foot(this.key, elems);
      }
      this.key = key;
      head(key, value);
      elems.setSize(0);
    }

    body(key, value);
    elems.addElement(value);
  }

  public void close() throws Exception {
    if (elems.size() > 0) {
      foot(key, elems);
    }
    key = null;
    elems.setSize(0);
  }

  /**
   * This method is called at the begining of a new group, with the new key
   */
  public abstract void head(Object key, Object elem) throws Exception;

  /**
   * This method is called for each new element
   */
  public abstract void body(Object key, Object elem) throws Exception;

  /**
   * This method is called at the end of a group, with body containing all elements
   */
  public abstract void foot(Object key, Vector body) throws Exception;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Object		key;
  private Vector		elems = new Vector();
}
