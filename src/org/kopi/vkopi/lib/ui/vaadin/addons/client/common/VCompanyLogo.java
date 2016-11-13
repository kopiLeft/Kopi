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

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The company logo widget that contains an image.
 */
public class VCompanyLogo extends SimplePanel {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the company logo widget.
   */
  public VCompanyLogo() {
    getElement().setId("companyLogo");
    logo = new VLogo();
    logo.getElement().setPropertyInt("border", 0);
    add(logo);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
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

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VLogo				logo;
}
