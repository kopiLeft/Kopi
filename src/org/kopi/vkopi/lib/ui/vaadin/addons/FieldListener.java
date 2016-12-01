/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.Serializable;

/**
 * The field listener that notifies registered objects with
 * actions performed on a field.
 */
public interface FieldListener extends Serializable {

  /**
   * Fired when increment button is clicked.
   */
  public void onIncrement();
  
  /***
   * Fired when decrement action is fired.
   */
  public void onDecrement();

  /**
   * Fired when the field is clicked.
   */
  public void onClick();
  
  /**
   * Transfers the focus to this field.
   */
  public void transferFocus();
  
  /**
   * Navigates to the next field.
   */
  public void gotoNextField();

  /**
   * Navigates to the previous field.
   */
  public void gotoPrevField();

  /**
   * Navigates to the next empty must fill field.
   */
  public void gotoNextEmptyMustfill();
  
  /**
   * Navigates to the next record.
   */
  public void gotoNextRecord();
  
  /**
   * Navigates to the previous record.
   */
  public void gotoPrevRecord();
  
  /**
   * Navigates to the first record.
   */
  public void gotoFirstRecord();
  
  /**
   * Navigates to the last record.
   */
  public void gotoLastRecord();
}
