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

import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>ScrollBar</code> is a representation of a UI
 * scroll bar component that is composed from two buttons
 * UP and DOWN without the indicating bar.
 */
@SuppressWarnings("serial")
public class ScrollBar extends VerticalLayout { // FIXME: Use CSSLayout.

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ScrollBar</code> instance.
   * @param dblock The block view.
   */
  public ScrollBar(DBlock dblock) {
    this.blockView = dblock;
    count = 0;
    init();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Scroll bar initialization.
   */
  public void init() {
    addStyleName(KopiTheme.SCROLL_BAR_STYLE);
    setSpacing(true);
    setWidth(null);
    setHeight("100%");

    up = new Button();
    up.setIcon(Utils.getImage("up.png").getResource());
    up.addStyleName("link");
    up.addStyleName("up");
    up.addClickListener(new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {  
        decrementPos();

        if (((DForm)blockView.getFormView()).getInAction()) {
	  // do not change the rows if there is currently a
	  // another command executed
	  return;
        }
        blockView.getFormView().performAsyncAction(new KopiAction("chart") {
          
          @Override
	  public void execute() throws VException {
	    if (!init) {
	      init = true;
	    } else {
	      try {
		blockView.setScrollPos(getScrollPos());
	      } catch (VException e) {
	        throw e;
	      }
	    }
	  }
        }); // performBasicAction : not asyncron!  
      }
	
      //-------------------------------------
      // DATA MEMBERS
      //-------------------------------------
    	      
      private boolean			init;
    });
    
    addComponent(up);
    setComponentAlignment(up, Alignment.TOP_CENTER);
    down = new Button(); 
    down.setIcon(Utils.getImage("down.png").getResource());
    down.addStyleName("link");
    down.addStyleName("down");
    down.addClickListener(new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
	incrementPos();     
	
        if (((DForm)blockView.getFormView()).getInAction()) {
	  // do not change the rows if there is currently a
	  // another command executed
	  return;
        }
        blockView.getFormView().performAsyncAction(new KopiAction("chart") {
          
          @Override
	  public void execute() throws VException {
	    if (!init) {
	      init = true;
	    } else {
	      try {
		blockView.setScrollPos(getScrollPos());
	      } catch (VException e) {
	        throw e;
	      }
	    }
	  }
        }); // performBasicAction : not asyncron!  
      }
	
      //-----------------------------------
      // DATA MEMBERS
      //-----------------------------------
    	      
      private boolean			init;
    });
    addComponent(down);
    setComponentAlignment(down, Alignment.BOTTOM_CENTER);
  }
  
  /**
   * Increments position.
   */
  public void incrementPos() {
    count++;
  }
  
  /**
   * Decrements position.
   */
  public void decrementPos() {
    if (count >= 0) {
      count--;
    }  
  }
  
  /**
   * Returns the scroll bar position.
   * @return The scroll bar position.
   */
  public int getScrollPos() {
    return count;
  }
  
  //---------------------------------------------------
  //  DATA MEMBERS
  //---------------------------------------------------
  
  public Button 				up;
  public Button 				down;
  public int					count;
  private DBlock                		blockView;
}
