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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VImage;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;

public class VScrollBar extends Composite implements HasEnabled, HasValue<Integer>, HasValueChangeHandlers<Integer> {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public VScrollBar(ApplicationConnection connection) {
    VImage		knob;
    
    content = new FlowPanel();
    panel = new FocusPanel();
    up = new VButton();
    up.setStyleName("scroll-button");
    down = new VButton();
    down.setStyleName("scroll-button");
    up.setIcon("caret-up");
    down.setIcon("caret-down");
    up.getElement().setAttribute("hideFocus", "true");
    up.getElement().getStyle().setProperty("outline", "0px");
    down.getElement().setAttribute("hideFocus", "true");
    down.getElement().getStyle().setProperty("outline", "0px");
    panel.getElement().setAttribute("hideFocus", "true");
    panel.getElement().getStyle().setProperty("outline", "0px");
    panel.setStyleName("k-scrollbar-shell");
    knobImage = new SimplePanel();
    knob = new VImage();
    knob.setSrc(ResourcesUtil.getImageURL(connection, Icons.KNOB));
    knobImage.setStyleName("k-scrollbar-knob");
    knobImage.setWidget(knob);
    content.add(up);
    panel.add(knobImage);
    content.add(panel);
    content.add(down);
    content.sinkEvents(Event.MOUSEEVENTS | Event.KEYEVENTS | Event.FOCUSEVENTS | Event.ONMOUSEWHEEL);
    initWidget(content);
    setStyleName("k-scrollbar");
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void setVisible(boolean visible) {
    if (visible) {
      content.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      panel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      up.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      down.getElement().getStyle().setVisibility(Visibility.VISIBLE);
      knobImage.getElement().getStyle().setVisibility(Visibility.VISIBLE);
    } else {
      content.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      panel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      up.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      down.getElement().getStyle().setVisibility(Visibility.HIDDEN);
      knobImage.getElement().getStyle().setVisibility(Visibility.HIDDEN);
    }
  }
  
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  @Override
  public Integer getValue() {
    return Double.valueOf(curValue).intValue();
  }

  @Override
  public void setValue(Integer value) {
    setCurrentValue(value, false);
  }

  @Override
  public void setValue(Integer value, boolean fireEvents) {
    setCurrentValue(value, fireEvents);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
  
  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    this.enabled = enabled;
    if (!enabled) {
      panel.addStyleName("v-disabled");
      up.addStyleName("v-disabled");
      down.addStyleName("v-disabled");
      addStyleName("v-disabled");
      knobImage.addStyleName("v-disabled");;
    } else {
      panel.removeStyleName("v-disabled");
      up.removeStyleName("v-disabled");
      down.removeStyleName("v-disabled");
      removeStyleName("v-disabled");
      knobImage.removeStyleName("v-disabled");
    }
    up.setEnabled(enabled);
    down.setEnabled(enabled);
  }
  
  @Override
  public void setHeight(String height) {
    content.setHeight(height);
    panel.setHeight((content.getElement().getClientHeight() - (6 + up.getElement().getClientHeight() + down.getElement().getClientHeight() )) + "px");
  }
  
  /**
   * Allows access to the key events.
   * @param keyCode
   */
  public void doKeyAction(final int keyCode) {
    switch (keyCode) {
      case KeyCodes.KEY_HOME:
        setCurrentValue(minValue);
        break;
      case KeyCodes.KEY_END:
        setCurrentValue(maxValue);
        break;
      case KeyCodes.KEY_PAGEUP:
        shiftUp(pageSize);
        break;
      case KeyCodes.KEY_PAGEDOWN:
        shiftDown(pageSize);
        break;
      case KeyCodes.KEY_UP:
	slidingKeyboard = true;
        shiftUp(1);
        keyTimer.schedule(400, true, 1);
        break;
      case KeyCodes.KEY_DOWN:
	slidingKeyboard = true;
        shiftDown(1);
        keyTimer.schedule(400, false, 1);
        break;
      case KeyCodes.KEY_SPACE:
        setCurrentValue(minValue + getTotalRange() / 2);
        break;
      default:
    }
  }
  
  /**
   * Draw the knob where it is supposed to be relative to the line.
   */
  @SuppressWarnings("deprecation")
  private void drawKnob() {
    // Abort if not attached
    if (!isAttached()) {
      return;
    }
    // Move the knob to the correct position
    final Element knobElement = knobImage.getElement();
    final int knobHeight = knobElement.getOffsetHeight();

    final int knobTopOffset = Math.max(0, (int) (getKnobPercent() * panel.getOffsetHeight()) - knobHeight);
    DOM.setStyleAttribute(knobElement, "top", knobTopOffset + "px");
  }
  /**
   * Return the current value.
   * @return the current value
   */
  public final double getCurrentValue() {
    return curValue;
  }

  /**
   * Get the percentage of the knob's position relative to the size of the line.
   * The return value will be between 0.0 and 1.0.
   * @return the current percent complete
   */
  protected final double getKnobPercent() {
    // If we have no range
    if (maxValue <= minValue) {
      return 0;
    }

    // Calculate the relative progress
    final double percent = (curValue - minValue) / (maxValue - minValue);
    return Math.max(0.0, Math.min(1.0, percent));
  }

  /**
   * Calculate the knob's position in coordinates.
   * @return the knob's position
   */
  private int getKnobPosition() {
    return knobImage.getAbsoluteTop();
  }

  /**
   * Return the max value.
   * @return the max value
   */
  public final double getMaxValue() {
    return maxValue;
  }

  /**
   * Return the minimum value.
   * @return the minimum value
   */
  public final double getMinValue() {
    return minValue;
  }

  /**
   * Retrieve the page size.
   * @return the page size
   */
  public final int getPageSize() {
    return pageSize;
  }

  /**
   * Return the total range between the minimum and maximum values.
   * @return the total range
   */
  public final double getTotalRange() {
    if (minValue > maxValue) {
      return 0;
    } else {
      return maxValue - minValue;
    }
  }
  
  /**
   * Listen for events that will move the knob.
   * @param event the event that occurred
   */
  @SuppressWarnings("deprecation")
  @Override
  public final void onBrowserEvent(final Event event) {
    super.onBrowserEvent(event);
    if (enabled) {
      switch (DOM.eventGetType(event)) {
      case Event.ONBLUR:
	if (slidingMouse) {
	  slidingMouse = false;
	  slideKnob(event, true);
	}
	break;
	// Mousewheel events
      case Event.ONMOUSEWHEEL:
	onWheelEvent(event);
	break;

	// Shift left or right on key press
      case Event.ONKEYDOWN:
	onKeyDownEvent(event);
	break;

	// Mouse Events
      case Event.ONMOUSEDOWN:
	if (sliderClicked(event)) {
	  slidingMouse = true;
	  DOM.setCapture(getElement());
	  DOM.eventPreventDefault(event);
	  slideKnob(event, false);
	} else if (upButtonClicked(event)) {
	  shiftUp(1);
	  slidingKeyboard = true;
	  keyTimer.schedule(400, true, 1);
	} else if (downButtonClicked(event)) {
	  shiftDown(1);
	  slidingKeyboard = true;
	  keyTimer.schedule(400, false, 1);
	} else {
	  pageKnob(event);
	}
	break;
      case Event.ONMOUSEUP:
	DOM.releaseCapture(getElement());
	if (slidingMouse) {
	  slideKnob(event, true);
	  slidingMouse = false;
	} else if (slidingKeyboard) {
	  slidingKeyboard = false;
	  keyTimer.cancel();
	}
	break;
      case Event.ONMOUSEMOVE:
	if (slidingMouse) {
	  slideKnob(event, false);
	}
	break;
      default:
      }
    }
  }

  @SuppressWarnings("deprecation")
  public void onKeyDownEvent(final Event event) {
    DOM.eventPreventDefault(event);
    if (slidingMouse) {
      slidingMouse = false;
    }
    doKeyAction(DOM.eventGetKeyCode(event));
  }
  
  public final void onWheelEvent(final Event event) {
    event.preventDefault();
    final int deltaY = event.getMouseWheelVelocityY();
    if (deltaY > 0) {
      shiftDown(deltaY);
    } else {
      shiftUp(Math.abs(deltaY));
    }
  }

  /**
   * Perform a page action.
   * @param event the event
   */
  @SuppressWarnings("deprecation")
  private void pageKnob(final Event event) {
    final int y = DOM.eventGetClientY(event);
    
    if (y > 0) {
      if (y < getKnobPosition()) {
        shiftUp(pageSize);
      } else {
        shiftDown(pageSize);
      }
    }
  }

  /**
   * Reset the progress to constrain the progress to the current range and
   * redraw the knob as needed.
   * @param fireEvent
   */
  private void resetCurrentValue(final boolean fireEvent) {
    setCurrentValue(getCurrentValue(), fireEvent);
  }

  /**
   * Set the current value and fire the onValueChange event.
   * @param curValue2 the current value
   */
  public final void setCurrentValue(final double curValue2) {
    setCurrentValue(curValue2, true);
  }

  /**
   * Set the current value and optionally fire the onValueChange event.
   * @param curValue2 the current value
   * @param fireEvent fire the onValue change event if true
   */
  public final void setCurrentValue(final double curValue2, final boolean fireEvent) {
    // Confine the value to the range
    curValue = Math.max(minValue, Math.min(maxValue, curValue2));

    // Redraw the knob
    drawKnob();

    // Fire the ValueChangeEvent
    if (fireEvent && enabled) {
      // keyTimer.cancel();
      ValueChangeEvent.fire(this, Double.valueOf(curValue).intValue());
    }
  }

  /**
   * Set the max value.
   * @param maxValue2 the max value
   * @param fireEvent
   */
  public final void setMaxValue(final double maxValue2) {
    maxValue = maxValue2;
    resetCurrentValue(true);
  }

  /**
   * Set the max value.
   * @param maxValue2 the max value
   * @param fireEvent
   */
  public final void setMaxValue(final double maxValue2, final boolean fireEvent) {
    maxValue = maxValue2;
    resetCurrentValue(fireEvent);
  }

  /**
   * Set the minimum value.
   * @param minValue2 the current value
   */
  public final void setMinValue(final double minValue2) {
    minValue = minValue2;
    resetCurrentValue(true);
  }

  /**
   * Set the page size.
   * @param pageSize2 the amount to page.
   */
  public final void setPageSize(final int pageSize2) {
    pageSize = pageSize2;
  }

  /**
   * Shift to the right (greater value).
   * @param numSteps the number of steps to shift
   */
  public final void shiftDown(final int numSteps) {
    setCurrentValue(getCurrentValue() + numSteps);
  }

  /**
   * Shift to the left (smaller value).
   * @param numSteps the number of steps to shift
   */
  public final void shiftUp(final int numSteps) {
    setCurrentValue(getCurrentValue() - numSteps);
  }

  /**
   * Slide the knob to a new location.
   * @param event the mouse event
   * @param fireEvent whether or not to just slide the knob
   */
  @SuppressWarnings("deprecation")
  private void slideKnob(final Event event, final boolean fireEvent) {
    final int y = DOM.eventGetClientY(event);
    if (y > 0) {
      final int lineTop = panel.getAbsoluteTop();
      final double percent = (double) (y - lineTop) / panel.getOffsetHeight() * 1.0;
      setCurrentValue(getTotalRange() * percent + minValue, fireEvent);
    }
  }

  /**
   * Checks whether the slider was clicked or not.
   * @param event the mouse event
   * @return whether the slider was clicked
   */
  private boolean sliderClicked(final Event event) {
    boolean sliderClicked = false;

    if (getUserAgent().contains("msie 6.0")) {
      if (DOM.eventGetTarget(event).equals(knobImage.getElement().getFirstChild())) {
	sliderClicked = true;
      }
    } else if (DOM.eventGetTarget(event).isOrHasChild(knobImage.getElement().getFirstChild())) {
      sliderClicked = true;
    }

    return sliderClicked;
  }

  /**
   * Checks whether the down button was clicked or not.
   * @param event the mouse event
   * @return whether the down button was clicked
   */
  private boolean downButtonClicked(final Event event) {
    if (DOM.eventGetTarget(event).isOrHasChild(down.getElement().getFirstChild())) {
      return true;
    }

    return false;
  }

  /**
   * Checks whether the up button was clicked or not.
   * @param event the mouse event
   * @return whether the up button was clicked
   */
  private boolean upButtonClicked(final Event event) {
    if (DOM.eventGetTarget(event).isOrHasChild(up.getElement().getFirstChild())) {
      return true;
    }

    return false;
  }
  
  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enabled is the scroll bar enabled ?
   * @param value The scroll position.
   */
  public void updateScroll(int pageSize, int maxValue, boolean enabled, int value) {
    if (slidingKeyboard || slidingMouse) {
      setPageSize(pageSize);
    }
    setMaxValue(maxValue);
    setEnabled(enabled);
    //setValue(value); removed for performance problems
  }
  
  /**
   * Gets user agent info
   */
  public static native String getUserAgent() /*-{
    return navigator.userAgent.toLowerCase();
  }-*/;

  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The timer used to continue to shift the knob as the user holds down one of
   * the left/right arrow keys. Only IE auto-repeats, so we just keep catching
   * the events.
   */
  private class KeyTimer extends Timer {

    /**
     * This method will be called when a timer fires. Override it to implement
     * the timer's logic.
     */
    @Override
    public void run() {
      // Highlight the knob on first run
      if (firstRun) {
        firstRun = false;
      }

      // Slide the slider bar
      if (shiftUp) {
        shiftUp(multiplier);
        if (getValue() == minValue) {
          cancel();
        }
      } else {
        shiftDown(multiplier);
        if (getValue() == maxValue) {
          cancel();
        }
      }

      // Repeat this timer until cancelled by keyup event
      schedule(repeatDelay);
    }

    /**
     * Schedules a timer to elapse in the future.
     *
     * @param delayMillis how long to wait before the timer elapses, in
     *          milliseconds
     * @param shiftUp whether to shift up or not
     * @param multiplier the number of steps to shift
     */
    public void schedule(int delayMillis, boolean shiftUp, int multiplier) {
      this.firstRun = true;
      this.shiftUp = shiftUp;
      this.multiplier = multiplier;
      super.schedule(delayMillis);
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    /**
     * A bit indicating that this is the first run.
     */
    private boolean 		firstRun = true;

    /**
     * The delay between shifts, which shortens as the user holds down the
     * button.
     */
    private int 		repeatDelay = 30;

    /**
     * A bit indicating whether we are shifting to a higher or lower value.
     */
    private boolean 		shiftUp = false;

    /**
     * The number of steps to shift with each press.
     */
    private int 		multiplier = 1;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * The current value.
   */
  private double 			curValue;

  /**
   * A bit indicating whether or not the slider is enabled.
   */
  private boolean 			enabled = false;

  /**
   * The knob that slides across the line.
   */
  private final SimplePanel 		knobImage;

  /**
   * The maximum slider value.
   */
  private double 			maxValue;

  /**
   * The minimum slider value.
   */
  private double 			minValue;

  /**
   * The page size value.
   */
  private int 				pageSize;

  /**
   * A bit indicating whether or not we are currently sliding the slider bar due
   * to mouse events.
   */
  private boolean 			slidingMouse = false;
  
  /**
   * A bit indicating whether or not we are currently sliding the slider bar due
   * to keyboard events.
   */
  private boolean			slidingKeyboard = false;
  
  /**
   * The timer used to continue to shift the knob if the user holds down a key.
   */
  private KeyTimer		 	keyTimer = new KeyTimer();
  
  private final VButton			up;
  private final VButton			down;
  private FocusPanel 			panel;
  private FlowPanel			content;
}
