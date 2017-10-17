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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.form.UField;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.ui.vaadin.addons.Actor;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.AutofillEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.AutofillListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.ClickEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.ClickListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.NavigationEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField.NavigationListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorFieldState.NavigationDelegationMode;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.grid.EditorTextFieldState.ConvertType;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.ui.vaadin.base.Utils;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VColor;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.Renderer;

/**
 * An UField associated with the grid block in inline edit.
 */
@SuppressWarnings("serial")
public abstract class DGridEditorField<T> implements UField, NavigationListener, ClickListener, AutofillListener {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
 
  /**
   * Creates a new instance of the grid editor field.
   */
  public DGridEditorField(final VFieldUI columnView,
                          DGridEditorLabel label,
                          int align,
                          int options)
  {     
    this.columnView = columnView;
    this.label = label;
    this.options = options;
    this.align = align;
    this.isEditable = (options & VConstants.FDO_NOEDIT) == 0;
    this.editor = createEditor();
    getEditor().setConverter(createConverter());
    getEditor().addNavigationListener(this);
    getEditor().addClickListener(this);
    getEditor().addAutofillListener(this);
    setLabelAlignment();
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  @Override
  public VField getModel() {
    return columnView.getModel();
  }

  @Override
  public DGridBlock getBlockView() {
    return (DGridBlock) columnView.getBlockView();
  }

  @Override
  public int getAccess() {
    if (getModel() != null) {
      return getModel().getAccess(getBlockView().getRecordFromDisplayLine(getPosition()));
    } else {
      return VConstants.ACS_SKIPPED;
    }
  }
  
  @Override
  public void setBlink(final boolean blink) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getEditor().setBlink(blink); 
      }
    });
  }

  @Override
  public int getPosition() {
    return 0;
  }

  @Override
  public void setPosition(int position) {}

  @Override
  public UComponent getAutofillButton() {
    return null;
  }

  @Override
  public void setInDetail(boolean detail) {}

  @Override
  public void forceFocus() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getEditor().focus();
      }
    });
  }

  @Override
  public void updateColor() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        getEditor().setColor(Utils.toString(getForeground()), Utils.toString(getBackground()));
      }
    });
  }

  @Override
  public void prepareSnapshot(int fieldPos, boolean activ) {}
  
  /**
   * Sets the label alignment according to the editor alignment.
   */
  protected void setLabelAlignment() {
    if (label != null) {
      switch (getModel().getAlign()) {
      case VConstants.ALG_LEFT:
        label.addStyleName("v-align-left");
        break;
      case VConstants.ALG_RIGHT:
        label.addStyleName("v-align-right");
        break;
      case VConstants.ALG_CENTER:
        label.addStyleName("v-align-center");
        break;
      default:
        label.addStyleName("v-align-left");
        break;
      }
    }
  }
  
  // ----------------------------------------------------------------------
  // UCOMPONENT
  // ----------------------------------------------------------------------
  
  @Override
  public boolean isEnabled() {
    return getEditor().isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {
    getEditor().setEnabled(enabled);
  }

  @Override
  public boolean isVisible() {
    return getEditor().isVisible();
  }

  @Override
  public void setVisible(boolean visible) {
    getEditor().setVisible(visible);
  }
  
  @Override
  public void updateFocus() {
    if (modelHasFocus()) {
      final VForm       form = getModel().getForm();

      form.setInformationText(getModel().getToolTip());
      form.setFieldSearchOperator(getModel().getSearchOperator());
    }
  }
  
  @Override
  public void updateAccess() {
    if (label != null) {
      label.update(columnView, getBlockView().getRecordFromDisplayLine(getPosition()));
    }
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        access = getAccess();
        setEnabled(access > VConstants.ACS_SKIPPED);
        setVisible(access != VConstants.ACS_HIDDEN);
        updateLabel();
      }
    });
  }
  
  /**
   * Updates a given field label.
   * @param label The label to be updated.
   */
  protected void updateLabel() {
    if (label != null) {
      boolean   was = label.isEnabled();
      boolean   will = access >= VConstants.ACS_VISIT;

      if (was != will) {
        label.setEnabled(will);
      }
    }
  }
  
  /**
   * Returns the null representation of this editor.
   * @return The null representation of this editor.
   */
  protected T getNullRepresentation() {
    return null;
  }
  
  /**
   * Performs a reset operation on this editor.
   */
  protected void reset() {
    getEditor().setValue(getNullRepresentation());
  }
  
  // ----------------------------------------------------------------------
  // CLICK
  // ----------------------------------------------------------------------
  
  @Override
  public void onClick(ClickEvent event) {
    if (!modelHasFocus()) {
      final int         recno = getBlockView().getRecordFromDisplayLine(getPosition());

      if (! columnView.getBlock().isRecordFilled(recno)) {
        getModel().getBlock().updateAccess(recno);
      }

      columnView.performAsyncAction(new KopiAction("mouse1") {

        @Override
        public void execute() throws VException {
          columnView.transferFocus(DGridEditorField.this); // use here a mouse transferfocus
        }
      });
    }
  }
  
  @Override
  public void onAutofill(AutofillEvent event) {
    performAutoFillAction();
  }
  
  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------
  
  @Override
  public void onGotoNextField(NavigationEvent event) {
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
  public void onGotoPrevField(NavigationEvent event) {
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
  public void onGotoNextBlock(NavigationEvent event) {
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
  public void onGotoPrevRecord(NavigationEvent event) {
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
  public void onGotoNextRecord(NavigationEvent event) {
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
  public void onGotoFirstRecord(NavigationEvent event) {
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
  public void onGotoLastRecord(NavigationEvent event) {
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
  public void onGotoNextEmptyMustfill(NavigationEvent event) {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_ALTENTER") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
        }
      }
    });
  }
  
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  
  /**
   * Returns the field editor hold by this component.
   * @return The field editor hold by this component.
   */
  public GridEditorField<T> getEditor() {
    return editor;
  }
  
  // ----------------------------------------------------------------------
  // UTIL
  // ----------------------------------------------------------------------
  
  /**
   * Sets this editor as the focused one in the browser window 
   */
  protected void enter() {
    updateFocus();
  }
  
  /**
   * Leaves this editor in the browser window.
   */
  protected void leave() {
    updateFocus();
  }
  
  /**
   * Creates the editor field associated with this component.
   * @return The created editor field.
   */
  protected abstract GridEditorField<T> createEditor();
  
  /**
   * Creates the conversion engine for grid data rendering. 
   * @return The data converter.
   */
  protected abstract Converter<T, Object> createConverter();
  
  /**
   * Creates the editor field renderer
   * @return The editor renderer.
   */
  protected abstract Renderer<T> createRenderer();
  
  /**
   * Performs the auto fill action.
   */
  public final void performAutoFillAction() {
    getModel().getForm().performAsyncAction(new KopiAction("autofill") {

      @Override
      public void execute() throws VException {
        columnView.transferFocus(DGridEditorField.this);
        columnView.autofillButton();
      }
    });     
  }
  
  /**
   * Performs the field action trigger
   */
  public final void performFieldAction() {
    if (columnView.hasAction()) {
      getModel().getForm().performAsyncAction(new KopiAction("TRG_ACTION") {

        @Override
        public void execute() throws VException {
          columnView.transferFocus(DGridEditorField.this);
          getModel().callTrigger(VConstants.TRG_ACTION);
        }
      }); 
    }
  }
  
  /**
   * Returns the field foreground color.
   * @param at The desired line.
   * @return The foreground color.
   */
  protected final VColor getForegroundAt(int at) {
    if (getModel() != null) {
      return getModel().getForeground(getBlockView().getRecordFromDisplayLine(at));
    } else {
      return null;
    }
  }
  
  /**
   * Returns the field background color.
   * @param at The desired line.
   * @return The background color.
   */
  protected final VColor getBackgroundAt(int at) {
    if (getModel() != null) {
      return getModel().getBackground(getBlockView().getRecordFromDisplayLine(at));
    } else {
      return null;
    }
  }
  
  /**
   * Returns the foreground color of the current data position.
   * @return The foreground color of the current data position.
   */
  public final VColor getForeground() {
    return getForegroundAt(getPosition());
  }
  
  /**
   * Returns the background color of the current data position.
   * @return The background color of the current data position.
   */
  public final VColor getBackground() {
    return getBackgroundAt(getPosition());
  }
  
  /**
   * Returns {@code true} if the field model is focused.
   * @return {@code true} if the field model is focused.
   */
  protected final boolean modelHasFocus() {
    if (getModel() == null) {
      return false;
    }

    final VBlock block = getModel().getBlock();
    return getModel().hasFocus() && block.getActiveRecord() == getBlockView().getRecordFromDisplayLine(getPosition());
  }
  
  /**
   * Returns the navigation delegation to server mode.
   * For POSTFLD AND PREFLD triggers we always delegate the navigation to server.
   * For POSTCHG, PREVAL, VALFLD and FORMAT triggers we delegate the navigation to server if
   * the field value has changed.
   * @return The navigation delegation to server mode.
   */
  protected NavigationDelegationMode getNavigationDelegationMode() {
    if (getModel().hasTrigger(VConstants.TRG_POSTFLD)) {
      return NavigationDelegationMode.ALWAYS;
    } else if (getModel().hasTrigger(VConstants.TRG_PREFLD)) {
      return NavigationDelegationMode.ALWAYS;
    } else if (getModel().getBlock().hasTrigger(VConstants.TRG_PREREC)) {
      return NavigationDelegationMode.ALWAYS;
    } else if (getModel().getBlock().hasTrigger(VConstants.TRG_POSTREC)) {
      return NavigationDelegationMode.ALWAYS;
    } else if (getModel().getBlock().hasTrigger(VConstants.TRG_VALREC)) {
      return NavigationDelegationMode.ALWAYS;
    } else if (getModel().getList() != null) {
      return NavigationDelegationMode.ONVALUE;
    } else if (getModel().hasTrigger(VConstants.TRG_POSTCHG)) {
      return NavigationDelegationMode.ONCHANGE;
    } else if (getModel().hasTrigger(VConstants.TRG_PREVAL)) {
      return NavigationDelegationMode.ONCHANGE;
    } else if (getModel().hasTrigger(VConstants.TRG_VALFLD)) {
      return NavigationDelegationMode.ONCHANGE;
    } else if (getModel().hasTrigger(VConstants.TRG_FORMAT)) {
      return NavigationDelegationMode.ONCHANGE;
    } else {
      return NavigationDelegationMode.NONE;
    }
  }
  
  /**
   * Returns the actors associated with this field. 
   * @return The actors associated with this field. 
   */
  protected Collection<Actor> getActors() {
    Set<Actor>          actors;
    
    actors = new HashSet<Actor>();
    for (VCommand cmd : columnView.getAllCommands()) {
      if (cmd != null) {
        // for field commands this is needed to have the actor model instance
        cmd.setEnabled(false);
        if ( cmd.getActor() != null) {
          actors.add((Actor)cmd.getActor().getDisplay());
        }
      }
    }
    
    return actors;
  }
  /**
   * Returns the convert type for the string field.
   * @return The convert type for the string field.
   */
  protected ConvertType getConvertType(VField model) {
    switch (getModel().getTypeOptions() & VConstants.FDO_CONVERT_MASK) {
    case VConstants.FDO_CONVERT_NONE:
      return ConvertType.NONE;
    case VConstants.FDO_CONVERT_UPPER:
      return ConvertType.UPPER;
    case VConstants.FDO_CONVERT_LOWER:
      return ConvertType.LOWER;
    case VConstants.FDO_CONVERT_NAME:
      return ConvertType.NAME;
    default:
      return ConvertType.NONE;
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected final DGridEditorLabel              label;
  protected final GridEditorField<T>            editor;
  protected VFieldUI                            columnView;
  protected int                                 options;
  protected int                                 align;
  protected int                                 access;         // current access of field
  protected boolean                             isEditable;     // is this field editable
}
