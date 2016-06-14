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

import org.kopi.vaadin.addons.ContextMenu;
import org.kopi.vaadin.addons.TextField;
import org.kopi.vaadin.addons.TextValueChangeListener;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import com.kopiright.vkopi.lib.form.ModelTransformer;
import com.kopiright.vkopi.lib.form.UTextField;
import com.kopiright.vkopi.lib.form.VCodeField;
import com.kopiright.vkopi.lib.form.VConstants;
import com.kopiright.vkopi.lib.form.VDateField;
import com.kopiright.vkopi.lib.form.VFieldUI;
import com.kopiright.vkopi.lib.form.VFixnumField;
import com.kopiright.vkopi.lib.form.VIntegerField;
import com.kopiright.vkopi.lib.form.VMonthField;
import com.kopiright.vkopi.lib.form.VStringField;
import com.kopiright.vkopi.lib.form.VTimeField;
import com.kopiright.vkopi.lib.form.VTimestampField;
import com.kopiright.vkopi.lib.form.VWeekField;
import com.kopiright.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import com.kopiright.vkopi.lib.ui.vaadin.base.Utils;
import com.kopiright.vkopi.lib.visual.VlibProperties;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.UI;

/**
 * The <code>DTextField</code> is the vaadin implementation
 * of the {@link UTextField} specifications.
 */
@SuppressWarnings("serial")
public class DTextField extends DField implements UTextField, VConstants {

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>DTextField</code> instance.
   * @param model The row controller.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options.
   * @param detail Does the field belongs to the detail view ?
   */
  public DTextField(final VFieldUI model,
                    DLabel label,
                    int align,
                    int options,
                    boolean detail)
  {
    super(model, label, align, options, detail);
    this.label = label;
    //model.getModel().addFieldChangeListener(this);
    noEdit = (options & VConstants.FDO_NOEDIT) != 0;
    scanner = (options & VConstants.FDO_NOECHO) != 0 && getModel().getHeight() > 1;

    if (getModel().getHeight() == 1
        || (!scanner && ((getModel().getTypeOptions() & FDO_DYNAMIC_NL) > 0)))
    {
      transformer = new DefaultTransformer(getModel().getWidth(),
                                           getModel().getHeight());
    } else if (!scanner) {
      transformer = new NewlineTransformer(getModel().getWidth(),
                                           getModel().getHeight());
    } else {
      transformer = new ScannerTransformer(this);
    }

    field = createFieldGUI((options & VConstants.FDO_NOECHO) != 0,
                           scanner,
                           align);
    field.setImmediate(true);
    valueChangeHandler = new TextValueChangeListener() {
      
      @Override
      public void onTextChange(String oldText, String newText) {
	// ensure that the active field is this field to check text
        // to fire the autoleave trigger on the right field.
	if (getModel() == getModel().getBlock().getActiveField()) {
	  checkText(newText, isChanged(oldText, newText));
	}
      }
      
      /**
       * Returns {@code true} if there is a difference between the old and the new text.
       * @param oldText The old text value.
       * @param newText The new text value.
       * @return {@code true} if there is a difference between the old and the new text.
       */
      protected boolean isChanged(String oldText, String newText) {
	if (oldText == null) {
	  oldText = ""; // replace null by empty string to avoid null pointer exceptions
	}
	
	if (newText == null) {
	  newText = "";
	}
	
	return !oldText.equals(newText);
      }
    };

    field.addTextValueChangeListener(valueChangeHandler);
    createContextMenu();
    setContent(field);
  }

  // --------------------------------------------------
  // CREATE FIELD UI
  // --------------------------------------------------

  /**
   * Creates the field UI component.
   * @param noEcho Password field ?
   * @param scanner Scanner field ?
   * @param align The field alignment.
   * @return The {@link TextField} object.
   */
  private TextField createFieldGUI(boolean noEcho, 
                                   boolean scanner, 
                                   int align) 
  {
    
    TextField      	textfield;

    textfield = new TextField(getModel().getWidth(),
	                      getModel().getHeight(),
	                      (getModel().getHeight() == 1) ? 1 : ((VStringField)getModel()).getVisibleHeight(),
	                      (!scanner && (getModel().getTypeOptions() & FDO_DYNAMIC_NL) > 0),
	                      noEcho,
	                      scanner,
	                      noEdit,
	                      align);
    textfield.setAutocompleteLength(getModel().getAutocompleteLength());
    textfield.setHasAutocomplete(getModel().hasAutocomplete());
    // set field type according to the model
    // this will set the validation strategy on the client side.
    if (getModel() instanceof VStringField) {
      // string field
      textfield.setType(TextField.Type.STRING);
    } else if (getModel() instanceof VIntegerField) {
      // integer field
      textfield.setType(TextField.Type.INTEGER);
    } else if (getModel() instanceof VMonthField) {
      // month field
      textfield.setType(TextField.Type.MONTH);
    } else if (getModel() instanceof VDateField) {
      // date field
      textfield.setType(TextField.Type.DATE);
    } else if (getModel() instanceof VWeekField) {
      // week field
      textfield.setType(TextField.Type.WEEK);
    } else if (getModel() instanceof VTimeField) {
      // time field
      textfield.setType(TextField.Type.TIME);
    } else if (getModel() instanceof VCodeField) {
      // code field
      textfield.setType(TextField.Type.CODE);
      textfield.setEnumerations(((VCodeField)getModel()).getLabels());
    } else if (getModel() instanceof VFixnumField) {
      // fixnum field
      textfield.setType(TextField.Type.FIXNUM);
    } else if (getModel() instanceof VTimestampField) {
      // timestamp field
      textfield.setType(TextField.Type.TIMESTAMP);
    } else {
      throw new IllegalArgumentException("unknown field model : " + getModel().getClass().getName());
    }
    // add navigation handler
    textfield.addTextFieldListener(new KeyNavigator(getModel(), textfield));

    return textfield;
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------

  @Override
  public synchronized void updateAccess() {
    DTextField.super.updateAccess();
    label.update(model, getPosition());
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	field.setEnabled(access >= VConstants.ACS_VISIT);
	setEnabled(access >= VConstants.ACS_VISIT);
      }
    });
  }

  @Override
  public synchronized void updateText() {
    final String	newModelTxt = getModel().getText(getRowController().getBlockView().getRecordFromDisplayLine(getPosition()));
    
    BackgroundThreadHandler.access(new Runnable() {
    
      @Override
      public void run() {
        field.setText(transformer.toGui(newModelTxt).trim());
      }
    });
    
    super.updateText();
    if (modelHasFocus() && !selectionAfterUpdateDisabled) {	
      selectionAfterUpdateDisabled = false;
    } 
  }
  
  @Override
  public void updateColor() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	if (field != null) {
	  if (getForeground() == null && getBackground() == null) {
	    field.removeStyleName(getModel().getBlock().getName() + "-" + getModel().getName() + "-" + getPosition());
	  } else {
	    Styles		styles;
	  
	    field.addStyleName(getModel().getBlock().getName() + "-" + getModel().getName() + "-" + getPosition());
	    styles = Page.getCurrent().getStyles();
	    styles.add(".v-app .k-textfield" + "-" + getModel().getBlock().getName() + "-" + getModel().getName() + "-" + getPosition() + " input.k-textinput {"
	      + "background-color: " + Utils.getCSSColor(getBackground())
	      + "color: " + Utils.getCSSColor(getForeground())
	      + "}"
	    );
	  }
	}
      }
    });
  }

  @Override
  public synchronized void updateFocus() {
    label.update(model, getPosition());
    if (!modelHasFocus()) {
      if (inside) {
        inside = false;
        leaveMe();
      }
    } else {
      if (!inside) {
        inside = true;
        enterMe(); 
      }
    }

    super.updateFocus();
  }

  /**
   * Gets the focus to this field.
   */
  private synchronized void enterMe() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	if (scanner) {
	  field.setText(transformer.toGui(""));
	}
	
	field.setFocus(true);
      }
    });
  }

  /**
   * Leaves the field.
   */
  private synchronized void leaveMe() {
    //field.setReadOnly(false);

    reInstallSelectionFocusListener();
    // update GUI: for
    // scanner nescessary
    if (scanner) {
      // trick: it is now displayed on a different way
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          field.setText(transformer.toModel(field.getText()));
        }
      });
    }
  }

  /**
   * Checks the given text.
   * @param s The text to be checked.
   * @param changed Is value changed ?
   */
  private void checkText(String s, boolean changed) {
    String	text = transformer.toModel(s);

    if (!transformer.checkFormat(text)) {
      return;
    }

    if (getModel().checkText(text) && changed) {
      getModel().onTextChange(text);
    }

    getModel().setChanged(changed);
  }

  // --------------------------------------------------
  // UTILS
  // --------------------------------------------------

  /**
   * Returns the field width.
   * @return The field width.
   */
  public float getFieldWidth() {
    return field.getWidth();
  }

  /**
   * Returns the field width unit.
   * @return The field width unit.
   */
  public Unit getFieldWidthUnits() {
    return field.getWidthUnits();
  }

  /**
   * Converts a given string to a line string.
   * @param source The source text.
   * @param col The column index.
   * @param row The row index.
   * @return The converted string.
   */
  private static String convertToSingleLine(String source, int col, int row) {
    StringBuffer      target = new StringBuffer();
    int               length = source.length();
    int               start = 0;

    while (start < length) {
      int             index = source.indexOf('\n', start);

      if (index-start < col && index != -1) {
        target.append(source.substring(start, index));
        for (int j = index - start; j < col; j++) {
          target.append(' ');
        }
        start = index+1;
        if (start == length) {
          // last line ends with a "new line" -> add an empty line
          for (int j = 0; j < col; j++) {
            target.append(' ');
          }
        }
      } else {
        if (start+col >= length) {
          target.append(source.substring(start, length));
          for (int j = length; j < start+col; j++) {
            target.append(' ');
          }
          start = length;          
        } else {
          // find white space to break line
          int   i;
    
          for (i = start+col-1; i > start; i--) {
            if (Character.isWhitespace(source.charAt(i))) {
              break;
            }
          }
          if (i == start) {
            index = start+col;
          } else {
            index = i+1;
          }
    
          target.append(source.substring(start, index));
          for (int j = (index - start)%col; j != 0 && j < col; j++) {
            target.append(' ');
          }
          start = index;
        }
      }
    }
    return target.toString();
  }

  /**
   * Converts a given string to a fixed line string.
   * @param source The source text.
   * @param col The column index.
   * @param row The row index.
   * @return The converted string.
   */
  private static String convertFixedTextToSingleLine(String source, int col, int row) {
    StringBuffer      target = new StringBuffer();
    int               length = source.length();
    int               start = 0;

    while (start < length) {
      int             index = source.indexOf('\n', start);

      if (index-start < col && index != -1) {
        target.append(source.substring(start, index));
        for (int j = index - start; j < col; j++) {
          target.append(' ');
        }
        start = index+1;
        if (start == length) {
          // last line ends with a "new line" -> add an empty line
          for (int j = 0; j < col; j++) {
            target.append(' ');
          }
        }
      } else {
        if (start+col >= length) {
          target.append(source.substring(start, length));
          for (int j = length; j < start+col; j++) {
            target.append(' ');
          }
          start = length;          
        } else {
          // find white space to break line
          int   i;
    
          for (i = start+col; i > start; i--) {
            if (Character.isWhitespace(source.charAt(i))) {
              break;
            }
          }
          if (i == start) {
            index = start+col;
          } else {
            index = i;
          }
    
          target.append(source.substring(start, index));
          for (int j = (index - start); j < col; j++) {
            target.append(' ');
          }
          start = index + 1;
        }
      }
    }
    return target.toString();
  }

  //---------------------------------------------------
  // TEXTFIELD IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public String getText() {
    return transformer.toModel(field.getText() == null ? "" : field.getText());
  }

  @Override
  public void setHasCriticalValue(boolean b) {
    // ignore
  }

  @Override
  public void addSelectionFocusListener() {
    // ignore
  }

  @Override
  public void removeSelectionFocusListener() {
    // ignore
  }

  /**
   * Reinstalls the focus listener.
   */
  public void reInstallSelectionFocusListener() {
    removeSelectionFocusListener();
    addSelectionFocusListener();
  }

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {
    selectionAfterUpdateDisabled = disable;
  }

  //---------------------------------------------------
  // DFIELD IMPLEMENTATION
  //---------------------------------------------------

  @Override
  public Object getObject() {
    return getText(); 
  } 

  @Override
  public void setBlink(final boolean blink) {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
	field.setBlink(blink);
      }
    });
  }

  //---------------------------------------------------
  //
  //---------------------------------------------------

  /**
   * Default implementation of the {@link ModelTransformer}
   */
  /*package*/ static class DefaultTransformer implements ModelTransformer {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>DefaultTransformer</code> instance.
     * @param col The column index.
     * @param row The row index.
     */
    public DefaultTransformer(int col, int row) {
      this.col = col;
      this.row = row;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public String toGui(String modelTxt) {
      return modelTxt;
    }
    
    @Override
    public String toModel(String guiTxt) {
      return guiTxt;
    }
    
    @Override
    public boolean checkFormat(String source) {
      return (row == 1) ? true : (convertToSingleLine(source, col, row).length() <= row * col);
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    int         			col;
    int         			row;
  }

  /**
   * A scanner model transformer.
   */
  /*package*/ static class ScannerTransformer implements ModelTransformer {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>ScannerTransformer</code> instance.
     * @param field The field view.
     */
    public ScannerTransformer(DTextField field) {
      this.field = field;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public String toGui(String modelTxt) {
      if (modelTxt == null || "".equals(modelTxt)) {
        return VlibProperties.getString("scan-ready");
      } else if (!field.field.isReadOnly()) {
        return VlibProperties.getString("scan-read") + " " + modelTxt;
      } else {
        return VlibProperties.getString("scan-finished");
      }
    }

    @Override
    public String toModel(String guiTxt) {
      return guiTxt;
    }

    @Override
    public boolean checkFormat(String software) {
      return true;
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private DTextField			field;
  }

  /**
   * New line model transformer.
   */
  /*package*/ static class NewlineTransformer implements ModelTransformer {
    
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    
    /**
     * Creates a new <code>NewlineTransformer</code> instance.
     * @param col The column index.
     * @param row The row index.
     */
    public NewlineTransformer(int col, int row) {
      this.col = col;
      this.row = row;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public String toModel(String source) {
      return convertFixedTextToSingleLine(source, col, row);
    }

    @Override
    public String toGui(String source) {
      StringBuffer      target = new StringBuffer();
      int               length = source.length();
      int               usedRows = 1;

      for (int start = 0; start < length; start += col) {
        String          line = source.substring(start, Math.min(start + col, length));
        int             last = -1;

        for (int i = line.length() - 1; last == -1 && i >= 0; --i) {
          if (! Character.isWhitespace(line.charAt(i))) {
            last = i;
          }
        }

        if (last != -1) {
          target.append(line.substring(0, last + 1));
        } 
        if (usedRows < row) {
          if (start+col < length) {
            target.append('\n');
          }
          usedRows++;
        }
      }
      return target.toString();
    }

    @Override
    public boolean checkFormat(String source) {
      return (source.length() <= row * col);
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private final int         		col;
    private final int         		row;
  }
  
  /*
  //---------------------------------------------
  // FieldChangeListener IMPLEMENTATION
  //---------------------------------------------
  
  @Override
  public void labelChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void searchOperatorChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void valueChanged(int r) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void accessChanged(int r) {
    if (!(model.getBlockView() instanceof DChartBlock)) {
      if (model.getModel().getDefaultAccess() == ACS_MUSTFILL) {
	if (!startDisplayed) {
          UI.getCurrent().access(new Runnable() {
	    @Override
	    public void run() {
	      Label star = new Label("*");
	      star.addStyleName(KopiTheme.STAR_STYLE);
	      addComponentAsFirst(star);
	      startDisplayed = true;
	    }
          });
	}
      }else{
	if(startDisplayed){
	  UI.getCurrent().access(new Runnable() {
	    @Override
	    public void run() {
	      if (getComponentError() != null) {
	        setComponentError(null);
	      }
	      removeComponent(getComponent(0));
	      startDisplayed = false;
	    }
	  });
	}
      }
    }
  }*/

  /**
   * Add the field context menu.
   */
  protected void createContextMenu() {
    if (model.hasAutofill() && getModel().getDefaultAccess() > VConstants.ACS_SKIPPED) {
      final ContextMenu		contextMenu = new ContextMenu();
      
      contextMenu.addItem(VlibProperties.getString("item-index")).setData(VlibProperties.getString("item-index"));
      contextMenu.addItemClickListener(new ContextMenuItemClickListener() {
	
	@Override
	public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
	  contextMenu.hide();
	  performAutoFillAction();
        }
      });
      
      contextMenu.setAsContextMenuOf(field);
    }
  }
  
  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------

  private TextField             	field;		// the text component
  protected boolean                     inside;
  protected boolean                     noEdit;
  protected boolean			scanner;
  private   boolean 		        selectionAfterUpdateDisabled;
  private   DLabel		        label;
  protected ModelTransformer            transformer;
  private   TextValueChangeListener	valueChangeHandler;
}
