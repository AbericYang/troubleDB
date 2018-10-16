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

import java.util.concurrent.TimeUnit;

/**
 * @author Aberic on 2018/10/15 21:50
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class ThreadTest {

    static class A {

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
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dt.start();
        }

        private Thread at = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                TDManager.obtain().putI("index", i, i);
//                System.out.println("map.putI(0) -> " + i + " = " + TDManager.obtain().putI("index", i, i));
            }
        });

        private Thread bt = new Thread(() -> {
            for (int i = 50; i < 100; i++) {
                TDManager.obtain().putI("index", i, i);
//                System.out.println("map.putI(2) -> " + i + " = " + TDManager.obtain().putI("index", i, i));
            }
        });

        private Thread ct = new Thread(() -> {
            for (int i = 100; i < 150; i++) {
                TDManager.obtain().putI("index", i, i);
//                System.out.println("map.putI(2) -> " + i + " = " + TDManager.obtain().putI("index", i, i));
            }
        });

        private Thread dt = new Thread(() -> {
            for (int i = 0; i < 150; i++) {
                System.out.println("map.getD(1) -> " + i + " = " + TDManager.obtain().getD("index", i));
            }
        });
    }

    public static void main(String[] args) {
        A a = new A();
        a.start();
    }

}
