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

import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.ObjectFieldServerRpc;

import com.vaadin.ui.AbstractComponent;

/**
 * The Object field server side component.
 */
@SuppressWarnings("serial")
public abstract class ObjectField extends AbstractComponent {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ObjectField</code> instance.
   */
  public ObjectField() {
    setImmediate(true);
    listeners = new LinkedList<ObjectFieldListener>();
    registerRpc(rpc);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Registers an object field listener.
   * @param l The object field listener.
   */
  public void addObjectFieldListener(ObjectFieldListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes an object field listener.
   * @param l The object field listener.
   */
  public void removeObjectFieldListener(ObjectFieldListener l) {
    listeners.remove(l);
  }
  
  /**
   * Fires a goto previous record event on this text field.
   */
  protected void fireGotoPrevRecord() {
    for (ObjectFieldListener l : listeners) {
      l.gotoPrevRecord();
    }
  }
  
  /**
   * Fires a goto previous field event on this text field.
   */
  protected void fireGotoPrevField() {
    for (ObjectFieldListener l : listeners) {
      l.gotoPrevField();
    }
  }
  
  /**
   * Fires a goto next record event on this text field.
   */
  protected void fireGotoNextRecord() {
    for (ObjectFieldListener l : listeners) {
      l.gotoNextRecord();
    }
  }
  
  /**
   * Fires a goto next field event on this text field.
   */
  protected void fireGotoNextField() {
    for (ObjectFieldListener l : listeners) {
      l.gotoNextField();
    }
  }
  
  /**
   * Fires a goto next block event on this text field.
   */
  protected void fireGotoNextBlock() {
    for (ObjectFieldListener l : listeners) {
      l.gotoNextBlock();
    }
  }
  
  /**
   * Fires a goto last record event on this text field.
   */
  protected void fireGotoLastRecord() {
    for (ObjectFieldListener l : listeners) {
      l.gotoLastRecord();
    }
  }
  
  /**
   *  Fires a goto first record event on this text field.
   */
  protected void fireGotoFirstRecord() {
    for (ObjectFieldListener l : listeners) {
      l.gotoFirstRecord();
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private List<ObjectFieldListener>		listeners;
  private ObjectFieldServerRpc			rpc = new ObjectFieldServerRpc() {
    
    @Override
    public void gotoPrevRecord() {
      fireGotoPrevRecord();
    }
    
    @Override
    public void gotoPrevField() {
      fireGotoPrevField();
    }
    
    @Override
    public void gotoNextRecord() {
      fireGotoNextBlock();
    }
    
    @Override
    public void gotoNextField() {
      fireGotoNextField();
    }
    
    @Override
    public void gotoNextBlock() {
      fireGotoNextBlock();
    }
    
    @Override
    public void gotoLastRecord() {
      fireGotoLastRecord();
    }
    
    @Override
    public void gotoFirstRecord() {
      fireGotoFirstRecord();
    }
  };
}
