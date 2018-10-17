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

package cn.aberic.trouble.db.core;

import cn.aberic.trouble.db.block.TroubleBlock;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Aberic on 2018/10/12 14:38
 * @version 1.0
 * @see TDConfig
 * @see TDMemoryTable
 * @see TDDiskTable
 * @see TDBlockTable
 * @since 1.0
 */
public class TDManager {

    private static volatile TDManager instance;

    private HashMap<String, TDMemoryTable> tdmMap;
    private HashMap<String, TDDiskTable> tddMap;
    private HashMap<String, TDBlockTable> tdbMap;
    private HashMap<String, ConcurrentTDDiskTable> ctddMap;
    private TDConfig config;
    private ReentrantLock lock = new ReentrantLock();

    public static TDManager obtain() {
        if (null == instance) {
            synchronized (TDManager.class) {
                if (null == instance) {
                    instance = new TDManager();
                }
            }
        }
        return instance;
    }

    private TDManager() {
        this.config = new TDConfig();
        this.tdmMap = new HashMap<>();
        this.tddMap = new HashMap<>();
        this.tdbMap = new HashMap<>();
        this.ctddMap = new HashMap<>();
    }

    public void config(TDConfig config) {
        this.config = config;
    }

    public void createMTable(String name) {
        try {
            lock.lock();
            while (null == tdmMap.get(name)) {
                tdmMap.put(name, new TDMemoryTable(name, config));
            }
        } finally {
            lock.unlock();
        }
    }

    public void createDTable(String name) {
        try {
            lock.lock();
            while (null == tddMap.get(name)) {
                tddMap.put(name, new TDDiskTable(name, config));
            }
        } finally {
            lock.unlock();
        }
    }

    public void createCDTable(String name) {
        try {
            lock.lock();
            while (null == ctddMap.get(name)) {
                ctddMap.put(name, new ConcurrentTDDiskTable(name, config));
            }
        } finally {
            lock.unlock();
        }
    }

    public void createBTable(String name) {
        try {
            lock.lock();
            while (null == tdbMap.get(name)) {
                tdbMap.put(name, new TDBlockTable(name, config));
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean containsMKey(String name, int key) {
        return tdmMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public boolean containsDKey(String name, int key) {
        if (null == tddMap.get(name)) {
            createDTable(name);
        }
        return tddMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public boolean containsCDKey(String name, int key) {
        if (null == ctddMap.get(name)) {
            createCDTable(name);
        }
        return ctddMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public boolean containsBKey(String name, int key) {
        if (null == tdbMap.get(name)) {
            createBTable(name);
        }
        return tdbMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public Object getM(String name, Object key) {
        return tdmMap.get(name).get(checkHashByKey(key), key);
    }

    public Object getD(String name, Object key) {
        if (null == tddMap.get(name)) {
            createDTable(name);
        }
        return tddMap.get(name).get(checkHashByKey(key), key);
    }

    public Object getCD(String name, Object key) {
        if (null == ctddMap.get(name)) {
            createCDTable(name);
        }
        return ctddMap.get(name).get(checkHashByKey(key), key);
    }

    public Object getB(String name, Object key) {
        if (null == tdbMap.get(name)) {
            createBTable(name);
        }
        return tdbMap.get(name).get(checkHashByKey(key), key);
    }

    public Object putM(String name, Object key, Object value) {
        return tdmMap.get(name).put(checkHashByKey(key), key, value);
    }

    public Object putD(String name, Object key, Object value) {
        if (null == tddMap.get(name)) {
            createDTable(name);
        }
        return tddMap.get(name).put(checkHashByKey(key), key, value);
    }

    public Object putCD(String name, Object key, Object value) {
        if (null == ctddMap.get(name)) {
            createCDTable(name);
        }
        return ctddMap.get(name).put(checkHashByKey(key), key, value);
    }

    public Object putB(String name, Object key, TroubleBlock block) {
        if (null == tdbMap.get(name)) {
            createBTable(name);
        }
        return tdbMap.get(name).put(checkHashByKey(key), key, block);
    }

    private static final int hash(Object key) {
        int h;
        return (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 检查传入的 <tt>key</tt> 是否为{@code Integer}类型，如果是，则直接返回强转后的值。
     * 如果不是，则计算该 <tt>key</tt> 的 <tt>hash</tt> 值
     *
     * @param key key
     * @return <tt>key</tt> 对应的 <tt>hash</tt> 值
     */
    private int checkHashByKey(Object key) {
        int hash;
        if (key instanceof Integer) {
            if ((Integer) key == 0) {
                hash = Integer.MAX_VALUE;
            } else {
                hash = (Integer) key;
            }
        } else {
            hash = hash(key);
            if (hash < 0) {
                hash = reHash(hash);
            }
        }
        return hash;
    }

    /**
     * 判断{@link #hash(Object)}计算出来的值是否为负，如果是负数，执行处理并返回一个正数hash值
     *
     * @param hash {@code TreeMemoryMap#hash(Object)}计算出来的值
     * @return 正数hash值
     */
    private int reHash(int hash) {
        hash += (config.getNodeArrayLength() * config.getTreeMaxLevel());
        if (hash < 0) {
            return reHash(hash);
        }
        return hash;
    }

}
