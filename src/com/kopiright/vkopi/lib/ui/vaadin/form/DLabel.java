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

import com.kopiright.vkopi.lib.form.ULabel;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.visual.VApplication;
import com.kopiright.vkopi.lib.ui.vaadin.visual.VApplicationContext;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class DLabel extends CssLayout implements ULabel {
 
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DLabel</code> instance.
   * @param text The label text.
   * @param help The label help.
   * @param commands The field commands.
   */
  public DLabel(String text, String help, VCommand[] commands) {
    this.commands = commands;
    init(text, help);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the label in detail mode.
   * @param mode The detail mode ability.
   */
  public void setInDetailMode(boolean mode) {
    this.detailMode = mode;
  }
  
  /**
   * Returns {@code true} is the label is in detail mode.
   * @return {@code true} is the label is in detail mode.
   */
  public boolean isInDetail() {
    return detailMode;
  }
  
  /**
   * Prepares the label's snapshot.
   * @param activ The field state.
   */
  public void prepareSnapshot(boolean activ) {
    // TODO
  }
  
  @Override
  public void init(String label, String toolTip) {
    this.label = new Label(label == null ? "" : label);
    this.label.setSizeUndefined(); 
    if (commands.length > 0) {
      String description = "<html>"+toolTip;
      for (int i = 0; i < commands.length; i++) {
	switch (commands[i].getTrigger()) {
	case VForm.CMD_AUTOFILL:
	  description = description +"<br>"+ localizeActor("Autofill")+" [F2]";
	  break;
	case VForm.CMD_EDITITEM:
	  description = description +"<br>"+ localizeActor("EditItem")+" [Shift-F2]";
	  break;
	case VForm.CMD_EDITITEM_S:
	  description = description +"<br>"+ localizeActor("EditItem_S")+" [F2]";
	  break;
	case VForm.CMD_NEWITEM:
	  description = description +"<br>"+ localizeActor("NewItem")+" [Shift-F4]";
	  break;
	}
      }
      this.label.setDescription(description);
    } else {
      this.label.setDescription(toolTip);
    }
    this.label.addStyleName(KopiTheme.DLABEL_STYLE);
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	removeAllComponents();
	addComponent(DLabel.this.label);
      }
    });
  }
  
  /**
   * Sets the info text.
   * @param info The info text.
   */
  public void setInfoText(String info) {
    infoText = info;
  }
  
  /**
   * Returns the info text.
   * @return The info text.
   */
  public String getInfoText() {
    return infoText;
  }

  /**
   * Returns the label text.
   * @return The label text.
   */
  public String getDLabelText() {
    return this.label.getValue();
  }
  
  /**
   * Updates the label content.
   * @param model The field model.
   * @param row The field row.
   */
  public void update(final VField model, final int row) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	if (model.hasFocus()){
	  label.setStyleName(KopiTheme.DLABEL_FOCUSED_STYLE);
	} else if (model.getAccess(row) == VConstants.ACS_MUSTFILL) {
          label.setStyleName(KopiTheme.DLABEL_MUSTFILL_STYLE);
	} else {
	  label.setStyleName(KopiTheme.DLABEL_STYLE);
	}
	
	if(model.hasAutofill()){
	  addStyleName(KopiTheme.AUTOFILL_LABEL_STYLE);
	}
		
        if (model.getAccess(row) == VConstants.ACS_HIDDEN) {
          if (isVisible()) {
            setVisible(false);
          }
        } else {
          if (!isVisible()) {
            setVisible(true);
          }
	}
      } 
    });
  }
  
  /**
   * Returns the label text.
   * @return The label text.
   */
  public String getText() {
    return label.getCaption();
  }
  
  /**
   * Returns the {@link Label} component.
   * @return The {@link Label} component.
   */
  public Label getLabel() {
    return label;
  }
  
  /**
   * Localize the label actor.
   * @param ident the actor identifier.
   * @return THe localized actor.
   */
  public String localizeActor(String ident) {
    return ((VApplication) VApplicationContext.getApplicationContext().getApplication()).getLocalizationManager()
	   .getActorLocalizer(MENU_LOCALIZATION_RESOURCE, ident).getLabel();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Label				label;
  private String			infoText;
  private boolean			detailMode;
  private VCommand[]                    commands;
  private static final String           MENU_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/Menu";
}
