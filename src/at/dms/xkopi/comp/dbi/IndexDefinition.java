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
 * $Id: IndexDefinition.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.sql.SQLException;
import java.util.ArrayList;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.Expression;
import at.dms.xkopi.comp.sqlc.SimpleIdentExpression;

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
