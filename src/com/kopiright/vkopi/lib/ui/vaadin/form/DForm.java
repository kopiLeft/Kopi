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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import java.io.File;

import org.kopi.vaadin.addons.Form;
import org.kopi.vaadin.addons.FormListener;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.form.BlockListener;
import com.kopiright.vkopi.lib.form.BlockRecordListener;
import com.kopiright.vkopi.lib.form.UBlock;
import com.kopiright.vkopi.lib.form.UForm;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFieldException;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;

/**
 * The <code>DForm</code> is the vaadin implementation of
 * the {@link UForm} specifications.
 */
@SuppressWarnings("serial")
public class DForm extends DWindow implements UForm, FormListener {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DForm</code> instance.
   * @param model The form model.
   */
  public DForm(VForm model) {
    super(model);
    content = new Form(getPageCount(), model.getPages());
    content.setLocale(getApplication().getDefaultLocale().toString());
    setSizeFull();
    content.setSizeFull();
    model.addFormListener(this);
    content.addFormListener(this);
    blockRecordHandler = new BlockRecordHandler();
    blockListener = new BlockAccessHandler();
    getModel().setDisplay(this);

    int blockcount = getVForm().getBlockCount();
    blockViews = new DBlock[blockcount];

    for (int i = 0; i < blockcount; i++) {
      VBlock      blockModel;

      blockModel = getVForm().getBlock(i);
      if (!blockModel.isInternal()) {
	DBlock      blockView;

        blockView = createViewForBlock(blockModel);
        blockViews[i] = blockView;
	
        addBlock(blockView, blockModel.getPageNumber());
      }

      blockModel.addBlockListener(blockListener);
    }
    setContent(content);
    getModel().enableCommands();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Creates the block view for a given block model
   * @param blockModel The block model.
   */
  protected DBlock createViewForBlock(VBlock blockModel) {
    DBlock                      blockView;
    
    if (! blockModel.isMulti()) {
      blockView = new DBlock(this, blockModel); 
    } else {
      if (blockModel.noChart() && blockModel.noDetail()) {
        throw new InconsistencyException("Block " + blockModel.getName() + " is \"NO DEATIL\" and \"NO CHART\" at the same time");
      }
      if (blockModel.noChart()) {
        blockView = new DBlock(this, blockModel);
      } else if (blockModel.noDetail()) {
        blockView = new DChartBlock(this, blockModel);
      } else {
        blockView = new DMultiBlock(this, blockModel);
      }
    }
    
    return blockView;
  }
  
  /**
   * Returns the number of pages.
   * @return The number of pages.
   */
  public int getPageCount() {
    return getVForm().getPages().length;
  }

  /**
   * Returns the title of the specified page.
   * @return The title of the specified page.
   */
  public String getPageTitle(int index) {
    return getVForm().getPages()[index];
  }

  /**
   * Returns the current page index.
   * @return The current page index.
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * Sets the current page index.
   * @param i The current page.
   */
  public void setCurrentPage(int i) {
    currentPage = i;
  }

  @Override
  public void reportError(VRuntimeException e) {
    if (e.getCause() instanceof VFieldException && e.getMessage()  != null) {
      displayFieldError((VFieldException)e.getCause());
    } else {
      super.reportError(e);
    }
  }

  /**
   * Displays a field error caused by the given exception.
   * @param fe The error cause.
   */
  public void displayFieldError(VFieldException fe) {
    VField	field = fe.getField();

    field.displayFieldError(fe.getMessage());
  }
  
  /**
   * Goes to the page with index = i
   * @param i The page index.
   */
  public void gotoPage(final int i) {
    setCurrentPage(i);
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	content.gotoPage(i);
      }
    });
  }

  /**
   * Releases the form.
   */
  public void release() {
    getVForm().removeFormListener(this);
    
    for (int i = 0; i < blockViews.length; i++) {
      getVForm().getBlock(i).removeBlockListener(blockListener);
    }

    super.release();
  }

 /**
  * Adds a block view into a given page.
  * @param block The block view.
  * @param page The page index.
  */
  private void addBlock(DBlock block, int page) {
    if (!block.getModel().isInternal()) {
      content.addBlock(block,
	               page,
	               block.getModel().isFollow(),
	               block.getModel().noDetail());
    }
  }
  
  /**
   * Returns the {@link VForm} model.
   * @return The {@link VForm} model.
   */
  public VForm getVForm() {
    return (VForm)super.getModel();
  }

  @SuppressWarnings("deprecation")
  @Override
  public void run() throws VException {
    getVForm().prepareForm();

    // initialize the access of the blocks
    int         blockcount;

    blockcount = getVForm().getBlockCount();
    for (int i = 0; i < blockcount; i++) {
      VBlock    blockModel;

      blockModel = getVForm().getBlock(i);
      blockModel.updateBlockAccess();
    }
    
    getVForm().executeAfterStart();
  }
  
  @Override
  public void onPageSelection(final int page) {
    if (getCurrentPage() != page) {
      performAsyncAction(new KopiAction("setSelectedIndex") {

	@Override
	public void execute() throws VException {  
	  getVForm().gotoPage(page);
	}
      });
    }
  }
  
  @Override
  public void gotoNextPosition() {
    performAsyncAction(new KopiAction("gotoNextPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoNextRecord();
      }
    });
  }
  
  @Override
  public void gotoPrevPosition() {
    performAsyncAction(new KopiAction("gotoPrevPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoPrevRecord();
      }
    });
  }

  @Override
  public void gotoLastPosition() {
    performAsyncAction(new KopiAction("gotoLastPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoLastRecord();
      }
    });
  }
  
  @Override
  public void gotoFirstPosition() {
    performAsyncAction(new KopiAction("gotoFirstPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoFirstRecord();
      }
    });
  }

  @Override
  public void gotoPosition(final int posno) {
    performAsyncAction(new KopiAction("gotoPosition") {
      
      @Override
      public void execute() throws VException, ArrayIndexOutOfBoundsException {
	DForm.this.getVForm().getActiveBlock().gotoRecord(posno - 1);
      }
    });
  }
  
  @Override
  public void currentBlockChanged(VBlock oldBlock, VBlock newBlock) {
    if (oldBlock != null) {
      oldBlock.removeBlockRecordListener(blockRecordHandler);
    }

    if (newBlock != null) {
      newBlock.addBlockRecordListener(blockRecordHandler);
      blockRecordHandler.blockRecordChanged(newBlock.getSortedPosition(newBlock.getRecord()), newBlock.getRecordCount());
    }

    if (newBlock != null) {
      if (newBlock.getPageNumber() != getCurrentPage()) {
	gotoPage(newBlock.getPageNumber());
      }
    }
  }

  public void setFieldSearchOperator(int op) {
    // nothing to do
  }
  
  @Override
  public UBlock getBlockView(VBlock block) {
    VBlock[]    blocks = getVForm().getBlocks();

    for (int i = 0; i < blocks.length; i++) {
      if (block == blocks[i]) {
	return blockViews[i];
      }
    }
    
    return null;
  }

  @Override
  public PrintJob printForm() throws VException {
    // TODO
    return null;
  }

  @Override
  public void printSnapshot() {
    // TODO
  }

  @Override
  public Throwable getRuntimeDebugInfo() {
    return runtimeDebugInfo;
  }
  
  @Override
  public void launchDocumentPreview(String file) throws VException { 
    fileProduced(new File(file));
  }
  
  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  
  /**
   * The <code>BlockAccessHandler</code> is the {@link DForm}
   * implementation of the {@link BlockListener}
   */
  private class BlockAccessHandler implements BlockListener {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public void blockClosed() {}

    @Override
    public void blockChanged() {}

    @Override
    public void blockCleared() {}

    @Override
    public void blockAccessChanged(final VBlock block, final boolean newAccess) {
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          if (getPageCount() == 1) {
            return;
          }
          //enable/disable tab of pages
          final int         pageNumber = block.getPageNumber();
          final VBlock[]    blocks = getVForm().getBlocks();

          if (newAccess) {
            content.setEnabled(true, pageNumber);
          } else {
            // tab is visible (another visible block there?)
            for (int i = 0; i < blocks.length; i++) {
              if (pageNumber == blocks[i].getPageNumber() && blocks[i].isAccessible()) {
        	return;
              }
            }
            content.setEnabled(false, pageNumber);
          }
        }
      });
    }

    @Override
    public void blockViewModeLeaved(VBlock block, VField actviceField) {}

    @Override
    public void blockViewModeEntered(VBlock block, VField actviceField) {}

    @Override
    public void validRecordNumberChanged() {}

    @Override
    public void orderChanged() {}

    @Override
    public UBlock getCurrentDisplay() {
      // Please don't use
      return null;
    }
  }
  
  /**
   * The <code>BlockRecordHandler</code> is the {@link DForm}
   * implementation of the {@link BlockRecordListener}
   */
  private class BlockRecordHandler implements BlockRecordListener {

    //---------------------------------------
    // IMPLEMENTATION
    //---------------------------------------
    
    @Override
    public void blockRecordChanged(final int current, final int count) {
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          content.setPosition(current, count);
        }
      });
    }
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private int				currentPage = -1;
  private Form				content;
  private BlockListener         	blockListener;
  private DBlock[]              	blockViews;
  private BlockRecordHandler    	blockRecordHandler;
}