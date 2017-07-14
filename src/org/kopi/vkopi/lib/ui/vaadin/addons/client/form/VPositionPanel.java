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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Icons;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VAnchor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.PositionPanelListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.vaadin.client.ApplicationConnection;

/**
 * A position panel widget used to fetch a form records.
 */
public class VPositionPanel extends HorizontalPanel implements ClickHandler {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VPositionPanel</code> widget.
   */
  public VPositionPanel() {
    setSpacing(2);
    listeners = new ArrayList<PositionPanelListener>();
    first = new VButton();
    last = new VButton();
    left = new VButton();
    right = new VButton();
    info = new VAnchor();
    info.setHref("#"); // to get hand cursor
    info.setStyleName("records-info");
    add(first);
    add(left);
    add(info);
    add(right);
    add(last);
    setCellHorizontalAlignment(first, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(first, HasVerticalAlignment.ALIGN_MIDDLE);
    setCellHorizontalAlignment(last, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(last, HasVerticalAlignment.ALIGN_MIDDLE);
    setCellHorizontalAlignment(left, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(left, HasVerticalAlignment.ALIGN_MIDDLE);
    setCellHorizontalAlignment(right, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(right, HasVerticalAlignment.ALIGN_MIDDLE);
    setCellHorizontalAlignment(info, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(info, HasVerticalAlignment.ALIGN_MIDDLE);
    first.addClickHandler(this);
    last.addClickHandler(this);
    left.addClickHandler(this);
    right.addClickHandler(this);
    info.addClickHandler(this);
    recordVisible = false;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Sets the current position of the position panel.
   * @param current The current record.
   * @param total The total records.
   */
  public void setPosition(int current, int total) {
    this.current = current;
    this.total = total;
    if (current == -1 || total == 0) {
      if (recordVisible) {
	setVisible(false);
	recordVisible = false;
      }
    } else {
      if (!recordVisible) {
	setVisible(true);
	recordVisible = true;
      }

      info.setText(" " + current + " / " + total + " ");
      left.setEnabled(current > 1);
      if (left.isEnabled()) {
	setButtonIcon(left, Icons.PREVIOUS);
      } else {
	setButtonIcon(left, Icons.PREVIOUS_OFF);
      }
      first.setEnabled(current > 1);
      if (first.isEnabled()) {
	setButtonIcon(first, Icons.START);
      } else {
	setButtonIcon(first, Icons.START_OFF);
      }
      right.setEnabled(current < total);
      if (right.isEnabled()) {
	setButtonIcon(right, Icons.NEXT);
      } else {
	setButtonIcon(right, Icons.NEXT_OFF);
      }
      last.setEnabled(current < total);
      if (last.isEnabled()) {
	setButtonIcon(last, Icons.END);
      } else {
	setButtonIcon(last, Icons.END_OFF);
      }
    }
  }
  
  /**
   * Creates the buttons icons.
   */
  protected void setButtonIcon(VButton button, String icon) {
    if (connection == null) {
      return;
    }
    
    button.setIcon(icon);
  }
  
  /**
   * Registers a new position panel listener.
   * @param l The listener object.
   */
  public void addPositionPanelListener(PositionPanelListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a position panel listener.
   * @param l The listener object.
   */
  public void removePositionPanelListener(PositionPanelListener l) {
    listeners.remove(l);
  }
  
  /**
   * Requests to go to the next position.
   */
  protected void fireGotoNextPosition() {
    for (PositionPanelListener l : listeners) {
      if (l != null) {
	l.gotoNextPosition();
      }
    }
  }

  /**
   * Requests to go to the previous position.
   */
  protected void fireGotoPrevPosition() {
    for (PositionPanelListener l : listeners) {
      if (l != null) {
	l.gotoPrevPosition();
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected void fireGotoLastPosition() {
    for (PositionPanelListener l : listeners) {
      if (l != null) {
	l.gotoLastPosition();
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected void fireGotoFirstPosition() {
    for (PositionPanelListener l : listeners) {
      if (l != null) {
	l.gotoFirstPosition();
      }
    }
  }

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  protected void fireGotoPosition(int posno) {
    for (PositionPanelListener l : listeners) {
      if (l != null) {
	l.gotoPosition(posno);
      }
    }
  }
  
  @Override
  public void onClick(ClickEvent event) {
    if (event.getSource() == first) {
      // go to the first record
      fireGotoFirstPosition();
    } else if (event.getSource() == last) {
      fireGotoLastPosition();
    } else if (event.getSource() == left) {
      fireGotoPrevPosition();
    } else if (event.getSource() == right) {
      fireGotoNextPosition();
    } else if (event.getSource() == info) {
      // show dialog to choose a position.
      showPosRequester();
    }
  }
  
  @Override
  public void clear() {
    super.clear();
    listeners.clear();
    listeners = null;
    first = null;
    last = null;
    left = null;
    right = null;
    info = null;
    connection = null;
    locale = null;
  }
  
  /**
   * Sets the client connection.
   * @param connection The application connection.
   */
  public void setClient(ApplicationConnection connection) {
    this.connection = connection;
  }
  
  /**
   * Sets the application locale.
   * @param locale The application locale.
   */
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  /**
   * Shows the position requester.
   */
  protected void showPosRequester() {
    if (connection == null || locale == null) {
      return; // we need them both
    }
    
    final VPosRequester		requester;
    
    requester = new VPosRequester(current, total);
    requester.initWidget(connection, locale);
    requester.show(connection, this);
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<PositionPanelListener>           listeners;
  private VButton				first;
  private VButton				last;
  private VButton				left;
  private VButton				right;
  private VAnchor				info;
  private ApplicationConnection			connection;
  private String				locale;
  private int 					current;
  private int					total;
  private boolean				recordVisible;
}
