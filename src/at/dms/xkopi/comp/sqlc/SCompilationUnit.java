/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: SCompilationUnit.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.sqlc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;

/**
 * This class represents a virtual file and is the main entry point in
 * kopi grammar
 */
public class SCompilationUnit {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public SCompilationUnit(TokenReference ref) {
    this.ref = ref;
    elems = new ArrayList();
  }

  // ----------------------------------------------------------------------
  // TREE COLLECTING
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void addStmt(Statement stmt) {
    elems.add(stmt);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * This method provide a semantics check after parsing
   * Some optimization may be done here
   */
  public void check() {
    // NO SEMANTIC CHECK HERE
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * This is the backend of our compiler, we expect to generate byte code here
   * @param p the buffered printwriter on which we write code
   */
  public void genSql() throws PositionedError {
    try {
      SqlcPrettyPrinter         spp;

      spp = new SqlcPrettyPrinter(ref.getFile());
      spp.printCUnit(elems);
      spp.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.err.println("cannot write: " + ref.getFile());
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private List                  elems;
  private TokenReference        ref;
}
