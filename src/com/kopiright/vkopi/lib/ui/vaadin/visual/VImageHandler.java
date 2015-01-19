/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.Image;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.ImageHandler;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;

/**
 * The <code>VImageHandler</code> is the vaadin implementation
 * of the {@link ImageHandler} specifications.
 */
public class VImageHandler extends ImageHandler {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Image getImage(String image) {
    return null;
  }

  @Override
  public Image getImage(byte[] image) {
    return null;
  }

  @SuppressWarnings("unused")
  @Override
  public String getURL(String image) {
    Resource	resource = Utils.getImage(image).getResource();
    String	applicationName = VaadinService.getCurrent().getBaseDirectory().getName();
    
    if (resource instanceof ConnectorResource) {
    //  return ApplicationContext.getApplicationContext().getApplication().getRelativeLocation((ConnectorResource)resource).replace("app:",  "/" + applicationName);
    }
    //  VaadinService.getCurrent().
    // retrieve the image from the theme resource
    return "../" + image;
  }
}
