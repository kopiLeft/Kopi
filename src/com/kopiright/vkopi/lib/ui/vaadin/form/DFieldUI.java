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

/**
 * The <code>DFieldUI</code> is the vaadin UI components implementation of
 * the {@link VFieldUI} row controller.
 */
@SuppressWarnings("serial")
public class DFieldUI extends VFieldUI {
	
  // --------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------

  /**
   * Creates a new <code>DFieldUI</code> instance.
   * @param blockView The block view.
   * @param model The field model.
   */
  public DFieldUI(UBlock blockView, VField model) {
    super(blockView, model);
  }
  
  // --------------------------------------------------
  // VFIELDUI IMPLEMENTATION
  // --------------------------------------------------

  @Override
  protected UField createDisplay(ULabel label, VField model, boolean detail) {
    DField      field = null;

    switch (model.getType()) {
    case VField.MDL_FLD_EDITOR:
      field = new DTextEditor(this, (DLabel)label, model.getAlign(),  model.getOptions(), ((VTextField) model).getHeight(), detail);
      break;
    case VField.MDL_FLD_TEXT:
      field = new DTextField(this, (DLabel)label, model.getAlign(), model.getOptions(), detail);
      break;
    case VField.MDL_FLD_IMAGE:
      field = new DImageField(this, (DLabel)label, model.getAlign(), 0, ((VImageField) model).getIconWidth(), ((VImageField) model).getIconHeight(), detail);
      break;
    default:
      throw new InconsistencyException("Type of model " + model.getType() + " not supported.");
    }
	    
    return field;
  }

  @Override
  protected FieldHandler createFieldHandler() {
    return new DFieldHandler(this);
  }

  @Override
  protected ULabel createLabel(String text, String help) {
    return new DLabel(text, help);
  }

  @Override
  protected UChartLabel createChartHeaderLabel(String text, 
                                               String help,
	                                       int index,
                                               OrderModel model)
  {
    return new DChartHeaderLabel(text, help, index, model);
  }
}
