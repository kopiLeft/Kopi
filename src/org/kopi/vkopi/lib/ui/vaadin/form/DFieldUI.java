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

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.form.FieldHandler;
import org.kopi.vkopi.lib.form.UBlock;
import org.kopi.vkopi.lib.form.UChartLabel;
import org.kopi.vkopi.lib.form.UField;
import org.kopi.vkopi.lib.form.ULabel;
import org.kopi.vkopi.lib.form.VBlock.OrderModel;
import org.kopi.vkopi.lib.form.VBooleanField;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.form.VStringField;
import org.kopi.vkopi.lib.form.VTextField;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;

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
  public DFieldUI(UBlock blockView, VField model, int index) {
    super(blockView, model, index);
  }
  
  // --------------------------------------------------
  // VFIELDUI IMPLEMENTATION
  // --------------------------------------------------

  @Override
  protected UField createDisplay(ULabel label, VField model, boolean detail) {
    DField      field = null;

    switch (model.getType()) {
    case VField.MDL_FLD_EDITOR:
      if (((VTextField) model).isStyled()) {
        field = new DRichTextEditor(this, (DLabel)label, model.getAlign(), model.getOptions(), ((VTextField) model).getHeight(), detail);
      } else {
        field = new DTextEditor(this, (DLabel)label, model.getAlign(), model.getOptions(), ((VTextField) model).getHeight(), detail);
      }
      break;
    case VField.MDL_FLD_TEXT:
      if (model instanceof VBooleanField) {
        field = new DBooleanField(this, (DLabel)label, model.getAlign(), model.getOptions(), detail);
      } else if (model instanceof VStringField && ((VStringField) model).isStyled()) {
        field = new DRichTextEditor(this, (DLabel)label, model.getAlign(), model.getOptions(), ((VStringField) model).getHeight(), detail);
      } else {
        field = new DTextField(this, (DLabel)label, model.getAlign(), model.getOptions(), detail);
      }
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
  protected ULabel createLabel(String text, String help, boolean detail) {
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
  
  @Override
  protected boolean includeBooleanAutofillCommand() {
    return false; // boolean fields are handled differently
  }
  
  /**
   * If the fields values are set in the model before display creation,
   * The {@link DFieldHandler#valueChanged(int)} is not called since the
   * listener is not registered yet. We will call the value change event for
   * every block record here to fill out the client side cached values.
   */
  @Override
  protected void fireDisplayCreated() {
    for (int r = 0; r < getBlock().getBufferSize(); r++) {
      // fire value changed only for text fields and when text value is not empty.
      if (getModel().getType() == VField.MDL_FLD_TEXT || getModel().getType() == VField.MDL_FLD_EDITOR) {
        if (getModel().getText(r) != null && getModel().getText(r).length() > 0) {
          ((DBlock)getBlockView()).fireValueChanged(getIndex(), r, getModel().getText(r));
        }
      }
      // fire color changed for non empty colors
      if (Utils.toString(getModel().getForeground(r)).length() > 0 || Utils.toString(getModel().getBackground(r)).length() > 0) {
        ((DBlock)getBlockView()).fireColorChanged(getIndex(),
                                                  r,
                                                  Utils.toString(getModel().getForeground(r)),
                                                  Utils.toString(getModel().getBackground(r)));
      }
    }
  }
}
