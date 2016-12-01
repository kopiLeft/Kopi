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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.window;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VScrollablePanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.VInputTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.main.VMainWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VPanel;

/**
 * A popup window widget that contains a caption and a content.
 * This is focusable widget.
 */
public class VPopupWindow extends FlexTable implements CloseHandler<PopupPanel> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the popup window widget. 
   */
  public VPopupWindow() {
    setStyleName(Styles.POPUP_WINDOW);
    caption = new VSpan();
    caption.setStyleName(Styles.POPUP_WINDOW_CAPTION);
    setWidget(0, 0, caption);
    setCellPadding(0);
    setCellSpacing(0);
    getCellFormatter().setStyleName(0, 0, "window-caption");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the window caption.
   * @param caption The window caption.
   */
  public void setCaption(String caption) {
    this.caption.setText(caption);
  }
  
  /**
   * Sets the window content.
   * @param content The window content.
   */
  public void setContent(final Widget content) {
    if (content != null) {
      if (this.content != null) {
	removeCell(1, 0); // clear content
	this.content.removeStyleName(Styles.POPUP_WINDOW_CONTENT);
      }
      setWidget(1, 0, content);
      getWidget(1, 0).addStyleName(Styles.POPUP_WINDOW_CONTENT);
      this.content = content; // save the content may be it is useful.
      if (content instanceof VWindow) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
          
          @Override
          public void execute() {
            // we should focus the window for accelerators activation when it is a help view
            if (((VScrollablePanel)((VWindow) content).getContent()).getWidget() instanceof VPanel) {
              ((VWindow) content).focus();
            }
          }
        });
      }
    }
  }
  
  /**
   * Displays this popup window as a modal dialog.
   * @param connection The application connection.
   */
  public void doModal(ApplicationConnection connection, HasWidgets parent) {
    createPopup(connection, true);
    popup.setGlassEnabled(true);
    popup.setGlassStyleName(Styles.POPUP_WINDOW + "-glass");
    show(parent);
    modalWindows.add(this);
  }
  
  /**
   * Displays this popup window as a non modal dialog.
   * @param connection The application connection.
   */
  public void doNotModal(ApplicationConnection connection, HasWidgets parent) {
    createPopup(connection, false);
    show(parent);
  }
  
  /**
   * Creates the popup instance.
   * @param connection The application connection.
   */
  protected void createPopup(ApplicationConnection connection, boolean modal) {
    popup = new VPopup(connection, false, modal) {

      @Override
      public void onBrowserEvent(Event event) {
	// If we're not yet dragging, only trigger mouse events if the event occurs
	// in the caption wrapper
	switch (event.getTypeInt()) {
	case Event.ONMOUSEDOWN:
	case Event.ONMOUSEUP:
	case Event.ONMOUSEMOVE:
	case Event.ONMOUSEOVER:
	case Event.ONMOUSEOUT:
	  if (!dragging && !isCaptionEvent(event)) {
	    return;
	  }
	}
	super.onBrowserEvent(event);
      }
    };
    // add Handlers
    addPopupHandlers();
  }

  private boolean isCaptionEvent(NativeEvent event) {
    EventTarget		target = event.getEventTarget();
    
    if (Element.is(target)) {
      return getCellFormatter().getElement(0, 0).getParentElement().isOrHasChild(Element.as(target));
    }
    
    return false;
  }
  
  /**
   * Add the popup handlers.
   */
  private void addPopupHandlers() {
    MouseHandler	mouseHandler;
    
    windowWidth = Window.getClientWidth();
    clientLeft = Document.get().getBodyOffsetLeft();
    clientTop = Document.get().getBodyOffsetTop();
    mouseHandler = new MouseHandler();
    popup.addDomHandler(mouseHandler, MouseDownEvent.getType());
    popup.addDomHandler(mouseHandler, MouseUpEvent.getType());
    popup.addDomHandler(mouseHandler, MouseMoveEvent.getType());
    popup.addDomHandler(mouseHandler, MouseOverEvent.getType());
    popup.addDomHandler(mouseHandler, MouseOutEvent.getType());
    popup.addCloseHandler(this);
  }
  
  /**
   * Shows this popup window.
   */
  protected void show(HasWidgets parent) {
    if (popup == null) {
      return;
    }
    parent.add(popup);
    popup.setWidget(this);
    popup.setAnimationEnabled(true);
    if (isModalWindowShowing() && modalWindows.get(modalWindows.size() - 1).getContent() instanceof VWindow) {
      // try to pick it from the last opened modal window
      lastActiveWindow = (VWindow) modalWindows.get(modalWindows.size() - 1).getContent();
    } else if (VMainWindow.get().getCurrentWindow() instanceof VWindow) {
      // try to get it from the main window current shown window.
      lastActiveWindow = (VWindow) VMainWindow.get().getCurrentWindow();
    } else if (VInputTextField.getLastFocusedTextField() != null) {
      lastActiveWindow = VInputTextField.getLastFocusedTextField().getParentWindow();
    }
    popup.show();
    // try to center the popup later
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
	popup.center();
      }
    });
  }
  
  /**
   * Closes the popup window.
   */
  public void close() {
    hide();
  }
  
  /**
   * Hides this popup window.
   */
  protected void hide() {
    if (popup == null) {
      return;
    }
    
    popup.hide();
    if (modalWindows.contains(this)) {
      modalWindows.remove(this);
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    if (lastActiveWindow != null) {
      // focus the window itself to activate attached actors.
      lastActiveWindow.focus();
      if (lastActiveWindow.hasLastFocusedTextBox()) {
        // focus last text focused text box if available
        lastActiveWindow.goBackToLastFocusedTextBox();
      }
    }
  }

  /**
   * Called on mouse down in the caption area, begins the dragging loop by
   * turning on event capture.
   *
   * @see DOM#setCapture
   * @see #continueDragging
   * @param event the mouse down event that triggered dragging
   */
  protected void beginDragging(MouseDownEvent event) {
    onMouseDown(caption, event.getX(), event.getY());
  }

  protected void onMouseDown(Widget sender, int x, int y) {
    if (DOM.getCaptureElement() == null) {
      /*
       * Need to check to make sure that we aren't already capturing an element
       * otherwise events will not fire as expected. If this check isn't here,
       * any class which extends custom button will not fire its click event for
       * example.
       */
      dragging = true;
      DOM.setCapture(popup.getElement());
      popup.getElement().getStyle().setCursor(Cursor.MOVE);
      dragStartX = x;
      dragStartY = y;
    }
  }

  /**
   * Called on mouse move in the caption area, continues dragging if it was
   * started by {@link #beginDragging}.
   *
   * @see #beginDragging
   * @see #endDragging
   * @param event the mouse move event that continues dragging
   */
  protected void continueDragging(MouseMoveEvent event) {
    onMouseMove(caption.asWidget(), event.getX(), event.getY());
  }
  
  public void onMouseMove(Widget sender, int x, int y) {
    if (dragging) {
      int absX = x + getAbsoluteLeft();
      int absY = y + getAbsoluteTop();

      // if the mouse is off the screen to the left, right, or top, don't
      // move the dialog box. This would let users lose dialog boxes, which
      // would be bad for modal popups.
      if (absX < clientLeft || absX >= windowWidth || absY < clientTop) {
        return;
      }

      popup.setPopupPosition(absX - dragStartX, absY - dragStartY);
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    popup = null;
    caption = null;
    content = null;
    lastActiveWindow = null;
  }
  
  /**
   * Returns the popup content widget
   * @return the popup content widget
   */
  protected Widget getContent() {
    return content;
  }

  /**
   * Called on mouse up in the caption area, ends dragging by ending event
   * capture.
   *
   * @param event the mouse up event that ended dragging
   *
   * @see DOM#releaseCapture
   * @see #beginDragging
   * @see #endDragging
   */
  protected void endDragging(MouseUpEvent event) {
    onMouseUp(caption.asWidget(), event.getX(), event.getY());
  }

  public void onMouseUp(Widget sender, int x, int y) {
    dragging = false;
    popup.getElement().getStyle().setCursor(Cursor.DEFAULT);
    DOM.releaseCapture(popup.getElement());
  }
  
  public void onMouseEnter(Widget sender) {}
  
  public void onMouseLeave(Widget sender) {}
  
  /**
   * Returns {@code true} if at least one modal window is showing.
   * @return {@code true} if at least one modal window is showing.
   */
  public static boolean isModalWindowShowing() {
    return !modalWindows.isEmpty();
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {

    @Override
    public void onMouseDown(MouseDownEvent event) {
      beginDragging(event);
    }
    
    @Override
    public void onMouseMove(MouseMoveEvent event) {
      continueDragging(event);
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
      VPopupWindow.this.onMouseLeave(caption);
    }
    
    @Override
    public void onMouseOver(MouseOverEvent event) {
      VPopupWindow.this.onMouseEnter(caption);
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
      endDragging(event);
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VPopup				popup;
  private VSpan				        caption;
  private Widget				content;
  private boolean 				dragging;
  private int 					dragStartX;
  private int 					dragStartY;
  private int 					windowWidth;
  private int 					clientLeft;
  private int 					clientTop;
  private VWindow                               lastActiveWindow;
  private static List<VPopupWindow>             modalWindows = new ArrayList<VPopupWindow>();
}
