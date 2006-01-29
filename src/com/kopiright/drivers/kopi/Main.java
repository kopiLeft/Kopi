/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.drivers.kopi;

import java.lang.reflect.Method;

/**
 * Wrapper class for all KOPI software.
 *
 * @author Edouard G. Parmelan <edouard.parmelan@quadratec.fr>
 */
public class Main {
  public static void main(String[] args) {
    try {
      /* Get class of command to invoke */
      Class		commandClass = Class.forName("com.kopiright." + args[0] + ".Main");

      /* Get method with signature main(String[])V */
      Method		commandMain = commandClass.getDeclaredMethod("main", new Class[]{ String[].class });

      /* Build the invoke arguments */
      String[]		commandArgs = new String[args.length - 1];
      System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

      /* Invoke it */
      commandMain.invoke(null, new Object[] { commandArgs });
    } catch (Exception e) {
      com.kopiright.kopi.comp.kjc.Main.main(args);
    }
  }
}
