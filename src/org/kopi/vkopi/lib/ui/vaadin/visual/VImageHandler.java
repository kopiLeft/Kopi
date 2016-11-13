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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import org.kopi.vkopi.lib.ui.vaadin.base.Image;
import org.kopi.vkopi.lib.visual.ImageHandler;

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

  @Override
  public String getURL(String image) {
    // image should be located in theme otherwise it
    // won't work. We cannot get the loaded image URL
    // since vaadin 7 does not give the possibility to
    // get the URL of a loaded resource.
    // A workaround is to put the used images as a theme
    // resources and try to load them from a fix path.
    return "../resource/" + image;
  }
}
