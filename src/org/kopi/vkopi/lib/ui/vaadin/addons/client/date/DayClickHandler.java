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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.date;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * A day click handler.
 */
public class DayClickHandler implements ClickHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DayClickHandler</code> instance.
   * @param owner The owner calendar pane.
   */
  public DayClickHandler(VCalendarPanel owner) {
    this.owner = owner;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @SuppressWarnings("deprecation")
  @Override
  public void onClick(ClickEvent event) {
    if (((Day)event.getSource()).isOffMonth()) {
      return; // off month days cannot be chosen.
    }
    
    Date 	newDate = ((Day) event.getSource()).getDate();
    
    if (newDate.getMonth() != owner.displayedMonth.getMonth() || newDate.getYear() != owner.displayedMonth.getYear()) {
      // If an off-month date was clicked, we must change the
      // displayed month and re-render the calendar (#8931)
      owner.displayedMonth.setMonth(newDate.getMonth());
      owner.displayedMonth.setYear(newDate.getYear());
      owner.renderCalendar();
    }
    owner.focusDay(newDate);
    owner.selectFocused();
    owner.onSubmit();
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final VCalendarPanel			owner;
}
