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
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This algorithm is deliberately slow
 * it has exponential complexity, with a minor speed up in that it uses a cache of previously
 * computed numbers.
 * The point is to illustrate that the cache can be memoized.
 *
 * We use big integer because the long datatype overflows at:  fibonacci (93)
 *
 *
 * @author : ToddCook
 * @since : Mar 11, 2011 11:21:29 AM
 */
public class FibonacciTaskBI implements Runnable, Callable<Triple<Integer, BigInteger, Long>>,
    Future<Triple<Integer, BigInteger, Long>>, IFibonacci<BigInteger>, Serializable {

    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean complete = new AtomicBoolean(false);
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger MAX_INT = new BigInteger(
        Integer.toString(Integer.MAX_VALUE));
    private ConcurrentHashMap<Integer, BigInteger> cache;
    private Integer seed;
    private BigInteger result;
    private Long start = 0L;
    private Long end = 0L;

    public FibonacciTaskBI (ConcurrentHashMap<Integer, BigInteger> cache, Integer seed) {
        this.cache = cache;
        this.seed = seed;
    }

    /**
     * Naive recursive algorithm (exponential time)
     * for computing fibonacci numbers.
     * Caution: recursive
     * Big O: n^n ; exponential
     *
     * @param n fibonacci value to calculate
     * @return the calculated value
     */
    public BigInteger fibonacci (BigInteger n) {

        int compare = n.compareTo(MAX_INT);
        if (compare < 1) {
            BigInteger result = cache.get(n.intValue());
            if (result != null) {
                return result;
            }
            compare = n.compareTo(TWO);
            if (compare < 1) {
                return n;
            }
        }
        return fibonacci(n.subtract(BigInteger.ONE)).add(
            fibonacci(n.subtract(TWO)));
    }

    public boolean cancel (boolean mayInterruptIfRunning) {
        return false;  // TODO fix
    }

    public boolean isCancelled () {
        return false;  //TODO fix
    }

    public boolean isDone () {
        return (result != null);
    }

    public Long getElapsedTime () {
        return end - start;
    }

    public void run () {
        running.set(true);
        start = System.currentTimeMillis();
        result = fibonacci(new BigInteger(Integer.toString(seed)));
        end = System.currentTimeMillis();
        complete.set(true);
        running.set(false);
    }

    public Triple<Integer, BigInteger, Long> get () {
        if (!running.get()) {
            run();
        }
        return new Triple<Integer, BigInteger, Long>(seed, result, getElapsedTime());
    }

    public Triple<Integer, BigInteger, Long> call () throws Exception {
        if (!running.get()) {
            run();
        }
        if (!complete.get()) {
            return null;
        }
        else

        {
            return new Triple<Integer, BigInteger, Long>(seed, result, getElapsedTime());
        }
    }

    public Triple<Integer, BigInteger, Long> get (long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return new Triple<Integer, BigInteger, Long>(seed, result, getElapsedTime());
    }

}
