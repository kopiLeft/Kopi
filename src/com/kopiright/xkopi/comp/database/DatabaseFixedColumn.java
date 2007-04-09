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
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.kopi.comp.kjc.CClassNameType;
import com.kopiright.kopi.comp.kjc.CType;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.xkopi.comp.xkjc.XStdType;
import com.kopiright.xkopi.lib.type.Fixed;

/**
 * The type of a field which represents a integer-column in Database.k. 
 */
public class DatabaseFixedColumn extends DatabaseColumn {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  
  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are restricted.
   *
   * @param nullable true if empty entries are allowed
   * @param min the smallest value allowed in this column
   * @param max the bigest value allowed in this column
   */
  public DatabaseFixedColumn(boolean isNullable,
                             int digits,
                             int scale,
                             Fixed min,
                             Fixed max)
  {
    super(isNullable, (min != null) || (max !=null));
    this.digits = digits;
    this.scale = scale;
    this.min = min;
    this.max = max;
  }

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are restricted.
   *
   * @param nullable true if empty entries are allowed
   * @param min the smallest value allowed in this column
   * @param max the bigest value allowed in this column
   */
  public DatabaseFixedColumn(boolean isNullable,
                             Fixed min,
                             Fixed max)
  {
    this(isNullable, -1, -1, min, max);
  }
  
  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are NOT restricted.
   */
  public DatabaseFixedColumn(boolean isNullable) {
    this(isNullable, null, null);
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   *
   */
  private int getDigits() {
    return digits;
  }
  
  /**
   *
   */
  private int getScale() {
    return scale;
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return isNullable ?  XStdType.Fixed : XStdType.PFixed;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    CClassNameType      classNameType;
    JExpression         minValExpression;
    JExpression         maxValExpression;
    
    classNameType = new CClassNameType(TokenReference.NO_REF,
                                       DatabaseFixedColumn.class.getName().replace('.','/'));
    
    if (min == null) {
      minValExpression = new JNullLiteral(TokenReference.NO_REF);
    } else {
      minValExpression = new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                                          XStdType.Fixed,
                                                          new JExpression[] {
                                                            new JDoubleLiteral(TokenReference.NO_REF,
                                                                               min.doubleValue())
                                                          });
    }
    if (max == null) {
      maxValExpression = new JNullLiteral(TokenReference.NO_REF);
    } else {
      maxValExpression = new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                                          XStdType.Fixed,
                                                          new JExpression[] {
                                                            new JDoubleLiteral(TokenReference.NO_REF,
                                                                               max.doubleValue())
                                                          });
    }
    
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                            classNameType,
                                            new JExpression[] {
                                              new JBooleanLiteral(TokenReference.NO_REF, isNullable()),
                                              new JIntLiteral(TokenReference.NO_REF, digits),
                                              new JIntLiteral(TokenReference.NO_REF, scale),
                                              minValExpression,
                                              maxValExpression
                                            });
  }

  /**
   * Returns true if dc is equivalent with the current column. 
   *
   * @param other other column
   * @param check specifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (! (other instanceof DatabaseFixedColumn)) {
      return false;
    } else {
      if ((check & CONSTRAINT_CHECK_NONE) == 0) {
        DatabaseFixedColumn   fixedColumn = (DatabaseFixedColumn) other;
        
        if ((min != null)
            && ((fixedColumn.min == null)
                || (!min.equals(fixedColumn.min))))
        {
          return false;
        }
        if ((max != null)
            && ((fixedColumn.max == null)
                || (!max.equals(fixedColumn.max))))
        {
          return false;
        }
        if ((digits != fixedColumn.getDigits())
            || (scale != fixedColumn.getScale()))
        {
          return false;
        }
      }
      return (verifyNullable(other, check));
    }

  }
  
  public String toString() {
    return getStandardType()
           + ((isNullable())? "(nullable, " : "(not null, ")
           + "digits: " + digits + ", scale:" + scale + ", "
           + ((isRestricted())? ("min: " + min + " max:" + max + ")") : "not rest.)");
  }  
  
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------
  
  private final int     digits;
  private final int     scale;
  private final Fixed   min;
  private final Fixed   max;
}
