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

package com.kopiright.vkopi.lib.visual;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.event.EventListenerList;

import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.visual.FileProductionListener;
import com.kopiright.vkopi.lib.base.Image;
import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;
import com.kopiright.xkopi.lib.base.DBDeadLockException;
import com.kopiright.xkopi.lib.base.XInterruptProtectedException;

@SuppressWarnings("serial")
public abstract class VWindow implements DBContextHandler, KopiExecutable, ActionHandler, VModel {

  /**
   * Creates a window without DB context
   */
  protected VWindow() {
    setDBContext(ApplicationContext.getDBContext());
    init();
  }

  /**
   * Creates a window with a DB context
   */
  protected VWindow(DBContext ctxt) {
    setDBContext(ctxt);
    init();
  }

  /**
   * Creates a window without DB context handler
   */
  protected VWindow(DBContextHandler ctxt) {
    setDBContext(ctxt.getDBContext());
    init();
  }

  private void init() {
    f12 = new VActor("File",
                     WINDOW_LOCALIZATION_RESOURCE,
                     "GotoShortcuts",
                     WINDOW_LOCALIZATION_RESOURCE,
                     null,
                     KeyEvent.VK_F12,
                     0);
    f12.setNumber(Constants.CMD_GOTO_SHORTCUTS);
    f12.setHandler(this);
    setActors(new VActor[] {f12});
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------

  /**
   * doModal
   * modal call to this form
   * @exception	VException	an exception may be raised by triggers
   */
  public boolean doModal(Frame f) throws com.kopiright.vkopi.lib.visual.VException {
    return WindowController.getWindowController().doModal(this);
  }

  /**
   * doModal
   * modal call to this form
   * @exception	VException	an exception may be raised by triggers
   */
  public boolean doModal(VWindow f) throws com.kopiright.vkopi.lib.visual.VException {
    return WindowController.getWindowController().doModal(this);
  }

  /**
   * doModal
   * modal call to this form
   * @exception	VException	an exception may be raised by triggers
   */
  public boolean doModal() throws com.kopiright.vkopi.lib.visual.VException {
    return WindowController.getWindowController().doModal(this);
  }

  /**
   * doNotModal
   * no modal call to this form
   * @exception	VException	an exception may be raised by triggers
   */
  public void doNotModal() throws VException {
    WindowController.getWindowController().doNotModal(this);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns true if asynchronous operations can be performed
   * Override to change behavior
   */
  public boolean allowAsynchronousOperation() {
    return true;
  }

  /**
   * Resets form to initial state
   */
  public void reset() throws com.kopiright.vkopi.lib.visual.VException {
    //!!! graf 990818 make abstract
    // do nothing
  }

  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  public boolean allowQuit() {
    return ! inTransaction();
  }

  /**
   * Destroy this class (break all references to help java to GC the form)
   */
  public void destroyModel() {
  }

  /**
   * Informs model, that this action was executed on it.
   * For cleanUp/Update/....
   * -) THIS method should do as less as possible
   * -) THIS method should need be used to fix the model
   */
  public void executedAction(KopiAction action) {
    // overrriden in VForm
    // nothing to do here
  }


  /**
   * Try to handle an exception
   */
  public final void fatalError(Object data, String line, Throwable reason) {
    try {
      if (inTransaction()) {
	context.abortWork();
      }
    } catch (Exception e) {
      ApplicationContext.reportTrouble("VWindow can not abort transaction", line, (data != null) ? data.toString() : "<no info about>", e);
      e.printStackTrace();
    }
    if (ApplicationContext.getDefaults().isDebugModeEnabled()) {
      error("FATAL ERROR: " + reason.getMessage());
      reason.printStackTrace(System.err);
    } else {
      ApplicationContext.reportTrouble("VWindow", line, (data != null) ? data.toString() : "<no info about>", reason);
      error(com.kopiright.vkopi.lib.visual.MessageCode.getMessage("VIS-00041"));
    }

    close(1);
  }

  /**
   * @deprecated        use method performAsynAction
   */
  public final void performAction(final KopiAction action, boolean block) {
    performAsyncAction(action);
  }

  public final void performAsyncAction(final KopiAction action) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==VActionListener.class) {
        ((VActionListener)listeners[i+1]).performAsyncAction(action);
      }
    }
  }

  // ----------------------------------------------------------------------
  // INFORMATION HANDLING
  // ----------------------------------------------------------------------

  /**
   * @deprecated use the method <code>notice(String message)</code> instead
   */
  public void displayNotice(String message) {
    notice(message);
  }

  public void notice(String message) {
    boolean     send = false;
    Object[]    listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==MessageListener.class) {
        ((MessageListener)listeners[i+1]).notice(message);
        send = true;
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happend in the
      // constructor)
      ApplicationContext.getApplicationContext().getApplication().notice(message);
    }
  }

  /**
   * @deprecated use the method <code>error(String message)</code> instead
   */
  public void displayError(String message) {
    error(message);
  }

  public void error(String message) {
    boolean     send = false;
    Object[]    listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==MessageListener.class) {
        ((MessageListener)listeners[i+1]).error(message);
        send = true;
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happened in the
      // constructor)
      ApplicationContext.getApplicationContext().getApplication().error(message);
    }
  }

  /**
   * @deprecated use the method <code>warn(String message)</code> instead
   */
  public void displayWarning(String message) {
    warn(message);
  }

  /**
   * Displays a warning message.
   */
  public void warn(String message) {
    boolean     send = false;
    Object[]    listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==MessageListener.class) {
        ((MessageListener)listeners[i+1]).warn(message);
        send = true;
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happend in the
      // constructor)
      ApplicationContext.getApplicationContext().getApplication().warn(message);
    }
  }


  /**
   * @deprecated use the method <code>ask(String message)</code> instead
   */
  public boolean askUser(String message) {
    return ask(message);
  }

  /**
   * Displays an ask dialog box
   */
  public boolean ask(String message) {
    return ask(message, false);
  }

  /**
   * @deprecated use the method <code>ask(String message, boolean yesIsDefault)</code> instead
   */
  public boolean askUser(String message, boolean yesIsDefault) {
    return ask(message, yesIsDefault);
  }

  /**
   * Displays an ask dialog box
   */
  public boolean ask(String message, boolean yesIsDefault) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==MessageListener.class) {
        int     value;

        value = ((MessageListener) listeners[i+1]).ask(message, yesIsDefault);
        switch (value) {
        case MessageListener.AWR_YES:
          return true;
        case MessageListener.AWR_NO:
          return false;
        case  MessageListener.AWR_UNDEF:
        default:
          // ask the next one
        }
      }
    }
    return false;
  }

  public String getTitle() {
    return title + ((extraTitle != null)? " " + extraTitle : "");
  }

  /**
   * Sets a the text to be appended to the title.
   */
  public void appendToTitle(String text) {
    this.extraTitle = text;
    if (display != null) {
      display.setTitle(getTitle());
    }
  }

  /**
   * change the title of this form
   */
  public void setTitle(String title) {
    this.title = title;
    if (display != null) {
      display.setTitle(getTitle());
    }
  }

  // ----------------------------------------------------------------------
  // PUBLIC (PROTECTED TO COMPILER) ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * addCommand in menu
   * Called by code generated by the compiler
   */
  public void setActors(VActor[] actorDefs) {
    if (actorDefs != null) {
      int	offset = 0;

      if (actors != null) {
	offset = actors.length;
	VActor[]	olds = actors;
	actors = new VActor[actorDefs.length + offset];
	for (int i = 0; i < olds.length; i++){
	  actors[i] = olds[i];
	}
      } else {
	actors = new VActor[actorDefs.length];
      }
      for (int i = 0; i < actorDefs.length; i++) {
	actors[i + offset] = actorDefs[i];
      }
    }
  }

  public VActor getActor(int at) {
    // "+1" because of the f12-Actor
    return actors[at + 1];
  }

  public VActor[] getActors() {
    return actors;
  }

  /**
   * Enables/disables the actor.
   */
  public void setActorEnabled(int position, boolean enabled) {
    VActor      actor;

    actor = getActor(position);
    actor.setHandler(this);
    actor.setEnabled(enabled);
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes the actors of this window
   *
   * @param     manager         the manger to use for localization
   */
  public void localizeActors(LocalizationManager manager) {
    for (int i = 0; i < actors.length; i++) {
      actors[i].localize(manager);
    }
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * close model if allowed
   */
  public void willClose(final int code) {
    close(code);
  }

  /**
   * Inform close linstener that this model was closed
   */
  public void close(final int code) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==ModelCloseListener.class) {
        ((ModelCloseListener)listeners[i+1]).modelClosed(code);
      }
    }
  }

  //----------------------------------------------------------------------
  // VMODEL IMPLEMENTATION
  //----------------------------------------------------------------------

  
  public UWindow getDisplay() {
    return display;
  }

  
  public void setDisplay(UComponent display) {
    assert display instanceof UWindow : "VWindow display should be instance of UWindow";

    this.display = (UWindow)display;
    setTitle(title);
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * setInformationText
   */
  public void setInformationText(String text) {
    if (display != null) {
      display.setInformationText(text);
    }
  }

  /**
   * setProgressDialog
   */
  public void setProgressDialog(String message, int currentJob) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==ProgressDialogListener.class) {
        ((ProgressDialogListener)listeners[i+1]).setProgressDialog(message, currentJob);
      }
    }
  }

  public final void setTotalJobs(int totalJobs) {
    if (getDisplay() != null) {
      getDisplay().setTotalJobs(totalJobs);
    }
  }

  public final void setCurrentJob(int currentJob) {
    if (getDisplay() != null) {
      getDisplay().setCurrentJob(currentJob);
    }
  }

  public void unsetProgressDialog() {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==ProgressDialogListener.class) {
        ((ProgressDialogListener)listeners[i+1]).unsetProgressDialog();
      }
    }
  }

  public final void updateWaitDialogMessage(String message) {
    if (getDisplay() != null) {
      getDisplay().updateWaitDialogMessage(message);
    }
  }

  /**
   * setWaitInfo
   */
  public void setWaitDialog(String message, int maxtime) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==WaitDialogListener.class) {
        ((WaitDialogListener)listeners[i+1]).setWaitDialog(message, maxtime);
      }
    }
  }

  /**
   * change mode to free state
   */
  public void unsetWaitDialog() {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==WaitDialogListener.class) {
        ((WaitDialogListener)listeners[i+1]).unsetWaitDialog();
      }
    }
  }

  /**
   * setWaitInfo
   */
  public void setWaitInfo(String message) {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==WaitInfoListener.class) {
        ((WaitInfoListener)listeners[i+1]).setWaitInfo(message);
      }
    }
  }

  /**
   * change mode to free state
   */
  public void unsetWaitInfo() {
    Object[]          listeners = modelListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==WaitInfoListener.class) {
        ((WaitInfoListener)listeners[i+1]).unsetWaitInfo();
      }
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  public int getType() {
    return Constants.MDL_UNKOWN;
  }



  public void enableCommands() {
    f12.setEnabled(true);
  }

  public void setCommandsEnabled(boolean enable) {
    f12.setEnabled(enable);
  }
  /**
   * Performs a void trigger
   *
   * @param	VKT_Type	the number of the trigger
   */
  public void executeVoidTrigger(final int VKT_Type) throws VException {
    if (VKT_Type == Constants.CMD_GOTO_SHORTCUTS) {
      try {
	ApplicationContext.getMenu().getDisplay().gotoShortcuts();
      } catch (NullPointerException npe) {
        throw new VExecFailedException(VlibProperties.getString("shortcuts-not-available"));
      }
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF DBContextHandler
  // ----------------------------------------------------------------------

  /**
   * Sets the database context for this object
   */
  public void setDBContext(DBContext context) {
    this.context = context;
    try {
      context.startWork();
      System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx transaction Isolation = " + context.getDefaultConnection().getJDBCConnection().getTransactionIsolation());
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      try {
        context.abortWork();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Gets the database context for this object
   */
  public DBContext getDBContext() {
    return context;
  }

  /**
   * Starts a protected transaction.
   * @param	message		the message to be displayed
   */
  public void startProtected(String message) {
    assert context != null : threadInfo() + "No database context for protected";

    synchronized (transactionMonitor) {
      // this object con not be used a monitor, it already used to signal
      // other forms that this form is finished.
      // must be done before startWait
      assert !isProtected : threadInfo() + "A transaction is already opened";
      isProtected = true;
    }

    setWaitInfo(message);
    // startWork waits, till the connection is free to use.
    // An instance of the applicatin has only one connection
    // and therefore only one transaction at any time.
    context.startWork();
  }

  /**
   * Commits a protected transaction.
   * @exception	Exception	an exception may be raised by DB
   */
  public void commitProtected() throws SQLException {
    try {
      context.commitWork();
    } finally {
      unsetWaitInfo();
      isProtected = false;
    }
  }


  /**
   * Handles transaction failure
   * @param	interrupt	interrupt transaction
   */
  public void abortProtected(boolean interrupt) {
    try {
      if (inTransaction()) {
        try {
          if (interrupt) {
            getDBContext().getDefaultConnection().interrupt();
          }
        } finally {
          context.abortWork();
        }
      }
    } catch (SQLException s) {
      ApplicationContext.reportTrouble("VWindow (not thrown but reported)",
                                       "abortProtected(" + interrupt + ")",
                                       this.toString(),
                                       s);
    } finally {
      isProtected = false;
      unsetWaitInfo();
    }
  }

  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  public boolean retryableAbort(Exception reason) {
    if (reason instanceof DBDeadLockException) {
      return true;
    } if (reason instanceof XInterruptProtectedException) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  public boolean retryProtected() {
    return ask(MessageCode.getMessage("VIS-00039"));
  }

  /**
   * return wether this object handle a transaction at this time
   */
  public boolean inTransaction() {
    return isProtected;
  }

  /**
   *
   */
  public String getUserName() {
    return context.getDefaultConnection().getUserName();
  }

  /**
   *
   */
  public int getUserID() {
    return context.getDefaultConnection().getUserID();
  }

  public void setSmallIcon(Image smallIcon) {
    this.smallIcon = smallIcon;
  }

  public Image getSmallIcon() {
    return smallIcon;
  }

  // ----------------------------------------------------------------------
  // Listener
  // ----------------------------------------------------------------------

  public void addMessageListener(MessageListener ml) {
    modelListener.add(MessageListener.class, ml);
  }
  public void removeMessageListener(MessageListener ml) {
    modelListener.remove(MessageListener.class, ml);
  }

  public void addWaitInfoListener(WaitInfoListener wil) {
    modelListener.add(WaitInfoListener.class, wil);
  }
  public void removeWaitInfoListener(WaitInfoListener wil) {
    modelListener.remove(WaitInfoListener.class, wil);
  }

  public void addWaitDialogListener(WaitDialogListener wil) {
    modelListener.add(WaitDialogListener.class, wil);
  }
  public void removeWaitDialogListener(WaitDialogListener wil) {
    modelListener.remove(WaitDialogListener.class, wil);
  }

  public void addProgressDialogListener(ProgressDialogListener wil) {
    modelListener.add(ProgressDialogListener.class, wil);
  }

  public void removeProgressDialogListener(ProgressDialogListener wil) {
    modelListener.remove(ProgressDialogListener.class, wil);
  }

  public void addVActionListener(VActionListener al) {
    modelListener.add(VActionListener.class, al);
  }
  public void removeVActionListener(VActionListener al) {
    modelListener.remove(VActionListener.class, al);
  }


  public void addModelCloseListener(ModelCloseListener mcl) {
    modelListener.add(ModelCloseListener.class, mcl);
  }
  public void removeModelCloseListener(ModelCloseListener mcl) {
    modelListener.remove(ModelCloseListener.class, mcl);
  }

  // ----------------------------------------------------------------------
  // DEBUGGING
  // ----------------------------------------------------------------------

  protected static final String threadInfo() {
    return "Thread: " + Thread.currentThread() + "\n";
  }
  
  //--------------------------------------------------------------------
  // FILE PRODUCTION LISTENERS HANDLING
  // --------------------------------------------------------------------

  /**
  * Adds a listener to the list that's notified each time a production 
  * of the report file occurs.
  *
  * @param l The FileProductionListener
  */
  public void addFileProductionListener(FileProductionListener l) {
    listenerList.add(FileProductionListener.class, l);
  }

  /**
  * Removes a listener from the list that's notified each time a
  * production of the report file occurs.
  *
  * @param l The FileProductionListener
  */
  public void removeReportListener(FileProductionListener l) {
    listenerList.remove(FileProductionListener.class, l);
  }

  /**
   * Notifies all listeners that the report file is produced.
   */
  public void fireFileProduced(File file) {
    Object[]		listeners = listenerList.getListenerList();

    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == FileProductionListener.class) {
	((FileProductionListener)listeners[i+1]).fileProduced(file);
      }
    }
  }
 
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public static final int   		CDE_QUIT = 0;
  public static final int       	CDE_ESCAPED = 1;
  public static final int       	CDE_VALIDATE = 2;
  public static final String    	WINDOW_LOCALIZATION_RESOURCE = "com/kopiright/vkopi/lib/resource/Window";

  private final Object          	transactionMonitor = new Object();
  private EventListenerList     	modelListener = new EventListenerList();
  private String                	extraTitle;

  protected VActor              	f12;
  protected UWindow             	display;
  protected DBContext           	context;
  protected boolean             	isProtected;
  protected VActor[]            	actors;
  protected String              	title;
  protected Image           		smallIcon;
  protected EventListenerList 	        listenerList = new EventListenerList(); // List of listeners
}
