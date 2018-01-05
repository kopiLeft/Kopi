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

import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;

/**
 * This class represents a message characterized by its identifier and content.
 */
public class VKMessageDefinition extends VKDefinition {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Construct a message element
   * @param where               the token reference of this node
   * @param pack                the package name of the class defining this object
   * @param ident               the ident
   * @param text                the message text
   */
  public VKMessageDefinition(TokenReference where,
                             String pack,
                             String ident,
                             String text)
  {
    super(where, pack, ident);
    this.text = text;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  public void checkCode(VKContext context) throws PositionedError {
    //!!! never called
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  public void genLocalization(VKLocalizationWriter writer) {
    writer.genMessageDefinition(getIdent(), text);
  }

  // ----------------------------------------------------------------------
  // VK CODE GENERATION
  // ----------------------------------------------------------------------

  public void genVKCode(VKPrettyPrinter p) {
    genComments(p);
    p.printMessageDefinition(getIdent(), text);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final String                  text;
}
