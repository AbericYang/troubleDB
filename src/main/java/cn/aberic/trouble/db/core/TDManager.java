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

import java.util.HashMap;

/**
 * @author Aberic on 2018/10/12 14:38
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TDManager {

    private HashMap<String, TDMemoryTable> tdmMap;
    private HashMap<String, TDIndexTable> tdiMap;
    private TDConfig config;

    public TDManager() {
        this.config = new TDConfig();
        this.tdmMap = new HashMap<>();
        this.tdiMap = new HashMap<>();
    }

    public TDManager(TDConfig config) {
        this.config = config;
        this.tdmMap = new HashMap<>();
        this.tdiMap = new HashMap<>();
    }

    public void createMTable(String name) {
        tdmMap.put(name, new TDMemoryTable(name, config));
    }

    public void createITable(String name) {
        tdiMap.put(name, new TDIndexTable(name, config));
    }

    public boolean containsMKey(String name, int key) {
        return tdmMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public boolean containsIKey(String name, int key) {
        return tdiMap.get(name).containsKey(checkHashByKey(key), key);
    }

    public Object getM(String name, Object key) {
        return tdmMap.get(name).get(checkHashByKey(key), key);
    }

    public Object getI(String name, Object key) {
        return tdiMap.get(name).get(checkHashByKey(key), key);
    }

    public Object putM(String name, Object key, Object value) {
        return tdmMap.get(name).put(checkHashByKey(key), key, value);
    }

    public Object putI(String name, Object key, Object value) {
        return tdiMap.get(name).put(checkHashByKey(key), key, value);
    }

    private static final int hash(Object key) {
        if (key instanceof Integer) {
            return (Integer) key;
        }
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
            hash = (Integer) key;
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
