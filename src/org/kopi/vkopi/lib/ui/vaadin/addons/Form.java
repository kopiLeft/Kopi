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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.BlockComponentData;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.form.FormState;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;

/**
 * The form server component.
 */
@SuppressWarnings("serial")
public class Form extends AbstractComponentContainer {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new form content component.
   * @param pageCount The page count.
   * @param titles The page titles.
   */
  public Form(int pageCount, String[] titles) {
    registerRpc(rpc);
    listeners = new ArrayList<FormListener>();
    blocks = new LinkedList<Component>();
    getState().pageCount = pageCount;
    getState().titles = titles;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Sets the form locale.
   * @param locale The form locale.
   */
  public void setLocale(String locale) {
    getState().locale = locale;
  }
  
  @Override
  protected FormState getState() {
    return (FormState) super.getState();
  }
  
  @Override
  protected FormState getState(boolean markAsDirty) {
    return (FormState) super.getState(markAsDirty);
  }
  
  /**
   * Adds a block to this form.
   * @param block The block to be added.
   * @param page The page index.
   * @param isFollow Is it a follow block ?
   * @param isChart Is it a chart block ?
   */
  public void addBlock(Component block, int page, boolean isFollow, boolean isChart) {
    if (block != null) {
      blocks.add(block);
      getState().blocksData.put(block, new BlockComponentData(isFollow, isChart, page));
      addComponent(block);
    }
  }
  
  /**
   * Registers a form listener.
   * @param l The listener to be registered.
   */
  public void addFormListener(FormListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a form listener.
   * @param l The listener to be removed.
   */
  public void removeFormListener(FormListener l) {
    listeners.remove(l);
  }
  
  /**
   * Goes to the given page.
   * @param page The page index.
   */
  public void gotoPage(int page) {
    getRpcProxy(FormClientRpc.class).selectPage(page);
  }
  
  /**
   * Sets the given page enabled or disabled.
   * @param enabled The page ability.
   * @param page The page index.
   */
  public void setEnabled(boolean enabled, int page) {
    getRpcProxy(FormClientRpc.class).setEnabled(enabled, page);
  }
  
  /**
   * Sets the block info position.
   * @param current The current record.
   * @param total The total records.
   */
  public void setPosition(int current, int total) {
    //getRpcProxy(FormClientRpc.class).setPosition(current, total);
    getState().currentPosition = current;
    getState().totalPositions = total;
  }
  
  /**
   * Fires a page selection event.
   * @param page The page index.
   */
  protected void firePageSelected(int page) {
    for (FormListener l : listeners) {
      if (l != null) {
	l.onPageSelection(page);
      }
    }
  }
  
  @Override
  public void replaceComponent(Component oldComponent, Component newComponent) {
    // component replacement is not supported.
  }

  @Override
  public int getComponentCount() {
    return blocks.size();
  }
  
  @Override
  public Iterator<Component> iterator() {
    return blocks.iterator();
  }
  
  /**
   * Requests to go to the next position.
   */
  protected void fireGotoNextPosition() {
    for (FormListener l : listeners) {
      if (l != null) {
	l.gotoNextPosition();
      }
    }
  }
  
  /**
   * Requests to go to the previous position.
   */
  protected void fireGotoPrevPosition() {
    for (FormListener l : listeners) {
      if (l != null) {
	l.gotoPrevPosition();
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected void fireGotoLastPosition() {
    for (FormListener l : listeners) {
      if (l != null) {
	l.gotoLastPosition();
      }
    }
  }

  /**
   * Requests to go to the last position.
   */
  protected void fireGotoFirstPosition() {
    for (FormListener l : listeners) {
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
    for (FormListener l : listeners) {
      if (l != null) {
	l.gotoPosition(posno);
      }
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private LinkedList<Component>		blocks;
  private final List<FormListener>	listeners;
  private FormServerRpc			rpc = new FormServerRpc() {
    
    @Override
    public void onPageSelection(int page) {
      firePageSelected(page);
    }

    @Override
    public void gotoNextPosition() {
      fireGotoNextPosition();
    }

    @Override
    public void gotoPrevPosition() {
      fireGotoPrevPosition();
    }

    @Override
    public void gotoLastPosition() {
      fireGotoLastPosition();
    }

    @Override
    public void gotoFirstPosition() {
      fireGotoFirstPosition();
    }

    @Override
    public void gotoPosition(int posno) {
      fireGotoPosition(posno);
    }
  };
}
