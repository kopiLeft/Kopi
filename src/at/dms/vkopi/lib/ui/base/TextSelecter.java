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
 * $Id: TextSelecter.java,v 1.1 2004/09/30 09:55:21 lackner Exp $
 */

package at.dms.vkopi.lib.ui.base;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.JTextComponent;

public class TextSelecter extends FocusAdapter {

  public void focusGained(FocusEvent e) {
    // Select all, but put caret at first position
    ((JTextComponent) e.getComponent()).getCaret().setDot(((JTextComponent) e.getComponent()).getText().length());
    ((JTextComponent) e.getComponent()).getCaret().moveDot(0);
  }  

  public static final  TextSelecter   TEXT_SELECTOR = new TextSelecter(); 
}


