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

import org.kopi.vkopi.lib.form.UTextField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VStringField;
import org.kopi.vkopi.lib.ui.vaadin.addons.RichTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.RichTextField.NavigationListener;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;

/**
 * Rich text editor implementation based on CK editor for vaadin.
 */
@SuppressWarnings("serial")
public class DRichTextEditor extends DField implements UTextField, FocusListener, ValueChangeListener, NavigationListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  public DRichTextEditor(VFieldUI model, 
                         DLabel label,
                         int align,
                         int options,
                         int height,
                         boolean detail)
  {
    super(model, label, align, options, detail);
    editor = new RichTextField(getModel().getWidth(),
                               getModel().getHeight(),
                               (getModel().getHeight() == 1) ? 1 : ((VStringField)getModel()).getVisibleHeight(),
                               model.getModel().isNoEdit(),
                               ApplicationContext.getDefaultLocale());
    editor.addFocusListener(this);
    editor.addValueChangeListener(this);
    editor.addNavigationListener(this);
    setContent(editor);
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void updateColor() {}

  @Override
  public Object getObject() {
    return editor.getValue();
  }

  @Override
  public void setBlink(final boolean blink) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (blink) {
          editor.addStyleName("blink");
        } else {
          editor.removeStyleName("blink");
        }
      }
    });
  }

  @Override
  public void focus(FocusEvent event) {
    // model transfer focus is performed
    // when the field is focused and not when
    // the field is clicked like text field
    // because CK editor does not provide a way
    // to capture click event on the editable area
    onClick();
  }

  @Override
  public void valueChange(ValueChangeEvent event) {
    // value change event is fired when the field is blurred.
    getModel().setChangedUI(true);
    getModel().setChanged(true);
  }
  
  @Override
  public void updateText() {
    final String        newModelTxt = getModel().getText(getRowController().getBlockView().getRecordFromDisplayLine(getPosition()));

    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        editor.setValue(newModelTxt);
      }
    });
  }
  
  @Override
  public void updateFocus() {
    label.update(model, getPosition());
    if (!modelHasFocus()) {
      if (inside) {
        inside = false;
      }
    } else {
      if (!inside) {
        inside = true;
        enterMe(); 
      }
    }
    
    super.updateFocus();
  }
  
  @Override
  public void updateAccess() {
    super.updateAccess();
    label.update(model, getBlockView().getRecordFromDisplayLine(getPosition()));
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        editor.setEnabled(access >= VConstants.ACS_VISIT);
        setEnabled(access >= VConstants.ACS_VISIT);
      }
    });
  }
  
  @Override
  public void forceFocus() {
    enterMe();
  }

  @Override
  public String getText() {
    return editor.getValue();
  }

  @Override
  public void setHasCriticalValue(boolean b) {}

  @Override
  public void addSelectionFocusListener() {}

  @Override
  public void removeSelectionFocusListener() {}

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {}
  
  /**
   * Gets the focus to this editor.
   */
  private void enterMe() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        editor.focus();
      }
    });
  }
  
  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------
  
  @Override
  public void onGotoNextField() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_TAB") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextField();
        }
      }
    });
  }
  
  @Override
  public void onGotoPrevField() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_STAB") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoPrevField();
        }
      }
    });
  }
  
  @Override
  public void onGotoNextBlock() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_BLOCK") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().gotoNextBlock();
        }
      }
    });
  }
  
  @Override
  public void onGotoPrevRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_UP") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoPrevRecord();
        }
      }
    });
  }
  
  @Override
  public void onGotoNextRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_DOWN") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextRecord();
        }
      }
    });
  }
  
  @Override
  public void onGotoFirstRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_FIRST") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoFirstRecord();
        }
      }
    });
  }
  
  @Override
  public void onGotoLastRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_LAST") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoLastRecord();
        }
      }
    });
  }
  
  @Override
  public void onGotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_ALTENTER") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
        }
      }
    });
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private final RichTextField                   editor;
  private boolean                               inside;
}
