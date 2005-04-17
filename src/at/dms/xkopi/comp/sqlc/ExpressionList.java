/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.xkopi.comp.sqlc;

import java.util.List;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

public class ExpressionList extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		           the token reference for this statement
   * @param	exprs	                   the vector of expression
   */
  public ExpressionList (TokenReference ref, List exprs) {
    super(ref);
    this.exprs = exprs;
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  /**
   * getExpression
   */
  public List getExpression() {
    return exprs;
  }

  /**
   * getElem
   */
  public Expression getElem(int i) {
    return (Expression)exprs.get(i);
  }

  // ----------------------------------------------------------------------
  // FUNCTIONS
  // ----------------------------------------------------------------------

  public boolean equals(Object otherExpressionList) {
    if (otherExpressionList instanceof ExpressionList) {
      List	otherExprs = ((ExpressionList)otherExpressionList).getExpression();
      if (exprs.size() == otherExprs.size()) {
	for (int i = 0; i < exprs.size(); i++) {
	  if ((exprs.get(i) instanceof Literal) &&
	      (otherExprs.get(i) instanceof Literal)) {
	    if (!((Literal)exprs.get(i)).equals((Literal)otherExprs.get(i))) {
	      for (int j = 0; j < exprs.size(); j++) {
	      }
	      return false;
	    }
	  } else {
	    System.err.println("Can't compare Expressions that are not literals");
	    return false;
	  }
	}
	return true;
      }
    }
    return false;
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
    visitor.visitExpressionList(this, exprs);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * Return the number of elements in the vector
   */
  public int elemNumber() {
    return exprs.size();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private List          exprs;
}
