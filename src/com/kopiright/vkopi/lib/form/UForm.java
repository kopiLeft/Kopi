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

package com.kopiright.vkopi.lib.form;

import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.DPositionPanelListener;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.kopiright.vkopi.lib.visual.VException;

/**
 * {@code UForm} is the top-level interface that must be implemented
 * by all kopi forms. It is the visual component of the {@link VForm} model.
 */
public interface UForm extends UWindow, DPositionPanelListener, FormListener {

  /**
   * Returns the block view of a given {@link UBlock} model.
   * @param block the {@link VBlock} model.
   * @return The {@link UBlock} view.
   */
  public UBlock getBlockView(VBlock block);

  /**
   * Returns the print job of the form view.
   * @return The {@link PrintJob} of this {@code UForm}
   * @throws VException operation may fail
   */
  public PrintJob printForm() throws VException;

  /**
   * Prepares the snapshot.
   */
  public void printSnapshot();

  /**
   * Returns the Debug throwable info
   * @return The {@link Throwable} debug info.
   */
  public Throwable getRuntimeDebugInfo();
}
