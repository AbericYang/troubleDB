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
public class HashTMap<K, V> extends AbstractTMap<K, V> implements Serializable {

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
        int hash = checkHashByKey(key);
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            return false;
        }
        return treeMaps[unit].containsKey(storeHash(hash, unit));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public V get(K key) {
        int hash = checkHashByKey(key);
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
        int hash = checkHashByKey(key);
        int unit = unit(hash);
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
        V v = treeMaps[unit].put(storeHash(hash, unit), key, value);
        size++;
        return v;
    }

    /**
     * 因为{@code #treeMaps}中每一个{@link TTreeMap}的树规模及树叶子的对象都一样，
     * 所以当通过{@link #unit(int)}方法计算出要访问{@code #treeMaps}的数组下标后需要重新计算传入key在该数组中的hash值。
     *
     * @param hash 通过{@link #reHash(int)}方法处理过的hash值
     * @param unit 通过{@link #unit(int)}方法计算出要访问{@code #treeMaps}的数组下标
     *
     * @return 真正执行存入操作的hash值
     */
    private int storeHash(int hash, int unit) {
        return hash - unit * Pair.TREE_MAX_LENGTH;
    }

    /**
     * 判断{@link #hash(Object)}计算出来的值是否为负，如果是负数，执行处理并返回一个正数hash值
     *
     * @param hash {@code TTreeMap#hash(Object)}计算出来的值
     *
     * @return 正数hash值
     */
    private int reHash(int hash) {
        hash += (hashArrayLength * Pair.TREE_MAX_LENGTH);
        if (hash < 0) {
            return reHash(hash);
        }
        return hash;
    }

    /**
     * 检查传入的 <tt>key</tt> 是否为{@code Integer}类型，如果是，则直接返回强转后的值。
     * 如果不是，则计算该 <tt>key</tt> 的 <tt>hash</tt> 值
     *
     * @param key key
     *
     * @return <tt>key</tt> 对应的 <tt>hash</tt> 值
     */
    private int checkHashByKey(K key) {
        int hash;
        if (key instanceof Integer) {
            hash = (int) key;
        } else {
            hash = hash(key);
            if (hash < 0) {
                hash = reHash(hash);
            }
        }
        return hash;
    }

    /**
     * 根据传入key获取当前Hash数组中要访问的下标
     *
     * @param key key
     *
     * @return 当前Hash数组中要访问的下标
     */
    private int unit(int key) {
        int tempKey = hash(key);
        return --tempKey / Pair.TREE_MAX_LENGTH;
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
        treeMaps[hashArrayLength - 1] = new TTreeMap<>();
        treeMaps[hashArrayLength - 2] = new TTreeMap<>();
        if (unit > hashArrayCheckLength) {
            resize(unit);
        }
    }

}