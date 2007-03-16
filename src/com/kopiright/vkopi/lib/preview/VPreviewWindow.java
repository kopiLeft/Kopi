/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.preview;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.event.EventListenerList;

import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.Constants;
import com.kopiright.vkopi.lib.visual.DWindow;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.UIBuilder;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.WindowController;

/**
 * A special window that display an html help
 */
public class VPreviewWindow extends VWindow {

  static {
    WindowController.getWindowController().registerUIBuilder(Constants.MDL_PREVIEW, new UIBuilder() {
        public DWindow createView(VWindow model) {
          return new DPreviewWindow((VPreviewWindow) model);
        }
      });
  }

  public int getType() {
    return Constants.MDL_PREVIEW;
  }

  /**
   * Construct a new Editor
   */
  public VPreviewWindow() {
    setTitle("Preview");
    setActors(new SActor[] {
      new SActor("File",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "Close",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "quit",
                 KeyEvent.VK_ESCAPE,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PageFirst",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "pageFirst",
                 KeyEvent.VK_HOME,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PageLeft",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "pageLeft",
                 KeyEvent.VK_PAGE_UP,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PageRight",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "pageRight",
                 KeyEvent.VK_PAGE_DOWN,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PageLast",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "pageLast",
                 KeyEvent.VK_END,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PreviewFit",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "zoomoptimal",
                 KeyEvent.VK_F5,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PreviewFitWidth",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "zoomwidth",
                 KeyEvent.VK_F8,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PreviewFitHeight",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "zoomheight",
                 KeyEvent.VK_F9,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PreviewPlus",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "zoomplus",
                 KeyEvent.VK_F6,
                 0),
      new SActor("Action",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "PreviewMinus",
                 PREVIEW_LOCALIZATION_RESOURCE,
                 "zoomminus",
                 KeyEvent.VK_F7,
                 0)

    });

    // localize the preview using the default locale
    localize(Locale.getDefault());
    
    getActor(CMD_QUIT).setNumber(CMD_QUIT);
    getActor(CMD_FIRST).setNumber(CMD_FIRST);
    getActor(CMD_LEFT).setNumber(CMD_LEFT);
    getActor(CMD_RIGHT).setNumber(CMD_RIGHT);
    getActor(CMD_LAST).setNumber(CMD_LAST);
    getActor(CMD_ZOOM_FIT).setNumber(CMD_ZOOM_FIT);
    getActor(CMD_ZOOM_FIT_W).setNumber(CMD_ZOOM_FIT_W);
    getActor(CMD_ZOOM_FIT_H).setNumber(CMD_ZOOM_FIT_H);
    getActor(CMD_ZOOM_PLUS).setNumber(CMD_ZOOM_PLUS);
    getActor(CMD_ZOOM_MINUS).setNumber(CMD_ZOOM_MINUS);
    previewListener = new EventListenerList();
  }

  /**
   * The user want to show an help
   */
  public void preview(PrintJob printJob,
                      String command)
    throws VException, IOException
  {
    File        tempFile = Utils.getTempFile("PREVIEW", "JPG");

    this.printJob = printJob;
    this.numberOfPages = printJob.getNumberOfPages();
    this.printFile = printJob.getDataFile();
    this.imageFile = tempFile.getPath();
    this.imageFile = imageFile.substring(0, imageFile.lastIndexOf('.'));
    this.height = printJob.getHeight();
    this.width = printJob.getWidth();
    this.command = command;

    createImagesFromPostscript();
    currentPage = 1;
    setActorEnabled(CMD_QUIT, true);
    setActorEnabled(CMD_FIRST, currentPage > 1);
    setActorEnabled(CMD_LEFT, currentPage > 1);
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages);
    setActorEnabled(CMD_LAST, currentPage < numberOfPages);
    setActorEnabled(CMD_ZOOM_FIT, true);
    setActorEnabled(CMD_ZOOM_FIT_H, true);
    setActorEnabled(CMD_ZOOM_FIT_W, true);
    setActorEnabled(CMD_ZOOM_PLUS, true);
    setActorEnabled(CMD_ZOOM_MINUS, true);

    doNotModal();
  }

  private void createImagesFromPostscript() {
    try {
      int       resolution;
      Process   p;

      resolution = (int) ((72f * this.height)/printJob.getHeight());
      p = Runtime.getRuntime().exec(command + 
                                    " -q" + 
                                    " -sOutputFile=" + imageFile + "%d.JPG" + 
                                    " -sDEVICE=jpeg" +
                                    " -r" + resolution + "x" + resolution + 
                                    " -g" + this.width + "x" + this.height +
                                    " -dNOPAUSE" + 
                                    " " + printFile + 
                                    " -c quit ");
      p.waitFor();
    } catch (Exception e) {
      fatalError(this, "VPreviewWindow.preview(File ...)", e);
    }
  }

  // ---------------------------------------------------------------------
  // Zoom
  // ---------------------------------------------------------------------

  public void zoom(float ratio) {
    if (Math.min(height, width) * ratio < 50) {
      ratio = 50 / Math.min(height, width);
    }
    if (Math.max(height, width) * ratio > 3000) {
      ratio = 3000 / Math.max(height, width);
    }

    width = (int)(width * ratio);
    height = (int)(height * ratio);

    createImagesFromPostscript();
    fireZoomChanged();
  }

  // ---------------------------------------------------------------------
  // ACCESSORS
  // ---------------------------------------------------------------------

  /**
   * Performs the appropriate action.
   *
   * @param	actor		the number of the actor.
   */
  public void executeVoidTrigger(int key) throws VException {
    switch (key) {
    case CMD_QUIT:
      getDisplay().closeWindow();
      break;
    case CMD_FIRST:
      setWaitInfo(VlibProperties.getString("WAIT"));
      currentPage = 1;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_LEFT:
      setWaitInfo(VlibProperties.getString("WAIT"));
      currentPage -= 1;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_RIGHT:
      setWaitInfo(VlibProperties.getString("WAIT"));
      currentPage += 1;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_LAST:
      setWaitInfo(VlibProperties.getString("WAIT"));
      currentPage = numberOfPages;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_PLUS:
      setWaitInfo(VlibProperties.getString("WAIT"));
      zoom(DEF_ZOOM_RATIO);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_MINUS:
      setWaitInfo(VlibProperties.getString("WAIT"));
      zoom(1/DEF_ZOOM_RATIO);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(VlibProperties.getString("WAIT"));
      fireZoomFit(PreviewListener.FIT_BOTH);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT_H:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(VlibProperties.getString("WAIT"));
      fireZoomFit(PreviewListener.FIT_HEIGHT);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT_W:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(VlibProperties.getString("WAIT"));
      fireZoomFit(PreviewListener.FIT_WIDTH);
      unsetWaitInfo();
      break;
     }
    setMenu();
  }

  /**
   * Goto the specified page.
   *
   * @param     posno           the page position number.
   */
  public void gotoPosition(int posno) {
    setWaitInfo(VlibProperties.getString("WAIT"));
    currentPage = posno;
    firePageChanged(currentPage);
    unsetWaitInfo();
    setMenu();
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  
  /**
   * Localize this menu tree
   * 
   * @param     locale  the locale to use
   */
  public void localize(Locale locale) {
    LocalizationManager         manager;
      
    manager = new LocalizationManager(locale, Application.getDefaultLocale());
    
    // localizes the actors in VWindow
    super.localizeActors(manager);
    
    manager = null;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  public void addPreviewListener(PreviewListener bl) {
    previewListener.add(PreviewListener.class, bl);
  }
  public void removePreviewListener(PreviewListener bl) {
    previewListener.remove(PreviewListener.class, bl);
  }

  protected void firePageChanged(int current) {
    Object[]            listeners = previewListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== PreviewListener.class) {
        ((PreviewListener)listeners[i+1]).pageChanged(current);
      }
    }
  }
  protected void fireZoomChanged() {
    Object[]            listeners = previewListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== PreviewListener.class) {
        ((PreviewListener)listeners[i+1]).zoomChanged();
      }
    }
  }
  protected void fireZoomFit(int type) {
    Object[]            listeners = previewListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== PreviewListener.class) {
        ((PreviewListener)listeners[i+1]).zoomFit(type);
      }
    }
  }

  private void setMenu() {
    setActorEnabled(CMD_FIRST, currentPage > 1);
    setActorEnabled(CMD_LEFT, currentPage > 1);
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages);
    setActorEnabled(CMD_LAST, currentPage < numberOfPages);
  }

  public String getPreviewFileName(int current) {
    return imageFile + current + ".JPG";
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public String getTitle() {
    return title;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }

  protected PrintJob getPrintJob() {
      return printJob;  
  }


  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private int                   currentPage;
  private int                   numberOfPages;
  private String                command;
  private PrintJob              printJob;
  private File                  printFile;
  private String                imageFile;
  private int                   height;
  private int                   width;
  private EventListenerList     previewListener;


  private static final float    DEF_ZOOM_RATIO  = 1.30f;
  private static final String   PREVIEW_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/Preview";

  // the following commands *MUST* be in the same order than 
  // in 'actors' field set in the contructor of the current class.
  protected static final int    CMD_QUIT        =  0;
  protected static final int    CMD_FIRST       =  1;
  protected static final int    CMD_LEFT        =  2;
  protected static final int    CMD_RIGHT       =  3;
  protected static final int    CMD_LAST        =  4;
  protected static final int    CMD_ZOOM_FIT    =  5;
  protected static final int    CMD_ZOOM_FIT_W  =  6;
  protected static final int    CMD_ZOOM_FIT_H  =  7;
  protected static final int    CMD_ZOOM_PLUS   =  8;
  protected static final int    CMD_ZOOM_MINUS  =  9;
}
