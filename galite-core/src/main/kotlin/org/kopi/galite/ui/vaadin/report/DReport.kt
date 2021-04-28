/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.ui.vaadin.report

import java.awt.Color

import org.kopi.galite.report.MReport
import org.kopi.galite.report.Parameters
import org.kopi.galite.report.Point
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VReport
import org.kopi.galite.report.VReportRow
import org.kopi.galite.report.VSeparatorColumn
import org.kopi.galite.ui.vaadin.visual.DWindow
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VException

import com.vaadin.flow.component.Unit

/**
 * The `DReport` is the visual part of the [VReport] model.
 *
 * The `DReport` ensure the implementation of the [UReport]
 * specifications.
 *
 * @param report The report model.
 */
class DReport(private val report: VReport) : DWindow(report), UReport {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model: MReport = report.model // report model
  private lateinit var table: DTable
  private var parameters: Parameters? = null
  private var selectedColumn = 0

  init {
    model.addReportListener(this)
    getModel()!!.setDisplay(this)
    setSizeFull()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    report.initReport()
    report.setMenu()
    table.focus()
    setInfoTable()
  }

  override fun build() {
    // load personal configuration
    parameters = Parameters(Color(71, 184, 221))
    table = DTable(VTable(model, buildRows(model.getColumnCount())))
    table.isColumnReorderingAllowed = true
    // TODO
    //table.setColumnCollapsingAllowed(true)
    //table.setNullSelectionAllowed(false)
    //table.setCellStyleGenerator(ReportCellStyleGenerator(model, parameters))
    // 200 px is approximately the header window size + the actor pane size
    ui.ifPresent {
      it.page.retrieveExtendedClientDetails {
        table.setHeight(it.windowInnerHeight.toFloat() - 200, Unit.PIXELS)
      }
    }
    setContent(table)
    resetWidth()
    addTableListeners()
  }

  override fun redisplay() {
    // TODO
  }

  /**
   * Reorders the report columns.
   * @param newOrder The new columns order.
   */
  fun reorder(newOrder: IntArray) {
    model.columnMoved(newOrder)
    table.setColumnOrder(
            newOrder.map { table.getColumnByKey(it.toString()) }
    )
    //BackgroundThreadHandler.access(Runnable { TODO
      for (col in 0 until model.getAccessibleColumnCount()) {
        table.getColumnByKey(col.toString()).isVisible = !(model.getAccessibleColumn(col)!!.isFolded &&
                model.getAccessibleColumn(col) !is VSeparatorColumn)
      }
    //})
  }

  override fun removeColumn(position: Int) {
    model.removeColumn(position)
    model.initializeAfterRemovingColumn(table.convertColumnIndexToView(position))

    // set new order.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until model.getAccessibleColumnCount()) {
      pos[i] = if (model.getDisplayOrder(i) > position) model.getDisplayOrder(i) - 1 else model.getDisplayOrder(i)
    }
    table.dataCommunicator.reset() // TODO
    report.columnMoved(pos)
  }

  override fun addColumn(position: Int) {
    var position = position
    position = table.convertColumnIndexToView(position)
    position += 1
    val headerLabel = "col" + model.getColumnCount()
    model.addColumn(headerLabel, position)
    // move last column to position.
    val pos = IntArray(model.getAccessibleColumnCount())
    for (i in 0 until position) {
      pos[i] = model.getDisplayOrder(i)
    }
    for (i in position + 1 until model.getAccessibleColumnCount()) {
      pos[i] = model.getDisplayOrder(i - 1)
    }
    pos[position] = model.getDisplayOrder(model.getAccessibleColumnCount() - 1)
    table.dataCommunicator.reset() // TODO
    report.columnMoved(pos)
  }

  override fun addColumn() {
    //addColumn(table.convertColumnIndexToModel(table.getColumnCount() - 1))
  }

  override fun getTable(): UReport.UTable {
    return table
  }

  override fun contentChanged() {
    if (this::table.isInitialized) {
      table.model.fireContentChanged()
    }
  }

  override fun columnMoved(pos: IntArray) {
    reorder(pos)
    model.columnMoved(pos)
    redisplay()
  }

  override fun resetWidth() {
    // TODO
  }

  override fun getSelectedColumn(): Int {
    return table.selectedColumn
  }

  override fun getSelectedCell(): Point = Point(table.selectedColumn, table.selectedRow)

  override fun setColumnLabel(column: Int, label: String) {
    // Nothing to do
  }

  /**
   * Notify the report table that the report content has been
   * change in order to update the table content.
   */
  fun fireContentChanged() {
    if (::table.isInitialized) {
      //table.model.fireContentChanged() TODO
      synchronized(table) { report.setMenu() }
    }
  }

  /**
   * Return the columns display order.
   * @return The columns display order.
   */
  val displayOrder: IntArray
    get() {
      val displayOrder = IntArray(model.getColumnCount())
      for (i in 0 until model.getColumnCount()) {
        displayOrder[i] = table.convertColumnIndexToModel(i)
      }
      return displayOrder
    }

  /**
   * Returns the number of columns displayed in the table
   * @return tThe number or columns displayed
   */
  val columnCount: Int
    get() = TODO()

  /**
   * Add listeners to the report table.
   */
  private fun addTableListeners() {
    // TODO
    // Listener for item double click to fold and unfold the row
    table.addItemDoubleClickListener { event ->
      val row = event.item
      val col = event.column.key.toInt()
      if (model.isRowLine(row.rowIndex)) {
        getModel()!!.performAsyncAction(object : Action("edit_line") {
          override fun execute() {
            try {
              report.editLine()
            } catch (ve: VException) {
              // exception thrown by trigger.
              throw ve
            }
          }
        })
      } else {
        if (model.isRowFold(row.rowIndex, col)) {
          model.unfoldingRow(row.rowIndex, col)
        } else {
          model.foldingRow(row.rowIndex, col)
        }
      }
    }

    // Listener for column reorder
    table.addColumnReorderListener { event ->
      table.viewColumns = event.columns.map { it.key.toInt() }
      val newColumnOrder = IntArray(model.getColumnCount())
      val visibleColumns = table.viewColumns
      var hiddenColumnsCount = 0
      for (i in newColumnOrder.indices) {
        if (!model.getAccessibleColumn(i)!!.isVisible) {
          hiddenColumnsCount += 1
          newColumnOrder[i] = model.getDisplayOrder(i)
        } else {
          newColumnOrder[i] = visibleColumns!![i - hiddenColumnsCount]
        }
      }
      model.columnMoved(newColumnOrder)
    }
  }

  /**
   * Display table information in the footer of the table
   */
  private fun setInfoTable() {
    //TODO()
  }

  /**
   * Builds the grid rows.
   *
   * @param length The ID list length.
   */
  private fun buildRows(length: Int): List<ReportModelItem> {
    val rows = mutableListOf<ReportModelItem>()
    for (i in 0 until length) {
      rows.add(ReportModelItem(i))
    }
    return rows
  }

  //---------------------------------------------------
  // TABLE MODEL ITEM
  //---------------------------------------------------
  /**
   * The `TableModelItem` is the report table
   * data model.
   *
   * @param rowIndex The row index.
   */
  inner class ReportModelItem(val rowIndex: Int) {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun getValueAt(columnIndex: Int): Any {
      return model.accessibleColumns[columnIndex]!!.format(model.getValueAt(rowIndex, columnIndex))
    }

    val reportRow: VReportRow? get() = model.getRow(rowIndex)
  }
}
