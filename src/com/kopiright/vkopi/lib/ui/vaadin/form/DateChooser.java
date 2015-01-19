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

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.type.Date;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullMonth;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The <code>DateChooser</code> is date selection component.
 */
public class DateChooser extends Panel implements UComponent, ValueChangeListener {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DateChooser</code> instance.
   * @param date The initial date.
   */
  public DateChooser(Date date) {
    setImmediate(true);
    addStyleName("light");
    setSizeUndefined();
    selectedDate = date;
    cal = new CalendarPane();
    cal.setImmediate(true);
    todayButton = new Button(VlibProperties.getString("today"));
    todayButton.addStyleName("small default");
    todayButton.addClickListener(new ClickListener() {
      
      private static final long serialVersionUID = -7103888370716161397L;

      public void buttonClick(ClickEvent event) {
	setSelectedDate(Date.now());
        synchronized (popup) {
          popup.notify(); 
        }
        popup.close();	
      }
    });
    
    createContent();
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  public static Date selectDate(Date date) {
    DateChooser		chooser = new DateChooser(date);
    
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
  @SuppressWarnings("serial")
  private Date doModal(final Date date) {
    popup = new Window(null, this);
    popup.setImmediate(true);
    popup.setModal(true);
    popup.setDraggable(false);
    popup.setResizable(false);
    popup.addShortcutListener(new ShortcutListener("escape", KeyCode.ESCAPE, null) {

      @Override
      public void handleAction(Object sender, Object target) {
        BackgroundThreadHandler.releaseLock(popup);
        popup.close();
      }
    });
    
    popup.addCloseListener(new CloseListener() {
      
      @Override
      public void windowClose(CloseEvent e) {
	BackgroundThreadHandler.releaseLock(popup);
      }
    });
    
    BackgroundThreadHandler.startAndWait(new Runnable() {
      
      @Override
      public void run() {
        UI.getCurrent().addWindow(popup);
        focus();
      }
    }, popup);
     
    return selectedDate;
  }
  
  /**
   * Disposes the date chooser.
   */
  protected void dispose() {
    popup.close();
  }
  
  /**
   * Destroys the date chooser.
   */
  protected void destroy() {
    cal = null;
    selectedDate = null;
    popup = null;
    todayButton = null;
  }

  /**
   * Returns the first day of the selected month.
   * @return The first day of the selected month.
   */
  public int getFirstDay() {
    return firstDay;
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
  // VALUECHANGELISTENER IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void valueChange(ValueChangeEvent event) {
    Calendar	cal = Calendar.getInstance(ApplicationContext.getDefaultLocale());
    
    cal.setTime((java.util.Date) event.getProperty().getValue());
    setSelectedDate(new NotNullDate(cal));
    popup.close();
  }
  
  //-------------------------------------------------
  // DATE CHOOSER CONTENT
  //-------------------------------------------------
  
  /**
   * Creates the date chooser content.
   */
  public void createContent() {
    VerticalLayout	content;

    content = new VerticalLayout();
    content.setSpacing(true);
    content.addComponent(cal);
    content.setComponentAlignment(cal, Alignment.TOP_CENTER);
    content.addComponent(todayButton);
    content.setComponentAlignment(todayButton, Alignment.BOTTOM_CENTER);
    setContent(content);
  }
  
  //-------------------------------------------------
  // INNER CLASSES
  //-------------------------------------------------
  
  /**
   * The <code>CalendarPane</code> is an {@link InlineDateField}
   * for date chooser.
   */
  /*package*/ class CalendarPane extends InlineDateField {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>CalendarPane</code> instance.
     */
    public CalendarPane() {
      setImmediate(true);
      setValue(getSelectedDate().toCalendar().getTime());
      setLocale(ApplicationContext.getDefaultLocale());
      setResolution(Resolution.DAY);
      setShowISOWeekNumbers(true);
      addValueChangeListener(DateChooser.this);
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private static final long	serialVersionUID = -3958329773743250969L;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Date				selectedDate;
  private Button			todayButton;
  private CalendarPane			cal;
  private int				firstDay;
  private Window			popup;
  private static final long		serialVersionUID = 9070604800608635033L;
}