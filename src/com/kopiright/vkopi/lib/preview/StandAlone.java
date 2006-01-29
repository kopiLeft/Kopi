/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.preview;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.swing.UIManager;

import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.ApplicationDefaultsAdapter;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.DObject;

/**
 * Starts PreviewWindow (for testing) without an Application
 */
public class StandAlone {

  public StandAlone(String inFile) {
    title = inFile;

    try {
      FileInputStream   fileIn = new FileInputStream(inFile);
      File		file = Utils.getTempFile("PREVIEW", "PS");

      BufferedWriter	ous = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
      // USE A TEMP FILE !!!

      // READ HEADER
      LineNumberReader    reader = new LineNumberReader(new InputStreamReader(fileIn));
      String              s;
      int                 currentPage = -1;

      while ((s = reader.readLine()) != null) {
        if (s.equals("/toprinter {true} def")) {
          ous.write("/toprinter {false} def");
          ous.write("\n");
        } else if (numberOfPages == -1 && s.startsWith("%%Page: ")) {
          currentPage = readCurrentPageNumber(s);
          ous.write(s);
          ous.write("\n");
        } else {
          ous.write(s);
          ous.write("\n");
        }
      }
      ous.close();

      if (numberOfPages == -1 && currentPage != -1) {
        numberOfPages = currentPage;
      }

      try {
        VPreviewWindow    preview = new VPreviewWindow() {
            public void close(int type) {
              System.exit(0);
            }
          };
        PrintJob        job = new PrintJob(file, false);

        job.setPrintInformation(title,
                                landscape,
                                width,
                                height,
                                numberOfPages);
        preview.preview(job, command);
      } catch (VException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private int readCurrentPageNumber(String line) {
    StringBuffer    buffer = new StringBuffer();

    // skip "%%Page: "
    for (int i = 8; i < line.length() && Character.isDigit(line.charAt(i)); i++) {
      buffer.append(line.charAt(i));
    }
    if (buffer.length() == 0) {
      return -1;
    } else {
      try {
        return Integer.parseInt(buffer.toString());
      } catch (NumberFormatException e) {
        return -1;
      }
    }
  }

  public static void main(String[] argv) {
    if (argv.length != 1) {
      System.out.println("usage: java com.kopiright.vkopi.lib.preview.StandAlone filename");
      System.exit(1);
    }

    try {
      UIManager.setLookAndFeel(new com.kopiright.vkopi.lib.ui.plaf.KopiLookAndFeel());//UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      System.err.println("Undefined look and feel: Kopi Look & Feel must be installed!");
      System.exit(1);
    }

    new Application(new ApplicationDefaultsAdapter()) {
        public DBContext login(String database, String driver, String username, String password) {
          return null;
        }
      };
    new StandAlone(argv[0]);
  }

  private String		command = "gs ";
  private String		title = "Editor";

  private boolean		landscape = false;
  private int			width = 595;
  private int			height = 842;
  private int			numberOfPages = -1;
}
