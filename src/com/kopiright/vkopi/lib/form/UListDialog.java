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

import com.kopiright.vkopi.lib.ui.base.UComponent;
import com.kopiright.vkopi.lib.visual.UWindow;

/**
 * {@code UListDialog} is the top level interface for all list dialogs ui components.
 */
public interface UListDialog extends UComponent {

  /**
   * Shows The {@code UListDialog} with a window and a field reference.
   * @param window The caller {@link UWindow}
   * @param field The {@link UField} reference.
   * @param showSingleEntry Show the {@code UListDialog} when it contains only a single entry ?
   * @return The selected element id.
   */
  public int selectFromDialog(UWindow window, UField field, boolean showSingleEntry);

  /**
   * Shows The {@code UListDialog} with a window reference.
   * @param window The reference {@link UWindow}.
   * @param showSingleEntry Show the {@code UListDialog} when it contains only a single entry ?
   * @return The selected element id.
   */
  public int selectFromDialog(UWindow window, boolean showSingleEntry);
}
