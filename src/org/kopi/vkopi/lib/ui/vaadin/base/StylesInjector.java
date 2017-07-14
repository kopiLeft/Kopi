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

package org.kopi.vkopi.lib.ui.vaadin.base;

import java.util.HashMap;
import java.util.Map;

import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.visual.VColor;

import com.vaadin.server.Page;

/**
 * A centralized way to inject styles in the browser page.
 * TODO : use this class to handle report styles.
 */
public class StylesInjector {

  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  
  public StylesInjector() {
    this.styles = new HashMap<Style, String>();
  }

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  
  /**
   * Creates (if needed) and inject a CSS style characterized by its align, foreground color
   * and background color.
   * 
   * The styles are generated only when needed. if a style does not exists in the styles map,
   * a new style is generated and injected to the current browser page. Otherwise, an existing style
   * will be used and its name will be returned.
   * 
   * We note that two styles are equals only if they have the same align,
   * foreground color and background color.
   * 
   * @param align The alignment of the style.
   * @param foreground The foreground color of the style.
   * @param background The background color of the style.
   * @return
   */
  public String createAndInjectStyle(int align, VColor foreground, VColor background) {
    Style               style;
    
    style = new Style(++injectedStyleId, align, foreground, background);
    if (!styles.containsKey(style)) {
      styles.put(style, style.getName());
      style.inject();
    }

    return styles.get(style);
  }
  
  // --------------------------------------------------
  // INNER CLASSES
  // --------------------------------------------------
  
  /**
   * A style is represented by its align, foreground color and background color.
   */
  /*package*/ class Style {
    
    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------
    
    public Style(int id,
                 int align,
                 VColor foreground,
                 VColor background)
    {
      this.id = id;
      this.align = align;
      this.foreground = foreground;
      this.background = background;
    }
    
    // --------------------------------------------------
    // IMPLEMENTATION
    // --------------------------------------------------
    
    /**
     * Apply this style to current browser page.
     * @param selector The CSS selector to be used to inject the style in the browser page
     */
    public void inject() {
      // apply only when needed.
      if (align != VConstants.ALG_LEFT || foreground != null || background != null) {
        BackgroundThreadHandler.access(new Runnable() {

          @Override
          public void run() {
            Page.getCurrent().getStyles().add(toCSS());
          }
        });
      }
    }
    
    /**
     * returns the style name
     * @return
     */
    public String getName() {
      return "injected-style" + "-" + id;
    }
    
    /**
     * Returns the equivalent CSS style of this style object.
     * @return The equivalent CSS style of this style object.
     */
    public String toCSS() {
      StringBuffer              css;
      
      css = new StringBuffer("." + getName() +  "{");
      if (align != VConstants.ALG_LEFT) {
        css.append("text-align: " + getCSSAlign() + " !important;");
      }
      if (background != null) {
        css.append("background-color: " + Utils.toString(background) + " !important;");
      }
      if (foreground != null) {
        css.append("color: " + Utils.toString(foreground) + " !important;");
      }
      css.append("}");
      
      return css.toString();
    }
    
    /**
     * Return the CSS align of the alignment constants.
     * @return The CSS align of the alignment constants.
     */
    protected String getCSSAlign() {
      switch (align) {
      case VConstants.ALG_LEFT:
        return "left";
      case VConstants.ALG_RIGHT:
        return "right";
      case VConstants.ALG_CENTER:
        return "center";
      default:
        return "left"; // cell are left aligned by default;
      }
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Style) {
        return align == ((Style)obj).align
          && Utils.equals(foreground, ((Style)obj).foreground)
          && Utils.equals(background, ((Style)obj).background);
      } else {
        return super.equals(obj);
      }
    }
    
    @Override
    public int hashCode() {
      return align + (foreground == null ? 0 : foreground.hashCode())
        + (background == null ? 0 : background.hashCode());
    }
    
    // --------------------------------------------------
    // DATA MEMBERS
    // --------------------------------------------------
    
    private final int           id;
    private final int           align;
    private final VColor        foreground;
    private final VColor        background;
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  /*
   * Key is the style instance and the object is the style name
   */
  private final Map<Style, String>              styles;
  private int                                   injectedStyleId;
}
