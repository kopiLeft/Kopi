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
import at.dms.xkopi.comp.xkjc.XStdType;

/**
 * The type of a field which represents a integer-column in Database.k. 
 */
public class DatabaseTextColumn extends DatabaseStringColumn{

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are restricted.
   *
   * @param isNullable true if empty entries are allowed
   * @param width the smallest value allowed in this column
   * @param height the bigest value allowed in this column
   */
  public DatabaseTextColumn(boolean isNullable, Integer width, Integer height) {
    super(isNullable, width, height);
  }
  public DatabaseTextColumn(boolean isNullable, int width, int height) {
    super(isNullable, width, height);
  }

  /**
   * Creates a representation of a column with type integer. The values in 
   * the column are NOT restricted.
   */
  public DatabaseTextColumn(boolean isNullable) {
    super(isNullable);
  }
}
