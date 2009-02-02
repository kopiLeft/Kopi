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

package com.kopiright.vkopi.comp.print;

import java.util.Hashtable;
import java.util.Vector;

import com.kopiright.vkopi.comp.base.VKDefinitionCollector;
import com.kopiright.vkopi.comp.base.VKDefinition;
import com.kopiright.vkopi.comp.base.VKContext;
import com.kopiright.compiler.base.PositionedError;

/**
 * The compilation unit for a PR element
 */
public class PRDefinitionCollector extends VKDefinitionCollector {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a wrapper to all definitions
   */
  public PRDefinitionCollector(String[] insertDirectories) {
    super(insertDirectories);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * search the Style in compilation unit
   */
  public PRStyle getStyleDef(String name) {
    Object	obj = styles.get(name);
    if (obj != null) {
      return (PRStyle)obj;
    }
    for (int i = inserts.length - 1; i >= 0; i--) {
      obj = ((PRDefinitionCollector)inserts[i]).getStyleDef(name);
      if (obj != null) {
	styles.put(name, obj);
	return (PRStyle)obj;
      }
    }
    return null;
  }

  /**
   * add new style on top of definition for name
   */
  public void addStyleDef(VKDefinition style) {
    styles_V.addElement(style);
    styles.put(style.getIdent(), style);
  }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Insert all local definition to the class declaration
   */
  public void checkCode(VKContext context) throws PositionedError {
    super.checkCode(context);
    // !!!checkCode(styles_V, insert);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Vector			styles_V	= new Vector();
  private Hashtable			styles		= new Hashtable();
}
