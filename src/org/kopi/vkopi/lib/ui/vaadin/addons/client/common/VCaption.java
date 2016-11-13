/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;

/**
 * A tab caption widget. Used to display tab titles.
 */
public class VCaption extends VSpanPanel {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VCaption</code> instance.
   */
  public VCaption() {
    setStyleName("k-caption");
    anchor = new VAnchorPanel();
    caption = new VSpan();
    anchor.setHref("#"); // to have the hand cursor.
    add(anchor);
    anchor.add(caption);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the inner caption.
   * @param caption The inner caption.
   */
  public void setCaption(String caption) {
    this.caption.setText(caption);
  }
  
  /**
   * Returns the caption text.
   * @return The caption text.
   */
  public String getCaption() {
    return caption.getText();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchorPanel			anchor;
  private final VSpan				caption;
}
