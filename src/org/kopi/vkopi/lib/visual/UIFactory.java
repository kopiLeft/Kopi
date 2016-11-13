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

import org.kopi.vkopi.lib.base.UComponent;

/**
 * {@code UIFactory} is visual components factory for model classes.
 * <p>This class is used whenever a visual component is needed to be built
 * from a given model.</p>
 * <p>Implementations should ensure the creations of visual components of all kopi
 * model</p>
 * @see VModel
 * @see UComponent
 */
public abstract class UIFactory {

  //---------------------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------------------

  /**
   * Returns the {@code UIFactory} instance.
   */
  public static UIFactory getUIFactory() {
    return uiFactory;
  }

  /**
   * Sets the {@code UIFactory} instance.
   * @param factory The UI factory
   */
  public static void setUIFactory(UIFactory factory) {
    assert factory != null : "UIFactory cannot be null";

    uiFactory = factory;
  }

  //---------------------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------------------

  /**
   * Creates the {@link UComponent} that corresponds to the given model.
   * @param model The view model.
   * @throws IllegalArgumentException When the model has no UI correspondence.
   */
  public abstract UComponent createView(VModel model);

  //---------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------

  private static UIFactory			uiFactory;
}
