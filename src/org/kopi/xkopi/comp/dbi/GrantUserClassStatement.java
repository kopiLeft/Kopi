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

package org.kopi.xkopi.comp.dbi;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * This class represents a grant user class statement
 */
public class GrantUserClassStatement extends DbiStatement {

  public static final int TYP_ACCESS = 0;
  public static final int TYP_RESOURCE = 1;
  public static final int TYP_DBA = 2;

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	userClass	the user class
   * @param	userName	the user name
   */
  public GrantUserClassStatement(TokenReference ref,
				int userClass,
				String userName) {
    super(ref);
    this.userClass = userClass;
    this.userName = userName;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitGrantUserClassStatement(this, userClass, userName);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  int		userClass;
  String	userName;
}
