/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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

package org.kopi.vkopi.lib.ui.vaadin.visual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.base.FileUploader;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.FileHandler;
import org.kopi.vkopi.lib.visual.UWindow;

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
      e.printStackTrace();
      getApplication().displayError(window, e.getMessage());
      return null;
    }
  }
  
  @Override
  public File openFile(UWindow window, String defaultName) {
    return openFile(window, null, defaultName);
  }

  @Override
  public File openFile(UWindow window, FileFilter filter) {
    return openFile(window, null, null, getMimeType(filter));
  }

  @Override
  public File openFile(UWindow window, File dir, String defaultName) {
    return openFile(window, dir, defaultName, null);
  }
  
  /**
   * Uploads a file from client side with the given mime type.
   * @param window The window display.
   * @param dir The directory to be used to store the uploaded file.
   * @param defaultName The file name to be used.
   * @param mimeType The searched mime type.
   * @return The uploaded file.
   */
  protected File openFile(UWindow window, File dir, String defaultName, String mimeType) {
    byte[]              file;
    FileUploader        uploader;
    
    uploader = new FileUploader();
    file = uploader.upload(mimeType);
    if (file != null) {
      return toFile(window, file, dir, uploader.getFilename() == null ? defaultName : uploader.getFilename());
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

    // if parent directory does not exist, create file in java.io.tempdir directly. 
    if (directory != null && !directory.exists()) {
      directory = null;
    }
    basename = ensurePrefixLength(getBaseFileName(defaultName));
    extension = getExtension(defaultName);
    
    return File.createTempFile(basename, extension, directory);
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
	return "." + defaultName.substring(Math.min(defaultName.length(), index + 1));
      }
    }
    
    return null; // ".tmp" will be added when calling File#createTempFile(String, String, File).
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
    
    return defaultName; // returns the entire default name.
  }
  
  /**
   * When calling {@link File#createTempFile(String, String, File)} the name
   * prefix should at least have 3 characters. This method aims to satisfy this
   * condition and complete missing length with X character to avoid {@link IllegalArgumentException}
   * when creating temporary files. 
   * @param prefix The file name prefix.
   * @return A prefix having at least three characters. If {@code prefix == null}, "XXX" is returned.
   */
  protected String ensurePrefixLength(String prefix) {
    if (prefix != null && prefix.length() > 3) {
      return prefix;
    } else {
      StringBuffer      buffer;
      
      buffer = new StringBuffer();
      buffer.append(prefix);
      while (buffer.length() < 3) {
        buffer.insert(prefix.length(), "X");
      }
      
      return buffer.toString();
    }
  }
  
  /**
   * Returns the mime type provided by the given file filter.
   * The technique is to loop all over known mime types and to
   * test the if the file is accepted by the filter. The corresponding
   * mime type is returned.
   * If filter provide unknown file, {@code null} is returned.  
   * @param filter The file filter.
   * @return The corresponding mime type.
   */
  private static String getMimeType(FileFilter filter) {
    List<String>                mimeTypes;
    
    mimeTypes = new ArrayList<String>();
    for (Map.Entry<String, String> entry : knownFileTypeToMimeType.entrySet()) {
      /**
       * A dummy file is created. It does not matter if file exists or not.
       * Generally, file filter tests on file extensions.
       */
      if (filter.accept(new File("file" + "." + entry.getKey()))) {
        // we return the first mime type that fits with the filter
        mimeTypes.add(entry.getValue());
      }
    }
    
    return toString(mimeTypes);
  }
  
  /**
   * Converts the given list to a string where elements are separated by a comma.
   * @param list The list of strings.
   * @return The separated comma string.
   */
  private static String toString(List<String> list) {
    if (list == null || list.isEmpty()) {
      return null;
    } else {
      StringBuffer        buffer;

      buffer = new StringBuffer();
      for (int i = 0; i < list.size(); i++) {
        buffer.append(list.get(i));
        if (i != list.size() -1) {
          buffer.append(", ");
        }
      }
      
      return buffer.toString();
    }
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  
  /**
   * File type to mime type map
   * This will be used to inject mime type according to a given
   * file filter.
   * This map will be initialized with known mime types. If a file filter
   * comes with unknown type, {@code null} will be returned as mime type.
   */
  private static Map<String, String>            knownFileTypeToMimeType;
  
  static {
    /**
     * The injected of list of mime types are picked form 
     * http://www.sitepoint.com/web-foundations/mime-types-summary-list/
     */
    knownFileTypeToMimeType = new HashMap<String, String>();
    knownFileTypeToMimeType.put("au", "audio/basic");
    knownFileTypeToMimeType.put("avi", "video/msvideo, video/avi, video/x-msvideo, .avi");
    knownFileTypeToMimeType.put("bmp", "image/bmp");
    knownFileTypeToMimeType.put("bz2", "application/x-bzip2");
    knownFileTypeToMimeType.put("css", "text/css");
    knownFileTypeToMimeType.put("dtd", "application/xml-dtd");
    knownFileTypeToMimeType.put("doc", "application/msword, .doc");
    knownFileTypeToMimeType.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document, .docx");
    knownFileTypeToMimeType.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template, .dotx");
    knownFileTypeToMimeType.put("es", "application/ecmascript");
    knownFileTypeToMimeType.put("exe", "application/octet-stream");
    knownFileTypeToMimeType.put("gif", "image/gif");
    knownFileTypeToMimeType.put("gz", "application/x-gzip");
    knownFileTypeToMimeType.put("hqx", "application/mac-binhex40");
    knownFileTypeToMimeType.put("html", "text/html");
    knownFileTypeToMimeType.put("jar", "application/java-archive");
    knownFileTypeToMimeType.put("jpg", "image/jpeg");
    knownFileTypeToMimeType.put("js", "application/x-javascript");
    knownFileTypeToMimeType.put("midi", "audio/x-midi");
    knownFileTypeToMimeType.put("mp3", "audio/mpeg");
    knownFileTypeToMimeType.put("mpeg", "video/mpeg");
    knownFileTypeToMimeType.put("ogg", "audio/vorbis, application/ogg");
    knownFileTypeToMimeType.put("pdf", "application/pdf");
    knownFileTypeToMimeType.put("pl", "application/x-perl");
    knownFileTypeToMimeType.put("png", "image/png");
    knownFileTypeToMimeType.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
    knownFileTypeToMimeType.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
    knownFileTypeToMimeType.put("ppt", "application/vnd.ms-powerpointtd, .ppt");
    knownFileTypeToMimeType.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation, .pptx");
    knownFileTypeToMimeType.put("ps", "application/postscript");
    knownFileTypeToMimeType.put("qt", "video/quicktime");
    knownFileTypeToMimeType.put("ra", "audio/x-pn-realaudio, audio/vnd.rn-realaudio");
    knownFileTypeToMimeType.put("ram", "audio/x-pn-realaudio, audio/vnd.rn-realaudio");
    knownFileTypeToMimeType.put("rdf", "application/rdf, application/rdf+xml");
    knownFileTypeToMimeType.put("rtf", "application/rtf");
    knownFileTypeToMimeType.put("sgml", "text/sgml");
    knownFileTypeToMimeType.put("sit", "application/x-stuffit");
    knownFileTypeToMimeType.put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
    knownFileTypeToMimeType.put("svg", "image/svg+xml");
    knownFileTypeToMimeType.put("swf", "application/x-shockwave-flash");
    knownFileTypeToMimeType.put("tar.gz", "application/x-tar");
    knownFileTypeToMimeType.put("tgz", "application/x-tar");
    knownFileTypeToMimeType.put("tiff", "image/tiff");
    knownFileTypeToMimeType.put("tsv", "text/tab-separated-values");
    knownFileTypeToMimeType.put("txt", "text/plain");
    knownFileTypeToMimeType.put("wav", "audio/wav, audio/x-wav");
    knownFileTypeToMimeType.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
    knownFileTypeToMimeType.put("xls", "application/vnd.ms-excel, .xls");
    knownFileTypeToMimeType.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
    knownFileTypeToMimeType.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, .xlsx");
    knownFileTypeToMimeType.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
    knownFileTypeToMimeType.put("xml", "application/xml");
    knownFileTypeToMimeType.put("zip", "application/zip, application/x-compressed-zip");
  }
}
