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

import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.ButtonPanel;
import com.kopiright.vkopi.lib.ui.vaadin.base.ExportResource;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.PositionRequestor;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.MessageListener;
import com.kopiright.vkopi.lib.visual.PropertyException;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.kopiright.vkopi.lib.visual.VActor;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.visual.WaitInfoListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Container;
import com.vaadin.event.ActionManager;
import com.vaadin.server.VariableOwner;
import com.vaadin.shared.Position;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.MessageBoxListener;

/**
 * The <code>DWindow</code> is an abstract implementation of an {@link UWindow} component.
 * The vaadin implementation is based on lightweight components regarding to client side
 * and reduce the window load time.
 */
@SuppressWarnings({ "serial", "deprecation"})
public abstract class DWindow extends Panel implements UWindow { //!!! FIXME use CSSLayout instead

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DWindow</code> instance.
   * @param model The window model.
   */
  protected DWindow(VWindow model) {
    addStyleName(KopiTheme.PANEL_LIGHT);
    setImmediate(true);
    this.model = model;
    buttonPanel = new ButtonPanel();
    footPanel = new DFootPanel(this);
    footPanel.setWidth("100%");
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
    addActorsToGUI(model.getActors());
    // Add the help menu at last position
    notificationPanel = new NotificationPanel("", "");
    waitIndicator = new WaitIndicator();
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
   * Returns the button panel.
   * @return The button panel.
   */
  public ButtonPanel getButtonPanel() {
    return buttonPanel;
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
        model.notifyAll();
        release();
        dispose();
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
		
	actor = new DActor(actorDefs[i], this);
        addButton(buttonPanel, actor);
        //this.addShortcutListener(actor.getShortcutHandler()); Wrong: This does not allow to enable/disable the shortcut according to the actor
        if (i != actorDefs.length - 2
            && actor.getModel().iconName != null)
        {     	
          buttonPanel.addSeparator();
        }
      } 
    }
  }

  /**
   * Adds a button to the button panel.
   * @param panel The button panel.
   * @param actor The actor view.
   */
  private void addButton(CssLayout panel, DActor actor) {
    if (actor.getModel().iconName != null) {
      panel.addComponent(actor.getButton());
      //panel.setComponentAlignment(actor.getButton(),Alignment.MIDDLE_CENTER);
    }
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
    notificationPanel.hide();
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
      Thread     currentThread;
      //
      //	      // asyn. work of task
      //	      // must set inAction to false in event-disp.thread
      //	      // after it is fully completed
      //actionRunner.run();	
      currentThread = new Thread(actionRunner);
      currentThread.start();
    } 
  }
	  
  public final void setStatePanel(Panel statePanel) {
    footPanel.setStatePanel(statePanel);
  }
	  
  /**
   * Disposes the window. Finalize and close this window.
   */
  private void dispose() {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {  
        if (getParent().getParent() instanceof TabSheet) {
          TabSheet	tabsheet = ((TabSheet)getParent().getParent());

          tabsheet.removeComponent(getParent());
          if(tabsheet.getComponentCount() > 1){
            tabsheet.setSelectedTab(1);
          }
        } else {
          // all modal and children window are added to the application main window
          // It's enough to close the parent window.
          ((Window)getParent().getParent()).close();
        }
      }
    });
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
    getActionManager().removeAllActionHandlers();
    footPanel = null;
    buttonPanel = null;
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
   * Returns the foot panel of this window.
   * @return The foot panel of this window.
   */
  public DFootPanel getFootPanel() {
    return footPanel;
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
	  
  @Override
  protected ActionManager getActionManager() {
    if (actionManager == null) {
      actionManager = new ActionHandler(this);
    }
	    
    return actionManager;
  }
  
  /**
   * Returns {@code true} if the window contains the given action.
   * @param action The action to be checked.
   * @return {@code true} if the window contains the given action.
   */
  public boolean hasAction(Action action) {
    return ((ActionHandler)getActionManager()).hasAction(action);
  }
	  
  /**
   * Displays a text in the lower right corner of the window.
   * @param text The statistics text.
   */
  public final void setStatisticsText(String text) {
    footPanel.setStatisticsText(text);
  }

  /**
   * Asks the user for a record to go through.
   * @param parent The parent container.
   * @param current The current record.
   * @param total The total records.
   * @return The chosen position.
   */
  public static int askPosition(ComponentContainer parent, int current, int total) {
    PositionRequestor	requestor = new PositionRequestor(parent, current, total);
    
    return requestor.askPosition();
  }

  /**
   * Returns the {@link NotificationPanel} instance.
   * @return The {@link NotificationPanel} instance.
   */
  public NotificationPanel getNotificationPanel() {
    return notificationPanel;
  }

  /**
   * Returns the {@link WaitIndicator} instance.
   * @return The {@link WaitIndicator} instance.
   */
  public WaitIndicator getWaitIndicator() {
    return waitIndicator;
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
  public void setTotalJobs(int totalJobs) {
    // TODO
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
  public void setWaitDialog(String message, int maxtime) {
    // TODO
  }

  @Override
  public void unsetWaitDialog() {
    // TODO 
  }

  @Override
  public void setProgressDialog(String message, int totalJobs) {
    // TODO 
  }
  
  @Override
  public void unsetProgressDialog() {
    // TODO 
  }

  @Override
  public VWindow getModel() {
    return model;
  }
  
  @Override
  public void setCurrentJob(int currentJob) {
    // TODO
  }

  @Override
  public void setTitle(String title) {
    // TODO
  }

  @Override
  public void setInformationText(final String text) {
    footPanel.setInformationText(text);
  }

  @Override
  public void updateWaitDialogMessage(String message) {
    // TODO 
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
    verifyNotInTransaction("DWindow.showApplicationInformation(" + message + ")");
	    
    messageBox = MessageBox.showPlain(Icon.INFO,
    		                      VlibProperties.getString("Notice"),
    		                      message,
    		                      ButtonId.CLOSE);
    messageBox.getButton(ButtonId.CLOSE).setCaption(VlibProperties.getString("CLOSE"));
  }

  /**
   * Displays an ask dialog box.
   * @param message The message to be displayed.
   */
  public void askUser(String message) {
    verifyNotInTransaction("DWindow.askUser(" + message + ")");

    messageBox = MessageBox.showPlain(Icon.QUESTION,
    		                      VlibProperties.getString("Question"),
    		                      message,
    		                      ButtonId.YES,
    		                      ButtonId.NO);
    messageBox.getButton(ButtonId.YES).setCaption(VlibProperties.getString("OK"));
    messageBox.getButton(ButtonId.NO).setCaption(VlibProperties.getString("NO"));
  }
	  
  /**
   * Reports if a message is shown while in a transaction.
   * @param The message to be displayed.
   */
  private void verifyNotInTransaction(String message) {
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
      final Notification	notification;
	      
      notification = new Notification(VlibProperties.getString("Notice"),
                                      message,
                                      Type.TRAY_NOTIFICATION);
      notification.setPosition(Position.MIDDLE_CENTER);
      notification.setDelayMsec(800);
      BackgroundThreadHandler.start(new Runnable() {
        
        @Override
        public void run() {
          UI.getCurrent().showNotification(notification);
        }
      });
    }

    @Override
    public void error(final String message) {
      notificationPanel.displayNotification(VlibProperties.getString("Error"),message);
    }

    @Override
    public void warn(String message) {
      notificationPanel.displayNotification(VlibProperties.getString("Warning"),message);
    }

    /**
     * Displays a request dialog for a user interaction.
     * @param message The message to be displayed in the dialog box.
     */
    public void ask(String message) {
      messageBox = MessageBox.showPlain(Icon.QUESTION,
	                                VlibProperties.getString("Question"),
	                                message,
	                                ButtonId.YES,
	                                ButtonId.NO);
      messageBox.getButton(ButtonId.YES).setCaption(VlibProperties.getString("OK"));
      messageBox.getButton(ButtonId.NO).setCaption(VlibProperties.getString("NO"));
    }

    @Override
    public int ask(final String message, boolean yesIsDefault) {
      final Object			lock;
      final MessageBoxListener 		askToQuitListener;
      
      askUser = true;
      res = 0;	
      lock = new Object();
      askToQuitListener= new MessageBoxListener() {
	
	@Override
	public void buttonClicked(ButtonId buttonId) {
	  if (buttonId.equals(ButtonId.YES)) {
	    res = 1;
	  } else {
	    res = 0;
	  }
	  
	  messageBox.close();
	  BackgroundThreadHandler.releaseLock(lock);
	}
      };
      
      BackgroundThreadHandler.startAndWait(new Runnable() {
	
	@Override
	public void run() { 
	  messageBox = MessageBox.showPlain(Icon.QUESTION,
	                                    VlibProperties.getString("Question"),
	                                    message,
	                                    askToQuitListener, 
	                                    ButtonId.YES,
	                                    ButtonId.NO);
	  messageBox.getButton(ButtonId.YES).setCaption(VlibProperties.getString("OK"));
	  messageBox.getButton(ButtonId.NO).setCaption(VlibProperties.getString("NO"));
	}
      }, lock);
      
      askUser = false;
      return res;
    }
  }
	  
  //--------------------------------------------------------------
  // WAITINFOLISTENER IMPLEMENTATION
  //--------------------------------------------------------------

  /**
   * The <code>WaitInfoHandler</code> is the window implementation
   * of the {@link WaitInfoListener}
   */
  /*package*/ class WaitInfoHandler implements WaitInfoListener {

    @Override
    public void setWaitInfo(final String message) {
      if (footPanel != null) {
	BackgroundThreadHandler.start(new Runnable() {
	  
	  @Override
	  public void run() {
	    footPanel.setWaitInfo(message);	
	    waitIndicator.show(message);
	  }
	});
      }
    }

    @Override
    public void unsetWaitInfo() {
      if (footPanel != null) {
	BackgroundThreadHandler.start(new Runnable() {
	  
	  @Override
	  public void run() {
	    footPanel.unsetWaitInfo();	
	    waitIndicator.hide();
	  }
	});
      } 
    }
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
      } catch (VRuntimeException v) {
	v.printStackTrace();
	reportError(v);
	//getModel().error(v.getMessage());
      } catch (ArrayIndexOutOfBoundsException ar) {
	// Ignor out of bound exception in position requestor
      } catch (Throwable exc) {
	//exc.printStackTrace();
	//System.out.println("DWindow exc exception: "+exc.getMessage());
	// model can be destroyed
	((VApplication)ApplicationContext.getApplicationContext().getApplication()).displayError(null, com.kopiright.vkopi.lib.visual.MessageCode.getMessage("VIS-00041"));
	if (getModel() != null) {
	  getModel().fatalError(getModel(), "VWindow.performActionImpl(final KopiAction action)", exc);
	}
      } finally {  
	setInAction();
	BackgroundThreadHandler.updateUI();
      }   
    }

    /**
     * Executes the inner action of this runner. 
     */
    public synchronized void setInAction() {
      try {
	if (getModel() != null) {
	  // actions which close the window also
	  // set the referenced model to null
	  getModel().executedAction(currentAction);
	}

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
	  
  //--------------------------------------------------------------
  // ACTION MAPPER
  //--------------------------------------------------------------
  
  /**
   * The <code>ActionHandler</code> is the customized window
   * {@link ActionManager} that allows to test if the window
   * contains a given vaadin action and use this ability to
   * enable or disable window actors.
   */
  public class ActionHandler extends ActionManager {

    //-------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------
    
    /**
     * Creates a new <code>ActionHandler</code> instance.
     * @param viewer The component who will receive the actions.
     */
    public <T extends Component & Container & VariableOwner> ActionHandler(T viewer) {
      super(viewer);
    }

    //-------------------------------------------------
    // IMPLEMENTATIONS
    //-------------------------------------------------
    
    /**
     * Returns {@code true} if the action handler contains the given action.
     * @return {@code true} if the action handler contains the given action.
     */
    public boolean hasAction(Action action) {
      for (Action ownAction : ownActions) {
	if (action == ownAction) {
	  return true;
	}
      }
	      
      return false;
    }
	    
    //----------------------------------------------
    // DATA MEMBERS
    //----------------------------------------------
	    
    private static final long 		serialVersionUID = -2418676926322540654L;
  }
  
  //----------------------------------------------
  // FILE PRODUCTION IMPLEMENTATION
  //----------------------------------------------
   
  @Override
  public void fileProduced(File file) {
    final ExportResource    resource =  new ExportResource (file, file.getName());
    
    BackgroundThreadHandler.start(new Runnable() {
      
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
  private ButtonPanel				        buttonPanel;
  private boolean			                inAction;
  private KopiAction			                currentAction;
  protected Throwable           	                runtimeDebugInfo;
  private DFootPanel			                footPanel;
  private int			                        returnCode;
  private NotificationPanel                             notificationPanel;
  private WaitIndicator                                 waitIndicator;
  private int                                           res;
  private boolean					askUser;
  private MessageBox                                    messageBox;
  private final ActionRunner    	      	        actionRunner = new ActionRunner();
}