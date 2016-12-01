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
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JBooleanLiteral;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import org.kopi.xkopi.comp.xkjc.XStdType;

/**
 * The type of a field which represents a boolean-column in Database.k. 
 */
public class DatabaseBooleanColumn extends DatabaseColumn{

  /**
   * Creates a representation of a column with type boolean. The values in 
   * the column are NOT restricted.
   */
  public DatabaseBooleanColumn(boolean isNullable) {
    super(isNullable, false);
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return isNullable ? (CType)XStdType.Boolean : CStdType.Boolean;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                            new CClassNameType(TokenReference.NO_REF, 
                              DatabaseBooleanColumn.class.getName().replace('.','/')),
                            new JExpression[]{
                                new JBooleanLiteral(TokenReference.NO_REF, isNullable())}  
                            );
  }
  
  /**
   * Returns true if dc is equivalent with the current column. 
   *
   * @param other other column
   * @param check spezifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (other instanceof DatabaseBooleanColumn) {
      return verifyNullable(other, check);
    } else {
      return false;
    }
  }

}
