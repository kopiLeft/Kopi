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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorImageFieldState;

import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

/**
 * The server side implementation of an image grid editor.
 */
@SuppressWarnings("serial")
public class GridEditorImageField extends GridEditorField<Resource> {

  /**
   * Sets the associated image with this field.
   * @param image The field image.
   */
  public void setImage(byte[] image) {
    setValue(new ImageResource(new ImageStreamSource(image)), true, true);
    setIcon(getValue());
  }
  
  /**
   * Sets the image width.
   * @param width The image width in pixels.
   */
  public void setImageWidth(int width) {
    getState().imageWidth = width;
  }
  
  /**
   * Sets the image height.
   * @param width The image height in pixels.
   */
  public void setImageHeight(int height) {
    getState().imageHeight = height;
  }
  
  @Override
  public Class<? extends Resource> getType() {
    return Resource.class;
  }
  
  @Override
  public EditorImageFieldState getState() {
    return (EditorImageFieldState) super.getState();
  }

  /**
   * Creates a VAADIN resource from a binary stream.
   */
  public static class ImageResource extends StreamResource {

    public ImageResource(StreamSource streamSource) {
      super(streamSource, generateFileName());
      setCacheTime(0l);
    }
    
    @Override
    public String getMIMEType() {
      return "image/*";
    }
    
    /**
     * Returns the image byte stream.
     * @return The image byte stream.
     */
    public byte[] getImage() {
      return ((ImageStreamSource)getStreamSource()).getImage();
    }
    
    /**
     * Generates a dummy file name.
     * @return The generated file name.
     */
    private static String generateFileName() {
      return "image" + System.currentTimeMillis();
    }
  }
  
  /**
   * The stream provider for the image editor. 
   */
  public static class ImageStreamSource implements StreamSource {

    public ImageStreamSource(byte[] image) {
      this.image = image;
    }
    
    @Override
    public InputStream getStream() {
      return new ByteArrayInputStream(image);
    }
    
    /**
     * Returns the image byte stream.
     * @return The image byte stream.
     */
    public byte[] getImage() {
      return image;
    }
    
    private final byte[]        image;
  }
}
