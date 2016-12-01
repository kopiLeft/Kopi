/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

import org.kopi.vkopi.lib.ui.vaadin.addons.Form;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ConnectorUtils;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.ResourcesUtil;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockConnector;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.FormListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.event.PositionPanelListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.VWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.window.WindowConnector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * The form connector relating server and client side.
 */
@SuppressWarnings("serial")
@Connect(value = Form.class, loadStyle = LoadStyle.DEFERRED)
public class FormConnector extends AbstractComponentContainerConnector implements FormListener, PositionPanelListener {

  @Override
  protected void init() {
    super.init();
    registerRpc(FormClientRpc.class, rpc);
    getWidget().addFormListener(this);
    getWidget().addPositionPanelListener(this);
  }
  
  @Override
  public VForm getWidget() {
    return (VForm) super.getWidget();
  }
  
  @Override
  public FormState getState() {
    return (FormState) super.getState();
  }
  
  @Override
  public boolean delegateCaptionHandling() {
    // do not delegate caption handling
    return false;
  }
  
  @Override
  public void updateCaption(ComponentConnector connector) {
    // not handled
  }

  @Override
  public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    getWidget().init(getConnection(),
	             getState().locale,
	             getState().pageCount,
	             getState().titles,
	             ResourcesUtil.getImageURL(getConnection(), "single.gif"));
    // look for blocks
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof BlockConnector) {
	BlockComponentData	data = getState().blocksData.get(child);
	
	if (data != null) {
	  getWidget().addBlock(child.getWidget(), data.page, data.isFollow, data.isChart);
	}
      }
    }
  }
  
  /**
   * Sets the current position of the position panel.
   */
  @OnStateChange({"currentPosition", "totalPositions"})
  /*package*/ void setPosition() {
    setCurrentPosition(getState().currentPosition, getState().totalPositions);
  }

  @Override
  public void onPageSelection(int page) {
    // communicates the dirty values before leaving page
    ConnectorUtils.getParent(this, WindowConnector.class).cleanDirtyValues(null);
    disableAllBlocksActors();
    getRpcProxy(FormServerRpc.class).onPageSelection(page);
  }
  
  @Override
  public void onUnregister() {
    getWidget().removeFormListener(this);
    getWidget().removePositionPanelListener(this);
    if (getWidget().getParent() instanceof VWindow) {
      ((VWindow)getWidget().getParent()).clearFooter();
    }
    getWidget().clear();
    super.onUnregister();
  }

  @Override
  public void gotoNextPosition() {
    getRpcProxy(FormServerRpc.class).gotoNextPosition();
  }

  @Override
  public void gotoPrevPosition() {
    getRpcProxy(FormServerRpc.class).gotoPrevPosition();
  }

  @Override
  public void gotoLastPosition() {
    getRpcProxy(FormServerRpc.class).gotoLastPosition();
  }

  @Override
  public void gotoFirstPosition() {
    getRpcProxy(FormServerRpc.class).gotoFirstPosition();
  }

  @Override
  public void gotoPosition(int posno) {
    getRpcProxy(FormServerRpc.class).gotoPosition(posno);
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Sets the position in the position panel.
   * @param current The current position.
   * @param total The total number of positions.
   */
  public void setCurrentPosition(int current, int total) {
    getWidget().setPosition(current, total);
  }
  
  /**
   * Cleans the dirty values of this form.
   */
  public void cleanDirtyValues(BlockConnector active, boolean transferFocus) {
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof BlockConnector) {
        ((BlockConnector) child).cleanDirtyValues(active, transferFocus);
      }
    }
  }
  
  /**
   * Disables all block actors
   */
  public void disableAllBlocksActors() {
    for (ComponentConnector child : getChildComponents()) {
      if (child instanceof BlockConnector) {
        ((BlockConnector) child).setColumnViewsActorsEnabled(false);
      }
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private FormClientRpc			rpc = new FormClientRpc() {
    
    @Override
    public void selectPage(final int page) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().selectPage(page);
        }
      });
    }

    @Override
    public void setEnabled(final boolean enabled, final int page) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setEnabled(enabled, page);
        }
      });
    }

    @Override
    public void setPosition(final int current, final int total) {
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        
        @Override
        public void execute() {
          getWidget().setPosition(current, total);
        }
      });
    }
  };
}
