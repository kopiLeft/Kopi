/*
 * Copyright (c) 1990-2017 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.list;

import java.util.Locale;

import org.kopi.vkopi.lib.list.VListColumn;

import com.vaadin.data.util.converter.Converter;

/**
 * A list data model converter based on data model strings transformation.
 */
@SuppressWarnings("serial")
public class ListConverter implements Converter<String, Object> {

  public ListConverter(VListColumn model) {
    this.model = model;
  }
  
  @Override
  public Object convertToModel(String value, Class<? extends Object> targetType, Locale locale)
    throws ConversionException
  {
    return null; // no used
  }

  @Override
  public String convertToPresentation(Object value, Class<? extends String> targetType, Locale locale)
    throws ConversionException
  {
    return model.formatObject(value).toString();
  }

  @Override
  public Class<Object> getModelType() {
    return Object.class;
  }

  @Override
  public Class<String> getPresentationType() {
    return String.class;
  }
  
  private final VListColumn             model;
}
