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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VH4;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.VPage;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Block widget.
 */
public class VBlock extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a simple block widget.
   */
  public VBlock() {
    setStyleName(Styles.BLOCK);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the block content.
   * @param content The block content.
   */
  protected void setContent(Widget content) {
    if (content == null) {
      return;
    }
    
    setWidget(content);
  }

  /**
   * Sets the block widget layout.
   * @param layout The widget layout.
   */
  protected void setLayout(BlockLayout layout) {
    if (layout == null) {
      return;
    }
    
    this.layout = layout;
  }
  
  /**
   * Sets the block caption.
   * @param caption The block caption.
   * @param maxColumnPos The maximum column position.
   */
  protected void setCaption(String caption) {
    if (caption == null || caption.length() == 0) {
      return;
    }
    
    VPage		page;
   
    this.caption = new VH4(caption);
    this.caption.setStyleName("block-title");
    page = getParentPage();
    if (page != null) {
      page.setCaption(this);
    }
  }
  
  /**
   * Returns the block caption.
   * @return the block caption.
   */
  public VH4 getCaption() {
    return caption;
  }
  
  /**
   * Returns the parent block page.
   * @return The parent block page.
   */
  protected VPage getParentPage() {
    return WidgetUtils.getParent(this, VPage.class);
  }
  
  /**
   * Layout components Creates the content of the block.
   */
  protected void layout() {
    // create detail block view.
    if (layout != null) {
      layout.layout();
    }
  }
  
  /**
   * Switch from the chart view to the detail view and vis versa.
   * Switch is only performed when it is a multi block.
   * @param detail Should we switch to detail view ?
   */
  public void switchView(boolean detail) {
    if (layout instanceof VMultiBlockLayout) {
      ((VMultiBlockLayout)layout).switchView(detail);
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    if (getWidget() instanceof HasWidgets) {
      ((HasWidgets)getWidget()).clear();
    }
    if (layout != null) {
      try {
        layout.clear();
      } catch (IndexOutOfBoundsException e) {
        // ignore cause it can be cleared before
      }
      layout = null;
    }
    caption = null;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private BlockLayout				layout;
  private VH4					caption;
}
