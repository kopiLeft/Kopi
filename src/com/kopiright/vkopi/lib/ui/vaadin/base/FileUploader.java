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
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.kopi.vaadin.addons.Upload;
import org.kopi.vaadin.addons.UploadFailedEvent;
import org.kopi.vaadin.addons.UploadFailedListener;
import org.kopi.vaadin.addons.UploadFinishedEvent;
import org.kopi.vaadin.addons.UploadFinishedListener;
import org.kopi.vaadin.addons.UploadReceiver;
import org.kopi.vaadin.addons.UploadStartedEvent;
import org.kopi.vaadin.addons.UploadStartedListener;
import org.kopi.vaadin.addons.UploadSucceededEvent;
import org.kopi.vaadin.addons.UploadSucceededListener;

import com.kopiright.vkopi.lib.ui.vaadin.visual.VApplication;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.VException;
import com.vaadin.ui.Component;

/**
 * The <code>FileUploader</code> handles all client to server file upload operations.
 * <p>
 * Regarding to the application, there is only one instance of this uploader. Its attached
 * to the application at the UI start up.
 * </p>
 */
@SuppressWarnings("serial")
public class FileUploader implements UploadReceiver, UploadStartedListener, UploadFinishedListener, UploadSucceededListener, UploadFailedListener {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates a new <code>FileUploader</code> instance
   */
  public FileUploader() {
    listeners = new LinkedList<FileUploader.FileUploadListener>();
    uploader = new Upload(this);
    uploader.setImmediate(true);
    uploader.addSucceededListener(this);
    uploader.addStartedListener(this);
    uploader.addFinishedListener(this);
    uploader.addFailedListener(this);
  }
  
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Invokes the upload process for a given mime type.
   * @param mimeType The mime type to be uploaded.
   * @return The uploaded bytes.
   */
  public byte[] upload(final String mimeType) {
    this.mimeType = mimeType;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	uploader.upload(mimeType);
      }
    });

    synchronized (uploader) {
      try {
	uploader.wait();
      } catch (InterruptedException e) {
	e.printStackTrace();
      } 
    }

    if (output != null) {
      return output.toByteArray();
    } else {
      return null;
    }
  }
  
  /**
   * Returns the uploader component.
   * @return The uploader component.
   */
  public Upload getUploader() {
    return uploader;
  }
  
  /**
   * Registers a new {@link FileUploadListener} object.
   * @param l The listener object.
   */
  public void addFileUploadListener(FileUploadListener l) {
    listeners.add(l);
  }
  
  /**
   * Removes a new {@link FileUploadListener} object.
   * @param l The listener object.
   */
  public void removeFileUploadListener(FileUploadListener l) {
    listeners.remove(l);
  }
  
  
  @Override
  public void uploadFailed(UploadFailedEvent event) {
    if (event.getReason() != null) {
      event.getReason().printStackTrace(System.err);
      getApplication().displayError(null, event.getReason().getMessage());
    }
  }

  @Override
  public void uploadSucceeded(UploadSucceededEvent event) {
    
  }

  @Override
  public void uploadFinished(UploadFinishedEvent event) {
    synchronized (uploader) {
      uploader.notify(); 
    }
  }

  @Override
  public void uploadStarted(UploadStartedEvent event) {
    if (mimeType != null && !event.getMIMEType().startsWith(getParentMIMEType())) {
      uploader.interruptUpload();
    }
  }
  
  @Override
  public OutputStream receiveUpload(String filename, String mimeType) {
    output = new ByteArrayOutputStream();
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
    for (FileUploadListener listener : listeners) {
      if (listener != null) {
	listener.uploadFinished(new UploadEvent(uploader, filename, mimeType, uploaded));
      }
    }
  }
  
  /**
   * Returns the current application instance.
   * @return The current application instance.
   */
  protected VApplication getApplication() {
    return (VApplication) ApplicationContext.getApplicationContext().getApplication();
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
   * The <code>FileUploadListener</code> is a listener to
   * notify registered object of an upload process finish
   */
  public interface FileUploadListener extends Serializable {
    
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
    public UploadEvent(Upload source,
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
    public Upload getUploader() {
      return (Upload) source;
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
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final byte[]		uploaded;
    private final String		filename;
    private final String		mimeType;
  }
  
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  
  /**
   * Returns the file uploader.
   * @return The file uploader.
   */
  public Upload geUploader() {
    return uploader;
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

  //--------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------

  private final Upload				uploader;
  private ByteArrayOutputStream			output;
  private String				mimeType;
  private List<FileUploadListener>		listeners;
}
