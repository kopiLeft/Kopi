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
import java.io.IOException;

import com.kopiright.vkopi.lib.ui.vaadin.base.FileUploader;
import com.kopiright.vkopi.lib.visual.FileHandler;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.vaadin.util.FileTypeResolver;

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
    File		temp = null;
    
    try  {
      if (dir != null) {
	temp = File.createTempFile(defaultName.substring(0,defaultName.indexOf(".")), defaultName.substring(defaultName.indexOf(".")), dir);
      } else {
	temp = File.createTempFile(defaultName.substring(0,defaultName.indexOf(".")), defaultName.substring(defaultName.indexOf(".")));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return temp;
  }
  
  @Override
  public File openFile(UWindow window, String defaultName) {
    FileUploader.get().invoke(null);
    return FileUploader.get().getSelectedFile();
  }

  @Override
  public File openFile(UWindow window, final FileFilter filter) {
    FileUploader.get().invoke(((VFileFilter)filter).getMIMEType());
    return FileUploader.get().getSelectedFile();
  }

  @Override
  public File openFile(UWindow window, File dir, String defaultName) {    
    FileUploader.get().invoke(null);
    return FileUploader.get().getSelectedFile();
  }
  
  //--------------------------------------------------
  // FILE FILTER
  //--------------------------------------------------
  
  /**
   * The <code>VFileFilter</code> is a vaadin file filter.
   */
  public class VFileFilter implements FileFilter {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>VFileFilter</code> instance.
     * @param extension The searched file extension.
     */
    public VFileFilter(String extension) {
      this.extension = extension;
    }
    
    @Override
    public boolean accept(File f) {
      return true;
    }
    
    /**
     * Returns the encapsulated mime type.
     * @return The encapsulated mime type.
     */
    public String getMIMEType() {
      return FileTypeResolver.getExtensionToMIMETypeMapping().get(extension.toLowerCase());
    }
    
    @Override
    public String getDescription() {
      return null;
    }
   
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final String		extension;
  }
}
