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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.UIManager;
import javax.swing.AbstractAction;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JMenuButton extends JButton {

 /**
   * Constructor
   */
  public JMenuButton(Action action) {
    super(action);
    if (!UIManager.getBoolean("MenuButton.text.enabled")){
      setText(null);
    }
    setMargin(inset);
    setVerticalTextPosition(BOTTOM);
    setHorizontalTextPosition(CENTER);
    setRolloverEnabled(true);
    setPreferredSize(getStaticSize(UIManager.getBoolean("MenuButton.text.enabled")));
    setSize(getPreferredSize());
    setFocusable(false);
  }

  public JToolTip createToolTip() {
    at.dms.vkopi.lib.util.MultiLineToolTip tip = new at.dms.vkopi.lib.util.MultiLineToolTip();
    tip.setComponent(this);
    return tip;
  }

  public Dimension getPreferredSize() {
    return getStaticSize(getText() != null);
  }
  public Dimension getMinimumSize() {
    return getStaticSize(getText() != null);
  }
  public Dimension getMaxmumSize() {
    return getStaticSize(getText() != null);
  }

  public void setEnabled(boolean enabled) {

    if (!enabled) {
      getModel().setRollover(false);
    }
    super.setEnabled(enabled);
  }

  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, 1, width, width-2);
  }


  public static Dimension getStaticSize(boolean showtext) {
    return showtext ? dimension : dimensionSmall;
  }

//   public int getAcceleratorModifier() {
//     return accMod;
//   }

//   public int getAcceleratorKey() {
//     return accKey;
//   }

//   private int           accKey = 0;
//   private int           accMod = 0;

  private static Dimension	dimension = new Dimension(52, 52);
  private static Dimension	dimensionSmall = new Dimension(40, 40);
  private static Insets		inset = new Insets(1, 1, 1, 1);
}

