/*
  Copyright 2007 Nate Drake
  
  Licensed under the Apache License, Version 2.0 (the "License"); 
  you may not use this file except in compliance with the License. 
  You may obtain a copy of the License at 

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software 
  distributed under the License is distributed on an "AS IS" BASIS, 
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
  See the License for the specific language governing permissions and 
  limitations under the License.
*/

package net.slimeslurp.growl;

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
    private static final String GROWL_HOST = "localhost";
    private static final String GROWL_PASSWD = null;
    private Growl growl;

    public GrowlListener() {
        try {

            // Register with Growl/JGrowl
            growl = new Growl();

            growl.addGrowlHost(GROWL_HOST, GROWL_PASSWD);
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
                    GrowlNotification.NORMAL);
    }

    /**
     * Called when the build is finished.
     *
     * @param event
     */
    public void buildFinished(BuildEvent event) {
        Throwable t = event.getException();
        String projectName = event.getProject().getName();
        if (t != null) {
            sendMessage("Build failed: " + t.toString(), 
                        GrowlNotification.HIGH);
            return;
        }
        sendMessage("Build finished for " + projectName, 
                    GrowlNotification.NORMAL);
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
     */
    protected void sendMessage(String msg, int priority) {
        try {
            growl.sendNotification(new GrowlNotification(APP_NAME, 
                                                         APP_NAME, 
                                                         msg, 
                                                         APP_NAME, 
                                                         false, 
                                                         priority));
        } catch(GrowlException e) {
            
        }
    }

    public static void main(String[] args) {
        GrowlListener gl = new GrowlListener();
        gl.sendMessage("Testing Testing", GrowlNotification.NORMAL);
    }
}