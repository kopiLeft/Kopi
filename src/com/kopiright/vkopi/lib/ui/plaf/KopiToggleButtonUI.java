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

package com.kopiright.vkopi.lib.ui.plaf;

import java.awt.*;
import java.awt.font.*;
import java.text.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

import com.kopiright.vkopi.lib.ui.base.*;

public class KopiToggleButtonUI extends MetalToggleButtonUI {
  protected static KopiToggleButtonUI kopiButtonUI = new KopiToggleButtonUI();

  public static ComponentUI createUI(JComponent x ) {
    return kopiButtonUI;
  }

  public void installDefaults(AbstractButton b) {
    super.installDefaults(b);
    b.setBorder(new EmptyBorder(0,0,0,0));
    b.setBorderPainted(true);
    b.setRolloverEnabled(true);
  }

  public void paint(Graphics g, JComponent c) {
    AbstractButton      b = (AbstractButton) c;
    ButtonModel         model = b.getModel();
    Rectangle           rect = b.getBounds();

    b.setRolloverEnabled(true);

    super.paint(g, c);
    if (model.isRollover()) {
//       print nothing for now
//
//       rect.x = 0;
//       rect.y = 0;
//       rect.width -= 1;
//       rect.height -=1;
//       KopiUtils.drawActiveButtonBorder(g, b, rect, 
//                                        KopiTheme.USER_COLORS.COLOR_10, 
//                                        KopiTheme.USER_COLORS.COLOR_11, 
//                                        KopiTheme.USER_COLORS.COLOR_5);
    }
  }

  protected void paintFocus(Graphics g, AbstractButton b,
                            Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
  }

  Border       border = new KopiLookAndFeel.KopiButtonBorder();
}
