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

import java.awt.Color;

import com.kopiright.vkopi.lib.form.AbstractPredefinedValueHandler;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.xkopi.lib.type.Date;

/**
 * The <code>VPredefinedValueHandler</code> is the VAADIN implementation of
 * the predefined value handler specifications.
 */
public class VPredefinedValueHandler extends AbstractPredefinedValueHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VPredefinedValueHandler</code> instance.
   * @param model The row controller.
   * @param form The form model.
   * @param field The field model.
   */
  public VPredefinedValueHandler(VFieldUI model,
                                 VForm form,
                                 VField field)
  { 
    super(model,form,field);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public Color selectColor(Color color) throws VException {
    // no color selection
    return null;
  }

  @Override
  public Date selectDate(Date date) throws VException {
    return DateChooser.selectDate(date);
  }

 /**
 * This method will open the ImageFileChooser 
 * @return the selected image from the user file system
 * @author amira
 * @throws VException
 * @see com.kopiright.vkopi.lib.form.PredefinedValueHandler#selectImage()
 */
  @Override
  public byte[] selectImage() throws VException {
    // FIXME: Need to block here or the UI already blocks ?
    byte [] uploadedImage = ImageFileChooser.get().chooseImage();

    return uploadedImage;
  }
}
