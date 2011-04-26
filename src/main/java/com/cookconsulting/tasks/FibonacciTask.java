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

package com.cookconsulting.tasks;

import com.cookconsulting.util.data.Triple;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : Todd Cook
 * @since : Mar 12, 2011
 */
public final class FibonacciTask implements Runnable,
    Callable, Future, Serializable {

    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean complete = new AtomicBoolean(false);

    private final int seed;
    private long result;

    private long start;
    private long end;

    public FibonacciTask (int seed) {
        this.seed = seed;
    }

    /**
     * Naive recursive algorithm (exponential time) for computing FBs.
     * Naive Fibonnacci; recursive
     * Big O: n^n ; exponential
     *
     * @param n fibonacci value to calculate
     * @return the long value; caution overflows at fib(93)
     */
    private long fibRecursive (long n) {
        if (n <= 1) {
            return n;
        }
        else {
            return fibRecursive(n - 1) + fibRecursive(n - 2);
        }
    }

    public void run () {
        running.set(true);
        start = System.currentTimeMillis();
        result = fibRecursive(seed);
        end = System.currentTimeMillis();
        complete.set(true);
        running.set(false);
    }

    public long getResult () {
        return result;
    }

    public int getSeed () {
        return seed;
    }

    public boolean cancel (boolean mayInterruptIfRunning) {
        return false; // TODO
    }

    public boolean isCancelled () {
        return false; // TODO
    }

    public boolean isDone () {
        return complete.get();
    }

    /**
     * @return milliseconds wall-clock time for calculation to complete
     */
    public long getElapsedTime () {
        return end - start;
    }

    public Triple<Integer, Long, Long> get () {
        if (!running.get()) {
            run();
        }
        return new Triple<Integer, Long, Long>(seed, result, getElapsedTime());
    }

    public Triple<Integer, Long, Long> get (long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        throw new ExecutionException(new Exception("Not implemented"));
    }

    public Triple<Integer, Long, Long> call () throws Exception {
        if (!running.get()) {
            run();
        }
        return new Triple<Integer, Long, Long>(seed, result, getElapsedTime());
    }

    public String toDisplayString () {
        return "IFibonacci( " + seed + ") = " + result;
    }

    @Override
    public String toString () {
        return "FibonacciTask{" +
            "running=" + running +
            ", complete=" + complete +
            ", seed=" + seed +
            ", result=" + result +
            ", start=" + start +
            ", end=" + end +
            '}';
    }
}
