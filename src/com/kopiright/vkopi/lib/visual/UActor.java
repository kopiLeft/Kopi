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

import com.kopiright.vkopi.lib.base.UComponent;

/**
 * {@code UActor} is the top level interface that all visual actor components
 * should implement. It is the visual component of {@link VActor}
 */
public interface UActor extends UComponent {

  /**
   * Sets the actor model.
   * @param model The actor model.
   */
  public void setModel(VActor model);

  /**
   * Returns the actor model.
   * @return The actor model.
   */
  public VActor getModel();
}
