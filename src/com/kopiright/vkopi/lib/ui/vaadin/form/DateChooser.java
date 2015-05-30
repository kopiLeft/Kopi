/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import java.util.Calendar;

import org.kopi.vaadin.addons.DateChooserListener;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.visual.VApplication;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullMonth;
import com.vaadin.ui.Component;

/**
 * The <code>DateChooser</code> is date selection component.
 */
@SuppressWarnings("serial")
public class DateChooser extends org.kopi.vaadin.addons.DateChooser implements UComponent, DateChooserListener {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DateChooser</code> instance.
   * @param date The initial date.
   */
  @SuppressWarnings("deprecation")
  public DateChooser(Date date, Component reference) {
    setImmediate(true);
    selectedDate = date;
    showRelativeTo(reference);
    setToDay(VlibProperties.getString("today"));
    if (date != null) {
      super.setSelectedDate(date.toCalendar().getTime());
    }
    addDateChooserListener(this);
    setLocale(getApplication().getDefaultLocale().toString());
    setOffset(new java.util.Date().getTimezoneOffset());
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Static date selection.
   * @param date The initial date.
   * @return The selected date.
   */
  public static Date selectDate(Date date, Component reference) {
    DateChooser		chooser = new DateChooser(date, reference);
    
    return chooser.doModal(date);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Launches the date chooser as a modal window. 
   * @param date The initial date.
   * @return The selected date.
   */
  private Date doModal(final Date date) {
    BackgroundThreadHandler.startAndWait(new Runnable() {
      
      @Override
      public void run() {
        getApplication().attachComponent(DateChooser.this);
        getApplication().push(); // push to immediately view the date chooser.
      }
    }, this);
     
    return selectedDate;
  }
  
  /**
   * Disposes the date chooser.
   */
  protected void dispose() {
    getApplication().detachComponent(this);
  }
  
  /**
   * Destroys the date chooser.
   */
  protected void destroy() {
    selectedDate = null;
  }

  /**
   * Returns the first day of the selected month.
   * @return The first day of the selected month.
   */
  public int getFirstDay() {
    return firstDay;
  }
  
  /**
   * Returns the application instance.
   * @return The application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }

  /**
   * Returns The current selected date (can be {@code null})
   * @return The current selected date (can be {@code null})
   */
  public Date getSelectedDate() {
    return selectedDate;
  }

  /**
   * Sets the selected date.
   * @param selectedDate The selected date.
   */
  public void setSelectedDate(Date selectedDate) {
    this.selectedDate = selectedDate;
  }

  /**
   * Returns the number of days in the specified month
   * @return The number of days in the specified month
   */
  /*package*/ static int getDaysInMonth(Date d) {
    return new NotNullMonth(d).getLastDay().getDay();
  }
  
  //-------------------------------------------------
  // DATE CHOOSER LISTENER IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void onClose(java.util.Date selected) {
    if (selected == null) {
      return;
    }
    
    Calendar	cal = Calendar.getInstance(ApplicationContext.getDefaultLocale());
    
    cal.setTime(selected);
    setSelectedDate(new NotNullDate(cal));
    dispose();
    BackgroundThreadHandler.releaseLock(this);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Date				selectedDate;
  private int				firstDay;
}