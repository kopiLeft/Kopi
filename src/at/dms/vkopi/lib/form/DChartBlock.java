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

package at.dms.vkopi.lib.form;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.visual.SwingThreadHandler;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.visual.VExecFailedException;
import at.dms.vkopi.lib.visual.VRuntimeException;
import at.dms.vkopi.lib.visual.KopiAction;
import at.dms.vkopi.lib.ui.base.JFieldButton;
import at.dms.vkopi.lib.ui.plaf.KopiScrollBarUI;

public class DChartBlock extends DBlock {

  /*
   * ----------------------------------------------------------------------
   * CONSTRUCTION
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public DChartBlock(DForm form,
                     VBlock model,
		     int border,
		     String title,
		     int align,
                     int maxRowPos, 
                     int maxColumnPos, 
                     int displayedFields) {
    super(form, model, border, title, align, 
          maxRowPos, maxColumnPos, displayedFields);

    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      scrollBar = createScrollBar();

      scrollBar.setFocusable(false);

      addScrollBar(scrollBar);
    }
  }

  protected void addScrollBar(JScrollBar bar) {
    add(bar);
  }

  protected JScrollBar createScrollBar() {
    final JScrollBar          scrollBar;

    scrollBar = new JScrollBar(JScrollBar.VERTICAL,
                               0,
                               getModel().getDisplaySize(),
                               0,
                               getModel().getBufferSize() - 1);
    
    adjustmentListener = new AdjustmentListener() {
        public void adjustmentValueChanged(final AdjustmentEvent e) {
          if (getFormView().getInAction()) {
            // do not change the rows if there is currently a
            // another command executed
            return;
          }
          getFormView().performAsyncAction(new KopiAction("chart") {
              public void execute() throws VException {
                if (!init) {
                  init = true;
                } else {
                  // Must be outside of event disp thread, so that 
                  // the scrollbar loses focuses and stop the scroll thread
                  try {
                    setScrollPos(e.getValue());
                  } catch (VException e) {
                    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new RNCEvent());
                    throw e;
                  }
                }
              }
            }); // performBasicAction : not asyncron!       
        }
        private boolean		init;
      };
    return scrollBar;
  }

  protected KopiLayoutManager createLayoutManager() {
    return new KopiMultiBlockLayout(displayedFields, getModel().getDisplaySize() + 1 /* Records + Header*/);
  }


  public void validRecordNumberChanged() {
    if (getModel().getDisplaySize() < getModel().getBufferSize()) {
      // only useful if there is a scrollbar

      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new RNCEvent());
    }
  }

  protected void refresh(boolean force) {
    SwingThreadHandler.verifyRunsInEventThread("DBlock refresh");

    super.refresh(force);
    if (scrollBar != null) {
      updateScrollbar();
    }
  }

  private void updateScrollbar() {
    SwingThreadHandler.verifyRunsInEventThread("DChartBlock updateScrollbar");
    int         validRecords = getModel().getNumberOfValidRecord();
    int         dispSize     = getModel().getDisplaySize();

    if (validRecords > dispSize) {
      scrollBar.removeAdjustmentListener(adjustmentListener);
      scrollBar.setValues(getModel().getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)),
                          dispSize,
                          0,
                          validRecords);
      scrollBar.addAdjustmentListener(adjustmentListener);
      scrollBar.setEnabled(true);
    } else {
      scrollBar.setEnabled(false);
    }
  }

  // ----------------------------------------------------------------------
  // Event Handling
  // ----------------------------------------------------------------------

  class RNCEvent extends InvocationEvent implements Runnable {
    RNCEvent() {
      super(DChartBlock.this, EVT_RNC, null, null, false);
      runnable = this;
    }

    public void run() {
      updateScrollbar();
    }

    static final int    EVT_RNC = 2100;
  }

  protected AWTEvent coalesceEvents(AWTEvent existingEvent,
                                    AWTEvent newEvent) {
    int                 id = existingEvent.getID();

    switch (id) {
    case RNCEvent.EVT_RNC: 
      // coalesce updates to the scrollbar
      return existingEvent;
    default:
      return super.coalesceEvents(existingEvent, newEvent);
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------

  private AdjustmentListener    adjustmentListener;
  private JScrollBar            scrollBar;
}
