/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import com.kopiright.kopi.comp.kjc.*;


/**
 * The type of a field which represents a enum-column in Database.k. 
 */
public class DatabaseEnumColumn extends DatabaseColumn {

  /**
   * Creates a representation of a column with type Enum. The values in 
   * the column are restricted.
   *
   * @param isNullable true if empty entries are allowed
   * @param list allowed values
   */
  public DatabaseEnumColumn(boolean isNullable, String[] list) {
    super(isNullable, true);
    this.list = list;
  }

  /**
   * Creates a representation of a column with type String. The values in 
   * the column are NOT restricted.
   */
  public DatabaseEnumColumn(boolean isNullable) {
    super(isNullable, false);
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return CStdType.String;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    JExpression[] param;
    String name;

    if (isRestricted()) {
      param = new JExpression[list.length];
      for (int i=0; i<list.length; i++) {
        param[i] = new JStringLiteral(TokenReference.NO_REF, list[i]);
      } 
      JExpression strArray = new JNewArrayExpression(TokenReference.NO_REF,
                                                    DatabaseColumn.TYPE,
                                                    new JExpression[]{ null },
                                                    new JArrayInitializer(
                                                           TokenReference.NO_REF, 
                                                           param));
       param = new JExpression[]{
         new JBooleanLiteral(TokenReference.NO_REF, isNullable()), 
         strArray};
    } else {
       param = new JExpression[]{
         new JBooleanLiteral(TokenReference.NO_REF, isNullable())};
    }
    name = DatabaseEnumColumn.class.getName().replace('.','/');
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                            new CClassNameType(TokenReference.NO_REF, name),
                                            param);
  }

  /**
   * Returns true if dc is equivalent with the current column.
   *
   * @param other other column
   * @param check spezifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (other instanceof DatabaseStringColumn) {
      if (isRestricted() && ((check & CONSTRAINT_CHECK_NONE) == 0)) {
        int                     maxlength = 0;

        for (int i=0; i < list.length; i++) {
          if (list[0].length() > maxlength) {
            maxlength = list[0].length();
          }
        } 
        Integer                 width = ((DatabaseStringColumn) other).getWidth();

        if (width != null && width.intValue() < maxlength) {
          return false;
        }
      }
      return verifyNullable(other, check);
    } else if (other instanceof DatabaseEnumColumn) {
      if (isRestricted() && ((check & CONSTRAINT_CHECK_NONE) == 0)) {
        DatabaseEnumColumn      otherEnum = (DatabaseEnumColumn)other;  

        if (!other.isRestricted() || (list.length != otherEnum.list.length)) {
          return false;
        }
        for (int i=0; i < list.length; i++) {
          boolean               notfound = true;

          for (int k=0; k < otherEnum.list.length && notfound; k++) {
            if (list[i].equals(otherEnum.list[k])) {
              notfound = false;
            }
          }  
          if (notfound) {
            return false;
          }
        } 
      }
      return verifyNullable(other, check);
    } else {
      return false;
    }
  }
  
  /**
   * possible values of the enum type.
   */
  private String[] list=null;
}
