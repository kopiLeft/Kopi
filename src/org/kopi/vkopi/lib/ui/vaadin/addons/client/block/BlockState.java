/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The Block shared state.
 */
@SuppressWarnings("serial")
public class BlockState extends AbstractComponentState {
  
  /**
   * Is animation enabled.
   */
  @NoLayout
  public boolean                                isAnimationEnabled;
  
  /**
   * The scroll page size.
   */
  @NoLayout
  public int                                    scrollPageSize;
  
  /**
   * The max scroll value.
   */
  @NoLayout
  public int                                    maxScrollValue;
  
  /**
   * Should we enable scroll bar ? 
   */
  @NoLayout
  public boolean                                enableScroll;
  
  /**
   * The scroll value
   */
  @NoLayout
  public int                                    scrollValue;
  
  /**
   * The block buffer size.
   */
  @NoLayout
  public int                                    bufferSize;
  
  /**
   * The block display size.
   */
  @NoLayout
  public int                                    displaySize;
  
  /**
   * No Move option
   */
  @NoLayout
  public boolean                                noMove;
  
  /**
   * No chart option
   */
  @NoLayout
  public boolean                                noChart;
  
  /**
   * The model active record to be communicated to the client side.
   */
  @NoLayout
  public int                                    activeRecord = -1;
  
  /**
   * The block sorted records.
   */
  @NoLayout
  public int[]                                  sortedRecords = new int[0];
  
  /**
   * The block records info changes buffer.
   */
  @NoLayout
  public List<RecordInfo>                       recordInfo = new Vector<RecordInfo>();
  
  /**
   * The block fields values per record.
   */
  @NoLayout
  public List<CachedValue>                      cachedValues = new Vector<CachedValue>();
  
  /**
   * The cached field colors per record.
   */
  @NoLayout
  public List<CachedColor>                      cachedColors = new Vector<CachedColor>();
  
  /**
   * A serializable record info structure to be communicated
   * to the client side using the shared state mechanism
   */
  public static class RecordInfo implements Serializable {
    
    /**
     * Needed by GWT compiler.
     */
    public RecordInfo() {}
    
    /**
     * Creates a new record info instance.
     * @param rec The record number.
     * @param value The record info value.
     */
    public RecordInfo(int rec, int value) {
      this.rec = rec;
      this.value = value;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof RecordInfo) {
        RecordInfo      other = (RecordInfo) obj;
        
        return rec == other.rec && value == other.value;
      } else {
        return super.equals(obj);
      }
    }
    
    @Override
    public int hashCode() {
      return rec + value;
    }
    
    /**
     * The record number
     */
    public int            rec;
    /**
     * The record info value
     */
    public int            value;
  }
  
  /**
   * A serializable cached value structure to be passed to the
   * client side using the shared state mechanism.
   */
  public static class CachedValue implements Serializable {
    
    /**
     * Needed by GWT compiler.
     */
    public CachedValue() {}
    
    /**
     * Creates a new cached values instance.
     * @param col The column index.
     * @param rec The record number.
     * @param value The cached value.
     */
    public CachedValue(int col, int rec, String value) {
      this.col = col;
      this.rec = rec;
      this.value = value == null ? "" : value;
    }
    
    @Override
    public int hashCode() {      
      return col + rec + value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof CachedValue) {
        CachedValue     other = (CachedValue) obj;

        return col == other.col && rec == other.rec && value.equals(other.value);
      } else {
        return super.equals(obj);
      }
    }

    /**
     * The column index.
     */
    public int                  col;
    /**
     * The record number.
     */
    public int                  rec;
    /**
     * The cached value.
     */
    public String               value;          
  }
  
  /**
   * A serializable cached color structure to be passed to the
   * client side using the shared state mechanism.
   */
  public static class CachedColor implements Serializable {
    
    /**
     * Needed by GWT compiler.
     */
    public CachedColor() {}
    
    /**
     * Creates a new cached color instance.
     * @param col The column index.
     * @param rec The record number.
     * @param foreground The foreground color.
     * @param background The background color.
     */
    public CachedColor(int col,
                       int rec,
                       String foreground,
                       String background)
    {
      this.col = col;
      this.rec = rec;
      this.foreground = foreground == null ? "" : foreground;
      this.background = background == null ? "" : background;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof CachedColor) {
        CachedColor     other = (CachedColor) obj;
        
        return col == other.col && rec == other.rec
          && foreground.equals(foreground) && background.equals(other.background);
      } else {
        return super.equals(obj);
      }
    }
    
    @Override
    public int hashCode() {
      return col + col + foreground.hashCode() + background.hashCode();
    }

    /**
     * The column index.
     */
    public int                  col;
    
    /**
     * The record number
     */
    public int                  rec;
    
    /**
     * The foreground color
     */
    public String               foreground;
    
    /**
     * The background color
     */
    public String                background;
  }
  
  /**
   * A color pair composed of a foreground and a background color.
   */
  public static class ColorPair implements Serializable {
    
    /**
     * default constructor needed by compiler.
     */
    public ColorPair() {}
    
    /**
     * Creates a new color pair instance.
     * @param foreground The foreground color.
     * @param background The background color.
     */
    public ColorPair(String foreground, String background) {
      this.foreground = foreground;
      this.background = background;
    }
    
    /**
     * The foreground color
     */
    public String               foreground;
    
    /**
     * The background color
     */
    public String                background;
  }
}
