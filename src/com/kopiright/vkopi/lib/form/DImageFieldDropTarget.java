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

package com.kopiright.vkopi.lib.form;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import com.kopiright.vkopi.lib.form.DImageField;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;

public class DImageFieldDropTarget implements DropTargetListener {

  public DImageFieldDropTarget(DImageField imageField) {
    this.imageField = imageField;
  }

  /**
   * Sets the drag and drop state
   * @param enabled
   */
  public void setDragEnabled(boolean enabled) {
    if (enabled) {
      dropTarget = new DropTarget (imageField.getTargetPane(), this);
    }
  }

  /**
   * Drops the image into the DImage Field
   * @param image The image to set to the field
   */
  private void dropImage(byte[] image) {
    imageField.getModel().setObject(image);
  }

  /**
   * Drops the image as a file
   * @param file
   * @throws VException
   */
  private void dropImage(File file) throws VException {

    if (file == null) {
      return;
    }

    try {
      FileInputStream	is = new FileInputStream(file);
      byte[]            b = new byte[is.available()];

      is.read(b);
      dropImage(b);
    } catch (Exception e) {
      throw new VExecFailedException("bad-file", e);
    }
  }

  /**
   * Drops an image as an URL
   * @param url
   * @throws IOException
   */
  private void dropImage(URL url) throws IOException {
    ByteArrayOutputStream	baos;
    InputStream 		is;
    int				r;
    byte[] 			buffer;

    baos = new ByteArrayOutputStream();
    is = url.openStream();
    buffer = new byte[is.available()];
    while ((r = is.read(buffer)) >= 0) {
      if (r == 0) {
	continue;
      }
      baos.write(buffer, 0, r);
    }
    is.close();
    dropImage(baos.toByteArray());
  }

  /**
   * Gets the drop target
   * @return the DropTarget element
   */
  public DropTarget getDropTarget() {
    return this.dropTarget;
  }

  /**
   * Override
   */
  public void dragEnter(DropTargetDragEvent dtde) {}

  /**
   * Override
   */
  public void dragOver(DropTargetDragEvent dtde) {}

  /**
   * Override
   */
  public void dropActionChanged(DropTargetDragEvent dtde) {}

  /**
   * Override
   */
  public void dragExit(DropTargetEvent dte) {}

  /**
   * Override
   */
  public void drop(DropTargetDropEvent dtde) {
    Transferable 	trans;

    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
    trans = dtde.getTransferable();
    acceptableType = false;
    // Only accept a flavor that returns an image
    try {
      // try to get an image
      if (trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	List 		list;
	ListIterator	it;

	list = (List)trans.getTransferData (DataFlavor.javaFileListFlavor);
	it = list.listIterator();
	while (it.hasNext()) {
	  File		file;

	  file = (File)it.next();
	  dropImage(file);
	}
	acceptableType = true;
      } else if (trans.isDataFlavorSupported (uriListFlavor)) {
	StringTokenizer		izer;
	String 			uris;

	uris = (String)trans.getTransferData (uriListFlavor);
	// url-lists are defined by rfc 2483 as crlf-delimited
	izer = new StringTokenizer (uris, "\r\n");
	while (izer.hasMoreTokens ()) {
	  String	uri;
	  File		file;

	  uri = izer.nextToken();
	  file = new File(uri.substring(7));
	  dropImage(file);
	}
	acceptableType = true;
      } else if (trans.isDataFlavorSupported (urlFlavor)) {
	URL	url;

	url = (URL) trans.getTransferData (urlFlavor);
	dropImage(url);
	acceptableType = true;
      } else {
	throw new VExecFailedException("File format is not supported");
      }
    } catch (VException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (UnsupportedFlavorException e) {
      e.printStackTrace();
    } finally {
      dtde.dropComplete(acceptableType);
    }

  }

  private DImageField		imageField;
  private DropTarget 		dropTarget;
  private boolean		acceptableType;
  private static DataFlavor	urlFlavor;
  private static DataFlavor	uriListFlavor;

  static {
    try {
      urlFlavor = new DataFlavor ("application/x-java-url; class=java.net.URL");
      uriListFlavor = new DataFlavor ("text/uri-list; class=java.lang.String");
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }
}
