/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.visual;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * STATE PANEL CLASS
 */
/*package*/ class DStatePanel extends JPanel {

  public DStatePanel() {
    setFocusable(false);
    setOpaque(true);
    setLayout(new BorderLayout());

    setUserInterrupt(false);
  }

  public void setInfo(JPanel info) {
    if (this.info != null) {
      remove(info);
    }
    this.info = info;
    add(info, BorderLayout.CENTER);
  }

  /**
   * set the info panel that current process accept user interrupt
   */
  public void setUserInterrupt(boolean allowed) {
    userInterruptAllowed = allowed;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean		userInterruptAllowed;
  private JPanel		info;
}
