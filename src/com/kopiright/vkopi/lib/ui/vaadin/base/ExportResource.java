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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.StreamResource;

/**
 * An <code>ExportResource</code> used to download
 * server files. This {@link StreamResource} is used
 * to download exported files from a dynamic report.
 */
public class ExportResource extends StreamResource {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ExportResource</code> instance.
   * @param sourceFile The source file to be downloaded.
   * @param filename The file name used for the download.
   */
  public ExportResource(File sourceFile, String filename) {
    super(new ExportStreamSource(sourceFile), filename);
    fileLength = Long.toString(sourceFile.length()); 
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public DownloadStream getStream() {
    DownloadStream    ds = super.getStream();

    if (ds != null) {
      ds.setParameter("Content-Length", fileLength);
      ds.setParameter("Content-Type", getMIMEType());
      ds.setParameter("Content-Disposition", "attachment; filename=" + getFilename());
    }

    return ds;
  } 

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The <code>ExportStreamSource</code> is the {@link StreamSource}
   * for an {@link ExportResource}
   */
  private static class ExportStreamSource implements StreamSource {
  
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ExportStreamSource</code> instance.
     * @param file The source file.
     */
    public ExportStreamSource(File file) {
      this.file = file;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
  
    @Override
    public InputStream getStream() {
      try {
	return new ByteArrayInputStream(getBytes()) {

	  @Override
	  public void close() throws IOException {
	    super.close();
	    file.delete();
	  }
	};
      } catch (IOException e) {
	return null;
      } 
    }

    /**
     * Returns the bytes of the source file.
     * @return The bytes of the source file.
     * @throws IOException I/O errors.
     */
    private byte[] getBytes() throws IOException {
      FileInputStream	input = new FileInputStream(file);
      byte[]		buf = new byte[(int) file.length()];

      input.read(buf);
      input.close();
      return buf;
    } 
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final File            	file; 
    private static final long 		serialVersionUID = 1L;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private final String            		fileLength; 
  private static final long 			serialVersionUID = 1L;
}