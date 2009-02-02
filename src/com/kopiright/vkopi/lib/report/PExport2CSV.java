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

package com.kopiright.vkopi.lib.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.JTable;

import com.kopiright.util.base.InconsistencyException;

public class  PExport2CSV extends PExport implements Constants {
  /**
   * Constructor
   */
  public PExport2CSV(JTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);
  }

  public void export(OutputStream out) {
    try {
      writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
      exportData();
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new InconsistencyException(e);
    } 
  }

  protected void startGroup(String subTitle) {
  }

  protected void exportHeader(String[] data) {
    writeData(data);
  }
  
  protected void exportRow(int level, String[] data,  Object[] orig, int[] alignments) {
    writeData(data); 
  }
    
  private void writeData(String[] data) {
    try {
      boolean   first = true;

      for (int i = 0; i < data.length; i++) {
        if (! first) {
          writer.write("\t");
        }
        if (data[i] != null) {
          writer.write(data[i]);
        }
        first = false;
      }
      writer.write("\n"); 
    } catch (IOException e) {
      throw new InconsistencyException(e);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private Writer                writer;
}
