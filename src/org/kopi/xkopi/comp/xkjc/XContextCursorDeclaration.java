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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.JavadocComment;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.JFieldDeclaration;
import org.kopi.kopi.comp.kjc.JFormalParameter;
import org.kopi.kopi.comp.kjc.JMethodDeclaration;
import org.kopi.kopi.comp.kjc.JPhylum;
import org.kopi.kopi.comp.kjc.JTypeDeclaration;
import org.kopi.kopi.comp.kjc.KjcVisitor;

/**
 * This class represents a cursor type definition in kopi grammar
 */
public class XContextCursorDeclaration extends XCursorDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	context		the context in which this class is defined
   * @param	modifiers	the list of modifiers of this class
   * @param	ident		the short name of this class
   * @param	superName	the name of super class of this class
   * @param	interfaces	the name of this class's interfaces
   * @param	deprecated	is this class deprecated
   */
  public XContextCursorDeclaration(TokenReference where,
                                   int modifiers,
                                   String ident,
                                   JFieldDeclaration[] fields,
                                   JMethodDeclaration[] methods,
                                   JTypeDeclaration[] inners,
                                   JPhylum[] initializers,
                                   XSelectStatement select,
                                   JFormalParameter[] parameters,
                                   JavadocComment javadoc,
                                   JavaStyleComment[] comments)
  {
    super(where,
	  modifiers,
	  ident,
	  fields,
	  methods,
	  inners,
	  initializers,
          select,
          parameters,
	  javadoc,
	  comments);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void accept(KjcVisitor p) {
    if (isChecked()) {
      super.accept(p);
    } else {
      genComments(p);
      ((XKjcPrettyPrinter)p).visitContextCursorDeclaration(getModifiers(), getIdent(), select, parameters, getFields());
    }
  }
}
