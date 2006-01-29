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

package com.kopiright.bytecode.ssa;

import com.kopiright.bytecode.classfile.ClassInfo;
import com.kopiright.bytecode.classfile.CodeInfo;
import com.kopiright.bytecode.classfile.MethodInfo;

/**
 * This class is the entry point for the optimizer.
 */
public class Optimizer {

  /**
   * Reads, optimizes and writes a class file
   * @exception UnpositionedError       an error occurred
   */
  public static void optimizeClass(ClassInfo info)  {
    MethodInfo[]        methods;

    methods = info.getMethods();
    for (int i = 0; i < methods.length; i++) {
      CodeInfo          code;

      code = methods[i].getCodeInfo();
      if (code != null) {
        MethodOptimizer methOptim = new MethodOptimizer(methods[i], code);
        code = methOptim.generateCode();
        methods[i].setCodeInfo(code);
      }
    }
  }
  /**
   * Reads, optimizes and writes a class file
   * @exception UnpositionedError       an error occurred
   */
  public static MethodInfo optimize(MethodInfo info)  {
    CodeInfo            code;

    code = info.getCodeInfo();
    if (code != null) {
      MethodOptimizer methOptim = new MethodOptimizer(info, code);

      code = methOptim.generateCode();
      info.setCodeInfo(code);
    }
    return info;
  }
}
