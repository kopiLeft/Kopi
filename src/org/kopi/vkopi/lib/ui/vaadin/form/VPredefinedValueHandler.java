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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.awt.Color;

import org.kopi.vkopi.lib.form.AbstractPredefinedValueHandler;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.ui.vaadin.base.FileUploader;
import org.kopi.vkopi.lib.ui.vaadin.visual.VApplication;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.xkopi.lib.type.Date;

import com.vaadin.ui.Component;

/**
 * The <code>VPredefinedValueHandler</code> is the VAADIN implementation of
 * the predefined value handler specifications.
 */
@SuppressWarnings("serial")
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
    return DateChooser.selectDate(date, (Component) field.getDisplay());
  }

 /**
 * This method will open the file chooser to select an image. 
 * @return the selected image from the user file system
 * @throws VException
 * @see org.kopi.vkopi.lib.form.PredefinedValueHandler#selectImage()
 */
  @Override
  public byte[] selectImage() throws VException {
    return new FileUploader().upload("image/*");
  }
  
  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }
}
