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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.date.DateChooserServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.date.DateChooserState;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * The date chooser server component.
 */
@SuppressWarnings("serial")
public class DateChooser extends AbstractComponent {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new date chooser instance.
   */
  public DateChooser() {
    registerRpc(rpc);
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Sets the selected date.
   * @param selected The selected date.
   */
  public void setSelectedDate(Date selected) {
    getState().selected = selected;
  }
  
  /**
   * Sets the time zone offset regarding to UTC.
   * @param offset The time zone offset.
   */
  public void setOffset(int offset) {
    getState().offset = offset;
  }
  
  /**
   * Show the list dialog relative to the given component.
   * @param reference The reference component.
   */
  public void showRelativeTo(Component reference) {
    getState().reference = reference;
  }
  
  /**
   * Sets the date chooser locale.
   * @param locale the date chooser locale.
   */
  public void setLocale(String locale) {
    getState().locale = locale;
  }
  
  /**
   * Sets the localized today caption.
   * @param today The localized today caption.
   */
  public void setToDay(String today) {
    getState().today = today;
  }
  
  @Override
  protected DateChooserState getState() {
    return (DateChooserState) super.getState();
  }
  
  /**
   * Registers a new date chooser listener.
   * @param l The listener to be registered.
   */
  public void addDateChooserListener(DateChooserListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new date chooser listener.
   * @param l The listener to be removed.
   */
  public void removeDateChooserListener(DateChooserListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a close event.
   * @param selected The selected date.
   */
  protected void fireOnClose(Date selected) {
    for (DateChooserListener l : listeners) {
      if (l != null) {
	l.onClose(selected);
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<DateChooserListener>  	listeners = new ArrayList<DateChooserListener>();
  private DateChooserServerRpc		rpc = new DateChooserServerRpc() {
    
    @SuppressWarnings("deprecation")
    @Override
    public void onClose(Date selected, int offset) {
      int	timezoneOffset = new Date().getTimezoneOffset();
      
      if (timezoneOffset == offset) {
        fireOnClose(selected);
      } else {
	if (selected != null) {
	  Calendar	calendar = Calendar.getInstance();

	  calendar.setTime(selected);
	  calendar.add(Calendar.MINUTE, timezoneOffset - offset);
	  fireOnClose(calendar.getTime());
	} else {
	  fireOnClose(null);
	}
      }
    }
  };
}
