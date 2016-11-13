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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VSpan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A day widget that wraps a span element inside.
 */
public class Day extends VSpan {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  @SuppressWarnings("deprecation")
  public Day(Date date) {
    this.date = date;
    setText(String.valueOf(date.getDate()));
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Registers a click handler on this day widget.
   * @param handler The handler to be registered.
   * @return The registration handler.
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return addDomHandler(handler, ClickEvent.getType());
  }
  
  /**
   * Returns the day widget date.
   * @return The day widget date.
   */
  public Date getDate() {
    return date;
  }
  
  /**
   * Returns {@code true} is it is off month day.
   * @return  {@code true} is it is off month day.
   */
  public boolean isOffMonth() {
    return offMonth;
  }
  
  /**
   * Sets the day as off month day.
   * @param offMonth Is it an off month day.
   */
  public void setOffMonth(boolean offMonth) {
    this.offMonth = offMonth;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Date				date;
  private boolean				offMonth;
}
