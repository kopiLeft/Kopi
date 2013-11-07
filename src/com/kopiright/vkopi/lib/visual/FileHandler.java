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

package com.kopiright.vkopi.lib.visual;

import java.io.File;

/**
 * {@code FileHandler} handles all kopi actions on a {@link File}.
 */
public abstract class FileHandler {

  //------------------------------------------------------------
  // ACCESSORS
  //------------------------------------------------------------

  public static FileHandler getFileHandler() {
    return fileHandler;
  }

  public static void setFileHandler(FileHandler handler) {
    assert handler != null : "FileHandler cannot be null";

    fileHandler = handler;
  }

  //------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------

  /**
   * Shows a dialog box to choose a file with a given default name.
   * @param window The caller window of the choice operation.
   * @param defaultName The file default name.
   * @return The chosen file.
   */
  public abstract File chooseFile(UWindow window, String defaultName);

  /**
   * Shows a dialog box to choose a file with a given default name from a given directory.
   * @param window The caller window of the choice operation.
   * @param dir The directory where the file should be chosen from.
   * @param defaultName The file default name.
   * @return The chosen file.
   */
  public abstract File chooseFile(UWindow window, File dir, String defaultName);

  /**
   * Shows a dialog box to open a file with a given default name.
   * @param window The caller window of the open operation.
   * @param defaultName The file default name.
   * @return The opened file.
   */
  public abstract File openFile(UWindow window, String defaultName);

  /**
   * Shows a dialog box to open a file with a given default name.
   * @param window The caller window of the open operation.
   * @param filter The file selection filter.
   * @return The opened file.
   */
  public abstract File openFile(UWindow window, FileFilter filter);

  /**
   * Shows a dialog box to open a file with a given default name from a given directory.
   * @param window The caller window of the open operation.
   * @param dir The directory where the file should be opened from.
   * @param defaultName The file default name.
   * @return The opened file.
   */
  public abstract File openFile(UWindow window, File dir, String defaultName);


  //------------------------------------------------------------
  // FILE FILTER
  //------------------------------------------------------------

  /**
   * A filter for abstract pathnames.
   */
  public interface FileFilter {

    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param  pathname  The abstract pathname to be tested
     * @return  <code>true</code> if and only if <code>pathname</code>
     *          should be included
     */
    public boolean accept(File pathname);

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     */
    public String getDescription();
  }

  // -----------------------------------------------------------
  // PDF FILE FILTER
  // -----------------------------------------------------------

  public static class PdfFilter implements FileFilter {

    /**
     * 
     */
    public boolean accept(File pathname) {
      return pathname != null && pathname.getName().toUpperCase().endsWith(".PDF");
    }

    /**
     * 
     */
    public String getDescription() {
      return "All PDF files";
    }
  }

  //------------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------------

  private static FileHandler			fileHandler;
}
