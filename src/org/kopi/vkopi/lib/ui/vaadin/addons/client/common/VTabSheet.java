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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.common;

import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;
import org.gwt.advanced.client.ui.widget.tab.TopBandRenderer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * A customized tab sheet widget.
 */
@SuppressWarnings("deprecation")
public class VTabSheet extends AdvancedTabPanel {

  //-----------------------------------------------------
  // CONSTRUCTOR
  //-----------------------------------------------------
  
  /**
   * Creates a new tab sheet instance.
   * @param separator The tab separator image URI;
   */
  public VTabSheet(String separator) {
    super(new CustomTabPosition(separator));
    ((FlexTable)getWidget()).getCellFormatter().addStyleName(0, 1, "tabs-cell");
  }

  //-----------------------------------------------------
  // IMPLEMENTATIONS
  //-----------------------------------------------------
  
  @Override
  public void setSelected(int index) {
    oldSelected = getSelected();
    super.setSelected(index);
  }
  
  @Override
  protected void renderTabs() {
    if (this.tabsWidget == null || oldCount != count()) {
      tabsWidget = (FlexTable) getPosition().getRenderer().render(this);
      TabPosition.LayoutPosition layoutPosition = getPosition().getLayoutPosition();
      
      switch (layoutPosition) {
      case LEFT:
        getLayout().setWidget(1, 0, tabsWidget);
        break;
      case RIGHT:
        getLayout().setWidget(1, 2, tabsWidget);
        break;
      case BOTTOM:
        getLayout().setWidget(2, 1, tabsWidget);
        break;
      case TOP:
      default:
        getLayout().setWidget(0, 1, tabsWidget);
      }
      
      oldCount = count();
    }
    updateTabsStyles();
    Element parent = DOM.getParent(((Widget) getContentBorder()).getElement());
    DOM.setStyleAttribute(parent, "height", "100%");
    
    Widget      content = getContent(getTab(getSelected()));
    
    if (getSelected() != -1 && (content != getContentBorder().getWidget() || getContentBorder().getWidget() == null)) {
        getContentBorder().setWidget(getContent(getTab(getSelected())));
    }
    // do it deferred to get the right calculated dimensions.
    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        calculateLastTabWidth();
      }
    });
  }
  
  /**
   * Updates the styles for tabs. Sets the selected and the unselected styles
   */
  protected void updateTabsStyles() {
    if (tabsWidget != null) {
      HTMLTable.CellFormatter     formatter = tabsWidget.getCellFormatter();

      if (oldSelected >= 0) {
        formatter.setStyleName(0, (2 * oldSelected) + 1, "unselected");
      }
      if (getSelected() >= 0) {
        formatter.setStyleName(0, (2 * getSelected()) + 1, "selected");
      }
    }
  }
  
  /**
   * Calculates the last empty tab width for styles reasons.
   */
  protected void calculateLastTabWidth() {
    FlexTable               layout;
    FlexTable               tabs;
    int                     tabsCellWidth;
    int                     cellCount;
    Element                 lastEmpty;
    
    layout = getLayout();
    tabs = (FlexTable) layout.getWidget(0, 1);
    tabsCellWidth = layout.getCellFormatter().getElement(0, 1).getClientWidth();
    cellCount = tabs.getCellCount(0);
    lastEmpty = tabs.getCellFormatter().getElement(0, cellCount - 1);
    // set the width of the last cell of the first row in tabs cell table division
    lastEmpty.getStyle().setWidth(tabsCellWidth - lastEmpty.getOffsetLeft(), Unit.PX);
  }
  
  /**
   * Returns the layout widget.
   * @return The layout widget.
   */
  protected FlexTable getLayout() {
    return (FlexTable) getWidget();
  }
  
  @Override
  public void clear() {
    tabsWidget.clear();
    getLayout().clear();
    clearTabsheet(this);
    tabsWidget = null;
    super.clear();
  }
  
  public static native void clearTabsheet(AdvancedTabPanel tabsheet) /*-{
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::tabs = null;
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::position = null;
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::tabBorderFactory = null;
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::contentBorderFactory = null;
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::layout = null;
    tabsheet.@org.gwt.advanced.client.ui.widget.AdvancedTabPanel::tabStates = null;
  }-*/;

  //-----------------------------------------------------
  // INNER CLASSES
  //-----------------------------------------------------
  
  /**
   * A customized tab position.
   */
  private static class CustomTabPosition extends TabPosition {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a custom tab position instance.
     * @param separator The separator image URI
     */
    public CustomTabPosition(String separator) {
      super("top", new CustomTopBandRenderer(separator), LayoutPosition.TOP);
    }
  }
  
  private static class CustomTopBandRenderer extends TopBandRenderer {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a custom band renderer instance.
     * @param separator The separator image URI
     */
    public CustomTopBandRenderer(String separator) {
      this.separator = separator;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    protected int createEmpty(FlexTable result, int count, String styleName) {
      HTMLTable.CellFormatter	formatter = result.getCellFormatter();
      
      result.setWidget(0, count, new Image(separator));
      formatter.setStyleName(0, count++, styleName);
      return count;
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final String		separator;
  }
  
  //-----------------------------------------------
  // DATA MEMBERS
  //-----------------------------------------------
  
  private int                   oldSelected = -1;
  private int                   oldCount;
  private FlexTable             tabsWidget;
}
