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

package org.kopi.xkopi.comp.sqlc;

/**
 * This class represents a reference to an outer join
 */
public class SubOuterJoin {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param     type            the join type
   * @param     table           the joined table reference
   * @param     joinPred        the join pred.
   */
  public SubOuterJoin(String type,
                      SimpleTableReference table,
                      JoinPred joinPred)
  { 
    this.table = table;
    this.type = type;
    this.joinPred = joinPred;
  }

  public SimpleTableReference getTable() {
    return table;
  }
  
  public String getType() {
    return type;
  }

  public JoinPred getJoinPred() {
    return joinPred;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private SimpleTableReference          table;
  private String                        type;
  private JoinPred                      joinPred;
}
