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
 * $Id: VKFieldList.java,v 1.2 2004/12/17 10:52:30 lackner Exp $
 */

package at.dms.vkopi.comp.base;

import at.dms.xkopi.comp.sqlc.TableReference;
import at.dms.kopi.comp.kjc.*;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represent a list of data from the database
 */
public class VKFieldList extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a position given by x and y location
   *
   * @param where		the token reference of this node
   * @param table		the statement to select data
   * @param newForm		the name of the form to edit data
   * @param columns		a description of the columns
   * @param access		true if this field is only an access to a form
   */
  public VKFieldList(TokenReference where,
		     TableReference table,
		     CType newForm,
		     VKListDesc[] columns,
		     boolean access) {
    super(where);
    this.table = table;
    this.newForm = newForm;
    this.columns = columns;
    this.access = access;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return if there is a form to edit entries of this type
   */
  public boolean hasNewForm() {
    return newForm != null;
  }

  /**
   * Return true if there is a shortcut to access the form to edit this field
   */
  public boolean hasShortcut() {
    return newForm != null && access;
  }

  /**
   * Returns the table description
   */
  public TableReference getTable() {
    return table;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    /*
    if (newForm != null) {
      try {
	newForm.checkType(null); //  !!! Its not the right place
      } catch (UnpositionedError cue) {
        throw cue.addPosition(getTokenReference());
      }
      if (newForm.getCClass().descendsFrom(VKStdType.VDictionaryForm.getCClass())) {
	context.reportTrouble(new PositionedError(getTokenReference(),
						  "vk-must-inherit-from-dictionary",
						  newForm));
      }
    }
*/
    for (int i = 0; i < columns.length; i++) {
      columns[i].verfiyColumnType(context, table);
      columns[i].checkCode(context);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode(int actionNumber) {
    TokenReference	ref = getTokenReference();
    JExpression[]	init = new JExpression[columns.length];
    for (int i = 0; i < columns.length; i++) {
      init[i] = columns[i].genCode();
    }

    return new JUnqualifiedInstanceCreation(ref,
				    VKStdType.VList,
				    new JExpression[] {
				      VKUtils.createArray(ref, VKStdType.VListColumn, init),
				      VKUtils.toExpression(ref, actionNumber),
				      newForm != null
				      ? new JClassExpression(ref, newForm, 0)
				      : (JExpression)new JNullLiteral(ref),
				      VKUtils.toExpression(ref, hasShortcut())
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
    p.printList(table, newForm, columns, access);
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private	TableReference       	table;
  private       CType			newForm;
  private	VKListDesc[]		columns;
  private	boolean			access;
}
