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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.vaadin.peter.imagescaler.ImageScaler;

import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.form.VImageField;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

@SuppressWarnings("serial")
public class DImageField extends DObjectField {

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
    this.width = width;
    this.height = height;
    scaler = new ImageScaler();
    scaler.setImmediate(true);
    empty.addStyleName(KopiTheme.FIELD_IMAGE);
    empty.setContent(scaler);
    scaler.setWidth(width, Unit.PIXELS);
    scaler.setHeight(height, Unit.PIXELS);
    empty.addShortcutListener(new ShortcutListener("clear", KeyCode.BACKSPACE, null) {

      @Override
      public void handleAction(Object sender, Object target) {
  	if (target == empty) {
  	  setObject(null);
  	}
      }
    });
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
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
	empty.addStyleName("blink");
      }
    });
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  @Override
  public void updateAccess() {
    label.update(getModel(), getPosition());
  }

  @Override
  public void updateText() {
    setObject(((VImageField)getModel()).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())));
    super.updateText();
  }
  
  @Override
  public void updateFocus() {
    label.update(getModel(), getPosition());
    
    super.updateFocus();
  }
  
  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  public void setObject(final Object s) {
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() {
        if (s == null) {
          if (scaler.getImage() != null) {
	    scaler.setImage(null, width, height);
          }     
          image = null;
          return;
        }
        image = (byte[])s;

        if (image != null) {
          scaler.setImage(new DynamicImageResource(new ImageStreamSource(image),""),
	                  width,
	                  height);
        } 
        setBlink(false);
        setBlink(true);
      }
    });
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
    public DynamicImageResource(StreamSource streamSource,
	                        String fileName)
    {
      super(streamSource, fileName);
      //setMIMEType(ImageFileChooser.IMAGES_MIME_TYPE); //depend on ImageFileChooser implementation
      setCacheTime(0l);
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private byte[]			image;
  private int				width;
  private int				height;
  private ImageScaler			scaler;
}
