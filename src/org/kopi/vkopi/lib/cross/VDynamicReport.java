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

package org.kopi.vkopi.lib.cross;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VBooleanCodeField;
import org.kopi.vkopi.lib.form.VBooleanField;
import org.kopi.vkopi.lib.form.VCodeField;
import org.kopi.vkopi.lib.form.VDateField;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFixnumCodeField;
import org.kopi.vkopi.lib.form.VFixnumField;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.form.VIntegerCodeField;
import org.kopi.vkopi.lib.form.VIntegerField;
import org.kopi.vkopi.lib.form.VMonthField;
import org.kopi.vkopi.lib.form.VStringCodeField;
import org.kopi.vkopi.lib.form.VStringField;
import org.kopi.vkopi.lib.form.VTimeField;
import org.kopi.vkopi.lib.form.VTimestampField;
import org.kopi.vkopi.lib.form.VWeekField;
import org.kopi.vkopi.lib.report.Constants;
import org.kopi.vkopi.lib.report.MReport;
import org.kopi.vkopi.lib.report.PConfig;
import org.kopi.vkopi.lib.report.VBooleanCodeColumn;
import org.kopi.vkopi.lib.report.VBooleanColumn;
import org.kopi.vkopi.lib.report.VDateColumn;
import org.kopi.vkopi.lib.report.VDefaultReportActor;
import org.kopi.vkopi.lib.report.VFixnumCodeColumn;
import org.kopi.vkopi.lib.report.VFixnumColumn;
import org.kopi.vkopi.lib.report.VIntegerCodeColumn;
import org.kopi.vkopi.lib.report.VIntegerColumn;
import org.kopi.vkopi.lib.report.VMonthColumn;
import org.kopi.vkopi.lib.report.VNoRowException;
import org.kopi.vkopi.lib.report.VReport;
import org.kopi.vkopi.lib.report.VReportColumn;
import org.kopi.vkopi.lib.report.VReportCommand;
import org.kopi.vkopi.lib.report.VStringCodeColumn;
import org.kopi.vkopi.lib.report.VStringColumn;
import org.kopi.vkopi.lib.report.VTimeColumn;
import org.kopi.vkopi.lib.report.VTimestampColumn;
import org.kopi.vkopi.lib.report.VWeekColumn;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VActor;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.xkopi.lib.base.Query;
import org.kopi.xkopi.lib.type.NotNullFixed;

@SuppressWarnings("serial")
public class VDynamicReport extends VReport {

  public VDynamicReport (VBlock block) throws VException {
    model = new MReport();
    pconfig = new PConfig();
    setDBContext(block.getDBContext());
    this.block = block;
    this.fields = initFields(block.getFields());
    this.columns = new VReportColumn[fields.length];
    this.idColumn = -1;
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
    List<VField>        processedFields = new ArrayList<VField>();

    for (int i = 0; i < fields.length; i++) {
      // Images fields cannot be handled in dynamic reports
      if (!(fields[i] instanceof VImageField)
          && (!fields[i].isInternal() || fields[i].getName().equals(block.getIdField().getName())))
      {
        if (fields[i].getColumnCount() > 0  || block.isMulti() && isFetched()) {
          processedFields.add(fields[i]);
        }
      }
    }
    
    if (processedFields.isEmpty()) {
      throw new InconsistencyException("Can't generate a report, check that this block contains unhidden fields with database columns.");
    }

    return processedFields.toArray(new VField[processedFields.size()]);
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
                                         fields[i].getAlign(),
                                         getColumnGroups(fields[i]),
                                         null,
                                         fields[i].getWidth(),
                                         1,
                                         null);
      } else if (fields[i] instanceof VBooleanField) {
        columns[col] = new VBooleanColumn(null,
                                          0,
                                          fields[i].getAlign(),
                                          getColumnGroups(fields[i]),
                                          null,
                                          1,
                                          null);
      } else if (fields[i] instanceof VDateField) {
        columns[col] = new VDateColumn(null,
                                       0,
                                       fields[i].getAlign(),
                                       getColumnGroups(fields[i]),
                                       null,
                                       1,
                                       null);
      } else if (fields[i] instanceof VFixnumField) {
        columns[col] = new VFixnumColumn(null,
                                         0,
                                         fields[i].getAlign(),
                                         getColumnGroups(fields[i]),
                                         null,
                                         fields[i].getWidth(),
                                         ((VFixnumField)fields[i]).getScale(0),
                                         null);
      } else if (fields[i] instanceof VIntegerField) {
        // hidden field ID of the block will represent the last column in the report.
        if(fields[i].getName().equals(block.getIdField().getName()) && fields[i].isInternal()) {
          idColumn = fields.length - 1;
          columns[fields.length - 1] = new VIntegerColumn(null,
                                                          0,
                                                          fields[i].getAlign(),
                                                          getColumnGroups(fields[i]),
                                                          null,
                                                          fields[i].getWidth(),
                                                          null);
          columns[fields.length - 1].setFolded(true);
          // next column will have the position col.
          col -= 1;
        } else {
          if (fields[i].getName().equals(block.getIdField().getName())) {
            idColumn = i;
          }
          columns[col] = new VIntegerColumn(null,
                                            0,
                                            fields[i].getAlign(),
                                            getColumnGroups(fields[i]),
                                            null,
                                            fields[i].getWidth(),
                                            null);
        }
      } else if (fields[i] instanceof VMonthField) {
        columns[col] = new VMonthColumn(null,
                                        0,
                                        fields[i].getAlign(),
                                        getColumnGroups(fields[i]),
                                        null,
                                        fields[i].getWidth(),
                                        null);
      } else if (fields[i] instanceof VTimeField) {
        columns[col] = new VTimeColumn(null,
                                       0,
                                       fields[i].getAlign(),
                                       getColumnGroups(fields[i]),
                                       null,
                                       fields[i].getWidth(),
                                       null);
      } else if (fields[i] instanceof VTimestampField) {
        columns[col] = new VTimestampColumn(null,
                                            0,
                                            fields[i].getAlign(),
                                            getColumnGroups(fields[i]),
                                            null,
                                            fields[i].getWidth(),
                                            null);
      } else if (fields[i] instanceof VWeekField) {
        columns[col] = new VWeekColumn(fields[i].getName(),
                                       0,
                                       fields[i].getAlign(),
                                       getColumnGroups(fields[i]),
                                       null,
                                       fields[i].getWidth(),
                                       null);
      } else if (fields[i] instanceof VStringCodeField) {
        columns[col] = new VStringCodeColumn(null,
                                             null,
                                             null,
                                             0,
                                             fields[i].getAlign(),
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
                                               fields[i].getAlign(),
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
                                             fields[i].getAlign(),
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
                                              fields[i].getAlign(),
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
      if (!fields[i].getName().equals(block.getIdField().getName())) {
        String  columnLabel;

        if (fields[i].getLabel() != null) {
          columnLabel = fields[i].getLabel().trim();
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
          ArrayList<Object> list = new ArrayList<Object>();

          for (int j = 0; j < fields.length; j++) {
            if (!fields[j].getName().equals(block.getIdField().getName())) {
              list.add(fields[j].getObject());
            }
          }
          // add ID field in the end.
          for (int j = 0; j < fields.length; j++) {
            if (fields[j].getName().equals(block.getIdField().getName())) {
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
              if (!query.getObject(idColumn + 1).toString().equals("0")) {
                List<Object> result = new ArrayList<Object>();
                for (int i=0; i< fields.length; i++) {
                  result.add(query.getObject(i + 1));
                }
                model.addLine(result.toArray());
              }
            }
            while (query.next()) {
              List<Object> result = new ArrayList<Object>();
              for (int i=0; i< fields.length; i++) {
                result.add(query.getObject(i + 1));
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
    actorsDef = new VActor[11];
    createActor("File", "Quit", QUIT_ICON, KeyEvent.VK_ESCAPE, 0, Constants.CMD_QUIT);
    createActor("File", "Print", PRINT_ICON, KeyEvent.VK_F6, 0, Constants.CMD_PRINT);
    createActor("File", "ExportCSV", EXPORT_ICON, KeyEvent.VK_F8, 0, Constants.CMD_EXPORT_CSV);
    createActor("File", "ExportXLSX", EXPORT_ICON, KeyEvent.VK_F9, KeyEvent.SHIFT_MASK, Constants.CMD_EXPORT_XLSX);
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
    actorsDef[number] = new VDefaultReportActor(menuIdent, actorIdent, iconIdent, key, modifier);
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
   * return the report column group for the given table.
   */
  private int getColumnGroups(int table) {
    VField[]  flds = block.getFields();

    for (int i = 0; i < flds.length; i++) {
      if (flds[i].isInternal() && flds[i].getColumnCount() > 1) {
        int col = flds[i].fetchColumn(table);

        if (col != -1 && flds[i].getColumn(col).getName().equals(block.getIdField().getName())) {
          if (flds[i].fetchColumn(0) != -1) {
            // group with the Id of the block.
            return idColumn;
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

  private VReportColumn[]       	columns;
  private VField[]              	fields;
  private VBlock                	block;
  private VActor[]              	actorsDef;
  private int                   	number = 0;
  private int                   	idColumn = 0;
  private static String 		EXPORT_ICON            = "export";
  private static String 		FOLD_ICON              = "fold";
  private static String 		UNFOLD_ICON            = "unfold";
  private static String 		FOLD_COLUMN_ICON       = "foldColumn";
  private static String 		UNFOLD_COLUMN_ICON     = "unfoldColumn";
  private static String 		SERIALQUERY_ICON       = "serialquery";
  private static String 		HELP_ICON              = "help";
  private static String 		QUIT_ICON              = "quit";
  private static String 		PRINT_ICON             = "print";
}

