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

package at.dms.xkopi.comp.dbi;

import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import java.sql.SQLException;

/**
 * This class represents a table definition
 */
public class IndexElem extends DbiStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	name		the name of the index
   * @param	isDesc		has DESC
   * @param	key		the ek of the table
   */
  public IndexElem(TokenReference ref,
		   String name,
		   boolean isDesc)
  {
    super(ref);
    this.name = name;
    this.isDesc = isDesc;
  }

  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, String packageName, int index, String tableName, byte position)
     throws SQLException {
    a.addIndexColumn(index, packageName, tableName, name, position, isDesc);
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
    visitor.visitIndexElem(this, name, isDesc);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  String	name;
  boolean	isDesc;
}
