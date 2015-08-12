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

import java.io.Serializable;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.util.base.Utils;
import com.kopiright.vkopi.lib.visual.ActionHandler;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;

/**
 * This class implements all UI actions on fields
 */
@SuppressWarnings("serial")
public abstract class VFieldUI implements VConstants, ActionHandler, Serializable {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VFieldUI(UBlock blockView, VField model) {
    this.blockView = blockView;
    this.model = model;
    activeCommands = new Vector<VCommand>();
    fieldHandler = createFieldHandler();
    model.addFieldListener(fieldHandler);
    model.addFieldChangeListener(fieldHandler);

    VPosition   pos = model.getPosition();

    if (pos != null) {
      this.line = pos.line;
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

    if (model.getList() != null) {
      if (model.getList().getNewForm() != null) {
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
   * @return The created {@link ULabel}.
   */
  protected abstract ULabel createLabel(String text, String help);

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
    if (blockView.getDisplayLine(getBlock().getActiveRecord()) == -1) {
      model.getForm().error(message);
      return;
    }
    UField	display = displays[blockView.getDisplayLine(getBlock().getActiveRecord())];
    display.setBlink(true);
    model.getForm().error(message);
    display.setBlink(false);
    try {
      transferFocus(display);
    } catch (VException e) {
      throw new InconsistencyException();
    }
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
        VCommand      command = model.getForm().cmdAutofill;

        activeCommands.addElement(command);
        command.setEnabled(true);
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
      cmds.addElement(model.getForm().cmdAutofill);
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

  // ----------------------------------------------------------------------
  // PROTECTED BUILDING METHODS
  // ----------------------------------------------------------------------

  private void buildDisplay() {
    // building
    if (model.isSortable()) {
      // !!! override dl ist not good
      dl = createChartHeaderLabel(model.getLabel(), model.getToolTip(), getBlock().getFieldIndex(model),getBlock().getOrderModel());
    } else {
      dl = createLabel(model.getLabel(), model.getToolTip());
    }

    if (!model.isInternal()) {
      // no hidden field (in all modes):
      if (getBlock().isMulti() && !getBlock().noChart()) {
        // add all fields in the display for one column of the chart
        // first the label
        if (!model.noChart()) {
          // is the first column filled with detailViewButton
          final int   leftOffset = (getBlock().noDetail()) ?  -1 : 0;

          blockView.add(dl, new KopiAlignment(chartPos + leftOffset, 0, 1, false));

          // the fields for the values
          displays = new UField[getBlock().getDisplaySize()];
          for (int i = 0; i < getBlock().getDisplaySize(); i++) {
            displays[i] = createDisplay(dl, model, false);
            blockView.add(displays[i], new KopiAlignment(chartPos + leftOffset, i + 1, 1, false));
            displays[i].setPosition(i);
          }
          scrollTo(0);
          // detail view of the chart
        }

        if (!getBlock().noDetail() && !model.noDetail()) {
          // create the second label for the detail view
          dlDetail = createLabel(model.getLabel(), model.getToolTip());
          if (columnEnd >= 0) {
            ((UMultiBlock) getBlock().getDisplay()).addToDetail(dlDetail,
                                                                new KopiAlignment(column * 2 - 2, line - 1, 1, false, true));
          }
          // field for the value in the detail view
          detailDisplay = createDisplay(dlDetail, model, true);
          ((UMultiBlock) getBlock().getDisplay()).addToDetail(detailDisplay,
                                                              new KopiAlignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, false));
          detailDisplay.setPosition(0);
          detailDisplay.setInDetail(true);
        }

        // update text
        if (!model.noChart()) {
          for (int i = 0; i < getBlock().getDisplaySize(); i++) {
             displays[i].updateText();
          }
        }
        if (!getBlock().noDetail() && !model.noDetail()) {
          detailDisplay.updateText();
        }
      } else if (column < 0) {
        // multifields (special fields)
        // take care that in this row is only this multifield
	blockView.add(dl, new MultiFieldAlignment(columnEnd * 2 - 1, line - 1, 1, true));
	displays = new UField[] {createDisplay(dl, model, false)};
	blockView.add(displays[0], new MultiFieldAlignment(columnEnd * 2 - 1,
									 line,
									 1, false));
	displays[0].setPosition(0);
        displays[0].updateText();
      } else {
	if (columnEnd >= 0) {
	  // not an info field => show label
	  blockView.add(dl, new KopiAlignment(column * 2 - 2, line - 1, 1, false, true));
	}
	displays = new UField[] {createDisplay(dl, model, false)};
	blockView.add(displays[0], new KopiAlignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, false));
	displays[0].setPosition(0);
        displays[0].updateText();
      }

      if (displays != null) {
        for (int i = 0; i < displays.length; i++) {
          fireAccessHasChanged(i); // update access
        }
      }
    }
    // building = false;
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
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised in leave()
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
