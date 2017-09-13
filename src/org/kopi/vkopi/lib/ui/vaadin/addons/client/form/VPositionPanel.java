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

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.VPopup;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.VBlock;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.common.VButton;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.PositionPanelListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.VLabel;

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
    setStyleName(Styles.POSITION_PANEL);
    setSpacing(2);
    listeners = new ArrayList<PositionPanelListener>();
    first = new VButton();
    last = new VButton();
    left = new VButton();
    right = new VButton();
    info = new TextBox();
    totalInfo = new VLabel(); //VAnchor();
    slash = new VLabel();
    totalInfo.setStyleName("records-totalInfo");
    info.setStyleName("records-info");
    slash.setStyleName("slash");
    add(first);
    add(left);
    add(info);
    add(slash);
    add(totalInfo);
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
    setCellHorizontalAlignment(totalInfo, HasHorizontalAlignment.ALIGN_CENTER);
    setCellVerticalAlignment(totalInfo, HasVerticalAlignment.ALIGN_MIDDLE);
    first.addClickHandler(this);
    last.addClickHandler(this);
    left.addClickHandler(this);
    right.addClickHandler(this);
    info.addKeyPressHandler(new KeyPressHandler() {
      
      @Override
      public void onKeyPress(KeyPressEvent event) {
        if (Character.isLetter(event.getCharCode()) || event.getNativeEvent().getKeyCode() == KeyCodes.KEY_SPACE){
          ((TextBox) event.getSource()).cancelKey();
        } else if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && info.getValue() != null){
          int   infoVlue = Integer.valueOf((info.getValue()));

          if( infoVlue <= total && infoVlue > 0 ){
            setCurrent(infoVlue);
            fireGotoPosition(infoVlue);
            setButtonsStyleName();
          } else {
            setCurrent(current);
          }
        }
      }
    });
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
    
    setCurrent(current);
    totalInfo.setText("" + total );
    slash.setText("/");
    setButtonIcon(left, "angle-left");
    setButtonIcon(first, "angle-double-left");
    setButtonIcon(right, "angle-right");
    setButtonIcon(last, "angle-double-right");
    left.setEnabled(current > 1);
    first.setEnabled(current > 1);
    right.setEnabled(current < total);
    last.setEnabled(current < total);
  }
  
  public void setStyleName(VButton button) {
    if (!button.isEnabled()) {
      button.removeStyleName("v-enabled-button");
      button.addStyleName("v-disabled-button");
    } else {
      button.removeStyleName("v-disabled-button");
      button.addStyleName("v-enabled-button");
    }
  }
  
  public void setButtonsStyleName() {
    setStyleName(first);
    setStyleName(last);
    setStyleName(left);
    setStyleName(right);
  }

  /**
   * Sets the current position of the position panel.
   * @param current The current record.
   */
  public void setCurrent(int current) {
    this.current = current;
    
    info.setValue(String.valueOf(current));
  }
  
  /**
   * Creates the buttons icons.
   */
  protected void setButtonIcon(VButton button, String icon) {
    button.setIcon(icon);
    button.setStyleName("button");
    setButtonsStyleName();
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
      setButtonsStyleName();
    } else if (event.getSource() == last) {
      fireGotoLastPosition();
      setButtonsStyleName();
    } else if (event.getSource() == left) {
      fireGotoPrevPosition();
      setButtonsStyleName();
    } else if (event.getSource() == right) {
      fireGotoNextPosition();
      setButtonsStyleName();
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
  }
  
  /**
   * Sets the client connection.
   * @param connection The application connection.
   */
  public void setClient(ApplicationConnection connection) {
    this.connection = connection;
  }
  
  public void show(final VBlock block){
    final VPopup        popup;
    
    setButtonsStyleName();
    popup = new VPopup(connection, true, false) ;
    VPositionPanel.this.setVisible(true);
    popup.setWidget(this);

    Scheduler.get().scheduleFinally(new ScheduledCommand() {
      
      @Override
      public void execute() {
        popup.center(block.getParent().getParent()); 
      }
    });
  }
  
  public void hide(VPopup popup) {
    popup.hide();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<PositionPanelListener>           listeners;
  private VButton				first;
  private VButton				last;
  private VButton				left;
  private VButton				right;
  private TextBox				info;
  private VLabel                                totalInfo;
  private VLabel                                slash;
  private ApplicationConnection			connection;
  private int 					current;
  private int					total;
}
