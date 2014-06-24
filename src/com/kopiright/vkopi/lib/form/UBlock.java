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

import com.kopiright.vkopi.lib.base.UComponent;

/**
 * {@code UBlock} is the top-level interface that must be implemented
 * by all kopi blocks. It is the visual component of the {@link VBlock} model.
 */
public interface UBlock extends UComponent, BlockListener {

  /**
   * Returns the block model
   * @return The {@link VBlock} of this {@code UBlock}
   */
  public VBlock getModel();

  /**
   * Returns the form view of the block
   * @return The {@link UForm} of this block.
   */
  public UForm getFormView();

  /**
   * Returns the displayed line for a record.
   * @param recno The concerned record.
   * @return The displayed line.
   */
  public int getDisplayLine(int recno);

  /**
   * Returns the displayed line for the active record.
   * @return The displayed line for the active record.
   */
  public int getDisplayLine();

  /**
   * Returns the record number from display of a given line.
   * @param line The concerned line.
   * @return The record number.
   */
  public int getRecordFromDisplayLine(int line);

  /**
   * Adds a component to the block view with a corresponding constraints.
   * @param comp The {@link UComponent} to be added
   * @param constraints The {@link KopiAlignment} constraints
   */
  public void add(UComponent comp, KopiAlignment constraints);

  /**
   * Returns the position of a given column.
   * @param x The column number.
   * @return The position of a given column.
   */
  public int getColumnPos(int x);

  /**
   * Checks if the block is in detail mode.
   * @return {@code true} if the block is in detail mode
   */
  public boolean inDetailMode();
}
