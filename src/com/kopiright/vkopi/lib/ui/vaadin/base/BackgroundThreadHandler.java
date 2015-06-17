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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import org.kopi.vaadin.addons.StrategyUIExecutor;
import org.kopi.vaadin.addons.UIExecutor;
import org.kopi.vaadin.addons.UIRunnable;

import com.vaadin.ui.UI;

/**
 * Collects some utilities for background threads in a vaadin application.
 * <p>
 *   Note that all performed background tasks are followed by an client UI update
 *   using the push mechanism incorporated with vaadin.
 * </p> 
 * @see {@link UI#push()}
 */
public class BackgroundThreadHandler {

  /**
   * Starts a task asynchronously and blocks the current thread. The lock will be released
   * if a notify signal is send to the blocking object.
   * @param runnable The task to be run.
   * @param lock The lock object.
   * @see {@link UI#access(Runnable)}
   */
  public static void startAndWait(final Runnable runnable, final Object lock) {
    access(runnable);
    synchronized (lock) {
      try { 
	lock.wait();
      } catch (InterruptedException e) {
	e.printStackTrace();
      } 
    }
  }
  
  /**
   * Access the UI to pending tasks to perform some updates.
   * @param runnable The runnable to be run.
   */
  public static void access(final Runnable runnable) {
    executor.access(runnable);
  }
  
  /**
   * Starts a task asynchronously. This will lock the current session and all concurrent sessions.
   * @param runnable The task to be run.
   * Deprecated cause it is a source of a session interference.
   */
  @Deprecated
  public static void start(final Runnable runnable) {
    executor.executeAndAccess(new UIRunnable() {
      
      @Override
      public void runInUI(Throwable ex) {
	UI.getCurrent().push();
      }
      
      @Override
      public void runInBackground() {
	runnable.run();
      }
    });
  }
  
  /**
   * Starts a task synchronously. This will lock the current session.
   * @param runnable The task to be run.
   * @see {@link UI#accessSynchronously(Runnable)}
   */
  public static void startSynchronously(final Runnable runnable) {
    executor.accessSynchronously(runnable);
  }
  
  /**
   * Sends all pending events to client side and update
   * UI.
   */
  public static void updateUI() {
    executor.access(new Runnable() {
      
      @Override
      public void run() {
        UI.getCurrent().push();
      }
    });
  }
  
  /**
   * Releases the lock based on an object.
   * @param lock The lock object.
   */
  public static void releaseLock(Object lock) {
    synchronized (lock) {
      lock.notifyAll(); 
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private static UIExecutor 			executor;
  
  static {
    executor = new StrategyUIExecutor();
  }
}
