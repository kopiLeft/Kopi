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
 * $Id: SplashScreen.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

// THANKS TO KIWI FOR THE CODE: PING Software Group
// !!! (add to resources)

package at.dms.vkopi.lib.visual;

import java.awt.*;

public class SplashScreen extends Window {

  /** Construct a new <code>SplashScreen</code>.
   *
   * @param image The image to display in the splash screen.
   * @param caption A short text caption to display below the image (may be
   * <b>null</b>).
   */
  public SplashScreen(Image image, String caption) {
    super(DObject.phantom);
    this.image = image;
    this.caption = caption;
  }

  /**
   * Paint the splash screen.
   */
  public void paint(Graphics gc) {
    Dimension size = getSize();
    FontMetrics fm = gc.getFontMetrics();

    gc.setColor(Color.black);
    gc.drawRect(0, 0, size.width - 1, size.height - 1);
    gc.drawImage(image, 1, 1, null);

    if (caption != null) {
      int y = image.getHeight(null) + 2 + fm.getAscent();
      int x = (size.width - fm.stringWidth(caption)) / 2;

      gc.setColor(getForeground());
      gc.drawString(caption, x, y);
    }
  }

  /**
   * Display or hide the splash screen. The splash screen is displayed on the
   * desktop, centered on the screen. Although this method returns
   * immediately, the splash screen remains on the desktop for the duration
   * of the time delay, or indefinitely if the delay was set to 0.
   */
  public void setVisible(boolean flag) {
    if (flag) {
      pack();
      centerWindow(this);
    }
    super.setVisible(flag);
  }

  /**
   * Get the splash screen's preferred size.
   *
   * @return The preferred size of the component.
   */
  public Dimension getPreferredSize() {
    FontMetrics fm = getGraphics().getFontMetrics();
    Dimension d = new Dimension(image.getWidth(null) + 2,
				image.getHeight(null) + 2);
    if (caption != null) {
      d.height += fm.getHeight() + 2;
    }

    return d;
  }

  public static final void centerWindow(Window w) {
    Dimension s_size, w_size;
    int x, y;

    s_size = w.getToolkit().getScreenSize();
    w_size = w.getSize();

    x = (s_size.width - w_size.width) / 2;
    y = (s_size.height - w_size.height) / 2;

    w.setLocation(x, y);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Image		image;
  private String	caption;
}
