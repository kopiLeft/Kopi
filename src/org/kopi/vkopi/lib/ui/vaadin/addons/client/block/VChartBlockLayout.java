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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.WidgetUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.VLabel;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.label.VSortableLabel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;

/**
 * The chart block layout.
 */
public class VChartBlockLayout extends VAbstractBlockLayout implements HasValueChangeHandlers<Integer> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the chart block layout.
   */
  public VChartBlockLayout() {
    setCellSpacing(0);
    addStyleDependentName("chart");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void add(Widget widget, ComponentConstraint constraints) {
    if (constraints != null && aligns != null && widgets != null) {
      aligns[constraints.x][constraints.y] = constraints;
      widgets[constraints.x][constraints.y] = widget;
    }
  }

  @Override
  public void layout() {
    for (int x = 0; x < widgets.length; x++) {
      if (widgets[x][0] != null) {
	setHeaderWidget(x, widgets[x][0]);
	setHeaderAlignment(x, 
	    		   (aligns[x][0].alignRight ? HasHorizontalAlignment.ALIGN_RIGHT : HasHorizontalAlignment.ALIGN_LEFT), 
	    		   HasVerticalAlignment.ALIGN_MIDDLE);
	getHeaderElement(x).setClassName("label");
      }
      for (int y = 1; y < widgets[0].length; y++) {
	if (widgets[x][y] != null && aligns[x][y] != null) {
	  widgets[x][y].addStyleDependentName("chart");
	  setWidget(widgets[x][y], aligns[x][y].x, aligns[x][y].y -1);
	  setAlignment(aligns[x][y].y - 1, aligns[x][y].x, aligns[x][y].alignRight);
	}
      }
    }
  }
  
  @Override
  public void updateScroll(final int pageSize, final int maxValue, final boolean enable, final int value) {
    new Timer() {
      
      @Override
      public void run() {
        if (scrollbar != null) {
          scrollbar.updateScroll(pageSize, maxValue, enable, value);
        }
      }
    }.schedule(15); //!!! experimental
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    if (!initialized) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {

	@Override
	public void execute() {
	  resizeLabels();
	  if (scrollbar != null) {
	    scrollbar.setHeight(getBodyElement().getClientHeight());
	  }
	}
      });
      initialized = true;
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
  
  /**
   * Returns {@code true} if the layout should be fully invisible.
   * @return {@code true} if the layout should be fully invisible.
   */
  @Override
  protected boolean isFullyInvisible() {
    for (int col = 0; col < getHeaderWidgets().size(); col++) {
      if (WidgetUtils.isVisible(getHeaderElement(col))) {
        return false;
      }
    }
    
    return true;
  }
  
  @Override
  public void clear() {
    super.clear();
    scrollbar = null;
  }
  
  /**
   * Resize labels to allow text-overflow CSS property
   */
  protected void resizeLabels() {
    for (int x = 0; x < getHeaderWidgets().size(); x++) {
      Widget	label = getHeaderWidgets().get(x);
      
      if (label != null && label instanceof VLabel) {
	InputElement           textfield;
	int                    initalLabelWidth;
	
	textfield = getTextField(0, x);
	initalLabelWidth = label.getElement().getClientWidth();
	if (textfield != null) {
	  ((VLabel)label).setWidth(Math.max(12, textfield.getClientWidth()));
	  // check if label width - 6px applied as padding in CSS
	  // are longer than the text field width. In this case sort icons
	  // will be hidden when displaying the label.
	  // We will force the padding to be 0 and apply a negative margin to
	  // force the visibility of sort icons.
	  if (label instanceof VSortableLabel && (initalLabelWidth - 6 > textfield.getClientWidth())) {
	    // ensure that sort icons are visible in this case
	    // so we apply a negative margin to show them
	    ((VSortableLabel)label).setSortIconsMargin(-8);
	  }
	}
      }
    }
  }
  
  /**
   * Creates the block scrollbar widget
   * @param conection The application connection needed to fetch icons
   */
  protected void createScrollBar(ApplicationConnection conection) {
    this.scrollbar = new VChartBlockScrollBar(this);

    // adding scrollbar in event state change after the hierarchy change event.
    setWidget(scrollbar, widgets.length, 0, 1, widgets[0].length);
    getCellFormatter().setStyleName(0, widgets.length, "scrollbar");
    scrollbar.addValueChangeHandler(new ValueChangeHandler<Integer>() {

      @Override
      public void onValueChange(ValueChangeEvent<Integer> event) {
	ValueChangeEvent.fire(VChartBlockLayout.this, event.getValue());
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VChartBlockScrollBar	        scrollbar;
  private boolean                       initialized = false;
}
