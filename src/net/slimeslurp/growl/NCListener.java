/*
 *  Copyright 2013 Nate Drake
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

import java.io.IOException;
import java.util.Hashtable;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;

/**
 * BuildListener that displays information on the current build
 * via Notification Center.
 * 
 * REQUIRES: terminal-notifier (https://github.com/alloy/terminal-notifier)
 *           (brew install terminal-notifier)
 *
 *
 * TODO:
 * o Read some things from a properties file (i.e. host, passwd, etc.)
 *
 * @author ndrake
 *
 */
public class NCListener implements BuildListener {


    private static final String APP_NAME = "Ant";
    private static final String DEFAULT_GROWL_HOST = "localhost";
    private static final String DEFAULT_GROWL_PASSWD = null;
    private static final int DEFAULT_GROWL_PORT = 23053;

    private static final String FINISHED_STICKY_NAME = "gbl.endsticky";
    
    private boolean haveNotifier = true;
    
    public NCListener() {
        
        try {
            // Check for terminal-notifier
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("which terminal-notifier");
            proc.waitFor();
            int exitVal = proc.exitValue();
            if(exitVal != 0) {
                haveNotifier = false;
                System.err.println("Can't find terminal-notifier.  Please make sure it is installed and on your path.");
            }
            
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Called when the build is started.
     *
     * @param event
     */
    public void buildStarted(BuildEvent event) {        
        sendMessage("Build starting...", "Build Started", "ant-started",
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
            sendMessage("Build failed: " + t.toString(), "Build failed", "ant-failed",
                        2, sticky);
            return;
        }
        sendMessage("Build finished for " + projectName, "Build finished", "ant-failed",
                    0, sticky);
                    
    }

    // Other messages are currently ignored
    public void messageLogged(BuildEvent event) { }
    public void targetFinished(BuildEvent event) {}
    public void targetStarted(BuildEvent event) {}
    public void taskFinished(BuildEvent event) {}
    public void taskStarted(BuildEvent event) {}


    /**
     * Send a message to Notification Center.
     *
     * @param type The NotificationType
     * @param msg The message
     * @param title The title
     * @param priority The message priority
     * @param sticky If true, notification should be "sticky"
     */
    protected void sendMessage(String msg, String title, String group, int priority, boolean sticky) {
        if(haveNotifier) {
            try {
                ProcessBuilder pb = new ProcessBuilder("terminal-notifier", "-message", 
                    msg, "-title", "\ud83d\udc1c", "-group", group, "-subtitle", title);
                pb.start();                
            } catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
    }
    

    public static void main(String[] args) {
        NCListener ncl = new NCListener();
        ncl.sendMessage("Testing Testing", "Test Title", "ant-test", 0, false);
    }
    

}