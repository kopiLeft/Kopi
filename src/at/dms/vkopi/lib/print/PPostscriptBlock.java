/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: PPostscriptBlock.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.print;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PPostscriptBlock extends PBlock {

  /**
   *
   */
  public PPostscriptBlock(String ident, PPosition pos, PSize size, String data) {
    super(ident, pos, size, null);
    InputStream	file = at.dms.vkopi.lib.visual.Utils.getFile(data);
    if (file != null) {
      BufferedReader i = new BufferedReader(new InputStreamReader(file));

      try {
	StringBuffer buf = new StringBuffer();
	String s;
	while ((s = i.readLine()) != null) {
	  buf.append(s + "\n");
	}
	ps = buf.toString();
      } catch (java.io.IOException io) {
	ps = null;
      }
    }
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
    page.getPostscriptStream().checkCachedInfos();
    page.getPostscriptStream().println(ps);
  }

  /**
   * Returns true if this block is fully printed
   */
  public boolean isFullyPrinted() {
    return true;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private	String		ps;
}
