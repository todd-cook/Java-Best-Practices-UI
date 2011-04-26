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

import com.cookconsulting.util.data.Pair;
import com.cookconsulting.util.configuration.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author : Todd Cook
 * @since : Mar 8, 2011 4:26:04 PM
 */
public enum FibonacciMemoize {

    instance;

    private final ConcurrentHashMap<Integer, BigInteger> cache =
        new ConcurrentHashMap<Integer, BigInteger>();

    public void cache (Integer seed, BigInteger result) {
        cache.putIfAbsent(seed, result);
    }

    public int cacheSize () {
        return cache.size();
    }

    public String save (String file) {
        Log.DEBUG.write("Saving: " + file);
        int size = 0;
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            size = cache.size();
            out.writeObject(size);
            for (Integer key : cache.keySet()) {
                out.writeObject(key);
                out.writeObject(cache.get(key));
            }
            out.close();
            fileOut.close();
        }
        catch (FileNotFoundException e) {
            Log.ERROR.write(e);
        }
        catch (IOException e) {
            Log.ERROR.write(e);
        }
        String result = String.format(
            "Successfully wrote %d %s cache items %n", size, getClass());
        Log.INFO.write(result);
        return result;
    }

    public String load (String file) {
        Log.INFO.write("loading: " + file);
        int size = 0;
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            size = (Integer) in.readObject();
            for (int i = 0; i < size; i++) {
                Integer key = (Integer) in.readObject();
                BigInteger bigInt = (BigInteger) in.readObject();
                cache.put(key, bigInt);
            }
            in.close();
            fileIn.close();
        }
        catch (ClassNotFoundException e) {
            Log.ERROR.write(e);

            return "Failure";
        }
        catch (FileNotFoundException e) {
            Log.ERROR.write(e);
            return "Failure";
        }
        catch (IOException e) {
            Log.ERROR.write(e);
            return "Failure";
        }
        String result = String.format(
            "Successfully loaded %d %s cache items %n", size, getClass());
        Log.INFO.write(result);
        return result;
    }

    public ConcurrentHashMap<Integer, BigInteger> getCacheMap () {
        return cache;
    }

    public List<Pair<Integer, BigInteger>> getCache () {
        List<Pair<Integer, BigInteger>> list =
            new ArrayList<Pair<Integer, BigInteger>>();
        for (Integer key : cache.keySet()) {
            list.add(new Pair<Integer, BigInteger>(key, cache.get(key)));
        }
        return list;
    }
}