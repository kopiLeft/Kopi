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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;

/**
 * A scrollable panel.
 */
public class VScrollablePanel extends ScrollPanel implements ResizeHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the scrollable panel instance.
   */
  public VScrollablePanel(final Widget content, String style) {
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    if (content != null) {
      add(content);
    }
    if (style != null) {
      setStyleName(style);
    }
    Window.addResizeHandler(this);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {

      @Override
      public void execute() {
	resize(Window.getClientWidth(), Window.getClientHeight());
      }
    });
  }

  @Override
  public void onResize(ResizeEvent event) {
    resize(event.getWidth(), event.getHeight());
  }
  
  /**
   * Resizes the panel.
   * @param width The window width.
   * @param height The window height.
   */
  public void resize(int width, int height) {
    if (WidgetUtil.getRequiredHeight(this) > height - getElement().getAbsoluteTop() - 28) {
      // make scrollable in y axis
      getElement().getStyle().setOverflowY(Overflow.AUTO);
      getElement().getStyle().setHeight(Math.max(0, height -  getElement().getAbsoluteTop() - 28), Unit.PX);
    } else {
      getElement().getStyle().setOverflowY(Overflow.HIDDEN);
      getElement().getStyle().setProperty("height", "auto");
    }

    if (WidgetUtil.getRequiredWidth(this) > width - getElement().getAbsoluteLeft() - 10) {
      // make scrollable in x axis
      getElement().getStyle().setOverflowX(Overflow.AUTO);
      getElement().getStyle().setWidth(Math.max(0, width -  getElement().getAbsoluteLeft() - 10), Unit.PX);
    } else {
      getElement().getStyle().setOverflowX(Overflow.HIDDEN);
      getElement().getStyle().setProperty("width", "auto");
    }
  }
}
