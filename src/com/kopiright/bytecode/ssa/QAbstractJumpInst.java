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

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public abstract class QAbstractJumpInst extends QInst {
  /**
   * Simplify all consecutive jumps
   */
  public abstract void simplifyAllJumps();

  /**
   * Try to simplify consecutive jumps.
   *
   * @param dest the edge of the jump destination.
   */
  protected void simplifyJump(Edge dest) {
    BasicBlock  source = (BasicBlock) dest.getSource();
    Set         sourceExceptionNextBlocks = source.getExceptionNextBlocks();
    BasicBlock  nextBB = (BasicBlock) dest.getTarget();
    Set         blocks = new HashSet();

    while (nextBB.isEmpty()) {
      //the destination block contain only a jump
      QJump jump = nextBB.getJump();

      if (jump != null) {
        Set removedBlockExceptionNextBlocks = nextBB.getExceptionNextBlocks();
        nextBB = (BasicBlock) jump.getEdge().getTarget();
        if (blocks.contains(nextBB)) {
          break;
        }
        blocks.add(nextBB);
        dest.setTarget(nextBB);

        // if the removed block was linked to catch blocks,
        // the source block must alos be linked to these blocks.
        Iterator it = removedBlockExceptionNextBlocks.iterator();
        while (it.hasNext()) {
          BasicBlock catchBlock = (BasicBlock) it.next();
          if (! sourceExceptionNextBlocks.contains(catchBlock)) {
            source.addExceptionNextBlock(catchBlock);
            sourceExceptionNextBlocks.add(catchBlock);
          }
        }
      } else {
        break;
      }
    }
  }
}
