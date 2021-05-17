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

package org.kopi.vkopi.lib.ui.vaadin.report;

import java.awt.Color;

import org.kopi.vkopi.lib.report.ColumnStyle;
import org.kopi.vkopi.lib.report.Parameters;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.vaadin.cssinject.CSSInject;

import com.vaadin.ui.UI;

/**
 * Some utilities for columns styles generation.
 */
public class StyleGenerator {

  //---------------------------------------------------
  // STATIC METHODS
  //---------------------------------------------------
  
  /**
   * Returns the {@link CSSStyle} for a given target {@link UI}.
   * @param target The target {@link UI}.
   * @param parameters The styles parameters.
   * @param columnStyle The {@link ColumnStyle}.
   * @param column The column index.
   * @param align The column alignment.
   * @param separator Is it a separator column ? 
   * @return The created {@link CSSStyle}.
   */
  public static CSSStyle getStyle(UI target,
                                  Parameters parameters,
                                  ColumnStyle columnStyle,
                                  int column,
                                  String align,
                                  boolean separator)
  {
    return new CSSStyle(target, parameters, columnStyle, column, align, separator);
  }

  /**
   * Returns the corresponding CSS color of a given {@link Color}.
   * @param color The AWT color.
   * @return The CSS color.
   */
  protected static String getCSSColor(Color color) {
    return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ") !important ;";
  }
	  
  //---------------------------------------------------
  // CSS STYLE
  //---------------------------------------------------

  /**
   * The <code>CSSStyle</code> is a CSS style model.
   */
  public static class CSSStyle {
	    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------

    /**
     * Creates a new <code>CSSStyle</code> instance.
     * @param target The target {@link UI}.
     * @param parameters The styles parameters.
     * @param columnStyle The {@link ColumnStyle}.
     * @param column The column index.
     * @param align The column alignment.
     * @param separator Is it a separator.
     */
    public CSSStyle(final UI target,
                    final Parameters parameters,
                    final ColumnStyle columnStyle,
                    final int column,
                    final String align,
                    final boolean separator)
    {
      this.column = column;
      this.separator = separator; 
      this.parameters = parameters;
      this.align = align;
      this.columnStyle = columnStyle;
      this.background = columnStyle.getBackground();
      this.foreground = columnStyle.getForeground();
      this.fontSize = parameters.getFont().getSize();
      this.fontFamily = parameters.getFont().getName();
      setItalic(parameters.getFont().isItalic());
      setBold(parameters.getFont().isBold());
      
      BackgroundThreadHandler.access(new Runnable() {
	
        @Override
        public void run() {
          if (separator) {
          style = new CSSInject(target);
          style.setStyles(".v-table-cell-content-separator{"
            + "background-color: #FF0000 !important ;"
            + "}"
            );
          } else {
	    style = new CSSInject(target);
            setStyle();
          }
        }
      });
    }

    /**
     * Returns the style name.
     * @return The style name.
     */
    public String getName() {
      if (separator) {
	return "separator";      
      } else {
	return "level-" + level + "-column-" + column;
      }
    }

    /**
     * Returns the {@link CSSInject} encapsulated instance.
     * @return The {@link CSSInject} encapsulated instance.
     */
    public CSSInject getStyle() {
      return style;
    }
    
    /**
     * Updates the CSS style of a given {@link ColumnStyle}.
     * @param columnStyle The {@link ColumnStyle}.
     */
    public void updateStyle(ColumnStyle columnStyle) {   
      if (columnStyle.getBackground() != this.columnStyle.getBackground()) {
	background = columnStyle.getBackground();
      } else {
	background = parameters.getBackground(level);
      }
      
      if (columnStyle.getForeground() != this.columnStyle.getForeground()) {
	foreground = columnStyle.getForeground();
      } else {
	foreground = parameters.getForeground(level);
      }
      
      if (columnStyle.getFont().getSize() != this.columnStyle.getFont().getSize()) {
	fontSize = columnStyle.getFont().getSize();
      } else {
	fontSize = parameters.getFont().getSize();
      }
      
      if (columnStyle.getFont().getName() != this.columnStyle.getFont().getName()) {
	fontFamily = columnStyle.getFont().getName();
      } else {
	fontFamily = parameters.getFont().getName();
      }
      
      if (columnStyle.getFont().isBold() != this.columnStyle.getFont().isBold()) {
	setBold(columnStyle.getFont().isBold());
      } else {
	setBold(parameters.getFont().isBold());
      }
      
      if (columnStyle.getFont().isItalic() != this.columnStyle.getFont().isItalic()) {
	setItalic(columnStyle.getFont().isItalic());
      } else {
	setItalic(parameters.getFont().isItalic());
      }
      
      BackgroundThreadHandler.access(new Runnable() {
	
        @Override
        public void run() {
          setStyle();
        }
      });
    }
    
    /**
     * Returns the encapsulated CSS style.
     */
    public void setStyle() {
      style.setStyles(".v-table-cell-content-" + getName() + "{"
        + "background-color: " + getCSSColor(background)
        + "color: " + getCSSColor(foreground)
        + "font-size: " + fontSize + " !important;"
        + "font-family: " + fontFamily + " !important;"
        + "text-align: " + align + " !important;"
        + "font-style: " + fontStyle + " !important;"
        + "font-weight: " +fontWeight + " !important;"
        + "}"
        + ".v-table-cell-content-" + getName() + " .v-table-cell-wrapper {"
        + "text-align: " + align + " !important;"
        + "}"
      );
    }
    
    /**
     * Sets the italic style.
     * @param isItalic The italic style.
     */
    public void setItalic(boolean isItalic) {
      if (isItalic) {
        this.fontStyle = "italic";
      } else {
	this.fontStyle = "normal";
      }
    }
    
    /**
     * Sets the bold style.
     * @param isBold The bold style.
     */
    public void setBold(boolean isBold) {
      if (isBold) {
        this.fontWeight = "bold";
      } else {
	this.fontWeight = "normal";
      }
    }

    /**
     * Sets the column level.
     * @param level The column level.
     */
    public void setLevel(int level) {
      this.level = level;
      this.background = parameters.getBackground(level);
      this.foreground = parameters.getForeground(level);
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private int			level;
    private final int			column;
    private CSSInject			style;
    private final boolean               separator;
    private ColumnStyle                 columnStyle;
    private Parameters                  parameters;
    private String                      align;
    private int                         fontSize;
    private String                      fontFamily;
    private String                      fontStyle;
    private String                      fontWeight;
    private Color                       background;
    private Color                       foreground;
  }
}
