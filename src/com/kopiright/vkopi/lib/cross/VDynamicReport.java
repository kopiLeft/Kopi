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

package com.kopiright.vkopi.lib.cross;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VBooleanCodeField;
import com.kopiright.vkopi.lib.form.VBooleanField;
import com.kopiright.vkopi.lib.form.VCodeField;
import com.kopiright.vkopi.lib.form.VColorField;
import com.kopiright.vkopi.lib.form.VDateField;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFixnumCodeField;
import com.kopiright.vkopi.lib.form.VFixnumField;
import com.kopiright.vkopi.lib.form.VImageField;
import com.kopiright.vkopi.lib.form.VIntegerCodeField;
import com.kopiright.vkopi.lib.form.VIntegerField;
import com.kopiright.vkopi.lib.form.VMonthField;
import com.kopiright.vkopi.lib.form.VStringCodeField;
import com.kopiright.vkopi.lib.form.VStringField;
import com.kopiright.vkopi.lib.form.VTextField;
import com.kopiright.vkopi.lib.form.VTimeField;
import com.kopiright.vkopi.lib.form.VTimestampField;
import com.kopiright.vkopi.lib.form.VWeekField;
import com.kopiright.vkopi.lib.report.Constants;
import com.kopiright.vkopi.lib.report.DColumnStyle;
import com.kopiright.vkopi.lib.report.MReport;
import com.kopiright.vkopi.lib.report.PConfig;
import com.kopiright.vkopi.lib.report.SDefaultReportActor;
import com.kopiright.vkopi.lib.report.VBooleanCodeColumn;
import com.kopiright.vkopi.lib.report.VBooleanColumn;
import com.kopiright.vkopi.lib.report.VDateColumn;
import com.kopiright.vkopi.lib.report.VFixnumCodeColumn;
import com.kopiright.vkopi.lib.report.VFixnumColumn;
import com.kopiright.vkopi.lib.report.VIntegerCodeColumn;
import com.kopiright.vkopi.lib.report.VIntegerColumn;
import com.kopiright.vkopi.lib.report.VMonthColumn;
import com.kopiright.vkopi.lib.report.VNoRowException;
import com.kopiright.vkopi.lib.report.VReport;
import com.kopiright.vkopi.lib.report.VReportColumn;
import com.kopiright.vkopi.lib.report.VReportCommand;
import com.kopiright.vkopi.lib.report.VStringCodeColumn;
import com.kopiright.vkopi.lib.report.VStringColumn;
import com.kopiright.vkopi.lib.report.VTimeColumn;
import com.kopiright.vkopi.lib.report.VTimestampColumn;
import com.kopiright.vkopi.lib.report.VWeekColumn;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;
import com.kopiright.xkopi.lib.base.Query;
import com.kopiright.xkopi.lib.type.NotNullFixed;

public class VDynamicReport extends VReport {

  public VDynamicReport (VBlock block) throws VException {
    model = new MReport();
    pconfig = new PConfig();
    setDBContext(block.getDBContext());
    this.block = block;
    this.fields = initFields(block.getFields());
    this.columns = new VReportColumn[fields.length];
    setPageTitle(block.getTitle());
    initDefaultActors();
    initDefaultCommands();
    initColumns();
  }

  /**
   * Implements interface for COMMAND CreateDynamicReport
   */
  public static void createDynamicReport(VBlock block) throws VException {
    try {
      VReport   report;

      block.getForm().setWaitInfo(Message.getMessage("report_generation"));
      report = new VDynamicReport(block);
      report.doNotModal();
    } catch (VNoRowException e) {
      block.getForm().error(MessageCode.getMessage("VIS-00057"));
    } finally {
      block.getForm().unsetWaitInfo();
    }
    block.setRecordChanged(0, false);
  }  
  
  /**
   * @param     fields  block fields.          
   * @return fields that will represent columns in the dynamic report.
   */
  private VField[] initFields(VField[] fields) {
    VField[]  processedFields = new VField[fields.length];
    int       size = 0;
    
    for (int i = 0; i < fields.length; i++) {
      if ((!fields[i].isInternal() || fields[i].isInternal() && fields[i].getName().equals("ID"))) {
        if (fields[i].getColumnCount() > 0  || block.isMulti() && isFetched()) {
          if (!(fields[i] instanceof VTextField || fields[i] instanceof VImageField || fields[i] instanceof VColorField)) {
            processedFields[size] = fields[i];
            size ++;
          }
        }
      }
    }
    if (size == 0) {
      throw new InconsistencyException("Can't generate a report, check that this block contains unhidden fields with database columns.");
    }
    fields = processedFields;
    processedFields = new VField[size];
    for (int i = 0; i < size; i++) {
      processedFields[i] = fields[i];
    }
    return processedFields;
  }
  
  public boolean isFetched() {
    for (int i = 0; i < block.getBufferSize(); i += 1) {
      if (block.isRecordFetched(i)) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * create report columns and fill them with data.
   */
  protected void initColumns() throws VException {
    int         col = 0;
    
    for (int i = 0; i < fields.length; i++) {
      if (fields[i] instanceof VStringField) {
        columns[col] = new VStringColumn(null,
                                         0,
                                         0,
                                         getColumnGroups(fields[i]),
                                         null,
                                         fields[i].getWidth(),
                                         1,
                                         null);
      } else if (fields[i] instanceof VBooleanField) {
        columns[col] = new VBooleanColumn(null,
                                          0,
                                          0,
                                          getColumnGroups(fields[i]),
                                          null,
                                          1,
                                          null);
      } else if (fields[i] instanceof VDateField) {
        columns[col] = new VDateColumn(null,
                                       0,
                                       0,
                                       getColumnGroups(fields[i]),
                                       null,
                                       1,
                                       null);
      } else if (fields[i] instanceof VFixnumField) {
        columns[col] = new VFixnumColumn(null,
                                         0,
                                         0,
                                         getColumnGroups(fields[i]),
                                         null,
                                         fields[i].getWidth(), 
                                         ((VFixnumField)fields[i]).getScale(0),
                                         null);
      } else if (fields[i] instanceof VIntegerField) {
        // field ID of the block will represent the last column in the report ,and it will have the red color.
        //!!! graf 20080418: replace by block.getIdField()
        if(fields[i].getName().equals("ID")) {
          DColumnStyle  style = new DColumnStyle();
          
          columns[fields.length - 1] = new VIntegerColumn(null,
                                                          0,
                                                          0,
                                                          getColumnGroups(fields[i]),
                                                          null,
                                                          0,
                                                          null);
          columns[fields.length - 1].setFolded(true);
          style.setFont(0);
          style.setBackground(Constants.CLR_RED);
          style.setForeground(Constants.CLR_RED);
          columns[fields.length - 1].setStyles(new DColumnStyle[] {style});
          // next column will have the position col.
          col -= 1;
        } else {
          columns[col] = new VIntegerColumn(null,
                                            0,
                                            0,
                                            getColumnGroups(fields[i]),
                                            null,
                                            fields[i].getWidth(),
                                            null);
        }
      } else if (fields[i] instanceof VMonthField) {
        columns[col] = new VMonthColumn(null,
                                        0,
                                        0,
                                        getColumnGroups(fields[i]),
                                        null,
                                        fields[i].getWidth(),
                                        null);
      } else if (fields[i] instanceof VTimeField) {
        columns[col] = new VTimeColumn(null,
                                       0,
                                       0,
                                       getColumnGroups(fields[i]),
                                       null,
                                       fields[i].getWidth(),
                                       null);
      } else if (fields[i] instanceof VTimestampField) {
        columns[col] = new VTimestampColumn(null,
                                            0,
                                            0,
                                            getColumnGroups(fields[i]),
                                            null,
                                            fields[i].getWidth(),
                                            null);
      } else if (fields[i] instanceof VWeekField) {
        columns[col] = new VWeekColumn(fields[i].getName(),
                                       0,
                                       0,
                                       getColumnGroups(fields[i]),
                                       null,
                                       fields[i].getWidth(),
                                       null);
      } else if (fields[i] instanceof VStringCodeField) {
        columns[col] = new VStringCodeColumn(null,
                                             null,
                                             null,
                                             0,
                                             0,
                                             getColumnGroups(fields[i]),
                                             null,
                                             fields[i].getWidth(),
                                             null,
                                             ((VCodeField)fields[i]).getLabels(),
                                             (String[])((VCodeField)fields[i]).getCodes());
      } else if (fields[i] instanceof VIntegerCodeField) {
        columns[col] =  new VIntegerCodeColumn(null,
                                               null,
                                               null,
                                               0,
                                               0,
                                               getColumnGroups(fields[i]),
                                               null,
                                               fields[i].getWidth(),
                                               null,
                                               ((VCodeField)fields[i]).getLabels(),
                                               getIntArray((Integer[])((VCodeField)fields[i]).getCodes()));
      } else if (fields[i] instanceof VFixnumCodeField) {
        columns[col] = new VFixnumCodeColumn(null,
                                             null,
                                             null,
                                             0,
                                             0,
                                             getColumnGroups(fields[i]),
                                             null, 
                                             1,
                                             null,
                                             ((VCodeField)fields[i]).getLabels(),
                                             (NotNullFixed[])((VCodeField)fields[i]).getCodes());
      } else if (fields[i] instanceof VBooleanCodeField) {
        columns[col] = new VBooleanCodeColumn(null,
                                              null,
                                              null,
                                              0,
                                              0,
                                              getColumnGroups(fields[i]),
                                              null,
                                              1,
                                              null,
                                              ((VCodeField)fields[i]).getLabels(),
                                              getBoolArray((Boolean[])((VCodeField)fields[i]).getCodes()));     
      } else {
        throw new InconsistencyException("Error: unknown field type.");
      }
      // add labels for columns.
      //!!! graf 20080418: replace by block.getIdField()
      if (!fields[i].getName().equals("ID")) { 
        String  columnLabel;
        
        if (fields[i].getLabel() != null) {
          columnLabel = fields[i].getLabel().endsWith(":") ?
            fields[i].getLabel().trim().substring(0, fields[i].getLabel().length() - 1) : 
            fields[i].getLabel().trim();
        } else {
          columnLabel = fields[i].getName();
        }
        columns[col].setLabel(columnLabel);
      }
      col ++;
    }
    model.columns = columns;
    
    if (block.isMulti() && isFetched()) {
      for (int i = 0; i < block.getBufferSize(); i++) {
        if (block.isRecordFilled(i)) {
          block.setCurrentRecord(i);
          ArrayList list = new ArrayList();
          
          for (int j = 0; j < fields.length; j++) { 
            if (!fields[j].getName().equals("ID")) { 
              list.add(fields[j].getObject());
            }
          }
          // add ID field in the end.
          for (int j = 0; j < fields.length; j++) { 
            if (fields[j].getName().equals("ID")) { 
              list.add(fields[j].getObject());
              break;
            }
          }
          model.addLine(list.toArray());
        }
      }
    } else {
      boolean     alreadyProtected = block.getForm().inTransaction();
      try {
        while (true) {
          try {
            if (!alreadyProtected) {
              block.getForm().startProtected(null);
            }
            
            if (block.isMulti()) {
              block.setActiveRecord(0);
            }
            
            Query         query = new Query(block.getForm().getDBContext().getDefaultConnection());
            String        searchCondition = block.getSearchConditions() == null ? "" : block.getSearchConditions(); 
            String        searchColumns = block.getReportSearchColumns(); 
            String        searchTables = block.getSearchTables(); 
          
            if (block.isMulti()) {
              block.setActiveRecord(-1);
              block.setActiveField(null);
            } 
            query.open("SELECT " + searchColumns + " " + searchTables  + " " + searchCondition);
            if(query.next()) {
              // don't  add a line when ID equals 0.
              if (!query.getObject(fields.length).toString().equals("0")) {
                List result = new ArrayList();
                for (int i=0; i< fields.length; i++) {
                  result.add(query.getObject(i + 1 ));
                }
                model.addLine(result.toArray());
              }
            }
            while (query.next()) {
              List result = new ArrayList();
              for (int i=0; i< fields.length; i++) {
                result.add(query.getObject(i + 1 ));
              }
              model.addLine(result.toArray()); 
            }
            query.close();
          
            if (!alreadyProtected) {
              block.getForm().commitProtected();
            }
            break;
          } catch (SQLException e) {
            if (!alreadyProtected) {
              block.getForm().abortProtected(e);
            } else {
              throw e;
            }
          } catch (Error error) {
            if (!alreadyProtected) {
              block.getForm().abortProtected(error);
            } else {
              throw error;
            }
          } catch (RuntimeException rte) {
            if (!alreadyProtected) {
              block.getForm().abortProtected(rte);
            } else {
              throw rte;
            }
          }
        }
      } catch (Throwable e) {
        throw new VExecFailedException(e);
      }
    }
  }

  // methods overriden from VReport
  
  public void localize(Locale locale) {
    // report clumnns inherit their localization from the Block.
    // actors are localized with VlibProperties.
  }

  public void add() {}

  protected void init() throws VException {}
  
  public void initReport() throws VException {
    build();
  }
  
  public void destroyModel() {
    //
  }
  
  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  
  private void initDefaultActors() {
    actorsDef = new SActor[11];
    createActor("File", "Quit", QUIT_ICON, KeyEvent.VK_ESCAPE, 0, Constants.CMD_QUIT);
    createActor("File", "Print", PRINT_ICON, KeyEvent.VK_F6, 0, Constants.CMD_PRINT);
    createActor("File", "ExportCSV", EXPORT_ICON, KeyEvent.VK_F8, 0, Constants.CMD_EXPORT_CSV);
    createActor("File", "ExportXLS", EXPORT_ICON, KeyEvent.VK_F8, KeyEvent.SHIFT_MASK, Constants.CMD_EXPORT_XLS);
    createActor("File", "ExportPDF", EXPORT_ICON,KeyEvent.VK_F9, 0, Constants.CMD_EXPORT_PDF);
    createActor("Action", "Fold", FOLD_ICON, KeyEvent.VK_F2, 0, Constants.CMD_FOLD);
    createActor("Action","Unfold", UNFOLD_ICON, KeyEvent.VK_F3, 0, Constants.CMD_UNFOLD);
    createActor("Action", "FoldColumn", FOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED , 0, Constants.CMD_FOLD_COLUMN);
    createActor("Action", "UnfoldColumn", UNFOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED , 0, Constants.CMD_UNFOLD_COLUMN);
    createActor("Action", "Sort", SERIALQUERY_ICON, KeyEvent.VK_F4 , 0, Constants.CMD_SORT);
    createActor("Help", "Help", HELP_ICON, KeyEvent.VK_F1, 0, Constants.CMD_HELP);
    // !!! wael 20070418: these actors can be added in the future.
    //    createActor("File", "Preview", null, KeyEvent.SHIFT_MASK + KeyEvent.VK_F6, 0, Constants.CMD_PREVIEW);
    //    createActor("File", "PrintOptions", "border", KeyEvent.VK_F7, KeyEvent.SHIFT_MASK, Constants.CMD_PRINT_OPTIONS);
    //    createActor("Action", "OpenLine", "edit", KeyEvent.VK_UNDEFINED, 0, CMD_OPEN_LINE);
    //    createActor("Settings", "RemoveConfiguration", null, KeyEvent.VK_UNDEFINED, 0, Constants.CMD_REMOVE_CONFIGURATION);
    //    createActor("Settings", "LoadConfiguration", "save", KeyEvent.VK_UNDEFINED, 0, Constants.CMD_LOAD_CONFIGURATION);
    //    createActor("Action", "ColumnInfo", "options", KeyEvent.VK_UNDEFINED , 0, Constants.CMD_COLUMN_INFO);
    setActors(actorsDef);
  }
  
  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  
  private void createActor(String menuIdent,String actorIdent,String iconIdent,int key,int modifier,int trigger) {
    actorsDef[number] = new SDefaultReportActor(menuIdent, actorIdent, iconIdent, key, modifier);
    actorsDef[number].setNumber(trigger);
    number ++;
  }
  
  // ----------------------------------------------------------------------
  // Default Commands
  // ----------------------------------------------------------------------
  
  private void initDefaultCommands() {
    commands = new VCommand[actorsDef.length];
    for (int i = 0; i < 11; i++) {
      commands[i] = new VReportCommand(this, actorsDef[i]);  
    }
  }
  
  /**
   * get the key column position for the given table (position is calculated in the  order of the report columns), return -1 if not found.
   */
  // !!! wael 20070514: a table can have many keys, this method returns the first key found.
  private int getKeyColumnForTable(int table) {
    int       i;
    
    // the order of fields is different of the order of dynamic report columns, 
    // since ID field, represents the last column in the dynamic report.
    // search before field ID.
    for (i = 0; i < fields.length - 1 && !fields[i].isInternal(); i++) {
      if (fields[i].getColumnCount() > 0 && fields[i].getColumn(0).getTable() == table && fields[i].getColumn(0).isKey()) {        
        return i;
      }
    }
    // search after field ID.
    for (int j = i + 1; j < fields.length - 1; j++) {
      if (fields[j].getColumnCount() > 0 && fields[j].getColumn(0).getTable() == table && fields[j].getColumn(0).isKey()) {
        return j - 1; 
      }
    }
    return -1;
  }
  
  /**
   * return the report column group for the given table.
   */
  private int getColumnGroups(int table) {
    VField[]  flds = block.getFields();
    
    for (int i = 0; i < flds.length; i++) {
      if (flds[i].isInternal() && flds[i].getColumnCount() > 1) {
        int col = flds[i].fetchColumn(table);
        
        if (col != -1 && flds[i].getColumn(col).getName().equals("ID")) {
          if (flds[i].fetchColumn(0) != -1) {
            // group with the Id of the block.
            return this.fields.length - 1;
          } else {
            // group with the key of the joined table.
            // if this table is joined with many tables, group with the key of the next table in the list of columns for this field.
            // if the next table doesn't exist do nothing.
            if (col + 1 < flds[i].getColumnCount()) {
              return getKeyColumnForTable(flds[i].getColumn(col + 1).getTable());
            } else if (flds[i].getColumnCount() == 2) {
              return getKeyColumnForTable(flds[i].getColumn(col - 1).getTable());
            } else {
              return -1;
            }
          }
        }
      }
    }
    return -1;
  }
  
  /**
   * return the report column group for the given field.
   */
  private int getColumnGroups(VField field) {
    if (field.getColumnCount() == 0 || field.getColumn(0).getTable() == 0) {
      return -1;
    } else {
        return getColumnGroups(field.getColumn(0).getTable());
    }
  }
  
  // ----------------------------------------------------------------------
  //  useful Methods.
  // ----------------------------------------------------------------------
  
  private boolean[] getBoolArray(Boolean[] codes) {
    boolean[]   result = new boolean[codes.length];

    for (int i = 0; i < codes.length; i++) {
      result[i] = codes[i].booleanValue();
    }
    return result;
  }
  
  private int[] getIntArray(Integer[] codes) {
    int[]       result = new int[codes.length];

    for (int i = 0; i < codes.length; i++) {
      result[i] = codes[i].intValue();
    }
    return result;
  }
  
  // ----------------------------------------------------------------------
  // Data Members
  // ----------------------------------------------------------------------
  
  private VReportColumn[]       columns;
  private VField[]              fields;
  private VBlock                block;    
  private SActor[]              actorsDef;
  private int                   number = 0;
  private static String EXPORT_ICON            = "export";
  private static String FOLD_ICON              = "fold";
  private static String UNFOLD_ICON            = "unfold";
  private static String FOLD_COLUMN_ICON       = "foldColumn";
  private static String UNFOLD_COLUMN_ICON     = "unfoldColumn";
  private static String SERIALQUERY_ICON       = "serialquery";
  private static String HELP_ICON              = "help";
  private static String QUIT_ICON              = "quit";
  private static String PRINT_ICON             = "print";
}

