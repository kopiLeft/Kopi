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

import org.kopi.util.base.InconsistencyException;

/**
 * Creates the GUI, opens the window, form, ...
 * Handles actions initialized by the user
 */
@SuppressWarnings("serial")
public abstract class WindowController implements Serializable {

  //------------------------------------------------------------------
  // ACCESSORS
  //------------------------------------------------------------------

  /**
   * Returns the {@code WindowController} instance.
   * @return The WindowController instance.
   */
  public static WindowController getWindowController() {
    return windowController;
  }

  /**
   * Sets the {@code WindowController} instance.
   * @param controller The WindowController instance.
   */
  public static void setWindowController(WindowController controller) {
    assert controller != null : "WindowController cannot be null";

    windowController = controller;
  }

  //------------------------------------------------------------------
  // UTILS
  //------------------------------------------------------------------

  /**
   * Registers {@link WindowBuilder} for a given model type.
   * @param type The model type.
   * @param uiBuilder The given WindowBuilder.
   * @return The old registered {@link WindowBuilder} for the model type.
   */
  public WindowBuilder registerWindowBuilder(int type, WindowBuilder uiBuilder) {
    WindowBuilder   old = builder[type];

    builder[type] = uiBuilder;
    return old;
  }

  /**
   * Returns the {@link WindowBuilder} registered to a given model.
   * @param model The window model.
   * @return The corresponding WindowBuilder
   */
  protected WindowBuilder getWindowBuilder(VWindow model) {
    if (model.getType() > builder.length || builder[model.getType()] == null) {
      // programm should never reach here.
      Thread.dumpStack();
      throw new InconsistencyException("WindowController: WindowBuilder not found");
    } else {
      return builder[model.getType()];
    }
  }

  //------------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------------

  /**
   * Shows the {@link UWindow} and block the executing thread. The model should
   * wait until the view is closed.
   * @param model The {@link UWindow} model.
   */
  public abstract boolean doModal(VWindow model);

  /**
   * Shows the {@link UWindow} without blocking the executing thread.
   * @param model The {@link UWindow} model.
   */
  public abstract void doNotModal(VWindow model);

  //------------------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------------------

  private WindowBuilder[]                   	builder = new WindowBuilder[256];
  private static WindowController       	windowController;
}
