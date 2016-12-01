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

package org.kopi.vkopi.lib.ui.vaadin.report;

import org.kopi.vkopi.lib.report.ColumnStyle;
import org.kopi.vkopi.lib.report.Constants;
import org.kopi.vkopi.lib.report.MReport;
import org.kopi.vkopi.lib.report.Parameters;
import org.kopi.vkopi.lib.report.VReportColumn;
import org.kopi.vkopi.lib.report.VSeparatorColumn;
import org.kopi.vkopi.lib.ui.vaadin.report.StyleGenerator.CSSStyle;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.UI;

/**
 * The <code>ReportCellStyleGenerator</code> is the dynamic report
 * Implementation of the {@link CellStyleGenerator}.
 */
@SuppressWarnings("serial")
public class ReportCellStyleGenerator implements CellStyleGenerator {
  
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  
  /**
   * Creates a new <code>ReportCellStyleGenerator</code> instance.
   * @param model The report model.
   * @param parameters The style parameters.
   */
  public ReportCellStyleGenerator(MReport model, Parameters parameters) {
    this.model = model;
    this.parameters = parameters;
    stylesMap = new CSSStyle[model.getAccessibleColumnCount() + 30][11];
    updateStyles();
  }
  
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  @Override
  public String getStyle(Table source, Object itemId, Object propertyId) { 
    int		level = model.getRow((Integer)itemId).getLevel();
	    
    if (propertyId == null) {
      return null;
    } else { 
      int col = ((Integer)propertyId).intValue();
      if (col > stylesMap.length-1) {
	col = 0;
      }    
      
      return stylesMap[col][level] == null ? null : stylesMap[col][level].getName();
    }
  }

  /**
   * Updates the columns styles.
   */
  public void updateStyles() {
    for (int i = 0; i < 11; i ++) {
      for (int j = 0; j < model.getAccessibleColumnCount(); j ++) {
        String align;
        boolean separator = false;
        VReportColumn column = model.getAccessibleColumn(j);
        ColumnStyle[]	styles = (ColumnStyle[]) column.getStyles();
      
        if(column instanceof VSeparatorColumn){
	  separator = true;
        }
      
        if (column.getAlign()== Constants.ALG_RIGHT) {
	  align = "right";
        } else {
	  align = "left"; 
        }
      
        ColumnStyle style = styles[0];
    
	if (stylesMap[j][i] == null) {
          stylesMap[j][i] = StyleGenerator.getStyle(UI.getCurrent(), parameters, style, i, j, align, separator);         
	} else {
	  stylesMap[j][i].updateStyle(style);
	}
      } 
    }
  }
	  
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
	  
  private MReport				model;
  private Parameters 				parameters;
  private CSSStyle[][]				stylesMap;
}
