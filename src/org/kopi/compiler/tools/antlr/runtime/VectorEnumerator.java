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

package org.kopi.compiler.tools.antlr.runtime;

import java.util.Enumeration;
import java.util.NoSuchElementException;

// based on java.lang.Vector; returns any null indices between non-null ones.
class VectorEnumerator implements Enumeration {
  Vector vector;
  int i;


  VectorEnumerator(Vector v) {
    vector = v;
    i = 0;
  }
  public boolean hasMoreElements() {
    synchronized (vector) {
      return i <= vector.lastElement;
    }
  }
  public Object nextElement() {
    synchronized (vector) {
      if (i <= vector.lastElement) {
	return vector.data[i++];
      }
      throw new NoSuchElementException("VectorEnumerator");
    }
  }
}
