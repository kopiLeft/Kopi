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
 * $Id: DBlock.java 34539 2015-01-19 08:51:40Z hacheni $
 */

package com.kopiright.vkopi.lib.ui.vaadin.form;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;

import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VImageField;
import com.kopiright.vkopi.lib.form.VStringField;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.visual.VApplication;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;

/**
 * The <code>DBlockDropHandler</code> is the block implementation
 * of the {@link DropHandler} specifications.
 */
public class DBlockDropHandler implements DropHandler {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>DBlockDropHandler</code> instance.
   * @param block The block model.
   */
  public DBlockDropHandler(VBlock block) {
    this.block = block;
  }
  
  //---------------------------------------------------
  // DROPTARGETLISTENER IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void drop(DragAndDropEvent event) {
    if (isAccepted((WrapperTransferable)event.getTransferable())) {
      if (isChartBlockContext()) {
        fileList = new ArrayList<File>();
        filesCount = ((WrapperTransferable)event.getTransferable()).getFiles().length;
        for (int i = 0; i < filesCount; i++) {
	  ((WrapperTransferable)event.getTransferable()).getFiles()[i].setStreamVariable(new StreamHandler());
        }
      } else {
        ((WrapperTransferable)event.getTransferable()).getFiles()[0].setStreamVariable(new StreamHandler());
      }
    }
  }

  @Override
  public AcceptCriterion getAcceptCriterion() {
    return AcceptAll.get();
  }
  
  /**
   * Returns the application instance.
   * @return The application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
  }

  //---------------------------------------------------------
  // UTILS
  //---------------------------------------------------------
  
  /**
   * Launches the drop operation for a given file.
   * @param file The file instance.
   * @throws VException Visual errors.
   */
  private void acceptDrop(File file) {
    if (file != null) {
      try {
        if (isChartBlockContext()) {
          fileList.add(file);
          if (fileList.size() == filesCount){
            handleDrop(fileList);
          }
        } else {
          handleDrop(file, getExtension(file));
        }
      } catch (VException e) {
        // nothing to do
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns {@code true} is the data flavor is accepted.
   * @param transferable The {@link WrapperTransferable} instance.
   * @return {@code true} is the data flavor is accepted.
   */
  private boolean isAccepted(WrapperTransferable transferable) {
    ArrayList<Html5File>		flavors;

    flavors = new ArrayList<Html5File>();
    for (int i = 0; i < transferable.getFiles().length; i++) {
      flavors.add(transferable.getFiles()[i]);
    }
    if (isChartBlockContext()) {
      return isAccepted(flavors);
    } else {
      if (flavors.size() > 1) {
	return false;
      } else {
	return isAccepted(getExtension(flavors.get(0)));
      }
    }
  }

  /**
   * Returns {@code true} is the given data flavor is accepted for drop operation.
   * @param flavor The data flavor.
   * @return {@code true} is the given data flavor is accepted for drop operation.
   */
  private boolean isAccepted(String flavor) {
    return flavor != null && flavor.length() > 0 && block.isAccepted(flavor);
  }

  /**
   * A List of flavors is accepted if all elements
   * of the list are accepted and have the same extension
   * @param flavors The data flavors.
   * @return {@code true} when the drop operation succeeded.
   */
  private boolean isAccepted(ArrayList<Html5File> flavors) {
    String		oldFlavor = null;

    for (int i = 0; i < flavors.size(); i++) {
      String	newFlavor = getExtension(flavors.get(i));

      if ((oldFlavor != null && !newFlavor.equals(oldFlavor))
	  || !isAccepted(newFlavor))
      {
	return false;
      }

      oldFlavor = newFlavor;
    }

    return true;
  }

  /**
   * Handles drop action for multiple files in a chart block.
   * @param files The list of files to be dropped.
   * @return {@code true} when the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private boolean handleDrop(ArrayList<File> files)
    throws VException
  {
    for (int i = 0; i < files.size(); i++) {
      File	file = files.get(i);

      if (!handleDrop(file, getExtension(file))) {
	return false;
      }
    }

    return true;
  }

  /**
   * Handles drop operations for given flavors.
   * @param file The file instance.
   * @param flavor The data flavors.
   * @return {@code true} when the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private boolean handleDrop(File file, String flavor)
    throws VException
  {
    VField	target = block.getDropTarget(flavor);

    if (target == null) {
      return false;
    }
    
    target.onBeforeDrop();
    if (target instanceof VStringField) {
      if (target.getWidth() < file.getAbsolutePath().length()) {
	return false;
      } else {
        if (isChartBlockContext()) {
          int               rec;
          
          rec = getFirstUnfilledRecord(block, target);
          block.setActiveRecord(rec);
          ((VStringField)target).setString(rec, file.getAbsolutePath());
          target.onAfterDrop();
          block.setActiveRecord(rec + 1);
          block.gotoRecord(block.getActiveRecord());
          return true;
        } else {
	  ((VStringField)target).setString(file.getAbsolutePath());
	  target.onAfterDrop();
	  return true;
	}
      }
    } else if (target instanceof VImageField) {
      if (!target.isInternal()) {
	if (isImage(file)) {
	  return handleImage((VImageField)target, file);
	} else {
	  return false;
	}
      } else {
	return handleImage((VImageField)target, file);
      }
    } else {
      return false;
    }
  }

  /**
   * Handles the drop process for image files.
   * @param target The target image field.
   * @param file The file instance.
   * @return {@code true} is the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private boolean handleImage(VImageField target, File file)
    throws VException
  {
    if (isChartBlockContext()) {
      int               rec;
      
      rec = getFirstUnfilledRecord(block, target);
      block.setActiveRecord(rec);
      target.setImage(rec, toByteArray(file));
      target.onAfterDrop();
      block.setActiveRecord(rec + 1);
      block.gotoRecord(block.getActiveRecord());
      return true;
    } else {
      target.setImage(toByteArray(file));
      target.onAfterDrop();
      return true;
    }
  }

  /**
   * Returns {@code true} is the context block is chart block.
   * @return {@code true} is the context block is chart block.
   */
  private boolean isChartBlockContext() {
    return block.noDetail() || (block.isMulti() && !block.isDetailMode());
  }

  /**
   * Returns the file extension.
   * @param file The file instance.
   * @return The file extension.
   */
  private static String getExtension(File file) {
    String		extension = null;
    String 		name = file.getName();
    int 		index = name.lastIndexOf('.');

    if (index > 0 &&  index < name.length() - 1) {
      extension = name.substring(index + 1).toLowerCase();
    }

    return extension;
  }

  /**
   * Returns the file extension.
   * @param file The file instance.
   * @return The file extension.
   */
  private static String getExtension(Html5File file) {
    String		extension = null;
    String 		name = file.getFileName();
    int 		index = name.lastIndexOf('.');

    if (index > 0 &&  index < name.length() - 1) {
      extension = name.substring(index + 1).toLowerCase();
    }

    return extension;
  }

  /**
   * Returns {@code true} is the given file is an image.
   * @param file The file instance.
   * @return {@code true} is the given file is an image.
   */
  private static boolean isImage(File file) {
    String		mimeType;

    mimeType = MIMETYPES_FILE_TYPEMAP.getContentType(file);
    if(mimeType.split("/")[0].equals("image")) {
      return true;
    }

    return false;
  }

  /**
   * Returns the bytes of the given file.
   * @param file The file instance.
   * @return The file bytes.
   */
  private static byte[] toByteArray(File file) {
    try {
      ByteArrayOutputStream	baos = new ByteArrayOutputStream();

      copy(new FileInputStream(file), baos, 1024);
      return baos.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Copies the given input stream into the given output stream. 
   * @param input The input stream to be copied.
   * @param output The destination output stream.
   * @param bufferSize The buffer size.
   * @throws IOException I/O errors.
   */
  private static void copy(InputStream input, OutputStream output,  int bufferSize)
    throws IOException
  {
    byte[]	buf = new byte[bufferSize];
    int 	bytesRead = input.read(buf);

    while (bytesRead != -1) {
      output.write(buf, 0, bytesRead);
      bytesRead = input.read(buf);
    }

    output.flush();
  }
  
  /**
   * Looks for the first unfilled record according to the given target field.
   * @param block The block model.
   * @param target The target field.
   * @return The record for which the given field is {@code null}.
   */
  private static int getFirstUnfilledRecord(VBlock block, VField target) {
    for (int i = 0; i < block.getBufferSize(); i++) {
      if (target.isNull(i)) {
        return i;
      }
    }

    return 0;
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The <code>StreamHandler</code> is the block drop target handler
   * of the {@link StreamVariable} specifications.
   */
  @SuppressWarnings("serial")
  private final class StreamHandler implements StreamVariable {
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public OutputStream getOutputStream() {
      return bas;
    }
    
    @Override
    public boolean listenProgress() {
      return true;
    }
    
    @Override
    public void onProgress(StreamingProgressEvent event) {
      if (event.getContentLength() > 50 * 1024 * 1024) {
	// show progress bar only for file larger than 50 MB
	block.getForm().setCurrentJob((int)event.getBytesReceived());
      }
    }

    @Override
    public void streamingStarted(StreamingStartEvent event) {
      if (event.getContentLength() > 50 * 1024 * 1024) {
	block.getForm().setProgressDialog("", Long.valueOf(event.getContentLength()).intValue());
      }
    }

    @Override
    public void streamingFinished(StreamingEndEvent event) {
      try {
	File 			temp;
	FileOutputStream	out;
	      
        temp = createTempFile(event.getFileName());
        out = new FileOutputStream (temp);
        bas.writeTo(out);
        acceptDrop(temp);
      } catch (IOException e) {
	acceptDrop(null);
      } finally {
	if (event.getContentLength() > 50 * 1024 * 1024) {
	  block.getForm().unsetProgressDialog();
	}
      }
    }

    @Override
    public void streamingFailed(final StreamingErrorEvent event) {
      event.getException().printStackTrace(System.err);
      new Thread(new Runnable() {
        
        @Override
        public void run() {
          block.getForm().error(event.getException().getMessage());
          BackgroundThreadHandler.updateUI();
        }
      }).start();;
    }

    @Override
    public boolean isInterrupted() {
      return false;
    }
    
    /**
     * Creates a temporary file.
     * @param directory The parent directory.
     * @param defaultName The default file name.
     * @return The created temporary file.
     * @throws IOException I/O errors.
     */
    protected File createTempFile(String defaultName)
      throws IOException
    {
      String		basename;
      String		extension;
      
      basename = getBaseFileName(defaultName);
      extension = getExtension(defaultName);
      
      return File.createTempFile(basename, String.valueOf("." + extension), null);
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
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private ByteArrayOutputStream	bas = new ByteArrayOutputStream();
  }
  
  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private final VBlock				block;
  private static final MimetypesFileTypeMap	MIMETYPES_FILE_TYPEMAP = new MimetypesFileTypeMap();
  private ArrayList<File>                       fileList;   
  private int                                   filesCount;
  private static final long 			serialVersionUID = 3924306391945432925L;
  
  static {
    // missing PNG files in initial map
    MIMETYPES_FILE_TYPEMAP.addMimeTypes("image/png png");
  }
}
