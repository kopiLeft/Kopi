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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Handles all logic related to vertical scrolling.
 */
public class Scroller extends JsniWorkaround {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new instance of the {@link Scroller} object. 
   * @param scrollbar A reference to the current instance of {@link VerticalScrollBar}
   */
  public Scroller(VerticalScrollBar scrollbar) {
    super(scrollbar);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected native JavaScriptObject createScrollListenerFunction(VerticalScrollBar scrollbar) /*-{
    var vScroll = scrollbar.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.VerticalScrollBar::verticalScrollbar;
    var vScrollElem = vScroll.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.ScrollbarBundle::getElement()();

    return $entry(function(e) {
      var target = e.target || e.srcElement; // IE8 uses e.scrElement
      
      // in case the scroll event was native (i.e. scroll bars were dragged, or
      // the scrollTop/Left was manually modified), the bundles have old cache
      // values. We need to make sure that the caches are kept up to date.
      if (target === vScrollElem) {
        vScroll.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.ScrollbarBundle::updateScrollPosFromDom()();
      } else {
        $wnd.console.error("unexpected scroll target: "+target);
      }
    });
  }-*/;

  @Override
  protected native JavaScriptObject createMousewheelListenerFunction(VerticalScrollBar scrollbar) /*-{
    return $entry(function(e) {
      var deltaY = e.deltaY ? e.deltaY : -0.5*e.wheelDeltaY;
          
      // IE8 has only delta y
      if (isNaN(deltaY)) {
        deltaY = -0.5*e.wheelDelta;
      }

      scrollbar.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.VerticalScrollBar::moveScrollFromEvent(*)(deltaY, e);
    });
  }-*/;
  
  /**
   * Attaches the scroll listener to the given element.
   * @param element The target element.
   */
  public native void attachScrollListener(Element element)
  /*
   * Attaching events with JSNI instead of the GWT event mechanism because
   * GWT didn't provide enough details in events, or triggering the event
   * handlers with GWT bindings was unsuccessful. Maybe, with more time
   * and skill, it could be done with better success. JavaScript overlay
   * types might work. This might also get rid of the JsniWorkaround
   * class.
   */
  /*-{
     if (element.addEventListener) {
       element.addEventListener("scroll", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::scrollListenerFunction);
     } else {
       element.attachEvent("onscroll", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::scrollListenerFunction);
     }
  }-*/;

  /**
   * Detaches the scroll listener from the given element.
   * @param element The target element.
   */
  public native void detachScrollListener(Element element)
  /*
   * Attaching events with JSNI instead of the GWT event mechanism because
   * GWT didn't provide enough details in events, or triggering the event
   * handlers with GWT bindings was unsuccessful. Maybe, with more time
   * and skill, it could be done with better success. JavaScript overlay
   * types might work. This might also get rid of the JsniWorkaround
   * class.
   */
  /*-{
    if (element.addEventListener) {
      element.removeEventListener("scroll", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::scrollListenerFunction);
    } else {
      element.detachEvent("onscroll", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::scrollListenerFunction);
    }
  }-*/;

  /**
   * Attaches a mouse wheel listener to the given element.
   * @param element The target element.
   */
  public native void attachMousewheelListener(Element element)
  /*
   * Attaching events with JSNI instead of the GWT event mechanism because
   * GWT didn't provide enough details in events, or triggering the event
   * handlers with GWT bindings was unsuccessful. Maybe, with more time
   * and skill, it could be done with better success. JavaScript overlay
   * types might work. This might also get rid of the JsniWorkaround
   * class.
   */
  /*-{
    if (element.addEventListener) {
      // firefox likes "wheel", while others use "mousewheel"
      var eventName = 'onmousewheel' in element ? 'mousewheel' : 'wheel';
      element.addEventListener(eventName, this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::mousewheelListenerFunction);
    } else {
      // IE8
      element.attachEvent("onmousewheel", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::mousewheelListenerFunction);
    }
  }-*/;
  
  /**
   * Detaches the mouse wheel listener from the given element.
   * @param element The target element.
   */
  public native void detachMousewheelListener(Element element)
  /*
   * Detaching events with JSNI instead of the GWT event mechanism because
   * GWT didn't provide enough details in events, or triggering the event
   * handlers with GWT bindings was unsuccessful. Maybe, with more time
   * and skill, it could be done with better success. JavaScript overlay
   * types might work. This might also get rid of the JsniWorkaround
   * class.
   */
  /*-{
    if (element.addEventListener) {
      // firefox likes "wheel", while others use "mousewheel"
      var eventName = element.onwheel===undefined?"mousewheel":"wheel";
      element.removeEventListener(eventName, this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::mousewheelListenerFunction);
    } else {
      // IE8
      element.detachEvent("onmousewheel", this.@org.kopi.vkopi.lib.ui.vaadin.addons.client.scrollbar.JsniWorkaround::mousewheelListenerFunction);
    }
  }-*/;
}
