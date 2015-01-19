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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import com.kopiright.vkopi.lib.ui.vaadin.base.FileUploader;
import com.kopiright.vkopi.lib.ui.vaadin.base.FileUploader.FileUploadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.FileUploader.UploadEvent;
import com.kopiright.vkopi.lib.visual.VException;

/**
 * A VAADIN image file chooser.
 */
public class ImageFileChooser implements FileUploadHandler {
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Returns the file chooser instance.
   * @return The file chooser instance.
   */
  /*package*/ public static ImageFileChooser get() {
    if (instance == null) {
      instance = new ImageFileChooser();
    }
    
    return instance;
  }
  
  /**
   * Chooses an image file.
   * @return The chosen image file.
   * @throws VException Visual errors.
   */
  /*package*/ public byte[] chooseImage() throws VException {
    return FileUploader.get().invoke(IMAGES_MIME_TYPE);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void uploadFinished(UploadEvent event) throws VException {}

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static ImageFileChooser	instance;
  /*package*/ static final String	IMAGES_MIME_TYPE = "image/*";
  private static final long		serialVersionUID = 3627099461437237284L;
}
