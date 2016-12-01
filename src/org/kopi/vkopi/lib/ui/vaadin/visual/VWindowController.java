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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import org.kopi.vkopi.lib.preview.VPreviewWindow;
import org.kopi.vkopi.lib.ui.vaadin.addons.PopupWindow;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VHelpViewer;
import org.kopi.vkopi.lib.visual.VMenuTree;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VWindow;
import org.kopi.vkopi.lib.visual.WindowBuilder;
import org.kopi.vkopi.lib.visual.WindowController;

/**
 * The <code>VWindowController</code> is the vaadin implementation
 * of the {@link WindowController} specifications.
 */
@SuppressWarnings("serial")
public class VWindowController extends WindowController {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean doModal(VWindow model) {
    try {
      ModalViewRunner		viewStarter;

      viewStarter = new ModalViewRunner(model);
      BackgroundThreadHandler.startAndWait(viewStarter, model);

      return (viewStarter.getView() == null) ? false : viewStarter.getView().getReturnCode() == VWindow.CDE_VALIDATE;
    } finally {
      // This is a turn around to kill delayed wait dialog displayed in modal windows
      model.unsetWaitInfo();
    }
  }

  @Override
  public void doNotModal(VWindow model) {
    WindowBuilder		builder;
    
    builder = getWindowBuilder(model);
    if (builder != null) {
      try {
	DWindow			view;
	VApplication		application;

	view = (DWindow) builder.createWindow(model);
	view.run();
	application = (VApplication) ApplicationContext.getApplicationContext().getApplication();
	if (application != null) {
	  if (model instanceof VPreviewWindow
	      || model instanceof VHelpViewer
	      || model instanceof VMenuTree)
	  {
	    showNotModalPopupWindow(application, view, model.getTitle());
	  } else {
	    application.addWindow(view);
	  }
	}
      } catch (VException e) {
        throw new VRuntimeException(e.getMessage(), e);
      }
    }
  }
  
  /**
   * Shows a modal window in a popup view. This will handle
   * a window view not in a tabsheet but in a non modal
   * popup window.
   * @param application The application instance.
   * @param view The window view.
   * @param title The window title.
   */
  protected void showNotModalPopupWindow(final VApplication application,
                                         DWindow view,
                                         String title)
  {
    if (application == null) {
      return;
    }
    
    final PopupWindow			popup;
	  
    popup = new PopupWindow();
    popup.setModal(false);
    popup.setContent(view);
    popup.setCaption(title); // put popup title
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	application.attachComponent(popup);
	application.push(); // a push is needed to see the popup.
      }
    });
  }

  //---------------------------------------------------
  // MODAL VIEW STARTER
  //---------------------------------------------------

  /**
   * A modal view runner background task.
   */
  /*package*/ class ModalViewRunner implements Runnable {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ModalViewRunner</code> runner.
     * @param model The window model.
     */
    public ModalViewRunner(VWindow model) {
      this.model = model;
    }

    //---------------------------------------
    // IMPLEMENTATION
    //---------------------------------------
    
    @Override
    public void run() {
      WindowBuilder		builder;
      
      builder = getWindowBuilder(model);
      if (builder != null) {
	
	try {
	  VApplication		application;
	      
	  view = (DWindow) builder.createWindow(model);
	  view.run();
	  application = (VApplication) ApplicationContext.getApplicationContext().getApplication();
	  if (application != null) {
	    PopupWindow		popup;
		  
	    popup = new PopupWindow();
	    popup.setModal(true);
	    popup.setContent(view);
	    popup.setCaption(model.getTitle()); // put popup title
	    application.attachComponent(popup);
	    application.push(); // a push is needed to see the popup.
	  }
	} catch (VException e) {
	  throw new VRuntimeException(e.getMessage(), e);
	}
      }
    }

    /**
     * Returns the window view.
     * @return The window view.
     */
    public DWindow getView() {
      return view;
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private DWindow       		view;
    private final VWindow       	model; 
  }
}
