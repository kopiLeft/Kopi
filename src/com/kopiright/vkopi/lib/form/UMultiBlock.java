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
import com.kopiright.vkopi.lib.visual.VException;

/**
 * {@code UMultiBlock} is the top-level interface that represents a double layout
 * block.
 * {@code UMultiBlock} is a both chart block and simple block with the possibility
 * to switch between the two layouts.
 */
public interface UMultiBlock extends UBlock {

  /**
   * Switches view between list and detail mode.
   * @param row The selected record.
   * @throws VException Switch operation may fail.
   */
  public void switchView(int row) throws VException;

  /**
   * Adds a component to the detail view. The add to the chart block
   * is ensured by {@link #add(UComponent, KopiAlignment)}.
   * @param comp The {@link UComponent} to be added to detail view.
   * @param constraint The {@link KopiAlignment} component constraints.
   */
  public void addToDetail(UComponent comp, KopiAlignment constraint);
}
