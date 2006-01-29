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

package com.kopiright.xkopi.comp.xkjc;


import com.kopiright.compiler.base.PositionedError;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JLocalVariable;
import com.kopiright.kopi.comp.kjc.TypeFactory;
import com.kopiright.xkopi.comp.sqlc.SqlPhylum;
import com.kopiright.xkopi.comp.sqlc.SearchCondition;

/**
 * This class implements a Java pretty printer
 */
public interface XSqlVisitor extends com.kopiright.xkopi.comp.sqlc.SqlVisitor {

  /**
   * Visits a TypedSelectElem.
   */
  void visitTypedSelectElem(XTypedSelectElem self,
			    String ident,
			    JLocalVariable var,
			    SqlPhylum sqlExpr,
			    boolean hasAs)
    throws PositionedError;

  /**
   * Visits a XIfCondition.
   */
  void visitIfCondition(XIfCondition self,
			String oper,
			SearchCondition left,
			JExpression cond,
			XSqlExpr thenClause,
			XSqlExpr elseClause)
    throws PositionedError;

  /**
   * Visits a SqlExprJava.
   */
  void visitSqlExprJava(SqlExprJava self,
			JExpression expr,
			boolean asText)
    throws PositionedError;
}
