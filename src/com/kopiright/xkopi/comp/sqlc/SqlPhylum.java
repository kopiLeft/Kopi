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

package com.kopiright.xkopi.comp.sqlc;

import java.util.Vector;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;

/**
 * This class represents an SqlPhylumession
 */
public abstract class SqlPhylum extends com.kopiright.compiler.base.Phylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  public SqlPhylum(TokenReference ref) {
    super(ref);
  }


  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  public void setComment(Vector comments) {
    if (comments != null && comments.size() > 0) {
      this.comments = (JavaStyleComment[])comments.toArray(new JavaStyleComment[comments.size()]);//;Utils.toArray(comments, JavaStyleComment.class);
    }
  }

  public JavaStyleComment[] getComment() {
    return comments;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public abstract void accept(SqlVisitor visitor) throws PositionedError;

  // ----------------------------------------------------------------------
  // STRING CONVERSION
  // ----------------------------------------------------------------------

  /**
   * Generates code.
   *
  public String toString() {
    StringBuffer	buffer = new StringBuffer();
    try {
      checkSql(null, null, buffer);
      //throw new InconsistencyException();
    } catch (Throwable e) {
      buffer.append("...");
    }
    return buffer.toString();
  }
*/
  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private JavaStyleComment[]		comments;
}
