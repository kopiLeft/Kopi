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

import java.sql.SQLException;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.sqlc.Expression;
import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;

/**
 * This class represents a View deletion
 */
public class DropViewStatement extends DbiStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	viewName	the name of the view
   */
  public DropViewStatement(TokenReference ref, Expression viewName) {
    super(ref);
    this.viewName = viewName;
  }

  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, String packageName)
    throws SQLException {
    a.dropView(packageName, ((SimpleIdentExpression)viewName).getIdent());
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
    visitor.visitDropViewStatement(this, viewName);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  Expression	viewName;
}
