/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.bytecode.ssa;

import java.util.Iterator;

/**
 * A node in the dominator tree.
 * This node is associed with a node of the original graph.
 */
public class DominatorTreeNode extends Node {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a node linked to the node in the original graph.
   *
   * @param index the index of the node
   * @param node the node in the original graph.
   */
  public DominatorTreeNode(int index, Node node) {
    super();
    this.index1 = index;
    this.graphNode = node;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Return the node in the original graph
   *
   * @return node in the original graph.
   */
  public Node getNode() {
    return graphNode;
  }

  /**
   * Return the index of the node
   *
   * @return index of the node
   */
  public int getIndex() {
    return index1;
  }

  /**
   * Test if a children of this node has i as index
   *
   * @param i index searched
   */
  public boolean hasChildIndex(int i) {
    Iterator succ = getSuccessors();
    while (succ.hasNext()) {
      DominatorTreeNode s = (DominatorTreeNode) succ.next();
      if (s.getIndex() == i) {
        return true;
      }
    }
    return false;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected int index1;
  protected Node graphNode;
}
