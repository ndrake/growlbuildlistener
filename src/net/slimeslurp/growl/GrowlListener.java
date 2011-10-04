/*
 *  Copyright 2007 Nate Drake
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */

package net.slimeslurp.growl;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Hashtable;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;

import com.google.code.jgntp.GntpApplicationInfo;
import com.google.code.jgntp.GntpClient;
import com.google.code.jgntp.Gntp;
import com.google.code.jgntp.GntpListener;
import com.google.code.jgntp.GntpNotification;
import com.google.code.jgntp.GntpErrorStatus;
import com.google.code.jgntp.GntpNotificationInfo;

/**
 * BuildListener that displays information on the current build
 * via Growl/JGrowl.
 *
 *
 * TODO:
 * o Read some things from a properties file (i.e. host, passwd, etc.)
 *
 * @author ndrake
 *
 */
public class GrowlListener implements BuildListener {


    private static final String APP_NAME = "Ant";
    private static final String DEFAULT_GROWL_HOST = "localhost";
    private static final String DEFAULT_GROWL_PASSWD = null;
    private static final int DEFAULT_GROWL_PORT = 23053;

    private static final String FINISHED_STICKY_NAME = "gbl.endsticky";
    private static final String GROWL_HOST_PROP = "gbl.host";
    private static final String GROWL_PASSWD_PROP = "gbl.passwd";    
    private static final String GROWL_PORT_PROP = "gbl.port";        
    private String growlHost;
    private String growlPasswd;
    private int growlPort;
    
    public GrowlListener() {

        // Have to get these from the system properties as the build properties aren't  
        // available in buildStarted()
        //
        // These must be set via the ANT_OPTS env variable       
        growlHost = System.getProperty(GROWL_HOST_PROP, DEFAULT_GROWL_HOST);
        growlPasswd = System.getProperty(GROWL_PASSWD_PROP, DEFAULT_GROWL_PASSWD);
        String p = System.getProperty(GROWL_PORT_PROP, ""+DEFAULT_GROWL_PORT);
        growlPort = Integer.valueOf(p);
        
        System.out.println("new listener!");
        
    }

    /**
     * Called when the build is started.
     *
     * @param event
     */
    public void buildStarted(BuildEvent event) {        
        sendMessage("Build starting...",
                    0, false);
    }

    /**
     * Called when the build is finished.
     *
     * @param event
     */
    public void buildFinished(BuildEvent event) {
        Throwable t = event.getException();
        String projectName = event.getProject().getName();
        Hashtable props = event.getProject().getProperties();
        boolean sticky = false;

        // Check if this message should be sticky or not
        if(props != null && props.containsKey(FINISHED_STICKY_NAME)) {
            sticky = Boolean.parseBoolean((String)props.get(FINISHED_STICKY_NAME));
        } 

        if (t != null) {
            sendMessage("Build failed: " + t.toString(), 
                        2, sticky);
            return;
        }
        sendMessage("Build finished for " + projectName, 
                    0, sticky);
                    
        try {
            Thread.sleep(1500);
        }catch(InterruptedException e) {
            
        }
    }

    // Other messages are currently ignored
    public void messageLogged(BuildEvent event) { }
    public void targetFinished(BuildEvent event) {}
    public void targetStarted(BuildEvent event) {}
    public void taskFinished(BuildEvent event) {}
    public void taskStarted(BuildEvent event) {}


    /**
     * Send a message to Growl/JGrowl.
     *
     * @param msg The message
     * @param priority The message priority
     * @param sticky If true, notification should be "sticky"
     */
    protected void sendMessage(final String msg, int priority, boolean sticky) {
        GntpApplicationInfo info = Gntp.appInfo(APP_NAME).build(); //.icon(getImage(APPLICATION_ICON)).build();
        final GntpNotificationInfo notif1 = Gntp.notificationInfo(info, "Notify 1").build();
        
        final GntpClient client = Gntp.client(info).listener(new GntpListener() {
            
            @Override
            public void onRegistrationSuccess() {
                    System.out.println("Registered");
            }


            @Override
            public void onNotificationSuccess(GntpNotification notification) {
                    System.out.println("Notification success: " + notification);
            }

            @Override
            public void onClickCallback(GntpNotification notification) {
                    System.out.println("Click callback: " + notification.getContext());
            }

            @Override
            public void onCloseCallback(GntpNotification notification) {
                    System.out.println("Close callback: " + notification.getContext());
            }

            @Override
            public void onTimeoutCallback(GntpNotification notification) {
                    System.out.println("Timeout callback: " + notification.getContext());
            }

            @Override
            public void onRegistrationError(GntpErrorStatus status, String description) {
                    System.out.println("Registration Error: " + status + " - desc: " + description);
            }

            @Override
            public void onNotificationError(GntpNotification notification, GntpErrorStatus status, String description) {
                    System.out.println("Notification Error: " + status + " - desc: " + description);
            }

            @Override
            public void onCommunicationError(Throwable t) {
                t.printStackTrace();
            }
            
            
        }).forHost(growlHost).onPort(growlPort).withoutRetry().build();
        
        client.register();
        
        try { 

            client.waitRegistration(5, SECONDS);

            System.out.println("Notifying: " + msg);
            client.notify(Gntp.notification(notif1, APP_NAME)
                              .text(msg)
                              .withoutCallback()
                              //.header(APP_NAME)
                              .build(), 15, SECONDS);


            client.shutdown(5, SECONDS);
            
            
        } catch(InterruptedException ie) {

            System.err.println("InterruptedException :(");
        }



    }
    

    public static void main(String[] args) {
        GrowlListener gl = new GrowlListener();
        gl.sendMessage("Testing Testing", 0, false);
    }
    

}