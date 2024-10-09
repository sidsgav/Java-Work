package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Spring;

/**
 * A class to equalise the column widths across multiple {@code SpringPacker} panels. The labels and
 * {@code JComponent}s are columnated and this class looks across the {@code SpringPacker}s, finds
 * the widest columns and sets the same widths for those columns across all the
 * {@code SpringPacker}s.<br>
 * This class is used in combination with {@code SpringPacker} to provide consistent columnation
 * across multiple {@code SpringPacker}s which can be in different {@code JPanel}s.
 */
public final class SpringPackerEqualiser {

  private List<SpringPacker> packerList;

  private Spring widestLabelColumn;

  private List<Spring> widestComponentColumn;

  /**
   * Default constructor.
   */
  public SpringPackerEqualiser() {
    super();
    packerList = new ArrayList<SpringPacker>();
    widestComponentColumn = new ArrayList<Spring>();
  }

  /**
   * Invoke to add an individual {@code SpringPacker} panel which requires columnation against other
   * added panels.
   *
   * @param packer individual panel to add.
   */
  public void add(final SpringPacker packer) {
    packerList.add(packer);

    // As each SpringPacker is added, keep running total of the widest label column so far.
    if (widestLabelColumn == null) {
      widestLabelColumn = packer.getLabelsWidth(); // first packer
    } else {
      widestLabelColumn = Spring.max(widestLabelColumn, packer.getLabelsWidth());
    }

    // As each SpringPacker is added, keep running total of the widest component columns so far
    // widestComponentColumn is an ArrayList of of Springs, one for each column.
    for (int i = 0; i < packer.getNComponentColumns(); i++) {
      // Check if we have already encountered a packer with this many columns.
      if (widestComponentColumn.size() <= i) {
        // First example with this number of columns, so this must be the widest so far.
        widestComponentColumn.add(packer.getComponentWidth(i));
      } else {
        final Spring widestSoFar = Spring.max(widestComponentColumn.get(i), packer.getComponentWidth(i));
        widestComponentColumn.set(i, widestSoFar);
      }
    }
  }

  /**
   * Equalise the width of the 'label' and 'component' columns across several {@code SpringPacker}s
   * so they are vertically aligned when displayed one above the other. This is done by forcing the
   * width of all the columns to the same width as the widest needed by any of the packers. This
   * method needs to be called once all {@code SpringPacker} panels have been added.
   */
  public void equalise() {
    // If there's only one thing to equalise then don't bother.
    if (packerList.size() > 1) {
      final Iterator<SpringPacker> packers = packerList.iterator();
      // Set the width of the label and component columns to be the same as the
      // widest so that the label and component columns are aligned across the panels.
      while (packers.hasNext()) {
        final SpringPacker packer = packers.next();

        packer.setLabelsWidth(widestLabelColumn);
        for (int i = widestComponentColumn.size() - 1; i >= 0; --i) {
          packer.setComponentWidth(i, widestComponentColumn.get(i));
        }
      }
    }
  }
}
