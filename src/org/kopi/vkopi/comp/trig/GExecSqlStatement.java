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

package org.kopi.vkopi.comp.trig;

import org.kopi.compiler.base.JavaStyleComment;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.kopi.comp.kjc.CBodyContext;
import org.kopi.kopi.comp.kjc.CExpressionContext;
import org.kopi.kopi.comp.kjc.CType;
import org.kopi.kopi.comp.kjc.JCompoundStatement;
import org.kopi.kopi.comp.kjc.JExpression;
import org.kopi.kopi.comp.kjc.JExpressionListStatement;
import org.kopi.kopi.comp.kjc.JExpressionStatement;
import org.kopi.kopi.comp.kjc.JMethodCallExpression;
import org.kopi.kopi.comp.kjc.JNameExpression;
import org.kopi.kopi.comp.kjc.JStatement;
import org.kopi.kopi.comp.kjc.JThisExpression;
import org.kopi.kopi.comp.kjc.TypeFactory;
import org.kopi.xkopi.comp.xkjc.XCursorField;
import org.kopi.xkopi.comp.xkjc.XExecSqlStatement;
import org.kopi.xkopi.comp.xkjc.XKjcMessages;
import org.kopi.xkopi.comp.xkjc.XProtectedStatement;
import org.kopi.xkopi.comp.xkjc.XSqlExpr;
import org.kopi.xkopi.comp.xkjc.XStdType;

/**
 * This class represents cursor declaration in code part
 */
public class GExecSqlStatement extends XExecSqlStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	conn		a pointer to a valid connection to a database
   * @param	body		the SQL expression to be executed
   * @param	types		a vector of type of columns in the database
   * @param	into		a list of lvalue expression to be modifier by this statement
   * @param	comment		the statement comment
   */
  public GExecSqlStatement(TokenReference where,
			   JExpression conn,
			   XSqlExpr body,
			   CType[] types,
			   JExpressionListStatement into,
			   JavaStyleComment[] comments)
  {
    super(where, conn, body, types, into, comments);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   *
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   * @exception	UnpositionedError	this error will be positioned soon
   */
  public JStatement checkAndConvert(CBodyContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();

    for (int i = 0; i < types.length; i++) {
      try {
	types[i] = types[i].checkType(context);
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    }

    if (conn == null) {
      check(context,
	    context.getClassContext().getCClass().descendsFrom(XStdType.DBContextHandler.getCClass()),
	    XKjcMessages.BAD_DEFAULT_CONTEXT, context.getClassContext().getCClass().getQualifiedName());
    } else {
      conn = conn.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (!conn.isAssignableTo(context, XStdType.Connection)) {
	check(context, conn.isAssignableTo(context, XStdType.DBContextHandler),
	      XKjcMessages.BAD_CONNECTION, conn.getType(factory).getCClass().getQualifiedName());
	// CONSTRUCT THE CONNECTION ACCESS
	conn =XProtectedStatement.buildConnection(getTokenReference(), conn).analyse(new CExpressionContext(context, context.getEnvironment()));
      }
    }

    JExpression[] intos = into.getExpressions();
    JExpressionStatement[] intoStatements = new JExpressionStatement[intos.length];

    check(context, intos.length == types.length, XKjcMessages.SELECT_LIST_LENGTH);

    TokenReference ref = getTokenReference();

    if (conn == null) {
      conn = new JMethodCallExpression(ref, 
                                        new JMethodCallExpression(ref, 
                                                                  new JThisExpression(ref), 
                                                                  "getDBContext", 
                                                                  JExpression.EMPTY), 
                                        "getConnection",
                                        JExpression.EMPTY);
    }

    JExpression prefix = new JNameExpression(ref, "qry$");
    for (int i = 0; i < intos.length; i++) {
      JExpression acs = XCursorField.createAccessExpression(ref, types[i], i, prefix, new CExpressionContext(context, context.getEnvironment()));
      intoStatements[i] = new JExpressionStatement(ref,
						   new GAssignmentExpression(ref,
									     intos[i],
									     acs),
						   null);
    }

    JStatement stmt =  XExecSqlStatement.parseSelectInto(ref,
                                                         new JCompoundStatement(ref, intoStatements),
                                                         conn,
                                                         body);
    stmt.analyse(context);

    checkSql(context);

    return stmt;
  }
}
