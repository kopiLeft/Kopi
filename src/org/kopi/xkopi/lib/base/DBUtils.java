/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.xkopi.lib.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kopi.util.base.InconsistencyException;

/**
 * Remote execution client
 */
public class DBUtils {

  /**
   * Creates a remote execution client with specified host and port
   */
  public static void action(String driver, String db, String user, String pwd, String command, String[] params) {
    try {
      DBContext.registerDriver(driver);
      DBContext	context = new DBContext();
      context.setDefaultConnection(context.createConnection(db, user, pwd));

      if (command.equals("update-text")) {
	update_text(context, params);
      } else {
	System.err.println("Available commands:");
	System.err.println(" - update-text:");
      }
    } catch (DBException exc) {
      System.err.println("Failed to create connection:" + user + "@" + db + " with passwd " + pwd);
    } catch (SQLException exc) {
      System.err.println(exc.getMessage());
    }
  }

  /**
   * Checks if the given SQL statement contains "FOR UPDATE"
   * keyword.
   */
  public static boolean isSelectForUpdate(String sql) {
    String      patternStr = "FOR *UPDATE";
    Pattern     pattern = Pattern.compile(patternStr);
    Matcher     matcher = pattern.matcher(sql.toUpperCase());

    if (matcher.find()) {
      return true;
    }

    return false;
  }

  // ----------------------------------------------------------------------
  // COMMANDS
  // ----------------------------------------------------------------------

  /**
   * update-text
   */
  private static void update_text(DBContext context, String[] params) throws SQLException {
    String	table;
    String	column;
    int		oldSize;
    int		newSize;

    try {
      table = params[0];
      column = params[1];
      oldSize = Integer.valueOf(params[2]).intValue();
      newSize = Integer.valueOf(params[3]).intValue();
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.err.println("update_text table_name string_column_name oldsize newsize");
      System.exit(0);
      throw new InconsistencyException();
    }

    context.startWork();
    org.kopi.xkopi.lib.base.Connection	conn = context.getDefaultConnection();
    Statement stmt = conn.createStatement();
    stmt.executeQuery("SELECT ID, " + column + " FROM " + table);
    ResultSet   rs = stmt.getResultSet();
    Vector	ids = new Vector();
    Vector	vals = new Vector();
    while (rs.next()) {
      ids.addElement(new Integer(rs.getInt(1)));
      vals.addElement(rs.getString(2));
    }
    stmt.close();
    context.abortWork();

    int		count = 0;

    context.startWork();
    for (int i = 0; i < ids.size(); i++) {
      Integer	id = (Integer)ids.elementAt(i);
      if (vals.elementAt(i) != null) {
	String  val = "";
	String  old = (String)vals.elementAt(i);
	String  tmp;
	for (int j = 0; j < old.length(); j += oldSize) {
	  tmp = old.substring(j, Math.min(j + oldSize, old.length())).trim();
	  val += tmp + white(newSize - tmp.length()); // $$$
	}

        //!!! graf FIXME 20201121 - avoid SQL injection
	val = "'" + val + "'";

	stmt = conn.createStatement();
	stmt.executeUpdate("UPDATE " + table + " SET " + column + " = " + val + " WHERE ID = " + id);
        stmt.close();
	System.err.println("UPDATE " + table + " SET " + column + " = " + val + " WHERE ID = " + id);
	count++;
      }
    }
    context.commitWork();

    System.err.println("update_text executed, " + count + " tuple modified");
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  private static String white(int count) {
    StringBuffer sb = new StringBuffer(count);
    for (int i = 0; i < count; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }

  // ----------------------------------------------------------------------
  // MAIN
  // ----------------------------------------------------------------------

  /**
   * Main
   */
  public static void main(String[] args) {
    if (args.length < 5) {
      usage();
    }

    String[]	params = new String[args.length - 5];
    System.arraycopy(args, 5, params, 0, params.length);

    if (args[4].equals("-help")) {
      action(args[0], args[1], args[2], args[3], args[5], null);
    } else {
      action(args[0], args[1], args[2], args[3], args[4], params);
    }
  }

  private static void usage() {
    System.err.println("DBUtils driver database user passwd [-help] command params*");
    System.err.println(" -help: display help about command but don't execute");
    System.err.println(" driver: org.kopi.tbx.TbxDriver");
    System.err.println(" database: jdbc:tbx://vie:4002/XXX");
    System.exit(1);
  }
}
