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

package at.dms.vkopi.lib.preview;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.visual.*;
import at.dms.vkopi.lib.util.Message;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.*;

/**
 * A window with an html pane
 */
class DPreviewWindow extends DWindow implements DPositionPanelListener, PreviewListener {

  /**
   *
   */
  public DPreviewWindow(VPreviewWindow model) {
    super(model);
    this.model = model;
    model.setDisplay(this);
    registerKeyboardAction(new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
	closeWindow();
      }},
      null,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    label = new JLabel(); // model.label;
    bodypane = new JScrollPane(label,
                               JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                               JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(bodypane, BorderLayout.CENTER);
    label.setIconTextGap(0);
    label.setRequestFocusEnabled(true);
    setStatePanel(blockInfo = new DPositionPanel(this));
    model.addPreviewListener(this);
    label.addKeyListener(new KeyAdapter () {
      public void keyPressed(KeyEvent k) {
          if (k.getKeyCode() == KeyEvent.VK_PAGE_UP
              && DPreviewWindow.this.model.getCurrentPage() > 1)
            {
              gotoPrevPosition();
            }
          if (k.getKeyCode() == KeyEvent.VK_PAGE_DOWN
              && DPreviewWindow.this.model.getCurrentPage() < DPreviewWindow.this.model.getNumberOfPages())
            {
             gotoNextPosition();
            }
      }
    });
  }

  /**
   *
   */
  public void init() {
  }

  public void run() {
    Frame       frame;
    Rectangle   bounds;

    setTitle(model.getTitle());
    label.setIcon(new ImageIcon(model.getPreviewFileName(1)));    

    frame = getFrame();
    frame.pack(); // layout frame; get preferred size
    // calulate bounds for frame to fit screen
    bounds = Utils.calculateBounds(frame, null, null);
    frame.setBounds(bounds);


    setPagePosition(model.getCurrentPage(), model.getNumberOfPages());

    WindowStateListener listener = new WindowAdapter() {
        public void windowStateChanged(WindowEvent evt) {
          int   oldState = evt.getOldState();
          int   newState = evt.getNewState();
          
          if ((oldState & Frame.MAXIMIZED_BOTH) == 0
              && (newState & Frame.MAXIMIZED_BOTH) != 0) {
            getFrame().invalidate();
            getFrame().validate();
            
            UserConfiguration   userConfig = ApplicationConfiguration.getConfiguration().getUserConfiguration();

            zoomFit(userConfig == null ? PreviewListener.FIT_BOTH : userConfig.getPreviewMode());
          } 
        }
      };
    
    frame.addWindowStateListener(listener);

    frame.show();
    label.requestFocusInWindow();

    UserConfiguration   userConfig = ApplicationConfiguration.getConfiguration().getUserConfiguration();

    if (userConfig != null && userConfig.getPreviewScreen() == UserConfiguration.PRS_FULLSCREEN) {
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    } else {
      if (userConfig != null && userConfig.getPreviewMode() != UserConfiguration.PRM_OPT) {
        zoomFit(userConfig.getPreviewMode());
      }
    }
  }


  /**
   * setPagePosition
   * inform user about nb records fetched and current one
   */
  public void setPagePosition(int current, int count) {
    blockInfo.setPosition(current, count);
  }

  // ----------------------------------------------------------------------
  // INTERFACE PreviewListener
  // ----------------------------------------------------------------------

  private void setIcon(int current) {
    ImageIcon   img = (ImageIcon)label.getIcon();

    if (img != null) {
      img.getImage().flush();
    }

    ImageIcon   imgNew = new ImageIcon(model.getPreviewFileName(current));

    label.setIcon(imgNew);

    if (img != null) {
      bodypane.invalidate();
      bodypane.validate();

      centerScrollbar(bodypane.getHorizontalScrollBar());
      centerScrollbar(bodypane.getVerticalScrollBar());
    }
  }

  private void centerScrollbar(JScrollBar bar) {
    BoundedRangeModel   model = bar.getModel();

    model.setValue(Math.max((model.getMaximum()-model.getExtent())/2, 0));
  }

  public  void pageChanged(final int current) {
    setIcon(current);
    setPagePosition(current, model.getNumberOfPages());
  }

  public void zoomChanged() {
    setIcon(model.getCurrentPage());
  }
 
  public void zoomFit(int type) {
    Dimension         dim = bodypane.getSize(); // size of view
    int               height = model.getHeight();
    int               width = model.getWidth();
    float             ratio;

    switch (type) {
      case FIT_BOTH:
        // round the ratio with 0.99f, so that there are definitly no 
        // scrollbars
        ratio = Math.min((float) dim.height / model.getHeight(), 
                         (float) dim.width / model.getWidth()) * 0.99f;
        break;
      case FIT_HEIGHT:
        // 1.03 : do not show the white border
        ratio = dim.height * 1.03f / model.getHeight();
        break;
      case FIT_WIDTH:
        // 1.05 : do not show the white border
        ratio = dim.width * 1.05f / model.getWidth();
        break;
    default:
      throw new InconsistencyException("Unkown type of zoom");
    }

    model.zoom(ratio);
  }

  // ----------------------------------------------------------------------
  // INTERFACE DPositionPanelListener
  // ----------------------------------------------------------------------

  /**
   * Requests to go to the next position.
   */
  public void gotoNextPosition() {
    getModel().performAsyncAction(new KopiAction("preview right") {
        public void execute() throws VException {
          getModel().executeVoidTrigger(VPreviewWindow.CMD_RIGHT);
        }
      });
  }

  /**
   * Requests to go to the previous position.
   */
  public void gotoPrevPosition() {
    getModel().performAsyncAction(new KopiAction("preview left") {
        public void execute() throws VException {
          getModel().executeVoidTrigger(VPreviewWindow.CMD_LEFT);
        }
      });
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private VPreviewWindow        model;
  private JLabel                label;
  private JScrollPane           bodypane;
  private DPositionPanel        blockInfo;
}
