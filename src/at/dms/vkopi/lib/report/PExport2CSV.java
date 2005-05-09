/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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
 * $Id: PGenExcelFile.java 22806 2005-04-05 16:49:10Z taoufik $
 */

package at.dms.vkopi.lib.report;

import java.io.OutputStream;
import java.io.IOException;

import javax.swing.JTable;

public class  PExport2CSV extends PExport implements Constants {
  /**
   * Constructor
   */
  public PExport2CSV(JTable table, MReport model, PConfig pconfig, String title) {
    super(table, model, pconfig, title);

  }

  public void export(OutputStream out) {
    outStream = out;


    try {
      exportData();
      outStream.close();
    } catch (Exception e) {
      e.printStackTrace();
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
      boolean first = true;

      for (int i = 0; i < data.length; i++) {
        if (! first) {
          outStream.write("\t".getBytes());
        }
        if (data[i] != null) {
          outStream.write(data[i].getBytes());
        }
        first = false;
      }
      outStream.write("\n".getBytes()); 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  OutputStream  outStream;
}
