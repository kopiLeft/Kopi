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

import javax.swing.JLabel;

/**
 * INFOPANEL CLASS
 */
/*package*/ class DInfoPanel extends JLabel {

  /**
   * Constructs the information panel
   */
  public DInfoPanel() {
    super("\"visualKopi\" kopiRight Managed Solutions GmbH");
    setFocusable(false);
  }

  public void setText(String text) {
    setText(text, false);
  }

  public void setText(String text, boolean highlight) {
    final String currentText = getText();

    if (currentText != null && !currentText.equals(text)) {
      super.setText(text);
    }
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------
}



