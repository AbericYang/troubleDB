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

package cn.aberic.trouble.db.third;


import cn.aberic.trouble.db.third.service.IDBExec;

import java.util.Map;

/**
 * 作者：Aberic on 2018/09/03 14:16
 * <p>
 * 邮箱：abericyang@gmail.com
 */
public class DBExec {

    public static DBExec instance;

    private IDBExec idbExec;

    public static DBExec obtain() {
        if (null == instance) {
            synchronized (DBExec.class) {
                if (null == instance) {
                    instance = new DBExec();
                }
            }
        }
        return instance;
    }

    public DBExec() {
        idbExec = getRocksDB();
    }

    private LevelDBExec getLevelDB() {
        return new LevelDBExec();
    }

    private RocksDBExec getRocksDB() {
        return new RocksDBExec();
    }

    public void put(String key, String value) {
        idbExec.put(key, value);
    }

    public void put(Map<String, String> map) {
        idbExec.put(map);
    }

    public String get(String key) {
        return idbExec.get(key);
    }

    public void put(String contractHash, String key, String value) {
        idbExec.put(contractHash, key, value);
    }

    public void put(String contractHash, Map<String, String> map) {
        idbExec.put(contractHash, map);
    }

    public String get(String contractHash, String key) {
        return idbExec.get(contractHash, key);
    }

}
