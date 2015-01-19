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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.preview.VPreviewWindow;
import com.kopiright.vkopi.lib.report.VReport;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.report.DReport;
import com.kopiright.vkopi.lib.visual.UIFactory;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VHelpViewer;
import com.kopiright.vkopi.lib.visual.VMenuTree;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.WindowController;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * The <code>VWindowController</code> is the vaadin implementation
 * of the {@link WindowController} specifications.
 */
public class VWindowController extends WindowController {
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public boolean doModal(VWindow model) {
    ModalViewRunner	modalRunner = new ModalViewRunner(model); 
    
    modalRunner.run();
    synchronized (model) {
      try {
        model.wait();
      } catch (InterruptedException e1) {
	// wait interrupted
      } 
    }  
    return (modalRunner.getView() == null) ? false : modalRunner.getView().getReturnCode() == VWindow.CDE_VALIDATE; 
  }

  //!!! FIXME: Review all this.
  @Override
  public void doNotModal(final VWindow model) {
    final DWindow		view;
    final VApplication 		currentUi;
    
    currentUi = (VApplication) VApplicationContext.getApplicationContext().getApplication();
    view = (DWindow) UIFactory.getUIFactory().createView(model);
    
    try {
      view.run();
    } catch(VException e) {
      reportError(e);
    }
       
    UI.getCurrent().access(new Runnable() {
      
      @Override
      public void run() {
        if (model instanceof VMenuTree) {
          if (((VMenuTree)model).isSuperUser()) {
	    Window		window;

            window = new Window(model.getTitle());
            window.setModal(false);
            window.addStyleName(KopiTheme.WINDOW_OPAQUE);
            window.addStyleName(KopiTheme.WINDOW_ASSIGN_MODEL);
            window.setClosable(false);
            window.setResizable(false);
	    window.setWidth("30%");
	    window.setHeight("95%");
        
            view.setWidth("100%");
	    
            VerticalLayout windowLayout= new VerticalLayout();
            windowLayout.addComponent(view.getButtonPanel());
            windowLayout.addComponent(view);
	    
            window.setContent(windowLayout);
            currentUi.addWindow(window);
          } else {
	    currentUi.setMenuTree((DMenuTree) view);
          }
        } else if ((model instanceof VForm) || (model instanceof VReport)){
      
        //  model.getDBContext().
          VerticalLayout tab_Layout=new VerticalLayout();
          tab_Layout.setWidth("100%");
          tab_Layout.addComponent(view.getNotificationPanel());
          view.getNotificationPanel().addStyleName(KopiTheme.NOTIFICATION_TOP_STYLE); 
          
          if (model instanceof VForm) {
            tab_Layout.addComponent(DMenuTree.getBreadCrumb());
          } else {
	    tab_Layout.addComponent(new BreadCrumb(model.getTitle()));
	    tab_Layout.setHeight("100%");
          }
      
          tab_Layout.addComponent(view.getButtonPanel());   
          tab_Layout.addComponent(view); 
        
          VerticalLayout windowFooter= new VerticalLayout();
          windowFooter.addStyleName(KopiTheme.FOOTER_LAYOUT);
      
          windowFooter.addComponent(view.getFootPanel());
          tab_Layout.addComponent(windowFooter);   
          tab_Layout.addComponent(view.getWaitIndicator());
    	
          Tab tab;
      
          if (model instanceof VReport) {    
           ((DReport)view).setParentTab(currentUi.getTabsheet().getTab(currentUi.getTabsheet().getSelectedTab()));
	    tab = currentUi.getTabsheet().addTab(tab_Layout, model.getTitle(), (Resource) model.getSmallIcon(),
	                                         currentUi.getTabsheet().getTabPosition(((DReport)view).getParentTab())+1);
          } else {
	    tab = currentUi.getTabsheet().addTab(tab_Layout, model.getTitle(), (Resource) model.getSmallIcon(),1);
          }
      
          tab.setClosable(true);
          tab.setStyleName(KopiTheme.TAB_STYLE);
          tab.setEnabled(true);

          currentUi.getTabsheet().setSelectedTab(tab);
        } else if (model instanceof VHelpViewer) {
          Window		window;

          window = new Window(model.getTitle());
          window.setModal(false);
          window.addStyleName(KopiTheme.WINDOW_OPAQUE);
          window.addStyleName(KopiTheme.HELP_WINDOW);
          window.setClosable(true);
          window.setResizable(false);
          window.setWidth(600, Unit.PIXELS);
          window.setHeight(720, Unit.PIXELS);
      
          VerticalLayout windowLayout= new VerticalLayout();
          windowLayout.setWidth("100%");
          windowLayout.addComponent(view.getButtonPanel());
          windowLayout.addComponent(view);
          windowLayout.addComponent(view.getFootPanel());
      
          window.setContent(windowLayout);

          currentUi.addWindow(window);
        } else if (model instanceof VPreviewWindow) {
          Window		window;

          window = new Window(model.getTitle());
          window.setImmediate(true);
          window.setModal(true);
          window.center();
          window.addStyleName(KopiTheme.WINDOW_OPAQUE);
          window.addStyleName(KopiTheme.PREVIEW_WINDOW);
          window.setClosable(true);
          window.setResizable(false);
          window.setDraggable(false);
          window.setWidth("80%");
          window.setHeight("99%");
          window.setContent(view);

          ((UI)VApplicationContext.getApplicationContext().getApplication()).addWindow(window);
        }
    
        UI.getCurrent().push();
      }
    });   
  }

  /**
   * Reports an error represented by its reason.
   * @param throwable The error reason.
   */
  public void reportError(Throwable throwable) {
    if (throwable.getMessage() != null) {
      VApplicationContext.getApplicationContext().getApplication().displayError(null, throwable.getMessage());
    }
  }
  
  //-----------------------------------------------------
  // INNER CLASS
  //-----------------------------------------------------
  
  /**
   * The <code>ModalViewRunner</code> is a {@link Runnable}
   * used to display model windows. This class should ensure
   * the lock of the working thread until further notification
   * of the lock release.
   */
  /*package*/ class ModalViewRunner implements Runnable {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ModalViewRunner</code> instance.
     * @param model The window model.
     */
    /*package*/ ModalViewRunner(VWindow model) {
      this.model = model;
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    //!!! FIXME : Should be reviewed.
    @Override
    public void run() {
      try {
        view = (DWindow) UIFactory.getUIFactory().createView(model); 
	    
        UI.getCurrent().access(new Runnable() {
          
	  @SuppressWarnings({ "serial"})
	  @Override
	  public void run() {
	    VerticalLayout windowContent=new VerticalLayout();
	    windowContent.addStyleName(KopiTheme.FORM_WINDOW);
	    windowContent.addComponent(view.getButtonPanel());
	    windowContent.addComponent(view); 
	    
	    VerticalLayout windowFooter= new VerticalLayout();
	    windowFooter.addStyleName(KopiTheme.FOOTER_LAYOUT);  
	    windowFooter.addComponent(view.getFootPanel());
	    windowFooter.addComponent(view.getNotificationPanel());
	    view.getNotificationPanel().addStyleName(KopiTheme.NOTIFICATION_WINDOW_STYLE); 
	    
	    windowContent.addComponent(windowFooter); 
	    windowContent.addComponent(view.getWaitIndicator());
	        
	    Window window = new Window(model.getTitle());
	    window.setModal(true);
	    window.setDraggable(false);
	    window.setClosable(true);
	    window.addCloseListener(new CloseListener() {
	      
	      @Override
	      public void windowClose(final CloseEvent e) {
		UI.getCurrent().access(new Runnable() {
		  
	          @Override
		  public void run() {
		    if (!view.isUserAsked()) {
		      if (view.getModel() != null) {
		        view.getModel().close(-1);
		      }
		    } else {
		      ((UI)VApplicationContext.getApplicationContext().getApplication()).addWindow(e.getWindow());
		    }
	          }
	        });		
	      }
	    });
	    window.center();
	    window.setResizable(false);
	    window.setWidth("80%");
	    window.setHeight("80%");
      	    window.addStyleName(KopiTheme.POPUP_FORM);
      	    window.setImmediate(true);
      	    window.setContent(windowContent);
      	
      	    ((UI)VApplicationContext.getApplicationContext().getApplication()).addWindow(window);
      	    view.markAsDirtyRecursive();
      	    window.focus();
      	    
      	    ((UI)VApplicationContext.getApplicationContext().getApplication()).push();
          }
        });
        view.run();
      } catch (VException e) {
        throw new VRuntimeException(e.getMessage(), e);
      }
    }
    
    /**
     * Returns the created window view.
     * @return The created window view.
     */
    public DWindow getView() {
      return view;
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private DWindow			view;
    private final VWindow		model;
  }
}
