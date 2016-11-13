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

window.org_kopi_vkopi_lib_ui_vaadin_plotly_base_PlotlyChart = function () {
  this.onStateChange = function () {
    // read state
    var domId = this.getState().domId;
    var data = this.getState().data;
    var layout = this.getState().layout;

    // evaluate plotly JS which needs to define var "options"
    eval(data);
    eval(layout);
    // set chart context
    Plotly.newPlot(domId, data, layout, {displayModeBar: false});
  };
};