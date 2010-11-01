/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package com.kopiright.xkopi.comp.dbi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.kopiright.compiler.base.Compiler;
import com.kopiright.compiler.base.CompilerMessages;
import com.kopiright.compiler.base.PositionedError;
import com.kopiright.compiler.base.UnpositionedError;
import com.kopiright.compiler.tools.antlr.extra.InputBuffer;
import com.kopiright.compiler.tools.antlr.runtime.ParserException;
import com.kopiright.kopi.comp.kjc.CTypeContext;
import com.kopiright.util.base.Utils;
import com.kopiright.xkopi.comp.sqlc.SelectStatement;
import com.kopiright.xkopi.comp.sqlc.SqlContext;
import com.kopiright.xkopi.comp.sqlc.TableReference;
import com.kopiright.xkopi.lib.base.DriverInterface;

/**
 * This class is a console which allows to write in Dbi syntax to
 * databases.
 */
public class Console extends Compiler implements Constants {

  private Console() {
    super(null, null);

    infiles = new Vector();
  }

  public static void executeKopiDbSchema(String host, String dbname, String datasource, String login, String password, InputStream file) {
    // initialise a new console instance.
    Console console = new Console();
    ConsoleOptions options = new ConsoleOptions();
    
    options.datasource = datasource;
    options.host = host;
    options.dbname = dbname;
    options.login = login;
    options.password = password;
    options.noprompt = false;
    options.abortonerror = true;
    options.trace = true;
    console.options = options;
    console.executeKopiDbSchema(file);
  }

  private void executeKopiDbSchema(InputStream file) {
    try {
      datasource = DbiDataSourceFactory.create(options.datasource);
      if (options.host != null &&
          options.dbname != null &&
          options.login != null) {
        final String  url = datasource.startURL + "//" + options.host + "/" + options.dbname;
        
        connection = DriverManager.getConnection(url,
                                                 options.login,
                                                 options.password);
        connection.setAutoCommit(false);
      } else {
        System.out.println("error : verify that host, dbname and login are not null.");
        System.exit(1);
      }
    } catch (SQLException e) {
      inform(DbiMessages.CONNECTION_FAILED, e.getMessage());
    }
    output = new PrintWriter(new OutputStreamWriter(System.out));
    input = new BufferedReader(new InputStreamReader(file));
    processInput();
  }

  // --------------------------------------------------------------------
  // METHODS INHERITED FROM Compiler
  // --------------------------------------------------------------------

  /**
   * Runs a compilation session
   *
   * @param     args            the arguments to the compiler
   * @return    true iff the compilation succeeded
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    } else {
      output = new PrintWriter(new OutputStreamWriter(System.out));
      
      if (infiles.isEmpty()) {
        input = new BufferedReader(new InputStreamReader(System.in));
        processInput();
      } else {
        ListIterator      iterator = infiles.listIterator();
        
        while (iterator.hasNext()) {
          String  filename = (String)iterator.next();
          
          try {
            input = new BufferedReader(new FileReader(filename));
            
            processInput();
          } catch (FileNotFoundException e) {
            reportTrouble(e);
          }
        }
      }
      return true;
    }
  }

  /**
   * Reports a trouble (error or warning).
   *
   * @param     trouble         a description of the trouble to report.
   */
  public void reportTrouble(PositionedError trouble) {
    inform(trouble);
  }

  /**
   * Reports a trouble (error or warning).
   *
   * @param     trouble         a description of the trouble to report.
   */
  public void reportTrouble(Throwable trouble) {
    inform(CompilerMessages.FORMATTED_ERROR, trouble.getMessage());
  }

  /**
   * Returns true iff comments should be parsed (false if to be skipped).
   */
  public boolean parseComments() {
    return false;
  }

  /**
   * Returns true iff compilation runs in verbose mode.
   */
  public boolean verboseMode() {
    return false;
  }

  /**
   * Returns the version of the source code
   *
   * @return     version of the code
   */
  public int getSourceVersion() {
    return -1;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * Parse the argument list
   */
  private boolean parseArguments(String[] args) {
    options = new ConsoleOptions();

    if (options.parseCommandLine(args)) {
      trace = options.trace;
      datasource = DbiDataSourceFactory.create(options.datasource);

      try {
        if (options.host != null
            && options.dbname != null
            && options.login != null)
          {
            final String  url = datasource.startURL + "//" + options.host + "/"
              + options.dbname;

            connection = DriverManager.getConnection(url,
                                                     options.login,
                                                     options.password);
            connection.setAutoCommit(false);
          } else {
          options.usage();

          return false;
        }
      } catch (SQLException e) {
        inform(DbiMessages.CONNECTION_FAILED, e.getMessage());
        return false;
      }

      infiles = Utils.toList(options.nonOptions);

      return true;
    } else {
      return false;
    }
  }

  private void processInput() {
    boolean     quit = false;

    while (!quit) {
      try {
        AbstractStatement     s;

        s = readStatement();
        quit = s.execute();
      } catch (Throwable e) {
        reportTrouble(e);
        if (options.abortonerror) {
          abortTransaction();
          quit();
          quit = true;
        }
      }
    }
  }

  /**
   *
   */
  private void printPrompt(boolean statementEnded) {
    if (!options.noprompt) {
      output.print(options.host + ":");
      output.print(options.login + "@" + options.dbname + " ");
      output.print(inTransaction ?"+ " : "- ");
      output.print((statementEnded ? "> " : "| "));
    }
    output.flush();
  }

  /**
   *
   */
  private AbstractStatement readStatement() {
    try {
      String    line;

      // read first line
      printPrompt(true);
      do {
        line = input.readLine();
      } while (line == null || line.equals(""));

      if (line.equals("COMMIT;")) {
        return new CommitStatement();
      } else if (line.equals("ABORT;")) {
        return new AbortStatement();
      } else if (line.equals("QUIT;")) {
        return new QuitStatement();
      } else if (line.equals("HELP;")) {
        return new HelpStatement();
      } else if (line.equals("VERBATIM QUERY")) {
        return new VerbatimStatement(true, readVerbatimStatement());
      } else if (line.equals("VERBATIM OTHER")) {
        return new VerbatimStatement(false, readVerbatimStatement());
      } else {
        StringBuffer    buffer = new StringBuffer();

        for (;;) {
          buffer.append("\n");
          buffer.append(line);
          if (line.lastIndexOf(";") != -1) {
            break;
          }
          printPrompt(false);
          line = input.readLine();
        }
        return new SqlStatement(buffer.toString().trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }      

  /*
   * Reads until a line with 
   */
  private String readVerbatimStatement() throws IOException {
    StringBuffer    buffer = new StringBuffer();

    for (;;) {
      String    line;

      printPrompt(false);
      line = input.readLine();
      if (END_OF_VERBATIM_TEXT_MARKER.equals(line)) {
        break;
      }
      if (line != null) {
        if (buffer.length() != 0) {
          buffer.append("\n");
        }
        buffer.append(line);
      }
    }

    return buffer.toString();
  }

  /**
   *
   */
  private com.kopiright.xkopi.comp.sqlc.Statement parseStatement(String line)
    throws PositionedError, UnpositionedError
  {
    // LEXICAL ANALYSIS
    InputBuffer buffer;

    buffer = new InputBuffer("NO NAME", new StringReader(line));

    // SYNTACTIC ANALYSIS
    DbiParser                   parser;
    com.kopiright.xkopi.comp.sqlc.Statement       statement = null;
    long                        lastTime = System.currentTimeMillis();

    parser = new DbiParser(this, buffer);

    try {
      statement = parser.statement();
    } catch (ParserException e) {
      reportTrouble(parser.beautifyParseError(e));
      if (options.abortonerror) {
        throw parser.beautifyParseError(e);
      }
    }

    if (verboseMode()) {
      inform(CompilerMessages.FILE_PARSED,
             "NO PATH",
             new Long(System.currentTimeMillis() - lastTime));
    }

    try {
      buffer.close();
    } catch (IOException e) {
      UnpositionedError error =
        new UnpositionedError(CompilerMessages.IO_EXCEPTION,
                              "NO PATH",
                              e.getMessage());
      reportTrouble(error);
      if (options.abortonerror) {
        throw error;
      }
    }

    return statement;
  }

  /**
   * Execute the specified SQL code
   */
  private void executeQuery(String sqlCode)
    throws SQLException
  {
    Statement           stmt = connection.createStatement();
    ResultSet           rset = stmt.executeQuery(sqlCode);
    ResultSetMetaData   meta = rset.getMetaData();
    int                 count = 0;

    // Print Header
    for (int column = 1; column <= meta.getColumnCount(); column++) {
      if (column > 1) {
        output.print(FIELD_SEPARATOR);
      }
      output.print(meta.getColumnName(column));
    }
    output.println();
    
    // Print tuples
    while (rset.next()) {
      for (int column = 1; column <= meta.getColumnCount(); column++) {
        Object      value = rset.getObject(column);
        String      printedValue;
        
        if (column > 1) {
          output.print(FIELD_SEPARATOR);
        }
        if (value != null) {
          switch (meta.getColumnType(column)) {
          case Types.CLOB:
          case Types.LONGVARCHAR:
            printedValue = "<CLOB>";
            break;
          case Types.BLOB:
          case Types.LONGVARBINARY:
            printedValue = "<BLOB>";
            break;
          default:
            printedValue = value.toString();
          }
        } else {
          printedValue = "NULL";
        }
        output.print(printedValue);
      }
      output.println();
      
      count ++;
    }

    // Print summary
    output.println();
    switch (count) {
    case 0:
      output.print("No row was");
      break;
    case 1:
      output.print("1 row was");
      break;
    default:
      output.print(count + " rows were");
    }
    output.println(" selected.");
    
    rset.close();
  }

  /**
   * Execute the specified SQL code
   */
  private void executeOther(String sqlCode)
    throws SQLException
  {
    Statement   stmt = connection.createStatement();
    int         count = -1;

    synchronized (this) {
      inTransaction = true;
      count = stmt.executeUpdate(sqlCode);
    }
        
    // Print summary
    output.println();
    if (count < 0) {
      output.println("There is a problem : return of executeUpdate is negative : " + count);
    } else {
      switch (count) {
      case 0:
        output.print("No row was");
        break;
      case 1:
        output.print("1 row was");
        break;
      default:
        output.print(count + " rows were");
      }
      output.println(" updated, deleted or inserted.");
    }
  }

  /**
   *
   */
  private void executeStatement(com.kopiright.xkopi.comp.sqlc.Statement statement)
    throws PositionedError, UnpositionedError
  {
    String              sqlCode = genSQLCode(statement);

    if (sqlCode.equals("")) {
      return;
    }

    try {
      if (statement instanceof SelectStatement) {
        executeQuery(sqlCode);
      } else if (statement instanceof com.kopiright.xkopi.comp.sqlc.DeleteStatement
                 || statement instanceof com.kopiright.xkopi.comp.sqlc.InsertStatement
                 || statement instanceof com.kopiright.xkopi.comp.sqlc.UpdateStatement) {
        executeOther(sqlCode);
      } else {
        // NO SELECT, NO INSERT, NO UPDATE, NO DELETE => DDL statements.
        executeOther(sqlCode);
      }
    } catch (SQLException e) {
      output.println("A database exception has occured while executing this statement: ");
      output.println(datasource.driver.convertException(e).getMessage());
      if (options.abortonerror) {
        throw new UnpositionedError(DbiMessages.SQL_EXCEPTION, e.getMessage());
      }
    }
  }


  /**
   * Generates SQL code.
   */
  protected final String genSQLCode(final com.kopiright.xkopi.comp.sqlc.Statement statement)
    throws PositionedError
  {
    statement.accept(datasource.checker);

    return datasource.checker.getStatementText(datasource.driver);
  }

  private void quit() {
    if (inTransaction) {
      boolean   end = false;

      while (!end) {
        output.print("A transaction is opened. abort or commit it ? [a/c] ? ");
        output.flush();
        try {
          String          line = input.readLine();

          if (line.equals("a")) {
            abortTransaction();
            end = true;
          } else if (line.equals("c")) {
            commitTransaction();
            end = true;
          }
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  private void help() {
    output.println("\tType your DDL or DML SQL statement ended with a ;");
    output.println("\tCommit your modifications with COMMIT;");
    output.println("\tAbort your modifications with ABORT;");
    output.println("\tQuit the console with QUIT;");
    output.println();
  }

  private void commitTransaction() {
    synchronized(this) {
      try {
        connection.commit();
        inTransaction = false;
      } catch (SQLException e) {
        output.println("Error while committing the transaction : " + e.getMessage());
      }
    }
  }

  private void abortTransaction() {
    synchronized(this) {
      try {
        connection.rollback();
        inTransaction = false;
      } catch (SQLException e) {
        output.println("Error while committing the transaction : " + e.getMessage());
      }
    }
  }

  /**
   * Entry point
   */
  public static void main(String[] args) {
    boolean     success;

    try {
      success = new Console().run(args);
    } catch (RuntimeException re) {
      re.printStackTrace();
      success = false;
    }

    System.exit(success ? 0 : 1);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ConsoleOptions        options;
  private BufferedReader        input;
  private PrintWriter           output;

  private Connection            connection;
  private DbiDataSource         datasource;
  private List                  infiles;
  private boolean               inTransaction;

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  private static final class DbiDataSourceFactory {

    // Nobody can construct this factory
    private DbiDataSourceFactory() {
    }

    public static DbiDataSource create(String name) {
      DbiDataSource dataSource;

      if (name.equals("sap")) {
        dataSource = new SapDBDataSource();
      } else if (name.equals("tbx")) {
        dataSource = new TbxDataSource();
      } else if (name.equals("pg")) {
        dataSource = new PostgresDataSource();
      } else if (name.equals("ora")) {
        dataSource = new OracleDataSource();
      } else if (name.equals("ora10g")) {
        dataSource = new Oracle10gDataSource();
      } else if (name.equals("mysql")) {
        return new MysqlDataSource();
      } else if (name.equals("ingres")) {
        return new IngresDataSource();
      } else {
        throw new IllegalArgumentException("No data source corresponding to '"
                                           + name + "'");
      }
      dataSource.setTrace(trace);
      return dataSource;
    }
  }

  private abstract static class DbiDataSource {
    protected DbiDataSource(String driver,
                            String startURL,
                            String syntax)
    {
      this.startURL = startURL;

      try {
        Class.forName(driver);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("loading driver '" + driver + "' failed");
      }
      this.checker = DbiChecker.create(syntax, sqlContext);
      this.driver = checker.getDriverInterface();
    }
    
    public void setTrace(boolean trace) {
      driver.setTrace(trace);
    }
    
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    public final DbiChecker             checker;
    public final DriverInterface        driver;
    public final String                 startURL;
    public static final SqlContext      sqlContext = new SqlContext() {
        /**
         * Returns the table reference with alias "alias"
         */
        public TableReference getTableFromAlias(String alias) {
          return null;
        }
        
        /**
         * Returns all tables defined in current context
         */
        public ArrayList getTables() {
          return new ArrayList();
        }

        /**
         * Returns the parent context
         */
        public SqlContext getParentContext() {
          return null;
        }
        
        /**
         * Returns the type context
         */
        public CTypeContext getTypeContext() {
          return null;
        }
        
        /**
         * Reports a trouble (error or warning).
         *
         * @param       trouble         a description of the trouble to report.
         */
        public void reportTrouble(PositionedError trouble) {
          System.err.println(trouble.getMessage());
        }
      };
  }

  private static final class SapDBDataSource extends DbiDataSource {
    SapDBDataSource() {
      super("com.sap.dbtech.jdbc.DriverSapDB",
            "jdbc:sapdb:",
            "sap");
    }
  }

  private abstract static class KConnectDataSource extends DbiDataSource {
    KConnectDataSource(String dbms, String syntax) {
      super("com.kopiright.kconnect.Driver",
            "jdbc:kconnect:" + dbms + ":",
            syntax);
    }
  }

  private static final class TbxDataSource extends KConnectDataSource {
    TbxDataSource() {
      super("tb", "tbx");
    }
  }

  private static final class OracleDataSource extends KConnectDataSource {
    OracleDataSource() {
      super("ora", "ora");
    }
  }

  private static final class Oracle10gDataSource extends DbiDataSource {
    Oracle10gDataSource() {
      super("oracle.jdbc.driver.OracleDriver",
            "jdbc:oracle:thin:@",
            "ora");
    }
  }

  private static final class PostgresDataSource extends DbiDataSource {
    PostgresDataSource() {
      super("org.postgresql.Driver",
            "jdbc:postgesql:",
            "pgsql");
    }
  }

  private static final class MysqlDataSource extends DbiDataSource {
    MysqlDataSource() {
      super("com.mysql.jdbc.Driver",
            "jdbc:mysql:",
            "mysql");
    }
  }

  private static final class IngresDataSource extends DbiDataSource {
    IngresDataSource() {
      super("ca.edbc.jdbc.EdbcDriver",
            "jdbc:edbc:",
            "ingres");
    }
  }

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  private abstract class AbstractStatement {
    public abstract boolean execute() throws PositionedError, UnpositionedError, SQLException;
  }

  private class AbortStatement extends AbstractStatement {
    public boolean execute() {
      abortTransaction();
      return false;
    }
  }

  private class CommitStatement extends AbstractStatement {
    public boolean execute() {
      commitTransaction();
      return false;
    }
  }

  private class HelpStatement extends AbstractStatement {
    public boolean execute() {
      help();
      return false;
    }
  }

  private class QuitStatement extends AbstractStatement {
    public boolean execute() {
      quit();
      return true;
    }
  }

  private class SqlStatement extends AbstractStatement {
    public SqlStatement(String sqlText) {
      this.sqlText = sqlText;
    }

    public boolean execute() throws PositionedError, UnpositionedError, SQLException {
      com.kopiright.xkopi.comp.sqlc.Statement     stmt;
          
      stmt = parseStatement(sqlText);
      if (stmt != null) {
        executeStatement(stmt);
      }
      return false;
    }

    private final String        sqlText;
  }

  /**
   * Execute SQL code without preprocessing
   */
  private class VerbatimStatement extends AbstractStatement {
    public VerbatimStatement(boolean isQuery, String sqlText) {
      this.isQuery = isQuery;
      this.sqlText = sqlText;
    }

    public boolean execute() throws SQLException {
      if (isQuery) {
        executeQuery(sqlText);
      } else {
        executeOther(sqlText);
      }
      return false;
    }

    private final boolean       isQuery;
    private final String        sqlText;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private static final String           END_OF_VERBATIM_TEXT_MARKER = "//";
  private static final char             FIELD_SEPARATOR = '\t';
  private static boolean                trace = false;
}
