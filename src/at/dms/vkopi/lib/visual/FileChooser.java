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
 * $Id: FileChooser.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.lib.visual;

import java.io.File;
import java.awt.Frame;
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
}
