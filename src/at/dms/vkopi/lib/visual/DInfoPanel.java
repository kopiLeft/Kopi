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
 * $Id: DInfoPanel.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.visual;

import java.awt.Color;
import javax.swing.JLabel;

/**
 * INFOPANEL CLASS
 */
/*package*/ class DInfoPanel extends JLabel {

  /**
   * Constructs the information panel
   */
  public DInfoPanel() {
    super("\"Visual Kopi\" DMS Decision Management Systems GmbH");
    setFocusable(false);
//     setFont(DObject.FNT_INFO);
//     setForeground(DObject.CLR_FOREGROUND);
  }

  public void setText(String text) {
    setText(text, false);
  }

  public void setText(String text, boolean highlight) {
    //    setForeground(highlight ? DObject.CLR_HIGHLIGHT : DObject.CLR_FOREGROUND);
    final String currentText = getText();
    if (currentText != null && !currentText.equals(text)) {
      super.setText(text);
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
}



