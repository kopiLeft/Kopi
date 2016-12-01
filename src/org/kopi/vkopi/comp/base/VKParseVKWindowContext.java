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

package org.kopi.vkopi.comp.base;


import org.kopi.kopi.comp.kjc.CParseClassContext;
import org.kopi.kopi.comp.kjc.CParseCompilationUnitContext;

public class VKParseVKWindowContext extends VKParseContext {

  protected VKParseVKWindowContext(VKEnvironment environment) {
    cUnitContext = new CParseCompilationUnitContext();
    classContext = new CParseClassContext();
    definitionCollector = new VKDefinitionCollector(environment.getInsertDirectories());
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (ADD)
  // ----------------------------------------------------------------------

  // ----------------------------------------------------------------------
  // ACCESSORS (GET)
  // ----------------------------------------------------------------------

  public CParseCompilationUnitContext getCompilationUnitContext() {
    return cUnitContext;
  }

  public CParseClassContext getClassContext() {
    return classContext;
  }

  public VKDefinitionCollector getDefinitionCollector() {
    return definitionCollector;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CParseCompilationUnitContext	cUnitContext;
  private CParseClassContext		classContext;
  private VKDefinitionCollector		definitionCollector;
}
