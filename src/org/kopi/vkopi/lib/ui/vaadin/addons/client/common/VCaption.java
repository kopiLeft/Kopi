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

import com.google.gwt.user.client.ui.Composite;

/**
 * A tab caption widget. Used to display tab titles.
 */
public class VCaption extends Composite {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VCaption</code> instance.
   * @param onlyAnchor Should the caption include only an anchor ?
   */
  public VCaption(boolean onlyAnchor) {
    anchor = new VAnchorPanel();
    if (onlyAnchor) {
      initWidget(anchor);
    } else {
      VSpanPanel          span;
      
      span = new VSpanPanel();
      initWidget(span);
      span.add(anchor);
    }
    setStyleName("k-caption");
    caption = new VSpan();
    anchor.setHref("#"); // to have the hand cursor.
    if (!onlyAnchor) {
      anchor.add(caption);
    }
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the inner caption.
   * @param caption The inner caption.
   */
  public void setCaption(String caption) {
    if (anchor.getWidgetCount() == 0) {
      anchor.setText(caption);
    } else {
      this.caption.setText(caption);
    }
  }
  
  /**
   * Returns the caption text.
   * @return The caption text.
   */
  public String getCaption() {
    if (anchor.getWidgetCount() == 0) {
      return anchor.getElement().getInnerText();
    } else {
      return caption.getText();
    }
  }
  
  /**
   * Sets this caption to be active or not.
   * @param active The active state.
   */
  public void setActive(boolean active) {
    if (active) {
      anchor.addStyleName("active");
    } else {
      anchor.removeStyleName("active");
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VAnchorPanel			anchor;
  private final VSpan				caption;
}
