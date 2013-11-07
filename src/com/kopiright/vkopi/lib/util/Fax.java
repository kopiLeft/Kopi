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

package com.kopiright.vkopi.lib.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * @deprecated  replaced by the class HylaFAXUtils
 */
public class Fax {

  // ----------------------------------------------------------------------
  // CONVENIENCE METHODS TO SEND FAX, GET QUEUE STATUS, ...
  // ----------------------------------------------------------------------

  /**
   * Sends a fax
   */
  public static void fax(String host,
			 InputStream is,
			 String user,
			 String nummer,
			 String jobId)
    throws IOException, PROTOException
  {
    fax(HFAX_PORT, host, is, user, nummer, jobId);
  }

  /**
   * Sends a fax
   */
  public static void fax(int port,
			 String host,
			 InputStream is,
			 String user,
			 String nummer,
			 String jobId)
    throws IOException, PROTOException
  {
    Fax         fax = new Fax(port, host);
    String      filename;

    fax.login(user);
    fax.command("IDLE 900");
    fax.command("TZONE LOCAL");
    filename = fax.sendbuffer(is);
    fax.setNewJob(nummer, user, jobId);
    fax.command("JPARM DOCUMENT " + filename);
    fax.command("JSUBM");
    fax.endCon();
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE SEND QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  public static Vector<FaxStatus> readSendQueue(String host, String user) throws FaxException {
    return readQueue(host, user, "sendq");
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE DONE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  public static Vector<FaxStatus> readDoneQueue(String host, String user) throws FaxException {
    return readQueue(host, user, "doneq");
  }

  /*
   * ----------------------------------------------------------------------
   * READ THE RECEIVE QUEUE
   * RETURNS A VECTOR OF FAXSTATUS
   * ----------------------------------------------------------------------
   */
  public static Vector<FaxStatus> readRecQueue(String host, String user) throws FaxException {
    return readQueue(host, user, "recvq");
  }


  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */
  public static Vector<String> readServerState(String host, String user) throws FaxException {
    Vector<String>	queue = new Vector<String>();
    try {
      String            ret = getQueue(HFAX_PORT, host, user, "status");
      StringTokenizer   token = new StringTokenizer(ret, "\n");

      Utils.log("Fax", "READ STATE : host " + host + " / user " + user);

      while (token.hasMoreElements()) {
        queue.addElement(token.nextElement().toString());
      }
    } catch (ConnectException e) {
      throw new FaxException("NO FAX SERVER");
    } catch (Exception e) {
      throw new FaxException("Trying read server state: " + e.getMessage(), e);
    }

    return queue;
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE SERVER AND MODEM STATE
   * ----------------------------------------------------------------------
   */
  public static String readSendtime(String jobId) {
    return null;
  }

  /**
   * Convenience method
   */
  public static void killJob(String host, String user, String job) throws IOException, PROTOException {
    killJob(HFAX_PORT, host, user, job);
  }

  /**
   * Convenience method
   */
  public static void killJob(int port,
			     String host,
			     String user,
			     String job)
    throws IOException, PROTOException
  {
    Fax         fax = new Fax(port, host);

    fax.login(DEFAULT_USER); // !!! laurent 20020626 : why DEFAULT_USER and not user ?
    fax.command("JKILL " + job);
    Utils.log("Fax", "Kill 1: " + job);
    fax.endCon();
  }

  /**
   * Convenience method
   */
  public static void clearJob(String host,
                              String user,
                              String job)
    throws IOException, PROTOException
  {
    clearJob(HFAX_PORT, host, user, job);
  }

  /**
   * Convenience method
   */
  public static void clearJob(int port,
			      String host,
			      String user,
			      String job)
    throws IOException, PROTOException
  {
    Fax         fax = new Fax(port, host);

    fax.login(DEFAULT_USER); // !!! laurent 20020626 : why DEFAULT_USER and not user ?
    fax.command("JDELE " + job);
    Utils.log("Fax", "Delete 1: " + job);
    fax.endCon();
  }

  /*
   * ----------------------------------------------------------------------
   * HANDLE THE QUEUES --- ALL QUEUES ARE HANDLED BY THAT METHOD
   * ----------------------------------------------------------------------
   */
  private static String getQueue(int port, String host, String user, String qname)
    throws IOException, PROTOException
  {
    Fax         fax = new Fax(port, host);
    String      ret;     // Hole Statusinformationen

    fax.login(user);
    fax.command("IDLE 900");
    fax.command("TZONE LOCAL");
    fax.command("JOBFMT \" %j| %J| %o| %e| %a| %P| %D| %s\"");
    fax.command("RCVFMT \" %f| %t| %s| %p| %h| %e\"");
    fax.command("MDMFMT \"Modem %m (%n): %s\"");
    ret = fax.infoS(qname);
    fax.endCon();

    return ret;
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  public Fax(int port, String host) throws IOException, PROTOException {
    if (port == 0) {
      port = HFAX_PORT;
    }
    if (host == null) {
      host = HFAX_HOST;
    }

    this.host = host;

    // Socket erzeugen
    clnt = new Socket(host, port);

    // I/O Streams erzeugen
    clntIn = new BufferedReader(new InputStreamReader(clnt.getInputStream()));
    clntOut = new PrintWriter(clnt.getOutputStream());

    check(readLine());
  }

  public int login(String uname) throws IOException, PROTOException {
    int         answer;

    Utils.log("Fax", "login:" + uname);

    print("USER " + uname + "\n");
    clntOut.flush();
    answer = check(readLine());
    if (answer == NEEDS_PASSWD) {
      print("PASS " + 0 + "\n");
      clntOut.flush();
    }
    return answer;
  }

  // Verbindung schliessen
  public void endCon() throws IOException, PROTOException {
    print("QUIT" + "\n");
    clntOut.flush();

    check(readLine());
    if (clnt != null) {
      clntIn.close();
      clntOut.close();
      clnt.close();
    }
  }

  public String sendbuffer(InputStream is) throws IOException, PROTOException {
    // Diese Funktion sendet das Byte-Array buf an den Server
    SendServ    sndsrv;
    String      pstr;
    byte[]      iaddr;

    Deflater                    df = new Deflater(9, false);
    ByteArrayOutputStream       baos = new ByteArrayOutputStream();
    DeflaterOutputStream        dos = new DeflaterOutputStream(baos, df, is.available());

    byte[]	buffer = new byte[1024];
    int         read;

    while ((read = is.read(buffer, 0, 1024)) != -1) {
      dos.write(buffer, 0, read);
    }
    dos.close();

    // Erzeugen des SendServ-Threads
    //sndsrv = new SendServ(buf, debug);
    sndsrv = new SendServ(baos.toByteArray(), debug);

    iaddr = getInetAddr();
    pstr = makePORT(iaddr, sndsrv.port);

    print("TYPE I" + "\n"); // Binaer
    clntOut.flush();
    check(readLine());

    print("MODE Z" + "\n"); // ZIP
    //print("MODE S" + "\n"); // Stream
    clntOut.flush();
    check(readLine());

    print("PORT " + pstr + "\n");
    System.err.println("PORT " + pstr + "\n");
    clntOut.flush();
    check(readLine());

    print("STOT" + "\n");
    clntOut.flush();
    String line = readLine();
    check(line);

    // Auf Beendigung des Threads warten
    try {
      sndsrv.join();
    } catch (InterruptedException e) {}

    StringTokenizer     st = new StringTokenizer(line, " ");
    st.nextToken();
    st.nextToken();
    String filename = st.nextToken();
    check(readLine());

    // Zurueckgegeben wird der Dateiname, unter dem der Server
    // den Buffer gespeichert hat
    return filename;
  }

  public byte[] getReceived(String name) throws IOException, PROTOException {
    // Diese Funktion gibt in einem Byte-Array die angeforderte
    // Datei name zurueck. Diese Datei muss sich innerhalb des
    // recvq Verzeichnisses des Servers befinden

    String      pstr;   // String für port
    byte[]      iaddr;  // byte-array für Internetadresse
    RecvServ    recsrv; // server-thread-object

    // Thread erzeugen
    recsrv = new RecvServ(debug);

    iaddr = getInetAddr();
    pstr = makePORT(iaddr, recsrv.port);

    if (debug) {
      System.out.println("Fax.getReceived: " + pstr);
    }

    print("TYPE I" + "\n");
    clntOut.flush();
    check(readLine());

    print("MODE S" + "\n");
    //print("MODE Z" + "\n");
    clntOut.flush();
    check(readLine());

    print("CWD recvq" + "\n");  // in das recvq Verzeichnis wechseln
    clntOut.flush();
    check(readLine());

    print("PORT " + pstr + "\n");
    clntOut.flush();
    check(readLine());

    print("RETR " + name + "\n");
    clntOut.flush();
    check(readLine());
    check(readLine());

    // Auf Beendigung des Threads warten
    try {
      recsrv.join();
    } catch (InterruptedException e) {}

    print("CWD" + "\n"); // zurueck nach Serverroot
    clntOut.flush();
    check(readLine());

    return recsrv.data;
  }

  /**
   * Diese Funktion gibt den Inhalt des mit what angegebenen
   * Verzeichnisses als String zurueck.
   */
  public String infoS(String what) throws PROTOException, IOException {
    String      pstr;   // String für port
    byte[]      iaddr;  // byte-array für Internetadresse
    RecvServ    recsrv; // server-thread-object

    // Thread erzeugen
    recsrv = new RecvServ(debug);

    iaddr = getInetAddr();
    pstr = makePORT(iaddr, recsrv.port);

    print("PORT " + pstr + "\n");
    clntOut.flush();
    check(readLine());

    print("LIST " + what + "\n");
    clntOut.flush();

    // Auf Beendigung des Threads warten
    try {
      recsrv.join();
    } catch (InterruptedException e) {}

    if (typeOfThread != NONE) {
      throw new PROTOException(errText, LOST_CONNECTION);
    }

    if (check(readLine()) == ABOUT_TO_OPEN_DATACON) {
      check(readLine());
    } else {
      throw new PROTOException("Fax.infoS: No Data from Faxserver", 1);
    }

    int         cnt; // Anzahl der Zeichen zaehlen
    for (cnt = 0; cnt < recsrv.data.length; cnt++) {
      if (recsrv.data[cnt] == 0) {
	break;
      }
    }

    //hibyte - the top 8 bits of each 16-bit Unicode character
    return new String(recsrv.data, 0, cnt);
  }

  public String command(String what) throws IOException, PROTOException {
    /*
     * Sendet ein Kommando an den Faxserver und
     * gibt die Antwort zurueck oder erzeugt ein
     * Ausnahmeobjekt
     */

    StringBuffer        response = new StringBuffer();
    String              line;

    print(what + "\n");
    clntOut.flush();

    int erg = check(readLine());

    // SYSTEM_STATUS und HELP_MESSAGE sind laenger als
    // eine Zeile
    if (erg == SYSTEM_STATUS || erg == HELP_MESSAGE) {
      while (true) {
	line = readLine();
	if (check(line) == erg) {
	  break;
	}
	response.append(line + "\n");
      }
    }

    return response.toString();
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /*
   * ----------------------------------------------------------------------
   * READS ANY QUEUE
   * RETURNS A VECTOR OF STRINGS
   * ----------------------------------------------------------------------
   */
  private static Vector<FaxStatus> readQueue(String host, String user, String qname) throws FaxException  {
    Vector<FaxStatus>	queue = new Vector<FaxStatus>();

    try {
      String		ret = getQueue(HFAX_PORT, host, user, qname);
      StringTokenizer   token = new StringTokenizer(ret, "\n");

      Utils.log("Fax", "READ " + qname + " : host " + host + " / user " + user);

      while (token.hasMoreElements()) {
        try {
          String                str = token.nextElement().toString();
	  StringTokenizer       prozess = new StringTokenizer(str, "|");

	  if (!qname.equals("recvq")) {
            queue.addElement(new FaxStatus(prozess.nextToken().trim(),	// ID
                                           prozess.nextToken().trim(),	// TAG
                                           prozess.nextToken().trim(),	// USER
                                           prozess.nextToken().trim(),	// DIALNO
                                           prozess.nextToken().trim(),	// STATE (CODE)
                                           prozess.nextToken().trim(),	// PAGES
                                           prozess.nextToken().trim(),	// DIALS
                                           prozess.nextToken().trim()));	// STATE (TEXT)
	  } else {
            queue.addElement(new FaxStatus(prozess.nextToken().trim(),	// FILENAME %f
                                           prozess.nextToken().trim(),	// TIME IN %t
                                           prozess.nextToken().trim(),	// SENDER %s
                                           prozess.nextToken().trim(),	// PAGES %p
                                           prozess.nextToken().trim(),	// DURATION %h
                                           prozess.nextToken().trim()));	// ERRORTEXT %e
	  }
	} catch (Exception e) {
          throw new FaxException(e.getMessage(), e);
	}
      }
    } catch (ConnectException e) {
      Utils.log("Fax", "NO FAX SERVER");
      throw new FaxException("NO FAX SERVER");
    } catch (Exception e) {
      throw new FaxException(e.getMessage(), e);
    }

    return queue;
  }

  private void setNewJob(String number, String user, String id) throws IOException, PROTOException {
    // number check:
    number = checkNumber(number);

    user = DEFAULT_USER;
    Utils.log("Fax", "NEW JOB:" + id + " / user: " + user);

    // Jobparameter einstellen
    command("JNEW");
    command("JPARM FROMUSER \"" + user + "\"");
    command("JPARM LASTTIME 145959");
    command("JPARM MAXDIALS 3");
    command("JPARM MAXTRIES 3");
    command("JPARM SCHEDPRI 127");
    command("JPARM DIALSTRING \"" + number  + "\"");
    command("JPARM NOTIFYADDR \"" + user + "\"");
    command("JPARM JOBINFO \"" + id + "\"");
    command("JPARM VRES 196");
    command("JPARM PAGEWIDTH " + 209);
    command("JPARM PAGELENGTH " + 296);
    command("JPARM NOTIFY \"NONE\""); //1:mail when done
    command("JPARM PAGECHOP \"default\"");
    command("JPARM CHOPTHRESHOLD 3");
  }

  private String checkNumber(String number) {
    String	newNumber = "";

    for (int i = 0; i < number.length(); i++) {
      if (number.charAt(i) >= '0' && number.charAt(i) <= '9') {
	newNumber += number.charAt(i);
      }
    }

    return newNumber;
  }

  private String makePORT(byte[] iaddr, int port) {
    // unteres byte
    byte a = (byte)(port & 0xff);
    // oberes byte
    byte b = (byte)((port & 0xff00) >> 8);

    // Zusammensetzen des Strings
    return new String((0xff & iaddr[0]) + "," + (0xff & iaddr[1]) + "," +
		      (0xff & iaddr[2]) + "," + (0xff & iaddr[3]) + "," +
		      (0xff & b) + "," + (0xff & a));
  }

  private void print(String s) {
    if (verboseMode) {
      System.err.print("->" + s);
    }
    clntOut.print(s);
  }

  private String readLine() throws IOException {
    String      readLine = clntIn.readLine();

    if (verboseMode) {
      System.err.println(readLine);
    }

    return readLine;
  }

  private int check(String str) throws PROTOException {
    /*
     * check prueft die Antworten des Faxservers und reagiert
     * folgendermassen:
     *
     * - gibt die Nummer des Reply-Codes zurueck, wenn alles
     *   in Ordnung ist
     * - erzeugt ein Ausnahmeobjekt vom Typ PROTOException
     *   wenn ein fataler Fehler auftrat
     * - gibt 0 zurück, wenn str kein Reply-Code ist
     */

    String              delim;
    StringBuffer        message = new StringBuffer();
    int                 rtc;

    /* Wenn der Protokollserver beendet wird, waerend eine Verbindung
     * bestand, wird ein null-String geliefert */
    if (str == null) {
      throw new PROTOException("Fax.check: empty Reply String!!!",
			       EMPTY_REPLY_STRING);
    }

    if (str.charAt(3) == '-') {
      delim = new String("-");
    } else {
      delim = new String(" ");
    }

    StringTokenizer st = new StringTokenizer(str, delim);

    // Wenn str ein normaler String ist, gib 0 zurueck
    try {
      rtc = Integer.parseInt(st.nextToken());
    } catch (NumberFormatException e) {
      rtc = 0;
    }

    for (int i = st.countTokens(); i > 0; i--) {
      message.append(st.nextToken() + " ");
    }

    // Die folgenden Reply-Codes erzeugen ein Ausnahmeobject
    switch (rtc) {
    case SERVICE_NOT_AVAILABLE:
    case NO_DATA_CONNECTION:
    case CONNECTION_CLOSED:
    case FILE_ACTION_NOT_TAKEN:
    case ACTION_ABORTED_ERROR:
    case ACTION_NOT_TAKEN_SPACE:
    case SYNTAX_ERROR_COMMAND:
    case SYNTAX_ERROR_PARAMETER:
    case COMMAND_NOT_IMPLEMENTED:
    case BAD_COMMAND_SEQUENCE:
    case OPERATION_NOT_PERMITTET:
    case NOT_LOGGED_IN:
    case NEED_ACC_FOR_STORING:
    case ACTION_NOT_TAKEN:
    case ACTION_ABORTED_PAGETYPE:
    case FILE_ACTION_ABORTED:
    case FAILED_TO_KILL_JOB:
    case ACTION_NOT_TAKEN_NAME:
      throw new PROTOException(message.toString(), rtc);
    default:
      return rtc;
    }
  }

  private byte[] getInetAddr() throws UnknownHostException {
    byte[] iaddr;

    if (host.equalsIgnoreCase("localhost")) {
      // Localhost soll auch Localhost bleiben...
      iaddr = new byte[] { 127, 0, 0, 1 };
    } else {
      iaddr = InetAddress.getLocalHost().getAddress();
    }

    return iaddr;
  }

  protected static void fail(String msg, Exception e, int which) {
    // Wird vom RecServ aufgerufen, wenn im Thread ein Fehler auftritt
    System.err.println(msg + ": " + e);

    typeOfThread = which;
    errText      = msg + ": " + e;
  }

  // ----------------------------------------------------------------------
  // DATA CONSTANTS
  // ----------------------------------------------------------------------

  private static final int ABOUT_TO_OPEN_DATACON    = 150;
  private static final int SYSTEM_STATUS            = 211;
  private static final int HELP_MESSAGE             = 214;
  private static final int NEEDS_PASSWD             = 331;
  private static final int SERVICE_NOT_AVAILABLE    = 421;
  private static final int NO_DATA_CONNECTION       = 425;
  private static final int CONNECTION_CLOSED        = 426;
  private static final int FILE_ACTION_NOT_TAKEN    = 450;
  private static final int ACTION_ABORTED_ERROR     = 451;
  private static final int ACTION_NOT_TAKEN_SPACE   = 452;
  private static final int FAILED_TO_KILL_JOB       = 460;
  private static final int SYNTAX_ERROR_COMMAND     = 500;
  private static final int SYNTAX_ERROR_PARAMETER   = 501;
  private static final int COMMAND_NOT_IMPLEMENTED  = 502;
  private static final int BAD_COMMAND_SEQUENCE     = 503;
  private static final int OPERATION_NOT_PERMITTET  = 504;
  private static final int NOT_LOGGED_IN            = 530;
  private static final int NEED_ACC_FOR_STORING     = 532;
  private static final int ACTION_NOT_TAKEN         = 550;
  private static final int ACTION_ABORTED_PAGETYPE  = 551;
  private static final int FILE_ACTION_ABORTED      = 552;
  private static final int ACTION_NOT_TAKEN_NAME    = 553;

  // Eigene Reply-Codes
  private static final int LOST_CONNECTION          = -1;
  private static final int EMPTY_REPLY_STRING       = -2;

  private static final int       HFAX_PORT = 4559;
  private static final String    HFAX_HOST = "localhost";

  private static final String    DEFAULT_USER = "KOPI"; // !!! laurent : why is there a DEFAULT_USER ?

  protected static final int    NONE    = 0;
  protected static final int    RECEIVE = 1;
  protected static final int    SEND    = 2;

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private boolean               debug;

  private final Socket          clnt;
  private final BufferedReader  clntIn;
  private final PrintWriter     clntOut;
  private final String          host;

  private static int            typeOfThread = NONE;
  private static boolean        verboseMode;
  private static String         errText   = "";

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  /**
   * Definition einer eigenen Ausnahme Klasse
   * fuer die Protokollabhaengigen Fehler
   */
  public class PROTOException extends FaxException {

	public PROTOException(String s, int number) {
      super(s);
      this.number = number;
    }

    public String getMessage() {
      return super.getMessage() + " Replay Code: " + number;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    public final int number; // Diese Variable speichert den Reply-Code
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2616931043801263148L;
  }


  /**
   * Mother class the send and receive thread workers.
   */
  private abstract class BasicServ extends Thread {

    protected BasicServ(boolean debug) throws IOException {
      this.debug1 = debug;
      this.srv = new ServerSocket(0, TIMEOUT);
      // get next free port
      this.port = srv.getLocalPort();
      debug("BasicServ: port=" + port);
      this.start();
    }

    protected final void debug(String message) {
      if (debug1) {
        System.out.println(message);
      }
    }

    // ----------------------------------------------------------------------
    // DATA CONSTANTS
    // ----------------------------------------------------------------------

    private static final int TIMEOUT = 20; // in seconds

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private final boolean               debug1;

    protected final int                 port;
    protected final ServerSocket        srv;
  }

  /**
   * Die Klasse RecServ ist abgeleitet von der Klasse BasicServ.
   * Sie empfaengt Daten vom Protokoll Server
   */
  private class RecvServ extends BasicServ {

    public RecvServ(boolean debug) throws IOException {
      super(debug);
    }

    // thread body
    public void run() {
      DataInputStream     in;
      byte[]              buf = new byte[1024];

      try {
        debug("RecvServ.run: Baue Verbindung auf");
        Socket  srv_clnt = srv.accept();

        debug("RecvServ.run: Erzeuge InputStream");
        in = new DataInputStream(srv_clnt.getInputStream());

        debug("RecvServ.run: Warte auf Daten");

        // ByteArrayOutputStream verhaelt sich wie ein Stream
        // und laesst sich wunderbar in ein Byte-Array zurueckwandeln
        ByteArrayOutputStream     out = new ByteArrayOutputStream();
        int                       anz;

        while ((anz = in.read(buf)) > 0) {
          out.write(buf, 0, anz);
        }

        data = out.toByteArray();

        if (srv != null) {
          srv.close();
        }
      } catch (IOException e) {
        Fax.fail("RecvServ", e, Fax.RECEIVE);
      }

      debug("RecvServ.run: Thread beendet!");
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    protected byte[]      data;
  }

  /*
   * Die Klasse SendServ ist abgeleitet von der Klasse BasicServ.
   * Sie sendet Daten zum Protokoll Server
   */
  private class SendServ extends BasicServ {

    public SendServ(byte[] buf, boolean debug) throws IOException {
      super(debug);
      this.buf = buf;
    }

    // thread body
    public void run() {
      DataOutputStream    out;

      try {
        debug("SendServ.run: Baue Verbindung auf");
        Socket  srv_clnt = srv.accept();

        debug("SendServ.run: Erzeuge OutputStream");
        out = new DataOutputStream(srv_clnt.getOutputStream());

        out.write(buf, 0, buf.length);
        out.flush();

        srv_clnt.close();

        debug("SendServ.run: Gesendete Bytes=" + out.size());
        if (srv != null) {
          srv.close();
        }
      } catch (IOException e) {
        Fax.fail("Thread error", e, Fax.SEND);
      }

      debug("SendServ.run: Thread beendet!");
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private byte[]        buf;
  }

  public static void main(String[] argv) throws Exception  {
    Vector<FaxStatus>      vec = Fax.readDoneQueue("vie.kopiright.com", "KOPI");

    System.out.println(vec.size());
  }
}
