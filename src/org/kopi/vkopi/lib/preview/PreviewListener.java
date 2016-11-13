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

package org.kopi.vkopi.lib.preview;

import java.io.Serializable;
import java.util.EventListener;

import org.kopi.vkopi.lib.visual.UserConfiguration;

public interface PreviewListener extends EventListener, Serializable {
  
  void pageChanged(int current);
  void zoomChanged();
  void zoomFit(int type);

  int FIT_BOTH          = UserConfiguration.PRM_OPT;
  int FIT_HEIGHT        = UserConfiguration.PRM_OPT_HEIGHT;
  int FIT_WIDTH         = UserConfiguration.PRM_OPT_WIDHT;
}
