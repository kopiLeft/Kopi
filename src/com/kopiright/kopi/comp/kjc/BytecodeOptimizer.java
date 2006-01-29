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

package com.kopiright.kopi.comp.kjc;

import com.kopiright.bytecode.classfile.CodeInfo;
import com.kopiright.bytecode.classfile.MethodInfo;

/**
 * This class encapsulates the bytecode optimization calls.
 * Depending on the requested optimization level, it will choose
 * the optimizer.
 */
public class BytecodeOptimizer {

  /**
   * Creates an optimizer.
   *
   * @param	level		the requested optimization level
   */
  public BytecodeOptimizer(int level) {
    this.level = level;
  }

  /**
   * Runs the optimizer on the given code info.
   *
   * @param	info		the code to optimize.
   */
  public MethodInfo run(MethodInfo info) {
    if (level >= 10) {
      return com.kopiright.bytecode.ssa.Optimizer.optimize(info);
    } else if (level >= 1) {
      CodeInfo		code;

      code = info.getCodeInfo();
      if (code != null) {
        code = com.kopiright.bytecode.optimize.Optimizer.optimize(code, level);
        info.setCodeInfo(code);
      }
      return info;
    } else {
      return info;
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  /**
   * The requested optimization level.
   */
  private int			level;
}
