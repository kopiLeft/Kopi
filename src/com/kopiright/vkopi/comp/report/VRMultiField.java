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

package com.kopiright.vkopi.comp.report;

import java.util.ArrayList;
import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.*;
import com.kopiright.vkopi.comp.base.VKCommand;
import com.kopiright.vkopi.comp.base.VKTrigger;
import com.kopiright.vkopi.comp.base.VKUtils;

/**
 * This class represents an editable element of a block
 */
public class VRMultiField extends VRField {

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
  public VRMultiField(TokenReference where,
		      String ident,
		      int number,
		      int count,
		      String label,
		      String help,
		      VRFieldType type,
		      int align,
		      int options,
		      String group,
		      VKCommand[] commands,
		      VKTrigger[] triggers) {
    super(where, ident, label, help, type, align, options, group, commands, triggers);
    this.number = number;
    this.count = count;
  }

  public static void generateFields(VRParseReportContext context,
				    TokenReference where,
				    String ident,
				    int count,
				    ArrayList label,
				    String help,
				    VRFieldType type,
				    int align,
				    int options,
				    String group,
				    VKCommand[] commands,
				    VKTrigger[] triggers) {
    VRMultiField field;
    for (int i = 0; i < count; i++) {
      String	lab = label == null ?
	null :
	i < label.size() ? (String)label.get(i) : null; // WARNING !!!
      field = new VRMultiField(where, ident + (i + 1), i, count, lab, help, type,
			       align, options, group, commands, triggers);
      context.addField(field);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate a class for this element
   */
  public JFieldDeclaration genCode(CParseClassContext context) {
    if (getPosInArray() == 0) {
      String		ident = getIdent().substring(0, getIdent().length() - ("" + count).length() + 1);

      context.addFieldDeclaration(VKUtils.buildFieldDeclaration(getTokenReference(),
								ACC_PUBLIC,
								new CArrayType(getType().getDef().getReportType(), 1),
								ident,
								null));
    }

    return super.genCode(context);
  }

  /**
   * Returns the position in the array of fields
   */
  public int getPosInArray() {
    return number;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private   int			number;
  private   int			count;
}
