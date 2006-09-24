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
   * @param pack                the package name of the class defining this object
   * @param ident		the ident
   * @param menu		the containing menu
   * @param label		the label
   * @param help		the help
   * @param key			the shortcut
   * @param icon		the icon
   */
  public VKActor(TokenReference where,
                 String pack,
                 String ident,
		 String menu,
		 String label,
		 String help,
		 String key,
		 String icon)
  {
    super(where, pack, ident);

    this.menu = menu;
    this.label = label;
    this.help = help;
    this.key = key;
    this.icon = icon;
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
  }

  /**
   * Check expression and evaluate and alter context
   * @param form	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public void checkCode(VKContext context, VKDefinitionCollector collector)
    throws PositionedError
  {
    checkKey();
    menuDef = collector.getMenuDef(menu);
    check(menuDef != null, BaseMessages.UNDEFINED_MENU, menu);
  }

  private void checkKey() throws PositionedError {
    if (key == null) {
      keyModifier = 0;
      keyCode = KeyEvent.VK_UNDEFINED;
    } else {
      String    baseKey;

      if (key.indexOf("Shift-") == -1) {
        baseKey = key;
        keyModifier = 0;
      } else {
        baseKey = key.substring(key.indexOf("Shift-") + ("Shift-").length());
        keyModifier = KeyEvent.SHIFT_MASK;
      }

      if (baseKey.equals("F1")) {
        keyCode = KeyEvent.VK_F1;
      } else if (baseKey.equals("F2")) {
        keyCode = KeyEvent.VK_F2;
      } else if (baseKey.equals("F3")) {
        keyCode = KeyEvent.VK_F3;
      } else if (baseKey.equals("F4")) {
        keyCode = KeyEvent.VK_F4;
      } else if (baseKey.equals("F5")) {
        keyCode = KeyEvent.VK_F5;
      } else if (baseKey.equals("F6")) {
        keyCode = KeyEvent.VK_F6;
      } else if (baseKey.equals("F7")) {
        keyCode = KeyEvent.VK_F7;
      } else if (baseKey.equals("F8")) {
        keyCode = KeyEvent.VK_F8;
      } else if (baseKey.equals("F9")) {
        keyCode = KeyEvent.VK_F9;
      } else if (baseKey.equals("F10")) {
        keyCode = KeyEvent.VK_F10;
      } else if (baseKey.equals("F11")) {
        keyCode = KeyEvent.VK_F11;
      } else if (baseKey.equals("F12")) {
        keyCode = KeyEvent.VK_F12;
      } else if (baseKey.equals("esc")) {
        keyCode = KeyEvent.VK_ESCAPE;
      } else {
        check(false, BaseMessages.ACTOR_INVALID_KEY, key, getIdent());
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Check expression and evaluate and alter context
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JExpression genCode(TokenReference ref) {
    int		number;

    if (getIdent().equals(VKConstants.CMD_AUTOFILL)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_AUTOFILL;
    } else if (getIdent().equals(VKConstants.CMD_NEWITEM)){
      number = com.kopiright.vkopi.lib.form.VForm.CMD_NEWITEM;
    } else if (getIdent().equals(VKConstants.CMD_EDITITEM)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_EDITITEM;
    } else if (getIdent().equals(VKConstants.CMD_SHORTCUT)) {
      number = com.kopiright.vkopi.lib.form.VForm.CMD_EDITITEM_S;
    } else {
      number = 0;
    }

    JExpression[]	exprs;

    /*
     * !!! graf 20060918 REPLACE WHEN LOCALIZED
    if (number != 0) {
      exprs = new JExpression[] {
	VKUtils.toExpression(ref, number),
        VKUtils.toExpression(ref, getSource()),
	VKUtils.toExpression(ref, menuDef.getIdent()),
	VKUtils.toExpression(ref, getIdent()),
	VKUtils.toExpression(ref, icon),
	VKUtils.toExpression(ref, keyCode),
	VKUtils.toExpression(ref, keyModifier)
      };
    } else {
      exprs = new JExpression[] {
        VKUtils.toExpression(ref, getSource()),
	VKUtils.toExpression(ref, menuDef.getIdent()),
	VKUtils.toExpression(ref, getIdent()),
	VKUtils.toExpression(ref, icon),
	VKUtils.toExpression(ref, keyCode),
	VKUtils.toExpression(ref, keyModifier)
      };
    }
    */
    if (number != 0) {
      exprs = new JExpression[] {
	VKUtils.toExpression(ref, number),
	VKUtils.toExpression(ref, menuDef.getLabel()),
	VKUtils.toExpression(ref, label),
	VKUtils.toExpression(ref, icon),
	VKUtils.toExpression(ref, keyCode),
	VKUtils.toExpression(ref, keyModifier),
	VKUtils.toExpression(ref, help)
      };
    } else {
      exprs = new JExpression[] {
	VKUtils.toExpression(ref, menuDef.getLabel()),
	VKUtils.toExpression(ref, label),
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
    p.printActor(menuDef.getIdent(), getIdent(), label, key, icon, help);
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  /**
   * !!!FIX:taoufik
   */
  public void genLocalization(VKLocalizationWriter writer) {
    writer.genActorDefinition(getIdent(), label, help);
  }

  /*!!!
  public void genLocalization() {
  
  }
   */
  
  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private final String          menu;
  private final String          label;
  private final String          help;
  private final String          key;
  private final String          icon;

  private VKMenuDefinition      menuDef;
  private int                   keyCode;
  private int                   keyModifier;
}
