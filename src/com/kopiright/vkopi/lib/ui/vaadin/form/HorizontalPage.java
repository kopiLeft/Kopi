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

import java.util.Iterator;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>HorizontalPage</code> is a page that lays inner components
 * horizontally.
 */
@SuppressWarnings("serial")
public class HorizontalPage extends HorizontalLayout implements DPage { //!!! FIXME: Use CSSLayout
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code><HorizontalPage/code> instance.
   */
  public HorizontalPage() {
    setSpacing(true);
    setSizeUndefined();
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
      temp.addComponent(last);
      temp.addComponent(block);
      temp.setSizeUndefined();
      temp.setSpacing(false);
      addComponent(temp);
      
      for (Iterator<Component> iterator = temp.iterator(); iterator.hasNext();) {
	Component comp = iterator.next();
	  
	if (comp instanceof DBlock) {
	  if (((DBlock)comp).getContent() instanceof KopiMultiBlockLayout) {
	    temp.setComponentAlignment(comp, Alignment.MIDDLE_CENTER);
	  } else {
	    temp.setComponentAlignment(comp, Alignment.MIDDLE_LEFT);
	  }
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
  
  private Component				last;
}
