/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: PGenExcelFile.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.vkopi.lib.report;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;

public class PGenExcelFile extends RDataExtractor {

  /**
   * Constructor
   */
  public PGenExcelFile(JTable table, MReport model, PConfig pconfig, File file, String title) {
    super(table, model, pconfig);

    parameters = new DParameters(Color.gray);
    try {
      fileStream = new FileOutputStream(file);

      // select data to be displayed
      data = selectDisplayData();
      addData(model);
      fileStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Add data to the document
   */
  public void addData(MReport model) {
    try {
      boolean first = true;

      for (int i = 0; i < model.getAccessibleColumnCount(); i++) {
	if (!getVisibleColumn(i).isFolded()) {
	  if (! first) {
	    fileStream.write("\t".getBytes());
	  }
	  fileStream.write(getVisibleColumn(i).getLabel().getBytes());
	  first = false;
	}
      }
      fileStream.write("\n".getBytes());

      for (int i = 0; i < data.length; i++) {
	String[]	strings = data[i];

	for (int j = 0; j < strings.length; j++) {
	  if (j != 0) {
	    fileStream.write("\t".getBytes());
	  }
	  if (strings[j] != null) {
	    fileStream.write(strings[j].replace('\n', ' ').getBytes());
	  }
	}
	fileStream.write("\n".getBytes());
      }
    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private FileOutputStream	fileStream;
}
