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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.form;

import java.io.Serializable;

/**
 * Constraints for block position.
 */
@SuppressWarnings("serial")
public class BlockComponentData implements Serializable {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Default constructor.
   */
  public BlockComponentData() {}
  
  /**
   * Creates a new block constraint.
   * @param isFollow is it a follow block ?
   * @param isChart is it a chart block ?
   * @param page the page index
   */
  public BlockComponentData(boolean isFollow, boolean isChart, int page) {
    this.isFollow = isFollow;
    this.isChart = isChart;
    this.page = page;
  }
  
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  /**
   * Is it a follow block ?
   */
  public boolean			isFollow;

  /**
   * Is it a chart block ?
   */
  public boolean			isChart;
  
  /**
   * The block page index.
   */
  public int				page;
}
