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

/**
 * @author Aberic on 2018/10/15 21:50
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class ThreadTest {

    static class A {

        private TDManager manager;

        public A() {
            TDConfig config = new TDConfig()
                    .setTree(3, 100);
            manager = new TDManager(config);
            // manager.createMTable("haha");
            manager.createITable("index");
        }

        public void start() {
            at.start();
            bt.start();
            ct.start();
        }

        private Thread at = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("map.putI(0) -> " + i + " = " + manager.putI("index", 1, i));
            }
        });

        private Thread ct = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("map.putI(2) -> " + i + " = " + manager.putI("index", 1, i));
            }
        });

        private Thread bt = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                System.out.println("map.getI(1) -> " + i + " = " + manager.getI("index", 1));
            }
        });
    }

    public static void main(String[] args) {
        A a = new A();
        a.start();
    }

}
