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

package org.kopi.vkopi.lib.form;

import java.io.Serializable;
import java.util.EventListener;

public interface BlockListener extends EventListener, Serializable {
  void blockClosed();
  void blockChanged();
  void blockCleared();

  void blockAccessChanged(VBlock block, boolean newAccess);
  void blockViewModeLeaved(VBlock block, VField actviceField);
  void blockViewModeEntered(VBlock block, VField actviceField);

  void validRecordNumberChanged();
  void recordInfoChanged(int rec, int info);
  void orderChanged();

  UBlock getCurrentDisplay(); // please do not use!
}
