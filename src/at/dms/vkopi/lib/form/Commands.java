/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: Commands.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.vkopi.lib.form;

import java.sql.SQLException;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.KnownBugs;
import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VExecFailedException;
import at.dms.vkopi.lib.visual.VRuntimeException;
import at.dms.xkopi.lib.base.DBDeadLockException;
import at.dms.xkopi.lib.base.DBInterruptionException;

/**
 * This class implements predefined commands
 */

public class Commands implements VConstants {
  /*
   * ----------------------------------------------------------------------
   * FORM-LEVEL COMMANDS
   * ----------------------------------------------------------------------
   */

  /**
   * Aborts current processing, resets form.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in form.reset()
   */
  public static void resetForm(final VForm form) throws VException {
    if (! form.isChanged() || form.ask(Message.getMessage("confirm_break"))) {
      form.reset();
    }
  }

  /**
   * Aborts current processing, closes form.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in form.close()
   */
  public static void quitForm(VForm form, int code) {
    if (! form.isChanged() || form.ask(Message.getMessage("confirm_quit"))) {
      form.close(code);
    }
  }

  /**
   * Aborts current processing, closes form.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in form.close()
   */
  public static void quitForm(VForm form) {
    quitForm(form, VForm.CDE_QUIT);
  }

  /*
   * ----------------------------------------------------------------------
   * BLOCK-LEVEL COMMANDS
   * ----------------------------------------------------------------------
   */

  /**
   * Aborts current processing (old)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur in b.clear()
   */
  public static void resetBlock(VBlock b) throws VException {
    if (! b.isChanged() || b.getForm().ask(Message.getMessage("confirm_break"))) {
      b.clear();

      if (b.getForm() instanceof VDictionaryForm) {
	if (((VDictionaryForm)b.getForm()).isRecursiveQuery()) {
	  // !!! until inheritence of commands
	  b.getForm().reset();
	} else if (!((VDictionaryForm)b.getForm()).isNewRecord()) {
	  b.setMode(MOD_QUERY);
	}
      } else {
	b.setMode(MOD_QUERY);
      }
    }
  }


  private static void gotoFieldIfNoActive(VBlock lastBlock) throws VException {
    // it is possible that (for example) the load method is
    // overridden and it inlcude now a gotoBlock(..)

    VForm       form;
    VBlock      activeBlock;

    form = lastBlock.getForm();
    activeBlock = form.getActiveBlock();

    if (activeBlock == null) {
      form.gotoBlock(lastBlock);
    } else {
      if (activeBlock.getActiveField() == null) {
        activeBlock.gotoFirstField();
      }
    }
  }

  /**
   * Menu query block, fetches selected record.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void menuQuery(VBlock b) throws VException {
    VForm	form = b.getForm();
    int		id;

    KnownBugs.freeMemory();

    b.validate();

    if (form instanceof VDictionaryForm) {
      ((VDictionaryForm)form).setMenuQuery(true);
    }

    if ((id = b.singleMenuQuery(false)) != -1) {
      for (;;) {
	try {
	  form.startProtected(Message.getMessage("loading_record"));

	  b.fetchRecord(id);

	  form.commitProtected();

          gotoFieldIfNoActive(b);
	  break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw abortEx;
          }
	} catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new InconsistencyException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new InconsistencyException(abortEx);
          }
	}
      }
    }
    KnownBugs.freeMemory();
  }


  /**
   * Menu query block, fetches selected record.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void recursiveQuery(VBlock b) throws VException {
    VDictionaryForm	form = (VDictionaryForm)b.getForm();
    int		id;

    KnownBugs.freeMemory();

    b.validate();

    form.saveFilledField();

    if ((id = b.singleMenuQuery(false)) != -1) {
      for (;;) {
	try {
	  form.startProtected(Message.getMessage("loading_record"));

          // fetches data to active record
	  b.fetchRecord(id);

	  form.commitProtected();
          gotoFieldIfNoActive(b);
	  break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw abortEx;
          }
	} catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new InconsistencyException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new InconsistencyException(abortEx);
          }
	}
      }
    }
    if (id == -1) {
      form.interruptRecursiveQuery();
    }
  }

  /**
   * Menu query block, fetches selected record, then moves to next block
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void queryMove(VBlock b) throws VException {
    VForm	form = b.getForm();
    int		id;

    b.validate();

    if ((id = b.singleMenuQuery(false)) != -1) {
      try {
	for (;;) {
	  try {
	    form.startProtected(Message.getMessage("loading_record"));

	    b.fetchRecord(id);

	    form.commitProtected();
	    break;
          } catch (VException e) {
            try {
              form.abortProtected(e);
            } catch(VException abortEx) {
              throw new VExecFailedException(abortEx.getMessage(), abortEx);
            }
	  } catch (SQLException e) {
            try {
              form.abortProtected(e);
            } catch(SQLException abortEx) {
              throw new VExecFailedException(abortEx);
            }
	  } catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new InconsistencyException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new InconsistencyException(abortEx);
          }
	}
	}
      } catch (VException e) {
	throw e;
      }

      // goto next block
      for (int i = 0; i < form.getBlockCount() - 1; i += 1) {
	if (b == form.getBlock(i)) {
	  form.gotoBlock(form.getBlock(i + 1));
	  return;
	}
      }

      throw new InconsistencyException("FATAL ERROR: NO BLOCK ACCESSIBLE");
    }
  }

  /**
   * Queries block, fetches first record.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void serialQuery(VBlock b) throws VException {
    VForm	form = b.getForm();

    b.validate();

    KnownBugs.freeMemory();

    // !!! laurent : there is a bug with serialQuery : if you run a
    // serialQuery and no row can be fetched the transaction is not
    // correctly aborted. I will enumerate the different fixes.
    try {
      for (;;) {
	try {
	  form.startProtected(Message.getMessage("searching_database"));

	  try {
	    b.load();
            gotoFieldIfNoActive(b);
	  } catch (VQueryOverflowException e) {
	    // !!! HANDLE OVERFLOW WARNING
	  }
          /*
          // FIX #1 :
           catch (VQueryNoRowException nre) {
            // DBNoRowException is more suitable but it does not extends SQLException
            throw new SQLException("No Row");
          }
          */
	  form.commitProtected();
	  break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw abortEx;
          }
	} catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new InconsistencyException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new InconsistencyException(abortEx);
          }
	}
      }
    } catch (VException e) {
      // FIX #2
      /* form.abortProtected(true); // The form's fields have to be reset */
      throw e;
    }
  }

  /**
   * Sets the block into insert mode.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void insertMode(VBlock b) throws VException {
    assert !b.isMulti() : "The command InsertMode can be used only with a single block.";
    assert b.getMode() != MOD_INSERT : "The block " + b.getName() + " is already in INSERT mode.";

    /*
      if (b.getMode() == MOD_UPDATE && FormNargs(CURRENTFORM) != 0) {
      RingBell();
      throw new VException(EXC_FAILED);
      }
    */

    if (b.getMode() == MOD_UPDATE && b.isChanged()) {
      if (!b.getForm().ask(Message.getMessage("confirm_insert_mode"))) {
	return;
      }
    }

    boolean changed = b.isRecordChanged(0);

    b.setMode(MOD_INSERT);
    b.setDefault();
    b.setRecordFetched(0, false);
    b.setRecordChanged(0, changed);

    if (!b.isMulti() && b.getForm().getActiveBlock() == b) {
      b.gotoFirstUnfilledField();
    }
  }

  /**
   * Saves current block (insert or update)
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void saveBlock(VBlock b) throws VException {
    VForm	form = b.getForm();

    KnownBugs.freeMemory();

    assert !b.isMulti() : "saveBlock can be used only with a single block.";

    b.validate();

    if (!b.isChanged() && !form.ask(Message.getMessage("confirm_save_unchanged"))) {
      return;
    }

    try {
      for (;;) {
	try {
	  form.startProtected(Message.getMessage("saving_record"));

	  b.save();

	  form.commitProtected();
	  break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	}
      }
    } catch (VException e) {
      throw e;
    }

    saveDone(b, true);
  }

  /**
   * saveDone
   * This method should be called after a self made save trigger
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void saveDone(VBlock b) throws VException {
    saveDone(b, false);
  }

  /**
   * saveDone
   * This method should be called after a self made save trigger
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  private static void saveDone(VBlock b, boolean single) throws VException {
    VForm	form = b.getForm();
    int		mode = b.getMode();

    if (form instanceof VDictionaryForm) {  // !!!until inheritence of commands
      if (((VDictionaryForm)form).isNewRecord()) {
	form.close(VForm.CDE_VALIDATE);
	return;
      } else if (((VDictionaryForm)form).isRecursiveQuery() ||
		 ((VDictionaryForm)form).isMenuQuery()) {
	form.reset();
	return;
      }
    }

    switch (mode) {
    case MOD_INSERT:
      if (single) {
	b.clear();
	b.setDefault();
	b.gotoFirstUnfilledField();
      } else {
	form.reset();
      }
      return;

    case MOD_UPDATE:
      try {
	b.fetchNextRecord(1);
	return;
      } catch (VException e) {
	// ignore it
	//e.printStackTrace();
      }

      b.clear();
      b.setMode(MOD_QUERY);
      return;

    default:
      throw new InconsistencyException();
    }
  }


  /**
   * Deletes current block
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void deleteBlock(VBlock b) throws VException {
    VForm	form = b.getForm();

    if (! form.ask(Message.getMessage("confirm_delete"))) {
      return;
    }

    try {
      for (;;) {
	try {
	  form.startProtected(Message.getMessage("deleting_record"));

	  b.delete();

	  form.commitProtected();
	  break;
        } catch (VException e) {
          try {
            form.abortProtected(e);
          } catch(VException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (SQLException e) {
          try {
            form.abortProtected(e);
          } catch(DBDeadLockException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(DBInterruptionException abortEx) {
            throw new VExecFailedException(Message.getMessage("abort_transaction"));
          } catch(SQLException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (Error e) {
          try {
            form.abortProtected(e);
          } catch(Error abortEx) {
            throw new VExecFailedException(abortEx);
          }
	} catch (RuntimeException e) {
          try {
            form.abortProtected(e);
          } catch(RuntimeException abortEx) {
            throw new VExecFailedException(abortEx);
          }
	}
      }
    } catch (VException e) {
      throw e;
    }

    if (b.getForm() instanceof VDictionaryForm) {  // !!!until inheritence of commands
      if (((VDictionaryForm)b.getForm()).isRecursiveQuery()) {
	b.getForm().reset();
	return;
      }
    }

    b.setMode(MOD_QUERY);

    // Fetch record (forward)
    try {
      b.fetchNextRecord(1);
      return;
    } catch (VException e) {
      // ignore it
    }

    // Fetch record (backward)
    try {
      b.fetchNextRecord(-1);
      return;
    } catch (VException e) {
      // ignore it
    }

    // No more records
    b.setMode(MOD_QUERY);
    b.clear();
  }

  /**
   * Inserts an empty line in multi-block.
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during DB access
   */
  public static void insertLine(VBlock b) throws VException {
    int		recno;

    assert b.isMulti() : "The command InsertLine can be used only with a multi block.";
    assert b == b.getForm().getActiveBlock() : b.getName() + " is not the active block. (" + b.getForm().getActiveBlock().getName() +")";

    recno = b.getActiveRecord();
    b.leaveRecord(true);

    try {
      b.insertEmptyRecord(recno);
    } catch (VException e) {
      throw new VExecFailedException(Message.getMessage("too_many_rows"));
    }
    finally {
      b.gotoRecord(recno);
      // REPLACED BY gotoRecord because enterRecord, does not enter any field.
      //      b.enterRecord(recno);
      // REPLACED BY fireBlockChanged()
      // which is done in enterRecord
//       b.getDisplay().refresh(true);
    }
  }

  /**
   * Sets the search operator for the current field
   */
  public static void setSearchOperator(VBlock b) {
    VField	f = b.getActiveField();

    if (f == null) {
      return;
    }

    int		v = new ListDialog(Message.getMessage("search_operator"),
				   new String[] {
				     Message.getMessage("operator_eq"),
				       Message.getMessage("operator_lt"),
				       Message.getMessage("operator_gt"),
				       Message.getMessage("operator_le"),
				       Message.getMessage("operator_ge"),
				       Message.getMessage("operator_ne")
				       }
				   ).selectFromDialog(b.getForm().getDisplay(), f.getDisplay());
    if (v != -1) {
      f.setSearchOperator(v);
      f.getForm().setFieldSearchOperator(f.getSearchOperator());
    }
  }

  /**
   * Navigate between accessible blocks
   * @exception	at.dms.vkopi.lib.visual.VException	an exception may occur during block focus transfert
   */
  public static void changeBlock(VBlock b) throws VException {
    b.validate();

    KnownBugs.freeMemory();

    int		blockCount = b.getForm().getBlockCount();
    VBlock[]	blockTable = new VBlock[blockCount - 1];
    String[]	titleTable = new String[blockCount - 1];
    int		otherBlocks = 0;

    for (int i = 0; i < blockCount; i++) {
      if (b == b.getForm().getBlock(i)) {
	continue;
      }

      if (! b.getForm().getBlock(i).isAccessible()) {
	continue;
      }

      blockTable[otherBlocks] = b.getForm().getBlock(i);
      titleTable[otherBlocks] = blockTable[otherBlocks].getTitle();
      otherBlocks += 1;
    }

    int		sel;

    switch (otherBlocks) {
    case 0:	// no other block is accessible
      sel = -1;
      break;

    case 1:	// toggle between 2 accessible blocks
      sel = 0;
      break;

    default:	// let user choose where to go
      sel = new ListDialog(Message.getMessage("pick_in_list"),
			   titleTable,
			   otherBlocks).selectFromDialog(b.getForm(), null);
    }

    if (sel < 0) {
      b.getForm().gotoBlock(b);
    } else {
      b.getForm().gotoBlock(blockTable[sel]);
    }
  }

  /*
   * ----------------------------------------------------------------------
   * FIELD-LEVEL COMMANDS
   * ----------------------------------------------------------------------
   */

  /**
   * Increment the value of the field
   */
  public static void increment(VField field) throws VException {
    if (field instanceof VIntegerField) {
      VIntegerField     f = (VIntegerField)field;
      int               r = f.getBlock().getActiveRecord();

      //      f.getUI().transferFocus(f.getDisplay());
      f.requestFocus();
      f.validate();
      if (f.isNull(r)) {
	f.setInt(r, new Integer(1));
      } else {
	f.setInt(r, new Integer(f.getInt(r).intValue() + 1));
      }
    }
  }

  /**
   * Increment the value of the field
   */
  public static void decrement(VField field) throws VException {
    if (field instanceof VIntegerField) {
      VIntegerField     f = (VIntegerField)field;
      int               r = f.getBlock().getActiveRecord();

      //      f.getUI().transferFocus(f.getDisplay());
      f.requestFocus();
      f.validate();
      if (f.isNull(r)) {
	f.setInt(r, new Integer(1));
      } else {
	f.setInt(r, new Integer(f.getInt(r).intValue() - 1));
      }
    }
  }
}
