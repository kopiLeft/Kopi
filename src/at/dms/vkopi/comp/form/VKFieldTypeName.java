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
 * $Id: VKFieldTypeName.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.comp.form;

import at.dms.vkopi.comp.base.*;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * A field type defined by a name
 */
public class VKFieldTypeName extends VKFieldType implements at.dms.vkopi.lib.form.VConstants {

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
	actionNumber = commandable.addTrigger(type.genCall(params), 0, at.dms.vkopi.lib.form.VConstants.TRG_OBJECT);
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
