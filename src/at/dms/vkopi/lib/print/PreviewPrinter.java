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
 * $Id$
 */

package at.dms.vkopi.lib.print;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import at.dms.vkopi.lib.util.AbstractPrinter;
import at.dms.vkopi.lib.util.PrintException;
import at.dms.vkopi.lib.util.PrintInformation;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.preview.VPreviewWindow;
import at.dms.vkopi.lib.visual.Application;
import at.dms.vkopi.lib.visual.VException;

/**
 * Local printer
 */
public class PreviewPrinter extends AbstractPrinter {

  /**
   *
   */
  public PreviewPrinter(String name, String command) {
    super(name);
    setCommand(command);
  }

  /**
   * Print a file and return the output of the command
   * @deprecated
   */
  public void setCommand(String command) {
    this.command = command;
  }

  public String print(PrintJob data) throws IOException, PrintException {
      try {
        new VPreviewWindow().preview((data.getDataType() != PrintJob.DAT_PS) ? data : convertToGhostscript(data), command);
      } catch (VException e) {
        throw new PSPrintException("PreviewPrinter.PrintTaskImpl::print()", e);
      }

      return "NYI";
    }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private String		command;
}
