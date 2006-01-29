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

package com.kopiright.vkopi.lib.print;

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

import com.kopiright.vkopi.lib.util.AbstractPrinter;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintInformation;
import com.kopiright.vkopi.lib.util.Printer;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.preview.VPreviewWindow;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.VException;

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
