package org.kopi.vkopi.lib.ui.vaadin.addons.client.base;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ButtonBase;
/**
 * An input element type button that cannot handle icons.
 */
public class VInputButton extends ButtonBase {

  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  
  /**
   * Creates a new input button.
   */
  public VInputButton() {
    this(null);
  }
  
  /**
   * Creates a new button widget.
   * @param caption The button caption
   */
  public VInputButton(String caption) {
    super(Document.get().createButtonInputElement());
    if (caption != null) {
      getInputElement().setValue(caption);
    }
    setStyleName(Styles.INPUT_BUTTON);
    sinkEvents(Event.ONCLICK);
    getElement().setAttribute("hideFocus", "true");
    getElement().getStyle().setProperty("outline", "0px");
  }
  
  /**
   * Creates a new button widget.
   * @param caption The button caption.
   * @param clickHandler The click handler.
   */
  public VInputButton(String caption, ClickHandler clickHandler) {
    this(caption);
    addClickHandler(clickHandler);
  }

  /**
   * Override onClick to capture client IP address.
   */
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONCLICK) {
      fetchClientIp();
    }
  }

  /**
   * Fetches the client IP address from the server.
   */
  private void fetchClientIp() {
    String url = URL.encode("/getClientIp"); // Endpoint to retrieve client IP
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

    try {
      builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          if (response.getStatusCode() == 200) {
            String clientIp = response.getText();
            System.out.println("Client IP: " + clientIp);
          } else {
            System.err.println("Failed to fetch client IP: " + response.getStatusText());
          }
        }

        @Override
        public void onError(Request request, Throwable exception) {
          System.err.println("Error fetching client IP: " + exception.getMessage());
        }
      });
    } catch (Exception e) {
      System.err.println("Error initiating request to fetch client IP: " + e.getMessage());
    }
  }


  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  
  /**
   * Fires a click for this button.
   */
  public void click() {
    getInputElement().click();
  }
  
  /**
   * Sets the button caption
   * @param caption The button caption.
   */
  public void setCaption(String caption) {
    getInputElement().setValue(caption);
  }
  
  /**
   * Returns the button caption.
   * @return The button caption.
   */
  public String getCaption() {
    return getInputElement().getValue();
  }
  
  /**
   * Returns the input element.
   * @return the input element.
   */
  public InputElement getInputElement() {
    return getElement().cast();
  }
  
  /**
   * Gets the focus on the button.
   */
  public void focus() {
    setFocus(true);
  }
}
