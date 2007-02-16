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

package com.kopiright.vkopi.comp.base;

import java.util.StringTokenizer;

import com.kopiright.kopi.comp.kjc.ClassReader;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.xkopi.comp.xkjc.XKjcEnvironment;

import com.kopiright.vkopi.comp.base.VKOptions;

public class VKEnvironment extends XKjcEnvironment {

  public VKEnvironment(ClassReader classReader,
                       TypeFactory typeFactory,
                       VKOptions options)
  {
    super(classReader, typeFactory, options);
    if (options.insertDirectories != null) {
      StringTokenizer     token = new StringTokenizer(options.insertDirectories,
                                                      ":",
                                                      false);
      this.insertDirectories = new String[token.countTokens()];
      for (int i = 0; token.hasMoreTokens(); i++) {
        insertDirectories[i]  = token.nextToken();
      }
    } else {
      this.insertDirectories = new String[0];
    }
  }

  public String[] getInsertDirectories() {
    return insertDirectories;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBES
  // ----------------------------------------------------------------------

  private String[]    insertDirectories;

}
