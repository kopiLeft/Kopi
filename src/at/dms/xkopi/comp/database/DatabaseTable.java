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
package at.dms.xkopi.comp.database;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CClassNameType;
import at.dms.kopi.comp.kjc.CReferenceType;
import at.dms.kopi.comp.kjc.CStdType;
import at.dms.kopi.comp.kjc.JArrayInitializer;
import at.dms.kopi.comp.kjc.JFieldAccessExpression;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JNameExpression;
import at.dms.kopi.comp.kjc.JNewArrayExpression;
import at.dms.kopi.comp.kjc.JStringLiteral;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.xkopi.comp.xkjc.XDatabaseColumn;
import at.dms.xkopi.comp.xkjc.XDatabaseTable;

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
       initializer[i] = new JFieldAccessExpression(TokenReference.NO_REF, tableName + "_" + columnNames[i]);
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
                                            new CClassNameType(TokenReference.NO_REF, at.dms.xkopi.comp.database.DatabaseTable.class.getName().replace('.','/')),
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
