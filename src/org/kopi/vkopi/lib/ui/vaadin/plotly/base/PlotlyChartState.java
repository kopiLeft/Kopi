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

package org.kopi.vkopi.lib.ui.vaadin.plotly.base;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * The plotly chart state.
 */
@SuppressWarnings("serial")
public class PlotlyChartState extends JavaScriptComponentState {

  /**
   * The DOM ID to be used to identify the chart in the DOM tree.
   */
  public String domId;
  
  /**
   * The chart data configuration.
   */
  public String data;
  
  /**
   * The chart layout configuration.
   */
  public String layout;
}
