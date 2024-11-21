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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import org.kopi.vkopi.lib.visual.VColor;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.UI;

import javax.servlet.http.HttpServletRequest;

/**
 * Some vaadin version utilities to obtain images and resources.
 */
public class Utils extends org.kopi.vkopi.lib.base.Utils {

  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Returns image from theme
   * @param img Must be an image from resource theme path separator is "/"
   * @return An Image or null if not found.
   */
  public static Image getImage(String image) {
    Image	img = cache.get(image);
    
    if (img == null) {
      img = getImageImpl(image);
      cache.put(image, img);
    }

    return img;
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An Image or null if not found.
   */
  private static Image getImageImpl(String img) {
    Image icon = getDefaultImage(img);
	    
    if (icon == null) {
      icon =  getKopiResourceImage(img);
    }
    if (icon == null) {
      icon = getApplicationImage(img);
    }

    if (icon == null) {
      System.err.println("Utils ==> cant load: " + img);
      return UKN_IMAGE;
    }

    return icon;
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource directory path separator is "/"
   * @return An imageIcon or null if not found
   */
  public static Image getDefaultImage(String img) {
    return getImageFromResource(VAADIN_RESOURCE_DIR, img);
  }

  /**
   * Returns image from theme.
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  public static Image getApplicationImage(String img) {
    return getImageFromResource(APPLICATION_DIR, img);
  }

  /**
   * Returns an image from kopi resources. 
   * @param img Must be an image from resource application directory
   * path separator is "/"
   * @return An Image or null if not found
   */
  public static Image getKopiResourceImage(String img) {
    return getImageFromResource(RESOURCE_DIR, img);
  }

  /**
   * Return image from resources or null if not found.
   * @param directory The image directory.
   * @param name The image name.
   * @return An Image or null if not found
   */
  public static Image getImageFromResource(String directory, String name) {
    if (Utils.class.getClassLoader().getResource(directory + "/" + name) != null) {
      return new Image(new ClassResource("/" + directory + "/" + name));
    }
	    
    return null;
  }
  
  /**
   * Returns the corresponding CSS color of a given {@link VColor}.
   * @param color The color model.
   * @return The CSS color.
   */
  public static String getCSSColor(VColor color) {
    if (color == null) {
      return "inherit;";
    } else {
      return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ") ;";
    }
  }
  
  /**
   * Returns the string representation of the given {@link VColor}.
   * @param color The color model.
   * @return The equivalent String color or empty string if the color is {@code null}.
   */
  public static String toString(VColor color) {
    if (color == null) {
      return "";
    } else {
      return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }
  }

  /**
   * Builds a list of identifiers. 
   * @param size The list size.
   * @return The resulting list.
   */
  public static List<Integer> buildIdList(int size) {
    List<Integer>       list = new ArrayList<Integer>(size);
    
    for (int i = 0; i < size; i++) {
      list.add(i);
    }
    
    return list;
  }
  
  /**
   * Creates a HTML tooltip that wraps a string content. 
   * @param content The content (String or html).
   * @return The decoredted tooltip
   */
  public static String createTooltip(String content) {
    return "<div class=\"info\"><i class=\"fa fa-sort-asc\" aria-hidden=\"true\"></i>" + content + "</div>";
  }
  
  /**
   * Returns the equivalent font awesome icon from the given icon name.
   * @param iconName The model icon name.
   * @return The font awesome icon.
   */
  public static String getFontAwesomeIcon(String iconName) {
    return pngToFontAwesomeMap.get(iconName);
  }
  
  /**
   * Returns true if the given two objects are equals.
   * @param o1 The first object.
   * @param o2 The second object.
   * @return true if the given two objects are equals.
   */
  public static boolean equals(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    } else if (o2 == null) {
      return false;
    } else {
      return o1.equals(o2);
    }
  }

  public static boolean isWebApplication() {
    boolean isWebApp = false;
    try {
      VaadinRequest request = VaadinService.getCurrentRequest();
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String clientIpAddress = httpRequest.getRemoteAddr();
      System.out.println("Client IP Address: " + clientIpAddress);
      isWebApp = request != null;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Request is defined outside common");
    }
    return isWebApp;
  }
  
  /**
   * Returns the complete theme resource URL of the given resource path.
   * @param resourcePath The theme complete resource path.
   * @return The URL of the given theme resource.
   */
  public static String getThemeResourceURL(String resourcePath) {
    return "./VAADIN/themes/" + UI.getCurrent().getTheme() + "/" + resourcePath;
  }
	  
  // --------------------------------------------------
  // PRIVATE DATA
  // --------------------------------------------------

  private static final String			VAADIN_RESOURCE_DIR = "org/kopi/vkopi/lib/ui/vaadin/resource";
  private static final String			THEME_DIR = "resource";
  private static final String			APPLICATION_DIR = "resources";
  private static final String			RESOURCE_DIR	= "org/kopi/vkopi/lib/resource";
  public  static final Image			UKN_IMAGE = new Image(new ThemeResource(THEME_DIR + "/" + "unknown.png"));
	  
  private static Hashtable<String, Image> 	cache = new Hashtable<String, Image>();
  private static Map<String, String>            pngToFontAwesomeMap;
  
  static {
    pngToFontAwesomeMap = new HashMap<String, String>();
    pngToFontAwesomeMap.put("all", "hand-paper-o");
    pngToFontAwesomeMap.put("block", "ban");
    pngToFontAwesomeMap.put("border", "cog");
    pngToFontAwesomeMap.put("bread_crumb_separator", "angle-double-right");
    pngToFontAwesomeMap.put("break", "times-circle");
    pngToFontAwesomeMap.put("calendar", "calendar-o");
    pngToFontAwesomeMap.put("collapsed", "folder-o");
    pngToFontAwesomeMap.put("collapsed_p", "folder-o");
    pngToFontAwesomeMap.put("copy", "files-o");
    pngToFontAwesomeMap.put("delete", "trash-o");
    pngToFontAwesomeMap.put("desk", "desktop");
    pngToFontAwesomeMap.put("detail", "search-plus");
    pngToFontAwesomeMap.put("detail_view", "search");
    pngToFontAwesomeMap.put("down", "angle-double-down");
    pngToFontAwesomeMap.put("duke", "expeditedssl");
    pngToFontAwesomeMap.put("edit", "pencil-square-o");
    pngToFontAwesomeMap.put("expanded_a", "folder-o");
    pngToFontAwesomeMap.put("expanded", "folder-o");
    pngToFontAwesomeMap.put("expanded_p", "folder-o");
    pngToFontAwesomeMap.put("exportCsv", "file-text-o");
    pngToFontAwesomeMap.put("exportPdf", "file-pdf-o");
    pngToFontAwesomeMap.put("exportXlsx", "file-excel-o");
    pngToFontAwesomeMap.put("foldColumn", "folder-o");
    pngToFontAwesomeMap.put("fold", "folder-o");
    pngToFontAwesomeMap.put("formula", "calculator");
    pngToFontAwesomeMap.put("help", "question-circle-o");
    pngToFontAwesomeMap.put("home", "home");
    pngToFontAwesomeMap.put("insertline", "list-ol");
    pngToFontAwesomeMap.put("insert", "pencil-square-o");
    pngToFontAwesomeMap.put("list", "mouse-pointer");
    pngToFontAwesomeMap.put("loading", "spinner");
    pngToFontAwesomeMap.put("login_img", "coffee");
    pngToFontAwesomeMap.put("mail", "envelope-o");
    pngToFontAwesomeMap.put("menuquery", "file-text-o");
    pngToFontAwesomeMap.put("note", "sticky-note-o");
    pngToFontAwesomeMap.put("nothing", "file-o");
    pngToFontAwesomeMap.put("open", "file-text-o");
    pngToFontAwesomeMap.put("options", "cogs");
    pngToFontAwesomeMap.put("preview", "file-text-o");
    pngToFontAwesomeMap.put("print", "print");
    pngToFontAwesomeMap.put("quit", "power-off");
    pngToFontAwesomeMap.put("save", "floppy-o");
    pngToFontAwesomeMap.put("searchop", "search");
    pngToFontAwesomeMap.put("search", "search");
    pngToFontAwesomeMap.put("serialquery", "binoculars");
    pngToFontAwesomeMap.put("serviceoff", "toggle-off");
    pngToFontAwesomeMap.put("serviceon", "toggle-on");
    pngToFontAwesomeMap.put("store", "building-o");
    pngToFontAwesomeMap.put("suggest", "phone");
    pngToFontAwesomeMap.put("timeStamp", "clock-o");
    pngToFontAwesomeMap.put("tri", "sort-alpha-desc");
    pngToFontAwesomeMap.put("unfoldColumn", "folder-open-o");
    pngToFontAwesomeMap.put("unfold", "folder-open-o");
    pngToFontAwesomeMap.put("up", "angle-double-up");
    pngToFontAwesomeMap.put("add", "floppy-o");
    pngToFontAwesomeMap.put("align_center", "align-center");
    pngToFontAwesomeMap.put("align_justify", "align-justify");
    pngToFontAwesomeMap.put("align_left", "align-left");
    pngToFontAwesomeMap.put("align_right", "align-right");
    pngToFontAwesomeMap.put("apply", "cogs");
    pngToFontAwesomeMap.put("area_chart", "area-chart");
    pngToFontAwesomeMap.put("arrowfirst", "step-backward");
    pngToFontAwesomeMap.put("arrowlast", "step-forward");
    pngToFontAwesomeMap.put("arrowleft", "backward");
    pngToFontAwesomeMap.put("arrowright", "forward");
    pngToFontAwesomeMap.put("article", "file-text-o");
    pngToFontAwesomeMap.put("ask", "question-circle");
    pngToFontAwesomeMap.put("bar_chart", "bar-chart");
    pngToFontAwesomeMap.put("bkup3", "exclamation-triangle");
    pngToFontAwesomeMap.put("bkup", "exclamation-triangle");
    pngToFontAwesomeMap.put("block2", "undo");
    pngToFontAwesomeMap.put("board", "cogs");
    pngToFontAwesomeMap.put("bold", "bold");
    pngToFontAwesomeMap.put("bomb", "bomb");
    pngToFontAwesomeMap.put("bookmark", "bookmark");
    pngToFontAwesomeMap.put("boxarrow", "dropbox");
    pngToFontAwesomeMap.put("bw", "step-backward");
    pngToFontAwesomeMap.put("calculate", "calculator");
    pngToFontAwesomeMap.put("cfolder", "folder");
    pngToFontAwesomeMap.put("chart_view", "bar-chart");
    pngToFontAwesomeMap.put("checkbox", "square-o");
    pngToFontAwesomeMap.put("clip", "paperclip");
    pngToFontAwesomeMap.put("collapsedb", "long-arrow-right");
    pngToFontAwesomeMap.put("collapsed_f", "folder");
    pngToFontAwesomeMap.put("collapsed_t", "folder-o");
    pngToFontAwesomeMap.put("column_chart", "bar-chart");
    pngToFontAwesomeMap.put("combo", "lightbulb-o");
    pngToFontAwesomeMap.put("config", "wrench");
    pngToFontAwesomeMap.put("convert", "exchange");
    pngToFontAwesomeMap.put("cut", "scissors");
    pngToFontAwesomeMap.put("deleteline", "list-ol");
    pngToFontAwesomeMap.put("done", "check-square-o");
    pngToFontAwesomeMap.put("error", "minus-circle");
    pngToFontAwesomeMap.put("expandedb", "long-arrow-down");
    pngToFontAwesomeMap.put("expanded_f", "folder-open");
    pngToFontAwesomeMap.put("expanded_s", "folder-open");
    pngToFontAwesomeMap.put("expanded_t", "folder-open");
    pngToFontAwesomeMap.put("export", "cog");
    pngToFontAwesomeMap.put("fax", "fax");
    pngToFontAwesomeMap.put("fw", "step-forward");
    pngToFontAwesomeMap.put("gifIcon", "file-image-o");
    pngToFontAwesomeMap.put("green", "map-o");
    pngToFontAwesomeMap.put("guide", "map-signs");
    pngToFontAwesomeMap.put("ident", "long-arrow-right");
    pngToFontAwesomeMap.put("index", "book");
    pngToFontAwesomeMap.put("interrupt", "stop-circle-o");
    pngToFontAwesomeMap.put("italic", "italic");
    pngToFontAwesomeMap.put("jpgIcon", "picture-o");
    pngToFontAwesomeMap.put("launch", "long-arrow-right");
    pngToFontAwesomeMap.put("line_chart", "line-chart");
    pngToFontAwesomeMap.put("lock", "lock");
    pngToFontAwesomeMap.put("login", "user-circle");
    pngToFontAwesomeMap.put("moneycheck", "money");
    pngToFontAwesomeMap.put("money", "money");
    pngToFontAwesomeMap.put("notice", "lightbulb-o");
    pngToFontAwesomeMap.put("ofolder", "folder-open");
    pngToFontAwesomeMap.put("pageFirst", "step-backward");
    pngToFontAwesomeMap.put("pageLast", "step-forward");
    pngToFontAwesomeMap.put("pageLeft", "backward");
    pngToFontAwesomeMap.put("pageRight", "forward");
    pngToFontAwesomeMap.put("password", "lock");
    pngToFontAwesomeMap.put("paste", "clipboard");
    pngToFontAwesomeMap.put("phone", "phone-square");
    pngToFontAwesomeMap.put("pie_chart", "pie-chart");
    pngToFontAwesomeMap.put("printoptions", "wrench");
    pngToFontAwesomeMap.put("project", "cubes");
    pngToFontAwesomeMap.put("red", "map-o");
    pngToFontAwesomeMap.put("redo", "repeat");
    pngToFontAwesomeMap.put("refresh", "refresh");
    pngToFontAwesomeMap.put("reload", "refresh");
    pngToFontAwesomeMap.put("report", "table");
    pngToFontAwesomeMap.put("sec", "unlock");
    pngToFontAwesomeMap.put("selected", "long-arrow-right");
    pngToFontAwesomeMap.put("send", "paper-plane");
    pngToFontAwesomeMap.put("sort", "sort-numeric-asc");
    pngToFontAwesomeMap.put("split", "chain-broken");
    pngToFontAwesomeMap.put("standard", "pencil");
    pngToFontAwesomeMap.put("stick", "thumb-tack");
    pngToFontAwesomeMap.put("stop", "times-circle-o");
    pngToFontAwesomeMap.put("todo", "magic");
    pngToFontAwesomeMap.put("top", "book");
    pngToFontAwesomeMap.put("underline", "underline");
    pngToFontAwesomeMap.put("undo", "undo");
    pngToFontAwesomeMap.put("unident", "arrow-circle-left");
    pngToFontAwesomeMap.put("unstick", "thumb-tack");
    pngToFontAwesomeMap.put("update", "pencil-square-o");
    pngToFontAwesomeMap.put("users", "users");
    pngToFontAwesomeMap.put("utils", "cogs");
    pngToFontAwesomeMap.put("validate", "check");
    pngToFontAwesomeMap.put("wait", "clock-o");
    pngToFontAwesomeMap.put("warning", "exclamation-triangle");
    pngToFontAwesomeMap.put("window", "angle-right");
    pngToFontAwesomeMap.put("yellow", "map-o");
    pngToFontAwesomeMap.put("zoomheight", "search");
    pngToFontAwesomeMap.put("zoomminus", "search-minus");
    pngToFontAwesomeMap.put("zoomoptimal", "search");
    pngToFontAwesomeMap.put("zoomplus", "search-plus");
    pngToFontAwesomeMap.put("zoomwidth", "search");
  }
}
