/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
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

package at.dms.xkopi.comp.sqlc;

import java.util.ArrayList;

import at.dms.compiler.base.PositionedError;
import at.dms.kopi.comp.kjc.CTypeContext;

/**
 * This class allows to provide a context during SQL checking
 * but nothing is done in this package
 */
public interface SqlContext {
  /**
   * Returns the table reference with alias "alias"
   */
  TableReference getTableFromAlias(String alias);

  /**
   * Returns all tables defined in current context
   */
  ArrayList getTables();

  /**
   * Returns the parent context
   */
  SqlContext getParentContext();
  /**
   * Returns the type context
   */
  CTypeContext getTypeContext();

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
   */
  void reportTrouble(PositionedError trouble);
}
