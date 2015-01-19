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
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DPositionPanel;
import com.kopiright.vkopi.lib.ui.vaadin.visual.DWindow;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * The <code>DForm</code> is the vaadin implementation of
 * the {@link UForm} specifications.
 */
@SuppressWarnings("serial")
public class DForm extends DWindow implements UForm {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DForm</code> instance.
   * @param model The form model.
   */
  public DForm(VForm model) {
    super(model);
    setSizeFull();
    setStyleName(KopiTheme.FORM_STYLE);
    model.addFormListener(this);
    blockPanel = new DPage[getPageCount() == 0 ? 1 : getPageCount()];
    
    for (int i = 0; i < blockPanel.length; i++) {
      if (getPageCount() != 0) {
	if (getPageTitle(i).endsWith("<CENTER>")) {
	  blockPanel[i] = new HorizontalPage();
	} else {
	  blockPanel[i] = new VerticalPage();
	}
      } else {
	blockPanel[i] = new VerticalPage();
      }
    }
    
    if (getPageCount() == 0) {
      setContent(blockPanel[0]);
    } else {
      tabbedBlockPanel = new TabSheet();
      
      tabbedBlockPanel.addStyleName(KopiTheme.TABSHEET_BORDERLESS);
      tabbedBlockPanel.addStyleName(KopiTheme.TABBED_BLOCK_PANEL_STYLE);
      for (int i = 0; i < blockPanel.length; i++) {
	final String    pageTitle = getPageTitle(i).endsWith("<CENTER>") ? getPageTitle(i).substring(0,  getPageTitle(i).length() - 8) : getPageTitle(i);

        VerticalLayout tab=new VerticalLayout();
        tab.setWidth("100%");
        tab.addComponent(blockPanel[i]); 

        tabbedBlockPanel.addTab(tab, pageTitle);
        tabbedBlockPanel.getTab(i).setEnabled(false);
        tabbedBlockPanel.getTab(i).setClosable(false);
      }
      
      tabbedBlockPanel.addSelectedTabChangeListener(new SelectedTabChangeListener() {
        
        public void selectedTabChange(SelectedTabChangeEvent event) {   
          final TabSheet 	tabsheet = (TabSheet) event.getTabSheet();
          int             	index = 0;
                   
          if (tabsheet.getSelectedTab() != null) {
            index = tabsheet.getTabIndex();
          }
          final int	selectedIndex = index;
          if (getCurrentPage() == index) {
            performBasicAction(new KopiAction("setSelectedIndex") {
              public void execute() throws VException {  
          	try {
		  getVForm().gotoPage(selectedIndex);
          	} catch (VException ve){
          	  tabsheet.setSelectedTab(getCurrentPage());
          	  if (ve.getMessage() != null) {
	            displayError(ve.getMessage());
	          }
          	}
              }
            });
          } 
        }
      });
      
      tabbedBlockPanel.setSizeFull();
      setContent(tabbedBlockPanel);
    }
    
    DPositionPanel      blockInfo;

    blockInfo = new DPositionPanel(this);
    setStatePanel(blockInfo);
    
    blockRecordHandler = new BlockRecordHandler(blockInfo);
    
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
    
    Environment.addDefaultFormKey(this);
    getModel().enableCommands();
  }
  
  //---------------------------------------------------
  // UTILS
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
    
    // layout the block
    blockView.layoutContainer();
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
    if (tabbedBlockPanel != null) {
      BackgroundThreadHandler.start(new Runnable() {
	
	@Override
	public void run() {
          tabbedBlockPanel.setSelectedTab(i);
        }
      });
    }
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
      if (block.getModel().isFollow()) {
	((DPage) blockPanel[page]).addFollowBlock(block);
      } else {
	((DPage) blockPanel[page]).addBlock(block);
      }
    }
  }
  
  /**
   * Returns the {@link VForm} model.
   * @return The {@link VForm} model.
   */
  public VForm getVForm() {
    return (VForm)super.getModel();
  }
  
  //---------------------------------------------------
  // WINDOW IMPLEMENTATION
  //---------------------------------------------------

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
  
  //---------------------------------------------------
  // POSITIONLISTENER IMPLEMENTATION
  //---------------------------------------------------
  
  /**
   * Requests to go to the next position.
   */
  @Override
  public void gotoNextPosition() {
    performAsyncAction(new KopiAction("gotoNextPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoNextRecord();
      }
    });
  }
  
  /**
   * Requests to go to the previous position.
   */
  @Override
  public void gotoPrevPosition() {
    performAsyncAction(new KopiAction("gotoPrevPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoPrevRecord();
      }
    });
  }

  /**
   * Requests to go to the last position.
   */
  @Override
  public void gotoLastPosition() {
    performAsyncAction(new KopiAction("gotoLastPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoLastRecord();
      }
    });
  }
  
  /**
   * Requests to go to the first position.
   */
  @Override
  public void gotoFirstPosition() {
    performAsyncAction(new KopiAction("gotoFirstPosition") {
      
      @Override
      public void execute() throws VException {
	DForm.this.getVForm().getActiveBlock().gotoFirstRecord();
      }
    });
  }

  /**
   * Requests to go to the specified position.
   */
  @Override
  public void gotoPosition(final int posno) {
    performAsyncAction(new KopiAction("gotoPosition") {
      
      @Override
      public void execute() throws VException, ArrayIndexOutOfBoundsException {
	DForm.this.getVForm().getActiveBlock().gotoRecord(posno - 1);
      }
    });
  }

  //---------------------------------------------------
  // FORMLISTENER IMPLEMENTATION
  //---------------------------------------------------
  
  @Override
  public void currentBlockChanged(VBlock oldBlock, VBlock newBlock) {
    if (oldBlock != null) {
      oldBlock.removeBlockRecordListener(blockRecordHandler);
    }
    
    if (newBlock != null) {
      newBlock.addBlockRecordListener(blockRecordHandler);
      blockRecordHandler.blockRecordChanged(newBlock.getSortedPosition(newBlock.getRecord()), newBlock.getRecordCount());
//    }
//
//    if (newBlock != null) {
      if (newBlock.getPageNumber() != getCurrentPage()) {
	gotoPage(newBlock.getPageNumber());
      }
    }
  }

  public void setFieldSearchOperator(int op) {
    // nothing to do
  }
  
  //---------------------------------------------------
  // FORM IMPLEMENTATION
  //---------------------------------------------------
  
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
    public void blockAccessChanged(VBlock block, boolean newAccess) {
      if (tabbedBlockPanel == null) {
	// nothing to do
	return; 
      }
      
      //enable/disable tab of tabbedPane (pages)
      final int         pageNumber = block.getPageNumber();
      final VBlock[]    blocks = getVForm().getBlocks();

      if (newAccess) {
        if (!tabbedBlockPanel.getTab(pageNumber).isEnabled()) {
          // enable page
          BackgroundThreadHandler.start(new Runnable() {
            
            @Override
            public void run() {
              tabbedBlockPanel.getTab(pageNumber).setEnabled(true);
            }
          });
        }
      } else {
        if (tabbedBlockPanel.getTab(pageNumber).isEnabled()) {
          // tab is visible (another visible block there?)
          for (int i = 0; i < blocks.length; i++) {
            if (pageNumber == blocks[i].getPageNumber()
                && blocks[i].isAccessible()) {
              return;
            }
          }
          // no accessible block on the page ->
          // disable page
          BackgroundThreadHandler.start(new Runnable() {
            
            @Override
            public void run() {
              tabbedBlockPanel.getTab(pageNumber).setEnabled(false);
            }
          });
        }
      }
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
  private static class BlockRecordHandler implements BlockRecordListener {

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    public BlockRecordHandler(DPositionPanel blockInfo) {
      this.blockInfo = blockInfo;
    }

    //---------------------------------------
    // IMPLEMENTATION
    //---------------------------------------
    
    @Override
    public void blockRecordChanged(final int current, final int count) {
      BackgroundThreadHandler.start(new Runnable() {
	
	@Override
	public void run() {
          blockInfo.setPosition(current, count);
	}
      });
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    private DPositionPanel		blockInfo;
  }
  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  
  private int				currentPage = -1;
  private DPage[]                       blockPanel;
  private TabSheet		        tabbedBlockPanel;
  private BlockListener         	blockListener;
  private DBlock[]              	blockViews;
  private BlockRecordHandler    	blockRecordHandler;
}