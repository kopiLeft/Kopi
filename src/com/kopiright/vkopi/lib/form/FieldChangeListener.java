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

package com.kopiright.vkopi.lib.form;

import java.io.Serializable;
import java.util.EventListener;

import com.kopiright.vkopi.lib.visual.VColor;

public interface FieldChangeListener extends EventListener, Serializable {

  /**
   * Fired when the field label is changed.
   */
  void labelChanged();
  
  /**
   * Fires when the field search operator is changed.
   */
  void searchOperatorChanged();
  
  /**
   * Fired when field value is changed.
   * @param r The current record.
   */
  void valueChanged(int r);
  
  /**
   * Fired when a field access is changed.
   * @param r The current record.
   */
  void accessChanged(int r);
  
  /**
   * Fired when the color properties of a field are changed.
   * <p>
   * Setting both the background and the foreground colors to
   * <code>null</code> will lead to reset the field color properties.
   * @param r The current record.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  void colorChanged(int r, VColor foreground, VColor background);
}
