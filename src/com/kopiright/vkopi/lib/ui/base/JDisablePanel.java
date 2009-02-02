/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.base;

import java.awt.*;
import javax.swing.JPanel;

public class JDisablePanel extends JPanel  {
  
public JDisablePanel() {
    super();
  }

  public void paint(Graphics g) {
    final Rectangle     b ;
    final Color         bgc;

    bgc = g.getColor();
    b = getBounds();
    g.setColor(Color.white);
    for (int i=0; i < b.height; i += 5) {
      g.drawLine(b.x, i, b.width, i + b.width);
    }
    for (int i=5; i < b.width; i += 5) {
      g.drawLine(i, b.y, i + b.height, b.height);
    }
    g.setColor(bgc);
  } 
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 8258783355588510649L;
}
