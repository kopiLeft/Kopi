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
import com.kopiright.vkopi.lib.form.*;
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
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.Locale;
import java.sql.SQLException;

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
      if(!fl[i].isInternal() && fl[i].getColumnCount() > 0 ) {
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

    //b.fetchLookup(b.fields[1]);
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
   *
   */
  public class DynamicReport extends VReport {
    public DynamicReport (VField[] fl) throws VException {
      VReportColumn[] columns = new VReportColumn[fl.length];

      this.block = fl[0].getBlock();

      for(int i = 0 ; i < fl.length ; i++ ) {
        if (fl[i] instanceof com.kopiright.vkopi.lib.form.VStringField) {
          columns[i] = new VStringColumn(null, 0, 0, -1, null, fl[i].getWidth(), 1, null);

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VBooleanField) {
          columns[i] = new VBooleanColumn(null, 0, 0, -1, null, 1, null);
        
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VDateField){
          columns[i] = new VDateColumn(null, 0, 0, -1, null, 1, null);
        
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VFixnumField){
          columns[i] = new VFixnumColumn(null, 0, 0, -1, null, fl[i].getWidth(), 
                                         ((VFixnumField)fl[i]).getScale(0), null
                                         );

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VIntegerField) {
          columns[i] = new VIntegerColumn(null, 0, 0, -1, null, fl[i].getWidth(), null);

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VMonthField) {
          columns[i] = new VMonthColumn(null, 0, 0, -1, null, fl[i].getWidth(), null);


        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VTimeField){
          columns[i] = new VTimeColumn(null, 0, 0, -1, null, fl[i].getWidth(), null);

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VTimestampField){
          columns[i] = new VTimestampColumn(null, 0, 0, -1, null, fl[i].getWidth(), null);

        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VWeekField){
          columns[i] = new VWeekColumn(fl[i].getName(), 0, 0, -1, null, fl[i].getWidth(), null);
          
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VStringCodeField) {
          columns[i] = new VStringCodeColumn(null, null, null, 0, 0, -1, null, fl[i].getWidth(), null,
                                             ((VCodeField)fl[i]).getLabels(), (String[])((VCodeField)fl[i]).getCodes()
                                             );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VIntegerCodeField) {
          columns[i] =  new VIntegerCodeColumn(null, null, null, 0, 0, -1, null,
                                               fl[i].getWidth(), null, ((VCodeField)fl[i]).getLabels(),
                                               getIntArray((Integer[])((VCodeField)fl[i]).getCodes())
                                               );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VFixnumCodeField){
          columns[i] = new VFixnumCodeColumn(null, null, null, 0, 0, -1, null, 1, null,
                                             ((VCodeField)fl[i]).getLabels(), (NotNullFixed[])((VCodeField)fl[i]).getCodes()
                                             );
        } else if (fl[i] instanceof com.kopiright.vkopi.lib.form.VBooleanCodeField){
          columns[i] = new VBooleanCodeColumn(null, null, null, 0, 0, -1, null, 1,
                                              null, ((VCodeField)fl[i]).getLabels(),
                                              getBoolArray((Boolean[])((VCodeField)fl[i]).getCodes())
                                              );
        } else {
          throw new InconsistencyException("Error: unknown field type.");
        }
        
        if (columns[i] != null) {
          columns[i].setLabel(fl[i].getName());
        }
        
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
    
    public  void add() {}
    protected void init() throws VException {}
    
    // ----------------------------------------------------------------------
    // LOCALIZATION
    // ----------------------------------------------------------------------
    
    public void localize(Locale locale) {
      // don't localize this dynamic report.
    }
    
    
    public void initReport() throws VException {
      build();
    }

    //
    //
    //
    
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
    
    //
    //
    //
    
    private VBlock block;    
  }
}
