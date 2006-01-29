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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.compiler.base.CWarning;
import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.compiler.base.JavaStyleComment;

/**
 * JLS 7.5.1 Single-Type-Import Declaration.
 *
 * This class represents a single-type-import declaration
 * in the syntax tree.
 */
public class JClassImport extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a single-type-import declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param	name		the canonical name of the type
   * @param	comments	other comments in the source code
   */
  public JClassImport(TokenReference where,
		      String name,
		      JavaStyleComment[] comments)
  {
    super(where);

    this.name = name;
    this.comments = comments;

    this.used = false;

    int		index = name.lastIndexOf('/');

    this.ident = index == -1 ? name : name.substring(index + 1).intern();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS & MUTATORS
  // ----------------------------------------------------------------------

  /**
   * Returns the fully qualified name of the imported type.
   */
  public String getQualifiedName() {
    return name;
  }

  /**
   * Returns the simple qualified name of the imported type.
   */
  public String getSimpleName() {
    return ident;
  }

  /**
   * States that specified class is used.
   */
  public void setUsed() {
    used = true;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(Compiler compiler) {
    if (!used && getTokenReference() != TokenReference.NO_REF) {
      compiler.reportTrouble(new CWarning(getTokenReference(),
					  KjcMessages.UNUSED_CLASS_IMPORT,
					  name.replace('/', '.'),
					  null));
    }
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
    p.visitClassImport(name);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String			name;
  private final String			ident;
  private final JavaStyleComment[]	comments;
  private boolean			used;
}
