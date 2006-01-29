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

package com.kopiright.vkopi.lib.visual;

import java.io.File;
import java.awt.Frame;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

public class FileChooser {

  // ----------------------------------------------------------------------
  // FILE CHOOSER
  // ----------------------------------------------------------------------

  public static File chooseFile(Frame frame, String defaultName) {
    File dir = new File(System.getProperty("user.home"));
    return chooseFile(frame, dir, defaultName);
  }

  public static File chooseFile(Frame frame, File dir, String defaultName) {
    JFileChooser filechooser = new JFileChooser(dir);

    // Init our preferences
    //filechooser.setApproveButtonText("TXT1 YES");
    //filechooser.setApproveButtonToolTipText("TXT2");
    //filechooser.setDialogTitle("XXXTITLE");
    filechooser.setSelectedFile(new File(defaultName));

    int returnVal = filechooser.showSaveDialog(frame);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }
  }

  public static File openFile(Frame frame, String defaultName) {
    File dir = new File(System.getProperty("user.home"));
    return openFile(frame, dir, defaultName);
  }

  public static File openFile(Frame frame, FileFilter filter) {
    JFileChooser filechooser = new JFileChooser(new File(System.getProperty("user.home")));

    filechooser.setFileFilter(filter);;

    int returnVal = filechooser.showOpenDialog(frame);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }
    
  }
  public static File openFile(Frame frame, File dir, String defaultName) {
    JFileChooser filechooser = new JFileChooser(dir);

    filechooser.setSelectedFile(new File(defaultName));

    int returnVal = filechooser.showOpenDialog(frame);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return filechooser.getSelectedFile();
    } else {
      return null;
    }
  }

  public static class PdfFilter extends FileFilter {
    public boolean accept(File f) {
      return f != null && f.getName().toUpperCase().endsWith(".PDF"); 
    }
    public String getDescription() {
      return "Alle Pdf-Dateien";
    }
  }
}
