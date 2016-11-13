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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.MainWindowServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.MainWindowState;

import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * The main application window.
 */
@SuppressWarnings("serial")
public class MainWindow extends AbstractComponentContainer {
  
  /**
   * Creates the main window server component.
   * @param locale The application locale.
   * @param logo The application logo
   * @param href The logo link.
   */
  public MainWindow(Locale locale,
                    Resource logo,
                    String href)
  {
    registerRpc(rpc);
    setImmediate(true);
    getState().locale = locale.toString();
    getState().href = href;
    setIcon(logo);
    listeners = new ArrayList<MainWindowListener>();
    windows = new LinkedList<Component>();
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  public void setModuleList(ModuleList moduleList) {
    this.moduleList = moduleList;
    this.moduleList.setImmediate(true);
    addComponent(moduleList);
  }
  
  /**
   * Adds a window to this main window.
   * @param window The window to be added.
   */
  public void addWindow(Component window) {
    if (windows == null) {
      windows = new LinkedList<Component>();
    }
    windows.add(window);
    addComponent(window);
  }
  
  /**
   * Removes the given window.
   * @param window The window to be removed.
   */
  public void removeWindow(Component window) {
    if (equals(window.getParent())) {
      windows.remove(window);
      removeComponent(window);
      if (window instanceof PopupWindow) {
        ((PopupWindow) window).fireOnClose(); // fire close event
      }
    }
  }
  
  /**
   * Sets the connected user.
   * @param username the connected user.
   */
  public void setConnectedUser(String username) {
    getState().username = username;
  }
  
  @Override
  protected MainWindowState getState() {
    return (MainWindowState) super.getState();
  }
  
  @Override
  public Iterator<Component> iterator() {
    List<Component>		components;
    
    components = new ArrayList<Component>();
    components.add(moduleList);
    components.addAll(windows);
    
    return components.iterator();
  }
  
  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
    // cannot replace component.
  }

  @Override
  public int getComponentCount() {
    return windows.size() + (moduleList != null ? 1 : 0);
  }
  
  /**
   * Adds a main window listener.
   * @param l the listener to be registered.
   */
  public void addMainWindowListener(MainWindowListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a main window listener.
   * @param l the listener to be removed.
   */
  public void RemoveMainWindowListener(MainWindowListener l) {
    listeners.add(l);
  }
  
  /**
   * Fires on support action.
   */
  protected void fireOnSupport() {
    for (MainWindowListener l : listeners) {
      if (l != null) {
	l.onSupport();
      }
    }
  }
  
  /**
   * Fires on help action.
   */
  protected void fireOnHelp() {
    for (MainWindowListener l : listeners) {
      if (l != null) {
	l.onHelp();
      }
    }
  }
  
  /**
   * Fires on admin action.
   */
  protected void fireOnAdmin() {
    for (MainWindowListener l : listeners) {
      if (l != null) {
	l.onAdmin();
      }
    }
  }
  
  /**
   * Fires on logout action.
   */
  protected void fireOnLogout() {
    for (MainWindowListener l : listeners) {
      if (l != null) {
	l.onLogout();
      }
    }
  }
  
  /**
   * Fires on user action.
   */
  protected void fireOnUser() {
    for (MainWindowListener l : listeners) {
      if (l != null) {
	l.onUser();
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ModuleList			moduleList;
  private LinkedList<Component>		windows;
  private List<MainWindowListener>	listeners;
  private MainWindowServerRpc		rpc = new MainWindowServerRpc() {

    @Override
    public void onLogout() {
      fireOnLogout();
    }

    @Override
    public void onUser() {
      fireOnUser();
    }
  };
}
