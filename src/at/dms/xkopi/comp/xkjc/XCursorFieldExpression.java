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

package at.dms.xkopi.comp.xkjc;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.kopi.comp.kjc.CClass;
import at.dms.kopi.comp.kjc.CExpressionContext;
import at.dms.kopi.comp.kjc.JExpression;
import at.dms.kopi.comp.kjc.JFieldAccessExpression;

/**
 * a field access ('getExpression().t' or 't')
 */
public class XCursorFieldExpression extends JFieldAccessExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param where the line of this node in the source code
   */
  public XCursorFieldExpression(TokenReference where,
				JExpression prefix,
				String ident,
				boolean hasSuffix) {
    super(where, prefix, ident);
    if (prefix instanceof XCursorFieldExpression) {
      this.prefix = ((XCursorFieldExpression)prefix).prefix;
      this.ident = ((XCursorFieldExpression)prefix).ident + "$" + ident;
    }
    this.hasSuffix = hasSuffix;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    if (hasSuffix) {
      return this; // collect name
    } else {
      CClass	local = context.getClassContext().getCClass();

      findPrefix(local, context);
      checkAccess(local, context);

      if (field instanceof XCursorField) {
        return ((XCursorField)field).createAccessExpression(getTokenReference(), prefix, context).analyse(context);
      } else {
        return new at.dms.kopi.comp.kjc.JNameExpression(getTokenReference(), prefix, field.getIdent()).analyse(context);
      }
    }
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  private boolean	hasSuffix;
}
