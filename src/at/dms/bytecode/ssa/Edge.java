/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: Edge.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.bytecode.ssa;

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
