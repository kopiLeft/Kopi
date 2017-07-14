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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorActorFieldState;

import com.vaadin.data.util.converter.Converter.ConversionException;

/**
 * The grid editor actor field server side implementation
 */
@SuppressWarnings("serial")
public class GridEditorActorField extends GridEditorField<String> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public GridEditorActorField(String caption) {
    setCaption(caption);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public Class<? extends String> getType() {
    return String.class;
  }
  
  /**
   * Sets the field icon name.
   * @param icon The icon name.
   */
  public void setIcon(String icon) {
    getState().icon = icon;
  }
  
  @Override
  public void setValue(String value)
    throws ReadOnlyException, ConversionException
  {
    getState().value = value;
    super.setValue(value);
  }
  
  @Override
  protected EditorActorFieldState getState() {
    return (EditorActorFieldState) super.getState();
  }
  
  @Override
  public void addNavigationListener(NavigationListener listener) {
    // NOT SUPPORTED
  }
  
  @Override
  public void removeNavigationListener(NavigationListener listener) {
    // NOT SUPPORTED
  }
}
