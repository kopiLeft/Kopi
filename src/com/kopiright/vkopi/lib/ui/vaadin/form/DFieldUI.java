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
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VlibProperties;

/**
 * The <code>DFieldUI</code> is the vaadin UI components implementation of
 * the {@link VFieldUI} row controller.
 */
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
    this.blockView = blockView;
    this.model = model;
  }
  
  // --------------------------------------------------
  // VFIELDUI IMPLEMENTATION
  // --------------------------------------------------

  @Override
  protected UField createDisplay(ULabel label, VField model, boolean detail) {
    DField      field = null;

    switch (model.getType()) {
    case VField.MDL_FLD_EDITOR:
      field = new DTextEditor(this, (DLabel)label, model.getAlign(), 0, ((VTextField) model).getHeight(), detail);
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
    return new DLabel(text, help, super.getAllCommands());
  }

  @Override
  protected UChartLabel createChartHeaderLabel(String text, 
                                               String help,
	                                       int index,
                                               OrderModel model)
  {
    return new DChartHeaderLabel(text, help, index, model, super.getAllCommands());
  }
  
  
  @Override
  public void displayFieldError(final String message) {
    if (blockView.getDisplayLine(getBlock().getActiveRecord()) == -1) {
      model.getForm().error(message);
      return;
    }
    
    UField[] 		displays = super.getDisplays();
    final DField	display = (DField) displays[blockView.getDisplayLine(getBlock().getActiveRecord())];
    
    if (blockView instanceof DChartBlock) {
      ((DWindow)getBlock().getForm().getDisplay()).getNotificationPanel().displayNotification(VlibProperties.getString("Error"),message);
    } else {
      BackgroundThreadHandler.start(new Runnable() {
	
        @Override
        public void run() {
          display.setComponentError(new com.vaadin.server.UserError(message));
        }
      });
    }
    
    try {
      transferFocus(display);
    } catch (VException e) {
      throw new InconsistencyException(e);
    }
  }  
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  private UBlock			blockView;
  private VField			model;
}
