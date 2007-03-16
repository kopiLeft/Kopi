/*
 * Copyright (c) 1990-2007 kopiRight Managed Solutions GmbH
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

import java.io.File;
import java.io.FileInputStream;
import java.awt.Color;
import javax.swing.JColorChooser;

import com.kopiright.vkopi.lib.list.VListColumn;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.xkopi.lib.type.Date;

public class GUIPredefinedValueHandler implements PredefinedValueHandler {
  
  public GUIPredefinedValueHandler(VFieldUI model, DForm form, DField field) {
    this.model = model;
    this.form = form;
    this.field = field;
  }

  public boolean selectDefaultValue() throws VException {
    return model.fillField();
  }

  public String selectFromList(VListColumn[] list, 
                               Object[][] values, 
                               String[] predefinedValues) 
  {
    final int		selected;
    final ListDialog	listDialog;
    
    listDialog = new ListDialog(list, values);
    selected = listDialog.selectFromDialog(form, field);
    
    if (selected != -1) {
      return predefinedValues[selected];
    } else {
      return null;
    }
  }

  public Color selectColor(Color color) {
    Color       f = JColorChooser.showDialog(form.getFrame(), 
                                             Message.getMessage("color-chooser"), 
                                             color);

    return f;
  }

  public Date selectDate(Date date) {
    return DateChooser.getDate(form, field, date);
  }

  public byte[] selectImage() throws VException {
    File        f = ImageFileChooser.chooseFile(form.getFrame());

    if (f == null) {
      return null;
    }

    try {
      FileInputStream	is = new FileInputStream(f);
      byte[]            b = new byte[is.available()];
      is.read(b);

      return b;
    } catch (Exception e) {
      throw new VExecFailedException("bad-file", e);
    }
  }

  private VFieldUI      model; 
  private DForm         form;
  private DField        field;
}
