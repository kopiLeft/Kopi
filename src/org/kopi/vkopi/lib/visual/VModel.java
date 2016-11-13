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

package org.kopi.vkopi.lib.visual;

import java.io.Serializable;

import org.kopi.vkopi.lib.base.UComponent;

/**
 * {@code VModel} is the top level interface that all model classes should implement.
 * This interface is used in {@link UIFactory} to create model displays.
 *
 * @see UIFactory
 * @see WindowBuilder
 */
public interface VModel extends Serializable {

  /**
   * Sets the model display.
   * @param display The model display.
   */
  public void setDisplay(UComponent display);

  /**
   * Returns the model display.
   * @return The model display
   */
  public UComponent getDisplay();
}
