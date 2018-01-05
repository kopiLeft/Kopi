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

package org.kopi.vkopi.lib.chart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Vector;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.base.Utils;
import org.kopi.vkopi.lib.l10n.ChartLocalizer;
import org.kopi.vkopi.lib.l10n.LocalizationManager;
import org.kopi.vkopi.lib.print.Printable;
import org.kopi.vkopi.lib.util.PPaperType;
import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.visual.ApplicationConfiguration;
import org.kopi.vkopi.lib.visual.ApplicationContext;
import org.kopi.vkopi.lib.visual.FileHandler;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.UIFactory;
import org.kopi.vkopi.lib.visual.UWindow;
import org.kopi.vkopi.lib.visual.VCommand;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VHelpViewer;
import org.kopi.vkopi.lib.visual.VWindow;
import org.kopi.vkopi.lib.visual.VlibProperties;
import org.kopi.vkopi.lib.visual.WindowBuilder;
import org.kopi.vkopi.lib.visual.WindowController;
import org.kopi.xkopi.lib.base.DBContextHandler;

import com.lowagie.text.Rectangle;

/**
 * The {@code VChart} is a window containing a chart inside.
 * The chart can have any type. The standard implementation will
 * provide <b>five</b> chart types :
 * <ul>
 *   <li>Bar chart;</li>
 *   <li>Area chart;</li>
 *   <li>Line chart;</li>
 *   <li>Column chart;</li>
 *   <li>Pie chart;</li>
 * </ul>
 * Other chart implementations can be provided by extending this class.
 */
@SuppressWarnings("serial")
public abstract class VChart extends VWindow implements CConstants, Printable {

  // --------------------------------------------------------------------
  // STATIC INITIALIZATION
  // --------------------------------------------------------------------

  static {
    WindowController.getWindowController().registerWindowBuilder(MDL_CHART, new WindowBuilder() {

      /**
       * @Override
       */
      public UWindow createWindow(VWindow model) {
	return (UChart)UIFactory.getUIFactory().createView(model);
      }
    });
  }

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Creates a new chart model.
   * @throws VException Visual errors.
   */
  public VChart() throws VException {
    this(null);
  }

  /**
   * Creates a new chart model.
   * @param ctxt The database context handler.
   * @throws VException Visual errors.
   */
  public VChart(DBContextHandler ctxt) throws VException {
    activeCommands = new Vector<VCommand>();
    rows = new Vector<VRow>(500);
    poptions = new VPrintOptions();
    if (ctxt != null) {
      setDBContext(ctxt.getDBContext());
    }
    init();
    // localize the report using the default locale
    localize(ApplicationContext.getDefaultLocale());
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
  protected void localize(LocalizationManager manager) {
    ChartLocalizer       	loc;

    loc = manager.getChartLocalizer(source);
    setPageTitle(loc.getTitle());
    setHelp(loc.getHelp());
    // dimensions
    for (int i = 0; i < dimensions.length; i++) {
      dimensions[i].localize(loc);
    }
    // measures
    for (int i = 0; i < measures.length; i++) {
      measures[i].localize(loc);
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  /**
   * @Override
   */
  public PrintJob createPrintJob() throws PrintException, VException {
    try {
      PrintJob          printJob;
      Rectangle       	page;
      File            	file = Utils.getTempFile("kopi", "pdf");
      PPaperType	paper = PPaperType.getPaperTypeFromCode(poptions.papertype);

      if (poptions.paperlayout.equals("Landscape")) {
	page  = new Rectangle(paper.getHeight(), paper.getWidth());
      } else {
	page  = new Rectangle(paper.getWidth(), paper.getHeight());
      }
      export(file, TYP_PDF);
      printJob = new PrintJob(file, true, page);
      printJob.setDataType(PrintJob.DAT_PDF);
      printJob.setTitle(getTitle());
      printJob.setNumberOfPages(1);
      printJob.setDocumentType(getDocumentType());

      return printJob;
    } catch (IOException e) {
      throw new VExecFailedException(e);
    }
  }

  /**
   * Returns the document type.
   * @return The document type.
   */
  public int getDocumentType() {
    return DOC_UNKNOWN;
  }

  /**
   * @Override
   */
  public int getType() {
    return MDL_CHART;
  }

  /**
   * Sets the chart menu. This will enable and disable
   * commands according to the chart generation context.
   */
  public void setMenu() {
    if (!built) {
      // only when commands are displayed
      return;
    }

    if (cmdBarView != null) {
      setCommandEnabled(cmdBarView, type != VChartType.BAR);
    }
    if (cmdColumnView != null) {
      setCommandEnabled(cmdColumnView, type != VChartType.COLUMN);
    }
    if (cmdLineView != null) {
      setCommandEnabled(cmdLineView, type != VChartType.LINE);
    }
    if (cmdAreaView != null) {
      setCommandEnabled(cmdAreaView, type != VChartType.AREA);
    }
    if (cmdPieView != null) {
      setCommandEnabled(cmdPieView, type != VChartType.PIE);
    }
  }

  /**
   * Initialization of the chart model.
   * @throws VException Visual errors.
   */
  public void initChart() throws VException {
    build();
    callTrigger(TRG_PRECHART);
  }


  /**
   * build everything after loading
   */
  protected void build() throws VException {
    if (rows.size() == 0) {
      throw new VNoChartRowException(MessageCode.getMessage("VIS-00015"));
    }
    ((UChart)getDisplay()).build();
    setType(VChartType.DEFAULT, false);
    built = true;
    if (hasTrigger(TRG_INIT)) {
      callTrigger(TRG_INIT);
    }
    if (hasFixedType()) {
      setType((VChartType) callTrigger(TRG_CHARTTYPE));
    }
    // all commands are by default enabled
    activeCommands.setSize(0);
    if (commands != null) {
      for (int i = 0; i < commands.length; i++) {
	if (commands[i].getIdent().equals("BarView")) {
          cmdBarView = commands[i];
        } else if (commands[i].getIdent().equals("ColumnView")) {
          cmdColumnView = commands[i];
        } else if (commands[i].getIdent().equals("LineView")) {
          cmdLineView = commands[i];
        } else if (commands[i].getIdent().equals("AreaView")) {
          cmdAreaView = commands[i];
        } else if (commands[i].getIdent().equals("PieView")) {
          cmdPieView = commands[i];
        } else {
          setCommandEnabled(commands[i], getColumnCount() + i + 1, true);
        }
      }
    }
  }

  /**
   * Returns {@code true} is the chart has a fixed type.
   * @return {@code true} is the chart has a fixed type.
   */
  public boolean hasFixedType() {
    return hasTrigger(TRG_CHARTTYPE);
  }

  /**
   * Returns the chart fixed type.
   * @return The chart fixed type.
   */
  public VChartType getFixedType() throws VException {
    return (VChartType) callTrigger(TRG_CHARTTYPE);
  }

  /**
   * Refreshes the chart display.
   */
  public void refresh() {
    ((UChart)getDisplay()).refresh();
  }

  /**
   * Closes the chart window.
   */
  public void close() {
    getDisplay().closeWindow();
  }

  /**
   * Shows the chart help window.
   */
  public void showHelp() {
    new VHelpViewer().showHelp(genHelp());
  }

  /**
   * @Override
   */
  public void destroyModel() {
    try {
      callTrigger(TRG_POSTCHART);
    } catch (VException v) {
      // ignore
      v.printStackTrace();
    }
    super.destroyModel();
  }

  /**
   * Sets the new type of this chart model.
   * @param type The new chart type.
   */
  public void setType(VChartType type) throws VException {
    setType(type, true);
  }

  /**
   * Prints the report
   */
  public void export() throws VException {
    export(TYP_PNG);
  }

  /**
   * Prints the report
   */
  public void export(int type) throws VException {
    String      ext;

    switch (type) {
    case TYP_PNG:
      ext = ".png";
      break;
    case TYP_PDF:
      ext = ".pdf";
      break;
    case TYP_JPEG:
      ext = ".jpeg";
      break;
    default:
      throw new InconsistencyException("Export type unkown");
    }

    File file = FileHandler.getFileHandler().chooseFile(getDisplay(),
                                                        ApplicationConfiguration.getConfiguration().getDefaultDirectory(),
                                                        "chart"+ext);
    if (file != null) {
      try {
	export(file, type);
      } catch (IOException e) {
	throw new VExecFailedException(e);
      }
    }
  }

  /**
   * Exports the chart to the given format.
   * @param file The destination file.
   * @param type The export type.
   * @throws IOException I/O errors.
   */
  public void export(File file, int type) throws IOException {
    OutputStream		destination;
    boolean			exported;

    destination = new FileOutputStream(file);
    exported = false;
    setWaitInfo(VlibProperties.getString("export-message"));
    try {
      switch (type) {
      case TYP_PDF:
	((UChart)getDisplay()).getType().exportToPDF(destination, poptions);
	exported = true;
	break;
      case TYP_PNG:
	((UChart)getDisplay()).getType().exportToPNG(destination, poptions.imageWidth, poptions.imageHeight);
	exported = true;
	break;
      case TYP_JPEG:
	((UChart)getDisplay()).getType().exportToJPEG(destination, poptions.imageWidth, poptions.imageHeight);
	exported = true;
	break;
      default:
	throw new InconsistencyException("Export type unkown");
      }
    } finally {
      destination.close();
      unsetWaitInfo();
      if (exported) {
	fireFileProduced(file);
      }
    }
  }

  /**
   * Sets the new type of this chart model.
   * @param type The new chart type.
   * @param refresh should we refresh the view side ?
   */
  protected void setType(VChartType type, boolean refresh) throws VException {
    if (hasFixedType() && type != getFixedType()) {
      return;
    }

    this.type = type;
    type.createDataSeries(this);
    ((UChart)getDisplay()).setType(ChartTypeFactory.getChartTypeFactory().createTypeView(getTitle(), type));
    if (refresh) {
      refresh();
      ((UChart)getDisplay()).typeChanged();
    }
  }

  /**
   * Returns the chart type.
   */
  public VChartType getChartType() {
    return type;
  }

 /**
  * Appends a row to the chart rows.
  * @param dimension The dimension value.
  * @param measures The measures values.
  */
  protected void addRow(Object[] dimensions, Object[] measures) {
    rows.addElement(new VRow(dimensions, measures));
  }

  /**
   * Returns the column count.
   * @return The column count.
   */
  protected int getColumnCount()  {
    return dimensions.length + measures.length;
  }

  // ----------------------------------------------------------------------
  // COMMANDS
  // ----------------------------------------------------------------------

  /**
   * Enables/disables the actor.
   */
  public void setCommandEnabled(final VCommand command, int index, boolean enable) {
    if (enable) {
      // we need to check if VKT_Triggers is initialized
      // ex : org.kopi.vkopi.lib.cross.VDynamicReport
      if (VKT_Triggers != null && hasTrigger(TRG_CMDACCESS, index)) {
	boolean			active;

	try {
	  active = ((Boolean)callTrigger(TRG_CMDACCESS, index)).booleanValue();
	} catch (VException e) {
	  // trigger call error ==> command is considered as active
	  active = true;
	}
	enable = active;
      }
      command.setEnabled(enable);
      activeCommands.addElement(command);
    } else {
      activeCommands.removeElement(command);
      command.setEnabled(false);
    }
  }

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

  // --------------------------------------------------------------------
  // TRIGGER HANDLING
  // --------------------------------------------------------------------

  /**
   * @Override
   */
  public void executeVoidTrigger(final int VKT_Type) throws VException {}

  public Object executeObjectTrigger(final int VKT_Type) throws VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  public boolean executeBooleanTrigger(final int VKT_Type) throws VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
  }

  public int executeIntegerTrigger(final int VKT_Type) throws VException {
    throw new InconsistencyException("SHOULD BE REDEFINED");
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
    switch (CConstants.TRG_TYPES[event]) {
    case CConstants.TRG_VOID:
      executeVoidTrigger(VKT_Triggers[index][event]);
      return null;
    case CConstants.TRG_OBJECT:
      return executeObjectTrigger(VKT_Triggers[index][event]);
    default:
      throw new InconsistencyException("BAD TYPE" + CConstants.TRG_TYPES[event]);
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

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  protected String getSource() {
    return source;
  }

  /**
   * sets the print options
   */
  public VPrintOptions getPrintOptions() {
    return poptions;
  }

  /**
   * sets the print options
   */
  public void setPrintOptions(VPrintOptions potions) {
    this.poptions = potions;
  }

  /**
   * Set the source for this document
   * @param source The source of the document.
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * Returns the dimension column.
   * @return The dimension column.
   */
  public VMeasure getMeasure(int column) {
    return measures[column];
  }

  /**
   * Returns the dimension column.
   * @return The dimension column.
   */
  public VDimension getDimension(int column) {
    return dimensions[column];
  }

  /**
   * Sets the title
   */
  public void setPageTitle(String title) {
    this.pageTitle = title;
    setTitle(title);
  }

  /**
   * Sets the page title parameter.
   * @param param The page title parameter.
   */
  public void setPageTitleParams(Object param) {
    setPageTitleParams(new Object[] {param});
  }

  /**
   * Sets the page title parameters.
   * @param param1 The first parameter.
   * @param param2 The second parameter.
   */
  public void setPageTitleParams(Object param1, Object param2) {
    setPageTitleParams(new Object[] {param1, param2});
  }

  /**
   * Sets the page title parameters.
   * @param params The parameters to be set.
   */
  public void setPageTitleParams(Object[] params) {
    setPageTitle(MessageFormat.format(pageTitle, params));
  }

  /**
   * Returns the chart rows.
   * @return The chart rows.
   */
  protected VRow[] getRows() {
    return rows.toArray(new VRow[rows.size()]);
  }

  /**
   * Returns the chart columns.
   * @return the chart columns.
   */
  protected VColumn[] getColumns() {
    VColumn[]		columns;

    columns = new VColumn[getColumnCount()];
    for (int i = 0; i < dimensions.length; i++) {
      columns[i] = dimensions[i];
    }

    for (int i = 0; i < measures.length; i++) {
      columns[i + dimensions.length] = measures[i];
    }

    return columns;
  }

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------

  /**
   * Returns the chart help.
   * @return The chart help.
   */
  public String getHelp() {
    return help;
  }

  /**
   * Sets the chart help.
   * @param help The chart help to be set.
   */
  public void setHelp(String help) {
    this.help = help;
  }

  @SuppressWarnings("deprecation")
  public String genHelp() {
    String              fileName;
    StringBuffer        surl = new StringBuffer();

    fileName = new VHelpGenerator().helpOnChart(getTitle(),
                                                commands,
                                                getColumns(),
                                                help);
    if (fileName == null) {
      return null;
    } else {
      try {
        surl.append(new File(fileName).toURL().toString());
      } catch (java.net.MalformedURLException mue) {
        throw new InconsistencyException(mue);
      }

      return surl.toString();
    }
  }

  // --------------------------------------------------------------------
  // ABSTRACT METHODS
  // --------------------------------------------------------------------

  /**
   * The chart columns initialization. Will be implemented by subclasses
   * @throws VException Visual errors.
   */
  protected abstract void init() throws VException;

  /**
   * Adds a data row to this chart.
   */
  public abstract void add();

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private VCommand             			cmdBarView;
  private VCommand             			cmdColumnView;
  private VCommand             			cmdLineView;
  private VCommand             			cmdAreaView;
  private VCommand             			cmdPieView;

  private String               			source;
  private boolean              			built;
  private String               			pageTitle = "";
  private String               			help;
  private VChartType				type; // chart type

  protected int[][]            			VKT_Triggers;	// trigger list
  protected VCommand[]         			commands;	// commands
  private Vector<VCommand>     			activeCommands;
  private VPrintOptions				poptions;

  /**
   * The chart dimensions. The actual version supports only one dimension
   */
  protected VDimension[]			dimensions;
  /**
   * The chart measures.
   */
  protected VMeasure[]				measures;
  private Vector<VRow>				rows;


  public static final int       		TYP_PDF = 1;
  public static final int       		TYP_PNG = 2;
  public static final int       		TYP_JPEG = 3;
}
