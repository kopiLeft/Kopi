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
 * $Id: KopiTextFieldUI.java,v 1.2 2004/09/30 09:55:21 lackner Exp $
 */

package at.dms.vkopi.lib.ui.plaf;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

import at.dms.vkopi.lib.ui.base.*;

public class KopiTextFieldUI extends MetalTextFieldUI {

  public static ComponentUI createUI(JComponent c) {
    return new KopiTextFieldUI();
  }

  public void installUI(JComponent c) {
    super.installUI(c);

    c.setBorder(new KopiUtils.KopiFieldBorder((JTextComponent) c));
  }
  /**
   * Paints a background for the view.  This will only be
   * called if isOpaque() on the associated component is
   * true.  The default is to paint the background color 
   * of the component.
   *
   * @param g the graphics context
   */
  protected void paintBackground(Graphics g) {
    JTextComponent c = (JTextComponent) getComponent();

    KopiUtils.drawBackground(g, c, new Rectangle(0, 0, c.getWidth(), c.getHeight()));
  }
}
