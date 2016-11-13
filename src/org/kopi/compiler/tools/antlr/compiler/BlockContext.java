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

package org.kopi.compiler.tools.antlr.compiler;

/**
 * BlockContext stores the information needed when creating an
 * alternative (list of elements).  Entering a subrule requires
 * that we save this state as each block of alternatives
 * requires state such as "tail of current alternative."
 */
class BlockContext {
  AlternativeBlock block; // current block of alternatives
  int altNum;				// which alt are we accepting 0..n-1
  BlockEndElement blockEnd; // used if nested


  public void addAlternativeElement(AlternativeElement e) {
    currentAlt().addElement(e);
  }
  public Alternative currentAlt() {
    return (Alternative)block.alternatives.elementAt(altNum);
  }
  public AlternativeElement currentElement() {
    return currentAlt().tail;
  }
}
