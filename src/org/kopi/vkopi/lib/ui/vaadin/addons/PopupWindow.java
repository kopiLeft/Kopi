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

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.PopupWindowState;

import com.vaadin.ui.AbstractSingleComponentContainer;

/**
 * The popup window server component.
 */
@SuppressWarnings("serial")
public class PopupWindow extends AbstractSingleComponentContainer {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new popup window.
   */
  public PopupWindow() {
    listeners = new ArrayList<CloseListener>();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the modality of this window.
   * @param modal Is it a modal window.
   */
  public void setModal(boolean modal) {
    getState().modal = modal;
  }
  
  /**
   * Registers a close listener.
   * @param l The listener to be registered.
   */
  public void addCloseListener(CloseListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a close listener.
   * @param l The listener to be removed.
   */
  public void removeCloseListener(CloseListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a close event.
   */
  public void fireOnClose() {
    for (CloseListener l : listeners) {
      if (l != null) {
	l.onClose();
      }
    }
  }
  
  @Override
  protected PopupWindowState getState() {
    return (PopupWindowState) super.getState();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<CloseListener>	listeners;
}
