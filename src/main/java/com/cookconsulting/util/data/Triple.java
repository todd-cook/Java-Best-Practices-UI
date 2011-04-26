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

package com.cookconsulting.util.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple, generic class for three data points,
 * useful for sorting based on the different columns;
 * run main for an example.
 * Of course a TripleFactory would really make this useful, or perhaps a
 * TripleTree with a supplied Comparator.
 * Of course, if you've been doing some functional programming this will
 * probably just seem silly.
 *
 * @author : Todd Cook
 * @since : Mar 9, 2011 11:15:21 AM
 */
public class Triple<T, V, U> {

    T _1;
    V _2;
    U _3;

    public Triple () {
    }

    public Triple (T _1, V _2, U _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    public T get_1 () {
        return _1;
    }

    public void set_1 (T _1) {
        this._1 = _1;
    }

    public V get_2 () {
        return _2;
    }

    public U get_3 () {
        return _3;
    }

    public void set_3 (U _3) {
        this._3 = _3;
    }

    public void set_2 (V _2) {
        this._2 = _2;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triple)) {
            return false;
        }

        Triple triple = (Triple) o;

        if (_1 != null ? !_1.equals(triple._1) : triple._1 != null) {
            return false;
        }
        if (_2 != null ? !_2.equals(triple._2) : triple._2 != null) {
            return false;
        }
        if (_3 != null ? !_3.equals(triple._3) : triple._3 != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode () {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString () {
        return "Triple{" +
            "_1=" + _1 +
            ", _2=" + _2 +
            ", _3=" + _3 +
            '}';
    }

    /**
     * Self-testing main;
     * Assert() is used to prove the assumptions about order which are also
     * printed out.
     *
     * @param args none; however the VM parameter for enabling assertions,
     *             -ea
     *             must be set.
     */
    public static void main (String[] args) {
        Triple<Integer, Integer, Integer> t1 =
            new Triple<Integer, Integer, Integer>(1, 100, 0);
        Triple<Integer, Integer, Integer> t2 =
            new Triple<Integer, Integer, Integer>(2, 90, 0);
        Triple<Integer, Integer, Integer> t3 =
            new Triple<Integer, Integer, Integer>(3, 80, 0);
        Triple<Integer, Integer, Integer> t4 =
            new Triple<Integer, Integer, Integer>(4, 70, 0);
        Triple<Integer, Integer, Integer> t5 =
            new Triple<Integer, Integer, Integer>(5, 60, 0);
        ArrayList<Triple<Integer, Integer, Integer>> coordinates =
            new ArrayList<Triple<Integer, Integer, Integer>>();
        coordinates.add(t1);
        coordinates.add(t2);
        coordinates.add(t3);
        coordinates.add(t4);
        coordinates.add(t5);
        Collections.sort(coordinates,
                         new Comparator<Triple<Integer, Integer, Integer>>() {
                             public int compare (Triple<Integer, Integer, Integer> t,
                                                 Triple<Integer, Integer, Integer> t2) {
                                 if (t._1 > t2._1) {
                                     return 1;
                                 }
                                 if (t._1 < t2._1) {
                                     return -1;
                                 }
                                 return 0;
                             }
                         });
        System.out.println("Sorted by first value");
        for (Triple<Integer, Integer, Integer> t : coordinates) {
            System.out.println(t);
        }
        assert (coordinates.get(0)._1 == 1);
        Collections.sort(coordinates,
                         new Comparator<Triple<Integer, Integer, Integer>>() {
                             public int compare (Triple<Integer, Integer, Integer> t,
                                                 Triple<Integer, Integer, Integer> t2) {
                                 if (t._2 > t2._2) {
                                     return 1;
                                 }
                                 if (t._2 < t2._2) {
                                     return -1;
                                 }
                                 return 0;
                             }
                         });
        System.out.println("Sorted by second value");
        for (Triple<Integer, Integer, Integer> t : coordinates) {
            System.out.println(t);
        }
        assert (coordinates.get(0)._2 == 60);
        // and so on for sorting by the third value...
        // above seems like a lot of code.
        // Well, if you used Scala, or any other functional language that has
        // pair built in as a Tuple, all this could have been two lines
    }
}
