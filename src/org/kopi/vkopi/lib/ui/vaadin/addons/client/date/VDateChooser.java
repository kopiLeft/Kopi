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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VInputButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.DateChooserListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.LocaleNotLoadedException;

/**
 * The date chooser widget composed of calendar panel
 * used to choose a date.
 */
public class VDateChooser extends FlowPanel implements CloseHandler<PopupPanel> {

  //---------------------------------------------------
  // CONSTRUCTORs
  //---------------------------------------------------
  
  /**
   * Creates the date chooser widget.
   */
  @SuppressWarnings("serial")
  public VDateChooser() {
    setStylePrimaryName(Styles.DATE_CHOOSER);
    calendarPanel = new VCalendarPanel();
    today = new VInputButton("", new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
	dispose();
	fireOnClose((Date) new Date().clone());
      }
    });
    today.setWidth("100%");
    today.setStyleName("today");
    calendarPanel.setParent(this);
    calendarPanel.setDateTimeService(new DateTimeService());
    calendarPanel.setShowISOWeekNumbers(true);
    add(calendarPanel);
    add(today);
    calendarPanel.setSubmitListener(new SubmitListener() {
      
      @Override
      public void onSubmit() {
	dispose();
	fireOnClose((Date)calendarPanel.getDate().clone());
      }

      @Override
      public void onCancel() {
	dispose();
	fireOnClose(null);
      }
    });

    calendarPanel.setFocusOutListener(new FocusOutListener() {
      
      @Override
      public boolean onFocusOut(DomEvent<?> event) {
	return false;
      }
    });
    
    calendarPanel.setFocusChangeListener(new FocusChangeListener() {
      
      @Override
      public void focusChanged(Date focusedDate) {
	// do nothing
      }
    });
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Closes the chooser dialog.
   */
  protected void dispose() {
    if (popup != null) {
      popup.hide();
    }
  }
  
  /**
   * Initializes the widget.
   * @param connection The application connection.
   */
  public void init(ApplicationConnection connection) {
    popup = new VPopup(connection, true, true);
    popup.addCloseHandler(this);
  }
  
  /**
   * Shows the calendar panel.
   * @param parent The widget parent.
   */
  public void openCalendarPanel(HasWidgets parent) {
    if (popup != null) {
      if (selected != null) {
	calendarPanel.setDate(selected);
      }
      calendarPanel.renderCalendar();
      popup.setWidget(this);
      parent.add(popup);
      if (reference != null) {
	popup.showRelativeTo(reference);
      } else {
	popup.center();
      }
      // focus the widget to activate key navigation.
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          focus();
        }
      });
    }
  }
  
  @Override
  public void onClose(CloseEvent<PopupPanel> event) {
    event.getTarget().clear();
    event.getTarget().removeFromParent(); // remove popup from parent.
    if (event.isAutoClosed()) {
      fireOnClose(null);
    }
  }
  
  /**
   * Widget focus.
   */
  public void focus() {
    calendarPanel.focus();
  }
  
  /**
   * Show the date chooser beside this reference.
   * @param reference The reference widget.
   */
  public void showRelativeTo(Widget reference) {
    this.reference = reference;
  }
  
  /**
   * Sets the selected date.
   * @param selected The selected date.
   * @param offset The time zone offset.
   */
  @SuppressWarnings("deprecation")
  public void setSelectedDate(Date selected, int offset) {
    int		timezoneOffset = new Date().getTimezoneOffset();
    
    if (timezoneOffset == offset) {
      this.selected = selected;
    } else {
      this.selected = addMinutes(selected, timezoneOffset - offset);
    }
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
  @SuppressWarnings("deprecation")
  protected void fireOnClose(Date selected) {
    for (DateChooserListener l : listeners) {
      if (l != null) {
	if (selected != null) {
	  l.onClose(selected, selected.getTimezoneOffset());
	} else {
	  l.onClose(null, 0);
	}
      }
    }
  }
  
  /**
   * Sets the date chooser locale.
   * @param locale The locale ISO representation.
   */
  public void setLocale(String locale) {
    try {
      calendarPanel.getDateTimeService().setLocale(locale);
    } catch (LocaleNotLoadedException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
    }
  }
  
  /**
   * Sets the today caption.
   * @param caption The localized caption.
   */
  public void setTodayCaption(String caption) {
    if (today != null) {
      today.getInputElement().setValue(caption);
    }
  }
  
  /**
   * Adds a set of minutes for the given date.
   * @param date The reference date.
   * @param minutes The minutes to add.
   * @return The new added date.
   */
  private static Date addMinutes(Date date, int minutes) {
    final long		ONE_MINUTE_IN_MILLIS = 60000;//millisecs
    long 		curTimeInMs = date.getTime();
    
    return new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<DateChooserListener>	listeners = new ArrayList<DateChooserListener>();  
  private final VCalendarPanel 		calendarPanel;
  private VPopup			popup;
  private Widget 			reference;
  private Date 				selected;
  private final VInputButton		today;
}
