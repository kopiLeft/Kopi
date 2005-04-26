/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.preview;

import java.awt.Insets;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.util.PrintInformation;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.visual.Constants;
import at.dms.vkopi.lib.visual.DWindow;
import at.dms.vkopi.lib.visual.KopiAction;
import at.dms.vkopi.lib.visual.SActor;
import at.dms.vkopi.lib.visual.UIBuilder;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VRuntimeException;
import at.dms.vkopi.lib.visual.VWindow;
import at.dms.vkopi.lib.visual.WindowController;

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
      new SActor(Message.getMessage("menu-file"),
		 Message.getMessage("close"),
		 "quit",
		 KeyEvent.VK_ESCAPE,
		 0,
		 Message.getMessage("help-close-help")),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-page-left"),
		   "pageLeft",
		   KeyEvent.VK_PAGE_UP,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-page-right"),
		   "pageRight",
		   KeyEvent.VK_PAGE_DOWN,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-preview-fit"),
		   "searchop",
		   KeyEvent.VK_F5,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-preview-fit-width"),
		   "zoomwidth",
		   KeyEvent.VK_F8,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-preview-fit-height"),
		   "zoomheight",
		   KeyEvent.VK_F9,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-preview-plus"),
		   "detail",
		   KeyEvent.VK_F6,
		   0,
		   null),
        new SActor(Message.getMessage("menu-action"),
		   Message.getMessage("item-preview-minus"),
		   "zoomminus",
		   KeyEvent.VK_F7,
		   0,
		   null)
	});
    getActor(CMD_QUIT).setNumber(CMD_QUIT);
    getActor(CMD_LEFT).setNumber(CMD_LEFT);
    getActor(CMD_RIGHT).setNumber(CMD_RIGHT);
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

    this.height = printJob.isLandscape() ? DEF_WIDTH : DEF_HEIGHT;
    this.width = printJob.isLandscape() ? DEF_HEIGHT : DEF_WIDTH;

    this.command = command;

    createImagesFromPostscript();
    currentPage = 1;
    setActorEnabled(CMD_QUIT, true);
    setActorEnabled(CMD_LEFT, currentPage > 1);
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages);
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
      p = Runtime.getRuntime().exec(command + " -q -sOutputFile=" + imageFile + "%d.JPG -sDEVICE=jpeggray " +
                                    "-r" + resolution + "x" + resolution + " -g" + this.width + "x" + this.height +
                                    " -dNOPAUSE " + printFile + " -c quit ");
      p.waitFor();
    } catch (Exception e) {
      fatalError(this, "VPreviewWindow.preview(File ...)", e);
    }
  }

  // ---------------------------------------------------------------------
  // Zoom
  // ---------------------------------------------------------------------

  public void zoom(float ratio) {
    width = (int)(width * ratio);
    height = (int)(height * ratio);

    // min (50)/max (3000) values of size

    if (printJob.isLandscape()) {
      if (height < 50) {
        height = 50; width = (int) (50 * 1.4145);
      } else if (width > 3000) {
        height = (int) (3000f / 1.4145); width = 3000;
      }
    } else {
      if (width < 50) {
        height = (int) (50 * 1.4145); width = 50;
      } else if (height > 3000) {
        height = 3000; width = (int) (3000f / 1.4145);
      }
    }

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
   * @return	true iff an action was found for the specified number
   */
  public void executeVoidTrigger(int key) throws VException {
    switch (key) {
    case CMD_QUIT:
      getDisplay().closeWindow();
      break;
    case CMD_LEFT:
      setWaitInfo(Message.getMessage("WAIT"));
      currentPage -= 1;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_RIGHT:
      setWaitInfo(Message.getMessage("WAIT"));
      currentPage += 1;
      firePageChanged(currentPage);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_PLUS:
      setWaitInfo(Message.getMessage("WAIT"));
      zoom(DEF_ZOOM_RATIO);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_MINUS:
      setWaitInfo(Message.getMessage("WAIT"));
      zoom(1/DEF_ZOOM_RATIO);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(Message.getMessage("WAIT"));
      fireZoomFit(PreviewListener.FIT_BOTH);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT_H:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(Message.getMessage("WAIT"));
      fireZoomFit(PreviewListener.FIT_HEIGHT);
      unsetWaitInfo();
      break;
    case CMD_ZOOM_FIT_W:
      // ask gui to calculate zoom
      // gui calls method zoom with the good value
      setWaitInfo(Message.getMessage("WAIT"));
      fireZoomFit(PreviewListener.FIT_WIDTH);
      unsetWaitInfo();
      break;
     }
    setMenu();
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
    setActorEnabled(CMD_LEFT, currentPage > 1);
    setActorEnabled(CMD_RIGHT, currentPage < numberOfPages);
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

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  //  protected JLabel	label;
  private int                   currentPage;
  private int                   numberOfPages;
  private String                command;
  private PrintJob              printJob;

  private File                  printFile;
  private String                imageFile;
  private int                   height;
  private int                   width;
  private EventListenerList     previewListener;

  private static final int	DEF_HEIGHT	= 842;
  private static final int	DEF_WIDTH	= 595;
  private static final float    DEF_ZOOM_RATIO  = 1.30f;

  protected static final int	CMD_QUIT	= 0;
  protected static final int	CMD_LEFT	= 1;
  protected static final int	CMD_RIGHT	= 2;
  protected static final int	CMD_ZOOM_FIT	= 3;
  protected static final int	CMD_ZOOM_FIT_W	= 4;
  protected static final int	CMD_ZOOM_FIT_H	= 5;
  protected static final int	CMD_ZOOM_PLUS	= 6;
  protected static final int	CMD_ZOOM_MINUS	= 7;
}
