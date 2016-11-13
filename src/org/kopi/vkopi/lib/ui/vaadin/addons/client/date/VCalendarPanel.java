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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.aria.client.SelectedValue;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.ui.FocusableFlexTable;

/**
 * A calendar panel for date choose.
 */
@SuppressWarnings("deprecation")
public class VCalendarPanel extends FocusableFlexTable implements KeyDownHandler, KeyPressHandler, MouseOutHandler, MouseDownHandler,
  MouseUpHandler, BlurHandler, FocusHandler
{
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new calendar pane widget.
   */
  public VCalendarPanel() {
    getElement().setId(DOM.createUniqueId());
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
    setStyleName(Styles.DATE_CHOOSER + "-calendarpanel");
    Roles.getGridRole().set(getElement());

    /*
     * Firefox auto-repeat works correctly only if we use a key press
     * handler, other browsers handle it correctly when using a key down
     * handler
     */
    if (BrowserInfo.get().isGecko()) {
      addKeyPressHandler(this);
    } else {
      addKeyDownHandler(this);
    }
    addFocusHandler(this);
    addBlurHandler(this);
    days.setCellSpacing(0);
    days.setCellPadding(0);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public void onBlur(final BlurEvent event) {
    if (event.getSource() instanceof VCalendarPanel) {
      hasFocus = false;
      focusDay(null);
    }
  }

  @Override
  public void onFocus(FocusEvent event) {
    if (event.getSource() instanceof VCalendarPanel) {
      hasFocus = true;

      // Focuses the current day if the calendar shows the days
      if (focusedDay != null) {
	focusDay(focusedDate);
      }
    }
  }

  @Override
  public void onMouseOut(MouseOutEvent event) {
    if (mouseTimer != null) {
      mouseTimer.cancel();
    }
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    // Click-n-hold the left mouse button for fast-forward or fast-rewind.
    // Timer is first used for a 500ms delay after mousedown. After that has
    // elapsed, another timer is triggered to go off every 150ms. Both
    // timers are cancelled on mouseup or mouseout.
    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT && event.getSource() instanceof VEventButton) {
      final 	VEventButton sender = (VEventButton) event.getSource();
      
      processClickEvent(sender);
      mouseTimer = new Timer() {
	
	@Override
	public void run() {
	  mouseTimer = new Timer() {
	    @Override
	    public void run() {
	      processClickEvent(sender);
	    }
	  };
	  mouseTimer.scheduleRepeating(150);
	}
      };
      mouseTimer.schedule(500);
    }
  }
  
  @Override
  public void onMouseUp(MouseUpEvent event) {
    if (mouseTimer != null) {
      mouseTimer.cancel();
    }
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {
    handleKeyPress(event);
  }

  @Override
  public void onKeyPress(KeyPressEvent event) {
    handleKeyPress(event);
  }
  
  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    if (initialRenderDone) {
      // Dynamic updates to the stylename needs to render the calendar to
      // update the inner element stylenames
      renderCalendar();
    }
  }

  @Override
  public void setStylePrimaryName(String style) {
    super.setStylePrimaryName(style);
    if (initialRenderDone) {
      // Dynamic updates to the stylename needs to render the calendar to
      // update the inner element stylenames
      renderCalendar();
    }
  }

  /**
   * Sets the parent widget.
   * @param parent The parent widget.
   */
  public void setParent(VDateChooser parent) {
    this.parent = parent;
  }
  
  protected boolean onValueChange() {
    return false;
  }
  
  private void clearCalendarBody(boolean remove) {
    if (!remove) {
      // Leave the cells in place but clear their contents

      // This has the side effect of ensuring that the calendar always
      // contain 7 rows.
      for (int row = 1; row < 7; row++) {
	for (int col = 0; col < 8; col++) {
	  days.setHTML(row, col, "&nbsp;");
	}
      }
    } else if (getRowCount() > 1) {
      removeRow(1);
      days.clear();
    }
  }
  /**
   * Builds the top buttons and current month and year header.
   * 
   * @param needsMonth
   *            Should the month buttons be visible?
   */
  private void buildCalendarHeader(boolean needsMonth) {
    getRowFormatter().addStyleName(0, parent.getStylePrimaryName() + "-calendarpanel-header");
    if (prevMonth == null && needsMonth) {
      prevMonth = new VEventButton(this);
      prevMonth.getInputElement().setValue("<");
      prevMonth.setStyleName("v-button-prevmonth");
      prevMonth.setTabIndex(-1);
      nextMonth = new VEventButton(this);
      nextMonth.getInputElement().setValue(">");
      nextMonth.setStyleName("v-button-nextmonth");
      nextMonth.setTabIndex(-1);
      setWidget(0, 3, nextMonth);
      setWidget(0, 1, prevMonth);
    } else if (prevMonth != null && !needsMonth) {
      // Remove month traverse buttons
      remove(prevMonth);
      remove(nextMonth);
      prevMonth = null;
      nextMonth = null;
    }

    if (prevYear == null) {
      prevYear = new VEventButton(this);
      prevYear.getInputElement().setValue("<<");
      prevYear.setStyleName("v-button-prevyear");
      prevYear.setTabIndex(-1);
      nextYear = new VEventButton(this);
      nextYear.getInputElement().setValue(">>");
      nextYear.setStyleName("v-button-nextyear");
      nextYear.setTabIndex(-1);
      setWidget(0, 0, prevYear);
      setWidget(0, 4, nextYear);
    }

    updateControlButtonRangeStyles(needsMonth);

    final 	String monthName = needsMonth ? getDateTimeService().getMonth(displayedMonth.getMonth()) : "";
    final 	int year = displayedMonth.getYear() + 1900;

    getFlexCellFormatter().setStyleName(0, 2, parent.getStylePrimaryName() + "-calendarpanel-month");
    getFlexCellFormatter().setStyleName(0, 0, parent.getStylePrimaryName() + "-calendarpanel-prevyear");
    getFlexCellFormatter().setStyleName(0, 4, parent.getStylePrimaryName() + "-calendarpanel-nextyear");
    getFlexCellFormatter().setStyleName(0, 3, parent.getStylePrimaryName() + "-calendarpanel-nextmonth");
    getFlexCellFormatter().setStyleName(0, 1, parent.getStylePrimaryName() + "-calendarpanel-prevmonth");
    setHTML(0, 2, "<span class=\"" + parent.getStylePrimaryName() + "-calendarpanel-month\">" + monthName + " " + year + "</span>");
  }

  private void updateControlButtonRangeStyles(boolean needsMonth) {
    if (focusedDate == null) {
      return;
    }

    if (needsMonth) {
      Date 	prevMonthDate = (Date) focusedDate.clone();
      Date 	nextMonthDate = (Date) focusedDate.clone();

      removeOneMonth(prevMonthDate);
      addOneMonth(nextMonthDate);
    }

    Date 	prevYearDate = (Date) focusedDate.clone();
    Date 	nextYearDate = (Date) focusedDate.clone();
    
    prevYearDate.setYear(prevYearDate.getYear() - 1);
    nextYearDate.setYear(nextYearDate.getYear() + 1);
  }

  protected DateTimeService getDateTimeService() {
    return dateTimeService;
  }

  public void setDateTimeService(DateTimeService dateTimeService) {
    this.dateTimeService = dateTimeService;
  }

  /**
   * Returns whether ISO 8601 week numbers should be shown in the value
   * selector or not. ISO 8601 defines that a week always starts with a Monday
   * so the week numbers are only shown if this is the case.
   * 
   * @return true if week number should be shown, false otherwise
   */
  public boolean isShowISOWeekNumbers() {
    return showISOWeekNumbers;
  }

  public void setShowISOWeekNumbers(boolean showISOWeekNumbers) {
    this.showISOWeekNumbers = showISOWeekNumbers;
  }
  
  /**
   * Sets the focus to given date in the current view. Used when moving in the
   * calendar with the keyboard.
   * 
   * @param date A Date representing the day of month to be focused. Must be
   *             one of the days currently visible.
   */
  /*package*/ void focusDay(Date date) {
    if (focusedDay != null) {
      focusedDay.removeStyleDependentName(Styles.CN_FOCUSED);
    }

    if (date != null && focusedDate != null) {
      focusedDate.setTime(date.getTime());
      int 	rowCount = days.getRowCount();
      
      for (int i = 0; i < rowCount; i++) {
	int	cellCount = days.getCellCount(i);
	
	for (int j = 0; j < cellCount; j++) {
	  Widget	widget = days.getWidget(i, j);
	  
	  if (widget != null && widget instanceof Day) {
	    Day		curday = (Day) widget;
	    
	    if (curday.getDate().equals(date)) {
	      curday.addStyleDependentName(Styles.CN_FOCUSED);
	      focusedDay = curday;
	      return;
	    }
	  }
	}
      }
    }
  }

  /**
   * Sets the selection highlight to a given day in the current view
   * 
   * @param date A Date representing the day of month to be selected. Must be
   *        one of the days currently visible.
   * 
   */
  private void selectDate(Date date) {
    if (selectedDay != null) {
      selectedDay.removeStyleDependentName(Styles.CN_SELECTED);
      Roles.getGridcellRole().removeAriaSelectedState(selectedDay.getElement());
    }

    int 	rowCount = days.getRowCount();
    
    for (int i = 0; i < rowCount; i++) {
      int 	cellCount = days.getCellCount(i);
      
      for (int j = 0; j < cellCount; j++) {
	Widget		widget = days.getWidget(i, j);
	
	if (widget != null && widget instanceof Day) {
	  Day	curday = (Day) widget;
	  
	  if (curday.getDate().equals(date)) {
	    curday.addStyleDependentName(Styles.CN_SELECTED);
	    selectedDay = curday;
	    Roles.getGridcellRole().setAriaSelectedState(selectedDay.getElement(), SelectedValue.TRUE);
	    return;
	  }
	}
      }
    }
  }

  /**
   * Updates year, month, day from focusedDate to value
   */
  /*package*/ void selectFocused() {
    if (focusedDate != null) {
      if (value == null) {
	// No previously selected value (set to null on server side).
	// Create a new date using current date and time
	value = new Date();
      }
      /*
       * #5594 set Date (day) to 1 in order to prevent any kind of
       * wrapping of months when later setting the month. (e.g. 31 ->
       * month with 30 days -> wraps to the 1st of the following month,
       * e.g. 31st of May -> 31st of April = 1st of May)
       */
      value.setDate(1);
      if (value.getYear() != focusedDate.getYear()) {
	value.setYear(focusedDate.getYear());
      }
      if (value.getMonth() != focusedDate.getMonth()) {
	value.setMonth(focusedDate.getMonth());
      }
      if (value.getDate() != focusedDate.getDate()) {
      }
      // We always need to set the date, even if it hasn't changed, since
      // it was forced to 1 above.
      value.setDate(focusedDate.getDate());
      selectDate(focusedDate);
    }
  }

  /**
   * Builds the day and time selectors of the calendar.
   */
  private void buildCalendarBody() {
    final	int weekColumn = 0;
    final 	int firstWeekdayColumn = 1;
    final 	int headerRow = 0;

    setWidget(1, 0, days);
    setCellPadding(0);
    setCellSpacing(0);
    getFlexCellFormatter().setColSpan(1, 0, 5);
    getFlexCellFormatter().setStyleName(1, 0, parent.getStylePrimaryName() + "-calendarpanel-body");

    days.getFlexCellFormatter().setStyleName(headerRow, weekColumn, "v-week");
    days.setHTML(headerRow, weekColumn, "<strong></strong>");
    // Hide the week column if week numbers are not to be displayed.
    days.getFlexCellFormatter().setVisible(headerRow, weekColumn, isShowISOWeekNumbers());
    days.getRowFormatter().setStyleName(headerRow, parent.getStylePrimaryName() + "-calendarpanel-weekdays");

    if (isShowISOWeekNumbers()) {
      days.getFlexCellFormatter().setStyleName(headerRow, weekColumn, "v-first");
      days.getFlexCellFormatter().setStyleName(headerRow, firstWeekdayColumn, "");
      days.getRowFormatter().addStyleName(headerRow, parent.getStylePrimaryName() + "-calendarpanel-weeknumbers");
    } else {
      days.getFlexCellFormatter().setStyleName(headerRow, weekColumn, "");
      days.getFlexCellFormatter().setStyleName(headerRow, firstWeekdayColumn, "v-first");
    }

    days.getFlexCellFormatter().setStyleName(headerRow, firstWeekdayColumn + 6, "v-last");

    // Print weekday names
    final	int firstDay = getDateTimeService().getFirstDayOfWeek();
    
    for (int i = 0; i < 7; i++) {
      int day = i + firstDay;
      if (day > 6) {
	day = 0;
      }
      days.setHTML(headerRow, firstWeekdayColumn + i, "<strong>" + getDateTimeService().getShortDay(day) + "</strong>");
      Roles.getColumnheaderRole().set(days.getCellFormatter().getElement(headerRow, firstWeekdayColumn + i));
    }

    // Zero out hours, minutes, seconds, and milliseconds to compare dates
    // without time part
    final Date 	tmp = new Date();
    final Date 	today = new Date(tmp.getYear(), tmp.getMonth(), tmp.getDate());
    final Date 	selectedDate = value == null ? null : new Date(value.getYear(), value.getMonth(), value.getDate());

    final int startWeekDay = getDateTimeService().getStartWeekDay(displayedMonth);
    final Date curr = (Date) displayedMonth.clone();
    // Start from the first day of the week that at least partially belongs
    // to the current month
    curr.setDate(1 - startWeekDay);

    // No month has more than 6 weeks so 6 is a safe maximum for rows.
    for (int weekOfMonth = 1; weekOfMonth < 7; weekOfMonth++) {
      for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {

	// Actually write the day of month
	Date 	dayDate = (Date) curr.clone();
	Day 	day = new Day(dayDate);

	day.setStyleName(parent.getStylePrimaryName() + "-calendarpanel-day");
	day.addClickHandler(new DayClickHandler(this));
	if (curr.equals(selectedDate)) {
	  day.addStyleDependentName(Styles.CN_SELECTED);
	  Roles.getGridcellRole().setAriaSelectedState(day.getElement(), SelectedValue.TRUE);
	  selectedDay = day;
	}
	if (curr.equals(today)) {
	  day.addStyleDependentName(Styles.CN_TODAY);
	}
	if (curr.equals(focusedDate)) {
	  focusedDay = day;
	  if (hasFocus) {
	    day.addStyleDependentName(Styles.CN_FOCUSED);
	  }
	}
	if (curr.getMonth() != displayedMonth.getMonth()) {
	  day.addStyleDependentName(Styles.CN_OFFMONTH);
	  day.setOffMonth(true);
	}

	days.setWidget(weekOfMonth, firstWeekdayColumn + dayOfWeek, day);
	Roles.getGridcellRole().set(days.getCellFormatter().getElement(weekOfMonth, firstWeekdayColumn + dayOfWeek));
	// ISO week numbers if requested
	days.getCellFormatter().setVisible(weekOfMonth, weekColumn, isShowISOWeekNumbers());

	if (isShowISOWeekNumbers()) {
	  final String 	baseCssClass = parent.getStylePrimaryName() + "-calendarpanel-weeknumber";
	  String	weekCssClass = baseCssClass;
	  int 		weekNumber = DateTimeService.getISOWeekNumber(curr);

	  days.setHTML(weekOfMonth, 0, "<span class=\"" + weekCssClass + "\"" + ">" + weekNumber + "</span>");
	}
	curr.setDate(curr.getDate() + 1);
      }
    }
  }

  /**
   * Sets the data of the Panel.
   * 
   * @param currentDate The date to set
   */
  public void setDate(Date currentDate) {
    // Check that we are not re-rendering an already active date
    if (currentDate != null && currentDate.equals(value)) {
      return;
    }
    boolean currentDateWasAdjusted = false;
    // Check that selected date is inside the allowed range

    Date 	oldDisplayedMonth = displayedMonth;
    value = 	currentDate;

    // If current date was adjusted, we will not select any date,
    // since that will look like a date is selected. Instead we
    // only focus on the adjusted value
    if (value == null || currentDateWasAdjusted) {
      // If ranges enabled, we may need to focus on a different view to
      // potentially not get stuck
      focusedDate = displayedMonth = null;
    } else {
      focusedDate = new FocusedDate(value.getYear(), value.getMonth(), value.getDate());
      displayedMonth = new FocusedDate(value.getYear(), value.getMonth(), 1);
    }

    // Re-render calendar if the displayed month is changed,
    // or if a time selector is needed but does not exist.
    if (oldDisplayedMonth == null || value == null
	|| oldDisplayedMonth.getYear() != value.getYear()
	|| oldDisplayedMonth.getMonth() != value.getMonth())
    {
      renderCalendar();
    } else {
      focusDay(focusedDate);
      selectFocused();
    }

    if (!hasFocus) {
      focusDay(null);
    }
  }
  
  /**
   * Updates the calendar and text field with the selected dates.
   */
  public void renderCalendar() {
    renderCalendar(true);
  }

  /**
   * For internal use only. May be removed or replaced in the future.
   * 
   * Updates the calendar and text field with the selected dates.
   * 
   * @param updateDate The value false prevents setting the selected date of the
   *                   calendar based on focusedDate. That can be used when only the
   *                   resolution of the calendar is changed and no date has been
   *                   selected.
   */
  public void renderCalendar(boolean updateDate) {
    super.setStylePrimaryName(parent.getStylePrimaryName() + "-calendarpanel");

    if (focusedDate == null) {
      Date 	now = new Date();
      // focusedDate must have zero hours, mins, secs, millisecs
      focusedDate = new FocusedDate(now.getYear(), now.getMonth(), now.getDate());
      displayedMonth = new FocusedDate(now.getYear(), now.getMonth(), 1);
    }
    
    if (updateDate) {
      focusChangeListener.focusChanged(new Date(focusedDate.getTime()));
    }

    final boolean 	needsMonth = true;
    boolean 		needsBody = true;
    
    buildCalendarHeader(needsMonth);
    clearCalendarBody(!needsBody);
    if (needsBody) {
      buildCalendarBody();
    }

    initialRenderDone = true;
  }

  /**
   * Moves the focus forward the given number of days.
   */
  private void focusNextDay(int days) {
    if (focusedDate == null) {
      return;
    }

    Date 	focusCopy = ((Date) focusedDate.clone());
    int 	oldMonth = focusedDate.getMonth();
    int 	oldYear = focusedDate.getYear();
    
    focusCopy.setDate(focusedDate.getDate() + days);
    focusedDate.setDate(focusedDate.getDate() + days);
    if (focusedDate.getMonth() == oldMonth && focusedDate.getYear() == oldYear) {
      // Month did not change, only move the selection
      focusDay(focusedDate);
    } else {
      // If the month changed we need to re-render the calendar
      displayedMonth.setMonth(focusedDate.getMonth());
      displayedMonth.setYear(focusedDate.getYear());
      renderCalendar();
    }
  }

  /**
   * Moves the focus backward the given number of days.
   */
  private void focusPreviousDay(int days) {
    focusNextDay(-days);
  }

  /**
   * Selects the next month
   */
  private void focusNextMonth() {

    if (focusedDate == null) {
      return;
    }
    // Trying to request next month
    Date requestedNextMonthDate = (Date) focusedDate.clone();
    addOneMonth(requestedNextMonthDate);

    focusedDate.setTime(requestedNextMonthDate.getTime());
    displayedMonth.setMonth(displayedMonth.getMonth() + 1);

    renderCalendar();
  }

  private static void addOneMonth(Date date) {
    int 	currentMonth = date.getMonth();
    int 	requestedMonth = (currentMonth + 1) % 12;

    date.setMonth(date.getMonth() + 1);
    /*
     * If the selected value was e.g. 31.3 the new value would be 31.4 but
     * this value is invalid so the new value will be 1.5. This is taken
     * care of by decreasing the value until we have the correct month.
     */
    while (date.getMonth() != requestedMonth) {
      date.setDate(date.getDate() - 1);
    }
  }

  private static void removeOneMonth(Date date) {
    int 	currentMonth = date.getMonth();
    
    date.setMonth(date.getMonth() - 1);
    /*
     * If the selected value was e.g. 31.12 the new value would be 31.11 but
     * this value is invalid so the new value will be 1.12. This is taken
     * care of by decreasing the value until we have the correct month.
     */
    while (date.getMonth() == currentMonth) {
      date.setDate(date.getDate() - 1);
    }
  }

  /**
   * Selects the previous month
   */
  private void focusPreviousMonth() {
    if (focusedDate == null) {
      return;
    }
    
    Date 	requestedPreviousMonthDate = (Date) focusedDate.clone();
    
    removeOneMonth(requestedPreviousMonthDate);
    focusedDate.setTime(requestedPreviousMonthDate.getTime());
    displayedMonth.setMonth(displayedMonth.getMonth() - 1);
    renderCalendar();
  }

  /**
   * Selects the previous year
   */
  private void focusPreviousYear(int years) {
    if (focusedDate == null) {
      return;
    }
    Date previousYearDate = (Date) focusedDate.clone();
    previousYearDate.setYear(previousYearDate.getYear() - years);
    // Do not focus if not inside range

    // If we remove one year, but have to roll back a bit, fit it
    // into the calendar. Also the months have to be changed

    int currentMonth = focusedDate.getMonth();
    focusedDate.setYear(focusedDate.getYear() - years);
    displayedMonth.setYear(displayedMonth.getYear() - years);
    /*
     * If the focused date was a leap day (Feb 29), the new date becomes
     * Mar 1 if the new year is not also a leap year. Set it to Feb 28
     * instead.
     */
    if (focusedDate.getMonth() != currentMonth) {
      focusedDate.setDate(0);
    }

    renderCalendar();
  }

  /**
   * Selects the next year
   */
  private void focusNextYear(int years) {
    if (focusedDate == null) {
      return;
    }
    
    Date 	nextYearDate = (Date) focusedDate.clone();
    
    nextYearDate.setYear(nextYearDate.getYear() + years);
    // If we add one year, but have to roll back a bit, fit it
    // into the calendar. Also the months have to be changed

    int currentMonth = focusedDate.getMonth();
    focusedDate.setYear(focusedDate.getYear() + years);
    displayedMonth.setYear(displayedMonth.getYear() + years);
    /*
     * If the focused date was a leap day (Feb 29), the new date becomes
     * Mar 1 if the new year is not also a leap year. Set it to Feb 28
     * instead.
     */
    if (focusedDate.getMonth() != currentMonth) {
      focusedDate.setDate(0);
    }

    renderCalendar();
  }

  /**
   * Handles a user click on the component
   * 
   * @param sender The component that was clicked
   * @param updateVariable Should the value field be updated
   * 
   */
  private void processClickEvent(Widget sender) {
    if (sender == prevYear) {
      focusPreviousYear(1);
    } else if (sender == nextYear) {
      focusNextYear(1);
    } else if (sender == prevMonth) {
      focusPreviousMonth();
    } else if (sender == nextMonth) {
      focusNextMonth();
    }
  }

  /**
   * Handles the keypress from both the onKeyPress event and the onKeyDown
   * event
   * 
   * @param event The keydown/keypress event
   */
  private void handleKeyPress(DomEvent<?> event) {
    // Check tabs
    int keycode = event.getNativeEvent().getKeyCode();
    if (keycode == KeyCodes.KEY_TAB && event.getNativeEvent().getShiftKey()) {
      if (onTabOut(event)) {
	return;
      }
    }

    // Handle the navigation
    if (handleNavigation(keycode, event.getNativeEvent().getCtrlKey()
	|| event.getNativeEvent().getMetaKey(), event.getNativeEvent().getShiftKey()))
    {
      event.preventDefault();
    }
  }

  /**
   * Notifies submit-listeners of a submit event
   */
  /*package*/ void onSubmit() {
    if (getSubmitListener() != null) {
      getSubmitListener().onSubmit();
    }
  }

  /**
   * Notifies submit-listeners of a cancel event
   */
  private void onCancel() {
    if (getSubmitListener() != null) {
      getSubmitListener().onCancel();
    }
  }
  
  /**
   * Returns the current date.
   * @return The current date.
   */
  public Date getDate() {
    return value;
  }

  /**
   * Handles the keyboard navigation when the resolution is set to years.
   * 
   * @param keycode The keycode to process
   * @param ctrl Is ctrl pressed?
   * @param shift is shift pressed
   * @return Returns true if the keycode was processed, else false
   */
  protected boolean handleNavigationYearMode(int keycode, boolean ctrl, boolean shift) {

    // Ctrl and Shift selection not supported
    if (ctrl || shift) {
      return false;
    }

    else if (keycode == getPreviousKey()) {
      focusNextYear(10); // Add 10 years
      return true;
    }

    else if (keycode == getForwardKey()) {
      focusNextYear(1); // Add 1 year
      return true;
    }

    else if (keycode == getNextKey()) {
      focusPreviousYear(10); // Subtract 10 years
      return true;
    }

    else if (keycode == getBackwardKey()) {
      focusPreviousYear(1); // Subtract 1 year
      return true;

    } else if (keycode == getSelectKey()) {
      value = (Date) focusedDate.clone();
      onSubmit();
      return true;

    } else if (keycode == getResetKey()) {
      // Restore showing value the selected value
      focusedDate.setTime(value.getTime());
      renderCalendar();
      return true;

    } else if (keycode == getCloseKey()) {
      // TODO fire listener, on users responsibility??
      onCancel();
      return true;
    }
    
    return false;
  }

  /**
   * Handle the keyboard navigation when the resolution is set to MONTH
   * 
   * @param keycode The keycode to handle
   * @param ctrl Was the ctrl key pressed?
   * @param shift Was the shift key pressed?
   * @return
   */
  protected boolean handleNavigationMonthMode(int keycode, boolean ctrl, boolean shift) {
    // Ctrl selection not supported
    if (ctrl) {
      return false;

    } else if (keycode == getPreviousKey()) {
      focusNextYear(1); // Add 1 year
      return true;

    } else if (keycode == getForwardKey()) {
      focusNextMonth(); // Add 1 month
      return true;

    } else if (keycode == getNextKey()) {
      focusPreviousYear(1); // Subtract 1 year
      return true;

    } else if (keycode == getBackwardKey()) {
      focusPreviousMonth(); // Subtract 1 month
      return true;

    } else if (keycode == getSelectKey()) {
      value = (Date) focusedDate.clone();
      onSubmit();
      return true;

    } else if (keycode == getResetKey()) {
      // Restore showing value the selected value
      focusedDate.setTime(value.getTime());
      renderCalendar();
      return true;

    } else if (keycode == getCloseKey() || keycode == KeyCodes.KEY_TAB) {
      onCancel();
      // TODO fire close event
      return true;
    }

    return false;
  }

  /**
   * Handle keyboard navigation what the resolution is set to DAY
   * 
   * @param keycode The keycode to handle
   * @param ctrl Was the ctrl key pressed?
   * @param shift Was the shift key pressed?
   * @return Return true if the key press was handled by the method, else return false.
   */
  protected boolean handleNavigationDayMode(int keycode, boolean ctrl, boolean shift) {
    // Ctrl key is not in use
    if (ctrl) {
      return false;
    }
    /*
     * Jumps to the next day.
     */
    if (keycode == getForwardKey() && !shift) {
      focusNextDay(1);
      return true;
      /*
       * Jumps to the previous day
       */
    } else if (keycode == getBackwardKey() && !shift) {
      focusPreviousDay(1);
      return true;
      /*
       * Jumps one week forward in the calendar
       */
    } else if (keycode == getNextKey() && !shift) {
      focusNextDay(7);
      return true;
      /*
       * Jumps one week back in the calendar
       */
    } else if (keycode == getPreviousKey() && !shift) {
      focusPreviousDay(7);
      return true;
      /*
       * Selects the value that is chosen
       */
    } else if (keycode == getSelectKey() && !shift) {
      selectFocused();
      onSubmit(); // submit
      return true;

    } else if (keycode == getCloseKey()) {
      onCancel();
      // TODO close event
      return true;
      /*
       * Jumps to the next month
       */
    } else if (shift && keycode == getForwardKey()) {
      focusNextMonth();
      return true;
      /*
       * Jumps to the previous month
       */
    } else if (shift && keycode == getBackwardKey()) {
      focusPreviousMonth();
      return true;
      /*
       * Jumps to the next year
       */
    } else if (shift && keycode == getPreviousKey()) {
      focusNextYear(1);
      return true;
      /*
       * Jumps to the previous year
       */
    } else if (shift && keycode == getNextKey()) {
      focusPreviousYear(1);
      return true;

      /*
       * Resets the selection
       */
    } else if (keycode == getResetKey() && !shift) {
      // Restore showing value the selected value
      focusedDate = new FocusedDate(value.getYear(), value.getMonth(), value.getDate());
      displayedMonth = new FocusedDate(value.getYear(), value.getMonth(), 1);
      renderCalendar();
      return true;
    }

    return false;
  }

  /**
   * Handles the keyboard navigation
   * 
   * @param keycode The key code that was pressed
   * @param ctrl Was the ctrl key pressed
   * @param shift Was the shift key pressed
   * @return Return true if key press was handled by the component, else return false
   */
  protected boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
    return handleNavigationDayMode(keycode, ctrl, shift);
  }

  /**
   * Returns the reset key which will reset the calendar to the previous
   * selection. By default this is backspace but it can be overriden to change
   * the key to whatever you want.
   * 
   * @return
   */
  protected int getResetKey() {
    return KeyCodes.KEY_BACKSPACE;
  }

  /**
   * Returns the select key which selects the value. By default this is the
   * enter key but it can be changed to whatever you like by overriding this
   * method.
   * 
   * @return
   */
  protected int getSelectKey() {
    return KeyCodes.KEY_ENTER;
  }

  /**
   * Returns the key that closes the popup window if this is a VPopopCalendar.
   * Else this does nothing. By default this is the Escape key but you can
   * change the key to whatever you want by overriding this method.
   * 
   * @return
   */
  protected int getCloseKey() {
    return KeyCodes.KEY_ESCAPE;
  }

  /**
   * The key that selects the next day in the calendar. By default this is the
   * right arrow key but by overriding this method it can be changed to
   * whatever you like.
   * 
   * @return
   */
  protected int getForwardKey() {
    return KeyCodes.KEY_RIGHT;
  }

  /**
   * The key that selects the previous day in the calendar. By default this is
   * the left arrow key but by overriding this method it can be changed to
   * whatever you like.
   * 
   * @return
   */
  protected int getBackwardKey() {
    return KeyCodes.KEY_LEFT;
  }

  /**
   * The key that selects the next week in the calendar. By default this is
   * the down arrow key but by overriding this method it can be changed to
   * whatever you like.
   * 
   * @return
   */
  protected int getNextKey() {
    return KeyCodes.KEY_DOWN;
  }

  /**
   * The key that selects the previous week in the calendar. By default this
   * is the up arrow key but by overriding this method it can be changed to
   * whatever you like.
   * 
   * @return
   */
  protected int getPreviousKey() {
    return KeyCodes.KEY_UP;
  }

  /**
   * If true should be returned if the panel will not be used after this
   * event.
   * 
   * @param event
   * @return
   */
  protected boolean onTabOut(DomEvent<?> event) {
    if (focusOutListener != null) {
      return focusOutListener.onFocusOut(event);
    }
    
    return false;
  }

  /**
   * A focus out listener is triggered when the panel loosed focus. This can
   * happen either after a user clicks outside the panel or tabs out.
   * 
   * @param listener
   *            The listener to trigger
   */
  public void setFocusOutListener(FocusOutListener listener) {
    focusOutListener = listener;
  }

  /**
   * The submit listener is called when the user selects a value from the
   * calender either by clicking the day or selects it by keyboard.
   * 
   * @param submitListener The listener to trigger
   */
  public void setSubmitListener(SubmitListener submitListener) {
    this.submitListener = submitListener;
  }

  /**
   * The given FocusChangeListener is notified when the focused date changes
   * by user either clicking on a new date or by using the keyboard.
   * 
   * @param listener
   *            The FocusChangeListener to be notified
   */
  public void setFocusChangeListener(FocusChangeListener listener) {
    focusChangeListener = listener;
  }

  /**
   * Returns the submit listener that listens to selection made from the panel
   * 
   * @return The listener or NULL if no listener has been set
   */
  public SubmitListener getSubmitListener() {
    return submitListener;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private VEventButton 			prevYear;
  private VEventButton 			nextYear;
  private VEventButton 			prevMonth;
  private VEventButton 			nextMonth;
  private FlexTable 			days = new FlexTable();
  private Date 				value;
  private DateTimeService 		dateTimeService;
  private boolean 			showISOWeekNumbers;
  /*package*/ FocusedDate 		displayedMonth;
  private FocusedDate 			focusedDate;
  private Day 				selectedDay;
  private Day 				focusedDay;
  private FocusOutListener 		focusOutListener;
  private SubmitListener 		submitListener;
  private FocusChangeListener 		focusChangeListener;
  private boolean 			hasFocus = false;
  private boolean 			initialRenderDone = false;
  private VDateChooser			parent;
  private Timer			 	mouseTimer;
}
