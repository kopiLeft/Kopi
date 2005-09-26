/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.form;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class Environment {
  
  public void addDefaultTextKey(JComponent comp, boolean isMulti) {
    addKeyAction(comp, KeyNavigator.KEY_NEXT_BLOCK);
    addKeyAction(comp, KeyNavigator.KEY_EMPTY_FIELD);
    addKeyAction(comp, KeyNavigator.KEY_DIAMETER);
    addKeyAction(comp, KeyNavigator.KEY_REC_DOWN);
    addKeyAction(comp, KeyNavigator.KEY_REC_FIRST);
    addKeyAction(comp, KeyNavigator.KEY_REC_LAST);
    addKeyAction(comp, KeyNavigator.KEY_REC_UP);
    addKeyAction(comp, KeyNavigator.KEY_PREV_FIELD);
    addKeyAction(comp, KeyNavigator.KEY_NEXT_FIELD);
    addKeyAction(comp, KeyNavigator.KEY_ESCAPE);
    addKeyAction(comp, KeyNavigator.KEY_PREV_VAL);
    addKeyAction(comp, KeyNavigator.KEY_NEXT_VAL);
    addKeyAction(comp, KeyNavigator.KEY_PRINTFORM);

    addKey(comp, KeyNavigator.KEY_EMPTY_FIELD, KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_NEXT_BLOCK, KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_DIAMETER, KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_REC_DOWN, KeyEvent.VK_PAGE_DOWN, 0);
    addKey(comp, KeyNavigator.KEY_REC_DOWN, KeyEvent.VK_PAGE_DOWN, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_REC_FIRST, KeyEvent.VK_HOME, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_REC_LAST, KeyEvent.VK_END, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_REC_UP, KeyEvent.VK_PAGE_UP, 0);
    addKey(comp, KeyNavigator.KEY_REC_UP, KeyEvent.VK_PAGE_UP, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_PREV_FIELD, KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_PREV_FIELD, KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_PREV_FIELD, KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_TAB, 0);
    addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_PRINTFORM, KeyEvent.VK_PRINTSCREEN, KeyEvent.SHIFT_DOWN_MASK);
    // the magnet card reader sends a CNTR-J as last character
    addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK);

    addKey(comp, KeyNavigator.KEY_ESCAPE, KeyEvent.VK_ESCAPE, 0);
    addKey(comp, KeyNavigator.KEY_NEXT_VAL, KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK);
    addKey(comp, KeyNavigator.KEY_PREV_VAL, KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK);

    if (!isMulti) {
      // In multiline fields these keys are used for other stuff
      addKey(comp, KeyNavigator.KEY_PREV_FIELD, KeyEvent.VK_UP, 0);
      addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_DOWN, 0);
      addKey(comp, KeyNavigator.KEY_NEXT_FIELD, KeyEvent.VK_ENTER, 0);
    }

  }

  protected void addKeyAction(JComponent comp, int code){
    comp.getActionMap().put("KopiAction" + code, KeyNavigator.getKeyNavigator(code));
  }

  /**
   *
   */
  protected void addKey(JComponent comp, int code, int key, int mod) {
    KeyStroke	keyStroke = KeyStroke.getKeyStroke(key, mod);

    comp.getInputMap().put(keyStroke, "KopiAction" + code);
  }

  public boolean setTextOnFieldLeave() {
    return false;
  }

  public boolean forceCheckList() {
    return true;
  }
}
