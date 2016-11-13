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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.base.Styles;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.VBlock;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A form page, can be either or vertical or horizontal page.
 */
public class VPage extends Composite {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates the page widget.
   * @param content The wrapped widget
   */
  public VPage(CellPanel content) {
    initWidget(content);
    setStyleName(Styles.FORM_PAGE);
    this.content = content;
    this.content.setSpacing(0);
    this.content.setStyleName(Styles.FORM_PAGE_CONTENT);
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Adds a child to this page.
   * @param child The child widget.
   * @param hAlign The horizontal alignment.
   * @param vAlign The vertical alignment.
   */
  public void add(Widget child, HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
    content.add(child);
    if (hAlign != null) {
      content.setCellHorizontalAlignment(child, hAlign);
    }
    if (vAlign != null) {
      content.setCellVerticalAlignment(child, vAlign);
    }
    last = child;
  }
  
  /**
   * Adds a follow widget.
   * @param child The widget to be added.
   * @param hAlign The horizontal alignment.
   * @param vAlign The vertical alignment.
   */
  public void addFollow(Widget child, HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
    if (last != null) {
      VerticalPanel	temp = new VerticalPanel();
      
      temp.setStyleName("follow-blocks-container");
      content.remove(last);
      last.addStyleDependentName("orig");
      temp.add(last);
      temp.add(child);
      child.addStyleDependentName("aligned");
      content.add(temp);
    } else {
      add(child, hAlign, vAlign);
    }
    last = null;
  }
  
  /**
   * Sets the block caption.
   * @param block The block widget.
   */
  public void setCaption(VBlock block) {
    setCaption(content, block);
  }
  
  /**
   * Sets the block caption.
   * @param content The caption container.
   * @param block The block widget.
   */
  protected void setCaption(CellPanel content, VBlock block) {
    Widget		caption;
    
    caption = block.getCaption();
    if (caption != null) {
      if (content instanceof HorizontalPanel) {
	// wrap it in a vertical content before
	VerticalPanel		temp = new VerticalPanel();
	int			index = content.getWidgetIndex(block);
	
	temp.addStyleName("k-centered-page-wrapper");
	temp.add(caption);
	temp.add(block);
	((HorizontalPanel)content).insert(temp, index);
      } else if (content instanceof VerticalPanel) {
	int		index = content.getWidgetIndex(block);
	
	if (index >= 0) {
	  ((VerticalPanel)content).insert(caption, index);
	} else {
	  // it is a follow block
	  for (int i = 0; i < content.getWidgetCount(); i++) {
	    if (content.getWidget(i) != null
		&& "follow-blocks-container".equals(content.getWidget(i).getStyleName()))
	    {
	      setCaption((CellPanel)content.getWidget(i), block);
	    }
	  }
	}
      } else {
	content.add(caption); // not really suitable.
      }
      content.setCellHorizontalAlignment(caption, HasHorizontalAlignment.ALIGN_LEFT);
      content.setCellVerticalAlignment(caption, HasVerticalAlignment.ALIGN_TOP);
    }
  }
  
  @Override
  public Widget getWidget() {
    return super.getWidget();
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final CellPanel			content;
  private Widget				last; 
}
