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
 * $Id: PRDefinitionCollector.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.comp.print;

import java.util.Hashtable;
import java.util.Vector;

import at.dms.vkopi.comp.base.VKDefinitionCollector;
import at.dms.vkopi.comp.base.VKDefinition;
import at.dms.vkopi.comp.base.VKContext;
import at.dms.compiler.base.PositionedError;

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
