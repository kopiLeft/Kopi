/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.bytecode.ssa;

/**
 * Interface for an edge in a graph.
 *
 * @author Michael Fernandez
 */
public interface Edge {

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Get the origin of the edge
   *
   * @return the origin of the edge
   */
  Node getSource();

  /**
   * Set the origin of the edge
   *
   * @return the origin of the edge
   */
  void setSource(Node newSource);

  /**
   * Get the target of the edge
   *
   * @return the target of the edge
   */
  Node getTarget();

  /**
   * Set the target of the edge
   *
   * @param newSon the new target of the edge
   */
  void setTarget(Node newTarget);
}
