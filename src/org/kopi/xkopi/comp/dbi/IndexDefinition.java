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
import java.util.ArrayList;

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.sqlc.Expression;
import org.kopi.xkopi.comp.sqlc.SimpleIdentExpression;

/**
 * This class represents a table definition
 */
public class IndexDefinition extends DbiStatement {

  public static final int TYP_NONE = 0;
  public static final int TYP_PRIMARY = 1;
  public static final int TYP_DISALLOW = 2;
  public static final int TYP_IGNORE = 3;

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	hasUnique	has UNIQUE ?
   * @param	indexName	the name of the index
   * @param	tableName	the name of the table
   * @param	indexElemList	the list of index elements
   * @param	type		the type
   */
  public IndexDefinition(TokenReference ref,
			 boolean hasUnique,
			 String indexName,
			 Expression tableName,
			 ArrayList indexElemList,
			 int type) {
    super(ref);
    this.hasUnique = hasUnique;
    this.indexName = indexName;
    this.tableName = tableName;
    this.indexElemList = indexElemList;
    this.type = type;
 }

  // ----------------------------------------------------------------------
  // GENERATE INSERT INTO THE DICTIONARY
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   */
  public void makeDBSchema(DBAccess a, String packageName)
    throws SQLException {
    int		index = a.addIndex(packageName,
				   indexName,
				   hasUnique,
				   ((SimpleIdentExpression)tableName).getIdent(),
				   (byte)type);
    for (int i = 0; i < indexElemList.size(); i++) {
      ((IndexElem)indexElemList.get(i)).makeDBSchema(a, packageName, index,
							   ((SimpleIdentExpression)tableName).getIdent(),
							   (byte)i);
    }
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
    visitor.visitIndexDefinition(this, hasUnique, indexName, tableName, indexElemList, type);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  String	indexName;
  Expression	tableName;
  boolean	hasUnique;
  ArrayList	indexElemList;
  int		type;
}
