/*
 * Copyright (C) 1990-2005 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2 as published by the Free Software Foundation.
 *
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

package at.dms.vkopi.lib.form;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.UIManager;

import at.dms.util.base.InconsistencyException;
import at.dms.util.base.Utils;
import at.dms.xkopi.lib.base.KopiUtils;
import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.visual.*;

/**
 * This class implements all UI actions on fields
 */
public class VFieldUI implements VConstants, ActionHandler {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public VFieldUI(DBlock blockView, VField model)
  {
    SwingThreadHandler.verifyRunsInEventThread("VField <init>");
    this.blockView = blockView;
    this.model = model;
    activeCommands = new Vector();
    fieldHandler = new FieldHandler();
    model.addFieldListener(fieldHandler);

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

  /**
   * Create a display widget for this field
   */
  private DField createDisplay(DLabel label, VField model) {
    DField      field = null;

    switch (model.getType()) {
    case VField.MDL_FLD_COLOR:
      field = new DColorField(this, label, model.getAlign(), 0);
      break;
    case VField.MDL_FLD_IMAGE:
      field = new DImageField(this, label, model.getAlign(), 0, ((VImageField) model).getIconWidth(), ((VImageField) model).getIconHeight());
      break;
    case VField.MDL_FLD_EDITOR:
      field = new DTextEditor(this, label, model.getAlign(), 0, ((VTextField) model).getHeight());
      break;
    case VField.MDL_FLD_TEXT:
      field = new DTextField(this, label, model.getAlign(), model.getOptions());
      break;
    default:
      throw new InconsistencyException("Type of model " + model.getType() + " not supported.");
    }
    return field;
  }


  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------


  /**
   * Display error
   */
  public void displayFieldError(String message) {
    if (blockView.getDisplayLine(getBlock().getActiveRecord()) == -1) {
      model.getForm().error(message);
      return;
    }
    DField	display = displays[blockView.getDisplayLine(getBlock().getActiveRecord())];
    display.setBlink(true);
    model.getForm().error(message);
    display.setBlink(false);
    try {
      transferFocus(display);
    } catch (VException e) {
      throw new InconsistencyException();
    }
  }

  private void resetCommands() {
      for (int i = 0; i < activeCommands.size(); i++) {
	((VCommand)activeCommands.elementAt(i)).setEnabled(false);
      }
      activeCommands.setSize(0);
      needResetCommands = false;
      if (model.hasFocus()) {
	if (hasEditItem_S()) { // TRY TO REMOVE !!!!
          VCommand      command = blockView.getFormView().cmdEditItem_S;

	  activeCommands.addElement(command);
	  command.setEnabled(true);
	} else if (hasAutofill) {
          VCommand      command = blockView.getFormView().cmdAutofill;

	  activeCommands.addElement(command);
	  command.setEnabled(true);
	}
	if (hasNewItem) {
          VCommand      command = blockView.getFormView().cmdNewItem;

          activeCommands.addElement(command);
	  command.setEnabled(true);
	}
	if (hasEditItem) {
          VCommand      command = blockView.getFormView().cmdEditItem;

          activeCommands.addElement(command);
	  command.setEnabled(true);
	}
        VCommand[]      commands = model.getCommand();

	if (commands != null) {
	  for (int i = 0; i < commands.length; i++) {
	    if (commands[i].isActive(getBlock().getMode())) {
	      activeCommands.addElement(commands[i]);
	      commands[i].setEnabled(true);
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
    Vector	cmds = new Vector();

    for (int i = 0; commands != null && i < commands.length; i++) {
      cmds.addElement(commands[i]);
    }
    if (hasEditItem_S()) {
      cmds.addElement(blockView.getFormView().cmdEditItem_S);
    } else if (hasAutofill) {
      cmds.addElement(blockView.getFormView().cmdAutofill);
    }
    if (hasNewItem) {
      cmds.addElement(blockView.getFormView().cmdNewItem);
    }
    if (hasEditItem) {
      cmds.addElement(blockView.getFormView().cmdEditItem);
    }

    return (VCommand[])Utils.toArray(cmds, VCommand.class);
  }

  /**
   * Clears all display fields.
   */
  /*package*/ void close() {
    for (int i = 0; i < activeCommands.size(); i++) {
      ((VCommand)activeCommands.elementAt(i)).setEnabled(false);
    }
    activeCommands.setSize(0);
  }

  /**
   * resetLabel
   */
  /*package*/ void resetLabel() {
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
  /*package*/ VCommand getIncrementCommand() {
    return incrementCommand;
  }

  /**
   * @return the associated decrementCommand
   */
  /*package*/ VCommand getDecrementCommand() {
    return decrementCommand;
  }

  // ----------------------------------------------------------------------
  // PROTECTED BUILDING METHODS
  // ----------------------------------------------------------------------

  private void buildDisplay() {
    // building
    if (model.isSortable()) {
      // !!! override dl ist not good
      dl = new ChartHeaderLabel(model.getLabel(), model.getToolTip(), getBlock().getFieldIndex(model),getBlock().getOrderModel());
    } else {
      dl = new DLabel(model.getLabel(), model.getToolTip());
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
          displays = new DField[getBlock().getDisplaySize()];
          for (int i = 0; i < getBlock().getDisplaySize(); i++) {
            displays[i] = createDisplay(dl, model);
            blockView.add(displays[i], new KopiAlignment(chartPos + leftOffset, i + 1, 1, false));
            displays[i].setPosition(i);
          }
          scrollTo(0);
          // detail view of the chart
        }

        if (!getBlock().noDetail() && !model.noDetail()) {
          // create the second label for the detail view
          dlDetail = new DLabel(model.getLabel(), model.getToolTip());
          if (columnEnd >= 0) {
            ((DMultiBlock) getBlock().getDisplay()).addToDetail(dlDetail,
                                                                new KopiAlignment(column * 2 - 2, line - 1, 1, false, true));
          }
          // field for the value in the detail view
          detailDisplay = createDisplay(dlDetail, model);
          ((DMultiBlock) getBlock().getDisplay()).addToDetail(detailDisplay,
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
	displays = new DField[] {createDisplay(dl, model)};
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
	displays = new DField[] {createDisplay(dl, model)};
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
  /*package*/ DField getDisplay() {
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
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may be raised in leave()
   */
  public void transferFocus(DField display) throws VException {
    int		recno = blockView.getRecordFromDisplayLine(display.getPosition());

    // go to the correct block if necessary
    if (getBlock() != model.getForm().getActiveBlock()) {
      if (! getBlock().isAccessible()) {
	throw new VExecFailedException(Message.getMessage("actionInhibited"));
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
  /*package*/ void scrollTo(int toprec) {
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

  // ----------------------------------------------------------------------
  // F2
  // ----------------------------------------------------------------------



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

  public final VField getModel() {
    return model;
  }

  public DBlock getBlockView() {
    return blockView;
  }

  // ----------------------------------------------------------------------
  // FieldListener messages called in any thread
  // ----------------------------------------------------------------------

  class FieldHandler implements FieldListener {
  /**
   * Gets the displayed value.
   *
   * @param	trim		Should the text be trimmed ?
   */
  public Object getDisplayedValue(boolean trim) {
    final DField field;

    field = (DField) getCurrentDisplay();
    if (field instanceof DTextField) {
      String	text = ((DTextField)field).getText();
      if (!trim){
	return text;
      } else if (model.getHeight() == 1) {
	return KopiUtils.trimString(text);
      } else {
	return KopiUtils.trailString(text);
      }
    } else {
      return field.getObject();
    }
  }



  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception	VException	an exception may occur in gotoNextField
   */
  public boolean predefinedFill() throws VException {
    boolean     filled;

    filled = model.fillField(new GUIPredefinedValueHandler(VFieldUI.this, blockView.getFormView(), getDisplay()));
    if (filled) {
      model.getBlock().gotoNextField();
    }
    return filled;
  }


  // ---------------------------------------------------------------------
  // IMPLEMENTATION FieldListener
  // ---------------------------------------------------------------------

  /**
   * enter a field
   */
  public void enter() {
    // this is the correct thread to calculate the display of the
    // field NOT later in the event thread
    final DField      enterMe = getDisplay();

    if (enterMe != null) {
      SwingThreadHandler.start(new Runnable() {
          public void run() {
            resetCommands();
            enterMe.enter(true);
          }
        });
    }
  }

  /**
   * leaves field on the desktop
   */
  public void leave() {
    // this is the correct thread to calculate the display of the
    // field NOT later in the event thread
    final DField      leaveMe = getDisplay();

    if (leaveMe != null) {
      SwingThreadHandler.start(new Runnable() {
          public void run() {
            resetCommands();
            leaveMe.leave();
          }
        });
    }
  }

    /**
     *
     */
  public boolean loadItem(int mode) throws VException {
    int	id = -1;

    if (mode == VForm.CMD_NEWITEM) {
      id = ((VDictionaryForm)Module.getKopiExecutable(model.getList().getNewForm())).newRecord(model.getForm());
    } else if (mode == VForm.CMD_EDITITEM) {
      try {
	updateModel();
	if (!model.isNull(model.getBlock().getActiveRecord())) {
	  int	val = model.getListID();
	  if  (val != -1) {
	    id = ((VDictionaryForm)Module.getKopiExecutable(model.getList().getNewForm())).editWithID(model.getForm(), val);
	  } else {
	    mode = VForm.CMD_EDITITEM_S;
	  }
	} else {
	  mode = VForm.CMD_EDITITEM_S;
	}
      } catch (VException e) {
	mode = VForm.CMD_EDITITEM_S;
      }
    }
    if (mode == VForm.CMD_EDITITEM_S) {
      id = ((VDictionaryForm)Module.getKopiExecutable(model.getList().getNewForm())).openForQuery(model.getForm());
    }
    if (id == -1) {
      if (mode == VForm.CMD_EDITITEM || mode == VForm.CMD_EDITITEM_S) {
	model.setNull(model.getBlock().getActiveRecord());
      }
      throw new VExecFailedException();	// no message needed
    }
    model.setValueID(id);
    return true;
  }

    public void fieldError(String message) {
      displayFieldError(message);
    }

    public void labelChanged() {
      SwingThreadHandler.startEnqueued(new Runnable() {
          public void run() {
            resetLabel();
          }
        });
    }

    public void searchOperatorChanged() {
      int               operator = model.getSearchOperator();
      final String      info = operator == SOP_EQ ? null : OPERATOR_NAMES[operator];

      SwingThreadHandler.startEnqueued(new Runnable() {
          public void run() {
            if (dl != null) {
              dl.setInfoText(info);
            }
            if (dlDetail != null) {
              dl.setInfoText(info);
            }
          }
        });
    }

    public void valueChanged(int r) {
      final int         dispRow = blockView.getDisplayLine(r);

     if (dispRow != -1) {
        SwingThreadHandler.startEnqueued(new Runnable() {
            public void run() {
              if (displays != null) {
                displays[dispRow].updateText();
              }
              if (detailDisplay != null) {
                detailDisplay.updateText();
              }
            }
          });
      }
    }

    public void accessChanged(final int row) {
      if (blockView.getDisplayLine(row) != -1) {
        SwingThreadHandler.startEnqueued(new Runnable() {
            public void run() {
              fireAccessHasChanged(row);
            }
          });
      }
    }

    public void updateModel()  throws VException {
      if (model.isChanged() && (model.hasFocus())) {
        model.checkType(getDisplayedValue(true));
      }
    }

    /**
     * @deprecated
     */
    public void updateText() throws VException {
      updateModel();
    }

    public boolean requestFocus() throws VException {
      transferFocus(getDisplay());
      return true;
    }

    public Component getCurrentDisplay() {
      return getDisplay();
    }
  }

  class ChartHeaderLabel extends DLabel implements VBlock.OrderListener {
    ChartHeaderLabel(String text, String help, int index, VBlock.OrderModel model) {
      super(text, help);

      fieldIndex = index;
      sortModel = model;

      sortModel.addSortingListener(this);

      addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            sortModel.sortColumn(fieldIndex);
          }
        });
    }

    public void orderChanged() {
      repaint();
    }

     public void paint(Graphics g) {
       super.paint(g);

       int      w = getSize().width;
       int      order = sortModel.getColumnOrder(fieldIndex);

       switch (order) {
       case VBlock.OrderModel.STE_INC:
         g.setColor(color_active);
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);           
         g.setColor(color_mark);
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);           
         break;
       case VBlock.OrderModel.STE_DESC:
         g.setColor(color_mark);
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);           
         g.setColor(color_active);
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);           
         break;
       case VBlock.OrderModel.STE_UNORDERED:
       default:
         g.setColor(color_mark);
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{1, 8, 8}, 3);           
         g.fillPolygon(new int[]{w-6, w-1, w-11}, new int[]{16, 10, 10}, 3);           
       }
    }
    
    int                 fieldIndex;
    VBlock.OrderModel   sortModel;
  }


  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final FieldHandler    fieldHandler;

  // static (compiled) data
  private final boolean		hasAutofill;	// RE
  private	boolean		hasNewItem;	// MO
  private	boolean		hasEditItem;	// VE
  //private	boolean		hasEditItem_S;	// IT !!!!

  private	VCommand[]	commands;	// commands
  private	DField[]	displays;	// the object displayed on screen
  private	DLabel		dl;		// label text
  private	DLabel		dlDetail;	// label text (chart)
  private	DField          detailDisplay;	// the object displayed on screen (detail)
  private	int		line;		// USE A VPosition !!!!
  private	int		column;
  private	int		columnEnd;
  private	int		chartPos;

  private       DBlock          blockView;
  // dynamic data
  private	VField		model;	        // The corresponding VField
  private	Vector		activeCommands;	// commands currently actives
  private       boolean		needResetCommands = true;

  private	VCommand	incrementCommand;
  private	VCommand	decrementCommand;
  private	VCommand	autofillCommand;


  private static final Color    color_mark = UIManager.getColor("KopiField.ul.chart");
  private static final Color    color_active = UIManager.getColor("KopiField.ul.chart.active");
  private static final Color    color_back = UIManager.getColor("KopiField.background.skipped.color");
}
