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

import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.vkopi.comp.base.VKType;
import com.kopiright.vkopi.comp.base.VKPrettyPrinter;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

public class VKAliasType extends VKFieldType {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * This is a link to a field definition
   *
   * @param where		the token reference of this node
   */
  public VKAliasType(TokenReference where, String name) {
    super(where);

    this.name = name;
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
  public void checkCode(VKContext context, VKField field) throws PositionedError {
    VKField	alias = field.getBlock().getField(name);
    check(alias != null, FormMessages.FIELD_NOT_FOUND, name);
    def = alias.getFieldType().getDef();
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
    p.printAlias(name);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VKType		def;
  private String		name;
}
