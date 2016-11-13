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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.util.ArrayList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;

/**
 * The server side component of a simple block.
 * This UI component supports only laying components for simple
 * layout view.
 */
@SuppressWarnings("serial")
public abstract class Block extends AbstractSingleComponentContainer {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new block server side component.
   */
  public Block(boolean droppable) {
    this.droppable = droppable;
    registerRpc(rpc);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the block title.
   * @param title The block title.
   */
  public void setTitle(String title) {
    setCaption(title);
  }
  
  /**
   * Switches the block view.
   * @param detail Should be switch to the detail view ?
   */
  public void switchView(boolean detail) {
    getRpcProxy(BlockClientRpc.class).switchView(detail);
  }
  
  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  public void updateScroll(int pageSize, int maxValue, boolean enable, int value) {
    //getRpcProxy(BlockClientRpc.class).updateScroll(pageSize, maxValue, enable, value);
    getState().scrollPageSize = pageSize;
    getState().maxScrollValue = maxValue;
    getState().enableScroll = enable;
    getState().scrollValue = value;
  }
  
  /**
   * Sets the animation enabled for block view switch.
   * @param isAnimationEnabled Is animation enabled.
   */
  public void setAnimationEnabled(boolean isAnimationEnabled) {
    getState().isAnimationEnabled = isAnimationEnabled;
  }
  
  @Override
  protected BlockState getState() {
    return (BlockState) super.getState();
  }
  
  /**
   * Adds a component to this block.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  public void addComponent(Component component,
                           int x,
                           int y,
                           int width,
                           boolean alignRight,
                           boolean useAll)
  {
    getLayout().addComponent(component, x, y, width, alignRight, useAll);
  }
  
  /**
   * Returns the block layout.
   * @return the block layout.
   */
  public BlockLayout getLayout() {
    if (layout == null) {
      layout = createLayout();
      if (droppable) {
	dndWrapper = new DragAndDropWrapper(layout);
	dndWrapper.setImmediate(true);
	setContent(dndWrapper);
      } else {
	setContent(layout);
      }
    }
    
    return layout;
  }
  
  /**
   * Sets the block drop handler.
   * @param dropHandler The drop handler.
   */
  public void setDropHandler(DropHandler dropHandler) {
    if (dndWrapper != null) {
      dndWrapper.setDropHandler(dropHandler);
    }
  }

  /**
   * Sets the start drag mode.
   * @param dragStartMode The start drag mode.
   */
  public void setDragStartMode(DragStartMode dragStartMode) {
    if (dndWrapper != null) {
      dndWrapper.setDragStartMode(dragStartMode);
    }
  }
  /**
   * Sets data flavors available in the DragAndDropWrapper is used to start an
   * HTML5 style drags. Most commonly the "Text" flavor should be set.
   * Multiple data types can be set.
   * 
   * @param type
   *            the string identifier of the drag "payload". E.g. "Text" or
   *            "text/html"
   * @param value
   *            the value
   */
  public void setHTML5DataFlavor(String type, Object value) {
    if (dndWrapper != null) {
      dndWrapper.setHTML5DataFlavor(type, value);
    }
  }
  
  /**
   * Registers a new block listener.
   * @param l The listener to be registered.
   */
  public void addBlockListener(BlockListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a registered block listener.
   * @param l The listener to be removed.
   */
  public void removeBlockListener(BlockListener l) {
    listeners.add(l);
  }
  
  /**
   * Fired when the scroll position has changed.
   * @param value The new scroll position.
   */
  protected void fireOnScroll(int value) {
    for (BlockListener l : listeners) {
      if (l != null) {
	l.onScroll(value);
      }
    }
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  
  /**
   * Creates the block layout.
   * @return The created block layout.
   */
  public abstract BlockLayout createLayout();
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final boolean			droppable;
  private DragAndDropWrapper		dndWrapper;
  private BlockLayout			layout;
  private List<BlockListener>		listeners = new ArrayList<BlockListener>();
  private BlockServerRpc		rpc = new BlockServerRpc() {
    
    @Override
    public void updateScrollPos(int value) {
      fireOnScroll(value);
    }
  };
}
