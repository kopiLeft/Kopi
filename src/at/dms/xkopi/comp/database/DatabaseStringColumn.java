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
import at.dms.kopi.comp.kjc.*;
import at.dms.kopi.comp.kjc.CClassNameType;
import at.dms.kopi.comp.kjc.CType;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;

/**
 * The type of a field which represents a string-column in Database.k.
 */
public class DatabaseStringColumn extends DatabaseColumn {

  /**
   * Creates a representation of a column with type integer. The values in
   * the column are restricted.
   *
   * @param isNullable true if empty entries are allowed
   * @param width the smallest value allowed in this column
   * @param height the bigest value allowed in this column
   */
  public DatabaseStringColumn(boolean isNullable, Integer width, Integer height) {
    super(isNullable, true);
    this.width = width;
    this.height = height;
  }

  public DatabaseStringColumn(boolean isNullable, int width, int height) {
    super(isNullable, true);
    this.width = (width == -1) ? null : new Integer(width);
    this.height = (height == -1) ? null : new Integer(height);;
  }

  /**
   * Creates a representation of a column with type integer. The values in
   * the column are NOT restricted.
   */
  public DatabaseStringColumn(boolean isNullable) {
    super(isNullable, false);
    width = null;
    height = null;
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
   * Returns the width of this column.
   *
   * @return the width
   */
  protected Integer getWidth() {
    return width;
  }


  /**
   * Returns the height of this column.
   *
   * @return the width
   */
  protected Integer getHeight() {
    return height;
  }
  /**
   * Creates a new-Expression which contructs this object.
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    return new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                            new CClassNameType(TokenReference.NO_REF, DatabaseStringColumn.class.getName().replace('.','/')),
                            isRestricted() ?
                              new JExpression[]{
                                new JBooleanLiteral(TokenReference.NO_REF, isNullable()),
                                (width == null) ?
                                  (JExpression)new JNullLiteral(TokenReference.NO_REF) :
                                  new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                    new CClassNameType(TokenReference.NO_REF, "java/lang/Integer"),
                                    new JExpression[] {
                                      new JIntLiteral(TokenReference.NO_REF, width.intValue())
                                      }),
                                (height == null) ?
                                  (JExpression)new JNullLiteral(TokenReference.NO_REF) :
                                  new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                    new CClassNameType(TokenReference.NO_REF, "java/lang/Integer"),
                                    new JExpression[] {
                                      new JIntLiteral(TokenReference.NO_REF, height.intValue())
                                      }) }:
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
    if (other instanceof DatabaseEnumColumn) {
      return other.isEquivalentTo(this, check);
    } else if (other instanceof DatabaseStringColumn){
      if (isRestricted() && ((check & CONSTRAINT_CHECK_NONE) == 0)) {
        if (!other.isRestricted()) {
          return false;
        }
       DatabaseStringColumn textColumn = (DatabaseStringColumn) other;
        if ((width != null) &&
            ((textColumn.width == null) || (!width.equals(textColumn.width)))) {
          return false;
        }
        if ((height != null) &&
            ((textColumn.height == null) || (!height.equals(textColumn.height)))) {
          return false;
        }
      }
      return verifyNullable(other, check);
    } else {
      return false;
    }
  }

  public String toString() {
    return getStandardType()+
           ((isNullable())? "(nullable, " : "(not null, ")+
           ((isRestricted())? ("width: "+width+" height: "+height+")") : "not rest.) ");
  }

  private final Integer         width;
  private final Integer         height;
}
