/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: DMenuItem.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.visual;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.MenuSelectionManager;

/**
 * This subclass of JMenuItem redefines setAccelerator to enable unsetting
 * of key accelerators.
 */

public class DMenuItem extends JMenuItem {
  /**
   * Creates a menu item with text.
   */
  public DMenuItem(Action action) {//String text) {
    super(action);
    setIcon(null);
  }

  /**
   * Sets the key combination which invokes the Menu Item's
   * action listeners without navigating the menu hierarchy
   *
   * This method redefines setAccelerator in JMenuItem to allow
   * unsetting of the accelerator.
   */
  public void setAccelerator(KeyStroke keyStroke) {
    if (accelerator != null) {
      unregisterKeyboardAction(accelerator);
    }

    if (keyStroke != null) {
      registerKeyboardAction(new AbstractAction() {
	public void actionPerformed(ActionEvent e) {
	  MenuSelectionManager.defaultManager().clearSelectedPath();
	  doClick();
	}
      }, keyStroke, WHEN_IN_FOCUSED_WINDOW);
    }

    this.accelerator = keyStroke;
  }

  /**
   * Returns the KeyStroke which serves as an accelerator
   * for the menu item.
   *
   * This method redefines getAccelerator in JMenuItem because
   * accelerator is a private member of JMenuItem
   */
  public KeyStroke getAccelerator() {
    return this.accelerator;
  }

  private KeyStroke	accelerator;
}
