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

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;

public class PPostscriptBlock extends PBlock {
  Image img;
  /**
   *
   */
  public PPostscriptBlock(String ident, PPosition pos, PSize size, String data) {
    super(ident, pos, size, null);

//     try {
//       System.out.println(data + "  "+at.dms.vkopi.lib.visual.Utils.getURLFromResource(data)+"  "+at.dms.vkopi.lib.visual.Utils.getURLFromResource(data, at.dms.vkopi.lib.visual.Utils.APPLICATION_DIR));

//       img = Image.getInstance("/tmp/asd");//BriefpapierSW.EPS");//at.dms.vkopi.lib.visual.Utils.getURLFromResource(data, at.dms.vkopi.lib.visual.Utils.APPLICATION_DIR));
//     } catch (Exception e) {
//       e.printStackTrace();
//     }

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
//     try {
//       img.setAbsolutePosition(getPosition().getX(), page.getDocument().getPageSize().height()-getPosition().getY());
//       page.getDocument().add(img);
//     } catch (Exception e) {
//       e.printStackTrace();
//     }
    
//     page.getPostscriptStream().checkCachedInfos();
//     page.getPostscriptStream().println(ps);
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
