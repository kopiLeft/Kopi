package com.kopiright.vkopi.lib.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JColorChooser;

import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.xkopi.lib.type.Date;

public class JPredefinedValueHandler extends AbstractPredefinedValueHandler {

  //----------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------

  public JPredefinedValueHandler(VFieldUI model, VForm form, VField field) {
    super(model, form, field);
  }

  /**
   * @Override
   */
  public Color selectColor(Color color) {
    Color       f = JColorChooser.showDialog((Component)form.getDisplay(),
                                             Message.getMessage("color-chooser"),
                                             color);

    return f;
  }

  /**
   * @Override
   */
  public Date selectDate(Date date) {
    return DateChooser.getDate((Container)form.getDisplay(), (Component)field.getDisplay(), date);
  }

  /**
   * @Override
   */
  public byte[] selectImage() throws VException {
    File        f = ImageFileChooser.chooseFile((Component)form.getDisplay());

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

}
