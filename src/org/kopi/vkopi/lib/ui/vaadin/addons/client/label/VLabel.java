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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.label;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VAnchorPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VSpanPanel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.VChartBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.VTooltip;

/**
 * The field label widget.
 */
public class VLabel extends VSpanPanel implements HasEnabled {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new label widget.
   */
  public VLabel() {
    label = new VSpan();
    label.setStyleName("label");
    anchor = new VAnchorPanel();
    info = new VSpan();
    info.setStyleName("info-text");
    setStyleName(Styles.LABEL);
    sinkEvents(VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);
    add(anchor);
    anchor.add(label);
    anchor.add(info);
    // Hide focus outline in Mozilla/Webkit/Opera
    anchor.getElement().getStyle().setProperty("outline", "0px");
    // Hide focus outline in IE 6/7
    anchor.getElement().setAttribute("hideFocus", "true");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void setVisible(boolean visible) {
    if (!visible) {
      getElement().getStyle().setVisibility(Visibility.HIDDEN);
      if (DOM.getParent(getElement()) != null) {
        DOM.getParent(getElement()).getStyle().setVisibility(Visibility.HIDDEN);
      }
    } else {
      getElement().getStyle().setVisibility(Visibility.VISIBLE);
      if (DOM.getParent(getElement()) != null) {
        DOM.getParent(getElement()).getStyle().setVisibility(Visibility.VISIBLE);
      }
    }
    // now, we try to set the scroll bar visibility
    // cause the block may be fully invisible.
    handleChartLayoutVisiblility();
  }

  @Override
  public void setWidth(String width) {
    if (width == null || width.equals("")) {
      return; // don't override calculated width
    }
    super.setWidth(width);
  }
  
  /**
   * Adds a click handler to this 
   * @param handler The click handler.
   * @return The handler registration.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return label.addDomHandler(handler, ClickEvent.getType());
  }

  /**
   * Sets the label text.
   * @param text The label text.
   */
  @SuppressWarnings("deprecation")
  public void setText(String text) {
    if (BrowserInfo.get().isIE8()) {
      label.setText(Util.escapeHTML(text));
    } else {
      label.setText(text);
    }
  }
  
  /**
   * Sets the info text.
   * @param text The info text.
   */
  public void setInfoText(String text) {
    this.info.setText(text);
  }
  
  /**
   * Sets the label in auto fill mode.
   */
  public void setAutofill() {
    addStyleDependentName("autofill");
    anchor.setHref("#");
  }
  
  /**
   * Sets the inner label width.
   * @param width The label width in pixels.
   */
  public void setWidth(int width) {
    setWidth(width + Unit.PX.name());
  }
  
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      anchor.addStyleName("v-disabled");
      label.addStyleName("v-disabled");
      info.addStyleName("v-disabled");
    } else {
      anchor.removeStyleName("v-disabled");
      label.removeStyleName("v-disabled");
      info.removeStyleName("v-disabled");
    }
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        // The chart layout can be null if the label belongs to a simple block 
        chartLayout = WidgetUtils.getParent(VLabel.this, VChartBlockLayout.class);
      }
    });
  }
  
  @Override
  public void clear() {
    super.clear();
    info = null;
    label = null;
    anchor = null;
    chartLayout = null;
  }
  
  /**
   * Handles the chart layout caption and scroll bar visibililty.
   */
  protected void handleChartLayoutVisiblility() {
    if (chartLayout != null) {
      //chartLayout.maybeHideOrShowScrollBar();
      chartLayout.handleLayoutVisibility();
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VSpan                                 info;
  private VSpan                                 label;
  private VAnchorPanel                          anchor;
  private boolean				enabled;
  /**
   * Needed to update scroll bar and caption visibility
   * if all fields are invisible.
   */
  private VChartBlockLayout                     chartLayout;
}
