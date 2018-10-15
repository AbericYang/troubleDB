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

import cn.aberic.trouble.db.core.TDConfig;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Aberic on 2018/10/8 22:10
 * @see ClassLoader#defineClass(byte[], int, int)
 * @since 1.0
 */
public class HashMemoryMap<K, V> extends AbstractHashMap<K, V> implements Serializable {

    private static final long serialVersionUID = 2794796425862934413L;

    /** 构造哈希表数组默认大小 */
    private static final int DEFAULT_HASH_LENGTH = 4;
    private static final int DEFAULT_LOAD_FACTOR = 2;

    /** 当前Hash表中数据大小 */
    private int size;
    /** 构造哈希表数组检测用大小，始终比{@code #hashArrayLength}小1 */
    private int hashArrayCheckLength;
    /** 有序存储于哈希表中的B-tree */
    private TreeMemoryMap<K, V>[] treeMaps;

    public HashMemoryMap() {
        this(DEFAULT_HASH_LENGTH, 0 , 0);
    }

    public HashMemoryMap(TDConfig config) {
        this(DEFAULT_HASH_LENGTH, config.getTreeMaxLevel(), config.getNodeArrayLength());
    }

    @SuppressWarnings("unchecked")
    private HashMemoryMap(int hashArrayLength, int treeMaxLevel, int nodeArrayLength) {
        if (hashArrayLength <= 0) {
            this.hashArrayLength = DEFAULT_HASH_LENGTH;
        } else {
            this.hashArrayLength = hashArrayLength;
        }
        this.hashArrayCheckLength = hashArrayLength - 1;
        treeMaps = new TreeMemoryMap[this.hashArrayLength];
        for (int i = 0; i < this.hashArrayLength; i++) {
            TreeMemoryMap<K, V> treeMap = new TreeMemoryMap<>(treeMaxLevel, nodeArrayLength);
            treeMaps[i] = treeMap;
        }
        treeMaxLength = treeMaps[0].range().treeMaxLength;
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
    public boolean containsKey(int hash, K key) {
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            return false;
        }
        return treeMaps[unit].containsKey(unit, storeHash(hash, unit));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V get(int hash, K key) {
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            return null;
        }
        return treeMaps[unit].get(unit, storeHash(hash, unit), key);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V put(int hash, K key, V value) {
        if (null == key) {
            throw new NullPointerException();
        }
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
        V v = treeMaps[unit].put(unit, storeHash(hash, unit), key, value);
        size++;
        return v;
    }

    /**
     * 根据 <tt>unit</tt> 判定是否需要递归重设Hash数组大小
     *
     * @param unit 当前传入key所期望在Hash数组中的下标
     */
    private void resize(int unit) {
        hashArrayLength += DEFAULT_LOAD_FACTOR;
        hashArrayCheckLength = hashArrayLength - 1;
        treeMaps = Arrays.copyOf(treeMaps, hashArrayLength);
        treeMaps[hashArrayLength - 1] = new TreeMemoryMap<>();
        treeMaps[hashArrayLength - 2] = new TreeMemoryMap<>();
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
    }

}