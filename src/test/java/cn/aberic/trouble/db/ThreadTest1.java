/*
 * MIT License
 *
 * Copyright (c) 2018 Aberic Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.aberic.trouble.db;

import cn.aberic.trouble.db.core.TDConfig;
import cn.aberic.trouble.db.core.TDManager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Aberic on 2018/10/15 21:50
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class ThreadTest1 {

    static class A {

        ReentrantLock lock = new ReentrantLock();
        HashMap<Integer, HashMap<Integer, Integer>> iMap = new HashMap<>();

        public A() {
            TDConfig config = new TDConfig()
                    .setTree(3, 100);
            TDManager.obtain().config(config);
            // manager.createMTable("haha");
            TDManager.obtain().createDTable("index");
        }

        public void start() {
            at.start();
            bt.start();
            ct.start();
//            dt.start();
        }

        public void put(int k, int index, int size, int num) {
            try {
                lock.lock();
                while (null == iMap.get(k)) {
                    iMap.put(k, new HashMap<>());
                    System.out.println(Thread.currentThread().getName() + " 初始化");
                }
            } finally {
                lock.unlock();
            }

            synchronized (iMap.get(k)) {
                for (int i = index; i < size; i++) {
                    iMap.get(k).put(i, i + num);
//                TDManager.obtain().putD("index", i, i);
//                System.out.println("map.putD(0) -> " + i + " = " + TDManager.obtain().putD("index", i, i));
                    System.out.println("map.putD(" + Thread.currentThread().getName() + ") -> " + i + " <==> " + (i + num));
                }
            }
        }

        private Thread at = new Thread(() -> {
            put(1, 0, 50, 0);
        }, "at");

        private Thread bt = new Thread(() -> {
            put(1, 0, 50, 1);
        }, "bt");

        private Thread ct = new Thread(() -> {
            put(2, 0, 50, 2);
        }, "ct");

        private Thread dt = new Thread(() -> {
            put(1, 0, 50, 3);
        }, "dt");
    }

    public static void main(String[] args) {
        A a = new A();
        a.start();
    }

}
