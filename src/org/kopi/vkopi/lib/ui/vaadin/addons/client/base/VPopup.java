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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.VTooltip;
import com.vaadin.client.ui.VOverlay;

/**
 * A Custom popup window.
 */
@SuppressWarnings("deprecation")
public class VPopup extends VOverlay {

  //--------------------------------------------------
  // CONSTRUCTORS
  //--------------------------------------------------
  
  /**
   * Creates a new <code>VPopup</code> instance.
   * @param connection The application connection.
   */
  public VPopup(ApplicationConnection connection) {
    this(connection, false, false);
  }

  /**
   * Creates a new <code>VPopup</code> instance.
   * @param connection The application connection.
   * @param autoHide Set auto hide mode.
   */
  public VPopup(ApplicationConnection connection, boolean autoHide) {
    this(connection, autoHide, false);
  }

  /**
   * Creates a new <code>VPopup</code> instance.
   * @param connection The application connection.
   * @param autoHide Set auto hide mode.
   * @param modal Set the popup modality.
   */
  public VPopup(ApplicationConnection connection, boolean autoHide, boolean modal) {
    super(autoHide, modal);
    this.connection = connection;
    sinkEvents(Event.MOUSEEVENTS);
  }

  //--------------------------------------------------
  // IMPLEMENTATIONS
  //--------------------------------------------------
  
  @Override
  protected ApplicationConnection getApplicationConnection() {
    return connection;
  }

  @Override
  public void show() {
    maybeHideTootip();
    maybeShowGlass(true);
    super.show();
  }
  
  @Override
  public void hide() {
    super.hide();
    maybeShowGlass(false);
  }
  
  /**
   * When enabled, the background will be blocked with a semi-transparent pane
   * the next time it is shown. If the PopupPanel is already visible, the glass
   * will not be displayed until it is hidden and shown again.
   *
   * @param enabled true to enable, false to disable
   */
  @Override
  public void setGlassEnabled(boolean enabled) {
    this.isGlassEnabled = enabled;
    if (enabled && glass == null) {
      glass = Document.get().createDivElement();
      glass.setClassName(glassStyleName);

      glass.getStyle().setPosition(Position.ABSOLUTE);
      glass.getStyle().setLeft(0, Unit.PX);
      glass.getStyle().setTop(0, Unit.PX);
    }
  }

  /**
   * Sets the style name to be used on the glass element. By default, this is
   * "gwt-PopupPanelGlass".
   *
   * @param glassStyleName the glass element's style name
   */
  @Override
  public void setGlassStyleName(String glassStyleName) {
    this.glassStyleName = glassStyleName;
    if (glass != null) {
      glass.setClassName(glassStyleName);
    }
  }
  
  /**
   * Sets this popup to be waiting for an operation to be completed.
   */
  public void setWaiting() {
    if (glass != null) {
      glass.getStyle().setCursor(Cursor.WAIT);
    }
  }
  
  /**
   * Disables the waiting state for this popup.
   */
  public void unsetWaiting() {
    if (glass != null) {
      glass.getStyle().setCursor(Cursor.DEFAULT);
    }
  }

  /**
   * Show or hide the glass.
   */
  private void maybeShowGlass(boolean showing) {
    if (showing) {
      if (isGlassEnabled) {
	glass.getStyle().setZIndex(20000);
        getOverlayContainer().appendChild(glass);
        resizeRegistration = Window.addResizeHandler(glassResizer);
        glassResizer.onResize(null);
        glassShowing = true;
      }
    } else if (glassShowing) {
      getOverlayContainer().removeChild(glass);
      resizeRegistration.removeHandler();
      resizeRegistration = null;
      glassShowing = false;
    }
  }
  
  /**
   * Hides any showing application tooltip 
   */
  private void maybeHideTootip() {
    if (connection != null) {
      VTooltip  tooltip = connection.getVTooltip();
      
      if (tooltip != null) {
        tooltip.hideTooltip();
      }
    }
  }

  //--------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------
  
  private ApplicationConnection		       connection;

  /**
   * The glass element.
   */
  private Element 				glass;

  private String 				glassStyleName = "k-popup-glass";

  /**
   * A boolean indicating that a glass element should be used.
   */
  private boolean 				isGlassEnabled;
  private boolean				glassShowing;
  private HandlerRegistration 			resizeRegistration;
  /**
   * Window resize handler used to keep the glass the proper size.
   */
  private ResizeHandler glassResizer = new ResizeHandler() {
    
    @Override
    public void onResize(ResizeEvent event) {
      Style style = glass.getStyle();

      int winWidth = Window.getClientWidth();
      int winHeight = Window.getClientHeight();

      // Hide the glass while checking the document size. Otherwise it would
      // interfere with the measurement.
      style.setDisplay(Display.NONE);
      style.setWidth(0, Unit.PX);
      style.setHeight(0, Unit.PX);

      int width = Document.get().getScrollWidth();
      int height = Document.get().getScrollHeight();

      // Set the glass size to the larger of the window's client size or the
      // document's scroll size.
      style.setWidth(Math.max(width, winWidth), Unit.PX);
      style.setHeight(Math.max(height, winHeight), Unit.PX);

      // The size is set. Show the glass again.
      style.setDisplay(Display.BLOCK);
    }
  };
}
