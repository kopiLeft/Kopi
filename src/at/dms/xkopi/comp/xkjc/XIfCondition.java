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
 * $Id: XIfCondition.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.TypeFactory;
import at.dms.xkopi.comp.sqlc.SearchCondition;
import at.dms.xkopi.comp.sqlc.SqlVisitor;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a java expression within a Sql context
 */
public class XIfCondition extends SearchCondition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public XIfCondition(TokenReference ref,
		      String oper,
		      SearchCondition left,
		      JExpression cond,
		      XSqlExpr thenClause,
		      XSqlExpr elseClause) {
    super(ref);

    this.left = left;
    this.oper = oper;
    this.cond = cond;
    this.thenClause = thenClause;
    this.elseClause = elseClause;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(SqlVisitor visitor)
    throws PositionedError
  {
    ((XSqlVisitor)visitor).visitIfCondition(this,
                                            oper,
                                            left,
                                            cond,
                                            thenClause,
                                            elseClause);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		oper;
  private SearchCondition	left;
  private JExpression		cond;
  private XSqlExpr		thenClause;
  private XSqlExpr		elseClause;
}
