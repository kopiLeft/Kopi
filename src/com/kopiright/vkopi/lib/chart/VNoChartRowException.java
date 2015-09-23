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

package com.kopiright.vkopi.lib.chart;

import com.kopiright.vkopi.lib.visual.VRuntimeException;

/**
 * Visual exception thrown when a chart did not contain any values
 * to be displayed. This will notify the user that no data was found
 * for the chart content.
 */
@SuppressWarnings("serial")
public class VNoChartRowException extends VRuntimeException {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------
  
  /**
   * Creates a new no row exception.
   * @param message The exception message.
   */
  public VNoChartRowException(String message) {
    super(message);
  }
  
  /**
   * Creates a new no row exception.
   * @param exec The exception cause.
   */
  public VNoChartRowException(Throwable exc) {
    super(exc);
  }

  /**
   * Creates a new no row exception.
   * @param msg The exception message.
   * @param exc The exception cause.
   */
  public VNoChartRowException(String msg, Throwable exc) {
    super(msg, exc);
  }

  /**
   * Creates a new no row exception.
   */
  public VNoChartRowException() {
    super();
  }
}
