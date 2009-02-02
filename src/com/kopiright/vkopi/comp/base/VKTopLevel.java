/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.kopi.comp.kjc.JCompilationUnit;
import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.comp.base.VKEnvironment;

public class VKTopLevel extends com.kopiright.util.base.Utils {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VKTopLevel(Compiler compiler,
                    VKInsertParser parser,
                    VKEnvironment environment)
  {
    this.compiler = compiler;
    this.parser = parser;
    this.environment = environment;
  }

  // ----------------------------------------------------------------------
  //
  // ----------------------------------------------------------------------

  public List verifyFiles(List names) throws UnpositionedError {
    return compiler.verifyFiles(names);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return a collector from a file
   */
  public VKDefinitionCollector getCollector(File file) throws UnpositionedError {
    try {
      VKDefinitionCollector coll = (VKDefinitionCollector)table.get(file.getCanonicalPath());
      if (coll != null) {
	return coll;
      }
      VKInsert insert = parser.parseInsert(file, environment);
      if (insert == null) {
	if (!file.exists()) {
	  throw new UnpositionedError(BaseMessages.FILE_UNKNOWN, file);
	} else {
	  throw new UnpositionedError(BaseMessages.FILE_CANT_READ, file);
	}
      }
      inserts.addElement(insert);

      table.put(file.getCanonicalPath(), insert.getDefinitionCollector());
      return insert.getDefinitionCollector();
    } catch (Exception e) {
      e.printStackTrace();
      throw new InconsistencyException();
    }
  }

  // ----------------------------------------------------------------------
  // CHECK INTERFACE
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @param block	the actual context of analyse
   * @exception	PositionedError	Error catched as soon as possible
   */
  public JCompilationUnit[] genCUnits(Compiler compiler, VKCompilationUnit[] tree) {
    JCompilationUnit[]	cunits = new JCompilationUnit[tree.length + inserts.size()];
    for (int count = 0; count < tree.length; count++) {
      cunits[count] = tree[count].genCode(compiler);
    }
    for (int count = 0; count < inserts.size(); count++) {
      cunits[tree.length + count] = ((VKInsert)inserts.elementAt(count)).genCode(compiler);
    }

    return cunits;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final Compiler	compiler;
  private final VKInsertParser	parser;
  private Vector		inserts = new Vector();
  private Hashtable		table = new Hashtable();
  private final VKEnvironment   environment;
}
