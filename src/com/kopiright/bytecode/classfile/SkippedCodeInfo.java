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

package com.kopiright.bytecode.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * The code info, but without the instructions (only the interface)
 */
public class SkippedCodeInfo extends CodeInfo {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Make up a new attribute
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   *
   * @exception	java.io.IOException	an io problem has occured
   */
  public SkippedCodeInfo(DataInput in, ConstantPool cp)
    throws IOException
  {
    int		length = in.readInt();
    in.skipBytes(length);
  }
}
