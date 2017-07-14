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
import org.kopi.vkopi.lib.form.UBlock;
import org.kopi.vkopi.lib.form.UField;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.form.VImageField;
import org.kopi.vkopi.lib.form.VStringField;
import org.kopi.vkopi.lib.ui.vaadin.addons.Actor;
import org.kopi.vkopi.lib.ui.vaadin.addons.Field;
import org.kopi.vkopi.lib.ui.vaadin.addons.FieldListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.LabelEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.LabelListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.field.FieldState.NavigationDelegationMode;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VColor;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;

/**
 * The <code>DField</code> is the vaadin {@link UField} implementation.
 */
@SuppressWarnings("serial")
public abstract class DField extends Field implements UField, FieldListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
 
  /**
   * Creates a new <code>DField</code> instance.
   * @param model The field model.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options.
   * @param detail Is it a detail view ?
   */
  public DField(final VFieldUI model,
                DLabel label,
                int align,
                int options,
                boolean detail)
  {	
    super(model.getIncrementCommand() != null, model.getDecrementCommand() != null);
    this.inDetail = detail;
    this.model = model;
    this.options = options;
    this.label = label;
    this.align = align;
    this.isEditable = (options & VConstants.FDO_NOEDIT) == 0;
    setImmediate(true);
    addFieldListener(this);
    this.label.setHasAction(!model.hasIcon() && model.hasAction());
    if (getModel().getIcon() != null) {
      setVisibleHeight(3); // actor fields takes 3 lines by default
    } else if (getModel() instanceof VStringField) {
      setVisibleHeight(((VStringField)getModel()).getVisibleHeight());
    } else if (getModel() instanceof VImageField) {
      /*
       * Sets the visible height of the image field to allow row span
       * in simple block layouts.
       * We estimate that a row height is ~ 20 px
       */
      setVisibleHeight(((VImageField)getModel()).getIconHeight() / 20);
    } else {
      setVisibleHeight(getModel().getHeight());
    }
    listener = new LabelListener() {
      
      @Override
      public void onClick(LabelEvent event) {
        int                     displayLine;
        
        // The label click listener will be registered for the displayed size of the block
        // to be sure that the auto fill action is fired on the active record field, we will
        // test of the active record field display is the same field as this one and only in
        // this condition the auto fill action is fired.
        displayLine = model.getBlockView().getDisplayLine(model.getBlock().getActiveRecord());
        if (displayLine != -1 && model.getDisplays()[displayLine] == DField.this) {
          performAutoFillAction();
        }
      }
    };
    setNoChart(getModel().noChart());
    setNoDetail(getModel().noDetail());
    setNavigationDelegationMode(getNavigationDelegationMode());
    setDefaultAccess(getModel().getDefaultAccess());
    setIndex(model.getIndex());
    setHasPreFieldTrigger(getModel().hasTrigger(VConstants.TRG_PREFLD));
    addActors(getActors());
    enableActionTrigger(getModel().getDefaultAccess());
  }	
   
  //----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  /**
   * Enters this field.
   * @param refresh Should we refresh GUI ?
   */
  public void enter(boolean refresh) {
    updateFocus();
  }

  /**
   * Leaves this field.
   */
  public void leave() {
    updateFocus();
  }
 
  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------

  /**
   * Field cell renderer
   */
  @Override
  public void setPosition(final int pos) {
    this.pos = pos;
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        DField.super.setPosition(pos);
      }
    });
  }

  /**
   * Field cell renderer
   * @return the position in chart (0..nbDisplay)
   */
  @Override
  public int getPosition() {
    return pos;
  }
 
  /**
   * Returns the alignment.
   * @return the alignment.
   */
  public int getAlign() {
    return align;
  }
  
  @Override
  public VField getModel() {
    return model.getModel();
  }
  
  @Override
  public void setInDetail(boolean detail) {
    inDetail = detail;
    label.setInDetailMode(detail);
  }

  /**
   * Returns {@code true} is the field belongs to the detail view.
   * @return {@code true} is the field belongs to the detail view.
   */
  public boolean isInDetail() {
    return inDetail;
  }
  
  @Override
  public UComponent getAutofillButton() {
    return null;
  }

  /**
   * Returns the row controller.
   * @return The row controller.
   */
  public VFieldUI getRowController() {
    return model;
  }
 
  //-------------------------------------------------
  // UTILS
  //-------------------------------------------------

  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */
  public void update() {
    // overridden in subclasses
  }
  
  @Override
  public void updateText() {
    // overridden in subclasses
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
  public void forceFocus() {
    // to be implemented by subclasses
  }
  
  @Override
  public void updateAccess() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	access = getAccess();
	setDynAccess(access);
	updateStyles(access);
	setVisible(access != VConstants.ACS_HIDDEN);
	update(label);
	enableActionTrigger(access);
      }
    });
  }
  
  /**
   * Updates a given field label.
   * @param label The label to be updated.
   */
  private void update(final DLabel label) {
    if (label != null) {
      boolean	was = label.isEnabled();
      boolean	will = access >= VConstants.ACS_VISIT;

      if (was != will) {
	label.setEnabled(will);
      }
    }
  }
  
  /**
   * Update field style according to its access.
   * @param access The field access.
   */
  private void updateStyles(int access) {
    removeStyleName("visit");
    removeStyleName("skipped");
    removeStyleName("mustfill");
    removeStyleName("hidden");
    switch (access) {
    case VConstants.ACS_VISIT:
      addStyleName("visit");
      break;
    case VConstants.ACS_SKIPPED:
      addStyleName("skipped");
      break;
    case VConstants.ACS_MUSTFILL:
      addStyleName("mustfill");
      break;
    case VConstants.ACS_HIDDEN:
      addStyleName("hidden");
      break;
    default:
      addStyleName("visit");
      break;
    }
  }
  
  /**
   * Enables the action trigger according if the field has an action trigger and
   * does not contain an icon.
   */
  private void enableActionTrigger(int access) {
    if (access >= VConstants.ACS_VISIT) {
      if (getModel().hasTrigger(VConstants.TRG_ACTION) && getModel().getIcon() == null) {
        label.addLabelListener(listener);
      }
    } else {
      label.removeLabelListener(listener);
    }
  }
  
  /**
   * Returns the navigation delegation to server mode.
   * For POSTFLD AND PREFLD triggers we always delegate the navigation to server.
   * For POSTCHG, PREVAL, VALFLD and FORMAT triggers we delegate the navigation to server if
   * the field value has changed.
   * @return The navigation delegation to server mode.
   */
  private NavigationDelegationMode getNavigationDelegationMode() {
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
  private Collection<Actor> getActors() {
    Set<Actor>          actors;
    
    actors = new HashSet<Actor>();
    for (VCommand cmd : model.getAllCommands()) {
      if (cmd != null) {
        // for field commands this is needed to have the actor model instance
        cmd.setEnabled(false);
        if (cmd.getActor() != null) {
          actors.add((Actor)cmd.getActor().getDisplay());
        }
      }
    }
    
    return actors;
  }
 
  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
 
  /**
   * Returns the object associed to record r
   *
   * @param	r the position of the record
   * @return	the displayed value at this position
   */
  public abstract Object getObject(); 

  /**
   * set blink state
   * @param b The blink ability.
   */
  public abstract void setBlink(boolean b);
 
  //-------------------------------------------------
  // PROTECTED UTILS
  //-------------------------------------------------
 
  /**
   * Returns {@code true} if the field model is focused.
   * @return {@code true} if the field model is focused.
   */
  protected final boolean modelHasFocus() {
    if (getModel() == null) {
      return false;
    }

    final VBlock block = getModel().getBlock();
    return getModel().hasFocus() && block.getActiveRecord() == getBlockView().getRecordFromDisplayLine(pos);
  }

  /**
   * Returns {@code true} if the field is in skipped mode.
   * @return {@code true} if the field is in skipped mode.
   */
  protected final boolean isSkipped() {
    final VBlock block = getModel().getBlock();
    return getAccess() == VConstants.ACS_SKIPPED
      || !block.isRecordAccessible(getBlockView().getRecordFromDisplayLine(getPosition()));
  }
  
  @Override
  public final int getAccess() {
    return getAccessAt(getPosition());
  }

  /**
   * Returns the field access at a given line.
   * @param at The desired line.
   * @return The field access.
   */
  protected final int getAccessAt(int at) {
    if (getModel() != null) {
      return getModel().getAccess(getBlockView().getRecordFromDisplayLine(at));
    } else {
      return VConstants.ACS_SKIPPED;
    }
  }
  
  /**
   * Returns the field foreground color.
   * @param at The desired line.
   * @return The foreground color.
   */
  protected final VColor getForegroundAt(int at) {
    if (model != null) {
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
    if (model != null) {
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

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * prepare a snapshot
   * @param fieldPos position of this field within block visible fields
   */
  @Override
  public void prepareSnapshot(int fieldPos, boolean activ) {
    label.prepareSnapshot(activ);
  }
 
  //-------------------------------------------------
  // FIELD IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public UBlock getBlockView() {
    return model.getBlockView();
  }

  //-------------------------------------------------
  // FIELD LISTENER IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void onIncrement() {
    DField.this.model.getIncrementCommand().performAction();
  }
  
  @Override
  public void onDecrement() {
    DField.this.model.getDecrementCommand().performAction();
  }
  
  @Override
  public void onClick() {
    if (!modelHasFocus()) {
      // an empty row in a chart has not calculated
      // the access for each field (ACCESS Trigger)
      if (model.getBlock().isMulti()) {
	final int	recno = getBlockView().getRecordFromDisplayLine(getPosition());

        if (! model.getBlock().isRecordFilled(recno)) {
	  model.getBlock().updateAccess(recno);
        }
      }

      if (!model.getBlock().isMulti() 
	  || model.getBlock().isDetailMode() == isInDetail()
	  || model.getBlock().noChart())
      {
        KopiAction      action = new KopiAction("mouse1") {
	  
          @Override
          public void execute() throws VException {
	    model.transferFocus(DField.this); // use here a mouse transferfocus
	  }
        };
        // execute it as model transforming thread
        // it is not allowed to execute it not with
        // the method performAsync/BasicAction.
        model.performAsyncAction(action);
      }
    }
  }
  
  /**
   * !!! We never change transfer a focus to a field that belongs
   * to another block than this field model block. If we do it, it
   * can cause assertion errors when validating blocks caused by
   * actors actions.
   */
  @Override
  public void transferFocus() {
    if (!modelHasFocus()) {
      // an empty row in a chart has not calculated
      // the access for each field (ACCESS Trigger)
      if (model.getBlock().isMulti()) {
        final int       recno = getBlockView().getRecordFromDisplayLine(getPosition());

        if (! model.getBlock().isRecordFilled(recno)) {
          model.getBlock().updateAccess(recno);
        }
      }

      if (!model.getBlock().isMulti() 
          || model.getBlock().isDetailMode() == isInDetail()
          || model.getBlock().noChart())
      {
        KopiAction      action = new KopiAction("mouse1") {

          @Override
          public void execute() throws VException {
            // proceed only of we are in the same block context.
            if (getModel().getBlock() == getModel().getForm().getActiveBlock()) {
              int       recno = getBlockView().getRecordFromDisplayLine(getPosition());
              
              // go to the correct record if necessary
              // but only if we are in the correct block now
              if (getModel().getBlock().isMulti()
                  && recno != getModel().getBlock().getActiveRecord()
                  && getModel().getBlock().isRecordAccessible(recno))
              {
                getModel().getBlock().gotoRecord(recno);
              }

              // go to the correct field if already necessary
              // but only if we are in the correct record now
              if (recno == getModel().getBlock().getActiveRecord()
                  && getModel() != getModel().getBlock().getActiveField()
                  && getAccess() >= VConstants.ACS_VISIT)
              {
                getModel().getBlock().gotoField(getModel());
              }
            }
          }
        };
        // execute it as model transforming thread
        // it is not allowed to execute it not with
        // the method performAsync/BasicAction.
        model.performAsyncAction(action);
      }
    }
  }
  
  @Override
  public void gotoNextField() {
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
  public void gotoPrevField() {
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
  public void gotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_ALTENTER") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoNextEmptyMustfill();
        }
      }
    });
  }


  @Override
  public void gotoPrevRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_UP") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().gotoPrevRecord();
        }
      }
    });
  }

  @Override
  public void gotoNextRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_DOWN") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().gotoNextRecord();
        }
      }
    });
  }

  @Override
  public void gotoFirstRecord() {
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
  public void gotoLastRecord() {
    getModel().getForm().performAsyncAction(new KopiAction("keyKEY_REC_LAST") {

      @Override
      public void execute() throws VException {
        if (getModel() != null) {
          getModel().getBlock().getForm().getActiveBlock().gotoLastRecord();
        }
      }
    });
  }
  
  /**
   * Performs the auto fill action.
   */
  public final void performAutoFillAction() {
    getModel().getForm().performAsyncAction(new KopiAction("autofill") {

      @Override
      public void execute() throws VException {
        model.transferFocus(DField.this);
        model.autofillButton();
      }
    });     
  }
  
  /**
   * Performs the field action trigger
   */
  public final void performFieldAction() {
    if (model.hasAction()) {
      getModel().getForm().performAsyncAction(new KopiAction("TRG_ACTION") {

        @Override
        public void execute() throws VException {
          model.transferFocus(DField.this);
          getModel().callTrigger(VConstants.TRG_ACTION);
        }
      }); 
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	VFieldUI		model;
  protected	DLabel			label;
  protected	int			state;		// Display state
  protected	int			pos;
  protected	int			options;
  protected	int			align;
  protected     int			access;		// current access of field
  protected	boolean			isEditable;	// is this field editable
  protected	boolean			mouseInside;	// private events
  private       boolean             	inDetail;
  private       final LabelListener     listener;
}
