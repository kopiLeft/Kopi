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

import org.kopi.vkopi.lib.base.UComponent;
import org.kopi.vkopi.lib.form.KopiAlignment;
import org.kopi.vkopi.lib.form.UBlock;
import org.kopi.vkopi.lib.form.UForm;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.ui.vaadin.addons.Block;
import org.kopi.vkopi.lib.ui.vaadin.addons.BlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.addons.SimpleBlockLayout;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;

import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;

/**
 * The <code>DBlock</code> is the vaadin implementation
 * of the {@link UBlock} specifications.
 */
@SuppressWarnings("serial")
public class DBlock extends Block implements UBlock {
  
  //------------------------------------------------
  // CONSTRUCTOR
  //------------------------------------------------

  /**
   * Creates a new <code>DBlock</code> instance.
   * @param parent The parent form.
   * @param model The block model.
   */
  public DBlock(DForm parent, VBlock model) {
    super(model.isDroppable());
    this.model = model;
    this.maxRowPos = model.getMaxRowPos();
    this.maxColumnPos = model.getMaxColumnPos();
    this.displayedFields = model.getDisplayedFields();
    this.formView = parent;
    setBorder(model.getBorder(), model.getTitle());
    model.addBlockListener(this);
    setBufferSize(model.getBufferSize());
    setDisplaySize(model.getDisplaySize());
    setSortedRecords(model.getSortedRecords());
    setNoMove(model.noMove());
    setNoChart(model.noChart());
    
    if (model.isMulti()) {
      sortedRecToDisplay   = new int[model.getBufferSize()];
      displayToSortedRec   = new int[model.getDisplaySize()];
    } else {
      sortedRecToDisplay   = new int[1];
      displayToSortedRec   = new int[1];
    }

    rebuildCachedInfos();
    createFields();
    
    if (model.isDroppable()) {
      setDropHandler(new DBlockDropHandler(model));
      setDragStartMode(DragStartMode.HTML5);
    }
    
    // fire record info change event
    // this is needed to notify view side with the record
    // info changes done when the block listener is not yet
    // installed.
    for (int i = 0; i < model.getBufferSize(); i++) {
      if (model.getRecordInfoAt(i) != 0) {
        fireRecordInfoChanged(i, model.getRecordInfoAt(i));
      }
    }
  }
  
  //------------------------------------------------
  // UTILS
  //------------------------------------------------
	  
  /**
   * Creates block fields
   */
  protected void createFields() {
    VField[]    fields = model.getFields();
    int         index = 0;

    columnViews = new VFieldUI[fields.length];
    for (int i = 0; i < fields.length; i++) {
      columnViews[i] = createFieldDisplays(index, fields[i]);
      if (columnViews[i] != null) {
        index += 1;
      }
    }
  }

  /**
   * Goto the next record
   */
  public void gotoNextRecord() throws VException {
    getModel().gotoNextRecord();
  }

  /**
   * Goto the previous record
   */
  public void gotoPrevRecord() throws VException {
    getModel().gotoPrevRecord();
  }
	  
  /**
   * Rebuilds cached information
   */
  private void rebuildCachedInfos() {
    int		cnt = 0;
    int		i = 0;

    // sortedRecToDisplay
    for (; i < sortedToprec; i++) {
      sortedRecToDisplay[i] = -1;
    }

    for (; cnt < model.getDisplaySize() && i < model.getBufferSize(); i++) {
      // sortedRecToDisplay: view pos not real record number
      sortedRecToDisplay[i] = model.isSortedRecordDeleted(i) ? -1 : cnt++;
    }

    for (; i < model.getBufferSize(); i++) {
      sortedRecToDisplay[i] = -1;
    }

    // displayToSortedRec
    cnt = sortedToprec;
    for (i = 0; i < model.getDisplaySize(); i++) {
      while (cnt < model.getBufferSize() && model.isSortedRecordDeleted(cnt)) {
	cnt++;
      }
      // the last one can be deleted too
      if (cnt < model.getBufferSize()) {
	displayToSortedRec[i] = cnt++;
      }
    }
  }

  /**
   * Creates the field display representation.
   * @param model The field model.
   * @return The row controller.
   */
  private VFieldUI createFieldDisplays(int index, VField model) {
    if (!model.isInternal() /*hidden field */) {
      VFieldUI  	ui;

      ui = createFieldDisplay(index, model);
      return ui;
    } else {
      return null;
    }
  }
  
  protected VFieldUI createFieldDisplay(int index, VField model) {
    return new DFieldUI(this, model, index);
  }

  /**
   * Refreshes the block on screen.
   *
   * Arranges displayed lines to make sure that the current record is visible.
   * Redisplays only if forced or if the current record is off-screen.
   * If there is no current record, the first valid record is used
   */
  protected void refresh(boolean force) {
    boolean		redisplay = false;
    int			recno; // row in view

    if (!model.isMulti()) {
      return;
    }

    if (model.getActiveRecord() != -1) {
      recno = model.getSortedPosition(model.getActiveRecord());
    } else {
      rebuildCachedInfos();
      for (int i = 0; i < columnViews.length; i++) {
        if (columnViews[i] != null) {
          columnViews[i].scrollTo(sortedToprec);
        }
      }

      return;
    }

    if (recno < sortedToprec) {
      // record to be displayed is above screen => redisplay
      sortedToprec = recno;

      // scroll some more, if there are some (non deleted) records
      int     i = recno -1;
      int     scrollMore =  model.getDisplaySize() / 4;
	      
      while (scrollMore > 0 && i > 0) {
        // is there a non deleted record to see?
        if (! model.isSortedRecordDeleted(i)) {
          sortedToprec -= 1;
          scrollMore--;
        }
        i--;
      }

      redisplay = true;
    } else {
      int	displine = 0;

      for (int i = sortedToprec; i < recno; i += 1) {
	if (!model.isSortedRecordDeleted(i)) {
	  displine += 1;
	}
      }				

      if (displine < model.getDisplaySize()) {
	// record should be visible => redisplay iff requested
	redisplay = force;// do nothing
      } else {
	// scroll upwards until record is visible => redisplay
	do {
	  if (!model.isSortedRecordDeleted(sortedToprec)) {
	    displine -= 1;
	  }
	  sortedToprec += 1;
	} while (displine >= model.getDisplaySize());

        // scroll some more, if there are some (non deleted) records
        int     i = recno +1;
        int     scrollMore =  model.getDisplaySize() / 4;
	        
        while (scrollMore > 0 && i < model.getBufferSize()) {
            // is there a non deleted record to see?
          if (! model.isSortedRecordDeleted(i)) {
            sortedToprec += 1;
            scrollMore--;
          }
          i++;
        }

	redisplay = true;
      }
    }

    rebuildCachedInfos();

    if (redisplay) {
      for (int i = 0; i < columnViews.length; i++) {
        if (columnViews[i] != null) {
          columnViews[i].scrollTo(sortedToprec);
        }
      }
    }
    // sends the model active record to client side.
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        fireActiveRecordChanged(model.getActiveRecord());
      }
    });
  }
  
  @Override
  protected void fireValueChanged(final int col, final int rec, final String value) {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        DBlock.super.fireValueChanged(col, rec, value);
      }
    });
  }
  
  @Override
  protected void fireColorChanged(final int col,
                                  final int rec,
                                  final String foreground,
                                  final String background)
  {
    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        DBlock.super.fireColorChanged(col, rec, foreground, background);
      }
    });
  }
  
  @Override
  protected void fireRecordInfoChanged(final int rec, final int info) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        DBlock.super.fireRecordInfoChanged(rec, info);
      }
    });
  }

  /**
   * Performs a scroll action.
   * @exception	VException an exception may be raised record.leave()
   */
  public void setScrollPos(int val) throws VException {
    // Can not be called in event dispatch thread
    // Scrollbar timer is not stop if you click on one of the two buttons

    assert val < model.getBufferSize(); //getRecordSize

    if (sortedToprec != val) {
      int               recno = 0;  //temp sortedToprec

      while (val > 0) {
	if (!model.isSortedRecordDeleted(recno)) {
	  val--;
	}
	recno++;
      }

      if (model.getActiveField() != null) {
	int             lastVisibleRec = recno;
	int             nbDisplay = model.getDisplaySize() - 1;
	int             activeRecord = model.getActiveRecord();
	boolean         inside = false; // is active record still in the shown rows

	while (nbDisplay > 0) {
	  if (!model.isSortedRecordDeleted(lastVisibleRec)) {
	    nbDisplay--;
	  }
	  if (activeRecord == model.getDataPosition(lastVisibleRec)) {
	    // active record is still in the shown rows
	    inside = true;
	  }
	  lastVisibleRec += 1;
	}

	sortedToprec = recno;
	if (inside) {
	  if (model.getActiveField() != null) {
	    model.getActiveField().updateText();
	  }
	  blockChanged();
	} else {
	  int		nextRec;

	  if (model.getSortedPosition(model.getActiveRecord()) < recno) {
	    nextRec = model.getDataPosition(recno);
	  } else {
	    nextRec = model.getDataPosition(lastVisibleRec);
	  }

	  if (model.noMove() || !model.isRecordAccessible(nextRec)) {
	    throw new VExecFailedException();
	  }
	  model.changeActiveRecord(nextRec);
	}
      } else {
	if (model.noMove() || model.isRecordDeleted(model.getDataPosition(recno))) {
	  // || !model.isRecordAccessible(model.getDataPosition(recno))) {
	  throw new VExecFailedException();
	}
	sortedToprec = recno;
	blockChanged();
	if (model != model.getForm().getActiveBlock()) {  // NICHT AKTUELLER BLOCK -> ZURUECK AUF -1
	  model.setActiveRecord(-1);
	}
      }
    }
  }

  /**
   * Clears the block content.
   */
  public void clear() {
    sortedToprec = 0;
    refresh(true);
  }
	  
  /**
   * Sets the block border.
   * @param style The border style.
   * @param title The block title.
   */
  private void setBorder(int style, String title) {
    if (style != VConstants.BRD_NONE) {
      if (title != null) {
	setCaption(title);
      }
    }
  }
  
  //---------------------------------------------------
  // UBLOCK IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public VBlock getModel() {
    return model;
  }

  @Override
  public UForm getFormView() {  
    return formView;
  }
  
  @Override
  public int getDisplayLine(int recno) {
    if (recno < 0) {
      return -1;
    }
    int         pos = model.getSortedPosition(recno);

    if (pos < 0) {
      return -1;
    }

    return sortedRecToDisplay[pos];
  }

  @Override
  public int getDisplayLine() {
    return getDisplayLine(model.getActiveRecord());
  }

  @Override
  public int getRecordFromDisplayLine(int line) {
    return model.getDataPosition(displayToSortedRec[line]);
  }

  @Override
  public void add(UComponent comp, KopiAlignment constraints) {
    addComponent((Component)comp,
	         constraints.x,
	         constraints.y,
	         constraints.width,
	         constraints.height,
	         constraints.alignRight,
	         constraints.useAll);
  }

  @Override
  public int getColumnPos(int x) {
    return 0;
  }

  @Override
  public boolean inDetailMode() {
    return false;
  }

  @Override
  public BlockLayout createLayout() {
    SimpleBlockLayout		layout;
    
    // label + field => 2 + lines
    layout = new SimpleBlockLayout(2 * maxColumnPos, maxRowPos);
    if (model.getAlignment() != null) {
      layout.setBlockAlignment((Component)formView.getBlockView(model.getAlignment().getBlock()),
	                       model.getAlignment().getTartgets(),
	                       model.getAlignment().isChart());
    }
    
    return layout;
  }
	  
  //---------------------------------------------------
  // BLOCKLISTENER IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public void blockClosed() {}

  @Override
  public void blockChanged() {
    refresh(true);
  }

  @Override
  public void blockCleared() {
    clear();
  }

  @Override
  public void blockAccessChanged(VBlock block, boolean newAccess) {}

  @Override
  public void blockViewModeLeaved(VBlock block, VField activeField) {}

  @Override
  public void blockViewModeEntered(VBlock block, VField activeField) {}

  @Override
  public void validRecordNumberChanged() {}
  
  @Override
  public void recordInfoChanged(int rec, int info) {}
  
  @Override
  public void orderChanged() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        DBlock.super.fireOrderChanged(getModel().getSortedRecords());
      }
    });
    refresh(true);
  }
  
  @Override
  public void filterHidden() {}

  @Override
  public void filterShown() {}

  @Override
  public UBlock getCurrentDisplay() {
    return this;
  }

  //---------------------------------------------------
  // DATA MEMBER
  //---------------------------------------------------

  protected   final DForm		formView;
  protected final VBlock		model;
  protected   VFieldUI[]            	columnViews;
  // protected KopiLayout  		layout;

  protected final int			maxRowPos;
  protected final int			maxColumnPos;
  protected final int			displayedFields;
	  
  // cached infos
  protected  int		        sortedToprec;		// first record displayed
  private  int[]			sortedRecToDisplay;
  private  int[]			displayToSortedRec;
}
