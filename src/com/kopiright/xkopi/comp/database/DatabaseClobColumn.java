/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.xkopi.comp.database;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CClassNameType;
import com.kopiright.kopi.comp.kjc.CStdType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JBooleanLiteral;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;

/**
 * The type of a field which represents a boolean-column in Database.k. 
 */
public class DatabaseClobColumn extends DatabaseColumn{

  /**
   * Creates a representation of a column with type boolean. The values in 
   * the column are NOT restricted.
   */
  public DatabaseClobColumn(boolean isNullable) {
    super(isNullable, false);
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return CStdType.Object;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    final String                classNameType;
    final JExpression[]         arguments;

    classNameType = DatabaseClobColumn.class.getName().replace('.','/');
    arguments = new JExpression[]{ new JBooleanLiteral(TokenReference.NO_REF,
                                                       isNullable())};

    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                            new CClassNameType(TokenReference.NO_REF, classNameType),
                                            arguments);
  }
  
  /**
   * Returns true if dc is equivalent with the current column. 
   *
   * @param other other column
   * @param check spezifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (other instanceof DatabaseClobColumn) {
      return verifyNullable(other, check);
    } else {
      return false;
    }
  }
}
