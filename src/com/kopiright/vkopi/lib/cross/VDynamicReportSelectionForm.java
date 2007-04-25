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

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.form.*;
import com.kopiright.vkopi.lib.report.DColumnStyle;
import com.kopiright.vkopi.lib.report.Constants;
import com.kopiright.vkopi.lib.report.VNoRowException;
import com.kopiright.vkopi.lib.report.VReport;
import com.kopiright.vkopi.lib.report.VReportColumn;
import com.kopiright.vkopi.lib.report.VStringColumn;
import com.kopiright.vkopi.lib.report.VBooleanColumn;
import com.kopiright.vkopi.lib.report.VDateColumn;
import com.kopiright.vkopi.lib.report.VFixnumColumn;
import com.kopiright.vkopi.lib.report.VIntegerColumn;
import com.kopiright.vkopi.lib.report.VMonthColumn;
import com.kopiright.vkopi.lib.report.VTimeColumn;
import com.kopiright.vkopi.lib.report.VTimestampColumn;
import com.kopiright.vkopi.lib.report.VWeekColumn;
import com.kopiright.vkopi.lib.report.VStringCodeColumn;
import com.kopiright.vkopi.lib.report.VIntegerCodeColumn;
import com.kopiright.vkopi.lib.report.VFixnumCodeColumn;
import com.kopiright.vkopi.lib.report.VBooleanCodeColumn;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;
import com.kopiright.xkopi.lib.type.NotNullFixed;
import com.kopiright.xkopi.lib.base.Query;

import com.kopiright.vkopi.lib.report.VReportCommand;
import com.kopiright.vkopi.lib.report.SDefaultReportActor;

import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.Locale;
import java.sql.SQLException;
import java.awt.event.KeyEvent;

public class VDynamicReportSelectionForm extends VDictionaryForm {

  protected VDynamicReportSelectionForm() throws VException {
  }

  protected VDynamicReportSelectionForm(VForm caller) throws VException {
    super(caller);
  }

  protected VDynamicReportSelectionForm(DBContextHandler caller) throws VException {
    super(caller);
  }

  protected VDynamicReportSelectionForm(DBContext caller) throws VException {
    super(caller);
  }
  
  /**
   * Implements interface for COMMAND CreateDynamicReport
   */
  public void createDynamicReport(VBlock b) throws VException {
    VField[] fl = b.getFields();
    VField[] fields = new VField[fl.length];
    int size = 0;
    
    for (int i = 0 ; i < fl.length ; i++ ) {
      if((!fl[i].isInternal() || fl[i].isInternal() && fl[i].getName().equals("ID")) && fl[i].getColumnCount() > 0 ) {
        if (!(fl[i] instanceof VTextField || fl[i] instanceof VImageField || fl[i] instanceof VColorField)) {
          fields[size] = fl[i];
          size ++;
        }
      }
    }
    if (size == 0) {
      throw new InconsistencyException("Can't generate a report, check that this block contains unhidden fields with database columns.");
    }
    
    fl = fields;
    fields = new VField[size];
    
    for (int i = 0 ; i < size ; i++ ) {
      fields[i] = fl[i];
    }

    try {
      setWaitInfo(Message.getMessage("report_generation"));
      VReport report = new DynamicReport(fields);
      report.doNotModal();
      unsetWaitInfo();
    } catch (VNoRowException e) {
      unsetWaitInfo();
      error(MessageCode.getMessage("VIS-00057"));
    }

    b.setRecordChanged(0, false);
  }
  
  /**
   * create a dynamic report.
   */
  public class DynamicReport extends VReport {
    
    public DynamicReport (VField[] fl) throws VException {
      this.columns = new VReportColumn[fl.length];
      this.block = fl[0].getBlock();
      this.fl = fl;
      setPageTitle(block.getTitle());
      initDefaultActors();
      initDefaultCommands();
      initColumns();
    }
    
    
    public  void add() {}
    protected void init() throws VException {}
    
    /**
     * create report columns and fill them with data.
     */
    protected void initColumns() throws VException {
      int col = 0;

      for(int i = 0 ; i < fl.length ; i++ ) {
        
        if (fl[i] instanceof com.kopiright.vkopi.lib.form.VStringField) {
          columns[col] = new VStringColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), 1, null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VBooleanField) {
          columns[col] = new VBooleanColumn(null, 0, 0, getColumnGroups(fl[i]), null, 1, null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VDateField){
          columns[col] = new VDateColumn(null, 0, 0, getColumnGroups(fl[i]), null, 1, null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VFixnumField){
          columns[col] = new VFixnumColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), 
                                         ((VFixnumField)fl[i]).getScale(0), null
                                         );
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VIntegerField) {

          if(fl[i].getName().equals("ID")) {
            DColumnStyle style = new DColumnStyle();
            
            columns[fl.length - 1] = new VIntegerColumn(null,0, 0, getColumnGroups(fl[i]), null, 0, null);
            style.setFont(0);
            style.setBackground(Constants.CLR_RED);
            style.setForeground(Constants.CLR_RED);
            columns[fl.length - 1].setStyles( new DColumnStyle[] {style});
            col --;
          } else {
            columns[col] = new VIntegerColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null);
          }
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VMonthField) {
          columns[col] = new VMonthColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null);

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VTimeField){
          columns[col] = new VTimeColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VTimestampField){
          columns[col] = new VTimestampColumn(null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VWeekField){
          columns[col] = new VWeekColumn(fl[i].getName(), 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VStringCodeField) {
          columns[col] = new VStringCodeColumn(null, null, null, 0, 0, getColumnGroups(fl[i]), null, fl[i].getWidth(), null,
                                             ((VCodeField)fl[i]).getLabels(), (String[])((VCodeField)fl[i]).getCodes()
                                             );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VIntegerCodeField) {
          columns[col] =  new VIntegerCodeColumn(null, null, null, 0, 0, getColumnGroups(fl[i]), null,
                                               fl[i].getWidth(), null, ((VCodeField)fl[i]).getLabels(),
                                               getIntArray((Integer[])((VCodeField)fl[i]).getCodes())
                                               );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VFixnumCodeField){
          columns[col] = new VFixnumCodeColumn(null, null, null, 0, 0, getColumnGroups(fl[i]), null, 1, null,
                                             ((VCodeField)fl[i]).getLabels(), (NotNullFixed[])((VCodeField)fl[i]).getCodes()
                                             );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VBooleanCodeField){
          columns[col] = new VBooleanCodeColumn(null, null, null, 0, 0, getColumnGroups(fl[i]), null, 1,
                                              null, ((VCodeField)fl[i]).getLabels(),
                                              getBoolArray((Boolean[])((VCodeField)fl[i]).getCodes())
                                              );
        } else {
          throw new InconsistencyException("Error: unknown field type.");
        }

        // add labels for columns.

        if (!fl[i].getName().equals("ID")) { 
          String columnLabel = "";
          
          if (fl[i].getLabel() != null ) {
            columnLabel = fl[i].getLabel().endsWith(":") ? fl[i].getLabel().trim().substring(0, fl[i].getLabel().length() - 1) : fl[i].getLabel().trim();
          } else {
            columnLabel = fl[i].getName();
          }
          columns[col].setLabel(columnLabel);
        }
        col ++;
      }

      model.columns = columns ;
      
      boolean alreadyProtected = getForm().inTransaction();
      
      try {
        while (true) {
          try {
            if (!alreadyProtected) {
              getForm().startProtected(null);
            }
            
            Query  query = new Query(getForm().getDBContext().getDefaultConnection());
            String searchCondition = block.getSearchConditions() == null ? "" : block.getSearchConditions(); 
            String searchColumns = block.getReportSearchColumns(); 
            String searchTables = block.getSearchTables(); 
            
            query.open("SELECT " + searchColumns + "   " + searchTables  + "     " + searchCondition);
            // don't  add a line when ID equals 0.
            if(query.next()) {
              if (!query.getObject(fl.length).toString().equals("0")) {
                List result = new ArrayList();
                for (int i=0 ; i< fl.length ; i++ ) {
                  result.add(query.getObject(i + 1 ));
                }
                model.addLine(result.toArray());
              }
            }
            while (query.next()) {
              List result = new ArrayList();
              for (int i=0 ; i< fl.length ; i++ ) {
                result.add(query.getObject(i + 1 ));
              }
              model.addLine(result.toArray()); 
            }
            query.close();
            
            if (!alreadyProtected) {
              getForm().commitProtected();
            }
            break;
          } catch (SQLException e) {
            if (!alreadyProtected) {
              getForm().abortProtected(e);
            } else {
              throw e;
            }
          } catch (Error error) {
            if (!alreadyProtected) {
              getForm().abortProtected(error);
            } else {
              throw error;
            }
          } catch (RuntimeException rte) {
            if (!alreadyProtected) {
              getForm().abortProtected(rte);
            } else {
              throw rte;
            }
          }
        }
      } catch (Throwable e) {
        throw new VExecFailedException(e);
      }
      
    }
    
    // ----------------------------------------------------------------------
    // LOCALIZATION
    // ----------------------------------------------------------------------
    
    public void localize(Locale locale) {
      // don't localize this dynamic report.
    }
        
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

      createActor("File", "Quit", "quit-icon", KeyEvent.VK_ESCAPE, 0, Constants.CMD_QUIT);

      createActor("File", "Print", "print-icon", KeyEvent.VK_F6, 0, Constants.CMD_PRINT);
      // !!! wael 20070418: these actors can be added in the future.

      //    createActor("File", "Preview", null, KeyEvent.SHIFT_MASK + KeyEvent.VK_F6, 0, Constants.CMD_PREVIEW);
      //    createActor("File", "PrintOptions", "border", KeyEvent.VK_F7, KeyEvent.SHIFT_MASK, Constants.CMD_PRINT_OPTIONS);

      createActor("File", "Export", "export-icon", KeyEvent.VK_F8, 0, Constants.CMD_EXPORT);

      createActor("File", "ExportXLS", "export-icon", KeyEvent.VK_F8, KeyEvent.SHIFT_MASK, Constants.CMD_EXPORTXLS);

      createActor("File", "ExportPDF", "export-icon",KeyEvent.VK_F9, 0, Constants.CMD_EXPORTPDF);

      createActor("Action", "Fold", "fold-icon", KeyEvent.VK_F2, 0, Constants.CMD_FOLD);

      createActor("Action","Unfold", "unfold-icon", KeyEvent.VK_F3, 0, Constants.CMD_UNFOLD);
                  
      createActor("Action", "FoldColumn", "foldColumn-icon", KeyEvent.VK_UNDEFINED , 0, Constants.CMD_FOLD_COLUMN);

      createActor("Action", "UnfoldColumn", "unfoldColumn-icon", KeyEvent.VK_UNDEFINED , 0, Constants.CMD_UNFOLD_COLUMN);

      //    createActor("Action", "ColumnInfo", "options", KeyEvent.VK_UNDEFINED , 0, Constants.CMD_COLUMN_INFO);

      createActor("Action", "Sort", "serialquery-icon", KeyEvent.VK_F4 , 0, Constants.CMD_SORT);

      //    createActor("Action", "OpenLine", "edit", KeyEvent.VK_UNDEFINED, 0, CMD_OPEN_LINE);
      //    createActor("Settings", "RemoveConfiguration", null, KeyEvent.VK_UNDEFINED, 0, Constants.CMD_REMOVE_CONFIGURATION);
      //    createActor("Settings", "LoadConfiguration", "save", KeyEvent.VK_UNDEFINED, 0, Constants.CMD_LOAD_CONFIGURATION);

      createActor("Help", "Help", "help-icon", KeyEvent.VK_F1, 0, Constants.CMD_HELP);
      
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
      for (int i = 0 ; i < 11 ; i++ ) {
        commands[i] = new VReportCommand(this, actorsDef[i]);  
      }
    }
    /**
     * get the key column position for the given table (position is calculated in the  order of the report columns), return -1 if not found.
     */

    private int getKeyColumnForTable(int table) {
      int i = 0;
      // search before field ID.
      for (i = 0 ; i < fl.length - 1  && !fl[i].isInternal() ; i++ ) {
        if(fl[i].getColumn(0).getTable() == table && fl[i].getColumn(0).isKey()) {        
          return i;
        }
      }
      // search after field ID.
      for (int j = i + 1 ; j < fl.length - 1 ; j++ ) {
        if(fl[j].getColumn(0).getTable() == table && fl[j].getColumn(0).isKey()) {        
          return j - 1; 
        }
      }
      return -1;
    }
    /**
     * test whether this table is joined with block's main table or not.
     */
    private boolean isJoinedWithMainTable(int table) {
      VField[] fld = block.getFields();

      for (int i = 0 ; i < fld.length ; i++ ) {
        if( fld[i].isInternal() && fld[i].getColumnCount() > 1 ) {
          boolean mainTableFound = false;
          boolean currentTableFound = false;
          for (int j=0 ; j < fld[i].getColumnCount() ; j++) {
            if(fld[i].getColumn(j).getTable() == table) {
              if (fld[i].getColumn(j).getName().equals("ID")) {
                currentTableFound = true;
              }
            } else if(fld[i].getColumn(j).getTable() == block.getIdField().getColumn(0).getTable() ) {
              mainTableFound = true;
            }
            if(currentTableFound && mainTableFound ) {
              return true;
            }
          }
        }
      }
      return false;
    }

    /**
     * return the report column group for the given table.
     */
    private int getColumnGroups(int table) {
      VField[] fld = block.getFields();
      
      for (int i = 0 ; i < fld.length ; i++ ) {
        if( fld[i].isInternal() && fld[i].getColumnCount() > 1 ) {
          boolean mainTableFound = false;
          boolean currentTableFound = false;
          int currentTableColumnPos = -1;
          for (int j=0 ; j < fld[i].getColumnCount() ; j++) {
            if(fld[i].getColumn(j).getTable() == table) { 
              if (fld[i].getColumn(j).getName().equals("ID")) {
                currentTableFound = true;
                currentTableColumnPos = j;
              }
            } else if(fld[i].getColumn(j).getTable() == block.getIdField().getColumn(0).getTable() ) {
              mainTableFound = true;
            }
          }
          if(currentTableFound) {
            if(mainTableFound) {
              // group with the Id of the block.
              return fl.length - 1; 
            } else {
              int k = 0;
              
              while (k < fld[i].getColumnCount() ) {
                if (k != currentTableColumnPos) {
                  if(isJoinedWithMainTable(fld[i].getColumn(k).getTable())) {
                    return getKeyColumnForTable(fld[i].getColumn(k).getTable());
                  }
                }
                k++;
              }
              return -1;
            }
          }
        }
          
      }
      return -1;
    }
    /**
     * return the report column group for the given field.
     */
    private int getColumnGroups(VField fld) {
      
      if(fld.getColumn(0).getTable() == block.getIdField().getColumn(0).getTable()) {
        return -1;
      } else {
        return getColumnGroups(fld.getColumn(0).getTable());
      }
    }
    
    // ----------------------------------------------------------------------
    //  useful Methods.
    // ----------------------------------------------------------------------

    private boolean[] getBoolArray(Boolean[] codes) {
      boolean[] result  = new boolean[codes.length];
      for(int i = 0; i< codes.length; i++) {
        result[i]= codes[i].booleanValue();
      }
      return result;
    }
    
    public int[] getIntArray(Integer[] codes) {
      int[] result = new int[codes.length];
        for (int j = 0 ; j< codes.length ; j++ ) {
          result[j] = codes[j].intValue();
        }
        return result;
    }

    // ----------------------------------------------------------------------
    // Data Members
    // ----------------------------------------------------------------------

    private VReportColumn[]          columns;
    private VField[]                 fl;
    private VBlock                   block;    
    private SActor[]                 actorsDef;
    private int                      number = 0;
  }
}
