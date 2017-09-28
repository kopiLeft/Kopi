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

package org.kopi.vkopi.lib.form;

import java.io.Serializable;
import java.util.Vector;

import org.kopi.util.base.InconsistencyException;
import org.kopi.util.base.Utils;
import org.kopi.vkopi.lib.visual.ActionHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;

/**
 * This class implements all UI actions on fields
 */
@SuppressWarnings("serial")
public abstract class VFieldUI implements VConstants, ActionHandler, Serializable {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Creates a new field UI model without index indication.
   * @param blockView The block view.
   * @param model The field model.
   */
  protected VFieldUI(UBlock blockView, VField model) {
    this(blockView, model, 0);
  }
  
  /**
   * Creates a new field UI model.
   * @param blockView The block view.
   * @param model The field model.
   * @param index The row controller index.
   */
  protected VFieldUI(UBlock blockView, VField model, int index) {
    this.blockView = blockView;
    this.model = model;
    this.index = index;
    activeCommands = new Vector<VCommand>();
    fieldHandler = createFieldHandler();
    model.addFieldListener(fieldHandler);
    model.addFieldChangeListener(fieldHandler);

    VPosition   pos = model.getPosition();

    if (pos != null) {
      this.line = pos.line;
      this.lineEnd = pos.lineEnd;
      this.column = pos.column;
      this.columnEnd = pos.columnEnd;
      this.chartPos = pos.chartPos;
    }

    VCommand[]  cmd = model.getCommand();

    for (int i = 0; cmd != null && i < cmd.length; i++) {
      final String commandText = cmd[i].getIdent();

      if (commandText.equals("Increment")) {
	incrementCommand = cmd[i];
      } else if (commandText.equals("Decrement")) {
	decrementCommand = cmd[i];
      } else if (commandText.equals("Autofill") && !model.hasAutofill()) {
	autofillCommand = cmd[i];
      }
    }

    hasAutofill = model.hasAutofill() && !hasAutofillCommand();
    commands = cmd;
    if (model.getList() != null) {
      if (model.getList().getNewForm() != null || model.getList().getAction() != -1) {
	hasEditItem = hasNewItem = true;
      }
    }
    buildDisplay();
  }

  private boolean hasEditItem_S() {
    return model.getList() != null && model.getList().hasShortcut();
  }

  // ----------------------------------------------------------------------
  // ABSTRACT METHOD
  // ----------------------------------------------------------------------

  /**
   * Creates a display widget for this row controller.
   * @param label The field label.
   * @param model The field model.
   * @param detail Is this field is in detail mode ?
   * @return The {@link UField} display component of this field.
   */
  protected abstract UField createDisplay(ULabel label, VField model, boolean detail);

  /**
   * Creates a {@link FieldHandler} for this row controller.
   * @return The created {@link FieldHandler}.
   */
  protected abstract FieldHandler createFieldHandler();

  /**
   * Creates a {@link ULabel} for this row controller.
   * @param text The label text.
   * @param help The label help
   * @param detail Creates the label for the detail mode ?
   * @return The created {@link ULabel}.
   */
  protected abstract ULabel createLabel(String text, String help, boolean detail);

  /**
   * Creates a {@link UChartLabel} for this row controller.
   * @param text The label text.
   * @param help The label help.
   * @param index The chart label index.
   * @param model The chart label sort model.
   * @return The created {@link UChartLabel}
   */
  protected abstract UChartLabel createChartHeaderLabel(String text, String help, int index, VBlock.OrderModel model);

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Display error
   */
  @SuppressWarnings("deprecation")
  public void displayFieldError(String message) {
    UField      display = getModeDisplay();
    
    if (display == null) {
      model.getForm().error(message);
    } else {
      try {
        // navigates to the active record if needed
        // this is typically needed in grid based blocks
        gotoActiveRecord();
        // switch to detail view when needed
        if (getBlock().isMulti() && display == detailDisplay && !getBlock().isDetailMode()) {
          ((UMultiBlock)blockView).switchView(-1);
        }
        display.setBlink(true);
        model.getForm().error(message);
        display.setBlink(false);

        transferFocus(display);
      } catch (VException e) {
        throw new InconsistencyException();
      } finally {
        // ensure that the field gain focus again
        display.forceFocus();
      }
    }
  }
  
  /**
   * Returns the field display according to the model block.
   * If the block is in detail mode and the field is visible
   * in the detail mode, the detail display will be returned.
   * Otherwise, the chart display will be returned in case of
   * a valid display line
   */
  protected UField getModeDisplay() {
    UField              display;
    int                 displayLine;
    
    displayLine = blockView.getDisplayLine(getBlock().getActiveRecord());
    if (getModel().noChart()) {
      display = detailDisplay;
    } else if (getModel().noDetail() && displayLine != -1) {
      display = displays[displayLine];
    } else if (!getModel().noChart() && !getModel().noDetail()) {
      // field is visible on both views
      if (getBlock().isMulti() && getBlock().isDetailMode()) {
        display = detailDisplay;
      } else if (displayLine != -1) {
        display = displays[displayLine];
      } else {
        display = null;
      }
    } else {
      display = null;
    }
    
    return display;
  }
  
  /**
   * 
   */
  protected void gotoActiveRecord() throws VException {
    // to be redefined by subclasses
  }

  @SuppressWarnings("deprecation")
  public void resetCommands() {
    for (int i = 0; i < activeCommands.size(); i++) {
      activeCommands.elementAt(i).setEnabled(false);
    }
    activeCommands.setSize(0);
    if (model.hasFocus()) {
      if (hasEditItem_S()) { // TRY TO REMOVE !!!!
        VCommand      command = model.getForm().cmdEditItem_S;

        activeCommands.addElement(command);
        command.setEnabled(true);
      } else if (hasAutofill) {
        // for boolean fields, the auto fill command is not included for boolean field
        // when row controller does not allow it.
        if (!(getModel() instanceof VBooleanField) || includeBooleanAutofillCommand()) {
          VCommand      command = model.getForm().cmdAutofill;

          activeCommands.addElement(command);
          command.setEnabled(true);
        }
      }
      if (hasNewItem) {
        VCommand      command = model.getForm().cmdNewItem;

        activeCommands.addElement(command);
        command.setEnabled(true);
      }
      if (hasEditItem) {
        VCommand      command = model.getForm().cmdEditItem;

        activeCommands.addElement(command);
        command.setEnabled(true);
      }
      VCommand[]      localCommands = model.getCommand();

      if (localCommands != null) {
        for (int i = 0; i < localCommands.length; i++) {
          if (localCommands[i].isActive(getBlock().getMode())) {
            boolean		active;
            
            if (getBlock().hasTrigger(TRG_CMDACCESS, getBlock().fields.length + getBlock().commands.length + i + 1)) {
              try {
        	active = ((Boolean)getBlock().callTrigger(TRG_CMDACCESS, getBlock().fields.length + getBlock().commands.length + i + 1)).booleanValue();
              } catch (VException e) {
        	// consider that the command is active of any error occurs
        	active = true;
              }
            } else {
              // if no access trigger is associated with the command
              // we consider it as active command
              active = true;
            }
            if (active) {
              activeCommands.addElement(localCommands[i]);
              localCommands[i].setEnabled(true);
            }
          }
        }
      }
    }
    // 20021022 laurent : do the same for increment and decrement buttons ?
    if (model.getAccess(model.getBlock().getActiveRecord()) > VConstants.ACS_SKIPPED &&
        hasAutofillCommand() &&
        !model.getBlock().isChart() &&
        getDisplay() != null  && getDisplay().getAutofillButton() != null)
    {
      getDisplay().getAutofillButton().setEnabled(autofillCommand.isActive(model.getBlock().getMode()));
    }
  }

  /**
   *
   */
  public VCommand[] getAllCommands() {
    Vector<VCommand>	cmds = new Vector<VCommand>();

    for (int i = 0; commands != null && i < commands.length; i++) {
      cmds.addElement(commands[i]);
    }
    if (hasEditItem_S()) {
      cmds.addElement(model.getForm().cmdEditItem_S);
    } else if (hasAutofill) {
      if (!(getModel() instanceof VBooleanField) || includeBooleanAutofillCommand()) {
        cmds.addElement(model.getForm().cmdAutofill);
      }
    }
    if (hasNewItem) {
      cmds.addElement(model.getForm().cmdNewItem);
    }
    if (hasEditItem) {
      cmds.addElement(model.getForm().cmdEditItem);
    }

    return (VCommand[])Utils.toArray(cmds, VCommand.class);
  }

  /**
   * Clears all display fields.
   */
  /*package*/ void close() {
    for (int i = 0; i < activeCommands.size(); i++) {
      activeCommands.elementAt(i).setEnabled(false);
    }
    activeCommands.setSize(0);
  }

  /**
   * resetLabel
   */
  public void resetLabel() {
    if (dl != null) {
      dl.init(model.getLabel(), model.getToolTip());
    }
    if (dlDetail != null) {
      dl.init(model.getLabel(), model.getToolTip());
    }
  }

  /**
   *
   */
  public boolean hasAutofill() {
    return hasAutofill || model.getList() != null || hasAutofillCommand();
  }
  
  /**
   * Returns true if the field has an action trigger.
   * @return True if the field has an action trigger.
   */
  public boolean hasAction() {
    return model.hasTrigger(VConstants.TRG_ACTION);
  }
  
  /**
   * Returns true if the field has an icon.
   * @return True if the field has an icon.
   */
  public boolean hasIcon() {
    return model.getIcon() != null;
  }

  /**
   *
   */
  public boolean hasAutofillCommand() {
    return autofillCommand != null;
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public boolean fillField() throws VException {
    model.checkType(fieldHandler.getDisplayedValue(true));

    if (hasAutofill()) {
      if (hasEditItem_S()) {
	fieldHandler.loadItem(VForm.CMD_EDITITEM);
      } else {
	model.selectFromList(false);
      }
    }

    return true;
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public void autofillButton() throws VException {
    if (hasAutofillCommand()) {
      autofillCommand.performBasicAction();
    } else {
      fieldHandler.predefinedFill();
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public void nextEntry() throws VException {
    model.checkType(fieldHandler.getDisplayedValue(true));

    if (model.hasNextPreviousEntry()) {
      model.enumerateValue(true);
    } else if (decrementCommand != null) {
      decrementCommand.performBasicAction();
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public void previousEntry() throws VException {
    model.checkType(fieldHandler.getDisplayedValue(true));

    if (model.hasNextPreviousEntry()) {
      model.enumerateValue(false);
    } else if (incrementCommand != null) {
      incrementCommand.performBasicAction();
    }
  }

  /**
   * @return the associated incrementCommand
   */
  public VCommand getIncrementCommand() {
    return incrementCommand;
  }

  /**
   * @return the associated decrementCommand
   */
  public VCommand getDecrementCommand() {
    return decrementCommand;
  }
  
  /**
   * Returns the index of this row controller.
   * @return The index of this row controller.
   */
  public int getIndex() {
    return index;
  }
  
  /**
   * Returns true if the UI controller should include the auto fill
   * command for boolean fields. This is used to handle different UIs
   * for boolean field between swing and WEB implementations.
   * @return True if the auto fill command should be present for boolean fields.
   */
  protected boolean includeBooleanAutofillCommand() {
    return true;
  }

  // ----------------------------------------------------------------------
  // PROTECTED BUILDING METHODS
  // ----------------------------------------------------------------------

  private void buildDisplay() {
    // building
    if (model.isSortable()) {
      // !!! override dl ist not good
      dl = createChartHeaderLabel(model.getLabel(), model.getToolTip(), getBlock().getFieldIndex(model),getBlock().getOrderModel());
    } else {
      dl = createLabel(model.getLabel(), model.getToolTip(), false);
    }

    if (!model.isInternal()) {
      // no hidden field (in all modes):
      if (getBlock().isMulti() && !getBlock().noChart()) {
        // add all fields in the display for one column of the chart
        // first the label
        if (!model.noChart()) {
          // is the first column filled with detailViewButton
          final int   leftOffset = (getBlock().noDetail()) ?  -1 : 0;
          
          blockView.add(dl, new KopiAlignment(chartPos + leftOffset, 0, 1, 1, (model.getAlign() == VConstants.ALG_RIGHT)));
         
          // the fields for the values
          displays = new UField[getDisplaySize()];
          for (int i = 0; i < getDisplaySize(); i++) {
            displays[i] = createDisplay(dl, model, false);
            blockView.add(displays[i], new KopiAlignment(chartPos + leftOffset, i + 1, 1, 1, false));
            displays[i].setPosition(i);
          }
          scrollTo(0);
          // detail view of the chart
        }

        if (!getBlock().noDetail() && !model.noDetail()) {
          // create the second label for the detail view
          dlDetail = createLabel(model.getLabel(), model.getToolTip(), true);
          if (columnEnd >= 0) {
            ((UMultiBlock) getBlock().getDisplay()).addToDetail(dlDetail,
                                                                new KopiAlignment(column * 2 - 2, line - 1, 1, 1, false, true));
          }
          // field for the value in the detail view
          detailDisplay = createDisplay(dlDetail, model, true);
          ((UMultiBlock) getBlock().getDisplay()).addToDetail(detailDisplay,
                                                              new KopiAlignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, (lineEnd - line) *2 +1, false));
          detailDisplay.setPosition(0);
          detailDisplay.setInDetail(true);
        }

        // update text
        if (!model.noChart()) {
          for (int i = 0; i < getDisplaySize(); i++) {
             displays[i].updateText();
          }
        }
        if (!getBlock().noDetail() && !model.noDetail()) {
          detailDisplay.updateText();
        }
      } else if (column < 0) {
        // multifields (special fields)
        // take care that in this row is only this multifield
	blockView.add(dl, new MultiFieldAlignment(columnEnd * 2 - 1, line - 1, 1, 1, true));
	displays = new UField[] {createDisplay(dl, model, false)};
	blockView.add(displays[0], new MultiFieldAlignment(columnEnd * 2 - 1,
	                                                   line,
	                                                   1,
	                                                   (lineEnd - line + 1),
	                                                   false));
	displays[0].setPosition(0);
        displays[0].updateText();
      } else {
	displays = new UField[] {createDisplay(dl, model, false)};
  	if (columnEnd >= 0 && !(displays[0] instanceof UActorField)) {
  	  // not an info field and not an actor field  => show label
  	  blockView.add(dl, new KopiAlignment(column * 2 - 2, line - 1, 1, 1, false, true));
  	}
  	if (displays[0] instanceof UActorField) {
  	  // an actor field takes the label and the field space
  	  blockView.add(displays[0], new KopiAlignment(column * 2 - 2, line - 1, (columnEnd - column) * 2 + 2, (lineEnd - line + 1), false));
  	} else {
  	  blockView.add(displays[0], new KopiAlignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, (lineEnd - line + 1), false));
  	}
  	displays[0].setPosition(0);
        displays[0].updateText();
      }

      if (displays != null) {
        for (int i = 0; i < displays.length; i++) {
          fireAccessHasChanged(i); // update access
        }
      }
    }
    fireDisplayCreated();
    // building = false;
  }
  
  /**
   * Returns the displayed size of this column.
   * @return the displayed size of this column.
   */
  protected int getDisplaySize() {
    return getBlock().getDisplaySize();
  }
 
  /**
   *
   */
  @SuppressWarnings("deprecation")
  /*package*/ UField getDisplay() {
    if (blockView.getDisplayLine() == -1) {
      return null;
    } else {
      if (getBlock().isChart() && blockView.inDetailMode()) {
        return detailDisplay;
      } else {
        return (displays == null) ? null : displays[blockView.getDisplayLine()];
      }
    }
  }

  // ----------------------------------------------------------------------
  // FOCUS ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Transfers focus to next accessible field (tab typed)
   * @param display The field display.
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may be raised in leave()
   */
  public void transferFocus(UField display) throws VException {
    int		recno = blockView.getRecordFromDisplayLine(display.getPosition());

    // go to the correct block if necessary
    if (getBlock() != model.getForm().getActiveBlock()) {
      if (! getBlock().isAccessible()) {
	throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
      }
      model.getForm().gotoBlock(getBlock());
    }

    // go to the correct record if necessary
    // but only if we are in the correct block now
    if (getBlock() == model.getForm().getActiveBlock()
        && getBlock().isMulti()
        && recno != getBlock().getActiveRecord()
        && getBlock().isRecordAccessible(recno)) {
      getBlock().gotoRecord(recno);
    }

    // go to the correct field if already necessary
    // but only if we are in the correct record now
    if (getBlock() == model.getForm().getActiveBlock()
        && recno == getBlock().getActiveRecord()
        && model != getBlock().getActiveField()
        && display.getAccess() >= ACS_VISIT) {
      getBlock().gotoField(model);
    }
  }

  // ----------------------------------------------------------------------
  // NAVIGATING
  // ----------------------------------------------------------------------

  /**
   * Changes access dynamically, overriding mode access
   */
  public void fireAccessHasChanged(int recno) {
    // Comment out because:
    // update only the necessary Display in the column
    int         rowInDisplay = blockView.getDisplayLine(recno);

    if (displays != null) {
      if (rowInDisplay != -1) {
        // -1 means currently not displayed
        displays[rowInDisplay].updateAccess();
      }
    }
    if (detailDisplay != null && detailDisplay.getPosition() == rowInDisplay) {
      detailDisplay.updateAccess();
    }
  }
  
  /**
   * Changes the color properties of a field.
   */
  public void fireColorHasChanged(int recno) {
    int         rowInDisplay = blockView.getDisplayLine(recno);

    if (displays != null) {
      if (rowInDisplay != -1) {
        // -1 means currently not displayed
        displays[rowInDisplay].updateColor();
      }
    }
    if (detailDisplay != null && detailDisplay.getPosition() == rowInDisplay) {
      detailDisplay.updateColor();
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY UTILS
  // ----------------------------------------------------------------------

  /**
   * Clears all display fields.
   */
  public void scrollTo(int toprec) {
    if (displays != null) {
      for (int i = 0; i < displays.length; i++) {
        displays[i].updateFocus();
        displays[i].updateAccess();
        displays[i].updateText();
        displays[i].updateColor();
      }
    }
    if (detailDisplay != null) {
      int       record = blockView.getModel().getActiveRecord();
      // is there no active line, show the same content then the first row
      // in the chart
      int       dispLine = (record >= 0) ? blockView.getDisplayLine(record) : 0;
      // is the active line, is not in the visible part then show the same
      // content then in the first line of the chart
      if (dispLine < 0) {
        dispLine = 0;
      }

      detailDisplay.setPosition(dispLine);
      detailDisplay.updateFocus();
      detailDisplay.updateAccess();
      detailDisplay.updateText();
      detailDisplay.updateColor();
    }
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   * @param	block		This action should block the UI thread ?
   * @deprecated                use method performAsyncAction
   */
  // USE METHOD IN FORM
  public void performAction(final KopiAction action, boolean block) {
    blockView.getFormView().performAsyncAction(action);
  }
  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   * @param	block		This action should block the UI thread ?
   */
  public void performAsyncAction(final KopiAction action) {
    blockView.getFormView().performAsyncAction(action);
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param	action		the action to perform.
   */
  public void performBasicAction(final KopiAction action) {
    blockView.getFormView().performBasicAction(action);
  }

  /**
   * Performs a void trigger
   *
   * @param	VKT_Type	the number of the trigger
   */
  public void executeVoidTrigger(final int VKT_Type) throws VException {
    getBlock().executeVoidTrigger(VKT_Type);
  }

  /**
   * return the block of the model
   */
  public VBlock getBlock() {
    return model.getBlock();
  }
  
  /**
   * Called when the display is created for this row controller.
   * This may be used to execute actions after creating the field
   * displays.
   */
  protected void fireDisplayCreated() {
    // to be redefined if needed
  }
  
  /**
   * Returns the field handler instance.
   * @return The field handler instance.
   */
  public FieldHandler getFieldHandler() {
    return fieldHandler;
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * prepare a snapshot
   *
   * @param	fieldPos	position of this field within block visible fields
   */
  public void prepareSnapshot(int fieldPos, boolean activ) {
    for (int i = 0; i < displays.length; i++) {
      displays[i].prepareSnapshot(fieldPos, activ);
    }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public final VField getModel() {
    return model;
  }

  public UBlock getBlockView() {
    return blockView;
  }

  public final UField[] getDisplays() {
    return displays;
  }

  public final ULabel getLabel() {
    return dl;
  }

  public final ULabel getDetailLabel() {
    return dlDetail;
  }

  public final UField getDetailDisplay() {
    return detailDisplay;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final FieldHandler    	fieldHandler;

  private final int                     index;
  // static (compiled) data
  private final boolean		        hasAutofill;	// RE
  private	boolean			hasNewItem;	// MO
  private	boolean			hasEditItem;	// VE
  //private	boolean			hasEditItem_S;	// IT !!!!

  private	VCommand[]		commands;	// commands
  private	UField[]		displays;	// the object displayed on screen
  private	ULabel			dl;		// label text
  private	ULabel			dlDetail;	// label text (chart)
  private	UField          	detailDisplay;	// the object displayed on screen (detail)
  private	int			line;		// USE A VPosition !!!!
  private       int                     lineEnd;           
  private	int			column;
  private	int			columnEnd;
  private	int			chartPos;

  private      	UBlock          	blockView;
  // dynamic data
  private	VField			model;	        // The corresponding VField
  private	Vector<VCommand>	activeCommands;	// commands currently actives
  private	VCommand		incrementCommand;
  private	VCommand		decrementCommand;
  private	VCommand		autofillCommand;
}
