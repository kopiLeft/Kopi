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

package at.dms.vkopi.lib.visual;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.UndoManager;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.Message;
import at.dms.xkopi.lib.base.DBContextHandler;
import at.dms.xkopi.lib.base.DBContext;
import at.dms.xkopi.lib.base.DBDeadLockException;
import at.dms.xkopi.lib.base.DBInterruptionException;
import at.dms.xkopi.lib.base.XInterruptProtectedException;

/**
 * Creates the GUI, opens the window, form, ...
 * Handles actions initialized by the user
 */
public class WindowController {

  public static WindowController getWindowController() {
    if (windowController == null) {
      windowController = new WindowController();
    }
    return windowController;
  }

  private WindowController() {
  }

  class ModalViewRunner implements Runnable {
    ModalViewRunner(final VWindow model) {
      this.model = model;
    }

    public void run() {
      try {
        UIBuilder   builder;
        Window      focus = FocusManager.getCurrentManager().getFocusedWindow();
        
        builder = getUIBuilder(model);
        view = builder.createView(model);
        
        if (focus instanceof JFrame) {
          view.createModalDialog((Frame) focus);
        } else {
          view.createFrame();
        }
        
        view.run();
      } catch (VException e) {
        throw new VRuntimeException(e.getMessage(), e);
      }
    }

    public DWindow getView() {
      return view;
    }

    private DWindow       view;
    private VWindow       model;
  }

  public boolean doModal(final VWindow model) throws VException {
    final ModalViewRunner     viewStarter = new ModalViewRunner(model);

    synchronized(model) {
      SwingThreadHandler.startAndWait(viewStarter);
      try {
        if (SwingUtilities.isEventDispatchThread()) {
          // !! prevent that these code is executed
          // Real event handling is much more sophisticated
          DWindow       view;
          EventQueue    eventQueue;

          view = viewStarter.getView();
          if (view == null) {
            return false;
          }
          eventQueue = view.getToolkit().getSystemEventQueue();          

          while (view.isShowing()) {
            AWTEvent    event = eventQueue.getNextEvent();
            Object      source = event.getSource();

            try {
              if (event instanceof ActiveEvent) {
                ((ActiveEvent)event).dispatch();
              } else if (source instanceof Component) {
                ((Component)source).dispatchEvent(event);
              } else if (source instanceof MenuComponent) {
                ((MenuComponent)source).dispatchEvent(event);
              } else {
                System.err.println("unable to dispatch event: " + event);
              }
            } catch (RuntimeException e) {
              // is ignored
              Application.reportTrouble("WindowController", 
                                        "WindowController.dispatch (I should not be here!)", 
                                        event.toString(), 
                                        e);
            }
          }
        } else {
          model.wait();
        }
      } catch (InterruptedException e) {
        // wait interrupted
      }
    }
    return (viewStarter.getView() == null) ? false : viewStarter.getView().getReturnCode() == VWindow.CDE_VALIDATE;
  }

  public void doNotModal(final VWindow model) throws VException {
    SwingThreadHandler.start(new Runnable() {
        public void run() {
          try {
            DWindow     view;
            UIBuilder   builder;

            builder = getUIBuilder(model);
            view = builder.createView(model);
            view.createFrame();
            view.run();
          } catch (VException e) {
            // report error to user
            // this is called in the event-handling-thread
            // so this exceptions have not to be forwarded
            reportError(e);            
          } catch (VRuntimeException e) {
            // report error to user
            // this is called in the event-handling-thread
            // so this exceptions have not to be forwarded
            reportError(e);
          } 
        }
      });
  }

  public void reportError(Exception e) {
    if (e.getMessage() != null) {
      DWindow.displayError(null,
                           e.getMessage());
    }
  }

  public boolean doNotModal(DWindow view) throws VException {
    view.createFrame();

    SwingThreadHandler.start(view);
    return true;
  }

  public UIBuilder registerUIBuilder(int typ, UIBuilder uiBuilder) {
    UIBuilder   old = builder[typ];

    builder[typ] = uiBuilder;
    return old;
  }

  private UIBuilder getUIBuilder(VWindow model) throws VException {
    if (model.getType() > builder.length || builder[model.getType()] == null) {
      // programm should never reach here.
      Thread.dumpStack();
      throw new InconsistencyException("WindowController: UIBuilder not found");
    } else {
      return builder[model.getType()];
    }
  }

  private UIBuilder[]                   builder = new UIBuilder[256];
  private static WindowController       windowController;
} 
