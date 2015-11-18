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

import java.io.File;

import org.kopi.vaadin.addons.AbstractNotification;
import org.kopi.vaadin.addons.ConfirmNotification;
import org.kopi.vaadin.addons.ErrorNotification;
import org.kopi.vaadin.addons.InformationNotification;
import org.kopi.vaadin.addons.NotificationListener;
import org.kopi.vaadin.addons.PopupWindow;
import org.kopi.vaadin.addons.ProgressDialog;
import org.kopi.vaadin.addons.WaitDialog;
import org.kopi.vaadin.addons.WaitWindow;
import org.kopi.vaadin.addons.WarningNotification;

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.ExportResource;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.MessageListener;
import com.kopiright.vkopi.lib.visual.PropertyException;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.kopiright.vkopi.lib.visual.VActor;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.visual.WaitInfoListener;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

/**
 * The <code>DWindow</code> is an abstract implementation of an {@link UWindow} component.
 * The vaadin implementation is based on lightweight components regarding to client side
 * and reduce the window load time.
 */
@SuppressWarnings({ "serial", "deprecation"})
public abstract class DWindow extends org.kopi.vaadin.addons.Window implements UWindow {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DWindow</code> instance.
   * @param model The window model.
   */
  protected DWindow(VWindow model) {
    //addStyleName(KopiTheme.PANEL_LIGHT);
    setImmediate(true);
    this.model = model;
    setCaption(model.getTitle());
    setSizeFull();
    createEditMenu();
    model.addVActionListener(this);
    model.addModelCloseListener(this);
    model.addWaitDialogListener(this);
    model.addProgressDialogListener(this);
    model.addFileProductionListener(this);
    waitInfoHandler = new WaitInfoHandler();
    model.addWaitInfoListener(waitInfoHandler);
    messageHandler = new MessageHandler();
    model.addMessageListener(messageHandler);
    actionRunner = new ActionRunner();
    addActorsToGUI(model.getActors());
    progressDialog = new ProgressDialog();
    waitDialog = new WaitDialog();
    addAttachDetachListeners();
  }
	  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the window model.
   * @param model The window model.
   */
  public void setModel(VWindow model) {
    this.model = model;
    addActorsToGUI(getModel().getActors());
    setWindowFocusEnabled(true);
  }
	  
  /**
   * Displays an error message.
   * @param message The error message to be displayed.
   */
  public void displayError(String message) {
    messageHandler.error(message);
  }

  /**
   * Reports a visual error from a runtime exception.
   * @param e The runtime exception.
   */
  public void reportError(VRuntimeException e) {
    if (e.getMessage() != null) {
      displayError(e.getMessage());
    }
  }
  
  /**
   * Closes the view and the model definitely.
   * @param code The exit code
   * @see #closeWindow()
   */
  protected void close(int code) {
    VWindow     model = this.model; //destroyed in release()
    
    try {
      model.destroyModel();
    } finally {
      synchronized (model) {
        // set the return code
        returnCode = code;
        // Inform all threads who wait for this panel
        release();
        dispose();
        model.notifyAll();
      }
    }
  }
	  
  /**
   * Allow building of a customized edit menu.
   */
  public void createEditMenu() {}
  
  /**
   * Adds a command in the menu bar.
   * @param actorDefs The {@link VActor} definitions.
   */
  private void addActorsToGUI(VActor[] actorDefs) {
    if (actorDefs != null) {
      for (int i = 0; i < actorDefs.length; i++) {
	DActor		actor;
		
	actor = new DActor(actorDefs[i]);
        addActor(actor);
      } 
    }
  }
  
  /**
   * Adds progress bar and wait dialog attach and
   * detach listeners
   */
  private void addAttachDetachListeners() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        progressDialog.addAttachListener(new AttachListener() {
          
          @Override
          public void attach(AttachEvent event) {
            isProgressDialogAttached = true;
          }
        });
        progressDialog.addDetachListener(new DetachListener() {
          
          @Override
          public void detach(DetachEvent event) {
            isProgressDialogAttached = false;
          }
        });
        waitDialog.addAttachListener(new AttachListener() {
          
          @Override
          public void attach(AttachEvent event) {
            isWaitDialogAttached = true;
          }
        });
        waitDialog.addDetachListener(new DetachListener() {
          
          @Override
          public void detach(DetachEvent event) {
            isWaitDialogAttached = false;
          }
        });
      }
    });
  }

  @Override
  public void performBasicAction(final KopiAction action) {
    performActionImpl(action, false);
  }
  
  /**
   * Performs the appropriate action asynchronously or synchronously.
   * <p>
   *   You can use this method to perform any operation out of the UI event process
   * </p>
   * @param action The {@link KopiAction} to be executed.
   * @param asynch Should the action run asynchronously ?
   */
  private void performActionImpl(final KopiAction action, boolean asynch) {
    if (inAction == true) {
      return;
    }
    
    inAction = true;
    currentAction = action;
    getModel().setCommandsEnabled(false);
    runtimeDebugInfo = new RuntimeException(currentAction.toString());
    if (!asynch || !getModel().allowAsynchronousOperation()) {
      // synchronus call
      actionRunner.run();
      if (getModel() != null) {
        // actions which close the window also
        // set the referenced model to null
        getModel().executedAction(currentAction);
      }
    } else {
      Thread 	currentThread = new Thread(actionRunner);
      
      currentThread.start();
    } 
  }
	  
  public final void setStatePanel(Panel statePanel) {
    //footPanel.setStatePanel(statePanel);
  }
	  
  /**
   * Disposes the window. Finalize and close this window.
   */
  private void dispose() {
    VApplication		application;
    // close the window by removing it from the application.
    // this should not be called in a separate transaction.
    // Modal windows are attached to a popup window. So it is not closed
    // like not modal windows. We should remove the popup window from the application
    application = getApplication();
    if (application == null) {
      return; // this should never happen.
    }
    if (getParent() instanceof PopupWindow) {
      // it is a modal window ==> we remove its parent
      application.removeWindow(getParent());
    } else {
      // it is not a modal window, we need to remove it from the application.
      application.removeWindow(this);
    }
  }
	  
  /**
   * <p>Removes all registered listeners on this window.</p>
   * <p>Removes all registered actions on this window</p>
   * <p>Removes all components added to this window</p>
   */
  public synchronized void release() {
    model.removeVActionListener(this);
    model.removeWaitInfoListener(waitInfoHandler);
    model.removeMessageListener(messageHandler);
    model = null;
  }

  /**
   * Use {@link #closeWindow()} or {@link #close(int)} instead.
   */
  @Deprecated
  public void close() {
    closeWindow();
  }

  /**
   * Returns the exist code of this window.
   * @return The exist code of this window.
   */
  public int getReturnCode() {
    return returnCode;
  }
  
  /**
   * Returns the In Action state.
   * @return {@code true} if an action is being performed.
   */
  public boolean getInAction() {
    return inAction;
  }
  
  /**
   * Sets the In action state
   * @see ActionRunner#setInAction()
   */
  public void setInAction() {
    actionRunner.setInAction();
  }
	  
  /**
   * Displays a text in the lower right corner of the window.
   * @param text The statistics text.
   */
  public final void setStatisticsText(String text) {
    // footPanel.setStatisticsText(text);
  }
  
  /**
   * Returns {@code true} if the used has been asked for a request.
   * @return {@code true} if the used has been asked for a request.
   */
  public boolean isUserAsked() {
    return askUser;
  }
  
  //--------------------------------------------------------------
  // UWINDOW IMPLEMENTATION
  //--------------------------------------------------------------
  
  @Override
  public void setTotalJobs(final int totalJobs) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (progressDialog) {
          if (isProgressDialogAttached) {
            progressDialog.setTotalJobs(totalJobs);
          }
        }
      }
    });
  }
  
  @Override
  public void performAsyncAction(final KopiAction action) {
    performActionImpl(action, true);
  }

  @Override
  public void modelClosed(int type) {
    close(type);
  }

  @Override
  public void setWaitDialog(final String message, final int maxtime) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        synchronized (waitDialog) {
          waitDialog.setTitle(MessageCode.getMessage("VIS-00067"));
          waitDialog.setMessage(message);
          waitDialog.setMaxTime(maxtime);
          if (!isWaitDialogAttached) {
            getApplication().attachComponent(waitDialog);
          }
          getApplication().push(); 
        }
      }
    });
  }

  @Override
  public void unsetWaitDialog() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (waitDialog) {
          if (isWaitDialogAttached) {
            waitDialog.setTitle(null);
            waitDialog.setMessage(null);
            waitDialog.setMaxTime(0);
            getApplication().detachComponent(waitDialog);
            getApplication().push();
          } 
        }
      }
    });
  }

  @Override
  public void setProgressDialog(final String message, final int totalJobs) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (progressDialog) {
          progressDialog.setTitle(MessageCode.getMessage("VIS-00067"));
          progressDialog.setMessage(message);
          progressDialog.setTotalJobs(totalJobs);
          if (!isProgressDialogAttached) {
            getApplication().attachComponent(progressDialog);
          }
          getApplication().push();
        }
      }
    });
  }
  
  @Override
  public void unsetProgressDialog() {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (progressDialog) {
          if (isProgressDialogAttached) {
            progressDialog.setTitle(null);
            progressDialog.setMessage(null);
            progressDialog.setTotalJobs(0);
            getApplication().detachComponent(progressDialog);
            getApplication().push();
          }
        }
      }
    });
  }

  @Override
  public VWindow getModel() {
    return model;
  }
  
  @Override
  public void setCurrentJob(final int currentJob) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (progressDialog) {
          if (isProgressDialogAttached) {
            progressDialog.setProgress(currentJob);
          }
        }
      }
    });
  }

  @Override
  public void setTitle(final String title) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        setCaption(title);
        getApplication().push();
      }
    });
  }

  @Override
  public void setInformationText(final String text) {
   // footPanel.setInformationText(text);
  }

  @Override
  public void updateWaitDialogMessage(final String message) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        synchronized (waitDialog) {
          if (isWaitDialogAttached) {
            waitDialog.setMessage(message);
            getApplication().push(); 
          }
        }
      }
    });
  }
  
  @Override
  public void setWindowFocusEnabled(boolean enabled) {
    // do nothing
  }

  @Override
  public void setWaitInfo(String message) {
    waitInfoHandler.setWaitInfo(message);
  }

  @Override
  public void unsetWaitInfo() {
    waitInfoHandler.unsetWaitInfo();
  }

  /**
   * Called to close the view (from the user), it does not
   * definitly close the view(it may ask the user before)
   */
  @Override
  public void closeWindow() {
    if (! getModel().allowQuit()) {
      return;
    }
    
    getModel().willClose(VWindow.CDE_QUIT);
  }

  /**
   * Displays the application information.
   * @param message The application information.
   */
  public void showApplicationInformation(String message) {
//    verifyNotInTransaction("DWindow.showApplicationInformation(" + message + ")");
//	    
//    messageBox = MessageBox.showPlain(Icon.INFO,
//    		                      VlibProperties.getString("Notice"),
//    		                      message,
//    		                      ButtonId.CLOSE);
//    messageBox.getButton(ButtonId.CLOSE).setCaption(VlibProperties.getString("CLOSE"));
  }
  
  /**
   * Sets the window error.
   * @param e The exception cause.
   */
  protected void setWindowError(final Throwable e) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
	if (e == null) {
	  setComponentError(null);
	} else {
	  setComponentError(AbstractErrorMessage.getErrorMessageForException(e));
	  getApplication().push();
	}
      }
    });
  }
	  
  /**
   * Reports if a message is shown while in a transaction.
   * @param The message to be displayed.
   */
  protected void verifyNotInTransaction(String message) {
    if (getModel().inTransaction() && debugMessageInTransaction()) {
      try {
	ApplicationContext.reportTrouble("DWindow",
                                         message + " IN TRANSACTION",
                                         this.toString(),
                                         new RuntimeException("displayNotice in Transaction"));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns true if it should be checked whether a message is shown
   * while in a transaction.
   */
  private boolean debugMessageInTransaction() {
    boolean     debugMessageInTransaction;

    try {
      debugMessageInTransaction = ApplicationContext.getDefaults().debugMessageInTransaction();
    } catch (PropertyException e) {
      debugMessageInTransaction = false;
    }
    
    return debugMessageInTransaction;
  }
  
  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication)ApplicationContext.getApplicationContext().getApplication();
  }
	  
  //--------------------------------------------------------------
  // MESSAGELISTENER IMPLEMENTATION
  //--------------------------------------------------------------

  /**
   * The <code>MessageHandler</code> is the window implementation
   * of the {@link MessageListener}.
   */
  /*package*/ class MessageHandler implements MessageListener {

    @Override
    public void notice(String message) {
      final InformationNotification		dialog;
      final Object				lock;
      
      lock = new Object();
      dialog = new InformationNotification(VlibProperties.getString("Notice"), message);
      dialog.addNotificationListener(new NotificationListener() {
        
        @Override
        public void onClose(boolean yes) {
          getApplication().detachComponent(dialog);
          BackgroundThreadHandler.releaseLock(lock);
        }
      });
      showNotification(dialog, lock);
    }

    @Override
    public void error(final String message) {
      final ErrorNotification		dialog;
      final Object			lock;
      
      lock = new Object();
      dialog = new ErrorNotification(VlibProperties.getString("Error"), message);
      dialog.setOwner(DWindow.this);
      dialog.addNotificationListener(new NotificationListener() {
        
        @Override
        public void onClose(boolean yes) {
          setComponentError(null); // remove any further error.
          getApplication().detachComponent(dialog);
          BackgroundThreadHandler.releaseLock(lock);
        }
      });
      showNotification(dialog, lock);
    }

    @Override
    public void warn(String message) {
      final WarningNotification		dialog;
      final Object			lock;
      
      lock = new Object();
      dialog = new WarningNotification(VlibProperties.getString("Warning"), message);
      dialog.addNotificationListener(new NotificationListener() {
        
        @Override
        public void onClose(boolean yes) {
          getApplication().detachComponent(dialog);
          BackgroundThreadHandler.releaseLock(lock);
        }
      });
      showNotification(dialog, lock);
    }

    /**
     * Displays a request dialog for a user interaction.
     * @param message The message to be displayed in the dialog box.
     */
    public boolean ask(String message) {
      return ask(message, false) == MessageListener.AWR_YES;
    }

    @Override
    public int ask(String message, boolean yesIsDefault) {
      final ConfirmNotification		dialog;
      final Object			lock;

      lock = new Object();
      dialog = new ConfirmNotification(VlibProperties.getString("Question"), message);
      dialog.setYesIsDefault(yesIsDefault);
      dialog.addNotificationListener(new NotificationListener() {
        
        @Override
        public void onClose(boolean yes) {
          if (yes) {
            value = MessageListener.AWR_YES;
          } else {
            value = MessageListener.AWR_NO;
          }
          getApplication().detachComponent(dialog);
          BackgroundThreadHandler.releaseLock(lock);
        }
      });
      // attach the notification to the application.
      showNotification(dialog, lock);
      
      return value;
    }
    
    /**
     * Shows a notification.
     * @param notification The notification to be shown
     */
    protected void showNotification(final AbstractNotification notification, final Object lock) {
      if (notification == null) {
	return;
      }
      
      notification.setLocale(getApplication().getDefaultLocale().toString());
      BackgroundThreadHandler.startAndWait(new Runnable() {
        
        @Override
        public void run() {
          getApplication().attachComponent(notification);
          getApplication().push();
        }
      }, lock);
    }
	  
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private int				value; // only for use in ask(...)
  }
	  
  //--------------------------------------------------------------
  // WAITINFOLISTENER IMPLEMENTATION
  //--------------------------------------------------------------

  /**
   * The <code>WaitInfoHandler</code> is the window implementation
   * of the {@link WaitInfoListener}
   */
  /*package*/ class WaitInfoHandler implements WaitInfoListener {
    
    //-----------------------------------------------------------
    // CONSTRUCTOR
    //-----------------------------------------------------------
    
    public WaitInfoHandler() {
      waitIndicator = new WaitWindow();
      // add attach and detach listeners to detect
      // wait indicator state.
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          waitIndicator.addAttachListener(new AttachListener() {
            
            @Override
            public void attach(AttachEvent event) {
              iswaitIndicatorAttached = true;
            }
          });
          waitIndicator.addDetachListener(new DetachListener() {
            
            @Override
            public void detach(DetachEvent event) {
              iswaitIndicatorAttached = false;
            }
          });
        }
      });
    }
    
    //-----------------------------------------------------------
    // IMPLEMENTATIONS
    //-----------------------------------------------------------
    
    @Override
    public void setWaitInfo(final String message) {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          synchronized (waitIndicator) {
            waitIndicator.setText(message);
            if (!iswaitIndicatorAttached) {
              getApplication().attachComponent(waitIndicator);
            }
            getApplication().push(); 
          }
        }
      });
    }

    @Override
    public void unsetWaitInfo() {
      BackgroundThreadHandler.access(new Runnable() {

        @Override
        public void run() {
          synchronized (waitIndicator) {
            if (iswaitIndicatorAttached) {
              waitIndicator.setText(null);
              getApplication().detachComponent(waitIndicator);
              getApplication().push();
            } 
          }
        }
      });
    }
    
    //-----------------------------------------------------------
    // DATA MEMBERS
    //-----------------------------------------------------------
    
    private WaitWindow                          waitIndicator;
    private boolean                             iswaitIndicatorAttached;
  }
	  
  //--------------------------------------------------------------
  // ACTION RUNNER
  //--------------------------------------------------------------

  /**
   * The <code>ActionRunner</code> is the used to run all users
   * {@link KopiAction}.
   * 
   * <p>There is only one instance of ActionRunner.
   * It calls user actions.</p>
   */
  /*package*/ class ActionRunner implements Runnable {
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void run() {
      try {
	if (currentAction == null) {
	  return;
	}
	currentAction.run();
        if (getModel() != null) {
          // actions which close the window also
          // set the referenced model to null
          getModel().executedAction(currentAction);
        }
      } catch (VRuntimeException v) {
	v.printStackTrace();
        // close the wait info window if it is attached to avoid connector hierarchy corruption.  
        unsetWaitInfo();
	reportError(v);
	//getModel().error(v.getMessage());
      } catch (ArrayIndexOutOfBoundsException ar) {
	// Ignore out of bound exception in position requestor
        // close the wait info window if it is attached to avoid connector hierarchy corruption.  
        unsetWaitInfo();
      } catch (Throwable exc) {
	//exc.printStackTrace();
        // close the wait info window if it is attached to avoid connector hierarchy corruption.  
        unsetWaitInfo();
	setWindowError(exc);
	if (getModel() != null) {
	  getModel().fatalError(getModel(), "VWindow.performActionImpl(final KopiAction action)", exc);
	} else {
	  getApplication().displayError(null, MessageCode.getMessage("VIS-00041"));
	}
      } finally {
	setInAction();
	synchronized (getApplication()) {
	  BackgroundThreadHandler.updateUI();
	}
      }
    }

    /**
     * Executes the inner action of this runner. 
     */
    public synchronized void setInAction() {
      try {
	currentAction = null;
	inAction = false;

	setWindowFocusEnabled(true);

	if (getModel() != null) {
	  // commands like "Beenden" destroy the model
	  // so it must be tested, that there is still a model
	  getModel().setCommandsEnabled(true);
	}	    
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }
  
  //----------------------------------------------
  // FILE PRODUCTION IMPLEMENTATION
  //----------------------------------------------
   
  @Override
  public void fileProduced(File file) {
    final ExportResource    resource =  new ExportResource (file, file.getName());
    
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	UI.getCurrent().getPage().open(resource, "_blank", false);
      }
    });
  }
	  
  //--------------------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------------------

  private VWindow				        model;
  private WaitInfoHandler                               waitInfoHandler;
  private MessageHandler                                messageHandler;
  private boolean			                inAction;
  private KopiAction			                currentAction;
  protected Throwable           	                runtimeDebugInfo;
  private int			                        returnCode;
  private ProgressDialog				progressDialog;
  private boolean                                       isProgressDialogAttached;
  private WaitDialog					waitDialog;
  private boolean                                       isWaitDialogAttached;    
  private boolean					askUser;
  private final ActionRunner    	      	        actionRunner;
}