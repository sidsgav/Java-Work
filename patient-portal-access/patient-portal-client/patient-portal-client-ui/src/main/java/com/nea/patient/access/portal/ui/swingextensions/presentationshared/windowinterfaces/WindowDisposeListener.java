package com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces;

/**
 * An interface which can be implemented by a subclass of {@code JFrame}, {@code JDialog} or a
 * component that is capable of launching child windows but is not a descendant of
 * {@code Window}.<br>
 * This interface allows the owner of the child dialog to be notified when it's disposed and take
 * any further action such as allowing the owner to clear any reference to the disposed child
 * window. Thus it provides control to the parent owner to ensure that there is only one instance of
 * the required child window instantiated.<br>
 * The parent component should implement this interface if it needs to launch child {@code Dialog}s
 * that are required to be displayed in non-modal form. In the case of modal {@code Dialog}s the
 * parent window is blocked until the child modal {@code Dialog} is closed and thus the use of this
 * interface is not required.<br>
 * In order for the parent component to clear any reference held against the launched secondary
 * window, the secondary window needs to call the method on this interface when it's disposed and
 * supply its window identifier type to the method call. When the parent component enters into this
 * method, it simply switches on the {@code windowIdentifier} parameter to clear any required
 * reference of the secondary window it previously instantiated. Thus, if the secondary window is
 * required to be re-launched, the reference to the window should now be {@code null} and therefore
 * the parent component can decide to re-instantiate the child window.<br>
 * <br>
 * The Window identifiers can either be declared in this interface declaration or some other place.
 * Irrespective of where they are declared, each identifier needs to be unique in value.<br>
 * <br>
 * The classes {@code MDialog} and {@code MFrame} currently make the method call available in this
 * interface upon window dispose.
 */
public interface WindowDisposeListener {

  /**
   * As every application has a main application window, the window identifier for this window is
   * declared here.
   */
  int MAIN_PRIMARY_WINDOW = 0;

  /**
   * Needs to be invoked by the child window when it's disposed to inform the parent component of
   * the disposed state. The parent component should use the supplied {@code windowIdentifier}
   * parameter to determine which locally held window reference to clear (i.e. {@code null}).<br>
   * This method should not be called by the child window if it's simply made invisible.
   *
   * @param windowIdentifier unique identifier to allow the parent component to easily distinguish
   *        which reference to disposed child window to clear.
   */
  void clearWindowReference(int windowIdentifier);
}
