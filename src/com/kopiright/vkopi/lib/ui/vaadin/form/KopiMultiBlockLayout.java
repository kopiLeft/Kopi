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

import com.kopiright.vkopi.lib.form.KopiAlignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;

/**
 * The <code>KopiMultiBlockLayout</code> is the a layout for simple
 * blocks component arrangement.
 */
@SuppressWarnings("serial")
public class KopiMultiBlockLayout extends GridLayout implements KopiLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>KopiMultiBlockLayout</code> instance.
   * @param col The number of columns.
   * @param line The number of lines.
   */
  public KopiMultiBlockLayout(int col, int line) {
    super(col + 1, line);
    setSpacing(false);
    addStyleName("chart-block");
    components = new Component[col][line];
    aligns = new KopiAlignment[col][line];
    sizes = new int[col];
    realPos = new int[col];
  }

  //---------------------------------------------------
  // KOPILAYOUT IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void addLayoutComponent(final Component comp, final Object constraints) {
    if (constraints instanceof KopiAlignment) {
      KopiAlignment	align = (KopiAlignment)constraints;

      aligns[align.x][align.y] = align;
      components[align.x][align.y] = comp;
    } else if (comp instanceof ScrollBar) {
      scrollBar = (ScrollBar)comp;
    } else {
      throw new IllegalArgumentException("cannot add to layout: constraint must be a KopiAlignment");
    }
  }

  @Override
  public void layoutContainer() {
    int borderLeft = 5;
    int left = borderLeft;

    if (!computed) {
      precalculateComponentsSizes();
    }

    for (int x = 0; x < components.length; x++) {
      realPos[x] = left - 2 * hgap;
      //labels
      addComponent(components[x][0], aligns[x][0].x, aligns[x][0].y);
      
      if (components[x][0] instanceof DLabel) {
	if (components[x][1] instanceof DTextField) {
	  ((DLabel)components[x][0]).getLabel().setWidth(((DTextField)components[x][1]).getFieldWidth(),
	                                                 ((DTextField)components[x][1]).getFieldWidthUnits());
	}
      }
           
      for (int y = 1; y < components[0].length; y++) {
        //fields
	if (!components[x][y].isVisible()) {
	  CssLayout container = new CssLayout();
	  container.setWidth(sizes[x], Unit.PIXELS);
	  container.addComponent(components[x][y]);
	  container.setVisible(true);
	  addComponent(container, aligns[x][y].x, aligns[x][y].y);
	} else {
          addComponent(components[x][y], aligns[x][y].x, aligns[x][y].y);
	}
      }
      left += sizes[x] + hgap;
    }
    //scrollbar
    if (scrollBar != null) {
      addComponent(scrollBar,
	           components.length,
	           1,
	           components.length,
	           components[0].length - 1);
    }
  }
  
  @Override
  public int getColumnPos(int x) {
    if (x < realPos.length - 1) {
      return realPos[x + 1];
    } else if (x == realPos.length - 1) {
      return realPos[x] + sizes[x] + hgap;
    }
    return 0;
  }
  
  /**
   * Calculate layout components sizes
   */
  private void precalculateComponentsSizes() {
    for (int x = 0; x < components.length; x++) {
      if (components[x][1] instanceof DTextField) {   
        int         fieldWidth = ((DTextField) components[x][1]).getModel().getWidth();
        fieldWidth = (fieldWidth * 7) + 5;
       
        sizes[x] = Math.max(sizes[x], fieldWidth);
      }
    }

    computed = true;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private    Component[][]	        components;
  private    KopiAlignment[][]		aligns;
  private    ScrollBar			scrollBar;
  protected  int[]		        realPos;
  protected  int[]		        sizes;
  protected  int			hgap = 0;
  protected  boolean		        computed = false;
}
