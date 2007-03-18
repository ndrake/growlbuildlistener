/*
 *  Copyright 2007 Nate Drake
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.binaryblizzard.growl.Growl;
import com.binaryblizzard.growl.GrowlException;
import com.binaryblizzard.growl.GrowlNotification;
import com.binaryblizzard.growl.GrowlRegistrations;


/**
 * Ant Task that writes a message via Growl/JGrowl.
 *
 * @author ndrake
 *
 */
public class GrowlEcho extends Task {

    private static final String APP_NAME = "Ant";
    private static final String GROWL_HOST = "localhost";
    private static final String GROWL_PASSWD = null;
    private Growl growl;


    protected String message = "";

    public GrowlEcho() {
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
     * Does the work.
     *
     * @exception BuildException if something goes wrong with the build
     */
    public void execute() throws BuildException {

        try {
            growl.sendNotification(new GrowlNotification(APP_NAME, 
                                                         APP_NAME, 
                                                         message, 
                                                         APP_NAME, 
                                                         false, 
                                                         GrowlNotification.NORMAL));
        } catch(GrowlException e) {
            
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

}