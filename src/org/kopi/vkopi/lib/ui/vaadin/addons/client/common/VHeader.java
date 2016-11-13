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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The window header widget.
 */
public class VHeader extends FlowPanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the window header widget.
   */
  public VHeader() {
    logo = new VCompanyLogo();
    links = new VGlobalLinks();
    getElement().setId("header");
    add(logo);
    add(links);
    add(new VClearPanel());
    add(new VClearPanel());
    add(new VBromine());
    //add(new VBromine()); !!! not really nice
    add(new VClearPanel());
    add(new VLine());
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Sets the module list widget.
   * @param moduleList The module list widget.
   * @param offset Offset to use to position the lodule list
   */
  public void setModuleList(Widget moduleList, int offset) {
    insert(moduleList, 7 + offset);
  }
  
  /**
   * Sets the windows link widget.
   * @param windows The link widgets
   */
  public void setWindows(Widget windows) {
    insert(windows, 3);
  }
  
  /**
   * Sets the welcome widget.
   * @param welcome The welcome widget.
   */
  public void setWelcome(Widget welcome) {
    insert(welcome, 2);
  }
  
  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  public void setHref(String href) {
    logo.setHref(href);
  }
  
  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  public void setTarget(String target) {
    logo.setTarget(target);
  }
  
  /**
   * Sets the company logo image.
   * @param url The image URL.
   * @param alt The alternate text.
   */
  public void setImage(String url, String alt) {
    logo.setImage(url, alt);
  }
  
  /**
   * Adds a link without separator.
   * @param link The link to be added.
   */
  public void addLink(VLink link) {
    links.addLink(link);
  }
  
  /**
   * Adds a link with separator.
   * @param link The link to be added.
   */
  public void addLinkWithSeparator(VLink link) {
    links.addLinkWithSeparator(link);
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VCompanyLogo			logo;
  private final VGlobalLinks			links; 
}
