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

package com.kopiright.vkopi.lib.visual;

/**
 * {@code WindowBuilder} is responsible for creating {@link UWindow}
 * according to a given window model.
 * <p>{@code WindowBuilder} should be registered using {@link WindowController#registerWindowBuilder(int, WindowBuilder)}
 * @see WindowController#registerUIBuilder(int, WindowBuilder)</p>
 */
public interface WindowBuilder {

  /**
   * Creates the {@link UWindow} for a given window model
   * @param model The window model
   * @return The created {@link UWindow}
   */
  public UWindow createWindow(VWindow model);
}
