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

package at.dms.kopi.lib.assertion;

import java.io.PrintStream;
import java.io.PrintWriter;

public class PreconditionError extends AssertionError {

  public PreconditionError(Class clazz) {
    super();
    this.clazz = clazz;
  }

  public PreconditionError(Class clazz, Object detailMessage) {
    super(detailMessage);
    this.clazz = clazz;
  }

  public PreconditionError(Class clazz, boolean detailMessage) {
    this(clazz, "" +  detailMessage);
  }
 
  public PreconditionError(Class clazz, char detailMessage) {
    this(clazz, "" +  detailMessage);
  }
 
  public PreconditionError(Class clazz, int detailMessage) {
    this(clazz, "" +  detailMessage);
  }
 
  public PreconditionError(Class clazz, long detailMessage) {
    this(clazz, "" +  detailMessage);
  }
 
  public PreconditionError(Class clazz, float detailMessage) {
    this(clazz, "" +  detailMessage);
  }
 
  public PreconditionError(Class clazz, double detailMessage) {
    this(clazz, "" +  detailMessage);
  }

  public void setSuperPreconditionError(PreconditionError pe) {
    this.pe = pe;
  }
  /*
PreconditonError:  x > 0
PreconditonError:  x > 0 && x < 80
	at Main.main(Main.java:68)
PreconditionError: (B) error msg 1
PreconditionError: (A) error msg 2
	at B.method_x$pre(B.java:6)
	at B.method_x(B.java:20)
	at Main.main(B.java:25)
 */
  private String getDescription() {
    String      s = getClass().getName();
    String      message = getLocalizedMessage();
    String      description = s + ": (" + clazz.getName() + ") ";

    return (message != null) ? (description + ":" + message) :  description;
  }


  public String toString() {
    if (pe == null) {
      return getDescription();
    } else {
      return getDescription() + nl + pe.toString();
    }
  }

  private PreconditionError     pe;
  private static final String   nl = System.getProperty("line.separator");
  private Class                 clazz = null;
}
