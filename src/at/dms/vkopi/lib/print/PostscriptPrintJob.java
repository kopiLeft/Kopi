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
 * $Id: PostscriptPrintJob.java,v 1.3 2004/12/17 18:44:15 lackner Exp $
 */

package at.dms.vkopi.lib.print;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.PrintJob;

/**
 * PPage/Report creates a PrintJob
 *
 * A Printer prints PrintJob 
 */
public class PostscriptPrintJob extends PrintJob {
  public PostscriptPrintJob() throws IOException {
    super();
    try {
      postscript =  new PPostscriptStream(new PrintStream(getOutputStream(), true, "ISO-8859-15"));
    } catch (UnsupportedEncodingException e) {
      closed = true;
      throw new InconsistencyException(e);
    }
    closed = false;
  }

  public PPostscriptStream getPostscriptStream() throws PSPrintException {
    if (closed) {
      throw new PSPrintException("Postscript-stream is already closed");
    }
    return postscript;
  }

  public void close() throws PSPrintException {
    if (!closed) {
      closed = true;
      postscript.close(getNumberOfPages());
    } else {
      throw new PSPrintException("Postscript-stream is already closed");
    }
  }

  private PPostscriptStream     postscript;
  private boolean               closed;
}
