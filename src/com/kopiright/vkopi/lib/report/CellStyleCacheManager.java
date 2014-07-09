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

package com.kopiright.vkopi.lib.report;

import java.util.HashSet;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Manage the HSSFCellStyle cache.
 * In order to not have the 4000 style limit, cell styles are cached base on their
 * hash codes within a {@link Set} and reused when it two or more cells have the same
 * style.
 * Use {@link #setCellStyle(HSSFWorkbook, HSSFCell, HSSFCellStyle)} for caching functions.
 */
public class CellStyleCacheManager {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new Cell style cache manager.
   */
  public CellStyleCacheManager() {
    this.cellStyles = new HashSet<CellStyleWrapper>();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the cell style to the given {@code cell}. The given {@code style} is
   * compared to the cached styles and if the same style exists in the cache, we
   * use the cached one.
   * 
   * @param cell The cell which the style will be applied on.
   * @param cellStyle The cell style to be applied.
   */
  public void setCellStyle(HSSFWorkbook workbook, HSSFCell cell, HSSFCellStyle cellStyle) {
    CellStyleWrapper		cachedCellStyleWrapper = null;
    
    // look for the same style in the cache.
    for (CellStyleWrapper cellStyleWrapper : cellStyles) {
      // cell style wrapper found ==> don't search anymore
      if (cellStyleWrapper.equals(new CellStyleWrapper(cellStyle))) {
	cachedCellStyleWrapper = cellStyleWrapper;
	break;
      }
    }

    // None of the cached styles corresponds to the desired
    // cell style. We will copy the original cell style cause the
    // incoming style refers to the same object 'defaultCellStyle'
    // attribute. So, we need to create a new style and copy all the
    // incoming style properties to the new created style.
    // Then, the copied style will be cached for further use.
    if (cachedCellStyleWrapper == null) {
      CellStyleWrapper 		newWrap;
      HSSFCellStyle		newCellStyle;
      
      newCellStyle = workbook.createCellStyle();
      // copy the cell style
      copyCellStyle(workbook, cellStyle, newCellStyle);
      newWrap = new CellStyleWrapper(newCellStyle);
      cellStyles.add(newWrap);
      cachedCellStyleWrapper = newWrap;
    }
    
    cell.setCellStyle(cachedCellStyleWrapper.getHSSFCellStyle());
  }
  
  public HSSFCellStyle getDefaultStyle(HSSFWorkbook workbook) {
    // initializes the default style once
    if (defaultCellStyle == null) {
      defaultCellStyle = workbook.createCellStyle();
    }
    
    return defaultCellStyle;
  }
  
  /**
   * Copies the cell style from a source style to a target style.
   * @param workbook The workbook.
   * @param source The source style.
   * @param target The target style.
   */
  protected void copyCellStyle(HSSFWorkbook workbook, HSSFCellStyle source, HSSFCellStyle target) {
    //copy all all style properties
    target.setAlignment(source.getAlignment());
    target.setBorderBottom(source.getBorderBottom());
    target.setBorderLeft(source.getBorderLeft());
    target.setBorderRight(source.getBorderRight());
    target.setBorderTop(source.getBorderTop());
    target.setBottomBorderColor(source.getBottomBorderColor());
    target.setDataFormat(source.getDataFormat());
    target.setFillBackgroundColor(source.getFillBackgroundColor());
    target.setFillForegroundColor(source.getFillForegroundColor());
    target.setFillPattern(source.getFillPattern());
    target.setFont(workbook.getFontAt(source.getFontIndex()));
    target.setHidden(source.getHidden());
    target.setIndention(source.getIndention());
    target.setLeftBorderColor(source.getLeftBorderColor());
    target.setLocked(source.getLocked());
    target.setRightBorderColor(source.getRightBorderColor());
    target.setRotation(source.getRotation());
    target.setTopBorderColor(source.getTopBorderColor());
    target.setVerticalAlignment(source.getVerticalAlignment());
    target.setWrapText(source.getWrapText()); 
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * Helping class to compare wrapped cell style
   * since the equals and hashcode methods cannot
   * be redefined in the original class {@link HSSFCellStyle}.
   * Original equals and hashcode methods are based on the cell
   * format and the cell index which is not enough for our case.
   */
  /*package*/ static class CellStyleWrapper {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public CellStyleWrapper(HSSFCellStyle style) {
      this.style = style;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    /**
     * @Override
     */
    public boolean equals(Object obj) {      
      if (obj instanceof CellStyleWrapper) {
	CellStyleWrapper 	wrapper;
	
	wrapper = (CellStyleWrapper)obj;
	// all other properties are constants. So the equals method
	// depends only with the used properties.
	return (style.getAlignment() == wrapper.getHSSFCellStyle().getAlignment())
	  && (style.getDataFormat() == wrapper.getHSSFCellStyle().getDataFormat())
	  && (style.getFillForegroundColor() == wrapper.getHSSFCellStyle().getFillForegroundColor());
      }
      
      return false;
    }
    
    @Override
    public int hashCode() {
      return style.hashCode()
        + style.getAlignment()
        + style.getDataFormat()
        + style.getFillForegroundColor();
    }

    /**
     * Returns the cell style.
     * @return The cell style.
     */
    public HSSFCellStyle getHSSFCellStyle() {
      return style;
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final HSSFCellStyle 	style;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private HSSFCellStyle 			defaultCellStyle;
  private final Set<CellStyleWrapper> 		cellStyles;
}
