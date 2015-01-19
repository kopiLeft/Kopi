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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.DPositionPanelListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Reindeer;

/**
 * The <code>DPositionPanel</code> is used to display the current selected
 * record and the total buffered records at the button right of the window.
 */
@SuppressWarnings("serial")
public class DPositionPanel extends Panel { //!!! FIXME replace with CSSLayout

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DPositionPanel</code> instance.
   * @param listener The {@link DPositionPanelListener} object.
   */
  public DPositionPanel(DPositionPanelListener listener) {
    CssLayout		record = new CssLayout();
    
    setStyleName(KopiTheme.POSITION_PANEL_CONTAINER);
    setImmediate(true);
    record.addStyleName("position-record");
    layout = new CssLayout();
    layout.setWidth("100%");
    layout.setHeight(null);
    layout.addStyleName(KopiTheme.PANEL_POSITION_PANEL);
    addStyleName(Reindeer.PANEL_LIGHT);
    this.listener = listener;
    //record.setSpacing(false);
    
    // 'goto first' button
    first = new Button();
    first.setImmediate(true);
    first.addStyleName(KopiTheme.BUTTON_POSITION_PANEL);
    first.setIcon(Utils.getImage("arrowfirst.gif").getResource());
    first.addClickListener(new ClickListener() {
      
      public void buttonClick(ClickEvent event) {
	DPositionPanel.this.listener.gotoFirstPosition();
      }
    });
    
    // 'goto previous' button
    left = new Button();
    left.setImmediate(true);
    left.addStyleName(KopiTheme.BUTTON_POSITION_PANEL);
    left.setIcon(Utils.getImage("arrowleft.gif").getResource());
    left.addClickListener(new ClickListener() {
      
      public void buttonClick(ClickEvent event) {
	DPositionPanel.this.listener.gotoPrevPosition();
      }
    });
    
    // 'position/total' label
    info = new Button("position/total");
    info.setImmediate(true);
    info.addStyleName(KopiTheme.BUTTON_INFO);
    info.addClickListener(new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
	BackgroundThreadHandler.start(new Runnable() {

	  @Override
	  public void run() {
	    int		selectedPosition = DWindow.askPosition((ComponentContainer) DPositionPanel.this.getParent().getParent(),current, total);/*Hedi*/// i deleted the handler from argument list  

	    if (selectedPosition > 0) {
	      if (selectedPosition != current) {
		try {  
		  DPositionPanel.this.listener.gotoPosition(selectedPosition);
		} catch (ArrayIndexOutOfBoundsException ex) {
		  if (current > 0) {	
		    DPositionPanel.this.listener.gotoPosition(current);
		  }
		}
	      }
	    } else {
	      if (current > 0) {	
		DPositionPanel.this.listener.gotoPosition(current);
	      }
	    }
	  }
	});
      }
    });
    
    // 'goto next' button
    right = new Button();
    right.setImmediate(true);
    right.addStyleName(KopiTheme.BUTTON_POSITION_PANEL);
    right.setIcon(Utils.getImage("arrowright.gif").getResource());
    right.addClickListener(new ClickListener() {
      
      public void buttonClick(ClickEvent event) {
	DPositionPanel.this.listener.gotoNextPosition();
      }
    });
    
    // 'goto next' last
    last = new Button();
    last.setImmediate(true);
    //last.addStyleName(KopiTheme.BUTTON_LINK_STYLE);
    last.addStyleName(KopiTheme.BUTTON_POSITION_PANEL);
    last.setIcon(Utils.getImage("arrowlast.gif").getResource());
    last.addClickListener(new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
	DPositionPanel.this.listener.gotoLastPosition();
      }
    });
    
    record.addComponent(first);
    record.addComponent(left);
    record.addComponent(info);
    record.addComponent(right);
    record.addComponent(last);
    layout.addComponent(record);
    recordVisible = false;
  }
  
  //-----------------------------------------------
  // UTILS
  //-----------------------------------------------

  /**
   * Sets the selected position.
   * @param current The current selected position.
   * @param total The total records.
   */
  public void setPosition(final int current, final int total) {
    this.current = current;
    this.total = total;
    
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {		  
        if (current == -1 || total == 0) {
          if (recordVisible) {
            setContent(null);
            recordVisible = false;
          }
        } else {
          if (!recordVisible) {
            setContent(layout);
            recordVisible = true;
          }
      
          info.setCaption(" " + current + " / " + total + " ");
          left.setEnabled(current > 1);
          first.setEnabled(current > 1);
          right.setEnabled(current < total);
          last.setEnabled(current < total);
        }
        markAsDirty();
      }
    });
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final DPositionPanelListener	        listener;
  private CssLayout		                layout;
  private Button			        info;
  private Button			        left;
  private Button		                right;
  private Button			        first;
  private Button			        last;
  private boolean       		        recordVisible;
  private int           		        current;
  private int           		        total;
}