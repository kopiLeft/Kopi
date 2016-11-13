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

import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.JavaStyleComment;

/**
 * This class represents the "package org.kopi.kopi.comp.kjc" statement
 */
public class JPackageName extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a package name
   *
   * @param	where		the token reference of this node
   * @param	name		the package name
   */
  public JPackageName(TokenReference where, String name, JavaStyleComment[] comments) {
    super(where);

    this.name = name.intern();
    this.comments = comments;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the package name defined by this declaration.
   *
   * @return	the package name defined by this declaration
   */
  public String getName() {
    return name;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (comments != null) {
      p.visitComments(comments);
    }
    if (!name.equals("")) {
      p.visitPackageName(name.replace('/', '.'));
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * The unnamed package (JLS 7.4.2).
   */
  public static final JPackageName	UNNAMED = new JPackageName(TokenReference.NO_REF, "", null);

  private final String			name;
  private final JavaStyleComment[]	comments;
}
