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
 * $Id: XKjcPrettyPrinter.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.comp.xkjc;

import java.io.IOException;
import java.util.Vector;

import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TabbedPrintWriter;
import at.dms.kopi.comp.kjc.*;
import at.dms.xkopi.comp.sqlc.SqlPhylum;

/**
 * This class implements a Java pretty printer
 */
public class XKjcPrettyPrinter extends KjcPrettyPrinter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public XKjcPrettyPrinter(String fileName, TypeFactory factory) throws IOException {
    super(fileName.endsWith(".java") || (fileName.lastIndexOf(".") == -1)
          ? fileName + ".gen"
          : fileName.substring(0, fileName.lastIndexOf(".")) +  ".java",
          factory);
  }

  /**
   * construct a pretty printer object for java code
   * @param	fileName		the file into the code is generated
   */
  public XKjcPrettyPrinter(TabbedPrintWriter p, TypeFactory factory) {
    super(p, factory);
  }

  /**
   * Returns the Sql pretty printer
   */
  public XSqlcPrettyPrinter getSqlcPrettyPrinter() {
    int sql_pos = 2;

    // must be at n*8+4 from the beginning of the line.
    for (;((pos + sql_pos) % 8) != 4; ++sql_pos) /*NOTHING*/;

    getSqlcPrettyPrinter(true);

    sql_pp.setPos(sql_pos);
    p.setPos(pos);
    sql_pp.setColumn(0);
    return sql_pp;
  }

  /**
   * Returns the Sql pretty printer
   */
  public XSqlcPrettyPrinter getSqlcPrettyPrinter(boolean inPlace) {
    if (sql_pp == null) {
      sql_pp = new XSqlcPrettyPrinter(p, this);
    }

    return sql_pp;
  }

  // ----------------------------------------------------------------------
  // STATEMENT
  // ----------------------------------------------------------------------


  /**
   * prints a while statement
   */
  public void visitProtectedStatement(XProtectedStatement self,
				      JExpression mess,
				      JExpression cont,
				      JBlock body)
  {
    newLine();
    print("#protected");
    if (cont != null) {
      print(" [");
      cont.accept(this);
      print("] ");
    }
    print("(");
    if (mess != null) {
      mess.accept(this);
    }
    print(") ");
    pos += TAB_SIZE;
    body.accept(this);
    pos -= TAB_SIZE;
  }

  /**
   * prints a while statement
   */
  public void visitUnprotectedStatement(XUnprotectedStatement self,
					JExpression mess,
					JExpression cont,
					JBlock body)
  {
    newLine();
    print("#unprotected");
    if (cont != null) {
      print(" [");
      cont.accept(this);
      print("] ");
    }
    print("(");
    if (mess != null) {
      mess.accept(this);
    }
    print(") ");
    pos += TAB_SIZE;
    body.accept(this);
    pos -= TAB_SIZE;
  }

  /**
   * prints a while statement
   */
  public void visitGotoStatement(XGotoStatement self,
                                 String target)
  {
    print("goto " + target + ";");
  }

  /**
   * prints a local cursor statement
   */
  public void visitLocalCursorDeclaration(XLocalCursorDeclaration self,
					  JExpression cont,
					  XCursorDeclaration decl,
					  Vector defs)
  {
    /*newLine();*/
    print("#cursor ");
    if (cont != null) {
      print("[");
      cont.accept(this);
      print("] ");
    }
    decl.accept(this);
    for (int i = 0; i < defs.size(); i++) {
      if (i == 0) {
	print(" ");
      } else {
	print(", ");
      }
      print(defs.elementAt(i));
    }
    print(";");
  }

  /**
   * prints a cursor type declaration statement
   */
  /*package*/ void visitContextCursorDeclaration(int modifiers,
                                                 String ident,
                                                 XSelectStatement stmt,
                                                 JFormalParameter[] args,
                                                 JFieldDeclaration[] fields)
  {
      /*newLine();*/
      print(CModifier.toString(modifiers));
      print(" #cursor ");
      print(ident);
      print(" ");
      if (stmt != null) {
	visitImplicitCursorBody(stmt, args);
      } else {
	visitExplicitCursorBody(fields);
      }
      newLine();
  }

  /**
   * prints an implicit cursor body
   */
  public void visitImplicitCursorBody(XSelectStatement stmt,
                                      JFormalParameter[] args)
  {
    print("(");
    for (int i = 0; i < args.length; i++) {
      if (i != 0) {
	print(", ");
      }
      args[i].accept(this);
    }
    print(") ");
    print("{");
    newLine();
    stmt.accept(this);
    newLine();
    print("}");
  }

  /**
   * prints an implicit cursor body
   */
  public void visitXSelectStatement(XSelectStatement self,
				    String mod,
				    XTypedSelectElem[] attrs,
				    SqlPhylum body)
  {
    int	old_pos = pos;

    pos += TAB_SIZE;

    try {
      body.accept(getSqlcPrettyPrinter());
    } catch (PositionedError e) {
      e.printStackTrace();
    }
    pos = old_pos;
  }

  /**
   * prints an implicit cursor body
   */
  public void visitExplicitCursorBody(JFieldDeclaration[] fields)
  {
    print("{");
    newLine();
    for (int i = 0; i < fields.length; i++) {
      fields[i].accept(this);
    }
    newLine();
    print("}");
  }

  /**
   * prints a local cursor statement
   */
  public void visitExecSqlExpression(XExecSqlExpression self,
				     XSqlExpr sql,
				     JExpression conn,
				     JExpression cursor)
  {
    print("#update ");
    if (conn != null) {
      print("[");
      conn.accept(this);
      print("] ");
    }
    if (cursor != null) {
      print("(");
      cursor.accept(this);
      print(") ");
    }
    print("{ ");
    pos += TAB_SIZE;
    sql.accept(this);
    pos -= TAB_SIZE;
    newLine();
    print("}");
  }

  /**
   * prints a local cursor statement
   */
  public void visitParseSqlExpression(XParseSqlExpression self,
                                      XSqlExpr sql)
  {
    print("#sql ");
    print("{ ");
    pos += TAB_SIZE;
    sql.accept(this);
    pos -= TAB_SIZE;
    print("}");
  }

  /**
   * prints a local cursor statement
   */
  public void visitExecSqlStatement(XExecSqlStatement self,
				    JExpression conn,
				    JExpression body,
				    JExpressionListStatement list)
  {
    int oldPos;

    print("#execute ");
    if (conn != null) {
      print("[");
      conn.accept(this);
      print("] ");
    }
    print(" { ");
    pos += TAB_SIZE;
    body.accept(this);
    oldPos = pos;
    // find the same place of the SELECT
    for (pos += 2;(pos % 8) != 4; ++pos) /*NOTHING*/;
    print("INTO        ");
    pos = oldPos;
    list.accept(this);
    pos -= TAB_SIZE;
    newLine();
    print("}");
  }

  /**
   * prints a local cursor statement
   */
  public void visitLiteral(String text)
  {
    print(text);
  }

  /**
   * prints an Sql statement
   */
  public void visitSql(SqlPhylum text)
  {
    int	old_pos = pos;
    newLine();

    try {
      text.accept(getSqlcPrettyPrinter());
    } catch (PositionedError e) {
      e.printStackTrace();
    }

    pos = old_pos;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected XSqlcPrettyPrinter		sql_pp;
}
