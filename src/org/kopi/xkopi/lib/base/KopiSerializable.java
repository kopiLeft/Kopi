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

package org.kopi.xkopi.lib.base;

/**
 * Objects that are stored in DB should implements this interface, else they will be
 * serialized as ObjectStream.
 * This allow to save an ImageIcon as a InputStream of raw data that can be retreived by
 * any other class that can be construct from byte[] or from a C++ program or save in a file
 *
 * If a constructor with InputStream and KopiSerializable exists, it will be used
 * the second parameter is here only to differentiate kopi constructor from a possible
 * byte[] constructor
 * <pre>
 * NamOfYourClass(byte[] inData, KopiSerializable dummy) {...}
 * </pre>
 */
public interface KopiSerializable {

  /**
   * This method returns the representation of the object as a byte[]
   */
  byte[] toKopiData();
}
