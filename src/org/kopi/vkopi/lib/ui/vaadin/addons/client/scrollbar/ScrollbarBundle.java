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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.WidgetUtil;

/**
 * An element-like bundle representing a configurable and visual scroll bar in
 * one axis.
 */
public abstract class ScrollbarBundle implements DeferredWorker {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new scroll bundle instance.
   */
  protected ScrollbarBundle() {
    root.appendChild(scrollSizeElement);
    root.getStyle().setDisplay(Display.NONE);
    root.setTabIndex(-1);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public boolean isWorkPending() {
    return scrollSizeTemporaryScrollHandler != null || offsetSizeTemporaryScrollHandler != null;
  }
  
  /**
   * Sets the primary style name
   * 
   * @param primaryStyleName The primary style name to use
   */
  public void setStylePrimaryName(String primaryStyleName) {
    root.setClassName(primaryStyleName + "-scroller");
  }

  /**
   * Gets the root element of this scrollbar-composition.
   * 
   * @return the root element
   */
  public final Element getElement() {
    return root;
  }

  /**
   * Modifies the scroll position of this scroll bar by a number of pixels.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param delta The delta in pixels to change the scroll position by
   */
  public final void setScrollPosByDelta(double delta) {
    if (delta != 0) {
      setScrollPos(getScrollPos() + delta);
    }
  }

  /**
   * Sets the length of the scrollbar.
   * <p>
   * <em>Note:</em> Even though {@code double} values are used, they are
   * currently only used as integers as large {@code int} (or small but fast
   * {@code long}). This means, all values are truncated to zero decimal
   * places.
   * 
   * @param px The length of the scrollbar in pixels
   */
  public final void setOffsetSize(final double px) {
    /*
     * This needs to be made step-by-step because IE8 flat-out refuses to
     * fire a scroll event when the scroll size becomes smaller than the
     * offset size. All other browser need to suffer alongside.
     */
    boolean     newOffsetSizeIsGreaterThanScrollSize = px > getScrollSize();
    boolean     offsetSizeBecomesGreaterThanScrollSize = showsScrollHandle() && newOffsetSizeIsGreaterThanScrollSize;
    
    if (offsetSizeBecomesGreaterThanScrollSize && getScrollPos() != 0) {
      // must be a field because Java insists.
      offsetSizeTemporaryScrollHandler = addScrollHandler(new ScrollHandler() {
        
        @Override
        public void onScroll(ScrollEvent event) {
          setOffsetSizeNow(px);
        }
      });
      setScrollPos(0);
    } else {
      setOffsetSizeNow(px);
    }
  }

  /**
   * Gets the length of the scrollbar
   * 
   * @return the length of the scrollbar in pixels
   */
  public double getOffsetSize() {
    return parseCssDimensionToPixels(internalGetOffsetSize());
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
    if (isLocked()) {
      return;
    }

    double oldScrollPos = scrollPos;
    scrollPos = Math.max(0, Math.min(maxScrollPos, truncate(px)));

    if (!WidgetUtil.pixelValuesEqual(oldScrollPos, scrollPos)) {
      if (isInvisibleScrollbar) {
        invisibleScrollbarTemporaryResizer.show();
      }

      /*
       * This is where the value needs to be converted into an integer no
       * matter how we flip it, since GWT expects an integer value.
       * There's no point making a JSNI method that accepts doubles as the
       * scroll position, since the browsers themselves don't support such
       * large numbers (as of today, 25.3.2014). This double-ranged is
       * only facilitating future virtual scrollbars.
       */
      internalSetScrollPos(toInt32(scrollPos));
    }
  }

  /**
   * Should be called whenever this bundle is attached to the DOM (typically,
   * from the onLoad of the containing widget). Used to ensure the DOM scroll
   * position is maintained when detaching and reattaching the bundle.
   */
  public void onLoad() {
    internalSetScrollPos(toInt32(scrollPos));
  }

  /**
   * Gets the scroll position of the scrollbar in the axis the scrollbar is
   * representing.
   * 
   * @return the new scroll position in pixels
   */
  public final double getScrollPos() {
    assert internalGetScrollPos() == toInt32(scrollPos) : "calculated scroll position ("
      + toInt32(scrollPos)
      + ") did not match the DOM element scroll position ("
      + internalGetScrollPos() + ")";
    
    return scrollPos;
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
  public final void setScrollSize(final double px) {
    /*
     * This needs to be made step-by-step because IE8 flat-out refuses to
     * fire a scroll event when the scroll size becomes smaller than the
     * offset size. All other browser need to suffer alongside.
     */
    boolean newScrollSizeIsSmallerThanOffsetSize = px <= getOffsetSize();
    boolean scrollSizeBecomesSmallerThanOffsetSize = showsScrollHandle()
        && newScrollSizeIsSmallerThanOffsetSize;
    if (scrollSizeBecomesSmallerThanOffsetSize && getScrollPos() != 0) {
      // must be a field because Java insists.
      scrollSizeTemporaryScrollHandler = addScrollHandler(new ScrollHandler() {
        @Override
        public void onScroll(ScrollEvent event) {
          setScrollSizeNow(px);
        }
      });
      setScrollPos(0);
    } else {
      setScrollSizeNow(px);
    }
  }

  /**
   * Gets the amount of pixels the scrollbar needs to be able to scroll
   * through.
   * 
   * @return the number of pixels the scrollbar should be able to scroll
   *         through
   */
  public double getScrollSize() {
    return parseCssDimensionToPixels(internalGetScrollSize());
  }
  
  /**
   * Sets the scrollbar's thickness.
   * <p>
   * If the thickness is set to 0, the scrollbar will be treated as an
   * "invisible" scrollbar. This means, the DOM structure will be given a
   * non-zero size, but {@link #getScrollbarThickness()} will still return the
   * value 0.
   * 
   * @param px
   *            the scrollbar's thickness in pixels
   */
  public final void setScrollbarThickness(double px) {
    isInvisibleScrollbar = (px == 0);

    if (isInvisibleScrollbar) {
      Event.sinkEvents(root, Event.ONSCROLL);
      Event.setEventListener(root, new EventListener() {
        @Override
        public void onBrowserEvent(Event event) {
          invisibleScrollbarTemporaryResizer.show();
        }
      });
      root.getStyle().setVisibility(Visibility.HIDDEN);
    } else {
      Event.sinkEvents(root, 0);
      Event.setEventListener(root, null);
      root.getStyle().clearVisibility();
    }

    internalSetScrollbarThickness(Math.max(1d, px));
  }

  /**
   * Gets the scrollbar's thickness.
   * <p>
   * This value will differ from the value in the DOM, if the thickness was
   * set to 0 with {@link #setScrollbarThickness(double)}, as the scrollbar is
   * then treated as "invisible."
   * 
   * @return the scrollbar's thickness in pixels
   */
  public final double getScrollbarThickness() {
    if (!isInvisibleScrollbar) {
      return parseCssDimensionToPixels(internalGetScrollbarThickness());
    } else {
      return 0;
    }
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
    return getOffsetSize() < getScrollSize();
  }

  /**
   * Recalculates the max scroll position.
   */
  public void recalculateMaxScrollPos() {
    double scrollSize = getScrollSize();
    double offsetSize = getOffsetSize();
    maxScrollPos = Math.max(0, scrollSize - offsetSize);

    // make sure that the correct max scroll position is maintained.
    setScrollPos(scrollPos);
  }

  /**
   * Adds handler for the scrollbar handle visibility.
   * 
   * @param handler
   *            the {@link VisibilityHandler} to add
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addVisibilityHandler(final VisibilityHandler handler) {
    return getHandlerManager().addHandler(VisibilityChangeEvent.TYPE, handler);
  }

  /**
   * Locks or unlocks the scrollbar bundle.
   * <p>
   * A locked scrollbar bundle will refuse to scroll, both programmatically
   * and via user-triggered events.
   * 
   * @param isLocked
   *            <code>true</code> to lock, <code>false</code> to unlock
   */
  public void setLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }

  /**
   * Checks whether the scrollbar bundle is locked or not.
   * 
   * @return <code>true</code> iff the scrollbar bundle is locked
   */
  public boolean isLocked() {
    return isLocked;
  }

  /**
   * Adds a scroll handler to the scrollbar bundle.
   * 
   * @param handler The handler to add
   * @return the registration object for the handler registration
   */
  public HandlerRegistration addScrollHandler(final ScrollHandler handler) {
    return getHandlerManager().addHandler(ScrollEvent.getType(), handler);
  }
  
  /**
   * This is a method that JSNI can call to synchronize the object state from
   * the DOM.
   */
  public final void updateScrollPosFromDom() {
    int newScrollPos = internalGetScrollPos();
    if (!isLocked()) {
      scrollPos = newScrollPos;
      scrollEventFirer.scheduleEvent();
    } else if (scrollPos != newScrollPos) {
      // we need to actually undo the setting of the scroll.
      internalSetScrollPos(toInt32(scrollPos));
    }
  }

  /**
   * Force the scroll bar to be visible with CSS. In practice, this means to
   * set either <code>overflow-x</code> or <code>overflow-y</code> to "
   * <code>scroll</code>" in the scrollbar's direction.
   * <p>
   * This is an IE8 workaround, since it doesn't always show scrollbars with
   * <code>overflow: auto</code> enabled.
   */
  protected void forceScrollbar(boolean enable) {
    if (enable) {
      root.getStyle().clearDisplay();
    } else {
      root.getStyle().setDisplay(Display.NONE);
    }
    internalForceScrollbar(enable);
  }

  /**
   * Returns the handler manager.
   * @return The handler manager.
   */
  protected HandlerManager getHandlerManager() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    
    return handlerManager;
  }

  /**
   * Parses the CSS pixel size.
   * @param size The CSS size.
   * @return The pixel size as number.
   */
  private static double parseCssDimensionToPixels(String size) {
    /*
     * Sizes of elements are calculated from CSS rather than
     * element.getOffset*() because those values are 0 whenever display:
     * none. Because we know that all elements have populated
     * CSS-dimensions, it's better to do it that way.
     * 
     * Another solution would be to make the elements visible while
     * measuring and then re-hide them, but that would cause unnecessary
     * reflows that would probably kill the performance dead.
     */
    if (size.isEmpty()) {
      return 0;
    } else {
      assert size.endsWith("px") : "Can't parse CSS dimension \"" + size + "\"";
      return Double.parseDouble(size.substring(0, size.length() - 2));
    }
  }

  /**
   * Truncates a double such that no decimal places are retained.
   * <p>
   * E.g. {@code trunc(2.3d) == 2.0d} and {@code trunc(-2.3d) == -2.0d}.
   * 
   * @param num The double value to be truncated
   * @return the {@code num} value without any decimal digits
   */
  private static double truncate(double num) {
    if (num > 0) {
      return Math.floor(num);
    } else {
      return Math.ceil(num);
    }
  }

  /**
   * Sets the offset size.
   * @param px The offset size in pixels.
   */
  private void setOffsetSizeNow(double px) {
    internalSetOffsetSize(Math.max(0, truncate(px)));
    recalculateMaxScrollPos();
    fireVisibilityChangeIfNeeded();
    if (offsetSizeTemporaryScrollHandler != null) {
      offsetSizeTemporaryScrollHandler.removeHandler();
      offsetSizeTemporaryScrollHandler = null;
    }
  }

  /**
   * Sets the scroll size in pixels.
   * @param px The scroll size.
   */
  private void setScrollSizeNow(double px) {
    internalSetScrollSize(Math.max(0, px));
    recalculateMaxScrollPos();
    fireVisibilityChangeIfNeeded();
    if (scrollSizeTemporaryScrollHandler != null) {
      scrollSizeTemporaryScrollHandler.removeHandler();
      scrollSizeTemporaryScrollHandler = null;
    }
  }

  /**
   * Fires the visibility change event.
   */
  private void fireVisibilityChangeIfNeeded() {
    final boolean       oldHandleIsVisible = scrollHandleIsVisible;
    
    scrollHandleIsVisible = showsScrollHandle();
    if (oldHandleIsVisible != scrollHandleIsVisible) {
      final VisibilityChangeEvent event = new VisibilityChangeEvent(scrollHandleIsVisible);
      getHandlerManager().fireEvent(event);
    }
  }

  /**
   * Converts a double into an integer by JavaScript's terms.
   * <p>
   * Implementation copied from {@link Element#toInt32(double)}.
   * 
   * @param val
   *            the double value to convert into an integer
   * @return the double value converted to an integer
   */
  private static native int toInt32(double val) /*-{
    return val | 0;
  }-*/;
  
  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------

  /**
   * Returns the scroll direction of this scroll bar bundle.
   * 
   * @return the scroll direction of this scroll bar bundle
   */
  public abstract Direction getDirection();

  /**
   * Gets the scrollbar's thickness as defined in the DOM.
   * 
   * @return the scrollbar's thickness as defined in the DOM, in pixels
   */
  protected abstract String internalGetScrollbarThickness();

  /**
   * Modifies {@link #scrollSizeElement scrollSizeElement's} dimensions in the
   * opposite axis to what the scrollbar is representing.
   * 
   * @param px The dimension that {@link #scrollSizeElement} should take in
   *           the opposite axis to what the scrollbar is representing
   */
  protected abstract void internalSetScrollbarThickness(double px);

  /**
   * Retrieves the element's scroll position (scrollTop or scrollLeft).
   * <p>
   * <em>Note:</em> The parameter here is a type of integer (instead of a
   * double) by design. The browsers internally convert all double values into
   * an integer value. To make this fact explicit, this API has chosen to
   * force integers already at this level.
   * 
   * @return integer pixel value of the scroll position
   */
  protected abstract int internalGetScrollPos();

  /**
   * Modifies {@link #scrollSizeElement scrollSizeElement's} dimensions in
   * such a way that the scrollbar is able to scroll a certain number of
   * pixels in the axis it is representing.
   * 
   * @param px The new size of {@link #scrollSizeElement} in the dimension this scrollbar is representing
   */
  protected abstract void internalSetScrollSize(double px);

  /**
   * Modifies the element's scroll position (scrollTop or scrollLeft).
   * <p>
   * <em>Note:</em> The parameter here is a type of integer (instead of a
   * double) by design. The browsers internally convert all double values into
   * an integer value. To make this fact explicit, this API has chosen to
   * force integers already at this level.
   * 
   * @param px integer pixel value to scroll to
   */
  protected abstract void internalSetScrollPos(int px);
  
  /**
   * Returns the internal offset size.
   * @return The internal offset size.
   */
  public abstract String internalGetOffsetSize();
  
  /**
   * Forces the scroll bar to be visible.
   * @param enable The visibility of the scroll bar.
   */
  protected abstract void internalForceScrollbar(boolean enable);

  /**
   * Modifies {@link #root root's} dimensions in the axis the scroll bar is
   * representing.
   * 
   * @param px The new size of {@link #root} in the dimension this scroll bar is representing
   */
  protected abstract void internalSetOffsetSize(double px);
  
  /**
   * Returns the internal scroll size.
   * @return The internal scroll size.
   */
  protected abstract String internalGetScrollSize();

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * A scroll bar resizer.
   */
  private class TemporaryResizer {

    /**
     * Shows the scroll bar element.
     */
    public void show() {
      internalSetScrollbarThickness(OSX_INVISIBLE_SCROLLBAR_FAKE_SIZE_PX);
      root.getStyle().setVisibility(Visibility.VISIBLE);
      timer.schedule(TEMPORARY_RESIZE_DELAY);
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private static final int    TEMPORARY_RESIZE_DELAY = 1000;
    private final Timer         timer = new Timer() {
      
      @Override
      public void run() {
        internalSetScrollbarThickness(1);
        root.getStyle().setVisibility(Visibility.HIDDEN);
      }
    };
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  protected final Element               root = DOM.createDiv();
  protected final Element               scrollSizeElement = DOM.createDiv();
  protected boolean                     isInvisibleScrollbar = false;
  private double                        scrollPos = 0;
  private double                        maxScrollPos = 0;
  private boolean                       scrollHandleIsVisible = false;
  private boolean                       isLocked = false;
  private HandlerManager                handlerManager;
  private TemporaryResizer              invisibleScrollbarTemporaryResizer = new TemporaryResizer();
  private final ScrollEventFirer        scrollEventFirer = new ScrollEventFirer(this);
  private HandlerRegistration           scrollSizeTemporaryScrollHandler;
  private HandlerRegistration           offsetSizeTemporaryScrollHandler;
  /**
   * The pixel size for OSX's invisible scrollbars.
   * <p>
   * Touch devices don't show a scrollbar at all, so the scrollbar size is
   * irrelevant in their case. There doesn't seem to be any other popular
   * platforms that has scrollbars similar to OSX. Thus, this behavior is
   * tailored for OSX only, until additional platforms start behaving this
   * way.
   */
  private static final int              OSX_INVISIBLE_SCROLLBAR_FAKE_SIZE_PX = 13;
}
