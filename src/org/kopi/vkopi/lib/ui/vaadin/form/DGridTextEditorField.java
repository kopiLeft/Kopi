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

package org.kopi.vkopi.lib.ui.vaadin.form;

import java.util.Locale;

import org.kopi.vkopi.lib.form.ModelTransformer;
import org.kopi.vkopi.lib.form.UTextField;
import org.kopi.vkopi.lib.form.VCodeField;
import org.kopi.vkopi.lib.form.VConstants;
import org.kopi.vkopi.lib.form.VDateField;
import org.kopi.vkopi.lib.form.VFieldUI;
import org.kopi.vkopi.lib.form.VFixnumField;
import org.kopi.vkopi.lib.form.VIntegerField;
import org.kopi.vkopi.lib.form.VMonthField;
import org.kopi.vkopi.lib.form.VStringField;
import org.kopi.vkopi.lib.form.VTimeField;
import org.kopi.vkopi.lib.form.VTimestampField;
import org.kopi.vkopi.lib.form.VWeekField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorDateField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorEnumField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorFixnumField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorIntegerField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorMonthField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextAreaField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField.SuggestionsQueryEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField.SuggestionsQueryListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField.TextChangeEvent;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTextField.TextChangeListener;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTimeField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorTimestampField;
import org.kopi.vkopi.lib.ui.vaadin.addons.GridEditorWeekField;
import org.kopi.vkopi.lib.ui.vaadin.base.BackgroundThreadHandler;
import org.kopi.vkopi.lib.visual.KopiAction;
import org.kopi.vkopi.lib.visual.VException;
import org.kopi.vkopi.lib.visual.VlibProperties;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;

/**
 * A grid text editor based on custom components.
 */
@SuppressWarnings("serial")
public class DGridTextEditorField extends DGridEditorField<String> implements UTextField {

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  
  public DGridTextEditorField(VFieldUI columnView,
                              DGridEditorLabel label,
                              int align,
                              int options)
  {
    super(columnView, label, align, options);
    scanner = (options & VConstants.FDO_NOECHO) != 0 && getModel().getHeight() > 1;
    if (getModel().getHeight() == 1 || (!scanner && ((getModel().getTypeOptions() & VConstants.FDO_DYNAMIC_NL) > 0))) {
      transformer = new DefaultTransformer(getModel().getWidth(), getModel().getHeight());
    } else if (!scanner) {
      transformer = new NewlineTransformer(getModel().getWidth(), getModel().getHeight());
    } else {
      transformer = new ScannerTransformer(getEditor());
    }
    getEditor().addTextChangeListener(new TextChangeListener() {
      
      @Override
      public void onTextChange(TextChangeEvent event) {
        checkText(event.getNewText(), isChanged(event.getOldText(), event.getNewText()));
      }
      
      /**
       * Returns {@code true} if there is a difference between the old and the new text.
       * @param oldText The old text value.
       * @param newText The new text value.
       * @return {@code true} if there is a difference between the old and the new text.
       */
      private boolean isChanged(String oldText, String newText) {
        if (oldText == null) {
          oldText = ""; // replace null by empty string to avoid null pointer exceptions
        }
        
        if (newText == null) {
          newText = "";
        }
        
        return !oldText.equals(newText);
      }
    });
    
    getEditor().addSuggestionsQueryListener(new SuggestionsQueryListener() {

      @Override
      public void onSuggestionsQuery(final SuggestionsQueryEvent event) {
        getModel().getForm().performAsyncAction(new KopiAction("SUGGESTIONS_QUERY") {
          
          @Override
          public void execute() throws VException {
            final String[][]            suggestions;
            
            suggestions = getModel().getSuggestions(event.getQuery());
            if (suggestions != null) {
              BackgroundThreadHandler.access(new Runnable() {
                
                @Override
                public void run() {
                  getEditor().setSuggestions(suggestions, event.getQuery());
                }
              });
            }
          }
        });
      }
    });
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  @Override
  public Object getObject() {
    return getEditor().getValue();
  }
  
  @Override
  public void updateText() {
    final String        newModelTxt = getModel().getText(getBlockView().getRecordFromDisplayLine(getPosition()));

    BackgroundThreadHandler.access(new Runnable() {

      @Override
      public void run() {
        getEditor().setValue(transformer.toGui(newModelTxt).trim());
      }
    });

    if (modelHasFocus() && !selectionAfterUpdateDisabled) {     
      selectionAfterUpdateDisabled = false;
    }
  }

  @Override
  public void updateFocus() {
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
  
  @Override
  protected String getNullRepresentation() {
    return "";
  }
  
  @Override
  protected void reset() {
    inside = false;
    selectionAfterUpdateDisabled = false;
    super.reset();
  }

  @Override
  public String getText() {
    return getEditor().getDisplayedValue();
  }

  @Override
  public void setHasCriticalValue(boolean b) {}

  @Override
  public void addSelectionFocusListener() {}

  @Override
  public void removeSelectionFocusListener() {}

  @Override
  public void setSelectionAfterUpdateDisabled(boolean disable) {
    selectionAfterUpdateDisabled = disable;
  }
  
  @Override
  public GridEditorTextField getEditor() {
    return (GridEditorTextField) super.getEditor();
  }
  
  @Override
  protected GridEditorTextField createEditor() {
    GridEditorTextField         editor;
    
    editor = createEditorField();
    editor.setAlignment(columnView.getModel().getAlign());
    editor.setAutocompleteLength(columnView.getModel().getAutocompleteLength());
    editor.setHasAutocomplete(columnView.getModel().hasAutocomplete());
    editor.setNavigationDelegationMode(getNavigationDelegationMode());
    editor.setHasAutofill(columnView.hasAutofill());
    editor.setHasPreFieldTrigger(columnView.getModel().hasTrigger(VConstants.TRG_PREFLD));
    editor.addActors(getActors());
    editor.setConvertType(getConvertType(columnView.getModel()));
    
    return editor;
  }
  
  @Override
  protected Converter<String, Object> createConverter() {
    return new Converter<String, Object>() {
      
      @Override
      public Class<String> getPresentationType() {
        return String.class;
      }
      
      @Override
      public Class<Object> getModelType() {
        return Object.class;
      }
      
      @Override
      public String convertToPresentation(Object value, Class<? extends String> targetType, Locale locale)
        throws ConversionException
      {
        return transformer.toGui(getModel().toText(value));
      }
      
      @Override
      public Object convertToModel(String value, Class<? extends Object> targetType, Locale locale)
        throws ConversionException
      {
        try {
          return getModel().toObject(transformer.toModel(value));
        } catch (VException e) {
          throw new ConversionException(e);
        }
      }
    };
  }
  
  @Override
  protected Renderer<String> createRenderer() {
    return new TextRenderer();
  }
  
  /**
   * Creates an editor field according to the field model.
   * @return The created editor field.
   */
  protected GridEditorTextField createEditorField() {
    GridEditorTextField         editor;
    
    if (getModel() instanceof VStringField) {
      // string field & text area
      if (getModel().getHeight() > 1) {
        editor = createTextEditorField();
      } else {
        editor = createStringEditorField();
      }
    } else if (getModel() instanceof VIntegerField) {
      // integer fields
      editor = createIntegerEditorField();
    } else if (getModel() instanceof VMonthField) {
      // month field
      editor = createMonthEditorField();
    } else if (getModel() instanceof VDateField) {
      // date field
      editor = createDateEditorField();
    } else if (getModel() instanceof VWeekField) {
      // week field
      editor = createWeekEditorField();
    } else if (getModel() instanceof VTimeField) {
      // time field
      editor = createTimeEditorField();
    } else if (getModel() instanceof VCodeField) {
      // code field
      editor = createEnumEditorField();
    } else if (getModel() instanceof VFixnumField) {
      editor = createFixnumEditorField();
    } else if (getModel() instanceof VTimestampField) {
      editor = createTimestampEditorField();
    } else {
      throw new IllegalArgumentException("unknown field model : " + getModel().getClass().getName());
    }
    
    return editor;
  }
  
  /**
   * Creates a string editor for the grid block.
   * @return The created editor
   */
  protected GridEditorTextField createStringEditorField() {
    return new GridEditorTextField(getModel().getWidth());
  }
  /**
   * Creates a text editor for the grid block.
   * @return The created editor
   */
  protected GridEditorTextAreaField createTextEditorField() {
    boolean             scanner;
    
    scanner = (options & VConstants.FDO_NOECHO) != 0 && getModel().getHeight() > 1;
    return new GridEditorTextAreaField(scanner ? 40 : getModel().getWidth(),
                                       getModel().getHeight(),
                                       ((VStringField)getModel()).getVisibleHeight(),
                                       (!scanner && (options & VConstants.FDO_DYNAMIC_NL) > 0));
  }
  
  /**
   * Creates an integer editor for the grid block.
   * @return The created editor
   */
  protected GridEditorIntegerField createIntegerEditorField() {
    VIntegerField               model;
    
    model = (VIntegerField) getModel();
    return new GridEditorIntegerField(model.getWidth(),
                                      model.getMaxValue(),
                                      model.getMaxValue());
  }
  /**
   * Creates a deciaml editor for the grid block.
   * @return The created editor
   */
  protected GridEditorFixnumField createFixnumEditorField() {
    VFixnumField                model;
    
    model = (VFixnumField) getModel();
    return new GridEditorFixnumField(model.getWidth(),
                                     model.getMaxValue().doubleValue(),
                                     model.getMaxValue().doubleValue(),
                                     model.getMaxScale(),
                                     model.isFraction());
  }
  
  /**
   * Creates an month editor for the grid block.
   * @return The created editor
   */
  protected GridEditorEnumField createEnumEditorField() {
    return new GridEditorEnumField(getModel().getWidth(), ((VCodeField)getModel()).getLabels());
  }
  
  /**
   * Creates an month editor for the grid block.
   * @return The created editor
   */
  protected GridEditorMonthField createMonthEditorField() {
    return new GridEditorMonthField();
  }
  
  /**
   * Creates an date editor for the grid block.
   * @return The created editor
   */
  protected GridEditorDateField createDateEditorField() {
    return new GridEditorDateField();
  }
  
  /**
   * Creates an time editor for the grid block.
   * @return The created editor
   */
  protected GridEditorTimeField createTimeEditorField() {
    return new GridEditorTimeField();
  }
  
  /**
   * Creates an time stamp editor for the grid block.
   * @return The created editor
   */
  protected GridEditorTimestampField createTimestampEditorField() {
    return new GridEditorTimestampField();
  }
  
  /**
   * Creates an week editor for the grid block.
   * @return The created editor
   */
  protected GridEditorWeekField createWeekEditorField() {
    return new GridEditorWeekField();
  }

  /**
   * Reinstalls the focus listener.
   */
  protected void reInstallSelectionFocusListener() {
    removeSelectionFocusListener();
    addSelectionFocusListener();
  }

  /**
   * Leaves the field.
   */
  private synchronized void leaveMe() {
    reInstallSelectionFocusListener();
    // update GUI: for scanner necessary
    if (scanner) {
      // trick: it is now displayed on a different way
      BackgroundThreadHandler.access(new Runnable() {
        
        @Override
        public void run() {
          getEditor().setValue(transformer.toModel(getText()));
        }
      });
    }
  }

  /**
   * Gets the focus to this field.
   */
  private synchronized void enterMe() {
    BackgroundThreadHandler.access(new Runnable() {
      
      @Override
      public void run() {
        if (scanner) {
          getEditor().setValue(transformer.toGui(""));
        }
        getEditor().focus();
      }
    });
  }

  /**
   * Checks the given text.
   * @param s The text to be checked.
   * @param changed Is value changed ?
   */
  private void checkText(String s, boolean changed) {
    String      text = transformer.toModel(s);

    if (!transformer.checkFormat(text)) {
      return;
    }

    if (getModel().checkText(text) && changed) {
      getModel().setChangedUI(true);
    }

    getModel().setChanged(changed);
  }

  // ----------------------------------------------------------------------
  // TRANSFORMERS IMPLEMENTATION
  // ----------------------------------------------------------------------

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

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    
    int                                 col;
    int                                 row;
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
    public ScannerTransformer(GridEditorField<String> field) {
      this.field = field;
    }
    
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    
    @Override
    public String toGui(String modelTxt) {
      if (modelTxt == null || "".equals(modelTxt)) {
        return VlibProperties.getString("scan-ready");
      } else if (!field.isReadOnly()) {
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

    private GridEditorField<String>     field;
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

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------

    private final int                   col;
    private final int                   row;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  
  protected boolean                     inside;
  protected boolean                     scanner;
  private   boolean                     selectionAfterUpdateDisabled;
  protected final ModelTransformer      transformer;
}
