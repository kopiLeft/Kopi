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
 * $Id: DImage.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import at.dms.vkopi.lib.visual.Utils;
import at.dms.vkopi.lib.visual.DObject;

import java.awt.Component;

public class DImage extends JPanel {

  /**
   * Constructor
   */
  public DImage(String name, int border) {
    setLayout(null);
    image = Utils.getImage(name);
    label = new JLabel();
    label.setIcon(image);
    add(label);
    label.setLocation(2, 2);
    setBorder(border);
  }

  private void setBorder(int style) {
    switch (style) {
 //    case VConstants.BRD_NONE:
//       setBorder(DObject.BRD_EMPTY);
//       break;
//     case VConstants.BRD_LINE:
//       setBorder(DObject.BRD_LINE);
//       break;
//     case VConstants.BRD_RAISED:
//       setBorder(DObject.BRD_RAISED);
//       break;
//     case VConstants.BRD_LOWERED:
//       setBorder(DObject.BRD_LOWERED);
//       break;
//     case VConstants.BRD_ETCHED:
//       setBorder(DObject.BRD_ETCHED);
//       break;
//     default:
//       break;
    }
  }

  public void setLocation(int left, int top) {
    super.setLocation(left, top);
  }

  public Component getComponent() {
    return this;
  }

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

  private ImageIcon	image;
  private JLabel        label;
}
