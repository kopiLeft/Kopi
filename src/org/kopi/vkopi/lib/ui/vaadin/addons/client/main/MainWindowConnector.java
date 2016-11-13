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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.main;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.MainWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VScrollablePanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.date.DateChooserConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.date.VDateChooser;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.MainWindowListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.ListDialogConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.VListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.menu.ModuleListConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.AbstractNotificationConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.VAbstractNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.ProgressDialogConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.VProgressDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.UploadConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.VWaitDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.VWaitWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.WaitDialogConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.WaitWindowConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.PopupWindowConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VPopupWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.VScrollTable;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The main window connector.
 */
@SuppressWarnings("serial")
@Connect(value = MainWindow.class, loadStyle = LoadStyle.EAGER)
public class MainWindowConnector extends AbstractComponentContainerConnector implements MainWindowListener {
  
  @Override
  protected void init() {
    super.init();
    getWidget().addMainWindowListener(this);
  }
  
  @Override
  public VMainWindow getWidget() {
    return (VMainWindow) super.getWidget();
  }
  
  @Override
  public MainWindowState getState() {
    return (MainWindowState) super.getState();
  }
  
  @OnStateChange("resources")
  /*package*/ void setLogo() {
    String		uri = getIconUri();
    
    if (uri != null) {
      getWidget().setImage(uri, null);
    }
  }
  
  @OnStateChange("locale")
  /*package*/ void setLocale() {
    getWidget().setLocale(getState().locale);
    // add global links if we have locale
    getWidget().addLinksListeners();
    getWidget().setWindowsText();
    getWidget().setWelcomeText();
    getWidget().setLogoutLink();
  }
  
  
  @OnStateChange("username")
  /*package*/ void setUsername() {
    getWidget().setWelcomeLink(getState().username);
  }
  
  @OnStateChange("href")
  /*package*/ void setHref() {
    getWidget().setHref(getState().href);
    getWidget().setTarget("_blank");
  }

  @Override
  public void onLogout() {
    getRpcProxy(MainWindowServerRpc.class).onLogout();
  }

  @Override
  public void onUser() {
    getRpcProxy(MainWindowServerRpc.class).onUser();
  }

  @Override
  public void onWindows() {
    getWidget().showWindowsMenu(getConnection());
  }
  
  @Override
  public void onWindowVisible(Widget window) {
    /*
     * This is a workaround for VAADIN tables :
     * When the table became invisible cause it is not the 
     * component shown in main window, its size is reseted and it is
     * not restored even if the table becomes visible in the main window
     * again. A solution is to see if the shown window contains a VAADIN
     * table in is content and force the layout phase to be repainted.  
     */
    if (window instanceof VWindow) {
      final Widget      content = ((VWindow) window).getContent();
      
      if (content instanceof VScrollablePanel) {
        if (((VScrollablePanel) content).getWidget() instanceof VScrollTable) {
          ConnectorMap.get(getConnection()).getConnector(((VScrollablePanel) content).getWidget()).getLayoutManager().forceLayout();
        } else {
          new Timer() {
            
            @Override
            public void run() {
              // call resize twice ==> a work around for chrome browsers to force resizing with calculated
              // size of the scroll panel window content.
              ((VScrollablePanel) content).resize(Window.getClientWidth(), Window.getClientHeight());
              ((VScrollablePanel) content).resize(Window.getClientWidth(), Window.getClientHeight());
            }
          }.schedule(500);
        }
      }
    }
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    // do not delegate caption handling
    return false;
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }

  @Override
  public void updateCaption(ComponentConnector connector) {
    // nothing to do
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
    handleRemovedConnectors(event.getOldChildren()); // removed connectors before.
    for (ComponentConnector child : getChildComponents()) {
      if (event.getOldChildren().contains(child)) {
	continue;
      }
      
      if (child instanceof ModuleListConnector) {
	getWidget().setModuleList(child.getWidget());
      } else if (child instanceof ListDialogConnector) {
	handleListDialog((ListDialogConnector) child);
      } else if (child instanceof DateChooserConnector) {
	handleDateChooser((DateChooserConnector) child);
      } else if (child instanceof PopupWindowConnector) {
	handlePopupWindow((PopupWindowConnector) child);
      } else if (child instanceof AbstractNotificationConnector) {
	handleNotification((AbstractNotificationConnector) child);
      } else if (child instanceof WaitWindowConnector) {
	handleWaitWindow((WaitWindowConnector) child);
      } else if (child instanceof ProgressDialogConnector) {
	handleProgressDialog((ProgressDialogConnector) child);
      } else if (child instanceof WaitDialogConnector) {
	handleWaitDialog((WaitDialogConnector) child);
      } else if (child instanceof UploadConnector) {
	handleUpload((UploadConnector) child);
      } else {
	getWidget().addWindow(getConnection(), child.getWidget(), child.getState().caption);
      }
    }
  }
  
  /**
   * Handles the list dialog case.
   * @param connector The list dialog connector.
   */
  protected void handleListDialog(ListDialogConnector connector) {
    VListDialog			widget;
    
    widget = connector.getWidget();
    // force state variables cause the hearchy change event is fired befor the state change event.
    widget.setModel(connector.getState().model);
    if (connector.getState().reference != null) {
      widget.showRelativeTo(((ComponentConnector)connector.getState().reference).getWidget());
    }
    widget.setNewText(connector.getState().newText);
    // now show the list dialog
    getWidget().showListDialog(widget);
  }
  
  /**
   * Handles the date chooser case.
   * @param connector The date chooser connector.
   */
  protected void handleDateChooser(DateChooserConnector connector) {
    VDateChooser		widget;
    
    widget = connector.getWidget();
    if (connector.getState().reference != null) {
      widget.showRelativeTo(((ComponentConnector)connector.getState().reference).getWidget());
    }
    widget.setSelectedDate(connector.getState().selected, connector.getState().offset);
    widget.setLocale(connector.getState().locale);
    widget.setTodayCaption(connector.getState().today);
    // now show the list dialog
    getWidget().showDateChooser(widget);
  }
  
  /**
   * Handles the popup window case.
   * @param connector The popup window connector.
   */
  protected void handlePopupWindow(PopupWindowConnector connector) {
    VPopupWindow		widget;
    
    widget = connector.getWidget();
    if (connector.getState().caption != null) {
      widget.setCaption(connector.getState().caption);
    }
    if (connector.getState().modal) {
      getWidget().showModalPopupWindow(connector.getConnection(), widget);
    } else {
      getWidget().showNotModalPopupWindow(connector.getConnection(), widget);
    }
  }
  
  /**
   * Handles removed connectors.
   * @param oldConnectors The old attached connectors.
   */
  protected void handleRemovedConnectors(List<ComponentConnector> oldConnectors) {
    for (ComponentConnector child : oldConnectors) {
      if (isRemoved(child)) {
	if (child instanceof UploadConnector) {
	  child.getWidget().removeFromParent();
	} else if (getWidget().hasWindow(child.getWidget())) {
	  // connector removed. In this case it should be a VWindow
	  getWidget().removeWindow(child.getWidget());
	}
      }
    }
  }
  
  /**
   * Checks if a component connector is removed from children hearchy. 
   * @param connector The component connector.
   * @return {@code true} if the connector is removed.
   */
  protected boolean isRemoved(ComponentConnector connector) {
    // connector is removed when it is not included in children connectors
    return !getChildComponents().contains(connector);
  }
  
  /**
   * Handles notification widgets.
   * @param connector The notification connector.
   */
  protected void handleNotification(AbstractNotificationConnector connector) {
    VAbstractNotification		widget;
    
    widget = connector.getWidget();
    if (connector.getState().title != null) {
      widget.setNotificationTitle(connector.getState().title);
    }
    if (connector.getState().message != null) {
      widget.setNotificationMessage(connector.getState().message);
    }
    widget.setYesIsDefault(connector.getState().yesIsDefault);
    getWidget().showNotification(widget, connector.getState().locale);
  }
  
  /**
   * Handles the wait window case.
   * @param connector The wait window connector.
   */
  protected void handleWaitWindow(WaitWindowConnector connector) {
    VWaitWindow			widget;
    
    widget = connector.getWidget();
    if (connector.getState().caption != null) {
      widget.setText(connector.getState().caption);
    }
    getWidget().showWaitWindow(widget);
  }
  
  /**
   * Handles the case of the progress dialog.
   * @param connector The progress dialog connector.
   */
  protected void handleProgressDialog(ProgressDialogConnector connector) {
    VProgressDialog		widget;
    
    widget = connector.getWidget();
    if (connector.getState().title != null) {
      widget.setTitle(connector.getState().title);
    }
    if (connector.getState().message != null) {
      widget.setMessage(connector.getState().message);
    }
    if (connector.getState().totalJobs != 0) {
     getWidget().showProgressDialog(widget);
    }
  }
  
  /**
   * Handles the case of the wait dialog.
   * @param connector The wait dialog connector.
   */
  protected void handleWaitDialog(WaitDialogConnector connector) {
    VWaitDialog		widget;
    
    widget = connector.getWidget();
    if (connector.getState().title != null) {
      widget.setTitle(connector.getState().title);
    }
    if (connector.getState().message != null) {
      widget.setMessage(connector.getState().message);
    }
    if (connector.getState().maxTime != 0) {
     getWidget().showProgressDialog(widget);
    }
  }
  
  /**
   * Handles the upload connector attachment.
   * @param connector The upload connector.
   */
  protected void handleUpload(UploadConnector connector) {
    getWidget().showUpload(connector.getConnection(), connector.getWidget());
  }
}
