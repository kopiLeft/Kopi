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

package org.kopi.xkopi.comp.dbi;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.kopi.compiler.base.Phylum;
import org.kopi.compiler.base.PositionedError;
import org.kopi.compiler.base.TokenReference;
import org.kopi.xkopi.comp.sqlc.SqlPhylum;
import org.kopi.xkopi.comp.sqlc.Statement;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.Query;

/**
 * This class represents a virtual file and is the main entry point in kopi grammar
 */
public class SCompilationUnit extends Phylum {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public SCompilationUnit(TokenReference ref) {
    super(ref);

    elems = new ArrayList();
  }

  // ----------------------------------------------------------------------
  // ACCESSOR
  // ----------------------------------------------------------------------

  public ArrayList getElems() {
    return elems;
  }

  // ----------------------------------------------------------------------
  // TREE COLLECTING
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void addStmt(Statement expr) {
    elems.add(expr);
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * This method provides a semantics check after parsing
   * Some optimization may be done here
   */
  public void check() {
    // NO SEMANTIC CHECK HERE
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   *
   */
  public void execute(DBContext context,
		      boolean optionSimulate,
                      String syntax,
		      DBAccess access,
		      PrintWriter out)
    throws PositionedError
  {
    try {
      context.startWork();

      for (int i = checkHead() ? 1 : 0; i < elems.size(); i++) {
	StringBuffer	buff = new StringBuffer();

	buff.append(checkSQL(i, syntax));
	if (access != null) {
	  makeDBSchema(i, access);
	}

	try {
	  new Query(context.getConnection()).run(buff.toString());
	} catch (Exception e) {
	  out.print("At line " + (((SqlPhylum)elems.get(i)).getTokenReference()).getLine() + ":");
	  out.println(e.getMessage());
	  context.abortWork();
	  Main.flush();
	}
      }
      if (optionSimulate) {
	context.abortWork();
      } else {
	context.commitWork();
      }
    } catch (SQLException e) {
      out.println("db-execute-error" + e.getMessage());
//       context.abortWork();
    }
  }

  /**
   *
   */
  public void addScript(DBContext context,
			boolean optionSimulate,
                        String syntax,
			DBAccess access,
			PrintWriter out)
    throws PositionedError
  {
    try {
      StringBuffer	buff = new StringBuffer();

      context.startWork();
      if (!checkHead()) {
	out.println("error-script-head");
	Main.exit();
      }

      for (int i = 1; i < elems.size(); i++) {
	buff.append(checkSQL(i, syntax));
	//if (!optionSimulate && access != null) {
	//  makeDBSchema(i, access);
	//}
	buff.append(";\n");
      }
      if (optionSimulate) {
	context.abortWork();
      } else {
  	access.addScript(packageName,
			 version,
			 buff.toString(),
			 comment,
			 context.getConnection().getUserName());
	context.commitWork();
      }
    } catch (SQLException e) {
      out.println("db-execute-error" + e.getMessage());
    }
  }

  /**
   * Generate the SQL in correct for the given syntax.
   */
  public String checkSQL(int place,
                         String syntax)
    throws PositionedError
  {
    SqlPhylum	elem = (SqlPhylum)elems.get(place);
    DbiChecker	checker = DbiChecker.create(syntax);

    elem.accept(checker);

    String str = checker.toString();

    if (elem instanceof TableDefinition) {
      ArrayList	foreignKeys = ((TableDefinition)elem).getForeignKey();

      if (foreignKeys != null) {
	for (int i = 0; i < foreignKeys.size(); i++) {
	  elems.add(place + 1, foreignKeys.get(i)); //$$$
	}
      }
    }

    return str;
  }

  /**
   * Generates code.
   * @param	context		the sql context that holds the Context
   * @param	left		the SQL expression to be built
   * @param	current		the current string literal to be added to left
   */
  public void makeDBSchema(int place, DBAccess access) throws SQLException  {
    SqlPhylum	elem = (SqlPhylum)elems.get(place);

    if (elem instanceof TableDefinition) {
      TableDefinition	table = (TableDefinition)elem;
      table.makeDBSchema(access, packageName);
    }
    if (elem instanceof DropTableStatement) {
      DropTableStatement	table = (DropTableStatement)elem;
      table.makeDBSchema(access, packageName);
    }
    if (elem instanceof IndexDefinition) {
      IndexDefinition		index  = (IndexDefinition)elem;
      index.makeDBSchema(access, packageName);
    }
  }

  /*package*/ boolean checkHead() {
    try {
      ScriptHeadStatement	stmt;

      stmt = (ScriptHeadStatement)elems.get(0);
      packageName = stmt.getPackage();
      version = stmt.getVersion();
      comment = stmt.getScriptComment();

      return true;
    } catch (ClassCastException e) {
      return false;
    }
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
    visitor.visitSCompilationUnit(this,
				  packageName,
				  version,
				  elems);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ArrayList     elems;
  private String	packageName;
  private int		version;
  private String	comment;
}
