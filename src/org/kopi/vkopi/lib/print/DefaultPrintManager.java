/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.print;

import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.Printer;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VWindow;

public class DefaultPrintManager implements PrintManager {
  
  /**
   * Handle printing
   * @param	parent	the form that initiate the printing process
   * @param	report	the report to print
   * @param	printer	an optional default printer
   * @param	fax	an optional default fax number
   * @param	mail	an optional default mail address
   */
  public void print(VWindow parent,
		    Printable report,
		    int copies,
		    Printer printer,
		    String fax,
		    String mail)
    throws VException
  {
    try {
      report.createPrintJob();
    } catch (PrintException exc) {
      throw new VExecFailedException(exc.getMessage());
    }
  }

  public static void setPrintManager(PrintManager printCopies) {
    ApplicationContext.getApplicationContext().getApplication().setPrintManager(printCopies);
  }

  public static PrintManager getPrintManager() {
    if (ApplicationContext.getApplicationContext().getApplication().getPrintManager() == null) {
      return new DefaultPrintManager();
    }
    
    return ApplicationContext.getApplicationContext().getApplication().getPrintManager();
  }
}
