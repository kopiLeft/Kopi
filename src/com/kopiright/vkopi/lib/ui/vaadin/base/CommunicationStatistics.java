/*
 * Copyright (c) 1990-2009 kopiRight Managed Solutions GmbH
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

package com.kopiright.vkopi.lib.ui.vaadin.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterListener;
import org.atmosphere.cpr.DefaultBroadcasterFactory;
import org.atmosphere.cpr.Deliver;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

/**
 * A helper class to show statistics about communication between client and server side.
 */
public class CommunicationStatistics implements BroadcasterListener {

  //---------------------------------------------------------------------
  // CONSTRTUCTOR
  //---------------------------------------------------------------------
  
  /**
   * Creates a new communication statistics from a display duration interval.
   * @param displayStatInterval The display interval in seconds.
   * @param showCommunicatedMessages Should we show the communicated messages in the statistics report ?
   */
  public CommunicationStatistics(int displayStatInterval, boolean showCommunicatedMessages) {
    this.displayStatInterval = displayStatInterval;
    this.showCommunicatedMessages = showCommunicatedMessages;
    clientToServerMessages = new Stack<CommunicationMessage>();
    serverToClientMessages = new Stack<CommunicationMessage>();
    timer = new Timer("statistics");
  }

  //---------------------------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------------------------
  
  /**
   * Installs the atmosphere broadcaster listener.
   */
  @SuppressWarnings("deprecation")
  protected void installBroadcasterListener() {
    AtmosphereFramework         framework;
    
    framework = DefaultBroadcasterFactory.getDefault().lookup("/*").getBroadcasterConfig().getAtmosphereConfig().framework();
    if (framework != null) {
      framework.addBroadcasterListener(this);
    }
  }
  
  /**
   * Creates the servlet service needed by the customized servlet.
   * @param servlet The VAADIN servlet that will use the service.
   * @param deploymentConfiguration The deployment configuration.
   * @return The created service.
   * @throws ServiceException When the service cannot be created.
   */
  @SuppressWarnings("serial")
  protected VaadinServletService createServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration)
    throws ServiceException
  {
    VaadinServletService service = new VaadinServletService(servlet, deploymentConfiguration) {
      
      /*
       * We need to override the request start method to intercept communication commong from client side.
       */
      @Override
      public void requestStart(VaadinRequest request, VaadinResponse response) {
        // get only JSON requests
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
          try {
            String              message;
            int                 length;
            int                 pipeIndex;
            
            message = readMessage(request.getInputStream());
            // message has length|{"csrfToken":"UIID" ... form.
            // we will extract both message and its size
            pipeIndex = message.indexOf('|');
            length = Integer.parseInt(message.substring(0, pipeIndex));
            message = message.substring(pipeIndex + 1);
            clientToServerMessages.push(new CommunicationMessage(message, length));
          } catch (IOException e) {
            System.err.println("Message cannot be read " + e.getMessage());
          }
        }
        super.requestStart(request, response);
      }
    };
    service.init();
    
    return service;
  }
  
  /**
   * Starts the debugging of the statistics.
   */
  protected void start() {
    // displayStatInterval is in minutes
    timer.scheduleAtFixedRate(new StatisticsDisplayer(), 0, displayStatInterval * 1000);
  }
  
  /**
   * Stops the debugging of the statistics.
   */
  protected void stop() {
    timer.cancel();
  }
  
  /**
   * Reads the content of the given input stream.
   * @param in The input stream to be read.
   * @return The String representation of the input stream.
   * @throws IOException When stream cannot be read.
   */
  protected String readMessage(InputStream in) throws IOException {
    ByteArrayOutputStream       out = new ByteArrayOutputStream();
    byte[]                      b = new byte[1024];
    int                         read;
    
    while ((read = in.read(b)) != -1) {
      out.write(b, 0, read);
    }
    in.close();
    
    return new String(out.toByteArray());
  }
  
  //---------------------------------------------------------------------
  // BROADCASTERLISTENER IMPLEMENTATION
  //---------------------------------------------------------------------
  
  @Override
  public void onAddAtmosphereResource(Broadcaster broadcaster, AtmosphereResource resource) {}

  @Override
  public void onComplete(Broadcaster broadcaster) {}

  @Override
  public void onMessage(Broadcaster broadcaster, Deliver deliver) {
    // intercepted atmosphere message
    if (deliver.getMessage() != null) {
      serverToClientMessages.push(new CommunicationMessage(deliver.getMessage().toString()));
    }
  }

  @Override
  public void onPostCreate(Broadcaster broadcaster) {}

  @Override
  public void onPreDestroy(Broadcaster broadcaster) {}

  @Override
  public void onRemoveAtmosphereResource(Broadcaster broadcaster, AtmosphereResource resource) {}

  //---------------------------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------------------------
  
  /**
   * A representation of a communication message.
   */
  /*package*/ static class CommunicationMessage {
    
    //---------------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------------
    
    /**
     * Creates a new communication message.
     * @param message The message.
     * @param length The message length.
     */
    public CommunicationMessage(String message, int length) {
      this.message = message;
      this.length = length;
    }
    
    /**
     * Creates a new communication message.
     * @param message The message.
     */
    public CommunicationMessage(String message) {
      this(message, message.getBytes().length);
    }
    
    //---------------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------------
    
    @Override
    public String toString() {
      return length + " | " + message;
    }
    
    //---------------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------------
    
    public final String                 message;
    public final int                    length;
  }
  
  /**
   * A statistics displayer for communication traffic.
   */
  /*package*/ class StatisticsDisplayer extends TimerTask {

    @Override
    public void run() {
      System.out.println("--------------------------------------------------------------------------------");
      System.out.println("--------------------------- COMMUNICATION STATISTICS ---------------------------");
      System.out.println("--------------------------------------------------------------------------------");
      System.out.println("Client ==> Server : " + clientToServerMessages.size() + " messages with " + getMessagesTotalSize(clientToServerMessages) + " bytes exchanged");
      System.out.println("Server ==> Client : " + serverToClientMessages.size() + " messages with " + getMessagesTotalSize(serverToClientMessages) + " bytes exchanged");
      if (showCommunicatedMessages) {
        System.out.println(" == Messages from client to server side == ");
        for (CommunicationMessage message : clientToServerMessages) {
          System.out.println("Client ==> Server message " + message);
        }
        System.out.println(" == Messages from server to client side == ");
        for (CommunicationMessage message : serverToClientMessages) {
          System.out.println("Server ==> Client message " + message);
        }
      }
      System.out.println("--------------------------------------------------------------------------------");
    }
    
    /**
     * Calculates the total size of the messages contained in the given stack.
     * @param messages The stack of message.
     * @return The total size of the communicated messages.
     */
    protected int getMessagesTotalSize(Stack<CommunicationMessage> messages) {
      int               totalSize;
      
      totalSize = 0;
      for (CommunicationMessage message : messages) {
        totalSize += message.length;
      }
      
      return totalSize;
    }
  }
  
  //---------------------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------------------
  
  private final Stack<CommunicationMessage>     clientToServerMessages;
  private final Stack<CommunicationMessage>     serverToClientMessages;
  private final Timer                           timer;
  private final int                             displayStatInterval;
  private final boolean                         showCommunicatedMessages;
}
