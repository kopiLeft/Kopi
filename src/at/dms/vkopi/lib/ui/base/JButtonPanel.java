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

package at.dms.vkopi.lib.ui.base;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class JButtonPanel extends JPanel  {
  public JButtonPanel() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    //    setBorder(new LineBorder(borderColor));
    setBackground(backColor);
    setOpaque(true);
    setBorder(new PanelBorder(borderColor));
    setAlignmentY(0);
  }

  static class PanelBorder extends LineBorder {

    public PanelBorder(Color color)  {
      super(color, 1, true);
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      int i;
      
      g.setColor(borderColor);
      g.drawLine(x, y, width, y);
      g.drawLine(x, height-1, width, height-1);
      g.setColor(oldColor);
    }
  }

  private static final Color    borderColor = UIManager.getColor("ButtonPanel.border");
  private static final Color    backColor = UIManager.getColor("ButtonPanel.back");
}
