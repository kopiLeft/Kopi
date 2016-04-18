/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.comp.form;

import java.util.ArrayList;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CArrayType;
import com.kopiright.kopi.comp.kjc.CParseClassContext;
import com.kopiright.kopi.comp.kjc.JCompoundStatement;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JFieldAccessExpression;
import com.kopiright.kopi.comp.kjc.JStatement;
import com.kopiright.vkopi.comp.base.VKCommand;
import com.kopiright.vkopi.comp.base.VKTrigger;
import com.kopiright.vkopi.comp.base.VKUtils;

/**
 * This class represents an editable element of a block
 */
public class VKMultiField extends VKField implements com.kopiright.kopi.comp.kjc.Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This class represents the definition of a form
   *
   * @param where		the token reference of this node
   * @param ident		the ident of this field
   * @param number		the number of clone
   * @param pos			the position within the block
   * @param label		the label (text on the left)
   * @param help		the help text
   * @param type		the type of this field
   * @param align		the alignement of the text
   * @param options		the options of the field
   * @param columns		the column in the database
   * @param access		the access mode
   * @param commands		the commands accessible in this field
   * @param triggers		the triggers executed by this field
   * @param alias		th e alias of this field
   */
  public VKMultiField(TokenReference where,
		      String ident,
		      int number,
		      int count,
		      VKPosition pos,
		      String label,
		      String help,
		      VKFieldType type,
		      int align,
		      int options,
		      VKFieldColumns columns,
		      int[] access,
		      VKCommand[] commands,
		      VKTrigger[] triggers,
		      String alias) {
    super(where, ident, pos, label, help, type, align, options, columns, access,
	  commands, triggers, alias);
    this.number = number;
    this.count = count;
  }

  public static void generateFields(VKParseBlockContext context,
				    TokenReference where,
				    String ident,
				    int count,
				    VKPosition pos,
				    ArrayList label,
				    String help,
				    VKFieldType type,
				    int align,
				    int options,
				    VKFieldColumns columns,
				    int[] access,
				    VKCommand[] commands,
				    VKTrigger[] triggers,
				    String alias) {
    VKMultiField field;
    VKPosition   position = null;
    for (int i = 0; i < count; i++) {
      String	lab = label == null ?
	null :
	i < label.size() ? (String)label.get(i) : null; // WARNING !!!
      if (pos instanceof VKCoordinatePosition) {
	position = (VKCoordinatePosition)((VKCoordinatePosition)pos).clone();
      } else if (pos instanceof VKMultiFieldPosition) {
	position = (VKMultiFieldPosition)((VKMultiFieldPosition)pos).clone();
      }

      VKFieldColumns col = (columns != null) ? columns.cloneToPos(i + 1) : null;
      field = new VKMultiField(where, ident + (i + 1), i, count, position, lab, help, type,
			       align, options, col, access, commands, triggers, alias);

      if (field.getDetailedPosition() instanceof VKCoordinatePosition) {
	((VKCoordinatePosition)field.getDetailedPosition()).translate(i);
      } else if (field.getDetailedPosition() instanceof VKMultiFieldPosition) {
	((VKMultiFieldPosition)field.getDetailedPosition()).translate(i);
      }

      context.addField(field);
    }
  }

  /**
   * Returns the position in the array of fields
   */
  public int getPosInArray() {
    return number;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate a class for this element
   */
  public void genCode(CParseClassContext context) {
    context.addFieldDeclaration(VKUtils.buildFieldDeclaration(getTokenReference(),
							      ACC_PUBLIC | ACC_FINAL,
							      getFieldType().getDef().getDefaultType(),
							      getIdent(),
							      null));

    if (getPosInArray() == 0) {
      String		ident = getIdent().substring(0, getIdent().length() - 1);

      context.addFieldDeclaration(VKUtils.buildFieldDeclaration(getTokenReference(),
								ACC_PUBLIC | ACC_FINAL,
								new CArrayType(getFieldType().getDef().getDefaultType(), 1),
								ident,
								null));
    }
  }

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JStatement genConstructorCall() {
    JStatement		parent = super.genConstructorCall();

    if (count - 1 == number) {
      TokenReference	ref = getTokenReference();
      String		ident = getIdent().substring(0, getIdent().length() - ("" + count).length());
      JExpression[]	init = new JExpression[count];

      for (int i = 0; i < count; i++) {
	init[i] = new JFieldAccessExpression(ref, ident + (i + 1));
      }

      JStatement	stmt = VKUtils.assign(ref, ident, VKUtils.createArray(ref,
									      getFieldType().getDef().getType(),
									      init));

      return new JCompoundStatement(ref, new JStatement[] {parent, stmt});
    } else {
      return parent;
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private   int			number;
  private   int			count;
}
