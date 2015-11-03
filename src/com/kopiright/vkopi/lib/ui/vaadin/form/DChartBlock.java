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

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.kopi.vaadin.addons.BlockLayout;
import org.kopi.vaadin.addons.BlockListener;
import org.kopi.vaadin.addons.ChartBlockLayout;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.form.KopiAlignment;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.ui.Component;

/**
 * The <code>DChartBlock</code> is a {@link DBlock} representing
 * a chart block where data is represented in a data grid.
 */
@SuppressWarnings("serial")
public class DChartBlock extends DBlock implements BlockListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DChartBlock</code> instance.
   * @param parent The parent form.
   * @param model The block model.
   */
  public DChartBlock(DForm parent, VBlock model) {
    super(parent, model);
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      addBlockListener(this);
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void add(UComponent comp, KopiAlignment constraints) {
    addComponent((Component)comp,
                 constraints.x,
                 constraints.y,
                 constraints.width,
                 constraints.alignRight,
                 constraints.useAll);
  }
  
  @Override
  public BlockLayout createLayout() {
    ChartBlockLayout		layout;
    
    layout = new ChartBlockLayout(displayedFields, getModel().getDisplaySize() + 1);
    layout.setScrollable(getModel().getDisplaySize() < getModel().getBufferSize());
    return layout;
  }

  @Override
  public void validRecordNumberChanged() {
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      eventQueue.push(new RNCEvent());
      // schedule an RNC task execution.
      // this aims to minimize RPC communication
      // between server and client side
      timer.schedule(new RNCTask(), 100);
    }
  }
  
  @Override
  protected void refresh(boolean force) {
    super.refresh(force);
    // don't update scroll bar when the scroll position
    // is being changing from the client side.
    if (!scrolling) {
      updateScrollbar();
    }
  }

  @Override
  public void onScroll(final int value) {
    if (((DForm)getFormView()).getInAction()) {
      // do not change the rows if there is currently a
      // another command executed
      return;
    }
    if (!init) {
      init = true; // on initialization, we do not scroll.
    } else {
      try {
        scrolling = true;
	setScrollPos(value);
	scrolling = false;
      } catch (VException e) {
        e.printStackTrace();
	eventQueue.push(new RNCEvent());
	timer.schedule(new RNCTask(), 100);
      }
    }
  }

  /**
   * Updates the scroll bar position.
   */
  private void updateScrollbar() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	int		validRecords = getModel().getNumberOfValidRecord();
	int		dispSize = getModel().getDisplaySize();
	
	updateScroll(dispSize,
	             validRecords,
	             validRecords > dispSize,
	             getModel().getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)));
      }
    });
  }
  
  //-------------------------------------------------
  // SCROLL UPDATE HANDLING
  //-------------------------------------------------
  
  /**
   * An event called to update scroll bar.
   * @author hacheni
   *
   */
  /*package*/ class RNCEvent implements Runnable {

    @Override
    public void run() {
      updateScrollbar();
    }
  }
  
  /**
   * A scroll bar update event needed to control
   * updates of the scroll bar and to avoid overloading
   * the client side component.
   */
  /*package*/ class RNCTask extends TimerTask {

    @Override
    public void run() {
      if (!eventQueue.isEmpty()) {
	// run the last event queued and then clear the queue.
	RNCEvent	event = eventQueue.pop();
	
	while (!eventQueue.isEmpty() && event == null) {
	  event = eventQueue.pop();
	}
	if (event != null) {
	  event.run();
	  eventQueue.clear();
	}
      }
    }
  }
  
  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  
  private boolean			init = false;
  /*
   * This flag was added to avoid mutual communication
   * betwwen client and server side when the scroll bar
   * position is changed from the client side.
   * Thus, scroll bar position is not changed by server
   * side when it is changed from the client side
   * @see {@link #refresh(boolean) 
   */
  private boolean                       scrolling = false;
  private final Stack<RNCEvent>		eventQueue = new Stack<RNCEvent>();
  private final Timer			timer = new Timer();
}