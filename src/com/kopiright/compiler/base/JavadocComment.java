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

package com.kopiright.compiler.base;

/**
 * A simple character constant
 */
public class JavadocComment extends JavaStyleComment {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	text		the string representation of this comment
   */
  public JavadocComment(String text, boolean deprecated, boolean spaceBefore, boolean spaceAfter) {
    super(text, false, spaceBefore, spaceAfter);

    this.deprecated = deprecated;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return if this javadoc comment contains a deprecated clause
   */
  public boolean isDeprecated() {
    return deprecated;
    //    return text.indexOf("@deprecated") >= 0;
  }

  /**
   *
   */
  public String getParams() {
    return text;
  }

  private boolean deprecated;
}
