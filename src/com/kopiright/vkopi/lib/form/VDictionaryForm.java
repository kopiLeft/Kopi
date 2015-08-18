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

import java.sql.SQLException;
import java.util.Vector;

import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VExecFailedException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.xkopi.lib.base.DBContextHandler;
import com.kopiright.xkopi.lib.base.DBContext;

@SuppressWarnings("serial")
public abstract class VDictionaryForm extends VForm implements VDictionary {

  /*
   * ----------------------------------------------------------------------
   * Constructor / build
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
//   protected VDictionaryForm(VForm parent) throws VException {
//     super(parent);
//   }

  /**
   * Constructor
   */
  protected VDictionaryForm(DBContextHandler parent) throws VException {
    super(parent);
  }

  /**
   * Constructor
   */
  protected VDictionaryForm(DBContext parent) throws VException {
    super(parent);
  }

  /**
   * Constructor
   */
  protected VDictionaryForm() throws VException {
    super();
  }

  /**
   * build everything after loading
   *
  protected void build() {
    block = getBlock(0); !!!!! move
    assert !block.isMulti();
    super.build();
  }
*/
  /**
   *
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  // NOT USED
//   public void openWithID(VWindow parent, int id) throws VException {
//     setDBContext(parent.getDBContext());
//     doNotModal();

//     fetchBlockRecord(0, id);
//   }

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  public int editWithID(VWindow parent, int id) throws VException {
    setDBContext(parent.getDBContext());
    editID = id;
    doModal(parent);
    newRecord = false;
    editID = -1;
    return this.id;
  }

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  public int openForQuery(VWindow parent) throws VException {
    setDBContext(parent.getDBContext());
    lookup = true;
    doModal(parent);
    lookup = false;
    return this.id;
  }

  /**
   * create a new record and returns id
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  public int newRecord(VWindow parent) throws VException {
    newRecord = true;
    setDBContext(parent.getDBContext());
    doModal(parent);
    newRecord = false;
    return getID();
  }

  /**
   *
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised during execution
   */
//   public void run() throws VException {
//     block = getBlock(0);
//     assert !block.isMulti();

//     //initialise();
//     //callTrigger(TRG_PREFORM);

//     if (newRecord) {
//       if (getBlock(0) == null) {
// 	gotoBlock(block);
//       }
//       Commands.insertMode(block);
//     } else if (editID != -1) {
//       newRecord = true;

//       fetchBlockRecord(0, editID);

//       getBlock(0).setMode(MOD_UPDATE);
//     }

//     VBlock	block = getActiveBlock();
//     if (block != null) {
//       block.leave(false);
//     }
//     getDisplay().setVisible(true);
//     if (block != null) {
//       block.enter();
//     }
//     ((DForm)getDisplay()).checkUI();
//   }
  public void prepareForm() throws VException {
    block = getBlock(0);
    assert !block.isMulti() : threadInfo();

    //initialise();
    //callTrigger(TRG_PREFORM);

    if (newRecord) {
      if (getBlock(0) == null) {
	gotoBlock(block);
      }
      Commands.insertMode(block);
    } else if (editID != -1) {
      newRecord = true;

      fetchBlockRecord(0, editID);

      getBlock(0).setMode(MOD_UPDATE);
    }

    super.prepareForm();
  }
  
  /**
   * close the form
   */
  public void close(int code) {
    VField      id;

    assert !getBlock(0).isMulti() : threadInfo();

    id = getBlock(0).getField("ID");    //!!! graf 20080403: replace by getIdField()
    if (id != null) {
      Integer   i = id.getInt(0);

      this.id = i == null ? -1 : i.intValue();
    } else {
      this.id = -1;
    }
    super.close(code);
  }

  // ----------------------------------------------------------------------
  // VDICTIONARY IMPLEMENTATION
  // ----------------------------------------------------------------------
  
  @Override
  public int search(VWindow parent) throws VException {
    return openForQuery(parent);
  }
  
  @Override
  public int edit(VWindow parent, int id) throws VException {
    return editWithID(parent, id);
  }
  
  @Override
  public int add(VWindow parent) throws VException {
    return newRecord(parent);
  }

  // ----------------------------------------------------------------------
  // QUERY SEARCH
  // ----------------------------------------------------------------------

  /**
   * The id of selected or new record
   */
  public int getID() {
    return id;
  }

  /**
   *
   */
  public void setMenuQuery(boolean menuQuery) {
    this.menuQuery = menuQuery;
  }

  /**
   *
   */
  public void saveFilledField() {
    queryMode = true;
    savedData = new Vector<Object>();
    savedState = new Vector<Integer>();
    VField[] fields = block.getFields();

    for (int i = 0; i < fields.length; i++) {
      savedData.addElement(fields[i].getObject(0));
    }
    for (int i = 0; i < fields.length; i++) {
      savedState.addElement(new Integer(fields[i].getSearchOperator()));
    }
  }

  /**
   *
   */
  private void retrieveFilledField() throws VException {
    queryMode = false;

    super.reset();

    VField[] fields = block.getFields();

    for (int i = 0; i < fields.length; i++) {
      fields[i].setObject(0, savedData.elementAt(i));
    }

    block.setRecordChanged(0, false);

    for (int i = 0; i < fields.length; i++) {
      fields[i].setSearchOperator(((Integer)savedState.elementAt(i)).intValue());
    }
  }

  /**
   *
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  public void reset() throws VException {
    if (queryMode) {
      retrieveFilledField();
    } else {
      super.reset();
    }
    queryMode = false;
  }

  /**
   *
   */
  public void interruptRecursiveQuery() {
    queryMode = false;
  }

  /**
   *
   */
  public boolean isNewRecord() {
    return newRecord || lookup || closeOnSave;
  }

  /**
   *
   */
  public void setCloseOnSave() {
    closeOnSave = true;
  }

  /**
   *
   */
  public boolean isRecursiveQuery() {
    return queryMode;
  }

  /**
   *
   */
  public boolean isMenuQuery() {
    return menuQuery;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  private void fetchBlockRecord(final int block, final int record) throws VException {
    try {
      for (;;) {
        try {
          startProtected(null);

          getBlock(block).fetchRecord(record);

          commitProtected();
          break;
        } catch (VException e) {
          abortProtected(e);
        } catch (SQLException e) {
          abortProtected(e);
        } catch (Error e) {
          abortProtected(e);
        } catch (RuntimeException e) {
          abortProtected(e);
        }
      }
    } catch (Throwable e) {
      if (e instanceof VSkipRecordException) {
        throw new VExecFailedException();
      }
      throw new VRuntimeException(e);
    }
  }


  // ----------------------------------------------------------------------
  // QUERY SEARCH
  // ----------------------------------------------------------------------

  private int			id		= -1;
  private int			editID		= -1;
  private boolean		queryMode	= false;
  private boolean		menuQuery	= false;
  private boolean		newRecord	= false;
  private boolean		lookup		= false;
  private boolean		closeOnSave	= false;
  private Vector<Object>	savedData;
  private Vector<Integer>	savedState;
  private VBlock		block;
}
