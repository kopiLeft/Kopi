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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import at.dms.util.base.InconsistencyException;
import at.dms.vkopi.lib.util.Utils;
import at.dms.vkopi.lib.visual.KopiAction;
import at.dms.vkopi.lib.visual.SwingThreadHandler;
import at.dms.vkopi.lib.visual.VCommand;
import at.dms.vkopi.lib.visual.VException;
import at.dms.vkopi.lib.ui.base.FieldStates;
import at.dms.vkopi.lib.ui.base.JFieldButton;

/**
 * DField is a panel composed in a text field and an information panel
 * The text field appear as a JLabel until it is edited
 */
public abstract class DField extends JPanel {

  // ----------------------------------------------------------------------
  // CONSTRUCTION
  // ----------------------------------------------------------------------

  /**
   * Constructor
   */
  public DField(VFieldUI model,
                DLabel label,
                int align,
                int options,
                boolean detail) {
    setLayout(new BorderLayout());
    addMouseListener(new DFieldMouseListener());

    this.inDetail = detail;
    this.model = model;
    this.options = options;
    this.label = label;
    this.align = align;
    isEditable = (options & VConstants.FDO_NOEDIT) == 0;

    if ((!getModel().getBlock().isMulti() || getModel().getBlock().noChart() || isInDetail())
        && (getModel().getDefaultAccess() >= VConstants.ACS_SKIPPED)) {
      JPanel	optionPane = new JPanel();

      optionPane.setLayout(new BorderLayout());
      if (model.hasAutofill()) {
	info = new JFieldButton(listImg);
	info.addActionListener(new AbstractAction() {
	  public void actionPerformed(ActionEvent e) {
	    getModel().getForm().performAsyncAction(new KopiAction("autofill") {
	      public void execute() throws VException {
		DField.this.model.transferFocus(DField.this);
		DField.this.model.autofillButton();
	      }
	    });
	  }
	});
        info.setEnabled(getModel().getDefaultAccess() > VConstants.ACS_SKIPPED);

	optionPane.add(info, BorderLayout.WEST);
      }
      if (model.getDecrementCommand() != null) {
	decr = new JFieldButton(leftImg);
	decr.addActionListener(new AbstractAction() {
	  public void actionPerformed(ActionEvent e) {
	    DField.this.model.getDecrementCommand().performAction();
	  }
	});
	optionPane.add(decr, BorderLayout.CENTER);
      }
      if (model.getIncrementCommand() != null) {
	incr = new JFieldButton(rightImg);
	incr.addActionListener(new AbstractAction() {
	  public void actionPerformed(ActionEvent e) {
	    DField.this.model.getIncrementCommand().performAction();
	  }
	});
	optionPane.add(incr, BorderLayout.EAST);
      }
      add(optionPane, BorderLayout.EAST);
    }
  }



  /**
   * Returns the object associed to record r
   *
   * @param	r		the position of the record
   * @return	the displayed value at this position
   */
  public abstract Object getObject();


  /**
   * Field cell renderer
   */
  void setPosition(int pos) {
    this.pos = pos;
  }

  /**
   * Field cell renderer
   * @return the position in chart (0..nbDisplay)
   */
  int getPosition() {
    return pos;
  }

  /**
   * Returns the label text
   */
  public String getLabel() {
    return label.getTextWithoutTwoPoints();
  }

  /**
   * Returns the alignment
   */
  public int getAlign() {
    return align;
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  /*package*/ void enter(boolean refresh) {
    updateFocus();
  }

  /*package*/ void leave() {
    updateFocus();
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------

  public void updateText() {
  }

  public void updateFocus() {
    SwingThreadHandler.verifyRunsInEventThread("DField.updateFocus");
    if (modelHasFocus()) {
      final VForm       form = getModel().getForm();

      form.setInformationText(getModel().getToolTip());
      form.setFieldSearchOperator(getModel().getSearchOperator());
    }
  }

  public void updateAccess() {
    SwingThreadHandler.verifyRunsInEventThread("DField.updateAccess");
    access = getAccess();

    setVisible(access != VConstants.ACS_HIDDEN);
    fireMouseHasChanged(); // $$$

    update(info);
    update(incr);
    update(decr);
  }

  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */
  public void update() {
    // overridden in subclasses
  }

  public DBlock getBlockView() {
    return model.getBlockView();
  }

  /**
   * set blink state
   */
  public abstract void setBlink(boolean b);

  protected final boolean modelHasFocus() {
    if (getModel() == null) {
      return false;
    }

    final VBlock block = getModel().getBlock();
    return getModel().hasFocus()
      && block.getActiveRecord() == model.getBlockView().getRecordFromDisplayLine(pos);
  }

  protected final boolean isSkipped() {
    final VBlock block = getModel().getBlock();
    return getAccess() == VConstants.ACS_SKIPPED
      || !block.isRecordAccessible(model.getBlockView().getRecordFromDisplayLine(getPosition()));
  }

  protected final int getAccess() {
    return getAccessAt(getPosition());
  }

  protected final int getAccessAt(int at) {
    if (getModel() != null && getModel().getBlock() != null) {
      final VBlock 	block = getModel().getBlock();
      int               access = getModel().getAccess(model.getBlockView().getRecordFromDisplayLine(at));

      return access;
    } else {
      return VConstants.ACS_SKIPPED;
    }
  }

  public final VField getModel() {
    return model.getModel();
  }

  public final void fireMouseHasChanged() {
    final int	access = getAccess();
    final int	oldState = state;

    state = 0;

    if (modelHasFocus()) {
      state |= FieldStates.FOCUSED;
    }
    if (!isEditable) {
      state |= FieldStates.NOEDIT;
    }
    if (getModel().getBorder() == VConstants.BRD_HIDDEN) {
      state |= FieldStates.NOBORDER;
    }

    VBlock      block = model.getBlock();

    if (block.isMulti() && !block.noChart() && !isInDetail()) {
      state |= FieldStates.CHART;
      if (block.getActiveRecord() == model.getBlockView().getRecordFromDisplayLine(pos)){
        state |= FieldStates.ACTIVE;
      }
    } else {
      state |= FieldStates.ACTIVE;
    }

    if (mouseInside) {
      state |= FieldStates.ROLLOVER;
    }

    switch (access) {
    case VConstants.ACS_HIDDEN:
      state |= FieldStates.HIDDEN;
      break;
    case VConstants.ACS_MUSTFILL:
      state |= FieldStates.MUSTFILL;
      break;
    case VConstants.ACS_VISIT:
      state |= FieldStates.VISIT;
      break;
    default:
      state |= FieldStates.SKIPPED;
      break;
    }

    if (oldState != state) {
      setDisplayProperties();
    }
  }

  public JButton getAutofillButton() {
    return info;
  }

  public VFieldUI getRowController() {
    return model;
  }

  protected abstract void setDisplayProperties();

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * prepare a snapshot
   *
   * @param	fieldPos	position of this field within block visible fields
   */
  public void prepareSnapshot(int fieldPos, boolean activ) {
    label.prepareSnapshot(activ);
    if (activ) {
      state |= FieldStates.FOCUSED;
      setDisplayProperties();
    } else {
      state |= FieldStates.SKIPPED;
      setDisplayProperties();
    }

    if (info != null) {
      info.setVisible(false);
    }
    if (incr != null) {
      incr.setVisible(false);
    }
    if (decr != null) {
      decr.setVisible(false);
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  private void update(final JButton button) {
    if (button != null) {
      boolean	was = button.isEnabled();
      boolean	will = access >= VConstants.ACS_VISIT;
      if (was != will) {
        button.setEnabled(will);
      }
    }
  }

  public void setInDetail(boolean detail) {
    inDetail = detail;
  }

  public boolean isInDetail() {
    return inDetail;
  }

  // ----------------------------------------------------------------------
  // INNER CLASSES
  // ----------------------------------------------------------------------

  protected final class DFieldMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      performAction();
    }

    public void mouseEntered(MouseEvent e) {
      mouseInside = true;
      fireMouseHasChanged();
    }

    public void mouseExited(MouseEvent e) {
      mouseInside = false;
      fireMouseHasChanged();
    }

    private void performAction() {
      if (!modelHasFocus()) {
        // an empty row in a chart has not calculated
        // the access for each field (ACCESS Trigger)
        if (model.getBlock().isMulti()) {
          int	recno = model.getBlockView().getRecordFromDisplayLine(DField.this.getPosition());

          if (! model.getBlock().isRecordFilled(recno)) {
            model.getBlock().updateAccess(recno);
          }
        }

        if (model.getBlock().isDetailMode() && isInDetail()) {
          KopiAction	action = new KopiAction("mouse1") {
              public void execute() throws VException {
                model.transferFocus(DField.this); // use here a mouse transferfocus
              }
            };
          // execute it as model transforming thread
          // it is not allowed to execute it not with
          // the method performAsync/BasicAction.
          model.performAsyncAction(action);
        }
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	VFieldUI		model;
  protected	DLabel			label;
  protected	JButton			info;
  protected	JButton			incr;
  protected	JButton			decr;

  protected	int			state;		// Display state
  protected	int			pos;
  protected	int			options;
  protected	int			align;
  protected     int			access;		// current access of field
  protected	boolean			isEditable;	// is this field editable
  protected	boolean			mouseInside;	// private events

  private       boolean                 inDetail;

  private static final ImageIcon listImg = Utils.getImage("list.gif");
  private static final ImageIcon rightImg = Utils.getImage("arrowright.gif");
  private static final ImageIcon leftImg = Utils.getImage("arrowleft.gif");
}
