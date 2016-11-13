/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.compiler.tools.antlr.compiler;

class JavaBlockFinishingInfo {
  String postscript;		// what to generate to terminate block
  boolean generatedSwitch;// did block finish with "default:" of switch?
  boolean generatedAnIf;

  /**
   * When generating an if or switch, end-of-token lookahead sets
   *  will become the else or default clause, don't generate an
   *  error clause in this case.
   */
  boolean needAnErrorClause;


  public JavaBlockFinishingInfo() {
    postscript=null;
    generatedSwitch = false;
    needAnErrorClause = true;
  }
  public JavaBlockFinishingInfo(String ps, boolean genS, boolean generatedAnIf, boolean n) {
    postscript = ps;
    generatedSwitch = genS;
    this.generatedAnIf = generatedAnIf;
    needAnErrorClause = n;
  }
}
