/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

public class VBlockDefaultOuterJoin {

  /**
   * Constructor
   */
  public VBlockDefaultOuterJoin(VBlock block) {
    this.block = block;
    this.fields = block.getFields();
    this.tables = block.getBlockTables();
    this.joinedTables = new ArrayList<String>();
    this.processedFields = new ArrayList<String>();
  }

  /**
   * constructs an outer join tree.
   */
  private String getJoinCondition(int rootTable, int table) {
    StringBuffer        joinBuffer = new StringBuffer("");
    VField              field;
     
    if (table == rootTable) {
      joinBuffer.append(tables[table] + " T" + table);
      addToJoinedTables(rootTable);
    }
    for (int i = 0; i < fields.length; i++) {
      if (isProcessedField(i)) {
        continue;
      }
      field = fields[i];

      if (field.getColumnCount() > 1) {
        int     tableColumn = field.fetchColumn(table);
        int     rootColumn = field.fetchColumn(rootTable);

        if (tableColumn != -1) {
          if (field.getColumn(tableColumn).isNullable() ||
              field.getColumn(rootColumn).isNullable()) {
            for (int j = 0; j < field.getColumnCount(); j++) {
              if (j != tableColumn) {
                if (isJoinedTable(field.getColumn(j).getTable()) ) {
                  // the table for this column is present in the outer join tree
                  // as caster outer joins do not work, we assume that the
                  // condition will apply to the root 
                    if (j == rootColumn) {
                      joinBuffer.append(" AND " +  field.getColumn(tableColumn).getQualifiedName() + " = " + field.getColumn(j).getQualifiedName());
                    }
                    if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                      // a field is only processed if all columns processed
                      // for nullable or has only 2 columns and one has been
                      // already processed
                      addToProcessedFields(i);
                    }
                } else {
                    if (rootTable == table) {
                      // start of an outer join
                      addToJoinedTables(field.getColumn(j).getTable());

                      joinBuffer.append(" LEFT OUTER JOIN " + tables[field.getColumn(j).getTable()] + " T" + field.getColumn(j).getTable());
                      joinBuffer.append(" ON " +  field.getColumn(tableColumn).getQualifiedName() + " = " + field.getColumn(j).getQualifiedName());
                    }
                    if (j == field.getColumnCount() || field.getColumnCount() == 2) {
                      // a field is only processed if all columns processed
                      // for nullable or has only 2 columns and one has been
                      // already processed
                      // must be marked before going to next level
                      addToProcessedFields(i);
                    }
                    if (rootTable == table) {
                      joinBuffer.append(getJoinCondition(rootTable, field.getColumn(j).getTable()));
                    }
                    
                }
              }
            }
          }
        }
      }
    }
    
    return joinBuffer.toString();
  }

  /**
   * search from-clause condition
   */
  public static String getSearchTables(VBlock block) {
    return (new VBlockDefaultOuterJoin(block)).getSearchTablesCondition();
  }

  private String getSearchTablesCondition() {
    StringBuffer        buffer;

    if (tables == null) {
      return null;
    }
    buffer = new StringBuffer(" FROM ");
    // first search join condition for the block main table.
    buffer.append(getJoinCondition(0, 0));
    System.out.println(buffer);
    // search join condition for other lookup tables  not joined with main table.
    for (int i = 1 ; i < tables.length; i++) {
      if (!isJoinedTable(i)) {
        // all not joined tables need to be ran through
        // removed: if (block.hasNullableColumns(i)) {
        buffer.append(", " + getJoinCondition(i, i));
        //}
      }
    }
    // add remaining tables (not joined tables) to the list of tables.
    for (int i = 1 ; i < tables.length; i++) {
      if (!isJoinedTable(i)) {
        buffer.append(", " + tables[i]  + " T" + i);
      }
    }
    return buffer.toString();
  }

  public static StringBuffer getSearchCondition(VField fld, StringBuffer buffer) {
    if (fld.hasNullableCols()) {
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
        }
      }
    } else {
      for (int j = 1; j < fld.getColumnCount(); j++) {
        if (buffer == null) {
          buffer = new StringBuffer(" WHERE ");
        } else {
          buffer.append(" AND ");
        }
        buffer.append(fld.getColumn(j).getQualifiedName());
        buffer.append(" = ");
        buffer.append(fld.getColumn(j - 1).getQualifiedName());
      }
    }
    return buffer;
  }

  public static String getFetchRecordCondition(VField[] fields) {
    String tailbuf = "";

    for (int i = 0; i < fields.length; i++) {
      VField    fld = fields[i];

      if (fld.hasNullableCols()) {
        for (int j = 1; j < fld.getColumnCount(); j++) {
          if (!fld.getColumn(j).isNullable()) {
            tailbuf +=
              " AND " +
              fld.getColumn(j).getQualifiedName() +
              " = " +
              fld.getColumn(0).getQualifiedName();
          }
        }
      } else {
        for (int j = 1; j < fld.getColumnCount(); j++) {
          tailbuf +=
            " AND " +
            fld.getColumn(j).getQualifiedName() +
            " = " +
            fld.getColumn(j - 1).getQualifiedName();
        }
      }
    }
    return tailbuf;
  }

  private void addToJoinedTables(int table) {
    joinedTables.add(Integer.toString(table));
  }

  private boolean isJoinedTable(int table) {
    return joinedTables.contains(Integer.toString(table));
  }

  private void addToProcessedFields(int field) {
    processedFields.add(Integer.toString(field));
  }

  private boolean isProcessedField(int field) {
    return processedFields.contains(Integer.toString(field));
  }

  private VBlock                block;
  private VField[]              fields;
  private ArrayList<String>     joinedTables;
  private ArrayList<String>     processedFields;
  private String[]              tables;
}
