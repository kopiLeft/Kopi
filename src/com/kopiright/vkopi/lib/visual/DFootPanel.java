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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.kopiright.vkopi.lib.ui.plaf.KopiLookAndFeel;

/**
 * An information panel displayed at the foot of every window.
 */
public final class DFootPanel extends JPanel {

  /**
   * Constructs a foot panel
   */
  public DFootPanel(DWindow parent) {
    JPanel	east = new JPanel();

    setLayout(new BorderLayout());
    setFocusable(false);
    setBorder(new KopiLookAndFeel.TopLineBorder());

    infoPanel = new DInfoPanel();
    add(infoPanel, BorderLayout.CENTER);	// will have maximum size

    statePanel = new DStatePanel();
    waitPanel = new DWaitPanel(parent);

    east.setLayout(new BoxLayout(east, BoxLayout.X_AXIS));
    east.add(statePanel);
    east.add(waitPanel);

    add(east, BorderLayout.EAST);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * setInformationText
   */
  public void setInformationText(String message) {
    infoPanel.setText(message, false);
    waitPanel.setWaiting(false);
  }

  /**
   * change mode to wait state
   */
  public void setWaitInfo(String message) {
    if (!SwingUtilities.isEventDispatchThread()) {
      System.err.println("ERROR: DFootPanel.setWaitInfo(..) calles outside of Eventdispatching Thread");
    }

    oldMessage = infoPanel.getText();
    if (message != null) {
      infoPanel.setText(message, true);
    }
    waitPanel.setWaiting(true);
  }

  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  public void setStatePanel(JPanel state) {
    statePanel.setInfo(state);
  }

  /**
   * change mode to free state
   */
  public void unsetWaitInfo() {
    if (!SwingUtilities.isEventDispatchThread()) {
      System.err.println("ERROR: DFootPanel.unsetWaitInfo() calles outside of Eventdispatching Thread");
    }

    infoPanel.setText(oldMessage, false);
    waitPanel.setWaiting(false);
  }

  /**
   * set the info panel that current process accept user interrupt
   */
  public void setUserInterrupt(boolean allowed) {
    statePanel.setUserInterrupt(allowed);
  }

  public Dimension getMaximumSize() {
    Dimension		prf = super.getPreferredSize();
    Dimension		max = super.getMaximumSize();

    return new Dimension(max.width, Math.min(prf.height, max.height));
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private DWaitPanel		waitPanel;
  private DInfoPanel		infoPanel;
  private DStatePanel		statePanel;
  private String		oldMessage;
}
