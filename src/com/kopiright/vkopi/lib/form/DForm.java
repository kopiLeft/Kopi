/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.util.Message;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.*;
import com.kopiright.xkopi.lib.type.NotNullDate;
import com.kopiright.xkopi.lib.type.NotNullTime;

/**
 * This is the display class of a form.
 */
public class DForm extends DWindow implements DPositionPanelListener, FormListener {
  /**
   * Constructor
   */
  public DForm(VForm model) {
    super(model);
    SwingThreadHandler.verifyRunsInEventThread("DForm <init>");
    model.addFormListener(this);

    JPanel      contentPanel = getContentPanel();

    contentPanel.setLayout(new BorderLayout());

    blockPanel = new DPage[getPageCount() == 0 ? 1 : getPageCount()];
    for (int i = 0; i < blockPanel.length; i++) {
      if (getPageCount() != 0) {
	blockPanel[i] = new DPage(getPageTitle(i).endsWith("<CENTER>"));
      } else {
	blockPanel[i] = new DPage(false);
      }
    }

    if (getPageCount() == 0) {
      JScrollPane     pane = new JScrollPane();

      pane.setViewportView(blockPanel[0]);
      pane.setBorder(null);
      contentPanel.add(pane, BorderLayout.CENTER);
    } else {
      tabbedBlockPanel = new JTabbedPane();

      for (int i = 0; i < blockPanel.length; i++) {
	JScrollPane     pane = new JScrollPane();
	JPanel		inner = new JPanel();
        final String    pageTitle = getPageTitle(i); 

        inner.setFocusCycleRoot(true);
        inner.setFocusable(false);// !!! laurent

	inner.setLayout(new BorderLayout());
	inner.add(blockPanel[i], BorderLayout.CENTER);

        pane.setViewportView(inner);
        pane.setBorder(null);

	tabbedBlockPanel.addTab(pageTitle.endsWith("<CENTER>") ? pageTitle.substring(0,  pageTitle.length() - 8) : pageTitle, pane);//blockPanel[i]);
        tabbedBlockPanel.setEnabledAt(i, false);
      }

      // set the model after creating the tabs
      tabbedBlockPanel.setModel(new DefaultSingleSelectionModel() {
	public void setSelectedIndex(final int index) {
	  if (getCurrentPage() != index) {
	    performBasicAction(new KopiAction("setSelectedIndex") {
	      public void execute() throws VException {
		getVForm().gotoPage(index);
		superSetSelectedIndex(index);
	      }
	    });
	  } else {
	    super.setSelectedIndex(index);
	  }
	}
	private void superSetSelectedIndex(int index) {
	  super.setSelectedIndex(index);
	}
      });

      tabbedBlockPanel.setRequestFocusEnabled(false);
      contentPanel.add(tabbedBlockPanel, BorderLayout.CENTER);
    }

    DPositionPanel      blockInfo;

    blockInfo = new DPositionPanel(this);
    setStatePanel(blockInfo);
    blockRecordHandler = new BlockRecordHandler(blockInfo);

    int                 blockcount;

    blockListener = new BlockAccessHandler();
    getModel().setDisplay(this);

    blockcount = getVForm().getBlockCount();
    blockViews = new DBlock[blockcount];

    for (int i = 0; i < blockcount; i++) {
      VBlock      blockModel;
      DBlock      blockView;

      blockModel = getVForm().getBlock(i);
      blockView = createViewForBlock(blockModel);
      blockViews[i] = blockView;

      addBlock(blockView, blockModel.getPageNumber());

      blockModel.addBlockListener(blockListener);
    }
    getModel().enableCommands();
  }

  protected DBlock createViewForBlock(VBlock blockModel) {
    final boolean               isChart = blockModel.isMulti() && blockModel.isChart();
    final VBlock.UIProperties   uiProperties = blockModel.getUIProperties();

    DBlock                      blockView;

    if (blockModel.isMulti()) {
      if (blockModel.noChart() && blockModel.noDetail()) {
        // !! no display; warn ?
        throw new InconsistencyException("Block " + blockModel.getName() + " is \"NO DEATIL\" and \"NO CHART\" at the same time");
      } else if (blockModel.noChart()) {
        blockView = new DBlock(this,
                               blockModel,
                               uiProperties.border,
                               uiProperties.title,
                               uiProperties.alignment,
                               uiProperties.maxRowPos,
                               uiProperties.maxColumnPos,
                               uiProperties.displayedFields);
      } else if (blockModel.noDetail()) {
        blockView = new DChartBlock(this,
                                    blockModel,
                                    uiProperties.border,
                                    uiProperties.title,
                                    uiProperties.alignment,
                                    uiProperties.maxRowPos,
                                    uiProperties.maxColumnPos,
                                    uiProperties.displayedFields);
      } else {
        blockView = new DMultiBlock(this,
                                    blockModel,
                                    uiProperties.border,
                                    uiProperties.title,
                                    uiProperties.alignment,
                                    uiProperties.maxRowPos,
                                    uiProperties.maxColumnPos,
                                    uiProperties.displayedFields);
      }
    } else {
      blockView = new DBlock(this,
                             blockModel,
                             uiProperties.border,
                             uiProperties.title,
                             uiProperties.alignment,
                             uiProperties.maxRowPos,
                             uiProperties.maxColumnPos,
                             uiProperties.displayedFields);
    }    
    return blockView;
  }


  /**
   *
   */
  public void addBlock(DBlock block, int page) {
    if (!block.getModel().isInternal()) {
      if (block.getModel().isFollow()) {
	blockPanel[page].addFollowBlock(block);
      } else {
	blockPanel[page].addBlock(block);
      }
    }
  }

  protected void createEditMenu() {
    JMenu		edit;

    // there is always a file menu
    edit = new JMenu(Message.getMessage("menu-file"));
    getDMenuBar().add(edit);

    // and also an edit menu
    edit = new JMenu(Message.getMessage("menu-edit")) ;
    getDMenuBar().add(edit);

    undoAction = new DWindow.UndoAction();
    redoAction = new DWindow.RedoAction();

    // create the menu
    edit.add(undoAction);
    edit.add(redoAction);
    edit.addSeparator();
    //These actions come from the default editor kit.
    //We just get the ones we want and stick them in the menu.
    Action      action = getActionByName(DefaultEditorKit.cutAction);
    JMenuItem   item = new JMenuItem(Message.getMessage("item-cut"));

    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    action = getActionByName(DefaultEditorKit.copyAction);
    item = new JMenuItem(Message.getMessage("item-copy"));
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    action = getActionByName(DefaultEditorKit.pasteAction);
    item = new JMenuItem(Message.getMessage("item-paste"));
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    edit.addSeparator();

    action = getActionByName(DefaultEditorKit.selectAllAction);
    action.putValue(Action.NAME, Message.getMessage("item-select-all"));
    // prevents garbage collection
    // therefore commented out
    //    edit.add(action);

    edit.addSeparator();
  }

  /**
   * start a block and enter in the good field (rec)
   * @exception	com.kopiright.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  protected void run() throws VException {
    if (!SwingUtilities.isEventDispatchThread()) {
      System.err.println("ERROR: run() of DForm called outside the event-dispatching-thread");
    }
    getVForm().prepareForm();
    
    // initialize the access of the blocks
    int         blockcount;

    blockcount = getVForm().getBlockCount();
    for (int i = 0; i < blockcount; i++) {
      VBlock    blockModel;

      blockModel = getVForm().getBlock(i);
      blockModel.updateBlockAccess();
    }

    Window      window = Utils.getWindowAncestor(this);

    window.pack();
    // is the window to big or has to be moved in the left upper edge to 
    // be visible on the screen
    Rectangle   rectangle = Utils.calculateBounds(window, window.getLocation(), null);

    window.setBounds(rectangle);
    window.show();

    getVForm().executeAfterStart();
  }




  // ---------------------------------------------------------------------
  // NAVIGATION
  // ---------------------------------------------------------------------

  /**
   * Displays an error message.
   */
  public void reportError(VRuntimeException e) {
    Toolkit.getDefaultToolkit().beep();
    if (e.getCause() instanceof VFieldException && e.getMessage()  != null) {
      displayFieldError((VFieldException)e.getCause());
    } else {
      super.reportError(e);
    }
  }

  public void displayFieldError(VFieldException fe) {
    VField	field = fe.getField();


    field.displayFieldError(fe.getMessage());
  }
   /**
   *
   */
  public void gotoPage(int i) {
    setCurrentPage(i);
    if (tabbedBlockPanel != null) {
      tabbedBlockPanel.setSelectedIndex(i);
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE DPositionPanelListener
  // ----------------------------------------------------------------------

  /**
   * Requests to go to the next position.
   */
  public void gotoNextPosition() {
    performAsyncAction(new KopiAction("gotoNextPosition") {
        public void execute() throws VException {
          DForm.this.getVForm().getActiveBlock().gotoNextRecord();
        }
      });
  }
  
  /**
   * Requests to go to the previous position.
   */
  public void gotoPrevPosition() {
    performAsyncAction(new KopiAction("gotoPrevPosition") {
        public void execute() throws VException {
          DForm.this.getVForm().getActiveBlock().gotoPrevRecord();
        }
      });
  }

  /**
   * Returns the number of pages.
   */
  public int getPageCount() {
    return getVForm().getPages().length;
  }

  /**
   * Returns the title of the specified page.
   * @param	index		the index of the specified page
   */
  public String getPageTitle(int index) {
    return getVForm().getPages()[index];
  }

  /**
   * GET PAGE
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * SET CURRENT PAGE
   */
  public void setCurrentPage(int i) {
    currentPage = i;
  }

  // ----------------------------------------------------------------------
  // PRIVATE ACCESSORS
  // ----------------------------------------------------------------------

  private VForm getVForm() {
    return (VForm)getModel();
  }

  // ----------------------------------------------------------------------
  // FormListener
  // ----------------------------------------------------------------------

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

  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  public void setFieldSearchOperator(int op) {
    // nothing to do
  }

  public DBlock getBlockView(VBlock block) {
    VBlock[]    blocks = getVForm().getBlocks();
    
    for (int i=0; i < blocks.length; i++) {
      if (block == blocks[i]) {
        return blockViews[i];
      }
    }
    return null;
  }

  public void release() {
    int         blockcount = getVForm().getBlockCount();

    getVForm().removeFormListener(this);
    for (int i = 0; i < blockViews.length; i++) {
      getVForm().getBlock(i).removeBlockListener(blockListener);
      //!!!!      blockViews[i].release();
    }
    super.release();
  }


  // ----------------------------------------------------------------------
  // PRIVATE CLASSSES
  // ----------------------------------------------------------------------

  private static class BlockRecordHandler implements BlockRecordListener {
    public BlockRecordHandler(DPositionPanel blockInfo) {
      this.blockInfo = blockInfo;
    }

    public void blockRecordChanged(int current, int count) {
      blockInfo.setPosition(current, count);
    }

    private DPositionPanel	blockInfo;
  }

  private class BlockAccessHandler implements BlockListener {
    public void blockClosed() {}
    public void blockChanged() {}
    public void blockCleared() {}

    public void blockViewModeEntered(VBlock block, VField field) {}
    public void blockViewModeLeaved(VBlock block, VField field) {}

    public void blockAccessChanged(VBlock block, boolean newAccess) {
      if (tabbedBlockPanel == null) {
        // nothing to do
        return; 
      }
      //enable/disable tab of tabbedPane (pages)
      final int         pageNumber = block.getPageNumber();
      final VBlock[]    blocks =getVForm().getBlocks();

      if (newAccess) {
        if (!tabbedBlockPanel.isEnabledAt(pageNumber)) {
          // enable page
          tabbedBlockPanel.setEnabledAt(pageNumber, true);
        }
      } else {
        if (tabbedBlockPanel.isEnabledAt(pageNumber)) {
          // tab is visible (another visible block there?)
          for (int i = 0; i < blocks.length; i++) {
            if (pageNumber == blocks[i].getPageNumber()
                && blocks[i].isAccessible()) {
              return;
            }
          }
          // no accessible block on the page ->
          // disable page
          tabbedBlockPanel.setEnabledAt(pageNumber, false);
        }
      }
    }

    public void validRecordNumberChanged() {}
    public void orderChanged() {}
    public Component getCurrentDisplay() {
      // use another listener:
      return null; 
    }
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  public PrintJob printForm() throws VException {
    com.lowagie.text.Rectangle  pageSize = PageSize.A4.rotate();
    Document                    document = new Document(pageSize, 50, 50, 50, 50);
    File                        file;
    
    try {      
      file = Utils.getTempFile("kopi", "srn");

      PdfWriter         writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            
      document.open();

      Frame             frame = Utils.getFrameAncestor(this);
      Dimension         dim = frame.getSize(null);
      PdfContentByte    cb = writer.getDirectContent();
      PdfTemplate       tp = cb.createTemplate(dim.width, dim.height);
      Graphics2D        g2 = tp.createGraphics(dim.width, dim.height);

      frame.invalidate();
      frame.validate();
      frame.paint(g2);
      g2.dispose();


      PdfPTable       foot = new PdfPTable(2);
                
      foot.addCell(createCell(((VForm)getModel()).getName(), 7, Color.black, Color.white, Element.ALIGN_LEFT, false));
      foot.addCell(createCell(NotNullDate.now().format("dd.MM.yyyy") + " "+ NotNullTime.now().format("HH:mm"), 
                              7, Color.black, Color.white, Element.ALIGN_RIGHT, false));
      foot.setTotalWidth(pageSize.width() - document.leftMargin() - document.rightMargin());
      foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin()+foot.getTotalHeight(), cb);


      float             scale = Math.min((pageSize.width()-100)/dim.width, (pageSize.height()-100)/dim.height);

      cb.addTemplate(tp, scale, 0, 0, scale, 50, (pageSize.height()-(scale*dim.height))/2);
    } catch(DocumentException de) {
      throw new VExecFailedException(de);
    } catch(IOException ioe) {
      throw new VExecFailedException(ioe);      
    }
        
    document.close();

    PrintJob    printJob = new PrintJob(file, true);

    printJob.setDataType(PrintJob.DAT_PDF);
    printJob.setNumberOfPages(1);
    return printJob;
  }

  private PdfPCell createCell(String text, double size, Color textColor, Color background, int alignment, boolean border) {
    PdfPCell    cell;
    Font        font = FontFactory.getFont(FontFactory.HELVETICA, (float) size, 0 , textColor);

    cell = new PdfPCell(new Paragraph(new Chunk(text, font)));
    cell.setBorderWidth(1);
    cell.setPaddingLeft(0);
    cell.setPaddingRight(0);
    cell.setNoWrap(true);
    cell.setUseDescender(true);

    cell.setVerticalAlignment(Element.ALIGN_TOP);
    cell.setHorizontalAlignment(alignment);

    cell.setBackgroundColor(background);
    if (!border) {
      cell.setBorder(0);
    }
    return cell;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /*package*/ static final Insets	emptyInsets = new Insets(0, 0, 0, 0);

  private BlockRecordHandler    blockRecordHandler;
  private BlockListener         blockListener;

  private int			currentPage = -1;
  private DPage[]		blockPanel;
  private JTabbedPane		tabbedBlockPanel;
  private DBlock[]              blockViews;

  // 
  /*package*/ final VCommand cmdAutofill = new VFieldCommand(this, VForm.CMD_AUTOFILL);
  /*package*/ final VCommand cmdEditItem_S = new VFieldCommand(this, VForm.CMD_EDITITEM_S);
  /*package*/ final VCommand cmdEditItem = new VFieldCommand(this, VForm.CMD_EDITITEM);
  /*package*/ final VCommand cmdNewItem = new VFieldCommand(this, VForm.CMD_NEWITEM);
}
