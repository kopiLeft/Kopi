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
 * $Id: DatabaseIntegerColumn.java,v 1.1 2004/07/28 18:43:29 imad Exp $
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
 * The type of a field which represents a integer-column in Database.k. 
 */
public class DatabaseIntegerColumn extends DatabaseCardinalColumn{

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are restricted.
   *
   * @param nullable true if empty entries are allowed
   * @param min the smallest value allowed in this column
   * @param max the bigest value allowed in this column
   */
  public DatabaseIntegerColumn(boolean isNullable, Integer min, Integer max) {
    super(isNullable, min, max);
  }
  public DatabaseIntegerColumn(boolean isNullable, int width, int height) {
    super(isNullable, 
          (width == -1) ? null : new Integer(width),
          (height == -1) ? null : new Integer(height));
  }

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are NOT restricted.
   */
  public DatabaseIntegerColumn(boolean isNullable) {
    super(isNullable);
  }

  /**
   * Returns the type in Java of this column.
   *
   * @return the type
   */
  protected CType getStandardType(boolean isNullable) {
    return isNullable ? (CType)XStdType.Int : CStdType.Integer;
  } 

  /**
   * Creates a new-Expression which contructs this object.  
   *
   * @return the expression
   */
  public JExpression getCreationExpression() {
    return getCreationExpression(DatabaseIntegerColumn.class.getName().replace('.','/'));
  }

  /**
   * Returns true if dc is equivalent with the current column. 
   *
   * @param other other column
   * @param check specifies the check
   */
  public boolean isEquivalentTo(DatabaseColumn other,int check) {
    if (! (other instanceof DatabaseIntegerColumn)) {
      return false;
    } else {  
      return super.isEquivalentTo(other, check);
    }
  }
}
