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

/**
 * A workaround-class for GWT and JSNI.
 * <p>
 * GWT is unable to handle some method calls to Java methods in inner-classes
 * from within JSNI blocks. Having that inner class extend a non-inner-class (or
 * implement such an interface), makes it possible for JSNI to indirectly refer
 * to the inner class, by invoking methods and fields in the non-inner-class
 * API.
 * 
 * @see {@link Scroller}
 */
/*package*/ abstract class JsniWorkaround {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new instance of the {@link JsniWorkaround} object.
   * @param scrollbar A reference to the current instance of {@link VerticalScrollBar}
   */
  protected JsniWorkaround(VerticalScrollBar scrollbar) {
    scrollListenerFunction = createScrollListenerFunction(scrollbar);
    mousewheelListenerFunction = createMousewheelListenerFunction(scrollbar);
    this.scrollbar = scrollbar;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * A method that constructs the JavaScript function that will be stored into
   * {@link #scrollListenerFunction}.
   * 
   * @param scrollbar A reference to the current instance of {@link VerticalScrollBar}
   */
  protected abstract JavaScriptObject createScrollListenerFunction(VerticalScrollBar scrollbar);

  /**
   * A method that constructs the JavaScript function that will be stored into
   * {@link #mousewheelListenerFunction}.
   * 
   * @param scrollbar a reference to the current instance of {@link VerticalScrollBar}
   */
  protected abstract JavaScriptObject createMousewheelListenerFunction(VerticalScrollBar scrollbar);

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  protected final VerticalScrollBar       scrollbar;
  
  /**
   * A JavaScript function that handles the scroll DOM event, and passes it on
   * to Java code.
   * 
   * @see #createScrollListenerFunction(VerticalScrollBar)
   * @see VerticalScrollBar#onScroll()
   * @see Scroller#onScroll()
   */
  protected final JavaScriptObject      scrollListenerFunction;

  /**
   * A JavaScript function that handles the mousewheel DOM event, and passes
   * it on to Java code.
   * 
   * @see #createMousewheelListenerFunction(VerticalScrollBar)
   * @see VerticalScrollBar#onScroll()
   * @see Scroller#onScroll()
   */
  protected final JavaScriptObject      mousewheelListenerFunction;
}
