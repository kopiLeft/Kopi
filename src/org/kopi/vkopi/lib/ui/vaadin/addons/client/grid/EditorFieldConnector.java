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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.grid;

import com.google.gwt.user.client.Timer;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractFieldConnector;

/**
 * Grid editor field connector.
 */
@SuppressWarnings("serial")
public abstract class EditorFieldConnector extends AbstractFieldConnector implements DeferredWorker {
  
  @Override
  protected void init() {
    registerRpc(EditorFieldClientRpc.class, new EditorFieldClientRpc() {
      
      @Override
      public void setBlink(boolean blink) {
        getEditorWidget().setBlink(blink);
      }
      
      @Override
      public void focus() {
        layzyFocusEngine.schedule(150);
      }
    });
  }
  
  /**
   * Sets the color properties of the editor widget.
   */
  @OnStateChange({"foreground", "background"})
  /*package*/ void setColor() {
    getEditorWidget().setColor(getState().foreground, getState().background);
  }
  
  /**
   * Returns the editor widget of this connector.
   * @return The editor widget of this connector.
   */
  protected EditorField<?> getEditorWidget() {
    return (EditorField<?>) getWidget();
  }
  
  @Override
  public EditorFieldState getState() {
    return (EditorFieldState) super.getState();
  }
  
  @Override
  public boolean isWorkPending() {
    return layzyFocusEngine.isRunning();
  }
  
  @Override
  public void onUnregister() {
    super.onUnregister();
    layzyFocusEngine.cancel();
  }
  
  /**
   * Sends the value to the server side
   */
  protected void sendValueToServer() {
    // to be overridden by sub classes
  }
  
  //---------------------------------------------------
  // NAVIGATION
  //---------------------------------------------------
  
  /**
   * Performs a request to go to the next field.
   */
  public void gotoNextField() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoNextField();
  }

  /**
   * Performs a request to go to the previous field.
   */
  public void gotoPrevField() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoPrevField();
  }

  /**
   * Performs a request to go to the next block.
   */
  public void gotoNextBlock() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoNextBlock();
  }

  /**
   * Performs a request to go to the previous record.
   */
  public void gotoPrevRecord() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoPrevRecord();
  }

  /**
   * Performs a request to go to the next record.
   */
  public void gotoNextRecord() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoNextRecord();
  }

  /**
   * Performs a request to go to the first record.
   */
  public void gotoFirstRecord() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoFirstRecord();
  }

  /**
   * Performs a request to go to the last field.
   */
  public void gotoLastRecord() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoLastRecord();
  }

  /**
   * Performs a request to go to the next empty must fill field field.
   */
  public void gotoNextEmptyMustfill() {
    getRpcProxy(EditorFieldNavigationServerRpc.class).gotoNextEmptyMustfill();
  }
  
  /**
   * Lazy focus engine executor
   */
  private final Timer   layzyFocusEngine = new Timer() {
    
    @Override
    public void run() {
      getEditorWidget().focus();
    }
  };
}
