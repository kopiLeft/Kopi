/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * To represent a subroutine in the CFG.
 * Keep the start and the end blocks of the subroutines.
 * Keep each pair of block calling / block return of the subroutine.
 */
public class SubRoutine {

  // -------------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------------

  /**
   * Construct the subroutine with the start and end blocks of the
   * subroutine.
   *
   * @param start start block
   * @param end end block
   */
  public SubRoutine(BasicBlock start, BasicBlock end) {
    this.start = start;
    this.end = end;
    this.pairs = new LinkedList();
  }

  // -------------------------------------------------------------------
  // ACCESSORS
  // -------------------------------------------------------------------

  /**
   * Add a pair block calling / block return to the subroutine.
   *
   * @param call the calling block
   * @param ret the returning block
   */
  public void addCall(Edge call, Edge ret) {
    pairs.add(new Edge[] {call, ret});
  }

  /**
   * Remove a call from the list of call
   *
   * @param call the calling block.
   */
  public void removeCall(Edge call) {
    ListIterator calls = pairs.listIterator();

    while (calls.hasNext()) {
      Edge[] edge = (Edge[]) calls.next();

      if (edge[0] == call) {
        calls.remove();
        edge[0].getSource().removeSuccessor(edge[0]);
        edge[0].getTarget().removePredecessor(edge[0]);
        edge[1].getSource().removeSuccessor(edge[1]);
        edge[1].getTarget().removePredecessor(edge[1]);
      }
    }
  }

  /**
   * Get a list of call (Edge[2]).
   */
  public Iterator getCalls() {
    return pairs.iterator();
  }

  /**
   * Return the start block of the subroutine
   *
   * @return the start block of the subroutine
   */
  public BasicBlock getStart() {
    return start;
  }

  /**
   * Return the end block of the subroutine
   *
   * @return the end block of the subroutine
   */
  public BasicBlock getEnd() {
    return end;
  }

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------

  protected LinkedList pairs;
  protected BasicBlock start;
  protected BasicBlock end;
}
