/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import com.kopiright.vkopi.comp.base.*;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * A field type defined by a name
 */
public class VKFieldTypeName extends VKFieldType implements com.kopiright.vkopi.lib.form.VConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a link to a field definition
   *
   * @param where		the token reference of this node
   */
  public VKFieldTypeName(TokenReference where, String ident, JExpression[] params) {
    super(where);
    this.ident = ident;
    this.params = params;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the type definition
   */
  public VKType getDef() {
    verify(def != null);
    return def;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKField field, Commandable commandable) throws PositionedError {
    VKDefinitionCollector coll = field.getBlock().getDefinitionCollector();
    type = coll.getFieldTypeDef(ident);
    check(type != null, BaseMessages.UNDEFINED_TYPE, ident);
    type.checkCode(context);
    def = type.getDef();

    if (params == null) {
      super.checkCode(context, field, commandable);
    } else {
      checkAutofills(context, field, commandable);
      getDef().checkCode(context);
      if (getDef().getList() != null) {
	actionNumber = commandable.addTrigger(type.genCall(params), 0, com.kopiright.vkopi.lib.form.VConstants.TRG_OBJECT);
      }
    }
  }

  public void genCode(Commandable commandable) {
    if (getDef().getList() != null) {
      actionNumber = commandable.addTrigger(type.genCall(params), 0, TRG_VOID);
    }
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
    /*
    genComments(p);
    p.printFieldTypeName(ident, params);
    */
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		ident;
  private VKType		def;
  private JExpression[]		params;
  private VKTypeDefinition	type;
}
