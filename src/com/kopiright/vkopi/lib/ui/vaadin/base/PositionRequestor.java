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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import org.kopi.vaadin.fields.IntegerField;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The <code>PositionRequestor</code> is popup that asks for a user
 * to put a record number to be fetched.
 */
@SuppressWarnings("serial")
public class PositionRequestor extends Panel implements UComponent { 

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>PositionRequestor</code> object.
   * @param parent The parent container.
   * @param current The current record.
   * @param total The total records.
   */
  public PositionRequestor(ComponentContainer parent, int current, int total) {
    this.current = current;
    this.total = total;
    buildContent();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Builds the requester content.
   * @param current The current record.
   * @param total The total records.
   */
  protected void buildContent() {
    GridLayout 			grid;
    CssLayout			buttons;
    
    setSizeUndefined();
    grid = new GridLayout(2, 3);
    buttons =  new CssLayout();
    buttons.setStyleName(KopiTheme.BUTTONS_POS_ASKER);
    grid.setSpacing(true);
    grid.addStyleName(KopiTheme.GRID_POS_ASKER);
    addStyleName(KopiTheme.PANEL_LIGHT);
    icon = new com.vaadin.ui.Image();
    icon.addStyleName(KopiTheme.EMBEDDED_ICON);
    input = new IntegerField(8);
    input.setWidth(input.getWidth()+4,Unit.EX);
    input.addStyleName(KopiTheme.FIELD_INPUT);
    ok = new Button(VlibProperties.getString("OK"));
    ok.addStyleName(KopiTheme.BUTTON_SMALL);
    cancel = new Button(VlibProperties.getString("CANCEL"));
    cancel.addStyleName(KopiTheme.BUTTON_SMALL);
    input.setCaption(VlibProperties.getString("position-number") + " :");
    icon.setSource(Utils.getImage("ask.png").getResource());
    grid.addComponent(icon, 0, 1);
    grid.addComponent(input, 1, 1);
    buttons.addComponent(ok);
    buttons.addComponent(cancel);
    grid.addComponent(buttons, 0, 2, 1, 2);
    grid.setSizeUndefined();
    grid.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
    setContent(grid);
  }
  
  /**
   * Shows a popup containing this requester to ask for a position.
   * @return The selected record.
   */
  public int askPosition() {
    selectedPosition = -1;  
    createPopup();
    addListeners();
    BackgroundThreadHandler.startAndWait(new Runnable() {

      @Override
      public void run() {
	UI.getCurrent().addWindow(popup);
	input.focus();
      }
    }, popup);

    return selectedPosition;
  }
  
  /**
   * Creates the popup window.
   */
  protected void createPopup() {
    popup = new Window(this.current +  " " + VlibProperties.getString("from") + " " + this.total);
    popup.setContent(this);
    popup.setModal(true);
    popup.setImmediate(true);
    popup.center();
    popup.setDraggable(false);
    popup.setResizable(false);
    popup.setClosable(true);
    popup.addStyleName(KopiTheme.POPUP_REQUESTOR_STYLE);
    popup.addCloseListener(new CloseListener() {

      @Override
      public void windowClose(CloseEvent e) {
	BackgroundThreadHandler.releaseLock(popup);
	popup.close();
      }
    });
  }
  
  /**
   * Adds the buttons listeners.
   */
  protected void addListeners() {
    ok.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
	if(!input.getValue().isEmpty()) {
	  selectedPosition = Integer.parseInt(input.getValue());
	  BackgroundThreadHandler.releaseLock(popup);
	  popup.close();
	} else{
	  input.focus();
	}
      }
    });

    cancel.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
	BackgroundThreadHandler.releaseLock(popup);
	popup.close();  	
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private int			                current;
  private int			                total;
  private com.vaadin.ui.Image		        icon;
  private IntegerField		                input;
  private Button		                ok;
  private Button		                cancel;
  private Window		                popup;
  private int                                   selectedPosition;
}
