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
 * $Id: Key.java 27892 2007-02-16 16:09:48Z graf $
 */

package com.kopiright.xkopi.comp.dbi;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TokenReference;

/**
 * This class represents a pragma for extra satatements for a table
 * definition
 */
public class Pragma extends Constraint {
  
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  
  /**
   * Constructor
   *
   * @param     ref             the token reference for this clause
   * @param     databaseType    in which the statement will be executed
   */
  public Pragma(TokenReference ref,
                String databaseType,
                String statement)
  {
    super(ref);
    
    this.databaseType = databaseType;
    this.statement = statement;
  }
  
  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------
  
  /**
   * Test this pragma database type against the target 
   *
   * @param     type    the target database type
   * @return    true if the type is a match
   */
  public boolean databaseIs(String type) {
    return databaseType.equals(type);
  }
  
  /**
   * @return    the database type.
   */
  public String getDatabaseType() {
    return databaseType;
  }

  /**
   * @return    the statement of this pragma
   */
  public String getStatement() {
    return statement;
  }
  
  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------
  
  /**
   * Accepts a visitor.    
   *
   * @param     visitor                 the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  private String        databaseType;
  private String        statement;
}
