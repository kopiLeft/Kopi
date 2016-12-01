/*
 * Copyright (c) 2013-2015 kopiLeft Development Services
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

package org.kopi.vkopi.lib.ui.vaadin.addons.client.block;

import java.util.List;

import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.CachedColor;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.CachedValue;
import org.kopi.vkopi.lib.ui.vaadin.addons.client.block.BlockState.RecordInfo;

import com.vaadin.shared.annotations.NoLoadingIndicator;
import com.vaadin.shared.communication.ServerRpc;

/**
 * The server RPC for block communication between client and server side.
 */
public interface BlockServerRpc extends ServerRpc {

  /**
   * Updates the scroll position.
   * @param value The scroll position.
   */
  @NoLoadingIndicator
  public void updateScrollPos(int value);
  
  /**
   * Updates the value of the active record in server side.
   * @param record The client active record.
   * @param sortedTopRec the top sorted record.
   */
  @NoLoadingIndicator
  public void updateActiveRecord(int record, int sortedTopRec);
  
  /**
   * Clears the cached values. This is called by the client side when the cached values are
   * already registered in the client data model.
   */
  @NoLoadingIndicator
  public void clearCachedValues(List<CachedValue> cachedValues);
  
  /**
   * Clears the cached colors. This is called by the client side when the cached colors are
   * already registered in the client data model.
   */
  @NoLoadingIndicator
  public void clearCachedColors(List<CachedColor> cachedColors);
  
  /**
   * Clears the given record info list.
   * @param recordInfos The record info list.
   */
  @NoLoadingIndicator
  public void clearRecordInfo(List<RecordInfo> recordInfos);
}
