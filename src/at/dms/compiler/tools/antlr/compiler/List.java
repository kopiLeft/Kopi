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

package at.dms.compiler.tools.antlr.compiler;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A simple List interface that describes operations
 * on a list.
 */
public interface List {
  void add(Object o); // can insert at head or append.

  void append(Object o);

  Object elementAt(int index) throws NoSuchElementException;

  Enumeration elements();

  boolean includes(Object o);

  int length();
}
