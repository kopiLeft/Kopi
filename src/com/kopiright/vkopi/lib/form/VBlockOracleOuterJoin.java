/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.form;

import java.util.ArrayList;

public class VBlockOracleOuterJoin {
  
  /**
   * search from-clause condition
   */
  public static String getSearchTables(VBlock block) {
    StringBuffer        buffer;
    String[]            tables;

    tables = block.getBlockTables();
    if (tables == null) {
      return null;
    }
    buffer = new StringBuffer(" FROM  " + tables[0] + " T0");
    for (int i = 1 ; i < tables.length; i++) {
      buffer.append(", " + tables[i]  + " T" + i);
    }
    return buffer.toString();
  }
    
  public static StringBuffer getSearchCondition(VField fld, StringBuffer buffer) {
    for(int j = 1; j < fld.getColumnCount(); j++) {
      if (!fld.getColumn(j).isNullable()) {
        if (buffer == null) {
          buffer = new StringBuffer(" WHERE ");
        } else {
          buffer.append(" AND ");
        }
        buffer.append(fld.getColumn(j).getQualifiedName());
        buffer.append(" = ");
        buffer.append(fld.getColumn(0).getQualifiedName());        
      } else {
        if (buffer == null) {
          buffer = new StringBuffer(" WHERE ");
        } else {
          buffer.append(" AND ");
        }
        buffer.append(fld.getColumn(j).getQualifiedName());        
        buffer.append(" = ");
        buffer.append(fld.getColumn(0).getQualifiedName() + " (+)");
      }
    }
    return buffer;
  }
  
  public static String getFetchRecordCondition(VField[] fields) {
    String tailbuf = "";
    
    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];
      
      for (int j = 1; j < fld.getColumnCount(); j++) {
        if (!fld.getColumn(j).isNullable()) {
          tailbuf +=
            " AND " +
            fld.getColumn(j).getQualifiedName() +
            " = " +
            fld.getColumn(0).getQualifiedName();
        } else {
          tailbuf +=
            " AND " +
            fld.getColumn(j).getQualifiedName() +
            " = " +
            fld.getColumn(0).getQualifiedName() + " (+)";
        }
      }
    }
    return tailbuf;
  }
}
