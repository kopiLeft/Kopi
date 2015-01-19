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

package com.kopiright.vkopi.lib.ui.vaadin.form;

import com.kopiright.vkopi.lib.base.UComponent;
import com.kopiright.vkopi.lib.form.UBlock;
import com.kopiright.vkopi.lib.form.UField;
import com.kopiright.vkopi.lib.form.VBlock;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VField;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.form.VForm;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.FieldButton;
import com.kopiright.vkopi.lib.ui.vaadin.base.KopiTheme;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.KopiAction;
import com.kopiright.vkopi.lib.visual.VException;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;

/**
 * The <code>DField</code> is the vaadin {@link UField} implementation.
 * The <code>DField</code> extends the {@link CssLayout} for performance issues.
 */
@SuppressWarnings({ "serial", "deprecation" })
public abstract class DField extends CssLayout implements UField, LayoutClickListener {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
 
  /**
   * Creates a new <code>DField</code> instance.
   * @param model The field model.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options.
   * @param detail Is it a detail view ?
   */
  public DField(VFieldUI model,
                DLabel label,
                int align,
                int options,
                boolean detail)
  {	
    setImmediate(true);
    addListener(this);
	
    this.inDetail = detail;
    this.model = model;
    this.options = options;
    this.label = label;
    this.align = align;
    isEditable = (options & VConstants.FDO_NOEDIT) == 0;
    
    if ((!getModel().getBlock().isMulti() || getModel().getBlock().noChart() || isInDetail())
        && (getModel().getDefaultAccess() >= VConstants.ACS_SKIPPED)) {
    
      if (model.hasAutofill()) {
	if (model.getModel().getTypeName().equals(VlibProperties.getString("Date")) || 
	    model.getModel().getTypeName().equals(VlibProperties.getString("Month")) ||
	    model.getModel().getTypeName().equals(VlibProperties.getString("Week"))) {
	  info = new FieldButton(calendarImg);
	} else if (model.getModel().getTypeName().equals(VlibProperties.getString("Timestamp")) ||
	           model.getModel().getTypeName().equals(VlibProperties.getString("Time"))) {
	  info = new FieldButton(timestampImg);
	} else {
	  info = new FieldButton(listImg);
	}
	  
	this.label.addStyleName(KopiTheme.AUTOFILL_LABEL_STYLE);
	this.label.addLayoutClickListener(new LayoutClickListener() {
	    
	  @Override
	  public void layoutClick(LayoutClickEvent event) {
            performAutoFillAction();
	  }
	});
      }
      
      if (model.getDecrementCommand() != null) {
    	decr = new FieldButton(leftImg);  
    	decr.addClickListener(new Button.ClickListener() {
			
  	  @Override
  	  public void buttonClick(ClickEvent event) {
  	    DField.this.model.getDecrementCommand().performAction();
  	  }
  	});
    	decr.setImmediate(true);
    	decr.setEnabled(getModel().getDefaultAccess() > VConstants.ACS_SKIPPED);
    	addComponent(decr); 	  
      }
      if (model.getIncrementCommand() != null) {
    	incr=new FieldButton(rightImg);
        incr.addClickListener(new Button.ClickListener() {

          @Override
          public void buttonClick(ClickEvent event) {
    	    DField.this.model.getIncrementCommand().performAction();
    	  }
    	});
    	incr.setImmediate(true);
    	incr.setEnabled(getModel().getDefaultAccess() > VConstants.ACS_SKIPPED);
    	addComponent(incr);
      }
    }
  }	
   
  //----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------

  /**
   * Enters this field.
   * @param refresh Should we refresh GUI ?
   */
  public void enter(boolean refresh) {
    updateFocus();
  }

  /**
   * Leaves this field.
   */
  public void leave() {
    updateFocus();
  }
 
  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------

  /**
   * Field cell renderer
   */
  @Override
  public void setPosition(int pos) {
    this.pos = pos;
  }

  /**
   * Field cell renderer
   * @return the position in chart (0..nbDisplay)
   */
  @Override
  public int getPosition() {
    return pos;
  }
 
  /**
   * Returns the alignment.
   * @return the alignment.
   */
  public int getAlign() {
    return align;
  }
  
  @Override
  public VField getModel() {
    return model.getModel();
  }
  
  @Override
  public void setInDetail(boolean detail) {
    inDetail = detail;
    label.setInDetailMode(detail);
  }

  /**
   * Returns {@code true} is the field belongs to the detail view.
   * @return {@code true} is the field belongs to the detail view.
   */
  public boolean isInDetail() {
    return inDetail;
  }
  
  @Override
  public UComponent getAutofillButton() {
    return info;
  }

  /**
   * Returns the row controller.
   * @return The row controller.
   */
  public VFieldUI getRowController() {
    return model;
  }
 
  //-------------------------------------------------
  // UTILS
  //-------------------------------------------------

  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */
  public void update() {
    // overridden in subclasses
  }
  
  @Override
  public void updateText() {
    // overridden in subclasses
  }
  
  @Override
  public void updateFocus() {
    if (modelHasFocus()) {
      final VForm       form = getModel().getForm();

      form.setInformationText(getModel().getToolTip());
      form.setFieldSearchOperator(getModel().getSearchOperator());
    }
  }
  
  @Override
  public void updateAccess() {   
    access = getAccess();
    BackgroundThreadHandler.start(new Runnable() {
      
      @Override
      public void run() { 
        setVisible(access != VConstants.ACS_HIDDEN);
      }
    });
    update(info);
    update(incr);
    update(decr);
    update(label);
  }

  /**
   * Updates a given field button.
   * @param button The button to be updated.
   */
  private void update(final Button button) {
    if (button != null) {
      boolean	was = button.isEnabled();
      final boolean	will = access >= VConstants.ACS_VISIT;
      if (was != will) {
	BackgroundThreadHandler.start(new Runnable() {
	  
	  @Override
	  public void run() {
            button.setEnabled(will);
	  }
	});
      }
    }
  }
  
  /**
   * Updates a given field label.
   * @param label The label to be updated.
   */
  private void update(final DLabel label) {
    if (label != null) {
      BackgroundThreadHandler.start(new Runnable() {
        
        @Override
        public void run() {
          boolean	was = label.isEnabled();
          boolean	will = access >= VConstants.ACS_VISIT;
          
          if (was != will) {
            label.setEnabled(will);
          }
        }
      });
    }
  }
 
  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
 
  /**
   * Returns the object associed to record r
   *
   * @param	r the position of the record
   * @return	the displayed value at this position
   */
  public abstract Object getObject(); 

  /**
   * set blink state
   * @param b The blink ability.
   */
  public abstract void setBlink(boolean b);
 
  //-------------------------------------------------
  // PROTECTED UTILS
  //-------------------------------------------------
 
  /**
   * Returns {@code true} if the field model is focused.
   * @return {@code true} if the field model is focused.
   */
  protected final boolean modelHasFocus() {
    if (getModel() == null) {
      return false;
    }

    final VBlock block = getModel().getBlock();
    return getModel().hasFocus() && block.getActiveRecord() == getBlockView().getRecordFromDisplayLine(pos);
  }

  /**
   * Returns {@code true} if the field is in skipped mode.
   * @return {@code true} if the field is in skipped mode.
   */
  protected final boolean isSkipped() {
    final VBlock block = getModel().getBlock();
    return getAccess() == VConstants.ACS_SKIPPED
      || !block.isRecordAccessible(getBlockView().getRecordFromDisplayLine(getPosition()));
  }
  
  @Override
  public final int getAccess() {
    return getAccessAt(getPosition());
  }

  /**
   * Returns the field access at a given line.
   * @param at The desired line.
   * @return The field access.
   */
  protected final int getAccessAt(int at) {
    if (getModel() != null) {
      return getModel().getAccess(getBlockView().getRecordFromDisplayLine(at));
    } else {
      return VConstants.ACS_SKIPPED;
    }
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------

  /**
   * prepare a snapshot
   * @param fieldPos position of this field within block visible fields
   */
  @Override
  public void prepareSnapshot(int fieldPos, boolean activ) {
    label.prepareSnapshot(activ);

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
 
  //-------------------------------------------------
  // FIELD IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public UBlock getBlockView() {
    return model.getBlockView();
  }

  //-------------------------------------------------
  // LAYOUTCLICKLISTENER IMPLEMENTATION
  //-------------------------------------------------
  
  @Override
  public void layoutClick(LayoutClickEvent event) {
    if (!modelHasFocus()) {
      // an empty row in a chart has not calculated
      // the access for each field (ACCESS Trigger)
      if (model.getBlock().isMulti()) {
	final int	recno = getBlockView().getRecordFromDisplayLine(DField.this.getPosition());

        if (! model.getBlock().isRecordFilled(recno)) {
	  model.getBlock().updateAccess(recno);
        }
      }

      if (!model.getBlock().isMulti() 
	  || model.getBlock().isDetailMode() == isInDetail()
	  || model.getBlock().noChart()) {
        KopiAction	action = new KopiAction("mouse1") {
	  public void execute() throws VException {
	    model.transferFocus(DField.this); // use here a mouse transferfocus
	  //  UI.getCurrent().push();
	  }
        };
        // execute it as model transforming thread
        // it is not allowed to execute it not with
        // the method performAsync/BasicAction.
        model.performAsyncAction(action);
      }
    }
  }

  /**
   * Performs the auto fill action.
   */
  public void performAutoFillAction() {
    getModel().getForm().performAsyncAction(new KopiAction("autofill") {
      
      @Override
      public void execute() throws VException {
	DField.this.model.transferFocus(DField.this);
	try {
	  DField.this.model.autofillButton();		  
        } catch (VException e) {
          // ignore
          e.printStackTrace();
        }
      }
    });     
  }
  
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected	VFieldUI		model;
  public	DLabel			label;
  protected	FieldButton		info;
  protected	FieldButton		incr;
  protected	FieldButton		decr;

  protected	int			state;		// Display state
  protected	int			pos;
  protected	int			options;
  protected	int			align;
  protected     int			access;		// current access of field
  protected	boolean			isEditable;	// is this field editable
  protected	boolean			mouseInside;	// private events

  private       boolean             	inDetail;

  private static final Resource 	listImg = Utils.getImage("list.png").getResource();
  private static final Resource 	calendarImg = Utils.getImage("calendar.png").getResource();
  private static final Resource 	timestampImg = Utils.getImage("timeStamp.png").getResource();
  private static final Resource 	rightImg = Utils.getImage("arrowright.gif").getResource();
  private static final Resource 	leftImg = Utils.getImage("arrowleft.gif").getResource();
}
