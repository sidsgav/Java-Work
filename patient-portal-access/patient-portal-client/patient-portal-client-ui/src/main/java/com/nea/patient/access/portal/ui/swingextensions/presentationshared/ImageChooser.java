package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays a {@code JFileChooser} so that the user is able to select an image file. Only file types
 * of gif, jpeg and png are supported.
 */
@SuppressWarnings("serial")
public final class ImageChooser extends JFileChooser {

  private static final Logger logger = LoggerFactory.getLogger(ImageChooser.class);

  /**
   * Default constructor. It sets up the file filter to only allow supported image files to be
   * selected.
   */
  public ImageChooser() {
    super();

    setFileFilter(new ImageChooserFilter());
    setMultiSelectionEnabled(false);
    setFileSelectionMode(JFileChooser.FILES_ONLY);

    if (logger.isDebugEnabled()) {
      logger.debug("ImageChooser created");
    }
  }

  /**
   * Constructor to allow this component to be created and launched by any component requiring an
   * image to be loaded. It sets up the file filter to only allow supported image files to be
   * selected.
   *
   * @param dialogTitle title to display in dialog's title bar (which should be internationalised if
   *        required) or {@code null} if the dialog has no title.
   */
  public ImageChooser(final String dialogTitle) {
    super();

    setFileFilter(new ImageChooserFilter());
    setMultiSelectionEnabled(false);
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setDialogTitle(dialogTitle);

    if (logger.isDebugEnabled()) {
      logger.debug("ImageChooser created");
    }
  }

  /**
   * Inner class used to specify a filter for the chooser dialog. Only certain file types are
   * allowed, which is controlled by this filter.
   */
  private final class ImageChooserFilter extends FileFilter {

    private ImageChooserFilter() {
      super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file) {
      final String filename = file.getName();
      boolean acceptable = false;

      if (file.isDirectory()) {
        return true;
      }

      if ((filename.endsWith(".gif")) ||
          (filename.endsWith(".png")) ||
          (filename.endsWith(".jpeg")) ||
          (filename.endsWith(".jpg"))) {
        acceptable = true;
      }
      return acceptable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
      return "*.gif; *.png; *.jpeg; *.jpg";
    }
  }
}
