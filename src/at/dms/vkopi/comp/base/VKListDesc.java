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
 * $Id: VKListDesc.java,v 1.2 2004/12/17 10:52:30 lackner Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;

import at.dms.xkopi.comp.sqlc.SqlcMessages;
import at.dms.xkopi.comp.sqlc.TableName;
import at.dms.xkopi.comp.sqlc.TableReference;
import at.dms.xkopi.comp.xkjc.XDatabaseColumn;
import at.dms.xkopi.comp.xkjc.XUtils;

/**
 * The description of a list element
 */
public class VKListDesc extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param title		the title of the column
   * @param column		the column itself
   * @param type		the type of the column
   */
  public VKListDesc(TokenReference where, String title, String column, VKType type) {
    super(where);

    this.title = title;
    this.column = column;
    this.type = type;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  public void verfiyColumnType(VKContext context, TableReference table) {

    if (!table.hasColumn(column)) {
      String name  = null;

      if (table instanceof TableName) {
        name = ((TableName) table).getTableName();
      } 
      if (name == null) {
        name = "";
      }
      context.reportTrouble(new CWarning(getTokenReference(), SqlcMessages.COLUMN_NOT_IN_TABLE, column, name));
      return;
    }

    String      tableName = table.getTableForColumn(column);

    if (tableName == null) {
      return;
    }

    try {
      XUtils.checkDatabaseType(type.getColumnInfo(),
                               tableName,
                               column,
                               XDatabaseColumn.TYPE_CHECK_NARROWING | XDatabaseColumn.NULL_CHECK_NONE);    
    } catch (UnpositionedError e) {
      context.reportTrouble(new CWarning(getTokenReference(), e.getFormattedMessage()));
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    type.checkCode(context);
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode() {
    TokenReference	ref = getTokenReference();

    if (type instanceof VKFixedType) {
      return new JUnqualifiedInstanceCreation(ref,
				      type.getListColumnType(),
				      new JExpression[] {
					VKUtils.toExpression(ref, title),
					VKUtils.toExpression(ref, column),
					VKUtils.toExpression(ref, type.getDefaultAlignment()),
					VKUtils.toExpression(ref, type.getWidth()),
					VKUtils.toExpression(ref, type.getHeight()),
					VKUtils.trueLiteral(ref)});
    } else if (type instanceof VKCodeType) {
      return new JUnqualifiedInstanceCreation(ref,
				      type.getListColumnType(),
				      new JExpression[] {
					VKUtils.toExpression(ref, title),
					VKUtils.toExpression(ref, column),
					((VKCodeType)type).genNames(),
					((VKCodeType)type).genValues(),
					VKUtils.trueLiteral(ref)});
    } else if (hasSize(type)) {
       return new JUnqualifiedInstanceCreation(ref,
				      type.getListColumnType(),
				      new JExpression[] {
					VKUtils.toExpression(ref, title),
					VKUtils.toExpression(ref, column),
					VKUtils.toExpression(ref, type.getDefaultAlignment()),
					VKUtils.toExpression(ref, type.getWidth()),
					VKUtils.trueLiteral(ref)});
    } else {
       return new JUnqualifiedInstanceCreation(ref,
				       type.getListColumnType(),
				       new JExpression[] {
					 VKUtils.toExpression(ref, title),
					 VKUtils.toExpression(ref, column),
					 VKUtils.trueLiteral(ref)});
    }
  }

  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------

  /**
   * Returns true if this list need additional infos
   */
  public static boolean hasSize(VKType type) {
    if (type instanceof VKFixedType) {
      // !!!! MOVE TO TYPE
      return true;
    }
    if (type instanceof VKIntegerType) {
      return true;
    }
    if (type instanceof VKStringType) {
      return true;
    }

    return false;
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in kopi form
   * It is useful to debug and tune compilation process
   * @param p		the printwriter into the code is generated
   */
  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printListDesc(title, column, type);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		title;
  private String		column;
  private VKType		type;
}
