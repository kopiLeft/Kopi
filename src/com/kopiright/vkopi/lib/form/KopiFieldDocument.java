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

import java.awt.Toolkit;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.ui.base.Stateful;

/**
 * !!! NEED COMMENTS
 */
/*package*/ class KopiFieldDocument extends PlainDocument implements  Stateful {

  
public KopiFieldDocument(VField model, ModelTransformer transformer) {
    this.model = model;
    this.transformer = transformer;
  }

  // ----------------------------------------------------------------------
  // MODEL / VIEW INTERFACE
  // ----------------------------------------------------------------------

  /**
   * Returns the text currently showed by this document
   */
  public synchronized String getModelText() {
    try {
      String    text;

      text = getText(0, getLength());
      return transformer.toModel(text);
    } catch (BadLocationException e) {
      throw new InconsistencyException("BadLocationException in KopiFieldDocument");
    }
  }

  /**
   * Changes the text of this document without checking
   */
  public synchronized void setModelText(String s) {
    try {
      super.remove(0, getLength());
      s =  transformer.toGui(s);
      super.insertString(0, s, null);
    } catch (BadLocationException e) {
      throw new InconsistencyException("BadLocationException in KopiFieldDocument");
    }
  }

  // ----------------------------------------------------------------------
  // DOCUMENT IMPLEMENTATION
  // ----------------------------------------------------------------------

  public void remove(int offs, int len) throws BadLocationException {
    String      text;
    
    text = getText(0, getLength());
    text = text.substring(0, offs) + text.substring(offs + len);
    text = transformer.toModel(text);

    if (!transformer.checkFormat(text)) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    if (model.checkText(text)) {
      super.remove(offs, len);
      model.onTextChange(getText(0, getLength()));
    } else {
      java.awt.Toolkit.getDefaultToolkit().beep();
    }
  }

  public void insertString(int offs, String str, AttributeSet a)
    throws BadLocationException {

    if (str == null) {
      return;
    }

    String text = getText(0, getLength());
    text = text.substring(0, offs) + str + text.substring(offs);
    text = transformer.toModel(text);

    if (!transformer.checkFormat(text)) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }

    if (model.checkText(text)) {
      super.insertString(offs, str, a);
      model.onTextChange(getText(0, getLength()));
    } else {
      java.awt.Toolkit.getDefaultToolkit().beep();
    }
  }

  public Object getModel() {
    return model;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public void setAlert(boolean alert) {
    this.alert = alert;
  }
  public boolean isAlert() {
    return alert;
  }

  public boolean getAutofill() {
    return autofill;
  }

  public void setAutofill(boolean autofill) {
    this.autofill = autofill;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private int                   state;
  private boolean               alert;
  private boolean               autofill = false;

  private VField		model;
  private ModelTransformer      transformer;
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
  private static final long serialVersionUID = -7098798410173878552L;
}
