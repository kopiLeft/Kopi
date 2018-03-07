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

package org.kopi.vkopi.lib.ui.swing.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.Action;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.form.BlockListener;
import org.kopi.vkopi.lib.form.BlockRecordListener;
import org.kopi.vkopi.lib.form.UBlock;
import org.kopi.vkopi.lib.form.UForm;
import org.kopi.vkopi.lib.form.VBlock;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VField;
import org.kopi.vkopi.lib.form.VFieldException;
import org.kopi.vkopi.lib.form.VForm;
import org.kopi.vkopi.lib.ui.swing.visual.DPositionPanel;
import org.kopi.vkopi.lib.ui.swing.visual.DWindow;
import org.kopi.vkopi.lib.ui.swing.visual.SwingThreadHandler;
import org.kopi.vkopi.lib.ui.swing.visual.Utils;
import org.kopi.vkopi.lib.util.AWTToPS;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.DPositionPanelListener;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.PropertyException;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.xkopi.lib.type.Date;
import org.kopi.xkopi.lib.type.Time;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This is the display class of a form.
 */
public class DForm extends DWindow implements UForm, DPositionPanelListener {

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
	/**
		 * Comment for <code>serialVersionUID</code>
		 */
	private static final long serialVersionUID = -8625496726239343162L;
	public void setSelectedIndex(final int index) {
	  if (getCurrentPage() != index) {
	    performBasicAction(new KopiAction("setSelectedIndex") {
	      public void execute() throws VException {
		getModel().gotoPage(index);
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

    blockcount = getModel().getBlockCount();
    blockViews = new DBlock[blockcount];

    for (int i = 0; i < blockcount; i++) {
      VBlock      blockModel;
      DBlock      blockView;

      blockModel = getModel().getBlock(i);
      blockView = createViewForBlock(blockModel);
      blockViews[i] = blockView;

      addBlock(blockView, blockModel.getPageNumber());

      blockModel.addBlockListener(blockListener);
    }
    getModel().enableCommands();
  }

  protected DBlock createViewForBlock(VBlock blockModel) {
    DBlock                      blockView;

    if (! blockModel.isMulti()) {
      blockView = new DBlock(this, blockModel);
    } else {
      if (blockModel.noChart() && blockModel.noDetail()) {
        // !! no display; warn ?
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

  
  public Throwable getRuntimeDebugInfo() {
    return runtimeDebugInfo;
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
    edit = new JMenu(VlibProperties.getString("menu-file"));
    getDMenuBar().add(edit);

    // and also an edit menu
    edit = new JMenu(VlibProperties.getString("menu-edit")) ;
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
    JMenuItem   item = new JMenuItem(VlibProperties.getString("item-cut"));

    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    action = getActionByName(DefaultEditorKit.copyAction);
    item = new JMenuItem(VlibProperties.getString("item-copy"));
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    action = getActionByName(DefaultEditorKit.pasteAction);
    item = new JMenuItem(VlibProperties.getString("item-paste"));
    item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
    item.addActionListener(action);
    edit.add(item);

    edit.addSeparator();

    action = getActionByName(DefaultEditorKit.selectAllAction);
    action.putValue(Action.NAME, VlibProperties.getString("item-select-all"));
    // prevents garbage collection
    // therefore commented out
    //    edit.add(action);

    edit.addSeparator();
  }

  /**
   * start a block and enter in the good field (rec)
   * @exception	org.kopi.vkopi.lib.visual.VException	an exception may be raised by triggers
   */
  @SuppressWarnings("deprecation")
  public void run() throws VException {
    if (!SwingUtilities.isEventDispatchThread()) {
      System.err.println("ERROR: run() of DForm called outside the event-dispatching-thread");
    }
    getModel().prepareForm();

    // initialize the access of the blocks
    int         blockcount;

    blockcount = getModel().getBlockCount();
    for (int i = 0; i < blockcount; i++) {
      VBlock    blockModel;

      blockModel = getModel().getBlock(i);
      blockModel.updateBlockAccess();
    }

    Window      window = Utils.getWindowAncestor(this);

    window.pack();
    // is the window to big or has to be moved in the left upper edge to
    // be visible on the screen
    Rectangle   rectangle = Utils.calculateBounds(window, window.getLocation(), null);

    window.setBounds(rectangle);
    window.show();

    getModel().executeAfterStart();
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
          DForm.this.getModel().getActiveBlock().gotoNextRecord();
        }
      });
  }

  /**
   * Requests to go to the previous position.
   */
  public void gotoPrevPosition() {
    performAsyncAction(new KopiAction("gotoPrevPosition") {
        public void execute() throws VException {
          DForm.this.getModel().getActiveBlock().gotoPrevRecord();
        }
      });
  }

  /**
   * Requests to go to the last position.
   */
  public void gotoLastPosition() {
    performAsyncAction(new KopiAction("gotoLastPosition") {
        public void execute() throws VException {
          DForm.this.getModel().getActiveBlock().gotoLastRecord();
        }
      });
  }

  /**
   * Requests to go to the first position.
   */
  public void gotoFirstPosition() {
    performAsyncAction(new KopiAction("gotoFirstPosition") {
        public void execute() throws VException {
          DForm.this.getModel().getActiveBlock().gotoFirstRecord();
        }
      });
  }

  /**
   * Requests to go to the specified position.
   */
  public void gotoPosition(final int posno) {
    performAsyncAction(new KopiAction("gotoPosition") {
        public void execute() throws VException {
          DForm.this.getModel().getActiveBlock().gotoRecord(posno - 1);
        }
      });
  }

  /**
   * Returns the number of pages.
   */
  public int getPageCount() {
    return getModel().getPages().length;
  }

  /**
   * Returns the title of the specified page.
   * @param	index		the index of the specified page
   */
  public String getPageTitle(int index) {
    return getModel().getPages()[index];
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

  public VForm getModel() {
    return (VForm)super.getModel();
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

  public UBlock getBlockView(VBlock block) {
    VBlock[]    blocks = getModel().getBlocks();

    for (int i=0; i < blocks.length; i++) {
      if (block == blocks[i]) {
        return blockViews[i];
      }
    }
    return null;
  }

  public void release() {
    getModel().removeFormListener(this);
    for (int i = 0; i < blockViews.length; i++) {
      getModel().getBlock(i).removeBlockListener(blockListener);
      //!!!!      blockViews[i].release();
    }
    super.release();
  }

  public Environment getEnvironment() {
    if (environment == null) {
      environment = new Environment();
    }
    return environment;
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
      final VBlock[]    blocks =getModel().getBlocks();

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
    public void recordInfoChanged(int rec, int info) {}
    public void orderChanged() {}
    public void filterHidden() {}
    public void filterShown() {}
    public UBlock getCurrentDisplay() {
      // use another listener:
      return null;
    }
  }

  //----------------------------------------------------------------------
  // DOCUMENT PREVIEW
  // ----------------------------------------------------------------------
  
  /**
   * Show document preview
   */ 
  public void launchDocumentPreview(String file) throws VException {
    try {  
      String            command; 
      int		art = 0;
    
      if(file != null) {
        if(file.toLowerCase().endsWith(".pdf")) {
          art = VConstants.IMAGE_DOC_PDF;
        } else if(file.toLowerCase().endsWith(".jpeg")) {
          art = VConstants.IMAGE_DOC_JPEG;
        } else if(file.toLowerCase().endsWith(".tif") || file.toLowerCase().endsWith(".tiff")) {
          art = VConstants.IMAGE_DOC_TIF;
        } else {
  	  throw new VExecFailedException();
        }
      }

      if(art == VConstants.IMAGE_DOC_PDF) {
	command = ApplicationConfiguration.getConfiguration().getStringFor("pdf.preview.command");
      } else {
	command = ApplicationConfiguration.getConfiguration().getStringFor("image.preview.command");
      } 
      Runtime.getRuntime().exec(command + " " + file);
    } catch (IOException e) {
      throw new VExecFailedException(e);
    } catch (PropertyException e) {
      throw new VExecFailedException(e);
    }
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
      createFrame();
      setVisible(true);

      OutputStream                      fos;
      javax.swing.RepaintManager        rm;

      rm = javax.swing.RepaintManager.currentManager(getContentPanel());
      rm.setDoubleBufferingEnabled(false);
      setDoubleBuffered(false);

      for (int i = 0; i < getModel().blocks.length; i++) { // Walk over blocks
	try {
	  fos = new BufferedOutputStream(new FileOutputStream("images/" + getClass().getName().replace('.', '_') + "_" +
	                                                      getModel().blocks[i].getTitle().replace(' ', '_') + ".ps"));

	  if (getModel().getActiveBlock() != null) {
	    getModel().getActiveBlock().leave(false);
	  }
	  getModel().setActiveBlock(getModel().blocks[i]);
          // !!! find alternative correct
// 	  getDForm().setCurrentPage(blocks[i].getPageNumber());
// 	  getDForm().gotoPage(blocks[i].getPageNumber());

	  for (int j = 0; j < getModel().blocks.length; j++) {
	    getModel().blocks[j].prepareSnapshot(getModel().blocks[j] == getModel().blocks[i]);
	  }

          // !!! find alternative correct
          //	  getDisplay().getFrame().pack();
	  //getDisplay().repaint();
	  Thread.sleep(1000);
	  Toolkit.getDefaultToolkit().sync();
	  Thread.sleep(1000);

	  int		w = getSize().width / 2;
	  int		h = getSize().height / 2;

	  AWTToPS	ps = new AWTToPS(fos);
	  ps.setBoundingBox(0, 0, w + 2, h + 2);
	  ps.translate(0, 1200 - h - 1);
	  ps.drawRect(0, 0, w + 2, h + 2);
	  ps.translate(0, -(1200 - h - 1));
	  ps.setScale(0.5, 0.5);
	  ps.translate(2, 1200 - (h * 2) - 2);
	  ps.setTransparentColor(UIManager.getColor("snapshot.background"));
	  paint(ps);
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

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  @SuppressWarnings("deprecation")
  public PrintJob printForm() throws VException {
    com.lowagie.text.Rectangle  pageSize = PageSize.A4.rotate();
    Document                    document = new Document(pageSize, 50, 50, 50, 50);
    File                        file;

    try {
      file = org.kopi.vkopi.lib.ui.swing.base.Utils.getTempFile("kopi", "srn");

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
      foot.addCell(createCell(Date.now().format("dd.MM.yyyy") + " "+ Time.now().format("HH:mm"),
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

    PrintJob    printJob = new PrintJob(file, true, PrintJob.FORMAT_A4);

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

  private BlockRecordHandler   		blockRecordHandler;
  private BlockListener      		blockListener;

  private int				currentPage = -1;
  private DPage[]			blockPanel;
  private JTabbedPane			tabbedBlockPanel;
  private DBlock[]             		blockViews;
  protected Environment		        environment;

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long 	serialVersionUID = -5894823173117976720L;
}
