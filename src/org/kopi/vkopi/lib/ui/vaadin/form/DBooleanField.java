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

import org.kopi.vkopi.lib.form.UTextField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanField;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;

@SuppressWarnings("serial")
public class DBooleanField extends DObjectField implements UTextField, BooleanField.ValueChangeListener {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  /**
   * Creates a new boolean field instance.
   * @param model The field row controller.
   * @param label The field label.
   * @param align The field alignment (not used)
   * @param options The field options.
   * @param detail is it a detail field view ?
   */
  public DBooleanField(VFieldUI model,
                       DLabel label,
                       int align,
                       int options,
                       boolean detail)
  {
    super(model, label, align, options, detail);
    field = new BooleanField(getTrueRepresentation(), getFalseRepresentation());
    field.addValueChangeListener(this);
    setContent(field);
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  @Override
  protected boolean blinkOnFocus() {
    return false;
  }
  
  @Override
  public void updateColor() {
    // NOT SUPPORTED
  }
  
  @Override
  public void updateText() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setValue(getModel().getBoolean(getBlockView().getRecordFromDisplayLine(getPosition())));
      }
    });
    
    super.updateText();
  }
  
  @Override
  public void updateFocus() {
    label.update(model, getPosition());
    if (!modelHasFocus()) {
      if (inside) {
        inside = false;
      }
    } else {
      if (!inside) {
        inside = true;
        enterMe(); 
      }
    }
    
    super.updateFocus();
  }
  
  @Override
  public void valueChange(final BooleanField.ValueChangeEvent event) {
    String              text;
    
    // ensures to get model focus to validate the field
    if (!getModel().hasFocus()) {
      getModel().getBlock().setActiveField(getModel());
    }
    text = getModel().toText(event.getValue());
    if (getModel().checkText(text)) {
      getModel().setChangedUI(true);
      getModel().setBoolean(getBlockView().getRecordFromDisplayLine(getPosition()), event.getValue());
    }

    getModel().setChanged(true);
  }
  
  @Override
  public void updateAccess() {
    super.updateAccess();
    label.update(model, getBlockView().getRecordFromDisplayLine(getPosition()));
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setLabel(label.getText());
        field.setEnabled(getAccess() >= VConstants.ACS_VISIT);
        if (getAccess() == VConstants.ACS_MUSTFILL) {
          field.setMandatory(true);
        } else {
          field.setMandatory(false);
        }
      }
    });
  }

  @Override
  public Object getObject() {
    return getText();
  }

  @Override
  public void setBlink(final boolean b) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setBlink(b);
      }
    });
  }
  
  @Override
  public String getText() {
    return getModel().toText(field.getValue());
  }

  @Override
  public void setHasCriticalValue(boolean b) {}

  @Override
  public void addSelectionFocusListener() {}

  @Override
  public void removeSelectionFocusListener() {}

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {}
  
  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected String getTrueRepresentation() {
    return getModel().toText(Boolean.TRUE);
  }
  
  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected String getFalseRepresentation() {
    return getModel().toText(Boolean.FALSE);
  }

  /**
   * Gets the focus to this field.
   */
  protected void enterMe() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        field.setFocus(true);
      }
    });
  }

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  private final BooleanField                    field;
  private boolean                               inside;
}
