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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import com.kopiright.vaadin.component.fileupload.UploadWindow;
import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

/**
 * The <code>FileUploader</code> handles all client to server file upload operations.
 * <p>
 * Regarding to the application, there is only one instance of this uploader. Its attached
 * to the application at the UI start up.
 * </p>
 */
public class FileUploader extends Panel
  implements UComponent, Receiver, StartedListener, FinishedListener, SucceededListener,FailedListener
{
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>FileUploader</code> instance
   */
  public FileUploader() {
    fileChooser = new UploadWindow("", this);
    fileChooser.setImmediate(true);
    fileChooser.addSucceededListener((SucceededListener)this);
    fileChooser.addStartedListener((StartedListener)this);
    fileChooser.addFinishedListener((FinishedListener)this);
    fileChooser.addFailedListener((FailedListener)this);
  }
  
  //---------------------------------------------------
  // UTILS
  //---------------------------------------------------
  
  /**
   * Returns the file uploader instance.
   * @return The file uploader instance.
   */
  public static FileUploader get() { 
    if (instance == null) {
      instance = new FileUploader();
    }
    
    return instance;
  }
  
  /**
   * Invokes the upload process for a given mime type.
   * @param mimeType The mime type to be uploaded.
   * @return The uploaded bytes.
   */
  public byte[] invoke(String mimeType) {
    this.mimeType = mimeType;
    
    if (mimeType != null) {
      fileChooser.setFilter(mimeType);
    }
    fileChooser.chooseFile();
    synchronized (fileChooser) {
      try {
        fileChooser.wait();
      } catch (InterruptedException e) {
	e.printStackTrace();
      } 
    }
    focus();
    UI.getCurrent().push();
    
    return output.toByteArray();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * @override
   */
  public void uploadFailed(FailedEvent event) {
    event.getReason().printStackTrace();
    ApplicationContext.getApplicationContext().getApplication().displayError(this, event.getReason().getMessage());
  }
  
  /**
   * @override
   */
  public void uploadSucceeded(SucceededEvent event) {
    // nothing to do
  }
  
  /**
   * @override
   */
  public void uploadFinished(FinishedEvent event) {
    synchronized (fileChooser) {
      fileChooser.notify(); 
    }
  }
  
  /**
   * @override
   */
  public void uploadStarted(StartedEvent event) {
    if (mimeType != null && !event.getMIMEType().startsWith(getParentMIMEType())) {
      fileChooser.interruptUpload();
    }
  }
  
  /**
   * @override
   */
  public OutputStream receiveUpload(String filename, String mimeType) {
    output = new ByteArrayOutputStream();
    try {
      selectedFile = File.createTempFile(filename, "");
      new FileOutputStream(selectedFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return output;
  }
  
  /**
   * Marks the end of the upload process.
   * @param filename The uploaded file name.
   * @param mimeType The uploaded mime type.
   * @param uploaded The uploaded bytes.
   * @throws VException Upload errors.
   */
  protected void fireUploadFinished(String filename,
			            String mimeType,
	                            byte[] uploaded)
    throws VException
  {
    if (uploadHandler != null) {
      uploadHandler.uploadFinished(new UploadEvent(this, filename, mimeType, uploaded));
    }
  }
  
  /**
   * Returns the parent mime type of the holded mime type.
   * @return The parent mime type of the holded mime type.
   */
  private String getParentMIMEType() {
    return mimeType.substring(0, mimeType.indexOf('/'));
  }
  
  //---------------------------------------------------
  // UPLOAD LISTENER
  //---------------------------------------------------
  
  /**
   * The <code>FileUploadHandler</code> is a listener to
   * notify registered object of an upload process finish
   */
  public interface FileUploadHandler extends Serializable {
    
    /**
     * Fired when the upload operation has been successfully executed;
     * @param event The upload event
     */
    public void uploadFinished(UploadEvent event) throws VException;
  }
  
  //---------------------------------------------------
  // UPLOAD EVENT
  //---------------------------------------------------

  /**
   * The <code>UploadEvent</code> is a {@link Component.Event}
   * holding an upload finish event.
   */
  public static class UploadEvent extends Component.Event {

    //---------------------------------------
    // CONSTRUCTORS
    //---------------------------------------
    
    /**
     * Creates a new <code>UploadEvent</code> instance.
     * @param source The event source.
     * @param filename The uploaded file name.
     * @param mimeType The mime type to be uploaded.
     * @param uploaded The uploaded bytes.
     */
    public UploadEvent(FileUploader source,
		       String filename,
		       String mimeType,
	               byte[] uploaded)
    {
      super(source);
      this.filename = filename;
      this.mimeType = mimeType;
      this.uploaded = uploaded;
    }

    //---------------------------------------
    // ACCESSORS
    //---------------------------------------
    
    /**
     * Returns the uploaded bytes.
     * @return The uploaded bytes.
     */
    public byte[] getUploaded() {
      return uploaded;
    }
    
    /**
     * Returns the event source.
     * @return The event source.
     */
    public FileUploader getUploader() {
      return (FileUploader) source;
    }

    /**
     * Returns the file name.
     * @return The file name.
     */
    public String getFilename() {
      return filename;
    }

    /**
     * Returns the mime type.
     * @return The mime type.
     */
    public String getMimeType() {
      return mimeType;
    }
    
    //-----------------------------------------
    // DATA MEMBERS
    //-----------------------------------------
    
    private final byte[]		uploaded;
    private final String		filename;
    private final String		mimeType;
    
    private static final long	        serialVersionUID = 1414582850937188939L;
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the file chooser.
   * @return The file chooser.
   */
  public UploadWindow geFileChooser() {
    return fileChooser;
  }
  
  /**
   * Returns the output stream.
   * @return The output stream.
   */
  public ByteArrayOutputStream getOutput() {
    return output;
  }

  /**
   * Sets the output stream.
   * @param output The output stream.
   */
  public void setOutput(ByteArrayOutputStream output) {
    this.output = output;
  }
  
  /**
   * Returns the selected file.
   * @return The selected file.
   */
  public File getSelectedFile() {
    return selectedFile;
  }

  //--------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------

  private final UploadWindow			fileChooser;
  private File 					selectedFile;
  private ByteArrayOutputStream			output;
  private String				mimeType;
  private FileUploadHandler 			uploadHandler;
  private static FileUploader			instance;
  private static final long 			serialVersionUID = -598412676724701973L;
}
