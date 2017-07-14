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

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.form.UActorField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.ActorField;
import org.kopi.vkopi.lib.ui.vaadin.addons.ActorField.ClickEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.ActorField.ClickListener;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

/**
 * UI Implementation of an actor field.
 */
@SuppressWarnings("serial")
public class DActorField extends DField implements UActorField, ClickListener {
  
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  
  public DActorField(VFieldUI model,
                     DLabel label,
                     int align,
                     int options,
                     boolean detail)
  {
    super(model, label, align, options, detail);
    field = new ActorField();
    field.setMaxWidth(model.getModel().getWidth());
    field.setCaption(getModel().getLabel());
    if (getModel().getIcon() != null) {
      field.setIcon(Utils.getFontAwesomeIcon(getModel().getIcon()));
    }
    field.setEnabled(getModel().getDefaultAccess() >= VConstants.ACS_VISIT);
    field.addClickListener(this);
    setContent(field);
  }
  
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------

  @Override
  public void onClick(ClickEvent event) {
    // field action is performed in the window action queue
    // it is not like the other fields trigger
    if (getModel().hasTrigger(VConstants.TRG_ACTION)) {
      model.performAsyncAction(new KopiAction("FIELD_ACTION") {

        @Override
        public void execute() throws VException {
          getModel().callTrigger(VConstants.TRG_ACTION);
        }
      });
    }
  }

  @Override
  public void setBlink(boolean blink) {}

  @Override
  public UComponent getAutofillButton() {
    return null;
  }

  @Override
  public void updateText() {
    final String        newModelTxt = getModel().getText(getBlockView().getRecordFromDisplayLine(getPosition()));
    
    BackgroundThreadHandler.access(new Runnable() {
    
      @Override
      public void run() {
        field.setValue(newModelTxt.trim());
      }
    });
  }

  @Override
  public void updateAccess() {
    super.updateAccess();
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setVisible(access != VConstants.ACS_HIDDEN);
        field.setEnabled(access >= VConstants.ACS_VISIT);
      }
    });
  }

  @Override
  public void updateFocus() {}

  @Override
  public void forceFocus() {}

  @Override
  public void updateColor() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setColor(Utils.toString(getForeground()), Utils.toString(getBackground()));
      }
    });
  }

  @Override
  public Object getObject() {
    return field.getValue();
  }

  @Override
  public String getText() {
    return field.getValue();
  }

  @Override
  public void setHasCriticalValue(boolean b) {}

  @Override
  public void addSelectionFocusListener() {}

  @Override
  public void removeSelectionFocusListener() {}

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {}
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private final ActorField                      field;
}
