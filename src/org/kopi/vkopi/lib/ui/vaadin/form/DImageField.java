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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.ui.vaadin.addons.ImageField;
import org.kopi.vkopi.lib.ui.vaadin.addons.ImageFieldListener;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.xkopi.lib.type.Date;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;

/**
 * The image field implementation based on the customized VAADIN
 * addons.
 */
@SuppressWarnings("serial")
public class DImageField extends DObjectField implements DropHandler, ImageFieldListener {

  // --------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------
  
  /**
   * Creates a new <code>DImageField</code> instance.
   * @param model The field model.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options
   * @param width The field width.
   * @param height The field height.
   * @param detail Does the field belongs to detail view ?
   */
  public DImageField(VFieldUI model,
	             DLabel label,
		     int align,
		     int options,
		     int width,
	             int height,
                     boolean detail)
  {
    super(model, label, align, options, detail);
    field = new ImageField();
    field.setImmediate(true);
    field.setImageWidth(width);
    field.setImageHeight(height);
    field.addObjectFieldListener(this);
    field.addImageFieldListener(this);
    field.setWidth(width, Unit.PIXELS);
    field.setHeight(height, Unit.PIXELS);
    wrapper = new DragAndDropWrapper(field);
    wrapper.setImmediate(true);
    wrapper.setDropHandler(this);
    wrapper.setDragStartMode(DragStartMode.HTML5);
    setContent(wrapper);
  }

  // --------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // --------------------------------------------------

  @Override
  public Object getObject() {
    return image;
  }

  @Override
  public void setBlink(boolean b) {
    // TODO
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  @Override
  public void updateAccess() {
     label.update(model, getPosition());
  }

  @Override
  public void updateText() {
    setObject(((VImageField)getModel()).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())));
    super.updateText();
  }
  
  @Override
  public void updateFocus() {
    label.update(model, getPosition());
    super.updateFocus();
  }
  
  @Override
  public void updateColor() {
    // color properties are not set for an image field.
  }
  
  @Override
  public void onRemove() {
    setObject(null);
  }
  
  @Override
  public void onImageClick() {
    performAutoFillAction();
  }
  
  @Override
  public void drop(DragAndDropEvent event) {
    Html5File[]	files = ((WrapperTransferable)event.getTransferable()).getFiles();
    
    if (files.length > 0) {
      // even if there are multiple images dropped, we take only the first one.
      Html5File		image;
      
      image = files[0];
      // look if it is an image
      if (image.getType() != null && image.getType().contains("image/")) {
	image.setStreamVariable(new ImageStreamHandler());
      }
    }
  }

  @Override
  public AcceptCriterion getAcceptCriterion() {
    return AcceptAll.get();
  }
  
  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  public void setObject(final Object s) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (s == null) {
          field.setIcon(null);
        } else {
          field.setIcon(new DynamicImageResource(new ImageStreamSource((byte[])s), createFileName("image")));
          setBlink(false);
          setBlink(true);
        }
      }
    });
    image = (byte[])s;
  }
  
  /**
   * Creates the dynamic image name.
   * @param baseName The base name.
   * @return The dynamic image name.
   */
  protected String createFileName(String baseName) {
    return baseName + Date.now().format("yyyyMMddHHmmssSSS") + ".png";
  }
  
  //---------------------------------------------------
  // STREAM RESOURCE
  //---------------------------------------------------
  
  /**
   * The <code>ImageStreamSource</code> is the {@link StreamSource}
   * for an image field.
   */
  /*package*/ final class ImageStreamSource implements StreamSource {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ImageStreamSource</code> instance.
     * @param image The image content.
     */
    public ImageStreamSource(byte[] image) {
      this.image = image;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public InputStream getStream() {
      if (image == null) {
	return null;
      }
      
      return new ByteArrayInputStream(image);
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final byte[]		image;
  }
  
  /**
   * A dynamic {@link StreamResource} for an image field.
   */
  /*package*/ final class DynamicImageResource extends StreamResource {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>DynamicImageResource</code> instance.
     * @param streamSource The {@link StreamSource} object.
     * @param fileName The file name.
     */
    public DynamicImageResource(StreamSource streamSource, String fileName) {
      super(streamSource, fileName);
      setCacheTime(0l);
    }
  }
  
  /**
   * The image stream handler for reading uploaded stream from DnD
   * operations.
   */
  /*package*/ final class ImageStreamHandler implements StreamVariable {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public ImageStreamHandler() {
      output = new ByteArrayOutputStream();
    }

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public OutputStream getOutputStream() {
      return output;
    }

    @Override
    public boolean listenProgress() {
      return true;
    }

    @Override
    public void onProgress(StreamingProgressEvent event) {
      // show progress only when the file is bigger than 50MB
      if (event.getContentLength() > (50 * 1024 * 1024)) {
	getModel().getForm().setCurrentJob((int)event.getBytesReceived());
      }
    }

    @Override
    public void streamingStarted(StreamingStartEvent event) {
      // show progress only when the file is bigger than 50MB
      if (event.getContentLength() > (50 * 1024 * 1024)) {
	getModel().getForm().setProgressDialog("", (int)event.getContentLength());
      }
    }

    @Override
    public void streamingFinished(StreamingEndEvent event) {
      try {
	setObject(output.toByteArray());
      } finally {
	if (event.getContentLength() > (50 * 1024 * 1024)) {
	  getModel().getForm().unsetProgressDialog();
	}
      }
    }

    @Override
    public void streamingFailed(final StreamingErrorEvent event) {
      event.getException().printStackTrace(System.err);
      new Thread(new Runnable() {
        
        @Override
        public void run() {
          getModel().getForm().error(event.getException().getMessage());
          BackgroundThreadHandler.updateUI();
        }
      }).start();;
    }

    @Override
    public boolean isInterrupted() {
      return false; // never interrupt
    }
    
    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private final ByteArrayOutputStream		output;
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private byte[]				image;
  private ImageField				field;
  private final DragAndDropWrapper		wrapper;
}
