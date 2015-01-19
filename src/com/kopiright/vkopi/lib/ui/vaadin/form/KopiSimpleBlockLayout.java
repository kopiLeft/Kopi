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

import java.util.Vector;

import org.vaadin.cssinject.CSSInject;

import com.kopiright.vkopi.lib.form.KopiAlignment;
import com.kopiright.vkopi.lib.form.MultiFieldAlignment;
import com.kopiright.vkopi.lib.form.VStringField;
import com.kopiright.vkopi.lib.form.ViewBlockAlignment;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class KopiSimpleBlockLayout extends GridLayout implements KopiLayout {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>KopiSimpleBlockLayout</code> instance.
   * @param col The number of column.
   * @param line The number of lines.
   * @param align The block alignment.
   */
  public KopiSimpleBlockLayout(int col, int line, ViewBlockAlignment align) {
    super(col, line);
    setSizeUndefined();
    setSpacing(true);
    setMargin(false);
    addStyleName("simple-block");
    components = new Component[col][line];
    aligns = new KopiAlignment[col][line];
    minStart = new int[components.length + 2];
    alignComponentWidth = new int[components.length + 2];
    follows = new Vector<Component>();
    followsAligns = new Vector<KopiAlignment>();
    this.align = align;
    if (align != null) {
      addStyleName("aligned");
    }
  }
  
  //---------------------------------------------------
  // KOPILAYOUT IMPLEMENTATION
  //--------------------------------------------------- 

  @Override
  public void addLayoutComponent(Component comp, Object constraints) {
    if (constraints instanceof KopiAlignment) {
      KopiAlignment	align = (KopiAlignment)constraints;
      
      if (align.width < 0) {
	follows.addElement(comp);
	followsAligns.addElement(align);
      } else {
	aligns[align.x][align.y] = align;
	components[align.x][align.y] = comp;
      }
    } else {
      throw new IllegalArgumentException("cannot add to layout: constraint must be a KopiAlignment");
    }
  }
  
  @Override
  public void layoutContainer() {
    int borderSize = 5;
    int left = borderSize;
    int leftGap = 0;
    int compt;
    int xAlign = 0;
    int xPos;
    int yPos;
    int x1;
    int y1;
    boolean xAxis = true;
    boolean added = false;
    
    if (!computed) {
      precalculateComponentsAlignments();
    }
    
    for (int y = 0; y < components[0].length; y++) {
      leftGap = 0;
      xAlign = 0;
      left = borderSize; 
      for (int x = 0; x < components.length; x++) {
		
	if (components[x][y] != null) {	
	  
	  if (this.align == null) {  
	    added = false;
	    xAxis = true;
	    compt = 0;
	    xPos = 0;
	    yPos = 0;
	    x1 = Math.min(x + aligns[x][y].width - 1, x + getAllocatedWidth(x, y) - 1); 
            y1 = Math.min(y + getComponentHeight(components[x][y]) - 1, y + getAllocatedHeight(x, y) - 1);
	    while (!added) {
	      try { 
	        added = true;
	        addComponent(components[x][y], 
		             x + xPos, 
		             y + yPos, 
		             x1 + xPos, 
		             y1 + yPos);   
  	      } catch (OverlapsException oe) {
  	        added = false; 
	      
	        if (xAxis) {
		  compt ++;
		  xPos = compt;
		  yPos = 0;
		  if ((x1 + xPos + 1) > getColumns()) {
		    setColumns(x1 + xPos + 1);
		  }    
	        } else {	
	          xPos = 0;
	          yPos = compt;
	          if ((y1+ yPos + 1) > getRows()) {
		    setRows(y1+ yPos + 1);
	          }
                }
	        xAxis = !xAxis;
	      }    
	    }   
	    
	    if (components[x][y] instanceof DLabel) {
	      setComponentAlignment(components[x][y], Alignment.TOP_RIGHT);
	    } 
	  } else {
	    
	    addComponent(components[x][y],
	                 xAlign,
	                 y,
	                 xAlign,
	                 Math.min(y + getComponentHeight(components[x][y]) - 1, y + getAllocatedHeight(x, y) - 1));    
	     
	    int               componentWidth = getComponentWidth(components[x][y]);
	    int               cleft;

	    if (aligns[x][y] instanceof MultiFieldAlignment) {
	      if (aligns[x][y].alignRight) {
	        // label
	        cleft = ((x-1)/2) * (Math.max(componentWidth, getComponentWidth(components[x][y + 1])) + hgap) + borderSize;
	      } else {
	        // fields
	        cleft = ((x-1)/2) * (Math.max(componentWidth,getComponentWidth(components[x][y - 1])) + hgap) + borderSize;
	      }
	    } else if (!aligns[x][y].alignRight && align != null && align.isAligned(x / 2 + 1)) {
	      if (components[x][y] instanceof DLabel) {
	        cleft = borderSize;
	      } else {
	        if (align.isChart()) {
	          cleft = minStart[x + 1] - componentWidth;
	        } else {
	          cleft = minStart[x] ;
	        }
	      }
	    } else if (align != null && align.isAligned(x / 2 + 1)) {
	      cleft = borderSize;
	    } else if (aligns[x][y].alignRight) {
	      cleft = minStart[x + 1] - componentWidth;
	    } else {
	      cleft = left ;
	    }     
	    
	    if (components[x][y] instanceof DLabel) {	      
	      if(componentWidth == 0) {
		((DLabel)components[x][y]).setWidth(0, Unit.PIXELS);
	      } else {
		setComponentAlignment(components[x][y], Alignment.TOP_LEFT);	
		components[x][y].setWidth(alignComponentWidth[xAlign], Unit.PIXELS);
	      }  
	      leftGap += alignComponentWidth[xAlign];
	    }  else {
	      cleft = cleft - leftGap + (2 * borderSize) ;
	      if (cleft != 0) {
	        components[x][y].setStyleName("align-style-" + x + "-" + y);
	        setComponentLeftMargin("align-style-" + x + "-" + y, cleft);
	      } 
	      
	      if (cleft < 0) {
		leftGap += alignComponentWidth[xAlign] + (2 * borderSize) + hgap;
	      } else {
		leftGap += alignComponentWidth[xAlign] + cleft;
	      }
	    }  
	    xAlign++;
	  }
	}
	
	left = minStart[x + 1];
      }
    }
    
    for (int i = 0; i < follows.size(); i++) {
      KopiAlignment         align = followsAligns.elementAt(i);
      Component             comp = follows.elementAt(i);
      
      addInfoComponentAt(comp, align.x, align.y);
    }
  }
  
  /**
   * Returns the component height.
   */
  private int getComponentHeight(Component comp) {
    if (comp instanceof DTextField) {
      if (((DTextField)comp).getModel() instanceof VStringField) {
	if (((VStringField)((DTextField)comp).getModel()).getVisibleHeight() > 0) {
	  return ((VStringField)((DTextField)comp).getModel()).getVisibleHeight();
	}
      }
    }
    
    return 1;
  }
  
  /**
   * Returns the allocated height for the given column and row
   */
  private int getAllocatedHeight(int col, int row) {
    int		allocatedHeight = 1;
    
    for (int y = row + 1 ; y < components[col].length; y ++) {
      if (components[col][y] != null) {
	break;
      }
      
      allocatedHeight ++;
    }
    
    return allocatedHeight;
  }
  
  /**
   * Returns the allocated width for the given lolumn and row
   */
  private int getAllocatedWidth(int col, int row) {
    int		allocatedWidth = 1;
    
    for (int x = col + 1; x < components.length; x ++) {
      if (components[x][row] != null) {
	break;
      }
      
      allocatedWidth ++;
    }
    
    return allocatedWidth;
  }
  
  /**
   * Adds an info component
   * @param x The x coordinate of the component.
   * @param y The y coordinate of the component.
   */
  private void addInfoComponentAt(Component info, int x, int y) {
    Component		comp = components[x][y];
    Area		area = getComponentArea(comp);
    HorizontalLayout	content = new HorizontalLayout();
    
    content.setSpacing(true);
    content.addComponent(comp);
    content.addComponent(info);
    removeComponent(comp);
    addComponent(content,
	         area.getColumn1(),
	         area.getRow1(),
	         area.getColumn2(),
	         area.getRow2());    
  }
  
  @Override
  public int getColumnPos(int x) {
    if (x < minStart.length) {
      return minStart[x];
    }
    return 0;
  }
  
  public void setComponentLeftMargin (final String styleName, final int leftMargin) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() { 
	CSSInject cssInjector = new CSSInject(UI.getCurrent());
	cssInjector.setStyles("."+styleName+" { margin-left: "+leftMargin+"px !important; }");	
      }
    });
  }
  
  /**
   * Calculate the layout components alignments
   */
  private void precalculateComponentsAlignments() {
    
    for (int x = 0; x < components.length + 1; x++) {
      minStart[x] = 0;
      alignComponentWidth[x] = 0;
    }

    if (components.length == 0) {
      return;
    }
    
    for (int y = 0; y < components[0].length; y++) {
      for (int x = 0; x < components.length; x++) {
   
        if (components[x][y] != null 
            && !(aligns[x][y] instanceof MultiFieldAlignment)) {
          // use dimension with all follows
          int   componentWidth = getComponentWidth(x, y);

          minStart[x + aligns[x][y].width] = Math.max(minStart[x] + componentWidth + hgap,
                                                      minStart[x + aligns[x][y].width]);
        }
      }    
    }
    
    if (align != null) {
      // block alignment
      for (int x = 0; x < components.length; x++) {
        if ((x % 2 == 1) &&align.isChart() && align.isAligned(x / 2 + 1)) {
          minStart[x + 1] = align.getMinStart(x / 2 + 1);
        } else if (!align.isChart()) {
          // alignment if block is not a chart
          if (x % 2 == 1) {
            // fields
            minStart[x] = align.getMinStart(x / 2 + 1);
          } else {
            // labels
            minStart[x] = align.getLabelMinStart(x / 2 + 1);
          }
        }     
      }
      
      int xAlign = 0;
      for (int y = 0; y < components[0].length; y++) {
	xAlign = 0;
	for (int x = 0; x < components.length; x++) {
          if (components[x][y] != null && align != null) {
            alignComponentWidth[xAlign] =  Math.max(alignComponentWidth[xAlign], getComponentWidth(components[x][y]));
            xAlign ++;
          }
	}
      } 
    }
    computed = true;
  }
  
  /**
   * Returns component width from its graphic coordinates
   * 
   * @param x		x axis coordinate
   * @param y		y axis coordinate
   */
  private int getComponentWidth(int x, int y) {
    int         componentWidth = 8;
    
    componentWidth = getComponentWidth(components[x][y]);

    int initWidth = componentWidth;
    
    for (int i = 0; i < follows.size(); i++) {
      KopiAlignment         align = followsAligns.elementAt(i);

      if (align.x != x || align.y != y) {
        continue;
      }

      Component         comp = follows.elementAt(i);
      
      int         componentWidth2 = 0;
      
      componentWidth2 = getComponentWidth(comp);    
      componentWidth += componentWidth2 + hgap;
    }
    if (initWidth != componentWidth ) {
      return componentWidth;
    } else {
      return initWidth;
    }
  }
  
  /**
   * Returns component width
   * 
   * @param comp		the component to check width
   */
  private int getComponentWidth(Component comp) {
    int width = 0;
    if (comp instanceof DTextField) {
      width = getTextFieldWidth((DTextField) comp);
    } else if (comp instanceof DLabel) {
      width = getLabelWidth((DLabel)comp);
    } 
    
    return width;
  }
  
  /**
   * Returns text field width
   * 
   * @param textField		the text field to check width
   */
  private int getTextFieldWidth(DTextField textField) {
    int         width = textField.getModel().getWidth();
    width = (width * 7) + 5;
    
    return width;
  }
  
  /**
   * Returns label width
   * 
   * @param label		the label to check width
   */
  private int getLabelWidth(DLabel label) {
    int         width = 0;
    if (label.getLabel() != null) {
      width = label.getLabel().getValue().length();
    }
    if (width > 0) {
      width = (width * 6) + 30;
    }
    return width;
  }
 
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private    Component[][]			components;
  private    KopiAlignment[][]			aligns;
  private    ViewBlockAlignment			align;
  private    Vector<Component> 			follows;
  private    Vector<KopiAlignment>		followsAligns;
  protected  int				hgap = 7;
  protected  boolean				computed = false;
  protected  int[]				minStart;
  protected  int[]				alignComponentWidth;
}
