/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * $Id: Console.java,v 1.1 2004/07/28 18:43:28 imad Exp $
 */

package at.dms.xkopi.comp.dbi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.ArrayList;

import at.dms.kopi.comp.kjc.CTypeContext;
import at.dms.xkopi.comp.sqlc.SelectStatement;
import at.dms.xkopi.comp.sqlc.SqlContext;
import at.dms.xkopi.comp.sqlc.TableReference;
import at.dms.compiler.base.Compiler;
import at.dms.compiler.base.CompilerMessages;
import at.dms.compiler.base.PositionedError;
import at.dms.compiler.base.TokenReference;
import at.dms.compiler.base.UnpositionedError;
import at.dms.compiler.tools.antlr.extra.InputBuffer;
import at.dms.compiler.tools.antlr.runtime.ParserException;
import at.dms.util.base.Utils;
import at.dms.xkopi.lib.base.DriverInterface;

/**
 * This class is a console which allows to write in Dbi syntax to
 * databases.
 */
public class Console extends Compiler implements Constants {

  private Console() {
    super(null, null);

    infiles = new Vector();
  }

  // --------------------------------------------------------------------
  // METHODS INHERITED FROM Compiler
  // --------------------------------------------------------------------

  /**
   * Runs a compilation session
   *
   * @param	args		the arguments to the compiler
   * @return	true iff the compilation succeeded
   */
  public boolean run(String[] args) {
    if (!parseArguments(args)) {
      return false;
    }

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

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
   */
  public void reportTrouble(PositionedError trouble) {
    inform(trouble);
  }

  /**
   * Reports a trouble (error or warning).
   *
   * @param	trouble		a description of the trouble to report.
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
      datasource = DbiDataSourceFactory.create(options.datasource);

      try {
        if (options.host != null
            && options.dbname != null
            && options.login != null)
          {
            final String  url = datasource.startURL + "//" + options.host + "/" + options.dbname;

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
        String    statement;

        printPrompt(true);
        statement = readStatement();
        if (statement.equals("COMMIT;")) {
          commitTransaction();
        } else if (statement.equals("ABORT;")) {
          abortTransaction();
        } else if (statement.equals("QUIT;")) {
          quit();
          quit = true;
        } else if (statement.equals("HELP;")) {
          help();
        } else {
          at.dms.xkopi.comp.sqlc.Statement     stmt = parseStatement(statement);

          if (stmt != null) {
            executeStatement(stmt);
          }
        }
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
  private String readStatement() {
    StringBuffer      statement = new StringBuffer();

    try {
      boolean   statementEnded = false;

      while (!statementEnded) {
        String          readLine;

        readLine = input.readLine();
        if (readLine != null) {
          statement.append("\n");
          statement.append(readLine);

          statementEnded = (statement.lastIndexOf(";") != -1);
          if (!statementEnded) {
            printPrompt(statementEnded);
          }
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    return statement.toString().trim();
  }


  /**
   *
   */
  private at.dms.xkopi.comp.sqlc.Statement parseStatement(String line)
    throws PositionedError, UnpositionedError
  {
    // LEXICAL ANALYSIS
    InputBuffer buffer;

    buffer = new InputBuffer("NO NAME", new StringReader(line));

    // SYNTACTIC ANALYSIS
    DbiParser                   parser;
    at.dms.xkopi.comp.sqlc.Statement       statement = null;
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
   *
   */
  private void executeStatement(at.dms.xkopi.comp.sqlc.Statement statement)
    throws PositionedError, UnpositionedError
  {
    try {
      Statement         stmt = connection.createStatement();
      final String      sqlCode = genSQLCode(statement);

      if (statement instanceof SelectStatement) {
        ResultSet               rset = stmt.executeQuery(sqlCode);
        ResultSetMetaData       rsmd = rset.getMetaData();
        int                     count = 0;

        // Print Header
        for (int column = 1; column <= rsmd.getColumnCount(); column++) {
          output.print(rsmd.getColumnName(column));
          output.print(";");
        }
        output.println();

        // Print tuples
        while(rset.next()) {
          String      printedValue = null;

          for (int column = 1; column <= rsmd.getColumnCount(); column++) {
            Object    value = rset.getObject(column);

            if (value != null) {
              switch (rsmd.getColumnType(column)) {
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
            output.print(printedValue + ";");
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
      } else if (statement instanceof at.dms.xkopi.comp.sqlc.DeleteStatement
                 || statement instanceof at.dms.xkopi.comp.sqlc.InsertStatement
                 || statement instanceof at.dms.xkopi.comp.sqlc.UpdateStatement) {
        int     count = -1;

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
      } else {
        // NO SELECT, NO INSERT, NO UPDATE, NO DELETE => DDL statements.
        synchronized (this) {
          inTransaction = true;
          stmt.executeUpdate(sqlCode);
        }
      }
    } catch (SQLException e) {
      output.println("A database exception has occured while executing this statement : ");
      output.println(datasource.driver.convertException(e).getMessage());
      if (options.abortonerror) {
        throw new UnpositionedError(DbiMessages.SQL_EXCEPTION,
                                    e.getMessage());
      }
    }
  }


  /**
   * Generates SQL code.
   */
  protected final String genSQLCode(final at.dms.xkopi.comp.sqlc.Statement statement)
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
    boolean	success;

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
      if (name.equals("sap")) {
        return new SapDBDataSource();
      } else if (name.equals("tbx")) {
        return new TbxDataSource();
      } else if (name.equals("pg")) {
        return new PostgresDataSource();
      } else {
        throw new IllegalArgumentException("No data source corresponding to " + name);
      }
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
         * @param	trouble		a description of the trouble to report.
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
    KConnectDataSource(String database, String syntax) {
      super("at.dms.kconnect.Driver",
            "jdbc:kconnect:" + database + ":",
            syntax);
    }
  }

  private static final class TbxDataSource extends KConnectDataSource {
    TbxDataSource() {
      super("tb", "tbx");
    }
  }

  private static final class PostgresDataSource extends DbiDataSource {
    PostgresDataSource() {
      super("org.postgresql.Driver",
            "jdbc:postgesql:",
            "pgsql");
    }
  }

}
