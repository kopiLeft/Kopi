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

import java.util.ArrayList;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.CWarning;

/**
 * This class represents the last select clause
 */
public class SortSpec extends SqlPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public SortSpec(TokenReference ref, ArrayList elems) {
    super(ref);
    this.elems = elems;
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
    visitor.visitSortSpec(this, elems);
  }

  // ----------------------------------------------------------------------
  // CHECK SQL
  // ----------------------------------------------------------------------

  /**
   * return the number of idents
   */
  public int getNumberOfIdents() {
    return elems.size();
  }

  /**
   * This function check if all the idents are unique
   */
  public boolean checkIdentsAreUnique(SqlContext context) {
    for (int i = 0; i < elems.size(); i++) {
      Expression expr = ((SortElem)elems.get(i)).getExpression();
      if (expr instanceof SimpleIdentExpression) {
	String column = ((SimpleIdentExpression)expr).getIdent();
	for (int j = i + 1; j < elems.size(); j++) {
	  Expression otherExpr = ((SortElem)elems.get(j)).getExpression();
	  if (otherExpr instanceof SimpleIdentExpression) {
	    String otherColumn = ((SimpleIdentExpression)otherExpr).getIdent();
	    if (column.equals(otherColumn)) {
	      context.reportTrouble(new CWarning(getTokenReference(), SqlcMessages.COLUMN_NOT_UNIQUE, column));
	      return false;
	    }
	  }
	}
      } else {
	if (expr instanceof IntegerLiteral) {
	  Integer literal = (Integer)(((IntegerLiteral)expr).getValue());
	  for (int j = i + 1; j < elems.size(); j++) {
	    Expression otherExpr = ((SortElem)elems.get(j)).getExpression();
	    if (otherExpr instanceof IntegerLiteral) {
	      Integer otherLiteral = (Integer)(((IntegerLiteral)otherExpr).getValue());
	      if (literal != null && otherLiteral != null) {
		if (literal.intValue() == otherLiteral.intValue()) {
		  context.reportTrouble(new CWarning(getTokenReference(), SqlcMessages.COLUMN_NOT_UNIQUE, literal));
		  return false;
		}
	      }
	  }
	  }
	}
      }
    }
    return true;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ArrayList	elems;
}
