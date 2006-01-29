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

package com.kopiright.vkopi.lib.form;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;

public class DPage extends JPanel {

  public DPage(boolean align) {
    super(true);
    setLayout(new BoxLayout(this, align ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
  }

  public void addBlock(Component block) {
    if (getComponentCount() > 0) {
      add(Box.createRigidArea(new Dimension(10, 10)));
    }
    add(block);
    last = block;
  }

  public void addFollowBlock(Component block) {
    if (last != null) {
      JPanel	temp = new JPanel() {
	  public Dimension getMaximumSize() {
	    return this.getPreferredSize();
	  }
	};
      temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
      remove(last);
      temp.add(last);
      temp.add(block);
      add(temp);
    } else {
      add(block);
    }
    last = null;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Component	last;
}
