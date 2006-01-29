/*
 * Copyright (c) 1990-2006 kopiRight Managed Solutions GmbH
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
package com.kopiright.kopi.lib.assertion;

import java.util.LinkedList;

public final class AssertionRuntime {

  public static final boolean testAndSetRunAssertion() {
    Thread      t = Thread.currentThread();

    synchronized(threads){
      if (threads.contains(t)) {
        return true;
      } else {
        threads.add(t);
        return false;
      }
    }
  }

  public static final void clearRunAssertion() {
    Thread      t = Thread.currentThread();

    synchronized(threads){
      threads.remove(t);
    }    
  }

//   public static final void setRunAssertion(boolean enable) {
//     setRunAssertion(Thread.currentThread(), enable);
//   }
//   public static final void setRunAssertion(Thread t, boolean enable) {
//     if (enable) {
//       synchronized(threads){
//         if (! threads.contains(t)) {
//           threads.add(t);
//         }
//       }
//     } else {
//       synchronized(threads){
//         threads.remove(t);
//       }
//     }
//   }

//   public static final boolean getRunAssertion() {
//     return getRunAssertion(Thread.currentThread());
//   }

//   public static final boolean getRunAssertion(Thread t) {
//     boolean     found = false;

//     synchronized(threads){
//       if (threads.contains(t)) {
//         found = true;
//       }
//     }
//     return found;
//   }

  private static LinkedList threads = new LinkedList();
}

