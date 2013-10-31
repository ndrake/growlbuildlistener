/*
 *  Copyright 2013 Nate Drake
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.slimeslurp.growl;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Ant Task that writes a message via Notification Center.
 * 
 * REQUIRES: terminal-notifier (https://github.com/alloy/terminal-notifier)
 *           (brew install terminal-notifier)
 *
 *
 * @author ndrake
 *
 */
public class NCEcho extends Task {

    private static final String APP_NAME = "Ant";    
    
    /** The message to display */
    protected String message = "";

    /** Indicates if the notification should be 'sticky" */
    protected boolean sticky = false;
    
    /** Name of system property */
    private static final String GROWL_HOST_PROP = "gbl.host";
    
    /** Name of system property */
    private static final String GROWL_PASSWD_PROP = "gbl.passwd";    
        
    private static final String GROWL_PORT_PROP = "gbl.port";        
    
    /** The growl host to send messages to */
    private String growlHost;
    
    /** The password for network notifications */
    private String growlPasswd;
    
    private int growlPort;
    
    
    public NCEcho() {
        
        // Have to get these from the system properties as the build properties aren't  
        // available in buildStarted()
        //
        // These must be set via the ANT_OPTS env variable       
        
    }

    /**
     * Does the work.
     *
     * @exception BuildException if something goes wrong with the build
     */
    public void execute() throws BuildException {
        
        try {
            StringBuilder sb = new StringBuilder("terminal-notifier -message ");
            sb.append(message).append(" -title ").append("\ud83d\udc1c");
            
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(sb.toString());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        

    }

    /**
     * Set the message.
     *
     * @param msg Sets the value for message.
     */
    public void setMessage(String msg) {
        this.message = msg;
    }

    /**
     * Set a multiline message.
     *
     * Taken from the echo Task.
     *
     * @param msg the CDATA text to append to the output text
     */
    public void addText(String msg) {
        message += getProject().replaceProperties(msg);
    }

    /**
     * Set the sticky value.
     *
     * @param sticky If true, Growl message will be "sticky"
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
      
}