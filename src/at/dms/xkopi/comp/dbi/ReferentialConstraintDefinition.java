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
 * $Id: ReferentialConstraintDefinition.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.xkopi.comp.sqlc.FieldNameList;

/**
 * This class represents a table definition
 */
public class ReferentialConstraintDefinition extends TableConstraint {

  public static final int TYP_RESTRICT = 0;
  public static final int TYP_CASCADE = 1;
  public static final int TYP_NULL = 2;
  public static final int TYP_DEFAULT = 3;

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   * @param	ref		the token reference for this clause
   * @param	field		the field name list
   * @param	reference	the referenced table and column
   */
  public ReferentialConstraintDefinition(TokenReference ref,
					 String name,
					 FieldNameList field,
					 ReferencedTableAndColumns reference,
					 int type) {
    super(ref);
    this.name = name;
    this.field = field;
    this.reference = reference;
    this.type = type;
  }

  // --------------------------------------------------------------------
  // ACCEPT VISITORS
  // --------------------------------------------------------------------

  /**
   * Accepts a visitor.
   *
   * @param	visitor			the visitor
   */
  public void accept(DbiVisitor visitor) throws PositionedError {
    visitor.visitReferentialConstraintDefinition(this, name, field, reference, type);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  String			name;
  FieldNameList			field;
  ReferencedTableAndColumns	reference;
  int				type;
}
