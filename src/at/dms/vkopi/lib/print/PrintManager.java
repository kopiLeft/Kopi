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
 * $Id: PrintManager.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package	at.dms.vkopi.lib.print;

import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VWindow;
import at.dms.vkopi.lib.util.Printer;

public interface PrintManager {
  /**
   * Handle printing
   * @param	parent	the form that initiate the printing process
   * @param	report	the report to print
   * @param	printer	an optional default printer
   * @param	fax	an optional default fax number
   * @param	mail	an optional default mail address
   */
  void print(VWindow parent,
	     Printable report,
	     int copies,
	     Printer printer,
	     String fax,
	     String mail) throws VException;
}
