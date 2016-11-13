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

package org.kopi.xkopi.comp.xkjc;

import org.kopi.kopi.comp.kjc.*;
import org.kopi.xkopi.comp.sqlc.*;
import org.kopi.compiler.base.CWarning;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.compiler.base.UnpositionedError;
import org.kopi.compiler.base.JavaStyleComment;

/**
 * This class represents cursor declaration in code part
 */
public class XExecSqlStatement extends XStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	conn		a pointer to a valid connection to a database
   * @param	body		the Sql expression to be executed
   * @param	types		a vector of type of columns in the database
   * @param	into		a list of lvalue expression to be modifier by this statement
   * @param	comment		the statement comment
   */
  public XExecSqlStatement(TokenReference where,
			   JExpression conn,
			   XSqlExpr body,
			   CType[] types,
			   JExpressionListStatement into,
			   JavaStyleComment[] comments)
  {
    super(where, comments);
    this.conn = conn;
    this.body = body;
    this.into = into;
    this.types = types;
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
            context.getClassContext().getCClass().descendsFrom(factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER).getCClass()),
	    XKjcMessages.BAD_DEFAULT_CONTEXT, context.getClassContext().getCClass().getQualifiedName());
    } else {
      conn = conn.analyse(new CExpressionContext(context, context.getEnvironment()));
      if (!conn.isAssignableTo(context, factory.createReferenceType(XTypeFactory.RFT_CONNECTION))) {
	check(context, conn.isAssignableTo(context, factory.createReferenceType(XTypeFactory.RFT_DBCONTEXTHANDLER)),
	      XKjcMessages.BAD_CONNECTION, conn.getType(factory).getCClass().getQualifiedName());
	// CONSTRUCT THE CONNECTION ACCESS
	conn = XProtectedStatement.buildConnection(getTokenReference(), conn)
          .analyse(new CExpressionContext(context, context.getEnvironment()));
      }
    }

    JExpression[] intos = into.getExpressions();
    JExpressionStatement[] intoStatements = new JExpressionStatement[intos.length];

    check(context, intos.length == types.length, XKjcMessages.SELECT_LIST_LENGTH);

    TokenReference ref = getTokenReference();

    if (conn == null) {
      conn =  new JMethodCallExpression(ref, 
                                        new JMethodCallExpression(ref, 
                                                                  new JThisExpression(ref), 
                                                                  "getDBContext", 
                                                                  JExpression.EMPTY), 
                                        "getDefaultConnection", 
                                        JExpression.EMPTY);
    }

    JExpression prefix = new JNameExpression(ref, "qry$");
    for (int i = 0; i < intos.length; i++) {
      JExpression acs = XCursorField.createAccessExpression(ref, types[i], i, prefix, new CExpressionContext(context, context.getEnvironment()));
      intoStatements[i] = new JExpressionStatement(ref,
						   new XAssignmentExpression(ref,
									     intos[i],
									     acs), null);
    }

    JStatement stmt =  parseSelectInto(ref,
                                       new JCompoundStatement(ref, intoStatements),
                                       conn, body);
    stmt.analyse(context);

    checkSql(context);

    return stmt;
  }

  /**
   * 
   * org.kopi.xkopi.lib.base.KopiUtils.executeUpdate((Connection)exprs[0], (String)expr[1]);
   */
  public static JBlock parseSelectInto(TokenReference ref, JStatement info, JExpression connection, JExpression body) {
  return 
  new JBlock(ref, new JStatement[] {
    new JVariableDeclarationStatement(ref,
                                      new JVariableDefinition(ref, 0, CReferenceType.lookup(org.kopi.xkopi.lib.base.Query.class.getName().replace('.','/')), "qry$", new JNullLiteral(ref)), 
                                      null), 
      new JExpressionStatement(ref, 
                               new JAssignmentExpression(ref, 
                                                         new JNameExpression(ref, "qry$"), 
                                                         new JUnqualifiedInstanceCreation(ref, 
                                                                                          CReferenceType.lookup(org.kopi.xkopi.lib.base.Query.class.getName().replace('.','/')), 
                                                                                          new JExpression[]{connection})), 
                               null), 
      new JExpressionStatement(ref, 
                               new JMethodCallExpression(ref, 
                                                         new JNameExpression(ref, "qry$"), 
                                                         "open", new JExpression[] {body}), null), 
      new JIfStatement(ref, 
                       new JLogicalComplementExpression(ref, 
                                                        new JMethodCallExpression(ref, 
                                                                                  new JNameExpression(ref, "qry$"), 
                                                                                  "next", 
                                                                                  JExpression.EMPTY)),
                       new JBlock(ref, new JStatement[] { 
                       new JExpressionStatement(ref, 
                                                new JMethodCallExpression(ref, 
                                                                          new JNameExpression(ref, "qry$"), 
                                                                          "close", 
                                                                          JExpression.EMPTY), 
                                                null), 
                       new JThrowStatement(ref,
                                           new JUnqualifiedInstanceCreation(ref, 
                                                                            CReferenceType.lookup(org.kopi.xkopi.lib.base.DBNoRowException.class.getName().replace('.','/')), 
                                                                            JExpression.EMPTY),
                                           null)}, null), 
                       null, 
                       null), 
      info, 
      new JIfStatement(ref, 
                       new JMethodCallExpression(ref, 
                                                 new JNameExpression(ref, "qry$"), 
                                                 "next", 
                                                 JExpression.EMPTY), 
                       new JBlock(ref, new JStatement[] { 
                       new JExpressionStatement(ref, 
                                                new JMethodCallExpression(ref, 
                                                                          new JNameExpression(ref, "qry$"), 
                                                                          "close", 
                                                                          JExpression.EMPTY), 
                                                null), 
                       new JThrowStatement(ref,
                                           new JUnqualifiedInstanceCreation(ref, 
                                                                            CReferenceType.lookup(org.kopi.xkopi.lib.base.DBTooManyRowsException.class.getName().replace('.','/')), 
                                                                            JExpression.EMPTY), 
                                           null)}, null), 
                       null, 
                       null), 
      new JExpressionStatement(ref, 
                               new JMethodCallExpression(ref, 
                                                         new JNameExpression(ref, "qry$"), 
                                                         "close", 
                                                         JExpression.EMPTY), 
                               null)}, 
             null);
}

  protected void checkSql(CBodyContext context) {
    SelectStatement	select = (SelectStatement)body.getSql();

    for (int i = 0; i < types.length; i++) {
      if (types[i].isPrimitive()) {
	SelectElem	phylum = (SelectElem)select.getSelectElemAt(i);
	//SelectElem	elem = (SelectElem)((XTypedSqlExpr)select.getSelectElemAt(i)).getSql();
	SelectElem	elem = (SelectElem)(phylum instanceof XTypedSqlExpr ? 
					    ((XTypedSqlExpr)phylum).getSql() : phylum);

	if (elem instanceof SelectElemExpression) {
	  Expression	expr = ((SelectElemExpression)elem).getExpression();

	  if (expr instanceof SetFunction) {
	    context.reportTrouble(new CWarning(expr.getTokenReference(), XKjcMessages.EXECUTE_BAD_PRIMITIVE));
	  }
    	}
      }
    }
  }



  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genXKjcCode(XKjcPrettyPrinter p) {
    p.visitExecSqlStatement(this, conn, body, into);
  }

  /**
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public final void visitOther(KjcVisitor p) {
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected JExpression		conn;
  protected XSqlExpr		body;
  protected JExpressionListStatement	into;
  protected CType[]		types;
}
