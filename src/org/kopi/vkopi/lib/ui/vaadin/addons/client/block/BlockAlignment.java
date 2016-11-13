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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.io.Serializable;

import com.vaadin.shared.Connector;

/**
 * The block alignment info.
 */
@SuppressWarnings("serial")
public class BlockAlignment implements Serializable {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Default constructor needed.
   */
  public BlockAlignment() {}
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Returns <code>true</code> is there is an alignment for the given column.
   * @param x The column index.
   * @return <code>true</code> is there is an alignment for the given column.
   */
  public boolean isAligned(int x) {
    x--;
    if (x >= 0 && x < targets.length && targets[x] != -1) {
      return true;
    }
    return false;
  }

  /**
   * Returns the target position for for a given column. 
   * @param x The column index.
   * @return The target position for for a given column. 
   */
  public int getTargetAt(int x) {
    if (x < 0 || x >= targets.length) {
      return -1;
    } else {
      return targets[x];
    }
  }
  
  /**
   * Returns the target position of the block alignment.
   * @param x The aligned column index.
   * @return The target position of the block alignment.
   */
  protected int getTargetPos(int x) {
    // block alignment
    if ((x % 2 == 1) && isChart && isAligned(x / 2 + 1)) {
      return getFieldTargetPos(x / 2 + 1);
    } else if (!isChart) {
      // alignment if block is not a chart
      if (x % 2 == 1) {
	// fields
	return getFieldTargetPos(x / 2 + 1);
      } else {
	// labels
	return getLabelTargetPos(x / 2 + 1);
      }
    }
    
    return 0;
  }
  
  /**
   * Returns the target position of the block alignment.
   * @param x The aligned column index.
   * @return The target position of the block alignment.
   */
  protected int getFieldTargetPos(int x) {
    int         	target;

    x--; // we want to align middle
    target = getTargetAt(x);

    // if (x >= 0 && x < targets.length && targets[x] != -1) {
    if (target != -1) {
      if (ori == null) {
	return 0;
      }
      
      return isChart ? target : target * 2 + 1;
    }
    
    return 0;
  }
  
  /**
   * Returns the label target position.
   * @param x The column position.
   * @return The label target position.
   */
  protected int getLabelTargetPos(int x) {
    int         	target;
    
    x--; // we want to align middle
    target = getTargetAt(x);

    // if (x >= 0 && x < targets.length && targets[x] != -1) {
    if (target != -1) {
      if (ori == null) {
	return 0;
      }
      
      return 2 * target;
    }
    
    return 0;
  }
  
  /**
   * Returns the original block connector
   * @return The original block connector
   */
  protected BlockConnector getBlock() {
    return (BlockConnector)ori;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  public int[]				targets;
  public boolean 			isChart;
  public Connector			ori;
}
