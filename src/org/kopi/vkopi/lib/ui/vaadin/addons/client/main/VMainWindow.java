/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.LocalizedProperties;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VContent;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VHeader;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VLink;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VMain;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.date.VDateChooser;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.MainWindowListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.list.VListDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.notification.VAbstractNotification;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.progress.VProgressDialog;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.VUpload;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.wait.VWaitWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VPopupWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * Main application window composed of a header and content.
 * The content.
 * This main window will have a full size to fit with the browser
 * screen size.
 */
public class VMainWindow extends FocusableFlowPanel implements KeyDownHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the main window widget.
   */
  public VMainWindow() {
    VMain		main;
    
    setStyleName(Styles.MAIN_WINDOW);
    windows = new HashMap<Widget, MenuItem>();
    container = new VWindowContainer();
    listeners = new ArrayList<MainWindowListener>();
    shownInPopupWindow = new HashMap<Widget, VPopupWindow>();
    main = new VMain();
    header = new VHeader();
    welcome = new VWelcome();
    windowsLink = new VWindows();
    windowsMenu = new VWindowsDisplay();
    content = new VContent();
    content.setContent(container, false);
    add(header);
    header.setWelcome(welcome);
    header.setWindows(windowsLink);
    main.setContent(content);
    main.setWidth("100%");
    main.setHeight("100%");
    content.setWidth("100%");
    content.setHeight("100%");
    add(main);
    sinkEvents(Event.ONKEYDOWN);
    addKeyDownHandler(this);
    instance = this;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the module list widget.
   * @param moduleList The module list widget.
   */
  public void setModuleList(Widget moduleList) {
    header.setModuleList(moduleList, 1);
  }
  
  /**
   * Adds a window to this main page. Each window is added in a separate tab.
   * @param connection The application connection.
   * @param window The window widget to be added.
   * @param title The window title.
   */
  public void addWindow(ApplicationConnection connection, Widget window, String title) {
    // when a modal window is showing, the added window will not be accessible
    // since the modal glass has a bigger z-index than the main window one.
    // In this case, we will show the window in a non modal overlay so that it can be
    // accessible to the user.
    if (VPopupWindow.isModalWindowShowing()) {
      showNotModalPopupWindow(connection, window, title);
    } else {
      final VWindowsMenuItem		item;

      item = windowsMenu.addWindow(window, title);
      item.setScheduledCommand(new ScheduledCommand() {

        @Override
        public void execute() {
          if (currentWindow != item.getWindow()) {
            currentWindow = container.showWindow(item.getWindow());
            if (currentWindow instanceof VWindow) {
              ((VWindow) currentWindow).goBackToLastFocusedTextBox();
              fireWindowVisible(currentWindow);
            }
            windowsMenu.setCurrent(item);
            windowsMenu.hideMenu();
          }
        }
      });
      container.addWindow(window, title);
      container.setAnimationEnabled(false);
      // Immediately show added window
      currentWindow = container.showWindow(window);
      container.setAnimationEnabled(true);
      windows.put(window, item);
      windowsMenu.setCurrent(item);
      if (windows.size() > 1) {
        windowsLink.setEnabled(true);
      }
    }
  }
  
  /**
   * Shows the given window in a non modal popup overlay. 
   * @param connection The application connection.
   * @param window The window widget.
   * @param title The window title
   */
  protected void showNotModalPopupWindow(ApplicationConnection connection,
                                         Widget window,
                                         String title)
  {
    VPopupWindow                popup;
    
    popup = new VPopupWindow();
    popup.setCaption(title);
    popup.setContent(window);
    showNotModalPopupWindow(connection, popup);
    shownInPopupWindow.put(window, popup);
  }
  
  /**
   * Removes the given window 
   * @param window The window widget
   */
  public void removeWindow(Widget window) {
    if (shownInPopupWindow.containsKey(window)) {
      // window was shown in popup window
      // The popup must be closed.
      shownInPopupWindow.get(window).close();
      //shownInPopupWindow.get(window).clear();
      //shownInPopupWindow.get(window).removeFromParent();
      shownInPopupWindow.remove(window);
    } else {
      currentWindow = container.removeWindow(window);
      windowsMenu.removeWindow(windows.get(window));
      windowsMenu.setCurrent(windows.get(currentWindow));
      windows.remove(window);
      if (currentWindow instanceof VWindow) {
        ((VWindow) currentWindow).goBackToLastFocusedTextBox();
      }
      if (windows.size() <= 1) {
        windowsLink.setEnabled(false);
      }
      if (currentWindow == null) {
        Window.setTitle(originalWindowTitle);
      }
    }
  }
  
  /**
   * Updates the title (caption) of the given window.
   * @param window The concerned window.
   * @param title The new window title.
   */
  public void updateWindowTitle(Widget window, String title) {
    container.updateWindowTitle(window, title);
    windowsMenu.updateCaption(window, title);
    Window.setTitle(title);
  }
  
  /**
   * Shows a list dialog.
   * @param dialog The list dialog.
   */
  public void showListDialog(VListDialog dialog) {
    if (dialog != null) {
      dialog.show(this);
    }
  }
  
  /**
   * Shows the date chooser popup.
   * @param chooser The date chooser widget.
   */
  public void showDateChooser(VDateChooser chooser) {
    if (chooser != null) {
      chooser.openCalendarPanel(this);
    }
  }
  
  /**
   * Shows a modal popup window.
   * @param window The popup modal.
   */
  public void showModalPopupWindow(ApplicationConnection connection, VPopupWindow window) {
    if (window != null) {
      window.doModal(connection, this);
    }
  }
  
  /**
   * Shows a modal popup window.
   * @param window The popup modal.
   */
  public void showNotModalPopupWindow(ApplicationConnection connection, VPopupWindow window) {
    if (window != null) {
      window.doNotModal(connection, this);
    }
  }
  
  /**
   * Shows a notification popup.
   * @param notification The notification widget.
   * @param locale The notification locale.
   */
  public void showNotification(VAbstractNotification notification, String locale) {
    if (notification != null) {
      notification.show(this, locale == null ? this.locale : locale);
    }
  }
  
  /**
   * Shows the wait window.
   * @param wait The wait window.
   */
  public void showWaitWindow(VWaitWindow wait) {
    if (wait != null) {
      wait.show(this);
    }
  }
  
  /**
   * Shows the progress dialog.
   * @param progress The progress dialog.
   */
  public void showProgressDialog(VProgressDialog progress) {
    if (progress != null) {
      progress.show(this);
    }
  }
  
  /**
   * Attach the upload component.
   * @param connection The application connection
   * @param upload The upload component.
   */
  public void showUpload(ApplicationConnection connection, VUpload upload) {
    if (upload != null) {
      upload.show(connection, this);
    }
  }
  
  /**
   * Checks if the main window contains the given window.
   * @param window The searched window.
   * @return {@code true} if the window is found.
   */
  public boolean hasWindow(Widget window) {
    return windows.containsKey(window) || shownInPopupWindow.containsKey(window);
  }
  
  /**
   * Sets the href for the anchor element.
   * @param href the href
   */
  public void setHref(String href) {
    header.setHref(href);
  }
  
  /**
   * Sets the target frame.
   * @param target The target frame.
   */
  public void setTarget(String target) {
    header.setTarget(target);
  }
  
  /**
   * Sets the company logo image.
   * @param url The image URL.
   * @param alt The alternate text.
   */
  public void setImage(String url, String alt) {
    header.setImage(url, alt);
  }
  
  /**
   * Adds a link without separator.
   * @param link The link to be added.
   */
  public void addLink(VLink link) {
    header.addLink(link);
  }
  
  /**
   * Adds a link with separator.
   * @param link The link to be added.
   */
  public void addLinkWithSeparator(VLink link) {
    header.addLinkWithSeparator(link);
  }
  
  /**
   * Sets the opened windows link localized text.
   */
  protected void setWindowsText() {
    if (locale == null) {
      return;
    }
    
    this.windowsLink.setText(LocalizedProperties.getString(locale, "windowsText"));
  }

  /**
   * Sets the welcome text.
   */
  protected void setWelcomeText() {
    if (locale == null) {
      return;
    }
    
    this.welcome.setWelcomeText(LocalizedProperties.getString(locale, "welcomeText") + ", ");
  }
  
  /**
   * Sets the welcome link text.
   * @param text The welcome link text.
   */
  protected void setWelcomeLink(String text) {
    welcome.setWelcomeLink(text);
  }
  
  /**
   * Sets the logout link text.
   * @param text The logout link text.
   */
  protected void setLogoutLink() {
    if (locale == null) {
      return;
    }
    welcome.setLogoutLink(LocalizedProperties.getString(locale, "logoutText"));
  }
  
  /**
   * Sets the application locale;
   * @param locale the application locale;
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  /**
   * Shows the about text in a popup relative to the about link.
   * @param text The text to be shown.
   */
  public void showAboutText(ApplicationConnection connection, String text) {
    /*VPopupWindow		popup;
    
    popup = new VPopupWindow(connection, false, true);
    popup.setHeaderText("Information");
    popup.setWidth("300px");
    popup.setHeight("200px");
    //popup.setContent(new ); set content
    popup.showRelativeTo(about);*/
  }
  
  /**
   * Shows the opened windows menu.
   * @param connection The application connection
   */
  public void showWindowsMenu(ApplicationConnection connection) {
    windowsMenu.showMenuRelativeTo(connection, this, windowsLink);
  }
  
  /**
   * Adds the global links listeners
   */
  /*package*/ void addLinksListeners() {
    windowsLink.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	if (windowsLink.isEnabled()) {
	  fireWindowsClicked();
	}
      }
    });
    welcome.addLogoutClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        fireLogoutClicked();
      }
    });
    welcome.addWelcomeClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        fireUserClicked();
      }
    });
  }
  
  /**
   * Registers a main window listener on this main window.
   * @param l The listener to be registered.
   */
  public void addMainWindowListener(MainWindowListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a main window listener on this main window.
   * @param l The listener to be removed.
   */
  public void removeMainWindowListener(MainWindowListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires an help link click.
   */
  protected void fireLogoutClicked() {
    for (MainWindowListener l : listeners) {
      l.onLogout();
    }
  }
  
  /**
   * Fires an about link click.
   */
  protected void fireUserClicked() {
    for (MainWindowListener l : listeners) {
      l.onUser();
    }
  }
  
  /**
   * Fires opened windows link click.
   */
  protected void fireWindowsClicked() {
    for (MainWindowListener l : listeners) {
      l.onWindows();
    }
  }
  
  /**
   * Fires window become visible event.
   */
  protected void fireWindowVisible(Widget window) {
    for (MainWindowListener l : listeners) {
      l.onWindowVisible(window);
    }
  }
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    if (event.isAltKeyDown()) {
      switch (event.getNativeKeyCode()) {
      case KeyCodes.KEY_PAGEUP:
        gotoWindow(false);
        break;
      case KeyCodes.KEY_PAGEDOWN:
        gotoWindow(true);
        break;
      default:
        // nothing to do by default
        break;
      }
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    listeners.clear();
    listeners = null;
    header = null;
    windowsLink = null;
    welcome = null;
    content = null;
    container = null;
    locale = null;
    windows.clear();
    windows = null;
    shownInPopupWindow.clear();
    shownInPopupWindow = null;
    originalWindowTitle = null;
    windowsMenu = null;
    currentWindow = null;
    instance = null;
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    originalWindowTitle = Window.getTitle();
  }
  
  /**
   * Shows the next or previous window according to a flag
   * @param next Should we goto the next window ?
   *        Otherwise, it is the previous window that must be shown.
   */
  protected void gotoWindow(boolean next) {
    if (next) {
      currentWindow = container.showNextWindow();
    } else {
      currentWindow = container.showPreviousWindow();
    }
    if (currentWindow instanceof VWindow) {
      ((VWindow) currentWindow).goBackToLastFocusedTextBox();
      fireWindowVisible(currentWindow);
    }
    windowsMenu.setCurrent(currentWindow);
  }
  
  //---------------------------------------------------
  // STATIC ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the instance of the application main window.
   * @return The instance of the application main window.
   */
  public static VMainWindow get() {
    return instance;
  }
  
  /**
   * Returns the main window locale.
   * @return the main window locale.
   */
  public static String getLocale() {
    return instance.locale;
  }
  
  /**
   * Returns the current shown window. 
   * @return the current shown window. 
   */
  public Widget getCurrentWindow() {
    return currentWindow;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<MainWindowListener>              listeners;
  private VHeader				header;
  private VWindows			        windowsLink;
  private VWelcome			        welcome;
  private VContent			        content;
  private VWindowContainer		        container;
  private String				locale;
  private HashMap<Widget, MenuItem>		windows;
  private HashMap<Widget, VPopupWindow>         shownInPopupWindow;
  private VWindowsDisplay			windowsMenu;
  private Widget				currentWindow;
  private String                                originalWindowTitle;
  private static VMainWindow                    instance;
}
