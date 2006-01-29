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

import java.awt.event.KeyEvent;

import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JUnqualifiedInstanceCreation;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents an actor, ie a menu element with a name and may be an icon, a shortcut
 * and a help
 */
public class VKActor extends VKDefinition {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a menu element
   * @param where		the token reference of this node
   * @param ident		the menu item ident
   * @param menu		the menu name
   * @param item		the item name
   * @param icon		the icon name
   * @param key			the shorcut
   * @param help		the help
   */
  public VKActor(TokenReference where,
		 String ident,
		 String menu,
		 String item,
		 String icon,
		 String key,
		 String help) {
    super(where, help, ident);

    this.menu	= menu;
    this.item	= item;
    this.icon	= icon;
    this.key	= key;
    this.help	= help;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context) throws PositionedError {
    if (key == null) {
      return;
    }

    // $$$ use a static hashtable
    String	s = key;
    int	shiftIndex = s.indexOf("Shift-");
    if (shiftIndex != -1) {
      s = s.substring(shiftIndex + ("Shift-").length());
      keyModifier = java.awt.event.KeyEvent.SHIFT_MASK;
    }

    if (s.equals("F1")) {
      keyCode = java.awt.event.KeyEvent.VK_F1;
    } else if (s.equals("F2")) {
      keyCode = java.awt.event.KeyEvent.VK_F2;
    } else if (s.equals("F3")) {
      keyCode = java.awt.event.KeyEvent.VK_F3;
    } else if (s.equals("F4")) {
      keyCode = java.awt.event.KeyEvent.VK_F4;
    } else if (s.equals("F5")) {
      keyCode = java.awt.event.KeyEvent.VK_F5;
    } else if (s.equals("F6")) {
      keyCode = java.awt.event.KeyEvent.VK_F6;
    } else if (s.equals("F7")) {
      keyCode = java.awt.event.KeyEvent.VK_F7;
    } else if (s.equals("F8")) {
      keyCode = java.awt.event.KeyEvent.VK_F8;
    } else if (s.equals("F9")) {
      keyCode = java.awt.event.KeyEvent.VK_F9;
    } else if (s.equals("F10")) {
      keyCode = java.awt.event.KeyEvent.VK_F10;
    } else if (s.equals("F11")) {
      keyCode = java.awt.event.KeyEvent.VK_F11;
    } else if (s.equals("F12")) {
      keyCode = java.awt.event.KeyEvent.VK_F12;
    } else if (s.equals("esc")) {
      keyCode = java.awt.event.KeyEvent.VK_ESCAPE;
    } else {
      check(false, BaseMessages.MENU_UNDEFINED_KEY, s);
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode(TokenReference ref, int notused) {
    int		number = 0;
    if (getIdent().equals(VKConstants.CMD_AUTOFILL)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_AUTOFILL;
    } else if (getIdent().equals(VKConstants.CMD_NEWITEM)){
      number = com.kopiright.vkopi.lib.form.VForm.CMD_NEWITEM;
    } else if (getIdent().equals(VKConstants.CMD_EDITITEM)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_EDITITEM;
    } else if (getIdent().equals(VKConstants.CMD_SHORTCUT)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_EDITITEM_S;
    }

    JExpression[]	exprs;
    if (number != 0) {
      exprs = new JExpression[] {
	VKUtils.toExpression(ref, number),
	VKUtils.toExpression(ref, menu),
	VKUtils.toExpression(ref, item),
	VKUtils.toExpression(ref, icon),
	VKUtils.toExpression(ref, keyCode),
	VKUtils.toExpression(ref, keyModifier),
	VKUtils.toExpression(ref, help)
      };
    } else {
      exprs = new JExpression[] {
	VKUtils.toExpression(ref, menu),
	VKUtils.toExpression(ref, item),
	VKUtils.toExpression(ref, icon),
	VKUtils.toExpression(ref, keyCode),
	VKUtils.toExpression(ref, keyModifier),
	VKUtils.toExpression(ref, help)
      };
    }
    return new JUnqualifiedInstanceCreation(ref,
				    number == 0 ? VKStdType.SActor : VKStdType.SDefaultActor,
				    exprs);

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
    p.printActor(menu, getIdent(), item, key, icon, help);
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final String		menu;
  private final String		item;
  private final String		icon;
  private final String		key;
  private final String		help;
  private	int		keyModifier;
  private	int		keyCode = KeyEvent.VK_UNDEFINED;
}
