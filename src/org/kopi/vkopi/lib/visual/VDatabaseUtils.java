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

package org.kopi.vkopi.lib.visual;

import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.xkopi.lib.base.DBContextHandler;
import org.kopi.xkopi.lib.base.Query;

public class VDatabaseUtils {

  /*
   * Checks if a foreign key is referenced
   */
  @SuppressWarnings("deprecation")
  public static void checkForeignKeys(DBContextHandler ctxt, int id, String table)
    throws VException, SQLException
  {
    Query		query1, query2;

    query1 = new Query(ctxt.getDBContext().getDefaultConnection());
    query1.addString(table);
    query1.open("SELECT         tabelle, spalte, aktion " +
		"FROM		REFERENZEN " +
		"WHERE		referenz = #1 " +
		"ORDER BY	3 DESC");
    while (query1.next()) {
      switch (query1.getString(3).charAt(0)) {
      case 'R':		// restrict: abort when referenced
	query2 = new Query(ctxt.getDBContext().getDefaultConnection());
	query2.addString(query1.getString(1));
	query2.addString(query1.getString(2));
	query2.addInt(id);
	query2.open("SELECT ID FROM $1 WHERE $2 = #3");
	if (query2.next()) {
	  query2.close();
	  ctxt.getDBContext().abortWork();
	  throw new VExecFailedException(MessageCode.getMessage("VIS-00021",
							    new Object[]{
                                                              query1.getString(2),
                                                              query1.getString(1)
                                                            }));
	}
	query2.close();
	break;

      case 'C':		// cascade: delete tuple (with references)
	query2 = new Query(ctxt.getDBContext().getDefaultConnection());
	query2.addString(query1.getString(1));
	query2.addString(query1.getString(2));
	query2.addInt(id);
	query2.open("SELECT ID FROM $1 WHERE $2 = #3");
	while (query2.next()) {
	  checkForeignKeys(ctxt, query2.getInt(1), query1.getString(1));
	}
	query2.close();

        // now delete the tuple
	query2.addString(query1.getString(1));
	query2.addString(query1.getString(2));
	query2.addInt(id);
	query2.run("DELETE FROM $1 WHERE $2 = #3");
	break;

      case 'N':
	query2 = new Query(ctxt.getDBContext().getDefaultConnection());
	query2.addString(query1.getString(1));
	query2.addString(query1.getString(2));
	query2.addInt(id);
	query2.run("UPDATE $1 SET $2 = 0 WHERE $2 = #3");
	break;

      default:
	throw new InconsistencyException();
      }
    }
    query1.close();
  }


  /**
   * Deletes current record of given block from database.
   * Is like DeleteChecked in Aegis
   */
  public static  void deleteRecords(DBContextHandler ctxt,
                                    String table,
                                    String condition)
    throws VException, SQLException
  {
    Query       query;

    query = new Query(ctxt.getDBContext().getDefaultConnection());
    query.addString(table);
    if (condition != null && condition.length() > 0) {
      query.addString(condition);
      query.open("SELECT ID FROM $1 WHERE $2 FOR UPDATE");
    } else {
      query.open("SELECT ID FROM $1 FOR UPDATE");
    }

    while (query.next()) {
      int       id = query.getInt(1);

      checkForeignKeys(ctxt, id, table);

      query.addString(table);
      query.delete("DELETE FROM $1");
    }
    query.close();
  }

}
