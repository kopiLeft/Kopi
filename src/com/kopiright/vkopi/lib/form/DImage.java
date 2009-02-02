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

package com.kopiright.vkopi.lib.form;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import com.kopiright.vkopi.lib.visual.Utils;
import java.awt.Component;

public class DImage extends JPanel {

  
/**
   * Constructor
   */
  public DImage(String name, int border) {
    setLayout(null);
    image = com.kopiright.vkopi.lib.util.Utils.getImage(name);
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
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = 7670435167866572674L;

}
