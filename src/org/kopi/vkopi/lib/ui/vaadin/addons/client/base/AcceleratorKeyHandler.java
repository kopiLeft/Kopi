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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;


/**
 * An accelerator key handler can handle accelerator
 * key combinations.
 */
@SuppressWarnings("deprecation")
public class AcceleratorKeyHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new instance.
   */
  public AcceleratorKeyHandler() {
    kcs = new ArrayList<AcceleratorKeyCombination>();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Handle a keyboard event.
   * @param event The event to be handled
   */
  public void handleKeyboardEvent(final Event event) {
    final int 	modifiers = KeyboardListenerCollection.getKeyboardModifiers(event);
    final char 	keyCode = (char) DOM.eventGetKeyCode(event);

    if (keyCode == 0) {
      return;
    }
    
    for (Iterator<AcceleratorKeyCombination> iterator = kcs.iterator(); iterator.hasNext();) {
      AcceleratorKeyCombination		kc = iterator.next();
      
      if (kc != null && kc.equals(new AcceleratorKeyCombination(keyCode, modifiers))) {
	// we should have one accelerator enabled per combination key
	// otherwise, it would pick the first found one which may be the wrong actor
	if (kc.getOwner().isEnabled()) {
	  fireAction(event, kc.getOwner());
	  break;
	}
      }
    }
  }
  
  /**
   * Fires the registered key combination.
   * @param event The native browser event.
   */
  private void fireAction(final Event event, final HasAcceleratorKey owner) {
    event.preventDefault();
    
    if (owner != null) {
      if (owner instanceof Widget) {
        shakeTarget(((Widget)owner).getElement());
        Scheduler.get().scheduleDeferred(new Command() {

          @Override
          public void execute() {
            shakeTarget(((Widget)owner).getElement());
          }
        });
      }

      Scheduler.get().scheduleDeferred(new Command() {
	
	@Override
	public void execute() {
	  owner.onKeyPress();
	}
      });
    }
  }
  
  /**
   * Adds a new key combination.
   * @param kc The key combination.
   */
  public void addKeyCombination(AcceleratorKeyCombination kc) {
    kcs.add(kc);
  }

  /**
   * We try to fire value change in the component the key combination was
   * typed. Eg. textfield may contain newly typed text that is expected to be
   * sent to server. This is done by removing focus and then returning it
   * immediately back to target element.
   */
  private static void shakeTarget(final Element e) {
    blur(e);
    if (BrowserInfo.get().isOpera()) {
      // will mess up with focus and blur event if the focus is not
      // deferred. Will cause a small flickering, so not doing it for all
      // browsers.
      Scheduler.get().scheduleDeferred(new Command() {
	
	@Override
	public void execute() {
	  focus(e);
	}
      });
    } else {
      focus(e);
    }
  }

  /**
   * Native blur for an element.
   * @param e The element.
   */
  private static native void blur(Element e)
  /*-{
      if (e.blur) {
        e.blur();
     }
  }-*/;

  /**
   * Native focus for an element.
   * @param e The element.
   */
  private static native void focus(Element e)
  /*-{
      if (e.blur) {
        e.focus();
     }
  }-*/;
  
  //---------------------------------------------------
  // HELPER INTERFACES
  //---------------------------------------------------

  /**
   * An interface implemented by those users of this helper class that want to
   * support special components that don't properly
   * propagate key down events. Those components can build support for
   * shortcut actions by traversing the closest
   * {@link AcceleratorKeyHandlerOwner} from the component hierarchy an
   * passing key down events to {@link AcceleratorKeyHandler}.
   */
  public interface AcceleratorKeyHandlerOwner {
    
    /**
     * Returns the {@link AcceleratorKeyHandler} object.
     * @return The {@link AcceleratorKeyHandler} object.
     */
    public AcceleratorKeyHandler getAcceleratorKeyHandler();
  }
  
  /**
   * An interface implemented by those widgets that can have
   * accelerator keys like actors.
   */
  public interface HasAcceleratorKey extends HasEnabled {
    
    /**
     * Fired when the accelerator key is pressed.
     */
    public void onKeyPress();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final List<AcceleratorKeyCombination>		kcs;
}
