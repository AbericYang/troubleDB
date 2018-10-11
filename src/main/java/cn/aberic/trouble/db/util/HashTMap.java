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

package cn.aberic.trouble.db.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Aberic on 2018/10/8 22:10
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class HashTMap<K, V> implements TMap<K, V>, Serializable {

    private static final long serialVersionUID = 2794796425862934413L;

    /** 构造哈希表数组默认大小 */
    private static final int DEFAULT_HASH_LENGTH = 4;
    private static final int DEFAULT_LOAD_FACTOR = 2;

    /** 当前Hash表中数据大小 */
    private int size;
    /** 构造哈希表数组大小 */
    private int hashArrayLength;
    /** 构造哈希表数组检测用大小，始终比{@code #hashArrayLength}小1 */
    private int hashArrayCheckLength;
    /** 有序存储于哈希表中的B-tree */
    private TTreeMap<K, V>[] treeMaps;

    public HashTMap() {
        this(DEFAULT_HASH_LENGTH);
    }

    @SuppressWarnings("unchecked")
    private HashTMap(int hashArrayLength) {
        if (hashArrayLength <= 0) {
            this.hashArrayLength = DEFAULT_HASH_LENGTH;
        } else {
            this.hashArrayLength = hashArrayLength;
        }
        this.hashArrayCheckLength = hashArrayLength - 1;
        treeMaps = new TTreeMap[this.hashArrayLength];
        for (int i = 0; i < this.hashArrayLength; i++) {
            TTreeMap<K, V> treeMap = new TTreeMap<>();
            treeMaps[i] = treeMap;
        }
        size = 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean containsKey(K key) {
        int hash = TTreeMap.hash(key);
        if (hash < 0) {
            hash = hash(hash);
        }
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            return false;
        }
        return treeMaps[unit].containsKey(storeHash(hash, unit), key);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V get(K key) {
        int hash = TTreeMap.hash(key);
        if (hash < 0) {
            hash = hash(hash);
        }
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            return null;
        }
        return treeMaps[unit].get(storeHash(hash, unit), key);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        if (null == key) {
            throw new NullPointerException();
        }
        int hash = TTreeMap.hash(key);
        if (hash < 0) {
            hash = hash(hash);
        }
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
        V v = treeMaps[unit].put(storeHash(hash, unit), key, value);
        size++;
        return v;
    }

    private int storeHash(int hash, int unit) {
        return hash - unit * TTreeMap.NodeRange.TREE_MAX_LENGTH;
    }

    private int hash(int hash) {
        hash += (hashArrayLength * TTreeMap.NodeRange.TREE_MAX_LENGTH);
        if (hash < 0) {
            return hash(hash);
        }
        return hash;
    }

    private int unit(int key) {
        int tempKey = TTreeMap.hash(key);
        return --tempKey / TTreeMap.NodeRange.TREE_MAX_LENGTH;
    }

    private void resize(int unit) {
        hashArrayLength += DEFAULT_LOAD_FACTOR;
        hashArrayCheckLength = hashArrayLength - 1;
        treeMaps = Arrays.copyOf(treeMaps, hashArrayLength);
        treeMaps[hashArrayLength - 1] = new TTreeMap<>();
        treeMaps[hashArrayLength - 2] = new TTreeMap<>();
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
    }

}