/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.print;

/**
 * A simple rectangle
 */
public class PRectangleBlock extends PBlock {

  /**
   * Constructs the block
   */
  public PRectangleBlock(String ident, PPosition pos, PSize size, String style) {
    super(ident, pos, size, style);
  }

  /**
   * Try to fill the maximum of space
   * Returns 0 if this block can't place a part of data in the proposed space
   */
  public float fill(float size) {
    if (size >= getSize().getHeight()) {
      return getSize().getHeight();
    } else {
      return 0;
    }
  }

  /**
   * Prints this block
   */
  public void doPrint(PPage page) throws PSPrintException {
    printStyle(page, getSize().getWidth(), getSize().getHeight());
  }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return true;
  }
}
