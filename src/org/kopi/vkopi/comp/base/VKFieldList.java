/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.comp.base;

import org.kopi.xkopi.comp.sqlc.TableReference;
import org.kopi.kopi.comp.kjc.*;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

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
   * @param pack                the package name of the class defining the list containing type
   * @param type		the identifier of the type containing this list
   * @param table		the statement to select data
   * @param action		the field list action
   * @param columns		a description of the columns
   * @param newForm		the name of the form to edit data
   * @param access		true if this field is only an access to a form
   */
  public VKFieldList(TokenReference where,
                     String pack,
                     String type,
		     TableReference table,
		     VKFieldListAction action,
		     VKListDesc[] columns,
                     int autocompleteType,
                     int autocompleteLength,
		     CType newForm,
		     boolean access)
  {
    super(where);
    this.source = (pack == null) ? null : pack + "/" + where.getName().substring(0, where.getName().lastIndexOf('.'));
    this.type = type;
    this.table = table;
    this.action = action;
    this.columns = columns;
    this.autocompleteType = autocompleteType;
    this.autocompleteLength = autocompleteLength;
    this.newForm = newForm;
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
   * Returns <code>true</code> if the list has a list action.
   */
  public boolean hasAction() {
    return action != null;
  }

  /**
   * Return true if there is a shortcut to access the form to edit this field
   */
  public boolean hasShortcut() {
    return (hasNewForm() || hasAction()) && access;
  }

  /**
   * Returns the table description
   */
  public TableReference getTable() {
    return table;
  }
  
  /**
   * Returns the field list action.
   * @return The field list action.
   */
  public VKFieldListAction getAction() {
    return action;
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
      columns[i].verifyColumnType(context, table);
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
  public JExpression genCode(int actionNumber, int listActionNumber) {
    JExpression[]	init;
    TokenReference	ref;

    init = new JExpression[columns.length];
    for (int i = 0; i < columns.length; i++) {
      init[i] = columns[i].genCode();
    }
    ref = getTokenReference();

    return new JUnqualifiedInstanceCreation(ref,
                                            VKStdType.VList,
                                            new JExpression[] {
                                              VKUtils.toExpression(ref, type),
                                              VKUtils.toExpression(ref, source),
                                              VKUtils.createArray(ref, VKStdType.VListColumn, init),
                                              VKUtils.toExpression(ref, actionNumber),
                                              VKUtils.toExpression(ref, listActionNumber),
                                              VKUtils.toExpression(ref, autocompleteType),
                                              VKUtils.toExpression(ref, autocompleteLength),
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
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate localization for this type.
   *
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genFieldList(columns);
  }
  
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private final String                  source;
  private final String                  type;
  private final	TableReference       	table;
  private final VKFieldListAction 	action;
  private final VKListDesc[]		columns;
  private final int                     autocompleteType;
  private final int                     autocompleteLength;
  private final CType			newForm;
  private final boolean			access;
}
