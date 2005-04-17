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

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

public class JFieldButton extends JButton {

  /**
   * Constructor
   */
  public JFieldButton(ImageIcon icon) {
    super(null, icon);
    setFocusable(false);
    setMargin(new Insets(0, 0, 0, 0));
    setPreferredSize(new Dimension(19, 19));
    setMaximumSize(new Dimension(19, 19));
    setBorder(border);
  }

  static class ButtonLineBorder extends LineBorder {

    public ButtonLineBorder(Color color, Color disabledColor)  {
      super(color, 1, true);

      this.disabledColor = disabledColor;
    }    

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      int i;
      
      if (c.isEnabled()) {
        g.setColor(lineColor);
      } else {
        g.setColor(disabledColor);
      }
      g.drawRoundRect(x, y, width-1, height-1, border_arc, border_arc);
      g.setColor(oldColor);
    }

    private Color       disabledColor;
  }

  private static final Color    border_color = UIManager.getColor("FieldButton.border.color");
  private static final Color    border_disabled = UIManager.getColor("FieldButton.border.disabled");
  private static final int      border_arc = UIManager.getInt("FieldButton.border.arc");

  private static ButtonLineBorder  border = new ButtonLineBorder(border_color, border_disabled);
}

