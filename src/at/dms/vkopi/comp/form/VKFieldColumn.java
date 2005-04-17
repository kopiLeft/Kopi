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

package at.dms.vkopi.comp.form;


import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import at.dms.compiler.base.CWarning;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.vkopi.comp.base.*;
import at.dms.xkopi.comp.xkjc.XUtils;
import at.dms.xkopi.comp.xkjc.XDatabaseColumn;

/**
 * A binding to database
 */
public class VKFieldColumn extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param corr		the name of the table in database
   * @param ident		the name of the column
   * @param isKey		true if thiscolumn is a key in the database
   */
  public VKFieldColumn(TokenReference where,
		       String corr,
		       String ident,
		       boolean isKey) {
    super(where);

    this.corr = corr;
    this.ident = ident;
    this.isKey = isKey;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Sets the position in an array of fields
   */
  public VKFieldColumn cloneToPos(int pos) {
    return new VKFieldColumn(getTokenReference(), corr, ident + pos, isKey);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKFieldColumns columns, VKField field) throws PositionedError {
    VKBlockTable	table = field.getTable(corr);
    num = field.getTableNum(table);

    try {
      //      XUtils.checkDatabaseType(GVKAccess.translate(field.getFieldType().getDef().getType()),
      XUtils.checkDatabaseType(field.getFieldType().getDef().getColumnInfo(),
			       table.getName(),
			       ident,
			       XDatabaseColumn.TYPE_CHECK_NARROWING | XDatabaseColumn.NULL_CHECK_NONE);
    } catch (UnpositionedError e) {
      context.reportTrouble(new CWarning(getTokenReference(), e.getFormattedMessage()));
    }
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

    return new JUnqualifiedInstanceCreation(ref,
				    VKStdType.VColumn,
				    new JExpression[] {
				      VKUtils.toExpression(ref, num),
				      VKUtils.toExpression(ref, ident),
				      VKUtils.toExpression(ref, isKey),
				    });
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
    p.printFieldColumn(corr, ident, isKey);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private String	corr;
  private int		num;
  private String	ident;
  private boolean	isKey;
}
