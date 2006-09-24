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

package com.kopiright.vkopi.comp.base;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents the root class all definition
 * Definitions are identified by a name, and search for a definition is
 * made bottom-up allowing to redefine global definition
 */
public abstract class VKDefinition extends VKPhylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Create a new block element
   *
   * @param where		the token reference of this node
   * @param pack                the package name of the class defining this object
   * @param ident		the name of this definition
   */
  public VKDefinition(TokenReference where, String pack, String ident) {
    super(where);

    this.source = pack == null ? null : pack + "/" + where.getName().substring(0, where.getName().lastIndexOf('.'));
    this.ident = ident == null ? ("SYNTHETIC_IDENT_" + syntheticIdentCounter++) : ident;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public abstract void checkCode(VKContext context) throws PositionedError;

  // ----------------------------------------------------------------------
  // ACCCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the identifier associated with this phylum
   */
  public String getIdent() {
    return ident;
  }

  /**
   * Returns the source path containing the current definition
   */
  public String getSource() {
    return source;
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public abstract void genLocalization(VKLocalizationWriter writer);

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private static int            syntheticIdentCounter = 0;

  private final String          source;
  private final String          ident;
}
