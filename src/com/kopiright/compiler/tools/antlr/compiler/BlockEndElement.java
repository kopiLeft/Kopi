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

package com.kopiright.compiler.tools.antlr.compiler;

/**
 * All alternative blocks are "terminated" by BlockEndElements unless
 * they are rule blocks (in which case they use RuleEndElement).
 */
class BlockEndElement extends AlternativeElement {
  protected boolean[] lock;	// for analysis; used to avoid infinite loops
  protected AlternativeBlock block;// ending blocks know what block they terminate


  public BlockEndElement(Grammar g) {
    super(g);
    lock = new boolean[g.maxk+1];
  }
  public Lookahead look(int k) {
    return grammar.theLLkAnalyzer.look(k, this);
  }
  public String toString() {
    //return " [BlkEnd]";
    return "";
  }
}
