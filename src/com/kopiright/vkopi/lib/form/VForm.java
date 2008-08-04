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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.l10n.FormLocalizer;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.util.AWTToPS;
import com.kopiright.vkopi.lib.visual.MessageCode;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.Application;
import com.kopiright.vkopi.lib.visual.Constants;
import com.kopiright.vkopi.lib.visual.DWindow;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.Module;
import com.kopiright.vkopi.lib.visual.SActor;
import com.kopiright.vkopi.lib.visual.SDefaultActor;
import com.kopiright.vkopi.lib.visual.UIBuilder;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VHelpViewer;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.WindowController;
import com.kopiright.xkopi.lib.base.DBContext;
import com.kopiright.xkopi.lib.base.DBContextHandler;

public abstract class VForm extends VWindow implements VConstants {

  static {

    WindowController.getWindowController().registerUIBuilder(Constants.MDL_FORM, new UIBuilder() {
        public DWindow createView(VWindow model) {
          DForm         view = new DForm((VForm) model);

          return view;
        }
      });
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  protected VForm(DBContextHandler ctxt) throws VException {
    super(ctxt);
    initIntern(true);
  }

  /**
   * Constructor
   */
  protected VForm(DBContext ctxt) throws VException {
    super(ctxt);
    initIntern(true);
  }

  /**
   * Constructor
   */
  protected VForm() throws VException {
    initIntern(false);
  }

  /**
   * load form
   */
  private void initIntern(boolean enterField) throws VException {
    init();
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].initIntern();
    }
    if (!Application.isGeneratingHelp()) {
      initialise();
      callTrigger(TRG_PREFORM);
    }
    initActors();

    // localize the form using the default locale
    localize(Locale.getDefault());
  }

  /**
   * load form (from compiler)
   */
  protected void init() {
  }

  public int getType() {
    return Constants.MDL_FORM;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public VCommand[] getCommands() {
    return commands;
  }

  /**
   * @deprecated
   */
  public VForm getCaller() {
    return null;
  }

  /**
   * return the name of this field
   */
  public String getHelp() {
    return help;
  }


  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  public boolean allowQuit(){
      boolean   allowed;

      try {
        if (hasTrigger(TRG_QUITFORM)) {
          allowed = ((Boolean) callTrigger(TRG_QUITFORM)).booleanValue();
        } else {
          allowed = super.allowQuit();
        }
      } catch (VException e) {
        allowed = false;
        error(e.getMessage());
      }
      return allowed;
  }

  /**
   * close model if allowed
   */
  public void willClose(final int code) {
    Commands.quitForm(this, VWindow.CDE_ESCAPED);
  }

  /**
   * close the form
   */
  public void destroyModel() {
    try {
      if (activeBlock != null) {
        // !! lackner 2003.07.31
        // why a close before leave???
        // must be before close otherwise NullPointerException
        activeBlock.close();
        activeBlock.leave(false);
      }

      callTrigger(TRG_POSTFORM);
    } catch (VException e) {
      throw new InconsistencyException(e);
    }
    // do not set to null because the values (of
    // fields in the block) are still used
    //    blocks = null;
    super.destroyModel();
  }

  /**
   * implemented for compatiblity with old gui
   * @deprecated
   */
  public void executeAfterStart() {
    //do nothing
    // overrriden in Buchen.vf in fibu
  }

  /**
   * implemented for compatiblity with old gui
   * used in tib/Artikel.vf
   * @deprecated
   */
  public void checkUI() {
    // checkUI does now nothing
    // not useful to call

  }

  public void enableCommands() {
    super.enableCommands();
    // Form-level commands are always enabled
    if (commands != null) {
      for (int i = 0; i < commands.length; i++) {
        commands[i].setEnabled(true);
      }
    }
  }

  /**
   * addCommand in menu
   */
  public void setActors(SActor[] actors) {
    if (actors != null) {
      for (int i = 0; i < actors.length; i++) {
        if (actors[i] instanceof SDefaultActor) {
          switch (((SDefaultActor)actors[i]).getCode()) {
          case VForm.CMD_AUTOFILL:
            autofillActor = actors[i];
            break;
          case VForm.CMD_EDITITEM:
            editItemActor = actors[i];
            break;
          case VForm.CMD_EDITITEM_S:
            editItemActor_S = actors[i];
            break;
          case VForm.CMD_NEWITEM:
            newItemActor = actors[i];
          }
        }
      }
    }
    super.setActors(actors);
  }

  public Environment getEnvironment() {
    if (environment == null) {
      environment = new Environment();
    }
    return environment;
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localize this form
   * 
   * @param     locale  the locale to use
   */
  public void localize(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, Application.getDefaultLocale());
    super.localizeActors(manager); // localizes the actors in VWindow
    localize(manager);
    manager = null;
  }

  /**
   * Localizes this form
   *
   * @param     manager         the manger to use for localization
   */
  private void localize(LocalizationManager manager) {
    FormLocalizer       loc;

    loc = manager.getFormLocalizer(source);
    setTitle(loc.getTitle());
    for (int i = 0; i < pages.length; i++) {
      pages[i] = loc.getPage(i);
    }
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].localize(manager);
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------


  private void initActors() {
    for (int i = 0; i < blocks.length; i++) {
      setActors(blocks[i].getActors());
    }
  }

  protected void prepareForm() throws VException {
    final VBlock        block = getActiveBlock();

    if (block != null) {
      block.leave(false);
    }

    if (block != null) {
      block.enter();
    } else {
    // INSERTED to have an a correct state of this form
      enterBlock();
    }

    setCommandsEnabled(true);
  }

  /**
   * Informs model, that this action was executed on it.
   * For cleanUp/Update/....
   * -) THIS method should do as less as possible
   * -) THIS method should need be used to fix the model
   */
  public void executedAction(KopiAction action) {
    checkForm(action);
  }

  private void checkForm(KopiAction action) {
    // !!! fixes model (if left in a bad state)
    if (activeBlock == null) {
      int       i;

      for (i = 0; i < blocks.length; i++) {
        if (blocks[i].isAccessible()) {
          break;
        }
      }
      assert i < blocks.length : threadInfo() + "No accessible block";
      blocks[i].enter();
      // lackner 2003.07.31
      // - inserted to get information about the usage of this code
      // - can be removed if the method checkUI is removed
      try {
        Application.reportTrouble("DForm chechUI " + Thread.currentThread(),
                                  "Where is this code used? " + action,
                                  this.toString(),
                                  new RuntimeException("CHECKUI: Entered  block " + blocks[i].getName()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // !! end of model fix

    for (int i = 0; i < blocks.length; i++) {
      blocks[i].checkBlock();
      blocks[i].updateBlockAccess();
      //      blocks[i].checkCommands();
    }
  }

  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------

  /**
   * GOTO PAGE X
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by field.leave
   */
  public void gotoPage(int target) throws VException {
    VBlock      block = null;

    for (int i = 0; block == null && i < blocks.length; i++) {
      if (blocks[i].getPageNumber() == target && blocks[i].isAccessible()) {
        block = blocks[i];
      }
    }

    if (block == null) {
      throw new VExecFailedException(MessageCode.getMessage("VIS-00024"));
    }

    gotoBlock(block);
  }

  /**
   * GOTO BLOCK
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by field.leave
   */
  public void gotoBlock(VBlock target) throws VException {
    if (activeBlock != null) {
      activeBlock.leave(true);
    }
      
    target.enter();
  }

  /**
   * Go to the next block
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by field.leave
   */
  public void gotoNextBlock() throws VException {
    assert activeBlock != null : threadInfo() + "Active block is null";

    if (!blockMoveAllowed) {
      Toolkit.getDefaultToolkit().beep();
    }

    int		index = getBlockIndex(activeBlock);
    VBlock	target = null;

    for (int i = 0; target == null && i < blocks.length - 1; i += 1) {
      index += 1;
      if (index == blocks.length) {
	index = 0;
      }

      if (blocks[index].isAccessible()) {
	target = blocks[index];
      }
    }

    if (target == null) {
      throw new VExecFailedException(MessageCode.getMessage("VIS-00025"));
    }

    gotoBlock(target);
  }

  public void enterBlock()  throws VException {
      assert activeBlock == null : "active block = " + activeBlock;
      int	i;

      for (i = 0; i < blocks.length; i++) {
	if (blocks[i].isAccessible()) {
	  break;
	}
      }
      assert i < blocks.length : threadInfo() + "no accessible block";
      gotoBlock(blocks[i]);
  }

  /**
   * Returns true iff the form contents have been changed by the user.
   *
   * NOTE: TRG_CHANGED returns true iff form is considered changed
   */
  public boolean isChanged() {
    if (hasTrigger(TRG_CHANGED)) {
      Object	res;

      try {
	res = callTrigger(TRG_CHANGED);
      } catch (VException e) {
        throw new InconsistencyException();
      }

      return ((Boolean)res).booleanValue();
    } else {
      for (int i = 0; i < blocks.length; i++) {
	if (blocks[i].isChanged()) {
	  return true;
	}
      }
      return false;
    }
  }

  public VBlock[] getBlocks() {
    return blocks;
  }

  public String[] getPages() {
    return pages;
  }

  /**
   * Resets form to initial state
   *
   * NOTE: TRG_RESET returns true iff reset handled by trigger
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by field.leave
   */
  public void reset() throws VException {
    if (hasTrigger(TRG_RESET)) {
      Object	res;

      try {
	res = callTrigger(TRG_RESET);
      } catch (VException e) {
	e.printStackTrace(); // !!!
        throw new InconsistencyException();
      }

      if (((Boolean)res).booleanValue()) {
	return;
      }
    }

    if (activeBlock != null) {
      activeBlock.leave(false);
    }

    for (int i = 0; i < blocks.length; i++) {
      blocks[i].clear();
      blocks[i].setMode(MOD_QUERY); // vincent 14.9.98
    }

    initialise();

    if (activeBlock == null) {
      // it is possible, that the INIT-Trigger of the form
      // has a gotoBlock(...)
      enterBlock();
    }
  }

  /**
   * create a list of items and return id of selected one or -1
   * @param	showUniqueItem	open a list if there is only one item also
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by string formaters
   */
  public int singleMenuQuery(VWindow parent, boolean showUniqueItem) {
    setDBContext(parent.getDBContext());
    return getBlock(0).singleMenuQuery(showUniqueItem);
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callTrigger(int event) throws VException {
    switch (TRG_TYPES[event]) {
    case TRG_VOID:
      executeVoidTrigger(VKT_Triggers[event]);
      return null;
    case TRG_BOOLEAN:
      return new Boolean(executeBooleanTrigger(VKT_Triggers[event]));
    case TRG_INT:
      return new Integer(executeIntegerTrigger(VKT_Triggers[event]));
    default:
      return executeObjectTrigger(VKT_Triggers[event]);
    }
  }

  /**
   * @return If there is trigger associated with event
   */
  protected boolean hasTrigger(int event) {
    return VKT_Triggers[event] != 0;
  }

  public Object executeObjectTrigger(final int VKT_Type) throws com.kopiright.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }
  public boolean executeBooleanTrigger(final int VKT_Type) throws com.kopiright.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }
  public int executeIntegerTrigger(final int VKT_Type) throws com.kopiright.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  // ----------------------------------------------------------------------
  // TRAILING
  // ----------------------------------------------------------------------

  /**
   * Sets form untrailed (commits changes).
   */
  public void commitTrail() {
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].commitTrail();
    }
  }

  /**
   * Restore trailed information.
   */
  public void abortTrail() {
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].abortTrail();
    }
  }

    /**
   * Commits a protected transaction.
   * @exception	Exception	an exception may be raised by DB
   */
  public void commitProtected() throws SQLException {
    super.commitProtected();
    commitTrail();
  }


  /**
   * Handles transaction failure
   * @param	interrupt	interrupt transaction
   */
  public void abortProtected(boolean interrupt) {
    try {
      abortTrail();
    } finally {
      super.abortProtected(interrupt);
    }
  }

  /**
   * Handles transaction failure
   * @param	reason		the reason for the failure.
   * @exception	SQLException	an exception may be raised by DB
   */
   public void abortProtected(SQLException reason) throws SQLException {
    try {
      abortTrail();
    } finally {
      super.abortProtected(false);
      if (!retryableAbort(reason) || !retryProtected()) {
        throw reason;
      }
    }
   }

   public void abortProtected(Error reason) {
    try {
      abortTrail();
      throw reason;
    } finally {
      super.abortProtected(false);
    }
   }

   public void abortProtected(RuntimeException reason)  {
    try {
      abortTrail();
      throw reason;
    } finally {
      super.abortProtected(false);
     }
   }

   public void abortProtected(VException reason) throws VException {
    try {
      abortTrail();
      throw reason;
    } finally {
      super.abortProtected(false);
      }
   }

  /**
   * Handles transaction failure
   * @param	reason		the reason for the failure.
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by DB
   *//* old version
  public void abortProtected(Exception reason) throws VException {
    try {
      abortTrail();
    } finally {
      super.abortProtected(reason);
    }
    }*/

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * Gets the form (this)
   */
  public VForm getForm() {
    return this;
  }

  public void fireCurrentBlockChanged(VBlock oldBlock, VBlock newBlock) {
    Object[]            listeners = formListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== FormListener.class) {
        ((FormListener)listeners[i+1]).currentBlockChanged(oldBlock, newBlock);
      }
    }
  }


  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  public void setFieldSearchOperator(int op) {
    Object[]            listeners = formListener.getListenerList();

    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]== FormListener.class) {
        ((FormListener)listeners[i+1]).setFieldSearchOperator(op);
      }
    }
  }

  /**
   * Returns the number of blocks.
   */
  protected int getBlockCount() {
    return blocks.length;
  }

  /**
   * Returns the block with given index.
   * @param	index		the index of the specified block
   */
  public VBlock getBlock(int index) {
    return blocks[index];
  }

  /**
   * return a Block from its name
   * @param	name		name of the block
   * @return    the first block with this name, or null if the block is not found.
   */
  public VBlock getBlock(String name) {
    for (int i = 0; i < blocks.length; i++) {
      if (name.equals(blocks[i].getName())) {
	return blocks[i];
      }
    }

    return null;
  }

  /**
   * Returns the current block
   */
  public VBlock getActiveBlock() {
    return activeBlock;
  }

  /**
   * Sets the current block
   */
  public void setActiveBlock(VBlock block) {
    VBlock      old = activeBlock;

    activeBlock = block;
    // inform listener
    fireCurrentBlockChanged(old, activeBlock);
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------

  public void addFormListener(FormListener bl) {
    formListener.add(FormListener.class, bl);
  }
  public void removeFormListener(FormListener bl) {
    formListener.remove(FormListener.class, bl);
  }

  // ----------------------------------------------------------------------
  // SHARED COMPONENT
  // ----------------------------------------------------------------------

  /**
   * @deprecated
   */
  protected JFrame getFrame() {
    return getDisplay().getFrame();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  /*
   * Initialises the form.
   */
  protected void initialise() throws VException {
    callTrigger(TRG_INIT);
    for (int i = 0; i < blocks.length; i++) {
      blocks[i].initialise();
    }
  }

  /**
   * Returns the index of the specified block in the form
   */
  protected int getBlockIndex(VBlock blk) {
    for (int i = 0; i < blocks.length; i++) {
      if (blk == blocks[i]) {
	return i;
      }
    }
    throw new InconsistencyException();
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  /**
   *
   */
  public String getName() {
    String	name = getClass().getName();
    int		index = name.lastIndexOf(".");

    return index == -1 ? name : name.substring(index + 1);
  }

  public void genHelp(LatexPrintWriter p,
                      String name,
                      String help,
                      String code)
  {
    for (int i = 0; i < blocks.length; i++) {
      setActors(blocks[i].getActors());
    }
    new VDocGenerator(p).helpOnForm(getName(),
                                    commands,
                                    blocks,
                                    name,
                                    help,
                                    code);
  }

  public String genHelp() {
    String              fileName;
    String              description = getName();
    String              localHelp = "";
    Module              mod ;
    StringBuffer        surl = new StringBuffer();
    VField              field;

    try {
      mod = Application.getMenu().getModule(this);
    } catch (NullPointerException npe) {
	mod = null;
    }
    
    if (mod != null) {
      description = mod.getDescription();
      localHelp = mod.getHelp();
    }
    fileName = new VHelpGenerator().helpOnForm(getName(),
                                               commands,
                                               blocks,
                                               description,
                                               localHelp,
                                               "");

    try {
      surl.append(new File(fileName).toURL().toString());
    } catch (java.net.MalformedURLException mue) {
      throw new InconsistencyException(mue);
    }

    field = getActiveBlock().getActiveField();
    if (field != null) {
      String    anchor = field.getLabel();

      if (anchor == null) {
	anchor = field.getName();
      }
      anchor.replace(' ', '_');

      surl.append("#" + field.getBlock().getTitle().replace(' ', '_') + anchor);
    }

    return surl.toString();
  }

  public void showHelp(VForm form) {
    new VHelpViewer().showHelp(genHelp());
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * Print a snapshot of all blocks
   */
  public void printSnapshot() {
    try {
      // !!! fix this
      //  new DForm(this);
      getDisplay().createFrame();
      getDisplay().setVisible(true);

      OutputStream                      fos;
      javax.swing.RepaintManager        rm;

      rm = javax.swing.RepaintManager.currentManager(getDisplay().getContentPanel());
      rm.setDoubleBufferingEnabled(false);
      getDisplay().setDoubleBuffered(false);

      for (int i = 0; i < blocks.length; i++) { // Walk over blocks
	try {
	  fos = new BufferedOutputStream(new FileOutputStream("images/" + getClass().getName().replace('.', '_') + "_" +
							      blocks[i].getTitle().replace(' ', '_') + ".ps"));

	  if (activeBlock != null) {
	    activeBlock.leave(false);
	  }
	  setActiveBlock(blocks[i]);
          // !!! find alternative correct
// 	  getDForm().setCurrentPage(blocks[i].getPageNumber());
// 	  getDForm().gotoPage(blocks[i].getPageNumber());

	  for (int j = 0; j < blocks.length; j++) {
	    blocks[j].prepareSnapshot(blocks[j] == blocks[i]);
	  }

          // !!! find alternative correct
          //	  getDisplay().getFrame().pack();
	  //getDisplay().repaint();
	  Thread.sleep(1000);
	  Toolkit.getDefaultToolkit().sync();
	  Thread.sleep(1000);

	  int		w = getDisplay().getSize().width / 2;
	  int		h = getDisplay().getSize().height / 2;

	  AWTToPS	ps = new AWTToPS(fos);
	  ps.setBoundingBox(0, 0, w + 2, h + 2);
	  ps.translate(0, 1200 - h - 1);
	  ps.drawRect(0, 0, w + 2, h + 2);
	  ps.translate(0, -(1200 - h - 1));
	  ps.setScale(0.5, 0.5);
	  ps.translate(2, 1200 - (h * 2) - 2);
	  ps.setTransparentColor(UIManager.getColor("snapshot.background"));
	  getDisplay().paint(ps);
	  ps.showPage();
	  fos.close();
	} catch (Exception e) {
	  e.printStackTrace();
	}
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    close(0);
  }

  /*package*/ SActor getDefaultActor(int type) {
    switch (type) {
    case VForm.CMD_NEWITEM:
      return newItemActor;
    case VForm.CMD_EDITITEM:
      return editItemActor;
    case VForm.CMD_EDITITEM_S:
      return editItemActor_S == null ? autofillActor : editItemActor_S;
    case VForm.CMD_AUTOFILL:
      return autofillActor;
    }
    throw new InconsistencyException("UNDEFINED ACTOR: " + type);
  }

  /**
   * Enables the active commands or disable all commands.
   */
  public void setCommandsEnabled(boolean enable) {
    super.setCommandsEnabled(enable);
    // block-level commands
    for (int i = 0; i < blocks.length; i++) {
      if (!enable || blocks[i] == activeBlock) {
        // disable all commands
        // enable only the command of the currentblock
        blocks[i].setCommandsEnabled(enable);
      }
    }
    // form-level commands
    for (int i = 0; i < commands.length; i++) {
      commands[i].setEnabled(enable);
    }
  }

  public String toString() {
    StringBuffer        information = new StringBuffer();

    try {
      information.append("\n===========================================================\nFORM: ");
      information.append(super.toString());
      information.append(" ");
      information.append(getName());
      information.append("\n");

//       if (caller == null) {
//         information.append("caller: null");
//       } else {
//         information.append("caller: ");
//         information.append(caller.getName());
//       }
//       information.append("\n");

      information.append("activeBlock: ");
      if (activeBlock == null) {
        information.append("null");
      } else {
        information.append(activeBlock.getName());
      }
//       information.append("; currentPage: ");
//       information.append(currentPage);
      information.append("\n");

//       information.append("; inAction: ");
//       information.append(inAction);
//       information.append("\n");

      // support better message
      if (blocks != null) {
        for (int i=0; i < blocks.length; i++) {
          VBlock          block = blocks[i];

          if (block != null) {
            information.append(blocks[i].toString());
          } else {
            information.append("Block ");
            information.append(i);
            information.append(" is null \n");
          }
        }
      }
    } catch (Exception e) {
      information.append("exception while retrieving form information. \n");
    }
    information.append("===========================================================\n");

    return information.toString();
  }

  public PrintJob printFormScreen() throws VException {
    return ((DForm) getDisplay()).printForm();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // static (compiled) data
  protected String              source;         // qualified name of source file
  protected VBlock[]		blocks;
  protected String[]		pages;
  protected String		help;
  protected int[]		VKT_Triggers;

  // dynamic data
  private boolean		blockMoveAllowed = true;
  //  private VForm			caller;
  private VBlock		activeBlock;
  //  private int			currentPage = -1;
  protected VCommand[]		commands;	// commands

  private EventListenerList     formListener = new EventListenerList();

  protected Environment         environment;

  // ----------------------------------------------------------------------
  // SHARED DATA MEMBERS
  // ----------------------------------------------------------------------

  private	SActor		autofillActor;
  private	SActor		editItemActor;
  private	SActor		editItemActor_S;
  private	SActor		newItemActor;

  // ---------------------------------------------------------------------
  // PREDEFINED COMMANDS
  // ---------------------------------------------------------------------

  public static final int CMD_NEWITEM		= -2;
  public static final int CMD_EDITITEM		= -3;
  public static final int CMD_EDITITEM_S	= -4;
  public static final int CMD_AUTOFILL		= -5;
}
