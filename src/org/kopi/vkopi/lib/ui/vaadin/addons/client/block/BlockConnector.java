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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import org.kopi.vkopi.lib.ui.vaadin.addons.Block;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The block component connector.
 */
@SuppressWarnings("serial")
@Connect(value = Block.class, loadStyle = LoadStyle.DEFERRED)
public class BlockConnector extends AbstractSingleComponentContainerConnector implements ValueChangeHandler<Integer> {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  protected void init() {
    super.init();
    registerRpc(BlockClientRpc.class, rpc);
  }
  
  @Override
  public VBlock getWidget() {
    return (VBlock) super.getWidget();
  }
  
  @Override
  public BlockState getState() {
    return (BlockState) super.getState();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    return false;
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled.
  }
  
  @OnStateChange("caption")
  /*package*/ void setCaption() {
    getWidget().setCaption(getState().caption);
  }
  
  @OnStateChange({"scrollPageSize", "maxScrollValue", "enableScroll", "scrollValue"})
  /*package*/ void updateScroll() {
    if (getLayout() != null) {
      getLayout().updateScroll(getState().scrollPageSize,
                               getState().maxScrollValue,
                               getState().enableScroll,
                               getState().scrollValue);
    }
  }
  
  @Override
  public void onUnregister() {
    getWidget().clear();
  }
  
  /**
   * Handles the content widget.
   */
  protected void handleContentWidget() {
    Widget	content = getContentWidget();
    
    if (content instanceof VDragAndDropWrapper) {
      dndWrapper = (VDragAndDropWrapper)content;
    } else if (content instanceof BlockLayout) {
      layout = (BlockLayout) content;
    }
  }
  
  /**
   * Sets the block content.
   */
  protected void setContent() {
    if (dndWrapper != null) {
      getWidget().setContent(dndWrapper);
    } else {
      getWidget().setContent(layout.cast());
    }
  }
  
  /**
   * Returns the block layout.
   * @return The block layout;
   */
  protected BlockLayout getLayout() {
    return layout;
  }
  
  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    handleContentWidget();
    setContent();
    if (getLayout() != null) {
      getWidget().setLayout(getLayout());
      if (getLayout() instanceof VChartBlockLayout) {
	((VChartBlockLayout)getLayout()).addValueChangeHandler(this);
      }
    }
  }

  @Override
  public void onValueChange(ValueChangeEvent<Integer> event) {
    getRpcProxy(BlockServerRpc.class).updateScrollPos(event.getValue());
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * saved layout instance;
   */
  private BlockLayout			layout;
  private VDragAndDropWrapper		dndWrapper;
  
  /**
   * The client RPC implementation.
   */
  private BlockClientRpc		rpc = new BlockClientRpc() {
    
    @Override
    public void switchView(final boolean detail) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().switchView(detail);
        }
      });
    }

    @Override
    public void updateScroll(final int pageSize, final int maxValue, final boolean enable, final int value) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          if (getLayout() != null) {
            getLayout().updateScroll(pageSize, maxValue, enable, value);
          }
        }
      });
    }
  };
}
