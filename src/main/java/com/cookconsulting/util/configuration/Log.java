/*
 * Copyright (c) 2011, Todd Cook.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright notice,
 *        this list of conditions and the following disclaimer in the documentation
 *        and/or other materials provided with the distribution.
 *      * Neither the name of the <ORGANIZATION> nor the names of its contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cookconsulting.util.configuration;

import net.jcip.examples.LogService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Enum - singleton log gateway
 */
public enum Log {

    DEBUG("DEBUG"), TRACE("TRACE"), INFO("INFO"), ERROR("ERROR"),
    CONFIG("CONFIGURATION");

    private String state;
    private static LogService logService;
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private String logfile = "not yet initialized!";

    Log (String state) {
        this.state = state;
    }

    /**
     * Write a file in the tmp log directory
     *
     * @param message
     */
    public void write (String message) {
        try {
            logService.log(state + ": " + message);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void write (Throwable thrown) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        thrown.printStackTrace(printWriter);
        try {
            logService.log(state + ": " + writer.toString());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown () {
        logService.stop();
    }

    public String getLogfile () {
        return logfile;
    }

    public synchronized void initialize () {
        if (initialized.get()) {
            return;
        }
        StringBuilder logLocation = new StringBuilder();
        SimpleDateFormat fileDateFormat =
            new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS'Z'");
        logLocation.append(System.getProperty("java.io.tmpdir"))
            .append("MyAPP_")
            .append(fileDateFormat.format(new Date()))
            .append(".log");
        logfile = logLocation.toString();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(logLocation.toString()));
            initialized.set(true);
        }
        catch (IOException e) {
            System.err.println("Failure to initialize tmp log file: "
                + logLocation.toString());
            e.printStackTrace();
            return;
        }
        logService = new LogService(pw);
        logService.start();
        Log.DEBUG.write("Started at: " + new Date().toString());
        System.out.println("Created application log at: " + logLocation.toString());
    }
}
