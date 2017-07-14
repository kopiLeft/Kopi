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

import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorImageField;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.Renderer;

import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class DGridEditorImageField extends DGridEditorField<Resource> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public DGridEditorImageField(VFieldUI columnView,
                               DGridEditorLabel label,
                               int align,
                               int width,
                               int height,
                               int options)
  {
    super(columnView, label, align, options);
    getEditor().setImageWidth(width);
    getEditor().setImageHeight(height);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public void updateText() {
    setObject(((VImageField)getModel()).getImage(getBlockView().getRecordFromDisplayLine(getPosition())));
  }

  @Override
  public Object getObject() {
    return image;
  }

  @Override
  protected GridEditorImageField createEditor() {
    return new GridEditorImageField();
  }

  @Override
  protected Converter<Resource, Object> createConverter() {
    return new Converter<Resource, Object>() {
      
      @Override
      public Class<Resource> getPresentationType() {
        return Resource.class;
      }
      
      @Override
      public Class<Object> getModelType() {
        return Object.class;
      }
      
      @Override
      public Resource convertToPresentation(Object value, Class<? extends Resource> targetType, Locale locale)
        throws ConversionException
      {
        if (value == null) {
          return null;
        } else {
          return new GridEditorImageField.ImageResource(new GridEditorImageField.ImageStreamSource((byte[])value));
        }
      }
      
      @Override
      public Object convertToModel(Resource value, Class<? extends Object> targetType, Locale locale)
        throws ConversionException
      {
        if (value == null) {
          return null;
        } else {
          return ((GridEditorImageField.ImageResource)value).getImage();
        }
      }
    };
  }

  @Override
  protected Renderer<Resource> createRenderer() {
    return new ImageRenderer() {
      
      @Override
      public JsonValue encode(Resource resource) {
        String                  key = "key" + keyCounter++;
        ResourceReference       reference =  ResourceReference.create(resource, this, key);
        
        getEditor().getState().resources.put(key, reference);
        return encode(ResourceReference.create(resource, getEditor(), key), URLReference.class);
      }
    };
  }
  
  @Override
  public GridEditorImageField getEditor() {
    return (GridEditorImageField) super.getEditor();
  }
  
  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  public void setObject(final Object s) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (s != null) {
          getEditor().setImage((byte[])s);
          setBlink(false);
          setBlink(true);
        }
      }
    });
    image = (byte[])s;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private byte[]                        image;
  private static int                    keyCounter;
}
