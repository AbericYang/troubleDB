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

import cn.aberic.trouble.db.util.HashStorageMap;

/**
 * @author Aberic on 2018/10/12 14:38
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TDBManager {

    private HashStorageMap<String, TDBTable> map;
    private TDBConfig config;

    public TDBManager() {
        map = new HashStorageMap<>();
        config = new TDBConfig();
    }

    public <K, V> void createTable(String name) {
        map.put(name, new TDBTable<K, V>(name, config));
    }

//    public <K> boolean containsKey(String name, K key) {
//        return map.get(name).containsKey(key);
//    }
//
//    public <K, V> V get(String name, K key) {
//        return map.get(name).get(key);
//    }
//
//    <K, V> V put(String name, K key, V value) {
//        return map.get(name).put(key, value);
//    }

}
