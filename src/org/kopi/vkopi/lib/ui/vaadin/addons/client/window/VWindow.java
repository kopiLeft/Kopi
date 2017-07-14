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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.actor.VActorsNavigationPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyCombination;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.AcceleratorKeyHandler.AcceleratorKeyHandlerOwner;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * The window component. Composed of three major parts:
 * <ul>
 *   <li>The window actors</li>
 *   <li>The window body</li>
 *   <li>The window footer</li>
 * </ul>
 */
public class VWindow extends FocusableFlowPanel implements AcceleratorKeyHandlerOwner {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the window instance.
   */
  public VWindow() {
    setStyleName(Styles.WINDOW);
    sinkEvents(Event.ONKEYDOWN);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Initializes the window. Needed to set the application connection.
   */
  protected void init(ApplicationConnection connection) {
    this.connection = connection;
    actors = new VActorPanel(connection);
    add(actors);
  }
  
  /**
   * Registers a key combination on this window.
   * @param kc The key combination.
   */
  public void registerKeyCombination(AcceleratorKeyCombination kc) {
    getAcceleratorKeyHandler().addKeyCombination(kc);
  }
  
  @Override
  public void onBrowserEvent(Event event) {
    final int 		type = DOM.eventGetType(event);
    
    super.onBrowserEvent(event);
    if (type == Event.ONKEYDOWN && keyHandler != null) {
      keyHandler.handleKeyboardEvent(event);
      return;
    }
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Adds an actor to this window view.
   * @param actor The actor to be added.
   */
  public void addActor(Widget actor) {
    actors.addActor(actor);
  }
  
  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  public void addActorsNavigationPanel(VActorsNavigationPanel panel) {
    actors.addActorsNavigationPanel(panel);
  }
  
  /**
   * Sets the window content.
   * @param content The content.
   */
  public void setContent(Widget content) {
    if (this.content != null) {
      remove(this.content);
    }
    this.content = new ScrollPanel(content);
    insert(this.content, 1);
  }
  
  /**
   * Returns the window content.
   * @return The window content.
   */
  public Widget getContent() {
    return content;
  }
  
  /**
   * Sets the footer widget.
   * @param footer The foter widget.
   */
  public void setFooter(Widget footer) {
    // not supported anymore
  }
  
  /**
   * Sets the footer widget alignment.
   * @param hAlign The horizontal alignment.
   * @param vAlign The vertical alignment.
   */
  public void setFooterAlignment(HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
    // not supported anymore
  }
  
  /**
   * Clears the footer content.
   */
  public void clearFooter() {
    // not supported anymore
  }
  
  /**
   * Returns the application connection.
   * @return The application connection.
   */
  protected ApplicationConnection getApplicationConnection() {
    return connection;
  }
  
  @Override
  public AcceleratorKeyHandler getAcceleratorKeyHandler() {
    if (keyHandler == null) {
      keyHandler = new AcceleratorKeyHandler();
    }

    return keyHandler;
  }
  
  /**
   * Sets the last focused text box in this form.
   * @param lasFocusedTextBox The last focused text box.
   */
  public void setLastFocusedTextBox(TextBoxBase lasFocusedTextBox) {
    this.lasFocusedTextBox = lasFocusedTextBox;
  }
  
  /**
   * Returns {@code true} when this window has a focused text box before it looses focus.
   * @return {@code true} when this window has a focused text box before it looses focus.
   */
  public boolean hasLastFocusedTextBox() {
    return lasFocusedTextBox != null && lasFocusedTextBox.isAttached();
  }
  
  /**
   * Sets the focus to the last focused text box of this form.
   */
  public void goBackToLastFocusedTextBox() {
    new Timer() {
      
      @Override
      public void run() {
        if (lasFocusedTextBox != null) {
          lasFocusedTextBox.setFocus(true);
        }
      }
    }.schedule(100);
  }
  
  /**
   * Sets the window caption.
   * @param caption The window caption.
   */
  public void setCaption(String caption) {
    boolean             success;
    
    // first look if we can set the title on the main window.
    success = maybeSetMainWindowCaption(caption);
    if (!success) {
      // window does not belong to main window
      // It may be then belong to a popup window
      maybeSetPopupWindowCaption(caption);
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    connection = null;
    keyHandler = null;
    if (content instanceof HasWidgets) {
      ((HasWidgets)content).clear();
    }
    content = null;
    actors.clear();
    actors = null;
    lasFocusedTextBox = null;
  }
  
  /**
   * Returns the element associated with the actor menu to be used for tooltip.
   * @return The element associated with the actor menu.
   */
  protected Element getActorsMenuElement() {
    return actors.getActorsNavigationElement();
  }
  
  /**
   * Sets the window caption if it belongs to the main window.
   * @param caption The window caption.
   * @return {@code true} if the caption is set.
   */
  private boolean maybeSetMainWindowCaption(String caption) {
    VMainWindow         parent;
    
    parent =  WidgetUtils.getParent(this, VMainWindow.class);
    if (parent != null) {
      parent.updateWindowTitle(this, caption);
      return true;
    }
    
    return false;
  }
  
  /**
   * Sets the window caption if it belongs to a popup window.
   * @param caption The window caption.
   * @return {@code true} if the caption is set.
   */
  private boolean maybeSetPopupWindowCaption(String caption) {
    VPopupWindow         parent;
    
    parent =  WidgetUtils.getParent(this, VPopupWindow.class);
    if (parent != null) {
      parent.setCaption(caption);
      return true;
    }
    
    return false;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private ApplicationConnection			connection;
  private AcceleratorKeyHandler			keyHandler;
  private VActorPanel                           actors;
  private Widget                                content;
  private TextBoxBase                           lasFocusedTextBox;
}
