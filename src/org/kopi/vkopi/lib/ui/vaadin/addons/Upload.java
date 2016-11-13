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

package org.kopi.vkopi.lib.ui.vaadin.addons;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.UploadClientRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.UploadServerRpc;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.upload.UploadState;

import com.vaadin.server.NoInputStreamException;
import com.vaadin.server.NoOutputStreamException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.EventId;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.util.ReflectTools;

@SuppressWarnings({ "serial", "deprecation" })
public class Upload extends AbstractComponent implements LegacyComponent {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------

  /**
   * Creates a new instance of Upload.
   *
   * The receiver must be set before performing an upload.
   */
  public Upload() {
    registerRpc(new UploadServerRpc() {
      
      @Override
      public void change(String filename) {
	fireEvent(new UploadChangeEvent(Upload.this, filename));
      }

      @Override
      public void cancel() {
	fireEvent(new UploadFailedEvent(Upload.this, null, null, 0));
      }
    });
  }

  /**
   * Creates a new instance of Upload.
   * @param uploadReceiver The upload receiver.
   */
  public Upload(UploadReceiver uploadReceiver) {
    this();
    receiver = uploadReceiver;
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  @Override
  public void changeVariables(Object source, Map<String, Object> variables) {
    if (variables.containsKey("pollForStart")) {
      int	id = (Integer) variables.get("pollForStart");

      if (!isUploading && id == nextid) {
	notStarted = true;
	markAsDirty();
      }
    }
  }

  @Override
  public void paintContent(PaintTarget target) throws PaintException {
    if (notStarted) {
      target.addAttribute("notStarted", true);
      notStarted = false;
      return;
    }

    target.addAttribute("nextid", nextid);

    // Post file to this strean variable
    target.addVariable(this, "action", getStreamVariable());
  }

  /**
   * Adds the upload started event listener.
   * @param listener the Listener to be added.
   */
  public void addStartedListener(UploadStartedListener listener) {
    addListener(UploadStartedEvent.class, listener, UPLOAD_STARTED_METHOD);
  }

  /**
   * Removes the upload started event listener.
   * @param listener the Listener to be removed.
   */
  public void removeStartedListener(UploadStartedListener listener) {
    removeListener(UploadStartedEvent.class, listener, UPLOAD_STARTED_METHOD);
  }


  /**
   * Adds the upload received event listener.
   * @param listener the Listener to be added.
   */
  public void addFinishedListener(UploadFinishedListener listener) {
    addListener(UploadFinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
  }

  /**
   * Removes the upload received event listener.
   *
   * @param listener
   *            the Listener to be removed.
   */
  public void removeFinishedListener(UploadFinishedListener listener) {
    removeListener(UploadFinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
  }

  /**
   * Adds the upload interrupted event listener.
   * @param listener the Listener to be added.
   */
  public void addFailedListener(UploadFailedListener listener) {
    addListener(UploadFailedEvent.class, listener, UPLOAD_FAILED_METHOD);
  }

  /**
   * Removes the upload interrupted event listener.
   * @param listener the Listener to be removed.
   */
  public void removeFailedListener(UploadFailedListener listener) {
    removeListener(UploadFailedEvent.class, listener, UPLOAD_FAILED_METHOD);
  }

  /**
   * Adds the upload success event listener.
   * @param listener the Listener to be added.
   */
  public void addSucceededListener(UploadSucceededListener listener) {
    addListener(UploadSucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
  }

  /**
   * Removes the upload success event listener.
   * @param listener the Listener to be removed.
   */
  public void removeSucceededListener(UploadSucceededListener listener) {
    removeListener(UploadSucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
  }

  /**
   * Adds the upload progress event listener.
   * @param listener The progress listener to be added
   */
  public void addProgressListener(UploadProgressListener listener) {
    if (progressListeners == null) {
      progressListeners = new LinkedHashSet<UploadProgressListener>();
    }
    
    progressListeners.add(listener);
  }

  /**
   * Removes the upload progress event listener.
   *
   * @param listener The progress listener to be removed
   */
  public void removeProgressListener(UploadProgressListener listener) {
    if (progressListeners != null) {
      progressListeners.remove(listener);
    }
  }

  /**
   * Adds a filename change event listener
   * @param listener The Listener to add
   */
  public void addChangeListener(UploadChangeListener listener) {
    addListener(EventId.CHANGE, UploadChangeEvent.class, listener, FILENAME_CHANGED);
  }

  /**
   * Removes a filename change event listener
   * @param listener The listener to be removed
   */
  public void removeChangeListener(UploadChangeListener listener) {
    removeListener(EventId.CHANGE, UploadChangeEvent.class, listener);
  }
  
  /**
   * Emit upload received event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   */
  protected void fireStarted(String filename, String MIMEType) {
    fireEvent(new UploadStartedEvent(this, filename, MIMEType, contentLength));
  }

  /**
   * Emits the upload failed event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   */
  protected void fireUploadInterrupted(String filename, String MIMEType, long length) {
    fireEvent(new UploadFailedEvent(this, filename, MIMEType, length));
  }

  /**
   * Emits the upload failed event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   */
  protected void fireNoInputStream(String filename, String MIMEType, long length) {
    fireEvent(new NoInputStreamEvent(this, filename, MIMEType, length));
  }

  /**
   * Emits the upload failed event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   */
  protected void fireNoOutputStream(String filename, String MIMEType, long length) {
    fireEvent(new NoOutputStreamEvent(this, filename, MIMEType, length));
  }

  /**
   * Emits the upload failed event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   */
  protected void fireUploadInterrupted(String filename, String MIMEType, long length, Exception e) {
    fireEvent(new UploadFailedEvent(this, filename, MIMEType, length, e));
  }

  /**
   * Emits the upload success event.
   *
   * @param filename
   * @param MIMEType
   * @param length
   *
   */
  protected void fireUploadSuccess(String filename, String MIMEType, long length) {
    fireEvent(new UploadSucceededEvent(this, filename, MIMEType, length));
  }

  /**
   * Emits the progress event.
   *
   * @param totalBytes bytes received so far
   * @param contentLength actual size of the file being uploaded, if known
   *
   */
  protected void fireUpdateProgress(long totalBytes, long contentLength) {
    if (progressListeners != null) {
      for (Iterator<UploadProgressListener> it = progressListeners.iterator(); it.hasNext();) {
	UploadProgressListener l = it.next();
	l.updateProgress(totalBytes, contentLength);
      }
    }
  }

  /**
   * Returns the current receiver.
   *
   * @return the StreamVariable.
   */
  public UploadReceiver getReceiver() {
    return receiver;
  }

  /**
   * Sets the receiver.
   *
   * @param receiver
   *            the receiver to set.
   */
  public void setReceiver(UploadReceiver receiver) {
    this.receiver = receiver;
  }

  /**
   * Go into upload state. This is to prevent double uploading on same
   * component.
   *
   * Warning: this is an internal method used by the framework and should not
   * be used by user of the Upload component. Using it results in the Upload
   * component going in wrong state and not working. It is currently public
   * because it is used by another class.
   */
  public void startUpload() {
    if (isUploading) {
      throw new IllegalStateException("uploading already started");
    }
    isUploading = true;
    nextid++;
  }

  /**
   * Interrupts the upload currently being received. The interruption will be
   * done by the receiving tread so this method will return immediately and
   * the actual interrupt will happen a bit later.
   */
  public void interruptUpload() {
    if (isUploading) {
      interrupted = true;
    }
  }

  /**
   * Go into state where new uploading can begin.
   *
   * Warning: this is an internal method used by the framework and should not
   * be used by user of the Upload component.
   */
  private void endUpload() {
    isUploading = false;
    contentLength = -1;
    interrupted = false;
    markAsDirty();
  }

  /**
   * Returns {@code true} if upload operation is being performed.
   * @return {@code true} if upload operation is being performed.
   */
  public boolean isUploading() {
    return isUploading;
  }

  /**
   * Gets read bytes of the file currently being uploaded.
   *
   * @return bytes
   */
  public long getBytesRead() {
    return totalBytes;
  }

  /**
   * Returns size of file currently being uploaded. Value sane only during
   * upload.
   *
   * @return size in bytes
   */
  public long getUploadSize() {
    return contentLength;
  }

  /**
   * Forces the upload the send selected file to the server.
   * <p>
   * In case developer wants to use this feature, he/she will most probably
   * want to hide the uploads internal submit button by setting its caption to
   * null with {@link #setButtonCaption(String)} method.
   * <p>
   * Note, that the upload runs asynchronous. Developer should use normal
   * upload listeners to trac the process of upload. If the field is empty
   * uploaded the file name will be empty string and file length 0 in the
   * upload finished event.
   * <p>
   * Also note, that the developer should not remove or modify the upload in
   * the same user transaction where the upload submit is requested. The
   * upload may safely be hidden or removed once the upload started event is
   * fired.
   */
  public void submitUpload() {
    markAsDirty();
    getRpcProxy(UploadClientRpc.class).submitUpload();
  }
  
  /**
   * Notifies the client side the the upload is on progress.
   * @param contentLength The total length of the upload.
   * @param receivedBytes The received bytes.
   */
  public void fireOnProgress(long contentLength, long receivedBytes) {
    getRpcProxy(UploadClientRpc.class).onProgress(contentLength, receivedBytes);
  }
  
  /**
   * Sets the upload locale.
   * @param locale The upload locale
   */
  public void setLocale(String locale) {
    getState().locale = locale;
  }
  
  /**
   * Sets the mime type to be selected.
   * @param mimeType The mime type to be selected.
   */
  public void setMimeType(String mimeType) {
    getState().mimeType = mimeType;
  }
  
  @Override
  protected UploadState getState() {
    return (UploadState) super.getState();
  }
  
  /**
   * Returns the stream variable.
   * @return The stream variable.
   */
  protected com.vaadin.server.StreamVariable getStreamVariable() {
    if (streamVariable == null) {
      streamVariable = new com.vaadin.server.StreamVariable() {

	@Override
	public boolean listenProgress() {
	  return (progressListeners != null && !progressListeners.isEmpty());
	}

	@Override
	public void onProgress(StreamingProgressEvent event) {
	  fireUpdateProgress(event.getBytesReceived(), event.getContentLength());
	}

	@Override
	public boolean isInterrupted() {
	  return interrupted;
	}

	@Override
	public OutputStream getOutputStream() {
	  if (getReceiver() == null) {
	    throw new IllegalStateException("Upload cannot be performed without a receiver set");
	  }
	  
	  OutputStream receiveUpload = getReceiver().receiveUpload(lastStartedEvent.getFileName(), lastStartedEvent.getMimeType());
	  lastStartedEvent = null;
	  return receiveUpload;
	}

	@Override
	public void streamingStarted(StreamingStartEvent event) {
	  startUpload();
	  contentLength = event.getContentLength();
	  fireStarted(event.getFileName(), event.getMimeType());
	  lastStartedEvent = event;
	}

	@Override
	public void streamingFinished(StreamingEndEvent event) {
	  fireUploadSuccess(event.getFileName(), event.getMimeType(), event.getContentLength());
	  endUpload();
	}

	@Override
	public void streamingFailed(StreamingErrorEvent event) {
	  try {
	    Exception exception = event.getException();
	    if (exception instanceof NoInputStreamException) {
	      fireNoInputStream(event.getFileName(), event.getMimeType(), 0);
	    } else if (exception instanceof NoOutputStreamException) {
	      fireNoOutputStream(event.getFileName(), event.getMimeType(), 0);
	    } else {
	      fireUploadInterrupted(event.getFileName(), event.getMimeType(), 0, exception);
	    }
	  } finally {
	    endUpload();
	  }
	}
	
	//-----------------------------------
	// DATA MEMBERS
	//-----------------------------------
	
	private StreamingStartEvent 	lastStartedEvent;
      };
    }
    
    return streamVariable;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  /**
   * The output of the upload is redirected to this receiver.
   */
  private UploadReceiver 			receiver;
  private boolean 				isUploading;
  private long 					contentLength = -1;
  private int 					totalBytes;

  /**
   * ProgressListeners to which information about progress is sent during
   * upload
   */
  private LinkedHashSet<UploadProgressListener> progressListeners;
  private boolean 				interrupted = false;
  private boolean 				notStarted;
  private int					nextid;

  /*
   * Handle to terminal via Upload monitors and controls the upload during it
   * is being streamed.
   */
  private com.vaadin.server.StreamVariable 	streamVariable;
  
  private static final Method 			UPLOAD_FINISHED_METHOD;
  private static final Method 			UPLOAD_FAILED_METHOD;
  private static final Method 			UPLOAD_SUCCEEDED_METHOD;
  private static final Method 			UPLOAD_STARTED_METHOD;
  private static final Method 			FILENAME_CHANGED;

  static {
    UPLOAD_FINISHED_METHOD = ReflectTools.findMethod(UploadFinishedListener.class, "uploadFinished", UploadFinishedEvent.class);
    UPLOAD_FAILED_METHOD = ReflectTools.findMethod(UploadFailedListener.class, "uploadFailed", UploadFailedEvent.class);
    UPLOAD_STARTED_METHOD = ReflectTools.findMethod(UploadStartedListener.class, "uploadStarted", UploadStartedEvent.class);
    UPLOAD_SUCCEEDED_METHOD = ReflectTools.findMethod(UploadSucceededListener.class, "uploadSucceeded", UploadSucceededEvent.class);
    FILENAME_CHANGED = ReflectTools.findMethod(UploadChangeListener.class, "filenameChanged", UploadChangeEvent.class);
  }
}
