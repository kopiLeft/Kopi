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

package com.kopiright.vkopi.lib.ui.swing.form;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.form.FieldHandler;
import com.kopiright.vkopi.lib.form.UBlock;
import com.kopiright.vkopi.lib.form.UChartLabel;
import com.kopiright.vkopi.lib.form.UField;
import com.kopiright.vkopi.lib.form.ULabel;
import com.kopiright.vkopi.lib.form.VBlock.OrderModel;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.form.VImageField;
import com.kopiright.vkopi.lib.form.VTextField;
import com.kopiright.vkopi.lib.ui.swing.visual.SwingThreadHandler;

/**
 * {@code DFieldUI} is a swing implementation of {@link VFieldUI}
 */
@SuppressWarnings("serial")
public class DFieldUI extends VFieldUI {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  public DFieldUI(UBlock blockView, VField model) {
    super(blockView, model);
  }
  
  // ----------------------------------------------------------------------
  // VFIELDUI IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  /**
   * 
   */
  protected UField createDisplay(ULabel label, VField model, boolean detail) {
    UField      	field;

    switch (model.getType()) {
    case VField.MDL_FLD_COLOR:
      field = new DColorField(this, (DLabel)label, model.getAlign(), 0, detail);
      break;
    case VField.MDL_FLD_IMAGE:
      field = new DImageField(this, (DLabel)label, model.getAlign(), 0, ((VImageField) model).getIconWidth(), ((VImageField) model).getIconHeight(), detail);
      break;
    case VField.MDL_FLD_EDITOR:
      field = new DTextEditor(this, (DLabel)label, model.getAlign(), model.getOptions(), ((VTextField) model).getHeight(), detail);
      break;
    case VField.MDL_FLD_TEXT:
      field = new DTextField(this, (DLabel)label, model.getAlign(), model.getOptions(), detail);
      break;
    default:
      throw new InconsistencyException("Type of model " + model.getType() + " not supported.");
    }
    
    return field;
  }
  
  /**
   * 
   */
  protected FieldHandler createFieldHandler() {
    return new JFieldHandler(this);
  }

  /**
   * 
   */
  protected ULabel createLabel(String text, String help) {
    return new DLabel(text, help);
  }

  /**
   * 
   */
  protected UChartLabel createChartHeaderLabel(String text, String help, int index, OrderModel model) {
    return new DChartHeaderLabel(text, help, index, model);
  }

  // ----------------------------------------------------------------------
  // STATIC INITIALIZATION
  // ----------------------------------------------------------------------
  
  static {
    SwingThreadHandler.verifyRunsInEventThread("DFieldUI <init>");
  }
}
