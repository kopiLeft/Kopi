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

package org.kopi.vkopi.lib.chart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.kopi.vkopi.lib.base.UComponent;

/**
 * The Chart type view representation.
 */
public interface UChartType extends Serializable, UComponent {

  /**
   * Builds the content of this chart type.
   */
  public void build();
  
  /**
   * Refreshes the content of this chart type.
   */
  public void refresh();
  
  /**
   * Exports the chart type to the PDF format.
   * @param destination Where to write the export.
   * @param options The print options.
   * @throws IOException I/O errors.
   */
  public void exportToPDF(OutputStream destination, VPrintOptions options)
    throws IOException;
  
  /**
   * Exports the chart type to the PNG format.
   * @param destination Where to write the export.
   * @param width The image width.
   * @param height The image height.
   * @throws IOException I/O errors.
   */
  public void exportToPNG(OutputStream destination, int width, int height)
    throws IOException;
  
  /**
   * Exports the chart type to the JPEG format.
   * @param destination Where to write the export.
   * @param width The image width.
   * @param height The image height.
   * @throws IOException I/O errors.
   */
  public void exportToJPEG(OutputStream destination, int width, int height)
    throws IOException;
}
