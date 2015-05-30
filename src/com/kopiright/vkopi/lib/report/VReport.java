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

package com.kopiright.vkopi.lib.report;

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Vector;

import com.kopiright.util.base.InconsistencyException;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.l10n.LocalizationManager;
import com.kopiright.vkopi.lib.l10n.ReportLocalizer;
import com.kopiright.vkopi.lib.print.Printable;
import com.kopiright.vkopi.lib.util.PrintException;
import com.kopiright.vkopi.lib.util.PrintJob;
import com.kopiright.vkopi.lib.visual.ApplicationConfiguration;
import com.kopiright.vkopi.lib.visual.ApplicationContext;
import com.kopiright.vkopi.lib.visual.FileHandler;
import com.kopiright.vkopi.lib.visual.Message;
import com.kopiright.vkopi.lib.visual.UIFactory;
import com.kopiright.vkopi.lib.visual.UWindow;
import com.kopiright.vkopi.lib.visual.VCommand;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VHelpViewer;
import com.kopiright.vkopi.lib.visual.VRuntimeException;
import com.kopiright.vkopi.lib.visual.VWindow;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.kopiright.vkopi.lib.visual.WindowBuilder;
import com.kopiright.vkopi.lib.visual.WindowController;
import com.kopiright.xkopi.lib.base.DBContextHandler;

@SuppressWarnings("serial")
public abstract class VReport extends VWindow implements Constants, VConstants, Printable {

  static {
    WindowController.getWindowController().registerWindowBuilder(com.kopiright.vkopi.lib.visual.Constants.MDL_REPORT, new WindowBuilder() {
      
      public UWindow createWindow(VWindow model) {
	return (UReport)UIFactory.getUIFactory().createView(model);
      }
    });
  }

  public int getType() {
    return com.kopiright.vkopi.lib.visual.Constants.MDL_REPORT;
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
    activeCommands = new Vector<VCommand>();

    if (ctxt != null) {
      setDBContext(ctxt.getDBContext());
    }

    init();

    // localize the report using the default locale
    localize(Locale.getDefault());
  }

  /**
   * Constructor
   */
  protected VReport() throws VException {
    this(null);
  }

   /**
    * Redisplay the report after change in formating
    * @deprecated call method in display; model must not be refreshed
    */
   public void redisplay() {
     ((UReport)getDisplay()).redisplay();
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
   * @exception	com.kopiright.vkopi.lib.visual.VException	may be raised by triggers
   */
  protected abstract void init() throws VException;

  /**
   * build everything after loading
   */
  protected void build() {
    model.build();
    model.createTree();
    ((UReport)getDisplay()).build();
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
        } else if (command.getIdent().equals("EditColumnData")) {
          cmdEditColumn = command;
        } else {
          setCommandEnabled(commands[i], true);
        }
      }
    }
  }

  public void columnMoved(int[] pos) {
    ((UReport)getDisplay()).columnMoved(pos);
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this report
   *
   * @param     locale  the locale to use
   */
  public void localize(Locale locale) {
    LocalizationManager         manager;

    manager = new LocalizationManager(locale, ApplicationContext.getDefaultLocale());

    // localizes the actors in VWindow
    super.localizeActors(manager);

    localize(manager);
    manager = null;
  }

  /**
   * Localizes this report
   *
   * @param     manager         the manger to use for localization
   */
  private void localize(LocalizationManager manager) {
    ReportLocalizer       loc;

    loc = manager.getReportLocalizer(source);
    setPageTitle(loc.getTitle());
    setHelp(loc.getHelp());
    for (int i = 0; i < model.columns.length; i++) {
      model.columns[i].localize(loc);
    }
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

  /**
   * Set the source for this document
   */
  public void setSource(String source) {
    this.source = source;
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
    command.setEnabled(enable);

    if (enable) {
      activeCommands.addElement(command);
    } else {
      activeCommands.removeElement(command);
    }
  }

  public PrintJob createPrintJob() throws PrintException, VException {
    PExport2PDF       exporter;
    PrintJob          printJob;
    exporter = new PExport2PDF(((UReport)getDisplay()).getTable(),
    			       model,
			       pconfig,
                               pageTitle,
                               firstPageHeader,
                               Message.getMessage("toner_save_mode").equals("true"));
    printJob = exporter.export();
    printJob.setDocumentType(getDocumentType());
    return printJob;
  }

  /**
   * Prints the report
   */
  public void export() throws VException {
    export(TYP_CSV);
  }

  /**
   * Prints the report
   */
  public void export(int type) throws VException {
    String      ext;

    switch (type) {
    case TYP_CSV:
      ext = ".csv";
      break;
    case TYP_PDF:
      ext = ".pdf";
      break;
    case TYP_XLS:
      ext = ".xls";
      break;
    default:
      throw new InconsistencyException("Export type unkown");
    }

    File file = FileHandler.getFileHandler().chooseFile(getDisplay(),
                                                        ApplicationConfiguration.getConfiguration().getDefaultDirectory(),
                                                        "report"+ext);
    if (file != null) {
      export(file, type);
    }
  }

  /**
   * Prints the report
   */
  public void export(File file) throws VException {
    export(file, TYP_CSV);
  }

  public void export(File file, int type) throws com.kopiright.vkopi.lib.visual.VException {
    setWaitInfo(VlibProperties.getString("export-message"));

    PExport     exporter;

    switch (type) {
    case TYP_CSV:
      exporter = new PExport2CSV(((UReport)getDisplay()).getTable(),
                                 model,
                                 pconfig,
                                 pageTitle);
      break;
    case TYP_PDF:
      exporter = new PExport2PDF(((UReport)getDisplay()).getTable(),
                                 model,
                                 pconfig,
                                 pageTitle,
                                 firstPageHeader);
      break;
    case TYP_XLS:
      exporter = new PExport2XLS(((UReport)getDisplay()).getTable(),
                                 model,
                                 pconfig,
                                 pageTitle);
      break;
    default:
      throw new InconsistencyException("Export type unkown");
    }
    exporter.export(file);
    unsetWaitInfo();
    fireFileProduced(file);
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

  public void setPageTitleParams(Object param) {
    setPageTitleParams(new Object[] {param});
  }

  public void setPageTitleParams(Object param1, Object param2) {
    setPageTitleParams(new Object[] {param1, param2});
  }

  public void setPageTitleParams(Object[] params) {
    setPageTitle(MessageFormat.format(pageTitle, params));
  }

  public void setFirstPageHeader(String firstPageHeader) {
    this.firstPageHeader = firstPageHeader;
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
    ((UReport)getDisplay()).resetWidth();

    setMenu();
  }

  public void unfoldSelectedColumn() {
    int column = getSelectedColumn();
    if (column != -1) {
      model.setColumnFolded(column, false);
    }
    ((UReport)getDisplay()).resetWidth();

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

  public void setColumnData() throws VException {
    if (cmdEditColumn != null) {
      executeVoidTrigger(cmdEditColumn.getTrigger());
    }
  }

  public void setColumnInfo() throws VException {
    if (cmdColumnInfo != null) {
      executeVoidTrigger(cmdColumnInfo.getTrigger());
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
      if (model.getModelColumn(i).getIdent().equals("ID")) {
        idCol = i;
      }
    }
    if (idCol != -1 && (getSelectedCell().y != -1)) {
      id = ((Integer)model.getRow(getSelectedCell().y).getValueAt(idCol)).intValue();
    }
    if (id == -1) {
      throw new VRuntimeException();
    } else {
      return id;
    }
  }

  /**
   * Return the value of a field in the selected row
   * by passing its name(key)
   */

  public Object getValueOfField(Object key) {
    int         col = -1;

    for (int i = 0; i < model.getModelColumnCount() && col == -1; i++) {
      if (model.getModelColumn(i).getIdent().equals(key)) {
        col = i;
      }
    }
    if (col != -1 && (getSelectedCell().y != -1)) {
      return  model.getRow(getSelectedCell().y).getValueAt(col);
    }
      return null;
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

  public void executeVoidTrigger(final int VKT_Type) throws com.kopiright.vkopi.lib.visual.VException {
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

  public void setMenu() {
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
    if (cmdEditColumn != null) {
      setCommandEnabled(cmdEditColumn, column != -1 && model.getAccessibleColumn(column).isAddedAtRuntime());
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Returns the selected column or -1 if no column is selected.
   */
  private int getSelectedColumn() {
    return ((UReport)getDisplay()).getSelectedColumn();
  }

  /**
   * Returns the selected cell or !!! ??? if no cell is selected.
   */
  private Point getSelectedCell() {
    return ((UReport)getDisplay()).getSelectedCell();
  }

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  @SuppressWarnings("deprecation")
  public String genHelp() {
    String              fileName;
    StringBuffer        surl = new StringBuffer();

    fileName = new VHelpGenerator().helpOnReport(getTitle(),
                                                 commands,
                                                 model,
                                                 help);
    try {
      surl.append(new File(fileName).toURL().toString());
    } catch (java.net.MalformedURLException mue) {
      throw new InconsistencyException(mue);
    }

    return surl.toString();
  }

  public void showHelp() {
    new VHelpViewer().showHelp(genHelp());
  }
 
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VCommand             cmdFold;
  private VCommand             cmdUnfold;
  private VCommand             cmdSort;
  private VCommand             cmdOpenLine;
  private VCommand             cmdFoldColumn;
  private VCommand             cmdUnfoldColumn;
  private VCommand             cmdColumnInfo;
  private VCommand             cmdEditColumn;

  private String               source;
  protected MReport            model;
  private boolean              built;
  private String               pageTitle = "";
  private String               firstPageHeader = "";
  private String               help;

  protected int[][]            VKT_Triggers;	// trigger list
  protected VCommand[]         commands;	// commands
  private Vector<VCommand>     activeCommands;

  protected PConfig        	pconfig;	// print configuration object
  private String         	media;

  public static final int       TYP_CSV = 1;
  public static final int       TYP_PDF = 2;
  public static final int       TYP_XLS = 3;
}
