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

import com.kopiright.vkopi.lib.ui.base.UComponent;

/**
 * {@code UWindow} is the top-level interface that must be implemented
 * by all kopi windows. It is the visual component of the {@link VWindow} model.
 */
public interface UWindow extends UComponent, VActionListener, ModelCloseListener, WaitDialogListener, WaitInfoListener, ProgressDialogListener {

  /**
   * Returns the {@link VWindow} model of this UI component.
   * @return the {@code UWindow} model.
   */
  public VWindow getModel();

  /**
   * Sets the {@code UWindow} title.
   * @param title the window title.
   */
  public void setTitle(String title);

  /**
   * Sets the {@code UWindow} information text.
   * @param text The window information text.
   */
  public void setInformationText(String text);

  /**
   * Sets the {@code UWindow} total jobs.
   * @param totalJobs The window total jobs.
   */
  public void setTotalJobs(int totalJobs);

  /**
   * Sets the {@code UWindow} current job.
   * @param totalJobs The window current job.
   */
  public void setCurrentJob(int currentJob);

  /**
   * Sets the wait message.
   * @param message The wait message
   */
  public void updateWaitDialogMessage(String message);

  /**
   * Closes the {@code UWindow}
   */
  public void closeWindow();

  /**
   * Sets the {@code UWindow} focus enable state
   * @param enabled  boolean value specifying if the window focus should be enabled or not.
   */
  public void setWindowFocusEnabled(boolean enabled);

  /**
   * Performs the appropriate action synchronously.
   * @param action The {@link KopiAction} to be executed
   * @see VActionListener#performAsyncAction(KopiAction)
   */
  public void performBasicAction(KopiAction action);
}
