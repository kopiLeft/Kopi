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

import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorActorField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.ClickEvent;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;

/**
 * The grid editor actor field.
 */
@SuppressWarnings("serial")
public class DGridEditorActorField extends DGridEditorField<String> {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public DGridEditorActorField(VFieldUI columnView,
                               DGridEditorLabel label,
                               int align, int options)
  {
    super(columnView, label, align, options);
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  @Override
  public void updateText() {
    final String        newModelTxt = getModel().getText(getBlockView().getRecordFromDisplayLine(getPosition()));
    
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getEditor().setValue(newModelTxt);
      }
    });
  }

  @Override
  public Object getObject() {
    return getEditor().getValue();
  }

  @Override
  protected GridEditorActorField createEditor() {
    GridEditorActorField        editor;
    
    editor = new GridEditorActorField(getModel().getLabel());
    if (getModel().getIcon() != null) {
      editor.setIcon(getModel().getIcon());
    }
 
    return editor;
  }

  @Override
  protected Converter<String, Object> createConverter() {
    return new Converter<String, Object>() {
      
      @Override
      public Class<String> getPresentationType() {
        return String.class;
      }
      
      @Override
      public Class<Object> getModelType() {
        return Object.class;
      }
      
      @Override
      public String convertToPresentation(Object value, Class<? extends String> targetType, Locale locale)
        throws ConversionException
      {
        return getModel().toText(value);
      }
      
      @Override
      public Object convertToModel(String value, Class<? extends Object> targetType, Locale locale)
        throws ConversionException
      {
        try {
          return getModel().toObject(value);
        } catch (VException e) {
          throw new ConversionException(e);
        }
      }
    };
  }

  @Override
  protected Renderer<String> createRenderer() {
    return new TextRenderer();
  }
  
  @Override
  public void onClick(ClickEvent event) {
    // field action is performed in the window action queue
    // it is not like the other fields trigger
    if (getModel().hasTrigger(VConstants.TRG_ACTION)) {
      columnView.performAsyncAction(new KopiAction("FIELD_ACTION") {

        @Override
        public void execute() throws VException {
          getModel().callTrigger(VConstants.TRG_ACTION);
        }
      });
    }
  }
}
