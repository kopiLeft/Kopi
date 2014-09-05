/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
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

package com.kopiright.vkopi.lib.ui.swing.visual;

import java.io.IOException;

import com.kopiright.vkopi.lib.preview.VPreviewWindow;
import com.kopiright.vkopi.lib.print.PSPrintException;
import com.kopiright.vkopi.lib.util.AbstractPrinter;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.PreviewRunner;
import com.kopiright.vkopi.lib.visual.VException;

public class JPreviewRunner implements PreviewRunner {

  @Override
  public void run(PrintJob data, String command) throws IOException, PrintException {
    if (ApplicationConfiguration.getConfiguration().useAcroread()) {
      try {
	if (System.getProperty("os.name").startsWith("Linux")) {
	  Runtime.getRuntime().exec("acroread " + data.getDataFile());
	} else if (System.getProperty("os.name").startsWith("Windows")) {
	  Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " +  data.getDataFile());
	}
      } catch (IOException e) {
	System.out.println("Acroread failed: " + e.getMessage());
      }
    } else {
      try {
	new VPreviewWindow().preview((data.getDataType() != PrintJob.DAT_PS) ? data : AbstractPrinter.convertToGhostscript(data), command);
      } catch (VException e) {
	throw new PSPrintException("PreviewPrinter.PrintTaskImpl::print()", e);
      }
    }
  }
}
