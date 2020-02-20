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

import java.util.Hashtable;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;

import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;
import net.sf.libgrowl.Application;

import net.sf.libgrowl.internal.IProtocol;
import net.sf.libgrowl.internal.Message;
import net.sf.libgrowl.internal.NotifyMessage;
import net.sf.libgrowl.internal.RegisterMessage;

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

    private GrowlConnector gConn;
    private NotificationType[] notificationTypes;
    private Application ant;
    private static final NotificationType BUILD_STARTED =  new NotificationType("Build started");
    private static final NotificationType BUILD_FINISHED =  new NotificationType("Build finished");
    private static final NotificationType BUILD_FAILED =  new NotificationType("Build failed");

    public GrowlListener() {

        // Have to get these from the system properties as the build properties aren't
        // available in buildStarted()
        //
        // These must be set via the ANT_OPTS env variable
        growlHost = System.getProperty(GROWL_HOST_PROP, DEFAULT_GROWL_HOST);
        growlPasswd = System.getProperty(GROWL_PASSWD_PROP, DEFAULT_GROWL_PASSWD);
        String p = System.getProperty(GROWL_PORT_PROP, ""+DEFAULT_GROWL_PORT);
        growlPort = Integer.valueOf(p);


        ant = new Application("Ant");
        notificationTypes = new NotificationType[] { BUILD_STARTED, BUILD_FINISHED, BUILD_FAILED };

    }

    /**
     * Called when the build is started.
     *
     * @param event
     */
    public void buildStarted(BuildEvent event) {
        sendMessage(BUILD_STARTED, "Build starting...", "Build Started",
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
            sendMessage(BUILD_FAILED, "Build failed: " + t.toString(), "Build failed",
                        2, sticky);
            return;
        }
        sendMessage(BUILD_FINISHED, "Build finished for " + projectName, "Build finished",
                    0, sticky);

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
     * @param type The NotificationType
     * @param msg The message
     * @param title The title
     * @param priority The message priority
     * @param sticky If true, notification should be "sticky"
     */
    protected void sendMessage(NotificationType type, String msg, String title, int priority, boolean sticky) {

            Notification n = new Notification(ant, type, title, msg);
            if(sticky) n.setSticky(true);
            n.setPriority(priority);

            GrowlConnector gConn = new GrowlConnector(growlHost, growlPort);
            gConn.register(ant, notificationTypes);

            gConn.notify(n);

    }


    public static void main(String[] args) {
        GrowlListener gl = new GrowlListener();
        gl.sendMessage(BUILD_STARTED, "Testing Testing", "Test Title", 0, false);
    }


}