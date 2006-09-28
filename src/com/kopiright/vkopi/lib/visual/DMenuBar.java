/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.vkopi.lib.visual;

import java.util.Vector;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.kopiright.vkopi.lib.util.Message;

public class DMenuBar extends JMenuBar {

  /**
   * Constructs a menu panel
   */
  /*package*/ DMenuBar() {
    setBorderPainted(false);
    setFocusable(false);
  }

  /**
   * Adds a menu item
   */
  DMenuItem addItem(SActor actor) { //, DActor dactor) {
    JMenu		menu = null;
    DMenuItem		item;

    //!!! NOT YET IMPLEMENTED IN SWING
    if (actor.getMenuIdent().equals("Help")) {
      if ((menu = getHelpMenu()) == null) {
	menu = new JMenu(actor.menuName);
	setHelpMenu(menu);
      }
    } else {
      /* lookup menu with name menuName, add it if necessary */
      for (int i = 0; menu == null && i < getMenuCount(); i++) {
	if (getMenu(i).getText().equals(actor.menuName)) {
	  menu = getMenu(i);
	}
      }

      if (menu == null) {
	menu = this.add(new JMenu(actor.menuName));
      }
    }

    /* add menu item */
    item = (DMenuItem) menu.add(new DMenuItem(actor.getAction())); //actor.menuItem));

    return item;
  }

  public void addSeparator(String menuName) {
    JMenu		menu = null;

    //!!! NOT YET IMPLEMENTED IN SWING
    if (menuName.equals(Message.getMessage("help-menu"))) {
      if ((menu = getHelpMenu())== null) {
	menu = new JMenu(menuName);
	setHelpMenu(menu);
      }
    } else {
      for (int i = 0; menu == null && i < getMenuCount(); i++) {
	if (getMenu(i).getText().equals(menuName)) {
	  menu = getMenu(i);
	}
      }

      if (menu == null) {
	menu = this.add(new JMenu(menuName));
      }
    }

    menu.addSeparator();
  }

  // !!! TO BE REMVED WHEN SWING WILL IMPLEMENT IT
  public void setHelpMenu(JMenu menu) {
    help = menu;
  }
  public JMenu getHelpMenu() {
    return help;
  }
  public void finalizeMenu() {
    if (getHelpMenu() != null) {
      this.add(getHelpMenu());
    }
  }

  // ---------------------------------------------------------------------
  // ACTION LISTENER PANEL CLASS
  // ---------------------------------------------------------------------

  private JMenu 	help;
  private Vector 	listeners;
}
