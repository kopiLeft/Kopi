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
 * $Id: JDisablePanel.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.ui.base;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

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
}
