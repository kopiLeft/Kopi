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

package org.kopi.vkopi.lib.report;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * Manage the CellStyle cache.
 * In order to not have the 4000 style limit, cell styles are cached base on their
 * hash codes within a {@link Set} and reused when it two or more cells have the same
 * style.
 * Use {@link #setCellStyle(Workbook, Cell, CellStyle)} for caching functions.
 */
@SuppressWarnings("serial")
public class CellStyleCacheManager implements Serializable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new Cell style cache manager.
   */
  public CellStyleCacheManager() {
    this.stylesCache = new HashMap<StyleKey, CellStyle>();
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Returns the cell style corresponding to the given alignment, data format and color.
   * If the style is not cached yet, it will be created and added to the cache.
   * @param exporter The excel exporter instance.
   * @param wb The workbook instance.
   * @param alignment The style alignment.
   * @param dataFormat The style data format.
   * @param color The style background color.
   * @return The style instance.
   */
  public CellStyle getStyle(PExport2Excel exporter,
                            Workbook wb,
                            short alignment,
                            short dataFormat,
                            Color color)
  {
    StyleKey            key;
    
    key = new StyleKey(alignment, dataFormat, color);
    if (!stylesCache.containsKey(key)) {
      CellStyle         style;
      
      style = wb.createCellStyle();
      style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
      style.setBorderBottom(CellStyle.BORDER_THIN);
      style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
      style.setBorderLeft(CellStyle.BORDER_THIN);
      style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
      style.setBorderRight(CellStyle.BORDER_THIN);
      style.setRightBorderColor(IndexedColors.BLACK.getIndex());
      style.setBorderTop(CellStyle.BORDER_THIN);
      style.setTopBorderColor(IndexedColors.BLACK.getIndex());
      style.setFillPattern(CellStyle.SOLID_FOREGROUND);
      style.setWrapText(true);
      style.setAlignment(alignment);
      if (dataFormat != -1) {
        style.setDataFormat(dataFormat);
      }
      if (style instanceof XSSFCellStyle) {
        ((XSSFCellStyle)style).setFillForegroundColor((XSSFColor) exporter.createFillForegroundColor(color));
      } else {
        style.setFillForegroundColor(((HSSFColor)exporter.createFillForegroundColor(color)).getIndex());
      }
      
      stylesCache.put(key, style);
    }
    
    return stylesCache.get(key);
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /*package*/ static class StyleKey {
    
    public StyleKey(short alignement, short dataFormat, Color color) {
      this.alignement = alignement;
      this.dataFormat = dataFormat;
      this.color = color;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof StyleKey) {
        return alignement == ((StyleKey)obj).alignement
          && dataFormat == ((StyleKey)obj).dataFormat
          && color.equals(((StyleKey)obj).color);
      }
      
      return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return alignement + dataFormat + color.hashCode();
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final short         alignement; 
    private final short         dataFormat;
    private final Color         color;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final Map<StyleKey, CellStyle>        stylesCache;
}
