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
 * $Id: KnownBugs.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.util;

import java.awt.event.KeyEvent;
import java.awt.MediaTracker;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 */
public class KnownBugs  {
  /**
   * 2003.08.14; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * Ab jdk 1.4.2 gibt es auch die option -XX:MinHeapFreeRatio=0
   */
  public static void freeMemory() {
    System.gc();
  }

  /**
   * 2003.08.14; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that a failure with paintIcon happend
   */
  public static boolean paintIconFailure;
  /**
   * 2003.09.23; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that an Icon was not correctly loaded
   */
  public static String paintIconReload;

  /**
   * 2003.09.23; jdk 1.4.1; Wischeffekt, Speicherverbrauch
   * save that a failure with paintIcon happend
   */
  public static void loadImage(Icon icon) {
    if (icon instanceof ImageIcon) {
      ImageIcon       imageIcon = ((ImageIcon) icon);

      if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
        // reload image
        imageIcon.setImage(imageIcon.getImage());
        paintIconReload = imageIcon.toString()+"  status now: "+imageIcon.getImageLoadStatus()+" (ok if 8) ";
      }
    }
  }
}
