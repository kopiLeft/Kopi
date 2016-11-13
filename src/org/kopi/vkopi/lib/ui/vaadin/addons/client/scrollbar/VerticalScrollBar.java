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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;

/**
 * A vertical scroll bar widget based on browser native scroll bar.
 */
public class VerticalScrollBar extends Widget {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the vertical scroll bar widget instance.
   */
  public VerticalScrollBar(Widget parent) {
    verticalScrollbar = new VerticalScrollbarBundle();
    scroller = new Scroller(this);
    setElement(verticalScrollbar.getElement());
    this.parent = parent;
    setupScrollbar();
    verticalScrollbar.setStylePrimaryName("k-scrollbar");
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void onLoad() {
    super.onLoad();
    scroller.attachScrollListener(verticalScrollbar.getElement());
    scroller.attachMousewheelListener(parent.getElement());
  }
  
  @Override
  protected void onUnload() {
    scroller.detachScrollListener(verticalScrollbar.getElement());
    scroller.detachMousewheelListener(parent.getElement());
    super.onUnload();
  }
  
  /**
   * Force the scroll bar to be visible with CSS. In practice, this means to
   * set <code>overflow-y</code> to "<code>scroll</code>" in the scrollbar's direction.
   * <p>
   * This is an IE8 workaround, since it doesn't always show scrollbars with
   * <code>overflow: auto</code> enabled.
   */
  public void setAlwaysVisible(boolean enabled) {
    verticalScrollbar.forceScrollbar(enabled);
  }
  
  /**
   * Adds a scroll handler to the scroll bar.
   * 
   * @param handler The handler to add
   * @return the registration object for the handler registration
   */
  public HandlerRegistration addScrollHandler(ScrollHandler handler) {
    return verticalScrollbar.addScrollHandler(handler);
  }
  
  /**
   * Adds handler for the scroll bar handle visibility.
   * 
   * @param handler The {@link VisibilityHandler} to add
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addVisibilityHandler(VisibilityHandler handler) {
    return verticalScrollbar.addVisibilityHandler(handler);
  }

  /**
   * Returns the vertical scroll offset. Note that this is not necessarily the
   * same as the {@code scrollTop} attribute in the DOM.
   * 
   * @return the logical vertical scroll offset
   */
  public double getScrollTop() {
    return verticalScrollbar.getScrollPos();
  }

  /**
   * Sets the vertical scroll offset. Note that this will not necessarily
   * become the same as the {@code scrollTop} attribute in the DOM.
   * 
   * @param scrollTop The number of pixels to scroll vertically
   */
  public void setScrollTop(final double scrollTop) {
    verticalScrollbar.setScrollPos(scrollTop);
  }

  /**
   * Gets the scroll position of the scroll bar in the axis the scroll bar is
   * representing.
   * 
   * @return the new scroll position in pixels
   */
  public final double getScrollPos() {
    return verticalScrollbar.getScrollPos();
  }

  /**
   * Sets the amount of pixels the scrollbar needs to be able to scroll
   * through.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param px The number of pixels the scrollbar should be able to scroll through
   */
  public final void setScrollSize(double px) {
    verticalScrollbar.setScrollSize(px);
  }

  /**
   * Sets the scroll position of the scrollbar in the axis the scrollbar is
   * representing.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param px The new scroll position in pixels
   */
  public final void setScrollPos(double px) {
    verticalScrollbar.setScrollPos(px);
  }

  /**
   * Gets the length of the scroll bar
   * 
   * @return the length of the scroll bar in pixels
   */
  public double getOffsetSize() {
    return verticalScrollbar.getOffsetSize();
  }

  /**
   * Sets the length of the scroll bar.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param px The length of the scroll bar in pixels
   */
  public final void setOffsetSize(final double px) {
    verticalScrollbar.setOffsetSize(px);
  }

  /**
   * Recalculates the max scroll position.
   */
  public void recalculateMaxScrollPos() {
    verticalScrollbar.recalculateMaxScrollPos();
  }

  /**
   * Modifies the scroll position of this scrollbar by a number of pixels.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param delta The delta in pixels to change the scroll position by
   */
  public final void setScrollPosByDelta(double delta) {
    verticalScrollbar.setScrollPosByDelta(delta);
  }

  /**
   * Checks whether the scrollbar's handle is visible.
   * <p>
   * In other words, this method checks whether the contents is larger than
   * can visually fit in the element.
   * 
   * @return <code>true</code> iff the scrollbar's handle is visible
   */
  public boolean showsScrollHandle() {
    return verticalScrollbar.showsScrollHandle();
  }
  
  /**
   * Locks or unlocks the scroll bar bundle.
   * <p>
   * A locked scroll bar bundle will refuse to scroll, both programmatically
   * and via user-triggered events.
   * 
   * @param isLocked
   *            <code>true</code> to lock, <code>false</code> to unlock
   */
  public void setLocked(boolean isLocked) {
    verticalScrollbar.setLocked(isLocked);
  }

  /**
   * Checks whether the scroll bar bundle is locked or not.
   * 
   * @return <code>true</code> iff the scroll bar bundle is locked
   */
  public boolean isLocked() {
    return verticalScrollbar.isLocked();
  }
  
  /**
   * Moves the scroll handle from a given event.
   * @param deltaY The height variation to scroll to.
   * @param event The concerned event.
   */
  public void moveScrollFromEvent(double deltaY, NativeEvent event) {
    if (!Double.isNaN(deltaY)) {
      setScrollPosByDelta(deltaY);
    }

    if (deltaY != 0 && showsScrollHandle()) {
      event.preventDefault();
    }
  }
  
  /**
   * Installs the scroll bar element.
   */
  private void setupScrollbar() {
    int                 scrollbarThickness = WidgetUtil.getNativeScrollbarSize();
    ScrollHandler       scrollHandler = new ScrollHandler() {
      
      @Override
      public void onScroll(ScrollEvent event) {
        fireEvent(new ScrollEvent());
      }
    };
    
    if (BrowserInfo.get().isIE()) {
      /*
       * IE refuses to scroll properly if the DIV isn't at least one pixel
       * larger than the scrollbar controls themselves. But, probably
       * because of subpixel rendering, in Grid, one pixel isn't enough,
       * so we'll add two instead.
       */
      if (BrowserInfo.get().isIE9()) {
        scrollbarThickness += 2;
      } else {
        scrollbarThickness += 1;
      }
    }
    verticalScrollbar.addScrollHandler(scrollHandler);
    verticalScrollbar.setScrollbarThickness(scrollbarThickness);
    if (BrowserInfo.get().isIE8()) {
      /*
       * IE8 will have to compensate for a misalignment where it pops the
       * scrollbar outside of its box. See Bug 3 in
       * http://edskes.net/ie/ie8overflowandexpandingboxbugs.htm
       */
      Style vScrollStyle = verticalScrollbar.getElement().getStyle();
      vScrollStyle.setRight(verticalScrollbar.getScrollbarThickness() - 1, Unit.PX);
    }
    /*
     * Because of all the IE hacks we've done above, we now have scrollbars
     * hiding underneath a lot of DOM elements.
     * 
     * This leads to problems with OSX (and many touch-only devices) when
     * scrollbars are only shown when scrolling, as the scrollbar elements
     * are hidden underneath everything. We trust that the scrollbars behave
     * properly in these situations and simply pop them out with a bit of
     * z-indexing.
     */
    if (WidgetUtil.getNativeScrollbarSize() == 0) {
      verticalScrollbar.getElement().getStyle().setZIndex(90);
    }
  }
  
  //---------------------------------------------------
  // DATA MAMBERS
  //---------------------------------------------------
  
  private final Widget                          parent;
  private final VerticalScrollbarBundle         verticalScrollbar;
  private final Scroller                        scroller;
}
