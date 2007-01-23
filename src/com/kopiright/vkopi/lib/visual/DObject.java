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

package com.kopiright.vkopi.lib.visual;

import java.awt.Frame;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Class with only static methods to cache the properties of a Visual
 * Kopi application
 */
public class DObject {
  public static Image	windowIcon;

  // --------------------------------------------------------------------
  // PUBLIC CONSTANTS
  // --------------------------------------------------------------------

  public static final Frame     phantom = new Frame();

  static {
    ImageIcon		icon = com.kopiright.vkopi.lib.util.Utils.getImage("window.gif");
    windowIcon = icon.getImage();
    if (windowIcon.getHeight(null) <= 0) {
      windowIcon = null;
    }
  }
}
