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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * A scroll event firer.
 */
public class ScrollEventFirer {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new firer instance.
   * @param scrollbar The scroll bar bundle instance.
   */
  public ScrollEventFirer(ScrollbarBundle scrollbar) {
    this.scrollbar = scrollbar;
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Schedules the scroll event.
   */
  public void scheduleEvent() {
    if (!isBeingFired) {
      /*
       * We'll gather all the scroll events, and only fire once, once
       * everything has calmed down.
       */
      Scheduler.get().scheduleDeferred(fireEventCommand);
      isBeingFired = true;
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final ScrollbarBundle         scrollbar;
  private boolean                       isBeingFired;
  private final ScheduledCommand        fireEventCommand = new ScheduledCommand() {

    @Override
    public void execute() {

      /*
       * Some kind of native-scroll-event related asynchronous problem
       * occurs here (at least on desktops) where the internal
       * bookkeeping isn't up to date with the real scroll position.
       * The weird thing is, that happens only once, and if you drag
       * scrollbar fast enough. After it has failed once, it never
       * fails again.
       * 
       * Theory: the user drags the scrollbar, and this command is
       * executed before the browser has a chance to fire a scroll
       * event (which normally would correct this situation). This
       * would explain why slow scrolling doesn't trigger the problem,
       * while fast scrolling does.
       * 
       * To make absolutely sure that we have the latest scroll
       * position, let's update the internal value.
       * 
       * This might lead to a slight performance hit (on my computer
       * it was never more than 3ms on either of Chrome 38 or Firefox
       * 31). It also _slightly_ counteracts the purpose of the
       * internal bookkeeping. But since getScrollPos is called 3
       * times (on one direction) per scroll loop, it's still better
       * to have take this small penalty than removing it altogether.
       */
      scrollbar.updateScrollPosFromDom();
      scrollbar.getHandlerManager().fireEvent(new ScrollEvent());
      isBeingFired = false;
    }
  };
}
