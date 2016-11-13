/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.date;

import java.util.Date;

/**
 * Helper class to inform the screen reader that the user changed the
 * selected date. It sets the value of a field that is outside the view, and
 * is defined as a live area. That way the screen reader recognizes the
 * change and reads it to the user.
 */
@SuppressWarnings("serial")
public class FocusedDate extends Date {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  @SuppressWarnings("deprecation")
  public FocusedDate(int year, int month, int date) {
    super(year, month, date);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public void setTime(long time) {
    super.setTime(time);
  }

  @Override
  @Deprecated
  public void setDate(int date) {
    super.setDate(date);
  }

  @Override
  @Deprecated
  public void setMonth(int month) {
    super.setMonth(month);
  }

  @Override
  @Deprecated
  public void setYear(int year) {
    super.setYear(year);
  }
}
