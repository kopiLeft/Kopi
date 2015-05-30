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

package com.kopiright.vkopi.lib.ui.vaadin.visual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.FileHandler;
import com.kopiright.vkopi.lib.visual.UWindow;

/**
 * The <code>VFileHandler</code> is the vaadin implementation of
 * the {@link FileHandler} specifications.
 */
public class VFileHandler extends FileHandler {

  // --------------------------------------------------
  // FILE HANDLER IMPLEMENTATION
  // --------------------------------------------------

  @Override
  public File chooseFile(UWindow window, String defaultName) {
    return chooseFile(window, null, defaultName);  
  }

  @Override
  public File chooseFile(UWindow window, File dir, String defaultName) { 
    try {
      return createTempFile(dir, defaultName);
    } catch (IOException e) {
      getApplication().displayError(window, e.getMessage());
      return null;
    }
  }
  
  @Override
  public File openFile(UWindow window, String defaultName) {
    return openFile(window, null, defaultName);
  }

  @Override
  public File openFile(UWindow window, final FileFilter filter) {
    return openFile(window, (String)null); // FIXME: Use the file filter by adding a mime type in the FileFilter object.
  }

  @Override
  public File openFile(UWindow window, File dir, String defaultName) {    
    byte[]	file = getApplication().getUploader().upload(null); // no mime type is provided
    
    if (file != null) {
      return toFile(window, file, dir, defaultName);
    }
    
    return null;
  }
  
  /**
   * Returns the current application instance.
   * @return The current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }
  
  /**
   * Converts the given bytes to a file. The file is created under OS temp directory.
   * @param file The file bytes.
   * @param dir The parent directory.
   * @param defaultName The default file name.
   * @return
   */
  protected File toFile(UWindow window, byte[] file, File directory, String defaultName) {
    try {
      FileOutputStream		out;
      File			destination;

      destination = createTempFile(directory, defaultName);
      out = new FileOutputStream(destination);
      out.write(file);
      out.close();
      
      return destination;
    } catch (IOException e) {
      getApplication().displayError(window, e.getMessage());
      return null;
    }
  }
  
  /**
   * Creates a temporary file.
   * @param directory The parent directory.
   * @param defaultName The default file name.
   * @return The created temporary file.
   * @throws IOException I/O errors.
   */
  protected File createTempFile(File directory, String defaultName)
    throws IOException
  {
    String		basename;
    String		extension;
    
    basename = getBaseFileName(defaultName);
    extension = getExtension(defaultName);
    
    return File.createTempFile(basename, String.valueOf("." + extension), directory);
  }
  
  /**
   * Returns the file extension of a given file name.
   * @param defaultName The default file name.
   * @return The file extension.
   */
  protected String getExtension(String defaultName) {
    if (defaultName != null) {
      int	index = defaultName.lastIndexOf('.');
      
      if (index != -1) {
	return defaultName.substring(Math.min(defaultName.length(), index + 1));
      }
    }
    
    return ""; // no extension.
  }
  
  /**
   * Returns the base file name (without file extension).
   * @param defaultName The default file name.
   * @return The base file name 
   */
  protected String getBaseFileName(String defaultName) {
    if (defaultName != null) {
      int	index = defaultName.lastIndexOf('.');
      
      if (index != -1) {
	return defaultName.substring(0, Math.min(defaultName.length(), index));
      }
    }
    
    return ""; // empty name.
  }
}
