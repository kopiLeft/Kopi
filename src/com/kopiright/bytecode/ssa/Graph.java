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

import java.util.Vector;
import java.util.Iterator;

/**
 * Represent a graph.
 *
 * A graph contain graph nodes (GraphNode class)
 */
public class Graph {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct a new graph
   */
  public Graph() {
    nodes = new Vector();
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Add a node in the graph
   *
   * @param node node to add
   */
  public void addNode(Node node) {
    nodes.addElement(node);
  }

  /**
   * remove a node from the graph
   *
   * @param node node to remove
   */
  public void removeNode(Node node) {
    nodes.removeElement(node);
  }

  /**
   * Return an iterator of all nodes in the graph
   *
   * @return a nodes iterator
   */
  public Iterator getNodesIterator() {
    return nodes.iterator();
  }

  /**
   * Return an array containing all node of the graph
   *
   * @return an array containing all node of the graph
   */
  public Node[] getNodes() {
    Node[] nodeArray = new Node[nodes.size()];
    nodes.toArray(nodeArray);
    return nodeArray;
  }

  /**
   * Set the correct index of all nodes in the array of nodes
   */
  public void setNodesIndex() {
    for (int i = 0; i < nodes.size(); ++i) {
      ((Node) nodes.elementAt(i)).setIndex(i);
    }
  }

  /**
   * Visit all the graph in depth first search
   * Begin the exploration by start node.
   *
   * @param start the virst node to visit.
   * @param nodeVisitor the node visitor
   */
  public void visitGraph(Node start, NodeVisitor nodeVisitor) {
    Iterator it = nodes.iterator();
    while (it.hasNext()) {
      Node n = (Node) it.next();
      n.setMarked(false);
    }
    visitNode(start, nodeVisitor);
    it = nodes.iterator();
    while (it.hasNext()) {
      Node n = (Node) it.next();
      if (!n.getMarked()) {
        visitNode(n, nodeVisitor);
      }
    }
  }

  /**
   * Visit a graph in depth first search from a given node
   *
   * @param node the search source
   * @param nodeVisitor the node visitor
   */
  public void visitGraphFromNode(Node start, NodeVisitor nodeVisitor) {
    Iterator it = nodes.iterator();
    while (it.hasNext()) {
      Node n = (Node) it.next();
      n.setMarked(false);
    }
    visitNode(start, nodeVisitor);
  }

  /**
   * Visit a graph in depth first search from a given node
   *
   * @param node the search source
   * @param nodeVisitor the node visitor
   */
  private boolean visitNode(Node node, NodeVisitor nodeVisitor) {
    node.setMarked(true);
    if (!nodeVisitor.visit(node)) {
      return false;
    } else {
      Iterator it = node.getSuccessors();
      while (it.hasNext()) {
        Node n = (Node) it.next();
        if (!n.getMarked()) {
          if (!visitNode(n, nodeVisitor)) {
            return false;
          }
        }
      }
      return true;
    }
  }

  /**
   * Get the number of nodes in the graph
   */
  public int size() {
    return nodes.size();
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected Vector nodes;
}
