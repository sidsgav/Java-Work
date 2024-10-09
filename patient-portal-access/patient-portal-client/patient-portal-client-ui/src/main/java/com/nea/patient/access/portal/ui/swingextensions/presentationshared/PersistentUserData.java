package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * This class contains generic static methods to allow user entered textual data on 'Find' and
 * 'Filter' dialogs to be checked and persisted for the lifetime of the application. The data that
 * is persisted is that which is entered in the drop down {@code JComboBox} used by dialogs.<br>
 * The static method is currently called by subclasses {@code PersistentFindAndFilterUserData} and
 * {@code PersistentFindUserData}.
 */
public class PersistentUserData {

  // This default constructor should not be used as this is a utility class.
  // It is specified here so that the default constructor is not visible.
  private PersistentUserData() {
    // Intentionally empty.
  }

  /**
   * This method allows any newly added entries (supplied in parameter {@code suppliedData}) to be
   * persisted for the lifetime of the application. All new entries are added in alphabetical order
   * ignoring case. Duplicate entries are discarded.
   *
   * @param dataContainerToUpdate the persisted container that needs to store any new text entries
   *        entered by the user. The container is type {@code Vector} as it's supported by
   *        {@code DefaultComboBoxModel} which is used by {@code JComboBox} component.
   * @param suppliedData the default model for {@code JComboBox} containing user entered text data
   *        that may need to be copied across and persisted by parameter
   *        {@code dataContainerToUpdate}.
   */
  public static synchronized void updateRequiredDataContainer(
      final Vector<String> dataContainerToUpdate, final DefaultComboBoxModel suppliedData) {
    // Ensure that each element from suppliedData is only added if it does not
    // already exist and it is added in alphabetical order ignoring case.
    for (int i = 0; i < suppliedData.getSize(); i++) {
      final Object element = suppliedData.getElementAt(i);
      if (element != null) {
        // First ensure retrieved element from suppliedData does not already
        // exist in the persisted container.
        if (!checkForDuplicateEntry(element, dataContainerToUpdate)) {
          final String elementValue = element.toString();
          int j;
          // Loop round the container to update.
          for (j = 0; j < dataContainerToUpdate.size(); j++) {
            final String currentElement = dataContainerToUpdate.get(j);
            if (currentElement != null) {
              final int compareRes = elementValue.compareToIgnoreCase(currentElement);
              // If supplied argument (i.e. Vector element) less than element to add
              // then continue.
              if (compareRes > 0) {
                continue;
              }
              // The element to add is either greater than the
              // currentElement parameter or equal to it when
              // ignoring case. Therefore, the element to add
              // parameter generally needs to be inserted
              // before the current entry, except when the two
              // are equal and it needs to be determined
              // whether we need to insert before or after.
              int indexToInsert = j;
              // Check if comparison result returned equality
              // ignoring case.
              if (compareRes == 0) {
                final int equalityRes = elementValue.compareTo(currentElement);
                // Determine whether we need to insert after.
                if (equalityRes < 0) {
                  indexToInsert += 1;
                }
              }
              dataContainerToUpdate.insertElementAt(elementValue, indexToInsert);
              // Copy over the inserted index value.
              j = indexToInsert;
              break;
            }
          }
          if (j == dataContainerToUpdate.size()) {
            // Not been added - Insert at the start/end of the list.
            dataContainerToUpdate.addElement(elementValue);
          }
        }
      }
    }
  }

  private static boolean checkForDuplicateEntry(final Object itemToCheck, final Vector<String> containerToCheck) {
    boolean itemExistsInContainer = false;

    for (String containerEntry : containerToCheck) {
      if (containerEntry != null) {
        if (containerEntry.equals(itemToCheck)) {
          itemExistsInContainer = true;
          break;
        }
      }
    }
    return itemExistsInContainer;
  }
}
