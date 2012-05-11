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

import net.jcip.examples.MyThreadFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : Todd Cook
 * @since : Mar 6, 2011
 */
public enum Configurator {

    instance;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final static Object lock = new Object();

    private void loadConfigs () {
        // TODO load from .App.name.profile
    }

    public void initialize () {
        // force initialization to happen only once
        if (!initialized.get()) {
            synchronized (lock) {
                loadConfigs();
                Log.CONFIG.initialize();
                CommandRouter.background.setExecutor(
                    Executors.newCachedThreadPool(
                        new MyThreadFactory("My App")));
                CommandRouter.scheduler.setExecutor(
                    Executors.newScheduledThreadPool(20));
                //, new TimingThreadPool ()
                CommandRouter.timed.setExecutor(
                    new ThreadPoolExecutor(1, // min threads
                                           Runtime.getRuntime().availableProcessors()
                                           // max threads
                        , 3600 * 1000 * 24 // keep alive
                        , TimeUnit.MILLISECONDS
                        , new ArrayBlockingQueue(1000)
                        , new MyThreadFactory("timed executor")
                        , new ThreadPoolExecutor.DiscardPolicy()
                                           /**
                                            *A handler for rejected tasks that
                                            * silently discards the rejected task.
                                            */
                    ));
            }
        }
        // print out initializations
        for (CommandRouter cr : CommandRouter.values()) {
            Log.DEBUG.write(cr.toString());
        }
        addShutdownHook();
        initialized.set(true);
    }

    /**
     * NOTE: Because shutdown hooks run asynchronously, there is no way to
     * guarantee that shutdown data will be caputured in the log, hence
     * this application has one shutdown hook that synchronizes shutdown
     * activities that we wish to log.
     */
    private void addShutdownHook () {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run () {
                try {
                    // Shutdown executors
                    for (CommandRouter commandRouter : CommandRouter.values()) {
                        commandRouter.getConfigurableExecutor().shutdown();  // request graceful shutdown
                        commandRouter.getConfigurableExecutor().awaitTermination(1, TimeUnit.SECONDS); // wait politely for a second
                        List<Runnable> undone = commandRouter.getConfigurableExecutor().shutdownNow();   // force shutdown
                        for (Runnable task : undone) {
                            Log.INFO.write("undone " + commandRouter.name() + " task: " + task);
                        }
                    }
                    Log.CONFIG.shutdown();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void shutdown () {
        System.exit(0);
    }

    public String getMemoizedFilename (String name) {
        StringBuilder serializationFile = new StringBuilder();
        serializationFile.append(System.getProperty("java.io.tmpdir"))
            .append(Constants.APP_LOG_NAME)
            .append("_")
            .append(name)
            .append(".dat");
        return serializationFile.toString();
    }

}
