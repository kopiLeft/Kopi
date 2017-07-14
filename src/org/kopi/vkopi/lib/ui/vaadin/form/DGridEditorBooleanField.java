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

import java.util.Locale;

import org.kopi.vkopi.lib.form.UTextField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanRenderer;
import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanRenderer.ValueChangeEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.BooleanRenderer.ValueChangeListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorBooleanField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.Renderer;

@SuppressWarnings("serial")
public class DGridEditorBooleanField extends DGridEditorField<Boolean> implements UTextField, GridEditorBooleanField.ValueChangeListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public DGridEditorBooleanField(VFieldUI columnView,
                                 DGridEditorLabel label,
                                 int align,
                                 int options)
  {
    super(columnView, label, align, options);
    getEditor().setLabel(label.getText());
    getEditor().addValueChangeListener(this);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void updateText() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getEditor().setValue(getModel().getBoolean(getBlockView().getRecordFromDisplayLine(getPosition())));
      }
    });
  }
  
  @Override
  public void updateFocus() {
    label.update(columnView, getBlockView().getRecordFromDisplayLine(getPosition()));
    if (!modelHasFocus()) {
      if (inside) {
        inside = false;
      }
    } else {
      if (!inside) {
        inside = true;
        enterMe();
        if (rendrerValue != null) {
          getModel().setChangedUI(true);
          getModel().setBoolean(getBlockView().getModel().getActiveRecord(), rendrerValue);
          rendrerValue = null;
        }
      }
    }
    
    super.updateFocus();
  }
  
  @Override
  protected void reset() {
    inside = false;
    super.reset();
  }
  
  @Override
  public void updateAccess() {
    super.updateAccess();
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        getEditor().setLabel(label.getText());
        if (getAccess() == VConstants.ACS_MUSTFILL) {
          getEditor().setMandatory(true);
        } else {
          getEditor().setMandatory(false);
        }
      }
    });
  }

  @Override
  public Object getObject() {
    return getText();
  }

  @Override
  protected GridEditorField<Boolean> createEditor() {
    return new GridEditorBooleanField(getTrueRepresentation(), getFalseRepresentation());
  }

  @Override
  protected Converter<Boolean, Object> createConverter() {
    return new Converter<Boolean, Object>() {

      @Override
      public Object convertToModel(Boolean value, Class<? extends Object> targetType, Locale locale)
        throws ConversionException
      {
        return value;
      }

      @Override
      public Boolean convertToPresentation(Object value, Class<? extends Boolean> targetType, Locale locale)
        throws ConversionException
      {
        return (Boolean) value;
      }

      @Override
      public Class<Object> getModelType() {
        return Object.class;
      }

      @Override
      public Class<Boolean> getPresentationType() {
        return Boolean.class;
      }
    };
  }

  @Override
  protected Renderer<Boolean> createRenderer() {
    BooleanRenderer     renderer;
    
    renderer = new BooleanRenderer(getTrueRepresentation(), getFalseRepresentation());
    renderer.addValueChangeListener(new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        rendrerValue = event.getValue();
      }
    });
    
    return renderer;
  }

  @Override
  public String getText() {
    return getModel().toText(getEditor().getValue());
  }
  
  @Override
  public GridEditorBooleanField getEditor() {
    return (GridEditorBooleanField) super.getEditor();
  }

  @Override
  public void setHasCriticalValue(boolean b) {}

  @Override
  public void addSelectionFocusListener() {}

  @Override
  public void removeSelectionFocusListener() {}

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {}
  

  @Override
  public void valueChange(GridEditorBooleanField.ValueChangeEvent event) {
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
        getEditor().focus();
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private boolean                               inside;
  private Boolean                               rendrerValue;
}
