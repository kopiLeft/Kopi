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
 * $Id: DatabaseCardinalColumn.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */
package at.dms.xkopi.comp.database;

import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.kopi.comp.kjc.CClassNameType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.xkopi.comp.xkjc.XStdType;

/**
 * The type of a field which represents a byte/short/integer in Database.k. 
 */
public abstract class DatabaseCardinalColumn extends DatabaseColumn{

  /**
   * Creates a representation of a column.
   *
   * @param nullable true if empty entries are allowed
   * @param type kind of type
   */
  public DatabaseCardinalColumn(boolean isNullable, Integer min, Integer max) {
    super(isNullable, true);
    this.min = min;
    this.max = max;
  }

  /**
   * Creates a representation of a column. (Not restricted)
   *
   * @param nullable true if empty entries are allowed
   * @param type kind of type
   */
  public DatabaseCardinalColumn(boolean isNullable) {
    super(isNullable, false);
    min = null;
    max = null;
  }

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @param className qualified name of the class
   * @return the expression
   */
  protected JExpression getCreationExpression(String className) {
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                            new CClassNameType(TokenReference.NO_REF, className),
                            isRestricted() ? 
                              new JExpression[]{
                                new JBooleanLiteral(TokenReference.NO_REF, isNullable()),
                                (min == null) ?
                                  (JExpression)new JNullLiteral(TokenReference.NO_REF) :  
                                  new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                    new CClassNameType(TokenReference.NO_REF, "java/lang/Integer"),
                                    new JExpression[] {
                                      new JIntLiteral(TokenReference.NO_REF, min.intValue())
                                   }),  
                                (max == null) ?
                                  (JExpression)new JNullLiteral(TokenReference.NO_REF) :  
                                  new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                    new CClassNameType(TokenReference.NO_REF, "java/lang/Integer"),
                                    new JExpression[] {
                                      new JIntLiteral(TokenReference.NO_REF, max.intValue())
                                      }) }: 
                              new JExpression[]{
                                new JBooleanLiteral(TokenReference.NO_REF, isNullable())}
                            );
  }

  /**
   * Returns true if dc is equivalent with the current column.
   *
   * @param other other column
   * @param check specifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other, int check) {
    if (! (other instanceof DatabaseCardinalColumn)) {
      return false;
    } else {  
      if (isRestricted() && ((check & CONSTRAINT_CHECK_NONE) == 0 )) {
        if (!other.isRestricted()) {
          return false;
        } else {
          DatabaseCardinalColumn intColumn = (DatabaseCardinalColumn)other;
          if ((min != null) && ((intColumn.min == null) || (!min.equals(intColumn.min)))) {
            return false;
          }
          if ((max != null) && ((intColumn.max == null) || (!max.equals(intColumn.max)))) {
          return false;
          }
        }
      }
      return verifyNullable(other, check);
    }
  }

  /**
   * Returns the smallest allowed value.
   * @retrun the allowed minimum
   */
  public Integer getMin() {
    return min;
  }
  /**
   * Returns the bigest allowed value.
   * @retrun the allowed maxmum
   */
  public Integer getMax() {
    return max;
  }

  public String toString() {
    return getStandardType()+
           ((isNullable())? "(nullable, " : "(not null, ")+
           ((isRestricted())? ("min: "+min+" max:"+max+")") : "not rest.) ");
  }  
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Integer min;
  private final Integer max;
}
