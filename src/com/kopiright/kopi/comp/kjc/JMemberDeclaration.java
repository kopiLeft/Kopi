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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavadocComment;
import com.kopiright.compiler.base.JavaStyleComment;


/**
 * This class represents a java class in the syntax tree
 */
public abstract class JMemberDeclaration extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	javadoc		java documentation comments
   * @param	comments	other comments in the source code
   */
  public JMemberDeclaration(TokenReference where,
			    JavadocComment javadoc,
			    JavaStyleComment[] comments)
  {
    super(where);
    this.comments = comments;
    this.javadoc = javadoc;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (INTERFACE)
  // ----------------------------------------------------------------------

  /**
   * Returns true if this member is deprecated
   */
  public boolean isDeprecated() {
    return javadoc != null && javadoc.isDeprecated();
  }

  /**
   * @return	the interface
   */
  public CField getField() {
    return export.getField();
  }

  /**
   * @return	the interface
   */
  public CMethod getMethod() {
    return export.getMethod();
  }

  /**
   * @return	the interface
   */
  public CClass getCClass() {
    return export.getCClass();
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    genComments(p);
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genComments(KjcVisitor p) {
    if (comments != null) {
      p.visitComments(comments);
    }

    if (javadoc != null) {
      p.visitJavadoc(javadoc);
    }
  }

  /**
   * Returns the comments
   */
  public JavaStyleComment[] getComments() {
    return comments;
  }

  /**
   * Returns the comments
   */
  public void setComments(JavaStyleComment[] comments) {
    this.comments = comments;
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  protected void setInterface(CMember export) {
    this.export = export;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CMember			export;
  private JavadocComment		javadoc;
  private JavaStyleComment[]            comments;
}
