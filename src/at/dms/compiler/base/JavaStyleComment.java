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
 * $Id: JavaStyleComment.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.compiler.base;

/**
 * A simple character constant
 */
public class JavaStyleComment {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	text		the string representation of this comment
   * @param	!!! COMPLETE
   */
  public JavaStyleComment(String text, boolean isLineComment, boolean spaceBefore, boolean spaceAfter) {
    this.text = text;
    this.isLineComment = isLineComment;
    this.spaceBefore = spaceBefore;
    this.spaceAfter = spaceAfter;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public String getText() {
    return text;
  }

  /**
   *
   */
  public boolean isLineComment() {
    return isLineComment;
  }

  /**
   *
   */
  public boolean hadSpaceBefore() {
    return spaceBefore;
  }

  /**
   *
   */
  public boolean hadSpaceAfter() {
    return spaceAfter;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	String		text;
  private final boolean		isLineComment;
  private final boolean		spaceBefore;
  private final boolean		spaceAfter;

  public static JavaStyleComment[] EMPTY = new JavaStyleComment[0];
}
