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
package org.kopi.xkopi.comp.database;

import org.kopi.compiler.base.TokenReference;
import org.kopi.kopi.comp.kjc.CClassNameType;
import org.kopi.kopi.comp.kjc.CStdType;
import org.kopi.kopi.comp.kjc.JArrayInitializer;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JFieldAccessExpression;
import org.kopi.kopi.comp.kjc.JNewArrayExpression;
import org.kopi.kopi.comp.kjc.JStringLiteral;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.xkopi.comp.xkjc.XDatabaseColumn;
import org.kopi.xkopi.comp.xkjc.XDatabaseTable;
import org.kopi.xkopi.comp.sqlc.Constants;

/**
 * The type of a field which represents a table in Database.k
 */
public class DatabaseTable extends DatabaseMember implements XDatabaseTable {

  public DatabaseTable(XDatabaseColumn[] columns, String[] names) {
    this.columns = columns;
    this.names = names;
  }
  public DatabaseTable() {
    this.columns = null;
    this.names = null;
  }

  /**
   * Checks whether this field represents a table.
   *
   * @return    true iff this field represents a table
   */
  public boolean isTable() {
    return true;
  }

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public static JExpression getCreationExpression(String tableName, String[] columnNames) {
    if ((columnNames == null) || (tableName == null)) {
      throw new IllegalArgumentException("Parameters must not be null");
    }
     JExpression[] initializer = new JExpression[columnNames.length];
     JExpression[] initializerNames = new JExpression[columnNames.length];
     for (int i=0; i < columnNames.length; i++) {
       initializer[i] = new JFieldAccessExpression(TokenReference.NO_REF, tableName + Constants.DICT_SEPARATOR + columnNames[i]);
       initializerNames[i] = new JStringLiteral(TokenReference.NO_REF, columnNames[i]);
     }
//     JExpression[] initializer = new JExpression[]{};
    JExpression paraColumns = new JNewArrayExpression(TokenReference.NO_REF,
                                                    DatabaseColumn.TYPE,
                                                    new JExpression[]{ null },
                                                    new JArrayInitializer(TokenReference.NO_REF, initializer));
    JExpression paraNames = new JNewArrayExpression(TokenReference.NO_REF,
                                                    CStdType.String,
                                                    new JExpression[]{ null },
                                                    new JArrayInitializer(TokenReference.NO_REF, initializerNames));
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                            new CClassNameType(TokenReference.NO_REF, org.kopi.xkopi.comp.database.DatabaseTable.class.getName().replace('.','/')),
                                            new JExpression[]{ paraColumns, paraNames });
  }

  public XDatabaseColumn[] getColumns() {
    return columns;
  }
  public String[] getNames() {
    return names;
  }

  private final XDatabaseColumn[]       columns;
  private final String[]                names;
}
