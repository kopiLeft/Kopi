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

package com.kopiright.vkopi.lib.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.kopiright.vkopi.lib.util.Utils;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.SwingThreadHandler;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VRuntimeException;

public class DMultiBlock extends DChartBlock {
  /**
   * Constructor
   */
  public DMultiBlock(DForm form, VBlock model) {
    super(form, model);
    layeredPane = new JLayeredPane() {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = -3239511954723094795L;
        
        public Dimension getPreferredSize() {
          Dimension dim1 = chartPane.getPreferredSize();
          Dimension dim2 = detailLayerPane.getPreferredSize();
          Dimension dim = new Dimension();
          
          dim.width = Math.max(dim1.width, dim2.width);
          dim.height = Math.max(dim1.height, dim2.height);
          return dim;
        }
        public void setBounds(int x, int y, int w, int h) {
          super.setBounds(x, y, w, h);
          chartPane.setBounds(0, 0, w, h);
          detailLayerPane.setBounds(0, 0, w, h);
        }
      };
    chartPane.setOpaque(true);
    detailLayerPane = new JPanel(new BorderLayout());
    detailLayerPane.setOpaque(true);
    detailPane.setBorder(new EmptyBorder(2, 2, 2 ,2));
    detailLayerPane.setBorder(border);//new LineBorder(color_line));
    detailLayerPane.add(detailPane, BorderLayout.CENTER);
    //Add to layers
    layeredPane.add(chartPane, new Integer(2), 0);
    layeredPane.add(detailLayerPane, new Integer(1), 0);
    setLayout(new BorderLayout());
    super.add(layeredPane, BorderLayout.CENTER);
    int         displaySize = getModel().getDisplaySize();
    
    for (int i= 0; i < displaySize + 1; i++) {
      chartPane.add(new JLabel(""), new KopiAlignment(0, i, 1, false));
    }
  }
  
  protected void createFields() {
    chartPane = new JPanel();
    detailPane = new JPanel();
    detailPane.setLayout( detailLayout = new KopiSimpleBlockLayout(2 * maxColumnPos, 
                                                                   maxRowPos, 
                                                                   (model.getAlignment() == null) ? 
                                                                   null : 
                                                                   new ViewBlockAlignment(getFormView(), model.getAlignment())));
    chartPane.setLayout(chartLayout = layout = new KopiMultiBlockLayout(displayedFields + 1, getModel().getDisplaySize() +1));
    super.createFields();
  }
  
  public boolean inDetailMode() {
    return getModel().isDetailMode();
  }
  
  protected KopiLayoutManager createLayoutManager() {
    return null; 
  }
  
  protected void addScrollBar(JScrollBar bar) {
    chartPane.add(bar);
  }
  
  public void addToChart(Component c, Object o) {
    chartPane.add(c, o);
  }

  public void addToDetail(Component c, Object o) {
    detailPane.add(c, o);
  }

  /**
   * Switches view between list and detail mode.
   *
   * !!! graf 20080521: is row always == -1 ?
   */
  public void switchView(int row) throws VException {
    // if this block is not the current block
    //!!! graf 20080521: is this possible?
    if (!(getModel().getForm().getActiveBlock() == getModel())) {
      if (!getModel().isAccessible()) {
        return;
      }
      try {
        getModel().getForm().gotoBlock(getModel());
      } catch (Exception ex) {
        getFormView().reportError(new VRuntimeException(ex.getMessage(), ex));
        return;
      }
    }
    if (row >= 0) {
      getModel().gotoRecord(getRecordFromDisplayLine(row));
    }
    if (getModel().isDetailMode()) {
      getModel().setDetailMode(false);
      layout = chartLayout;
    } else {
      getModel().setDetailMode(true);
      layout = detailLayout;
    }
  }
  
  public void add(Component c, Object o) {
    chartPane.add(c, o);
  }

  public void blockViewModeLeaved(VBlock block, VField activeField) {
    try {
      // take care that value of current field
      // is visible in the other mode
      // Not field.updateText(); because the field is 
      // maybe not visible in the Detail Mode
      if (activeField != null) {
        activeField.leave(true);
      }
    } catch (VException ex) {
      getModel().getForm().displayError(ex.getMessage());
    }
  }

  public void blockViewModeEntered(VBlock block, VField activeField) {
    if (inDetailMode()) {
      try {
        // Show detail view
        
        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is 
        // maybe not visible in the Detail Mode
        if (activeField == null) {
          //     getModel().gotoFirstField();
        } else {
          if (! activeField.noDetail()) {
            // field is visible in chartView
            activeField.enter(); 
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.setActiveField(activeField);
            getModel().gotoNextField();
          }
        }
        layeredPane.setLayer(chartPane, 1, 0);
        layeredPane.setLayer(detailLayerPane, 2, 0);
      } catch (VException ex) {
        getModel().getForm().displayError(ex.getMessage());
      }
    } else {
      try {
        // show chart view
        
        // take care that value of current field
        // is visible in the other mode
        // Not field.updateText(); because the field is 
        // maybe not visible in the Detail Mode
                    
        if (activeField == null) {
          // getModel().gotoFirstField();
        } else {
          if (!activeField.noChart()) {
            // field is visible in chartView
            activeField.enter(); 
          } else {
            // field is not visible in in chart view:
            // go to the next visible field
            block.setActiveField(activeField);
            getModel().gotoNextField();
          }
        }
        layeredPane.setLayer(chartPane, 2, 0);
        layeredPane.setLayer(detailLayerPane, 1, 0);
      } catch (VException ex) {
        getModel().getForm().displayError(ex.getMessage());
      }
    }    
  }
  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------
  private final JLayeredPane    layeredPane;
  private JPanel                chartPane;  // pane with chart view
  private JPanel                detailPane; // pane with detail view (one row of thechart)
  private JPanel                detailLayerPane; // pane with buttons to move
  private KopiLayoutManager     detailLayout;
  private KopiLayoutManager     chartLayout;

  private static final Color    color_border_chart = UIManager.getColor("KopiField.ul.chart");
  private static final Color    color_border_chart_active = UIManager.getColor("KopiField.ul.chart.active");
  private static final Color    color_back = UIManager.getColor("KopiField.background.skipped.color");
  private static final Color    color_line = UIManager.getColor("KopiField.ul.color");

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3830738652737328391L;
}
