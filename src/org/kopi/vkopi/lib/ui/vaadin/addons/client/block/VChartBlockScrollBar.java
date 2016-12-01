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

import java.util.LinkedList;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.ScrollEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.ScrollHandler;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.VerticalScrollBar;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * A scroll bar widget for the chart block layout.
 */
public class VChartBlockScrollBar extends Composite implements ScrollHandler, HasEnabled, HasValueChangeHandlers<Integer> {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the chart block scroll bar widget.
   * @param parent The parent chart block.
   */
  protected VChartBlockScrollBar(VChartBlockLayout parent) {
    scrollbar = new VerticalScrollBar(parent);
    scrollInfoQueue = new LinkedList<ScrollInfo>();
    initWidget(scrollbar);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enabled is the scroll bar enabled ?
   * @param value The scroll position.
   */
  protected void updateScroll(int pageSize, int maxValue, boolean enable, int value) {
    if (!isAttached()) {
      scrollInfoQueue.add(new ScrollInfo(pageSize, maxValue, enable, value));
    } else {
      if (enable) {
        // The scroll step in pixel: The height of the scroll bar divided by the chart block displayed size. 
        scrollStep = scrollbar.getOffsetSize() / pageSize;
        // Now the max scroll position will be set for the scroll bar.
        // It is the max scroll reached value multiplied by the scroll step
        scrollbar.setScrollSize(scrollStep * maxValue);
        // Now set the scroll position which is the scroll reached value  multiplied by the scroll step
        scrollbar.setScrollPos(scrollStep * value);
      } else {
        scrollStep = 0;
        scrollbar.setScrollPos(0);
        scrollbar.setScrollSize(0);
        scrollInfoQueue.clear();
      }
      // Now set the scroll bar ability
      setEnabled(enable);
    }
  }
  
  /**
   * Sets the height of the scroll bar.
   * @param height The scroll bar height.
   */
  protected void setHeight(int height) {
    scrollbar.setOffsetSize(height);
  }
  
  /**
   * Runs all pending scroll actions on this scroll bar.
   */
  protected void purgeScrollQueue() {
    while (!scrollInfoQueue.isEmpty()) {
      ScrollInfo        info;
      
      info = scrollInfoQueue.pop();
      if (info != null) {
        info.apply(this);
      }
    }
  }

  @Override
  public boolean isEnabled() {
    return !scrollbar.isLocked();
  }

  @Override
  public void setEnabled(boolean enabled) {
    scrollbar.setLocked(!enabled);
  }
  
  @Override
  public void setVisible(boolean visible) {
    getElement().getStyle().setVisibility(visible ? Visibility.VISIBLE : Visibility.HIDDEN);
  }

  @Override
  public void onScroll(ScrollEvent event) {
    // notify the server side that the scroll position
    // has been changed in the client size
    if (isEnabled()) {
      ValueChangeEvent.fire(this, (int)(scrollbar.getScrollTop() / scrollStep));
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
  
  @Override
  protected void onLoad() {
    super.onLoad();
    /*
     * always shows scroll bar for chart block
     * event if there are no data to be scrolled.
     */
    scrollbar.setAlwaysVisible(true);
    scrollHandlerRegistration = scrollbar.addScrollHandler(this);
    new Timer() {
      
      @Override
      public void run() {
        purgeScrollQueue();
      }
    }.schedule(25);//!!! experimental
  }
  
  @Override
  protected void onUnload() {
    super.onUnload();
    if (scrollHandlerRegistration != null) {
      scrollHandlerRegistration.removeHandler();
    }
    scrollInfoQueue.clear();
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * A bean representing a scroll information
   */
  private static class ScrollInfo implements IsSerializable {
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    /**
     * Creates a new instance of a scroll info.
     * @param pageSize The page size.
     * @param maxValue The max scroll value
     * @param enable Should we enable scroll bar.
     * @param value The scroll value
     */
    public ScrollInfo(int pageSize, int maxValue, boolean enable, int value) {
      this.pageSize = pageSize;
      this.maxValue = maxValue;
      this.enable = enable;
      this.value = value;
    }
    
    //---------------------------------------------------
    // IMPLEMENTATIONS 
    //---------------------------------------------------
    
    /**
     * Apply this scrol info to a given scroll bar.
     * @param scrollbar The scroll bar instance.
     */
    public void apply(VChartBlockScrollBar scrollbar) {
      scrollbar.updateScroll(pageSize, maxValue, enable, value);
    }
    
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    
    public final int                    pageSize;
    public final int                    maxValue;
    public final boolean                enable;
    public final int                    value;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VerticalScrollBar                     scrollbar;
  private double                                scrollStep;
  private HandlerRegistration                   scrollHandlerRegistration;
  private LinkedList<ScrollInfo>                scrollInfoQueue;
}
