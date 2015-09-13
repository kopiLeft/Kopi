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

package com.kopiright.vkopi.comp.chart;

import java.util.ArrayList;

import com.kopiright.compiler.base.TokenReference;
import com.kopiright.kopi.comp.kjc.CArrayType;
import com.kopiright.kopi.comp.kjc.CParseClassContext;
import com.kopiright.kopi.comp.kjc.JFieldDeclaration;
import com.kopiright.vkopi.comp.base.VKCommand;
import com.kopiright.vkopi.comp.base.VKTrigger;
import com.kopiright.vkopi.comp.base.VKUtils;

public class VCMultiMeasure extends VCMeasure {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  /**
   * Creates a new multiple chart field instance.
   * @param where The token reference.
   * @param ident The field identifier.
   * @param number The field number.
   * @param count The fields count.
   * @param label The field label.
   * @param help The field help.
   * @param type The field type.
   * @param commands The field commands.
   * @param triggers The field triggers.
   */
  public VCMultiMeasure(TokenReference where,
                        String ident,
                        int number,
                        int count,
                        String label,
                        String help,
                        VCFieldType type,
                        VKCommand[] commands,
                        VKTrigger[] triggers)
  {
    super(where, ident, label, help, type, commands, triggers);
    this.number = number;
    this.count = count;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------

  /**
   * Generates the inner fields for this multiple field.
   * @param where The token reference.
   * @param ident The field identifier.
   * @param number The field number.
   * @param count The fields count.
   * @param label The field label.
   * @param help The field help.
   * @param type The field type.
   * @param commands The field commands.
   * @param triggers The field triggers.
   */
  public static void generateFields(VCParseChartContext context,
				    TokenReference where,
				    String ident,
				    int count,
				    ArrayList<String> label,
				    String help,
				    VCFieldType type,
				    VKCommand[] commands,
				    VKTrigger[] triggers) {
    VCMultiMeasure 		field;
    
    for (int i = 0; i < count; i++) {
      String	lab = label == null ?
	null :
	i < label.size() ? (String)label.get(i) : null; // WARNING !!!
      field = new VCMultiMeasure(where, ident + (i + 1), i, count, lab, help, type, commands, triggers);
      context.addField(field);
    }
  }

  /**
   * Generate a class for this element
   */
  public JFieldDeclaration genCode(CParseClassContext context) {
    if (getPosInArray() == 0) {
      String		ident = getIdent().substring(0, getIdent().length() - ("" + count).length() + 1);

      context.addFieldDeclaration(VKUtils.buildFieldDeclaration(getTokenReference(),
								ACC_PUBLIC,
								new CArrayType(getType().getDef().getMeasureChartType(), 1),
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

  private   int					number;
  private   int					count;
}
