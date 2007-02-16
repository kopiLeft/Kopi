/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

/**
 * An edge in the control flow graph.
 *
 * Edges are typed.
 *
 *
 * @author Michael Fernandez
 */
public class CFGEdge implements Edge {

  // -------------------------------------------------------------------
  // EDGE TYPES
  // -------------------------------------------------------------------

  public static final int DEFAULT_EDGE = 0;
  public static final int CONDITION_EDGE = 1;
  public static final int SWITCH_EDGE = 2;
  public static final int EXCEPTION_EDGE = 3;
  public static final int SUBROUTINE_EDGE = 4;
  public static final int CFG_EDGE = 5;

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct an edge
   */
  public CFGEdge() {
    this(0);
  }

  /**
   * Construct an edge with a given type
   *
   * @param type the type of the edge.
   */
  public CFGEdge(int type) {
    this.type = type;
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the type of the edge
   *
   * @return the user type
   */
  public int getType() {
    return type;
  }

  /**
   * Get the origin of the edge
   *
   * @return the origin of the edge
   */
  public Node getSource() {
    return source;
  }

  /**
   * Set the origin of the edge
   *
   * @param source the origin of the edge
   */
  public void setSource(Node newSource) {
    source = newSource;
  }


  /**
   * Get the destination of the edge
   *
   * @return the destination of the edge
   */
  public Node getTarget() {
    return target;
  }

  /**
   * Set the destination of the edge
   *
   * @param newTarget the new destination of the edge
   */
  public void setTarget(Node newTarget) {
    target = newTarget;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected int type;
  protected Node source;
  protected Node target;

}
