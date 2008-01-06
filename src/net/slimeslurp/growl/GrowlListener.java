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

import com.binaryblizzard.growl.Growl;
import com.binaryblizzard.growl.GrowlException;
import com.binaryblizzard.growl.GrowlNotification;
import com.binaryblizzard.growl.GrowlRegistrations;

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
    private Growl growl;

    private static final String FINISHED_STICKY_NAME = "gbl.endsticky";
    private static final String GROWL_HOST_PROP = "gbl.host";
    private static final String GROWL_PASSWD_PROP = "gbl.passwd";    
    private String growlHost;
    private String growlPasswd;
    
    public GrowlListener() {
        try {

            // Have to get these from the system properties as the build properties aren't  
            // available in buildStarted()
            //
            // These must be set via the ANT_OPTS env variable       
            growlHost = System.getProperty(GROWL_HOST_PROP, DEFAULT_GROWL_HOST);
            growlPasswd= System.getProperty(GROWL_PASSWD_PROP, DEFAULT_GROWL_PASSWD);
        
            // Register with Growl/JGrowl
            growl = new Growl();
            growl.addGrowlHost(growlHost, growlPasswd);
            GrowlRegistrations registrations = growl.getRegistrations(APP_NAME);
            registrations.registerNotification(APP_NAME, true);
        } catch(GrowlException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the build is started.
     *
     * @param event
     */
    public void buildStarted(BuildEvent event) {        
        sendMessage("Build starting...",
                    GrowlNotification.NORMAL, false);
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
                        GrowlNotification.HIGH, sticky);
            return;
        }
        sendMessage("Build finished for " + projectName, 
                    GrowlNotification.NORMAL, sticky);
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
    protected void sendMessage(String msg, int priority, boolean sticky) {
        try {
            growl.sendNotification(new GrowlNotification(APP_NAME, 
                                                         APP_NAME, 
                                                         msg, 
                                                         APP_NAME, 
                                                         sticky, 
                                                         priority));
        } catch(GrowlException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GrowlListener gl = new GrowlListener();
        gl.sendMessage("Testing Testing", GrowlNotification.NORMAL, false);
    }
}