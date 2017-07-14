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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.NoLayout;

/**
 * The welcome screen shared state.
 */
@SuppressWarnings("serial")
public class WelcomeViewState extends AbstractComponentState {

  /**
   * The application locale
   */
  @NoLayout
  public String                         locale = "";
  
  /**
   * The supported languages.
   */
  @NoLayout
  public Map<String, String>            languages = new HashMap<String, String>();
  
  /**
   * A list of a font to calculate their metrics.
   * The value of the map is the text to be used with the font.
   */
  @NoLayout
  public List<FontMetricsRequest>       fontMetricsRequests = new ArrayList<FontMetricsRequest>();
  
  /**
   * The logo link
   */
  @NoLayout
  public String                         href;
  
  /**
   * A font metrics request
   */
  public static class FontMetricsRequest implements Serializable {
    
    public FontMetricsRequest() {}
    
    public FontMetricsRequest(String fontFamily, int fontSize, String text) {
      this.fontFamily = fontFamily;
      this.fontSize = fontSize;
      this.text = text;
    }
    
    public String               fontFamily;
    public int                  fontSize;
    public String               text;
  }
  
  /**
   * A font metrics response.
   */
  public static class FontMetricsResponse implements Serializable {
    
    public FontMetricsResponse() {}
    
    public FontMetricsResponse(String fontFamily, int fontSize, String text, int width, int height) {
      this.fontFamily = fontFamily;
      this.fontSize = fontSize;
      this.text = text;
      this.width = width;
      this.height = height;
    }
    
    public String               fontFamily;
    public int                  fontSize;
    public int                  width;
    public String               text;
    public int                  height;
  }
}
