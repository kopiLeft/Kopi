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

  /*
   * ----------------------------------------------------------------------
   * CONSTRUCTION
   * ----------------------------------------------------------------------
   */

  /**
   * Constructor
   */
  public DMultiBlock(DForm form, VBlock model) {
    super(form, model);

    button = new MoveButton(MoveButton.CHART);
    firstButton = new MoveButton(MoveButton.FIRST);
    prevButton = new MoveButton(MoveButton.PREV);
    nextButton = new MoveButton(MoveButton.NEXT);
    lastButton = new MoveButton(MoveButton.LAST);

    final Box                   buttonBox = Box.createVerticalBox();

    layeredPane = new JLayeredPane() {
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
          //          positionLabel.setMaximumSize(new Dimension(19, h - 4*19));
        }
      };

    //    positionLabel = new PositionLabel();
    //    getModel().addBlockListener(positionLabel);

    buttonBox.add(button);
    buttonBox.add(firstButton);
    buttonBox.add(prevButton);
    buttonBox.add(Box.createVerticalGlue());
    //buttonBox.add(positionLabel);
    buttonBox.add(nextButton);
    buttonBox.add(lastButton);


    chartPane.setOpaque(true);
    
    detailLayerPane = new JPanel(new BorderLayout());
    detailLayerPane.setOpaque(true);

    detailPane.setBorder(new EmptyBorder(2, 2, 2 ,2));
    buttonBox.setBorder(new EmptyBorder(0, 0, 0 ,6));
    detailLayerPane.setBorder(new LineBorder(color_line));
    detailLayerPane.add(detailPane, BorderLayout.CENTER);
    detailLayerPane.add(buttonBox, BorderLayout.WEST);

    //Add to layers
    layeredPane.add(chartPane, new Integer(2), 0);
    layeredPane.add(detailLayerPane, new Integer(1), 0);

    setLayout(new BorderLayout());
    super.add(layeredPane, BorderLayout.CENTER);
 
    nextButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getFormView().performAsyncAction(new KopiAction("detailView next rec") {
              public void execute() throws VException {
                getModel().gotoNextRecord();
              }
            });
        }
      });
    prevButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getFormView().performAsyncAction(new KopiAction("detailView prev rec") {
              public void execute() throws VException {
                getModel().gotoPrevRecord();
              }
            });
        }
      });
    firstButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getFormView().performAsyncAction(new KopiAction("detailView first rec") {
              public void execute() throws VException {
                getModel().gotoFirstRecord();
              }
            });
        }
      });
    lastButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          getFormView().performAsyncAction(new KopiAction("detailView last rec") {
              public void execute() throws VException {
                getModel().gotoLastRecord();
              }
            });
        }
      });
    int         displaySize = getModel().getDisplaySize();

    chartPane.add(new JLabel(""), new KopiAlignment(0, 0, 1, false));

    for (int i=1; i < displaySize+1; i++) {
      
      DetailButton detailbutton = new DetailButton(i-1);

      getModel().addBlockListener(detailbutton);
      chartPane.add(detailbutton, new KopiAlignment(0, i, 1, false));
    }
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // A basic action is executed in the thread which calls the basic-action
          getFormView().performBasicAction(new KopiAction("detailView change view") {
              // !!! Execution in eventThread is problematic (TRIGGER's should not
              // be executed in the event-disp-thread
              public void execute() throws VException {
                switchView(-1);
              }  
            });
        }
      });
    
    firstButton.setVisible(false);
    prevButton.setVisible(false);
    nextButton.setVisible(false);
    lastButton.setVisible(false);
  }

  protected void createFields() {
    chartPane = new JPanel();
    detailPane = new JPanel();
    detailPane.setLayout(new KopiSimpleBlockLayout(2 * maxColumnPos, 
                                                   maxRowPos, 
                                                   (model.getAlignment() == null) ? 
                                                       null : 
                                                       new ViewBlockAlignment(getFormView(), model.getAlignment())));
    // width + 1 : detailbuttons
    // displaysize + 1 : header
    chartPane.setLayout(layout = new KopiMultiBlockLayout(displayedFields + 1, getModel().getDisplaySize() +1));
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

  private void switchView(int row) throws VException {
    SwingThreadHandler.verifyRunsInEventThread("Detailview change view");
    // if this block is not the current block
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
    } else {
      getModel().setDetailMode(true);
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
        
        firstButton.setVisible(true);
        prevButton.setVisible(true);
        nextButton.setVisible(true);
        lastButton.setVisible(true);
        button.setIcon(Utils.getImage("chart_view.png"));
              
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
                    
        firstButton.setVisible(false);
        prevButton.setVisible(false);
        nextButton.setVisible(false);
        lastButton.setVisible(false);
        button.setIcon(Utils.getImage("detail_view.png"));
                    
        layeredPane.setLayer(chartPane, 2, 0);
        layeredPane.setLayer(detailLayerPane, 1, 0);
      } catch (VException ex) {
        getModel().getForm().displayError(ex.getMessage());
      }
    }    
  }

  class MoveButton extends JButton {

    public MoveButton(int type) {
      this.type = type;
      setRolloverEnabled(true);
      setPreferredSize(new Dimension(19, 19));
      setMaximumSize(new Dimension(19, 19));
    }

    public void paint(Graphics g) {
      g.setColor(color_back);
      g.fillRect(0, 0, getSize().width-1, getSize().height-1);           

      if (getModel().isRollover() && DMultiBlock.this.getModel().isAccessible()) {
        g.setColor(Color.black);
      } else {
        g.setColor(color_line);
      }
      g.drawRect(0, 0, getSize().width-1, getSize().height-1);           

      switch(type) {
      case FIRST:
        g.fillPolygon(new int[]{4, 10 , 15}, new int[]{13 , 5 , 13}, 3);           
        g.fillRect(4, 4 , 11, 2);           
        break;
      case PREV:
        g.fillPolygon(new int[]{4, 10 , 15}, new int[]{13 , 5 , 13}, 3);           
        break;
      case CHART:
        int  hOffest = (getSize().height - 19) / 2;

        g.drawRect(3, 4 + hOffest , 12, 11);           
        g.drawLine(6, 4 + hOffest, 6, 15 + hOffest);           
        g.drawLine(9, 4 + hOffest , 9, 15 + hOffest);           
        g.drawLine(12, 4 + hOffest, 12, 15 + hOffest);           
        g.drawLine(3, 7 + hOffest, 14, 7 + hOffest);           
        g.drawLine(3, 10 + hOffest, 14, 10 + hOffest);           
        g.drawLine(3, 13 + hOffest, 14, 13 + hOffest);           
        break;
      case NEXT:
        g.fillPolygon(new int[]{4, 10 , 15}, new int[]{6 , 12 , 6}, 3);           
        break;
      case LAST:
        g.fillPolygon(new int[]{4, 10 , 15}, new int[]{6 , 12 , 6}, 3);           
        g.fillRect(4, 12 , 11, 2);           
        break;
      }
    }

    private int         type;

    static final int    FIRST   = 1;
    static final int    PREV    = 2;
    static final int    NEXT    = 3;
    static final int    LAST    = 4;
    static final int    CHART   = 5;
  }

  class DetailButton extends JButton implements BlockListener, ActionListener {

    public DetailButton(int row) {
      this.row = row;
      setRolloverEnabled(true);
      addActionListener(this);
    }

    public void paint(Graphics g) {
      g.setColor(color_back);
      g.fillRect(0, 0, getSize().width-1, getSize().height-1);           

      if (getModel().isRollover() && DMultiBlock.this.getModel().isAccessible()) {
        g.setColor(Color.black);
      } else if (row == getDisplayLine(DMultiBlock.this.getModel().getActiveRecord())) {
        g.setColor(color_border_chart_active);
      } else {
        g.setColor(color_border_chart);
      }
      g.drawRect(0, 0, getSize().width-1, getSize().height-1);           
      g.fillPolygon(new int[]{6 , 12 , 6}, new int[]{4, 10 , 15}, 3);           
    }

    public Dimension getPreferredSize() {
      return new Dimension(17,19);
    }

    public void actionPerformed(ActionEvent e) {
      // A basic action is executed in the thread which calls the basic-action
      if (DMultiBlock.this.getModel().isDetailMode()) {
        return;
      }

      getFormView().performBasicAction(new KopiAction("detailView change view") {
          // !!! Execution in eventThread is problematic (TRIGGER's should not
          // be executed in the event-disp-thread
          public void execute() throws VException {
            switchView(row);
          }  
        });
    }

    // -------------------------------------------------------------------
    // Implementing Block Listener
    // -------------------------------------------------------------------

    public void blockChanged(){
      repaint();
    }
    public void blockCleared(){
      repaint();
    }

    // the other methods has no information for this usage
    public void blockClosed() {}

    public void blockAccessChanged(VBlock block, boolean newAccess){}
    public void blockViewModeLeaved(VBlock block, VField actviceField){}
    public void blockViewModeEntered(VBlock block, VField actviceField){}

    public void validRecordNumberChanged() {}
    public void orderChanged() {
      repaint();
    }
    public Component getCurrentDisplay() { return null;}

    private int row;
  }

//   class PositionLabel extends JLabel implements BlockListener {

//     public PositionLabel() {
//       super("");
//       setBorder(new LineBorder(color_line));
//       setOpaque(true);
//     }

//     // -------------------------------------------------------------------
//     // Implementing Block Listener
//     // -------------------------------------------------------------------

//     public void blockChanged(){
//       if (getModel().getActiveRecord() >= 0) {
//         setText(String.valueOf(getModel().getActiveRecord()));
//       } else {
//         setText("");
//       }
//     }
//     public void blockCleared(){
//       if (getModel().getActiveRecord() >= 0) {
//         setText(String.valueOf(getModel().getActiveRecord()));
//       } else {
//         setText("");
//       }
//     }

//     // the other methods has no information for this usage
//     public void blockClosed() {}

//     public void blockAccessChanged(VBlock block, boolean newAccess){}
//     public void blockViewModeLeaved(VBlock block, VField actviceField){}
//     public void blockViewModeEntered(VBlock block, VField actviceField){}

//     public void validRecordNumberChanged() {}
//     public Component getCurrentDisplay() { return null;}

//     private int row;
//   }

  // ----------------------------------------------------------------------
  // PRIVATE DATA
  // ----------------------------------------------------------------------
  final JLayeredPane          layeredPane;
  final JButton               button;
  final JButton               firstButton;
  final JButton               prevButton;
  final JButton               nextButton;
  final JButton               lastButton;

//   private PositionLabel         positionLabel;
  private JPanel                chartPane;  // pane with chart view
  private JPanel                detailPane; // pane with detail view (one row of thechart)
  private JPanel                detailLayerPane; // pane with buttons to move
  private static final Color    color_border_chart        = UIManager.getColor("KopiField.ul.chart");
  private static final Color    color_border_chart_active = UIManager.getColor("KopiField.ul.chart.active");
  private static final Color    color_back          = UIManager.getColor("KopiField.background.skipped.color");
  private static final Color    color_line  = UIManager.getColor("KopiField.ul.color");
}
