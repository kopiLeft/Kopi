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

package at.dms.vkopi.lib.report;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.SwingUtilities;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.Message;
import at.dms.vkopi.lib.util.PrintException;
import at.dms.vkopi.lib.util.Printer;
import at.dms.vkopi.lib.util.PrintJob;
import at.dms.vkopi.lib.form.VConstants;
import at.dms.vkopi.lib.form.VField;
import at.dms.vkopi.lib.print.Printable;
import at.dms.vkopi.lib.visual.*;
import at.dms.xkopi.lib.base.DBContext;
import at.dms.xkopi.lib.base.DBContextHandler;

public abstract class VReport extends VWindow
  implements Constants, VConstants, Printable {

  static {
    WindowController.getWindowController().registerUIBuilder(Constants.MDL_REPORT, new UIBuilder() {
        public DWindow createView(VWindow model) {
          ((VReport) model).buildDisplay();
          return model.getDisplay();
        }
      });
  }

  public int getType() {
    return Constants.MDL_REPORT;
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  protected VReport(DBContextHandler ctxt) throws VException {
    model = new MReport();
    pconfig = new PConfig();
    activeCommands = new Vector();

    if (ctxt != null) {
      setDBContext(ctxt.getDBContext());
    }

    init();
  }

  /**
   * Constructor
   */
  protected VReport() throws VException {
    this(null);
  }

  /**
   * Build the display
   */
  protected void buildDisplay() {
    setDisplay(new DReport(this));
  }

   /**
    * Redisplay the report after change in formating
    * @deprecated call method in display; model must not be refreshed 
    */
   public void redisplay() {
     ((DReport)getDisplay()).redisplay();
   }

   /**
    * Close window
    * @deprecated call method in display; model must not be closed 
    */
  public void  close () {
    getDisplay().closeWindow();
  }


  public void destroyModel() {
    try {
      callTrigger(Constants.TRG_POSTREPORT);
    } catch (VException v) {
      // ignore
    }
    super.destroyModel();
  }

  /**
   * initialise fields
   * @exception	at.dms.vkopi.lib.visual.VException	may be raised by triggers
   */
  protected abstract void init() throws VException;

  /**
   * build everything after loading
   */
  protected void build() {
    model.build();
    model.createTree();
    getDisplay().build();
    built = true;

    // all commands are by default enabled
    activeCommands.setSize(0);
    if (commands != null) {
      for (int i = 0; i < commands.length; i++) {
        VCommand        command = commands[i];

        if (command.getIdent().equals("Fold")) {
          cmdFold = command;
        } else if (command.getIdent().equals("Unfold")) {
          cmdUnfold = command;
        } else if (command.getIdent().equals("Sort")) {
          cmdSort = command;
        } else if (command.getIdent().equals("FoldColumn")) {
          cmdFoldColumn = command;
        } else if (command.getIdent().equals("UnfoldColumn")) {
          cmdUnfoldColumn = command;
        } else if (command.getIdent().equals("OpenLine")) {
          cmdOpenLine = command;
        } else if (command.getIdent().equals("ColumnInfo")) {
          cmdColumnInfo = command;
        } else {
          setCommandEnabled(commands[i], true);
        }
      }
    }
  }

  public void columnMoved(final int[] pos) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          ((DReport)getDisplay()).reorder(pos);
          getModel().columnMoved(pos);
          ((DReport)getDisplay()).redisplay();
        }
      });
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get the media for this document
   */
  public String getMedia() {
    return media;
  }

  /**
   * Set the media for this document
   */
  public void setMedia(String media) {
    this.media = media;
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------

  public void initReport() throws VException {
    build();
    callTrigger(Constants.TRG_PREREPORT);
  }


  // ----------------------------------------------------------------------
  // INTERFACE (COMMANDS)
  // ----------------------------------------------------------------------

  /**
   * Enables/disables the actor.
   */
  public void setCommandEnabled(final VCommand command, final boolean enable) {
    SwingThreadHandler.start(new Runnable () {
        public void run() {
          command.setEnabled(enable);
        }
      });

     if (enable) {
       activeCommands.addElement(command);
     } else {
       activeCommands.removeElement(command);
     }
  }

  public PrintJob createPrintJob() throws PrintException, VException {
    PGenPS      pgenps = new PGenPS(((DReport)getDisplay()).getTable(), model, pconfig, pageTitle);
    PrintJob    printJob = pgenps.printInto();

    printJob.setDocumentType(getDocumentType());
    printJob.setTitle(pageTitle);
    return printJob;
  }

  /**
   * Prints the report
   */
  public void export() throws VException {
    File file = FileChooser.chooseFile(getDisplay().getFrame(),
                                       ApplicationConfiguration.getConfiguration().getDefaultDirectory(),
                                       "report.csv");
    if (file != null) {
      export(file);
    }
  }

  /**
   * Prints the report
   */
  public void export(File file) throws VException {
    setWaitInfo(Message.getMessage("export-message"));

    PExport2CSV exporter = new PExport2CSV(((DReport)getDisplay()).getTable(),
                                           model,
                                           pconfig,
                                           pageTitle);
    exporter.export(file);
  }

  /**
   * sets the print options
   */
  public PConfig getPrintOptions() {
    return pconfig;
  }

  /**
   * sets the print options
   */
  public void setPrintOptions(PConfig conf) {
    this.pconfig = conf;
  }

  /**
   * Sets the title
   */
  public void setPageTitle(String title) {
    this.pageTitle = title;
    setTitle(title);
  }

  public VReportColumn getColumn(int i) {
    return model.getModelColumn(i);
  }

  public void foldSelection() {
    int column = getSelectedColumn();

    if (column != -1) {
      model.foldingColumn(column);
    } else {
      Point point = getSelectedCell();
      if (point.y != -1 && point.x != -1) {
	model.foldingRow(point.y, point.x);
      }
    }

    setMenu();
  }

  public void unfoldSelection() {
    int		column = getSelectedColumn();

    if (column != -1) {
      model.unfoldingColumn(column);
    } else {
      Point point = getSelectedCell();
      if (point.y != -1 && point.x != -1) {
	model.unfoldingRow(point.y, point.x);
      }
    }

    setMenu();
  }

  public void foldSelectedColumn() {
    int		column = getSelectedColumn();
    if (column != -1) {
      model.setColumnFolded(column, true);
    }
    ((DReport)getDisplay()).resetWidth();

    setMenu();
  }

  public void unfoldSelectedColumn() {
    int column = getSelectedColumn();
    if (column != -1) {
      model.setColumnFolded(column, false);
    }
    ((DReport)getDisplay()).resetWidth();

    setMenu();
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  public void sortSelectedColumn() {
    model.sortColumn(getSelectedColumn());
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  public void editLine() throws VException {
    if (cmdOpenLine != null) {
      executeVoidTrigger(cmdOpenLine.getTrigger());
    }
  }

  public MReport getModel() {
    return model;
  }

  // ----------------------------------------------------------------------
  // INTERFACE (COMMANDS)
  // ----------------------------------------------------------------------

  /**
   * Adds a line.
   */
  public abstract void add();

  /**
   * Returns the ID
   */
  public int getValueOfFieldId() {
    int         idCol = -1;
    int         id = -1;

    for (int i = 0; i < model.getModelColumnCount() && idCol == -1; i++) {
      if (model.getModelColumn(i).getName().equals("ID")) {
        idCol = i;
      }
    }

    if (idCol != -1 && (getSelectedCell().y != -1)) {
      id = ((Integer)model.getRow(getSelectedCell().y)
            .getValueAt(idCol)).intValue();
    }
    if (id == -1) {
      throw new VRuntimeException();
    } else {
      return id;
    }
  }

  // ----------------------------------------------------------------------
  // METHODS FOR SQL
  // ----------------------------------------------------------------------

  /**
   * creates an SQL condition, so that the column have to fit the 
   * requirements (value and search operator) of the field.
   */  
  protected String buildSQLCondition(String column, VField field) {
    String      condition;

    condition = field.getSearchCondition();
    if (condition == null) {
      return (" TRUE = TRUE ");
    } else {
      return column + " " + condition;
    }
  }
  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  public void executeVoidTrigger(final int VKT_Type) throws at.dms.vkopi.lib.visual.VException {
  }
  public Object executeObjectTrigger(final int VKT_Type) throws at.dms.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }
  public boolean executeBooleanTrigger(final int VKT_Type) throws at.dms.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }
  public int executeIntegerTrigger(final int VKT_Type) throws at.dms.vkopi.lib.visual.VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  public int getDocumentType() {
    return DOC_UNKNOWN;
  }

  /**
   * overridden by forms to implement triggers
   * default triggers
   */
  protected Object execTrigger(Object block, int id) throws VException {
    executeVoidTrigger(id);
    return null;
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callTrigger(int event) throws VException {
    return callTrigger(event, 0);
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected Object callTrigger(int event, int index) throws VException {
    switch (Constants.TRG_TYPES[event]) {
    case Constants.TRG_VOID:
      executeVoidTrigger(VKT_Triggers[index][event]);
      return null;
    case Constants.TRG_OBJECT:
      return executeObjectTrigger(VKT_Triggers[index][event]);
    default:
      throw new InconsistencyException("BAD TYPE" + Constants.TRG_TYPES[event]);
    }
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected boolean hasTrigger(int event) {
    return hasTrigger(event, 0);
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected boolean hasTrigger(int event, int index) {
    return VKT_Triggers[index][event] != 0;
  }

  protected void setMenu() {
    if (!built) {
      // only when commands are displayed
      return;
    }

    int         column = getSelectedColumn();
    Point       cell = getSelectedCell();
    boolean     foldEnabled = (column != -1 && !model.isColumnFold(column)) ||
      (cell.x != -1 && cell.y != -1 && !model.isRowFold(cell.y, cell.x));
    boolean     unfoldEnabled = (column != -1) || (cell.x != -1 && cell.y != -1);

    if (cmdFold != null) {
      setCommandEnabled(cmdFold, foldEnabled);
    }
    if (cmdUnfold != null) {
      setCommandEnabled(cmdUnfold, unfoldEnabled);
    }
    if (cmdSort != null) {
      setCommandEnabled(cmdSort, column != -1);
    }
    if (cmdOpenLine != null) {
      setCommandEnabled(cmdOpenLine, model.isRowLine(cell.y));
    }
    if (cmdFoldColumn != null) {
      setCommandEnabled(cmdFoldColumn,column != -1 );
    }
    if (cmdUnfoldColumn != null) {
      setCommandEnabled(cmdUnfoldColumn, column != -1);
    }
    if (cmdColumnInfo != null) {
      setCommandEnabled(cmdColumnInfo, column != -1);
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Returns the selected column or -1 if no column is selected.
   */
  private int getSelectedColumn() {
    return ((DReport)getDisplay()).getSelectedColumn();
  }

  /**
   * Returns the selected cell or !!! ??? if no cell is selected.
   */
  private Point getSelectedCell() {
    return ((DReport)getDisplay()).getSelectedCell();
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VCommand		cmdFold;
  private VCommand		cmdUnfold;
  private VCommand		cmdSort;
  private VCommand		cmdOpenLine;
  private VCommand		cmdFoldColumn;
  private VCommand		cmdUnfoldColumn;
  private VCommand		cmdColumnInfo;

  private Thread		currentThread;
  protected MReport		model;
  private boolean		built;
  private String		pageTitle = "";

  protected int[][]		VKT_Triggers;	// trigger list
  protected VCommand[]		commands;	// commands
  private Vector		activeCommands;

  private PConfig		pconfig;	// print configuration object
  private boolean		inAction;

  private String                media;
}
