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
 * $Id: DPreviewWindow.java,v 1.1 2004/07/28 18:43:29 imad Exp $
 */

package at.dms.vkopi.lib.preview;

import at.dms.vkopi.lib.visual.*;
import at.dms.vkopi.lib.util.Message;

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
    Rectangle   bounds;
    Frame       frame;

    setTitle(model.getTitle());
    label.setIcon(new ImageIcon(model.getPreviewFileName(1)));    

    frame = getFrame();
    frame.pack(); // layout frame; get preferred size
    // calulate bounds for frame to fit screen
    bounds = Utils.calculateBounds(frame, null, null);
    frame.setBounds(bounds);

    getFrame().show();
    label.requestFocusInWindow();
    setPagePosition(model.getCurrentPage(), model.getNumberOfPages());
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
  }

  public  void pageChanged(final int current) {
    setIcon(current);
    setPagePosition(current, model.getNumberOfPages());
  }

  public void zoomChanged() {
    setIcon(model.getCurrentPage());
  }
 
  public void zoomFit() {
    Dimension         dim = bodypane.getSize(); // size of view
    int               height = model.getHeight();
    int               width = model.getWidth();
    float             ratio = Math.min((float)dim.height/model.getHeight(), 
                                       (float) dim.width/model.getWidth());

    // round the ratio with 0.99f, so that there are definitly no 
    // scrollbars
    model.zoom(ratio*0.99f);
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
