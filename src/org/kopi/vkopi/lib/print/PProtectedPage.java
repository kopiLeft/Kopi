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

package org.kopi.vkopi.lib.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.kopi.util.base.InconsistencyException;
import org.kopi.vkopi.lib.base.Utils;
import org.kopi.vkopi.lib.util.PrintException;
import org.kopi.vkopi.lib.util.PrintJob;
import org.kopi.vkopi.lib.visual.Message;
import org.kopi.vkopi.lib.visual.MessageCode;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VExecFailedException;
import org.kopi.vkopi.lib.visual.VRuntimeException;
import org.kopi.xkopi.lib.base.DBContext;
import org.kopi.xkopi.lib.base.DBContextHandler;
import org.kopi.xkopi.lib.base.DBDeadLockException;
import org.kopi.xkopi.lib.base.DBInterruptionException;
import org.kopi.xkopi.lib.type.Fixed;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Handle the generation of a document
 */
public abstract class PProtectedPage extends PPage implements DBContextHandler, Printable {

  /**
   * Construct a new Page description
   */
  public PProtectedPage(DBContextHandler handler, String message) {
    this.message = message;
    this.handler = handler;
  }
  public PProtectedPage(DBContextHandler handler) {
    this(handler, Message.getMessage("printing"));
  }
  /**
   * @deprecated
   */
  public PProtectedPage() {
    this(null);
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
   * Starts a print session on a file
   */
  public PrintJob createPrintJob(DBContext context) {
    return createPrintJob(context, Message.getMessage("printing"));
  }

  /**
   * Starts a print session with a printer
   * @deprecated
   */
  public PrintJob createPrintJob(DBContextHandler handler, String message) {
    this.handler = handler;
    this.message = message;
    return super.createPrintJob();
  }

  /**
   * Starts a print session on a file
   */
  public PrintJob createPrintJob(DBContext context, String message) {
    this.context = context;
    this.message = message;
    return super.createPrintJob();
  }

  /**
   *
   */
  protected void printBlocks(PdfPrintJob printjob) throws PSPrintException {
    if (!inTransaction()) {
      try {
	startProtected(message);
	initPage();
	super.printBlocks(printjob);
	closePage();
	commitProtected();
      } catch (SQLException e) {
	try {
	  abortProtected(e);
	} catch (DBDeadLockException ignored) {
          throw new VRuntimeException(MessageCode.getMessage("VIS-00058"));
	} catch (DBInterruptionException ignored) {
          throw new VRuntimeException(MessageCode.getMessage("VIS-00058"));
	} catch (SQLException ignored) {
          throw new InconsistencyException(ignored);
	}
      } catch (PSPrintException e) {
	try {
	  abortProtected((SQLException) (new SQLException(e.getMessage())));
	} catch (SQLException ignored) {
          throw new VRuntimeException(MessageCode.getMessage("VIS-00058")
                                      + ": " + ignored.getMessage());
	}
      } catch (Throwable e) {
	try {
	  abortProtected((SQLException) (new SQLException().initCause(e)));
	} catch (SQLException ignored) {
          throw new VRuntimeException(MessageCode.getMessage("VIS-00058")
                                      + ": " + ignored.getMessage());
	}
      }
    } else {
      try {
	initPage();
	super.printBlocks(printjob);
	closePage();
      } catch (Exception e) {
	PPage.fatalError(this, "PProtectedPage.printBlocks():2", e);
	throw new InconsistencyException(e.getMessage());
      }
    }
  }

  public void initPage() throws SQLException, PSPrintException {}

  public void closePage() throws SQLException {}

  public static Printable printableForReports(PProtectedPage[] reports, boolean restartPageNumer) {
    return new MultiReportPrintable(reports, restartPageNumer);
  }

  static class MultiReportPrintable implements Printable {
    public MultiReportPrintable(PProtectedPage[] reports, boolean restartPageNumer) {
      this.reports = reports;
      this.restartPageNumer = restartPageNumer;
    }

    public void print(org.kopi.vkopi.lib.util.Printer printer) throws PrintException, VException {
      try {
//         PrintTask         pt = printer.print(createPrintJob());

//         if (pt != null) {
//           pt.print();
//         } 
        printer.print(createPrintJob());
      } catch (IOException e) {
        throw new VExecFailedException(e);
      }
    }
    /**
     * Print the object
     * @param	printer	where to print
     */
    public PrintJob createPrintJob() throws PrintException, VException {
      PdfPrintJob        printJob;
      PdfPrintJob        pdfPrintJob;

      printJob = reports[0].printProlog();
      
      for (int i = 0; i < reports.length; i++) {
         // add report to print job
         reports[i].continuePrinting(printJob, restartPageNumer);
      }
      // end pdf document
      printJob.close();

      try {
        File              file = Utils.getTempFile("kopi", "pdf");
        PdfReader         reader = new PdfReader(new FileInputStream(printJob.getDataFile()));
        PdfStamper        stamper = new PdfStamper(reader, new FileOutputStream(file));
        int               startpage = 1;

        for (int i = 0; i < reports.length; i++) {
          // add report to print job
          startpage = reports[i].continuePrinting(stamper, restartPageNumer, startpage, printJob.getNumberOfPages());
        }

        stamper.close();
        pdfPrintJob = new PdfPrintJob(file, printJob.getFormat());
        pdfPrintJob.setTitle(reports[0].getTitle());
        return pdfPrintJob;
      } catch (Exception e) {
        throw new InconsistencyException(e);
      }
    }

    PProtectedPage[]    reports;
    boolean             restartPageNumer;
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF DBCONTEXTHANDLER
  // ----------------------------------------------------------------------

  /**
   * @return	the database context for this object
   */
  public DBContext getDBContext() {
    if (handler != null) {
      return handler.getDBContext();
    } else {
      return context;
    }
  }

  /**
   * Sets the database context for this object
   *
   * @param	context		the database context for this object
   */
  public void setDBContext(DBContext context) {
    this.context = context;
    if (handler != null) {
      handler.setDBContext(context);
    }
  }

  /**
   * @return	the database context handler for this object
   */
  public DBContextHandler getDBContextHandler() {
    return handler;
  }

  /**
   * Sets the database context for this object
   *
   * @param	context		the database context handler for this object
   */
  public void setDBContextHandler(DBContextHandler handler) {
    this.handler = handler;
  }

  /**
   * Starts a protected transaction.
   *
   * @param	message		the message to be displayed
   */
  public void startProtected(String message) {
    if (handler != null) {
      handler.startProtected(message);
    } else {
      inTransaction = true;
      context.startWork();
      System.err.println("Start protected: " + message);
    }
  }

  /**
   * Commits a protected transaction.
   */
  public void commitProtected() throws SQLException {
    if (handler != null) {
      handler.commitProtected();
    } else {
      inTransaction = false;
      context.commitWork();
      System.err.println("Commit protected");
    }
  }

  /**
   * Aborts a protected transaction.
   *
   * @param    reason          the reason for the failure
   */
  public void abortProtected(SQLException reason) throws SQLException {
    if (handler != null) {
      handler.abortProtected(false);
      if (!handler.retryableAbort(reason) || !handler.retryProtected()) {
        throw reason;
      }
    } else {
      inTransaction = false;
      context.abortWork();
      throw reason;
    }
  }

  public void abortProtected(boolean interrupt) {
    if (handler != null) {
      handler.abortProtected(interrupt);
    } else {
      inTransaction = false;
      try {
        context.abortWork();
      } catch (SQLException e) {
        throw new InconsistencyException("Can not abort work", e);
      }
    }
  }

  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  public boolean retryableAbort(Exception reason) {
    if (handler != null) {
      return handler.retryableAbort(reason);
    } else {
      return false;
    }
  }

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  public boolean retryProtected() {
    if (handler != null) {
      return handler.retryProtected();
    } else {
      return false;
    }
  }
  /**
   * @return	whether or not this object handles a transaction at this time
   */
  public boolean inTransaction() {
    if (handler != null) {
      return handler.inTransaction();
    } else {
      return inTransaction;
    }
  }



  public  Fixed formatFixed(Fixed f,int currentScale , int maxScale) {
    if (f == null) {
      return null;
    }
    f = f.setScale(currentScale, java.math.BigDecimal.ROUND_HALF_UP);
    f.setMaxScale(maxScale);
    return f;
  }

  // ---------------------------------------------------------------------
  // DATA MEMBERS
  // ---------------------------------------------------------------------

  private DBContext		context;
  private boolean		inTransaction;
  private String		message;
  private DBContextHandler	handler;
  private String                media;
}
