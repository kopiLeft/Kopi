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

package com.kopiright.compiler.tools.antlr.compiler;

import java.util.NoSuchElementException;

/**
 * A simple stack definition; restrictive in that you cannot
 * access arbitrary stack elements.
 *
 * @author Terence Parr
 * <a href=http://www.MageLang.com>MageLang Institute</a>
 */
public interface Stack {
  int height();

  Object pop() throws NoSuchElementException;

  void push(Object o);

  Object top() throws NoSuchElementException;
}
