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

/**
 * Styles constants.
 */
public abstract class Styles {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  private Styles() {
    // don't Instantiate please.
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  public static final String			MAIN_WINDOW = "k-main-window";
  public static final String			MAIN_WINDOW_TAB_PANEL = "k-main-window-tab";
  
  public static final String			WELCOME_VIEW = "k-welcome-view";
  
  public static final String			WINDOW = "k-window";
  public static final String			WINDOW_ERROR = "k-window-error";
  public static final String			WINDOW_TITLE = "k-window-title";
  public static final String			WINDOW_TITLE_IMAGE = "k-window-title-image";
  public static final String			WINDOW_VIEW = "k-window-view";
  public static final String			WINDOW_VIEW_ACTORS = "k-window-view-actors";
  public static final String			WINDOW_VIEW_CONTENT = "k-window-view-content";
  public static final String			WINDOW_VIEW_CONTENT_WRAPPER = "k-window-view-content-wrapper";
  public static final String			WINDOW_VIEW_FOOTER = "k-window-view-footer";
  public static final String			WINDOW_VIEW_FOOTER_INFO = "k-window-view-footer-info";
  public static final String			WINDOW_VIEW_FOOTER_STAT = "k-window-view-footer-stat";
  
  public static final String			ACTOR = "k-actor";
  public static final String			ACTOR_ANCHOR = "k-actor-anchor";
  public static final String			ACTOR_IMAGE = "k-actor-icon";
  public static final String			ACTOR_LABEL = "k-actor-label";
  
  public static final String			INPUT_BUTTON = "k-input-button";
  public static final String			INPUT_BUTTON_PRIMARY = "k-input-button-primary";
  
  public static final String			LOGIN_WINDOW = "k-loginBoxShadow";
  public static final String			LOGIN_BOX = "k-loginBox";
  public static final String			LOGIN_BOX_IMAGE = "k-loginBox-image";
  public static final String			LOGIN_BOX_PANE = "k-loginBox-pane";
  public static final String			LOGIN_BOX_PANE_ERROR = "k-loginBox-error";
  public static final String			LOGIN_BOX_PANE_INFO = "k-loginBox-info";
  
  public static final String			FIELD = "k-field";
  
  public static final String			TEXT_FIELD = "k-textfield";
  public static final String			TEXT_INPUT = "k-textinput";
  public static final String			TEXT_AREA_INPUT = "k-textareainput";
  
  public static final String			LABEL = "k-label";
  public static final String 			LABEL_UNDEFINED_WIDTH = "k-label-undef-w";
  
  public static final String			BLOCK = "k-block";
  public static final String			BLOCK_LAYOUT = "k-blocklayout";
  
  public static final String			FORM = "k-form";
  public static final String			FORM_PAGE = "k-form-page";
  public static final String			FORM_PAGE_CONTENT = "k-form-page-content";
  public static final String                    FORM_TAB_PANEL = "k-form-tab"; 
  
  public static final String			LIST_DIALOG = "k-listdialog";
  public static final String			LIST_DIALOG_CONTENT = "k-listdialog-content";
  public static final String			LIST_DIALOG_TABLE = "k-listdialog-table";
  
  public static final String                    NOTIFICATION = "k-notification";
  public static final String                    NOTIFICATION_TITLE = "k-notification-title";
  public static final String                    NOTIFICATION_IMAGE = "k-notification-image";
  public static final String                    NOTIFICATION_MESSAGE = "k-notification-message";
  public static final String                    NOTIFICATION_FILLER = "k-notification-filler";
  public static final String                    NOTIFICATION_BUTTONS = "k-notification-buttons";
  
  public static final String                    PROGRESS_DIALOG = "k-progress";
  public static final String                    PROGRESS_TITLE = "k-progress-title";
  public static final String                    PROGRESS_MESSAGE = "k-progress-message";
  public static final String                    PROGRESS_BAR = "k-progress-bar";
  public static final String                    PROGRESS_BAR_CELL = "k-progress-bar-cell";
  public static final String                    PROGRESS_BAR_BLANK = "k-progress-bar-blank";
  public static final String                    PROGRESS_BAR_FULL = "k-progress-bar-full";
  
  public static final String                    PIVOT_REPORT_WINDOW = "k-pivot-report-window";
  
  public static final String                    REPORT_ROW_DIMENSIONS_TABLE = "k-report-row-table";
  public static final String                    REPORT_COLUMN_DIMENSIONS_TABLE = "k-report-column-table";
  public static final String                    REPORT_DIMENSION_CELL = "k-report-dimensions-cell";
  
  public static final String                    REPORT_MEASURES_TABLE = "k-report-measures-table";
  public static final String                    REPORT_MEASURE_CELL = "k-report-measure-cell";
  
  public static final String                    REPORT_DIMENSION_DRAG = "k-report-dimension-drag";
  public static final String                    REPORT_DIMENSION_DRAG_PANEL = "k-report-dimension-drag-panel";
  public static final String                    REPORT_DIMENSION_DROP = "k-report-dimension-drop";
  public static final String                    REPORT_DIMENSION_DROP_PANEL = "k-report-dimension-drop-panel";
  
  public static final String                    REPORT_MEASURES_CHECK_PANEL = "k-report-measures-check-panel";
  
  // alignment
  public static final int 			CENTER  = 0;
  public static final int 			TOP     = 1;
  public static final int 			LEFT    = 2;
  public static final int 			BOTTOM  = 3;
  public static final int 			RIGHT   = 4;
  
  // calendar pane styles
  public static final String 			DATE_CHOOSER = "k-datechooser";
  public static final String 			CN_FOCUSED = "focused";
  public static final String 			CN_TODAY = "today";
  public static final String 			CN_SELECTED = "selected";
  public static final String 			CN_OFFMONTH = "offmonth";
  public static final String 			CN_OUTSIDE_RANGE = "outside-range";
  
  public static final String                    WAIT_WINDOW = "k-wait-window";
  public static final String                    WAIT_WINDOW_IMAGE = "k-wait-window-image";
  public static final String                    WAIT_WINDOW_TEXT = "k-wait-window-text";

  public static final String                    POPUP_WINDOW = "k-popup-window";
  public static final String                    POPUP_WINDOW_CAPTION = "k-popup-window-caption";
  public static final String                    POPUP_WINDOW_CONTENT = "k-popup-window-content";
  
  // font awesome icon primary style
  public static final String                    FONT_AWESOME = "fa";
  
  // actor field styles
  public static final String                    ACTOR_FIELD = "actor-field";
  public static final String                    ACTOR_FIELD_INNER = "actor-field-inner";
  public static final String                    ACTOR_FIELD_INFO = "actor-field-info";
  public static final String                    ACTOR_FIELD_VALUE = "actor-field-value";
  public static final String                    ACTOR_FIELD_CAPTION = "actor-field-caption";
  public static final String                    ACTOR_FIELD_ICON = "actor-field-icon";
  
  // boolean field styles
  public static final String                    BOOLEAN_FIELD = "k-boolean-field";
  
  public static final String                    POSITION_PANEL = "k-positionpanel";
}
