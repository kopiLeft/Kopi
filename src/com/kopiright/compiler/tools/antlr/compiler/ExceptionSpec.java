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

import com.kopiright.compiler.tools.antlr.runtime.*;

class ExceptionSpec {

  // Non-null if this refers to a labeled rule
  // Use a token instead of a string to get the line information
  protected Token label;

  // List of ExceptionHandler (catch phrases)
  protected Vector handlers;


  public ExceptionSpec(Token label_) {
    label = label_;
    handlers = new Vector();
  }
  public void addHandler(ExceptionHandler handler) {
    handlers.appendElement(handler);
  }
}
