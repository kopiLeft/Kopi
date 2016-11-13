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

import java.util.ArrayList;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CBlockError;
import org.kopi.kopi.comp.kjc.CBodyContext;
import org.kopi.kopi.comp.kjc.JPhylum;
import org.kopi.kopi.comp.kjc.KjcVisitor;
import org.kopi.xkopi.comp.sqlc.FromClause;
import org.kopi.xkopi.comp.sqlc.GroupByClause;
import org.kopi.xkopi.comp.sqlc.HavingClause;
import org.kopi.xkopi.comp.sqlc.SelectStatement;
import org.kopi.xkopi.comp.sqlc.SortSpec;
import org.kopi.xkopi.comp.sqlc.SqlPhylum;
import org.kopi.xkopi.comp.sqlc.UpdSpec;
import org.kopi.xkopi.comp.sqlc.WhereClause;

/**
 * This class represents an Sql select statement
 */
public class XSelectStatement extends JPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param where the line of this node in the source code
   * @param p1 the modifiers
   * @param p2 a list of typed columns in database
   * @param p3 a Sql expression
    */
  public XSelectStatement(TokenReference where,
			  String p1,
			  ArrayList p2,
			  FromClause tableRefs,
			  WhereClause whereClause,
			  GroupByClause groupByClause,
			  HavingClause havingClause,
			  SortSpec sortSpec,
			  UpdSpec updateSpec)
  {
    super(where);
    mod = p1;
    attrs = (XTypedSelectElem[])p2.toArray(new XTypedSelectElem[p2.size()]);
    body = new SelectStatement(where,
			       p1,
			       p2,
			       tableRefs,
			       whereClause,
			       groupByClause,
			       havingClause,
			       sortSpec,
			       updateSpec);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   *
   */
  public XTypedSelectElem[] getTypedSelectElem() {
    return attrs;
  }

  /**
   * Returns the sql statement.
   */
  public SqlPhylum getSql() {
    return body;
  }

  public SelectStatement getSelect() {
    return body;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    for (int i = 0; i < attrs.length; i++) {
      for (int j = i + 1; j < attrs.length; j++) {
	if (attrs[i].getIdent().equals(attrs[j].getIdent())) {
	  throw new CBlockError(getTokenReference(), XKjcMessages.DUPLICATE_IDENT, attrs[i].getIdent());
	}
      }
    }
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param p the printwriter into the code is generated
   */
  public void accept(KjcVisitor p) {
    ((XKjcPrettyPrinter)p).visitXSelectStatement(this, mod, attrs, body);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		mod;
  private XTypedSelectElem[]	attrs;
  private SelectStatement	body;
}
