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

package org.kopi.vkopi.lib.ui.vaadin.base;

import java.util.concurrent.Executor;

import com.vaadin.ui.UI;

/**
 * A UI executor that simply uses the current UI available via
 * {@link UI#getCurrent()} for synchronization/locking. Therefore tasks can only
 * be submitted to this executor from the UI thread. UIRunnables submitted will
 * be executed via a supplied {@link Executor} while access methods all delegate
 * directly to the current UI.
  * 
 * @deprecated Use the {@link StrategyUIExecutor} instead
 */
@Deprecated
public class CurrentUIExecutor extends AbstractUIExecutor implements UIExecutor {

  /**
   * Prepares the given runnable future for execution on the given UI. The
   * default implementation is a no-op.
   * 
   * @param ui
   *          the current UI
   * @param runnableFuture
   *          the runnable future about to be executed
   */
  @Override
  protected void prepareForExecution(UI ui, UIRunnableFuture runnableFuture) {
    // no op
  }

  @Override
  protected UI getUI() {
    return UI.getCurrent();
  }
}
