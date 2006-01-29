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

package com.kopiright.xkopi.comp.sqlc;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class BetweenPredicate extends Predicate {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this statement
   * @param	start		begining of the range
   * @param	startQualifier	mode (INCLUSIVE | EXCLUSIVE)
   * @param	end		end of the range
   * @param	endQualifier	mode (INCLUSIVE | EXCLUSIVE)
   */
  public BetweenPredicate(TokenReference ref,
			  Expression start,
			  String startQualifier,
			  Expression end,
			  String endQualifier) {
    super(ref);
    this.start = start;
    this.startQualifier = startQualifier;
    this.end = end;
    this.endQualifier = endQualifier;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor) throws PositionedError {
    visitor.visitBetweenPredicate(this, start, startQualifier, end, endQualifier);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Expression	start;
  private String	startQualifier;
  private Expression	end;
  private String	endQualifier;
}
