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

import java.io.IOException;

import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.TabbedPrintWriter;
import com.kopiright.kopi.comp.kjc.JExpression;
import com.kopiright.kopi.comp.kjc.JLocalVariable;
import com.kopiright.kopi.comp.kjc.KjcPrettyPrinter;
import com.kopiright.xkopi.comp.sqlc.SearchCondition;
import com.kopiright.xkopi.comp.sqlc.SqlPhylum;
import com.kopiright.xkopi.comp.sqlc.SqlcPrettyPrinter;

/**
 * This class implements a Java pretty printer
 */
public class XSqlcPrettyPrinter extends SqlcPrettyPrinter implements XSqlVisitor {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public XSqlcPrettyPrinter(String fileName) throws IOException {
    super(fileName);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public XSqlcPrettyPrinter(TabbedPrintWriter p, KjcPrettyPrinter java_pp) {
    super(p);

    this.output = java_pp;
  }

  // ----------------------------------------------------------------------
  // VISITOR
  // ----------------------------------------------------------------------

  /**
   * Visits a XTypedSelectElem.
   */
  public void visitTypedSelectElem(XTypedSelectElem self,
				   String ident,
				   JLocalVariable var,
				   SqlPhylum sqlExpr,
				   boolean hasAs)
    throws PositionedError
  {
    if (!hasAs) {
      print(var.getType());
      print(" ");
      pos += TAB_SIZE;
      pos += TAB_SIZE;
      if (sqlExpr != null) {
        sqlExpr.accept(this);
      } else {
        print(ident);
      }
      pos -= TAB_SIZE;
      pos -= TAB_SIZE;
    } else {
      print(var.getType());
      print(" ");
      pos += TAB_SIZE;
      pos += TAB_SIZE;
      sqlExpr.accept(this);
      if (!sqlExpr.toString().equals(ident)) {
        print(" AS ");
        print(ident);
      }
      pos -= TAB_SIZE;
      pos -= TAB_SIZE;
    }
  }

  /**
   * Visits a XIfCondition.
   */
  public void visitIfCondition(XIfCondition self,
			       String oper,
			       SearchCondition left,
			       JExpression cond,
			       XSqlExpr thenClause,
			       XSqlExpr elseClause)
    throws PositionedError
  {
    JavaSqlContext	sqlStatement = XSqlChecker.getJavaContext(getContext());

    left.accept(this);
    println();
    pos -= TAB_SIZE;
    print(oper);
    pos += TAB_SIZE;
    print("{if (");
    int         old_pos = output.getPos();
    output.setPos(pos);
    cond.accept(output);
    output.setPos(old_pos);
    print(") ");
    thenClause.getSql().accept(this);
    if (elseClause != null) {
      print(" else ");
      elseClause.getSql().accept(this);
    }
    print("}");
  }

  /**
   * Visits a SqlExprJava.
   */
  public void visitSqlExprJava(SqlExprJava self,
                               JExpression expr,
                               boolean asText)
    throws PositionedError
  {
    JavaSqlContext	sqlStatement = XSqlChecker.getJavaContext(getContext());

    print(":");
    if (asText) {
      print("!");
    }
    print("(");
    int         old_pos = output.getPos();
    output.setPos(pos);
    expr.accept(output);
    output.setPos(old_pos);
    print(")");
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected KjcPrettyPrinter		output;
}
