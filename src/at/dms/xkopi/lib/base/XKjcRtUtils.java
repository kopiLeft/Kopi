/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
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
 * $Id: XKjcRtUtils.java,v 1.1 2004/07/28 18:43:27 imad Exp $
 */

package at.dms.xkopi.lib.base;

/**
 * This class contains utility methods that help xkjc at runtime
 */
public class XKjcRtUtils {

  /**
   * ReturnMe
   * This methods return the first parameter
   * (and allow side effect on his second)
   * i++ ==> ReturnMe(x, x += 1)
   */
  public static Object returnMe(Object left, Object right) {
    return left;
  }
}
