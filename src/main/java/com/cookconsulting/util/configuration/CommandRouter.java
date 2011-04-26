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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service Locater/Provider
 * Enum as singleton; see Bloch, Effective Java, 2nd ed.  pp.
 */
public enum CommandRouter {

    background, scheduler, timed;

    CommandRouter () {
    }

    private ExecutorService executor;

    public void execute (Runnable r) {
        executor.execute(r);
    }

    public Future<?> submit (Runnable r) {
        return executor.submit(r);
    }

    /**
     * all Executor configuration must be done via package access;
     * i.e. the Configurator class
     *
     * @return an unconfigurable ExecutorService
     */
    public ExecutorService getExecutor () {
        return Executors.unconfigurableExecutorService(executor);
    }

    public Future<?> schedule (Runnable command,
                               long initialDelay,
                               long period,
                               TimeUnit unit) throws RejectedExecutionException {
        if (executor instanceof ScheduledExecutorService) {
            return ((ScheduledExecutorService) executor).scheduleAtFixedRate(
                command, initialDelay, period, unit);
        }
        else {
            /**
             * if somebody mistakenly submits a scheduled task
             *  to a plain old executor, then throw an exception
             */
            throw new RejectedExecutionException(
                "Underlying executor isn't a ScheduledExecutorService");
        }
    }

    /**
     * Initializaton method; restricted to package level access;
     * called by Configurator
     *
     * @param executor the Executor to be used
     */
    void setExecutor (ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * @return
     */
    ExecutorService getConfigurableExecutor () {
        return executor;
    }

    @Override
    public String toString () {
        return "CommandRouter{" +
            "executor=" + executor +
            '}';
    }
}
