package com.nea.patient.access.portal.ui;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.nea.patient.access.portal.ui.application.PatientAccessPortalFrame;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.PresentationUtilities;

@SpringBootApplication(scanBasePackages = {"com.nea.patient.access.portal.ui"})
public class SwingApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(SwingApplication.class);

  public static void main(final String[] args) {
    ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(
        SwingApplication.class).headless(false).run(args);

    Locale.setDefault(Locale.UK);

    EventQueue.invokeLater(() -> {
      SwingApplication swingApplication = applicationContext.getBean(SwingApplication.class);
      swingApplication.initialiseUserInterface();

      PatientAccessPortalFrame portalFrame = applicationContext.getBean(PatientAccessPortalFrame.class);
      portalFrame.setApplicationContext(applicationContext);
      PresentationUtilities.centerWindowAndClipIfRequired(null, portalFrame);
      portalFrame.setVisible(true);
    });
  }

  private void initialiseUserInterface() {
    LOGGER.info("Starting Patient Portal Access Client...");

    // Initialise native Look and Feel for application.
    initialiseNativeUILookAndFeel();
  }

  private void initialiseNativeUILookAndFeel() {
    try {
      // Attempt to set the Look And Feel to the native system's Look And Feel
      // if this fails, the cross-platform (Metal) look and feel will be used.
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      LOGGER.warn("Unable to set native Look And Feel: " + e.getMessage());
    }

    // Check if application has been started on Windows platform to determine
    // if the Windows L&F menu option is supported.
    try {
      String osName = System.getProperty("os.name");

      if ((osName != null) && (osName.length() > 0)) {
        PresentationUtilities.setWindowsLookAndFeelSupport(
            osName.startsWith(PresentationUtilities.WINDOWS_OS_NAME_ID));
      }
    } catch (Exception e) {
      LOGGER.warn("Unable to retrieve Property os.name: " + e.getMessage());
    }
  }
}
