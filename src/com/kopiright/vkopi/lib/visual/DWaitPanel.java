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

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * WAIT PANEL CLASS
 */
/*package*/ class DWaitPanel extends JProgressBar {

  public DWaitPanel(DWindow parent) {
    this.parent = parent;

    //    setBorder(null);
    setPreferredSize(new Dimension(40, getPreferredSize().height));
    setFocusable(false);
    addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          DWaitPanel.this.parent.getModel().getDBContext().getDefaultConnection().interrupt();
        }
      });
    //    setWaiting(false);
  }

  /**
   * Starts or stops the animation.
   *
   * @param	waiting		true to start the animation, false to stop it.
   */
  public void setWaiting(boolean waiting) {
    if (!SwingUtilities.isEventDispatchThread()) {
      Thread.dumpStack();
      System.err.println("ERROR: DWaitPanel.setWaiting(..) calles outside of Eventdispatching Thread");
    }

    if (waiting != isIndeterminate()) {
      setIndeterminate(waiting);
    }
    setVisible(waiting);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final DWindow         parent;
}
