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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * The position panel is the used to go trough the rows when fetching
 * several rows. Its location should be in right/bottom between the
 * info text and the working bar.
 */
public class DPositionPanel extends JPanel {

  /**
   * Creates a new position panel.
   *
   * @param     listener        the window that gets the requests
   */
  public DPositionPanel(DPositionPanelListener listener) {
    this.listener = listener;

    setLayout(new BorderLayout());
    setFocusable(false);

    record = new JPanel();
    record.setLayout(new BorderLayout());

    info = new JLabel();
//     info.setFont(DObject.FNT_INFO);
//     info.setForeground(DObject.CLR_FOREGROUND);
    info.setText(null);
//     info.setBorder(DObject.BRD_FOOT_PANEL);
    record.add(info, BorderLayout.CENTER);

    left = new JButton(Utils.getImage("arrowleft.gif"));
    left.setFocusable(false);
    left.setBorder(new EtchedBorder());
    left.setMargin(EMPTY_INSETS);
    left.setOpaque(false);
    left.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
          DPositionPanel.this.listener.gotoPrevPosition();
	}
      });
    record.add(left, BorderLayout.WEST);
    
    right = new JButton(Utils.getImage("arrowright.gif"));
    right.setFocusable(false);
    right.setBorder(new EtchedBorder());
    right.setMargin(EMPTY_INSETS);
    right.setOpaque(false);
    right.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
          DPositionPanel.this.listener.gotoNextPosition();
	}
      });
    record.add(right, BorderLayout.EAST);

    recordVisible = false;
  }
  
  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  public void setPosition(int current, int total) {
    if (current == -1) {
      if (recordVisible) {
        remove(record);
        recordVisible = false;
      }
    } else {
      if (!recordVisible) {
        add(record, BorderLayout.CENTER);
        recordVisible = true;
      }
      info.setText(" " + current + " / " + total + " ");
      left.setEnabled(current > 1);
      right.setEnabled(current < total);
    }

    doLayout();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final Insets	EMPTY_INSETS = new Insets(0, 0, 0, 0);

  private final DPositionPanelListener  listener;

  private final JPanel		record;
  private final JLabel  	info;
  private final JButton		left;
  private final JButton         right;
  private boolean		recordVisible;
}
