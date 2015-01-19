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

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * A vertical page that lays inner components vertically.
 */
@SuppressWarnings("serial")
public class VerticalPage extends VerticalLayout implements DPage {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>VerticalPage</code> instance.
   */
  public VerticalPage () {
    setSpacing(true);
    setSizeFull();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void addBlock(DBlock block) {  
    AbstractComponent abstractBlock ;  
    
    if (block.getModel().isDroppable()) {
      abstractBlock = block.getLayoutWrapper();
    } else {
      abstractBlock = block;
    }   
    abstractBlock.setSizeUndefined();
    addComponent(abstractBlock);
    if (block.getContent() instanceof KopiMultiBlockLayout) {
      setComponentAlignment(abstractBlock, Alignment.MIDDLE_CENTER);
    } else {
      setComponentAlignment(abstractBlock, Alignment.MIDDLE_LEFT);
    }
    
    last = block;
  }
  
  @Override
  public void addFollowBlock(DBlock block) {
    if (last != null) {
      VerticalLayout	temp = new VerticalLayout();
      
      removeComponent(last);
      last.setSizeUndefined();
      block.setSizeUndefined();
      temp.addComponent(last);
      temp.addComponent(block);
      temp.setSizeUndefined();
      temp.setSpacing(false);
      addComponent(temp);
      
      if (last instanceof DBlock) {
        if (((DBlock)last).getContent() instanceof KopiMultiBlockLayout) {
	  setComponentAlignment(temp, Alignment.MIDDLE_CENTER);
	} else {
	  setComponentAlignment(temp, Alignment.MIDDLE_LEFT);
	}
      }
      
    } else {
      addBlock(block);
    }
    last = null;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private Component			last;
}
