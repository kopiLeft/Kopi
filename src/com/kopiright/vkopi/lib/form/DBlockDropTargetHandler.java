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
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

public class DBlockDropTargetHandler implements DropTargetListener {

  //---------------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------------

  public DBlockDropTargetHandler(VBlock block) {
    this.block = block;
  }

  //---------------------------------------------------------
  // DROPTARGETLISTENER IMPLEMENTATION
  //---------------------------------------------------------

  public void dragEnter(DropTargetDragEvent dtde) {
    dragOver(dtde);
  }

  public void dragOver(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_COPY);

    if (!isAccepted(dtde.getTransferable())) {
      dtde.rejectDrag();
    }
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {}

  public void dragExit(DropTargetEvent dte) {}

  public void drop(DropTargetDropEvent dtde) {
    dtde.acceptDrop(DnDConstants.ACTION_COPY);

    try {
      if (isChartBlockContext()) {
	dtde.dropComplete(handleDrop((List)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)));
      } else {
	File		flavor;

	flavor = (File)((List)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).get(0);
	dtde.dropComplete(handleDrop(flavor, getExtension(flavor)));
      }
    } catch (UnsupportedFlavorException e) {
      dtde.dropComplete(false);
    } catch (IOException e) {
      dtde.dropComplete(false);
    }
  }

  //---------------------------------------------------------
  // UTILS
  //---------------------------------------------------------

  private boolean isAccepted(Transferable transferable) {
    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      try {
	List		flavors;

	flavors = (List)transferable.getTransferData(DataFlavor.javaFileListFlavor);

	if (isChartBlockContext()) {
	  return isAccepted(flavors);
	} else {
	  if (flavors.size() > 1) {
	    return false;
	  } else {
	    return isAccepted(getExtension((File)flavors.get(0)));
	  }
	}
      } catch (UnsupportedFlavorException e) {
	return false;
      } catch (IOException e) {
	return false;
      }
    } else {
      return false;
    }
  }

  private boolean isAccepted(String flavor) {
    return flavor != null && flavor.length() > 0 && block.isAccepted(flavor);
  }

  /**
   * A List of flavors is accepted if all elements
   * of the list are accepted and have the same extension
   */
  private boolean isAccepted(List flavors) {
    String		oldFlavor = null;

    for (int i = 0; i < flavors.size(); i++) {
      String	newFlavor = getExtension((File)flavors.get(i));

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
   * Handles drop action for multiple files in a chart block
   */
  private boolean handleDrop(List files) {
    for (int i = 0; i < files.size(); i++) {
      File	file = (File)files.get(i);

      if (!handleDrop(file, getExtension(file))) {
	return false;
      }
    }

    return true;
  }

  private boolean handleDrop(File file, String flavor) {
    VField	target = block.getDropTarget(flavor);

    if (target == null) {
      return false;
    }

    if (target instanceof VStringField) {
      if (target.getWidth() < file.getAbsolutePath().length()) {
	return false;
      } else {
	if (isChartBlockContext()) {
	  if (block.getActiveRecord() != -1) {
	    ((VStringField)target).setString(block.getActiveRecord(), file.getAbsolutePath());
	    block.setActiveRecord(block.getActiveRecord() + 1);
	    return true;
	  } else {
	    return false;
	  }
	} else {
	  ((VStringField)target).setString(file.getAbsolutePath());
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

  private boolean handleImage(VImageField target, File file) {
    if (isChartBlockContext()) {
      if (block.getActiveRecord() != -1) {
	target.setImage(block.getActiveRecord(), toByteArray(file));
	block.setActiveRecord(block.getActiveRecord() + 1);
	return true;
      } else {
	return false;
      }
    } else {
      target.setImage(toByteArray(file));
      return true;
    }
  }

  private boolean isChartBlockContext() {
    return block.noDetail() || (block.isMulti() && !block.isDetailMode());
  }

  private static String getExtension(File file) {
    String		extension = null;
    String 		name = file.getName();
    int 		index = name.lastIndexOf('.');

    if (index > 0 &&  index < name.length() - 1) {
      extension = name.substring(index + 1).toLowerCase();
    }

    return extension;
  }

  private static boolean isImage(File file) {
    String		mimeType;

    mimeType = MIMETYPES_FILE_TYPEMAP.getContentType(file);
    if(mimeType.split("/")[0].equals("image")) {
      return true;
    }

    return false;
  }

  private static byte[] toByteArray(File file) {
    try {
      ByteArrayOutputStream	baos = new ByteArrayOutputStream();

      copy(new FileInputStream(file), baos, 1024);
      return baos.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }

  private static void copy(InputStream input,
                           OutputStream output,
                           int bufferSize)
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

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------

  private final VBlock				block;
  private static final MimetypesFileTypeMap	MIMETYPES_FILE_TYPEMAP = new MimetypesFileTypeMap();

  static {
    // missing PNG files in initial map
    MIMETYPES_FILE_TYPEMAP.addMimeTypes("image/png png");
  }
}
