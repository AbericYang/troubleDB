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

/**
 * 基于哈希表的 <tt>TMap</tt> 的接口实现。此实现提供所有可选的映射操作，并允许使用 <tt>null</tt> 值和 <tt>null</tt> 键，但不推荐。
 * （除了此类的 <tt>key</tt> 被存入内存，<tt>HashRangeMap</tt> 类与<tt>HashBlockMap</tt> 大致相同。）
 *
 * <p>理论上此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
 * 实际实现中，此类在做映射操作的时候，会将传入 <tt>key</tt> 的 <tt>hash</tt> 在 <tt>RangeTreeMap</tt> 中按照从小到大的顺序做一个类双向链表的方式排列，
 * 可以非常快速的找到第一个和最后一个结点，同时可以根据第一个和最后一个结点进行有序迭代。
 * 注意：这个有序取决于 <tt>key</tt> 的 <tt>hash</tt> 值。
 * 因此，如果自定义 <tt>key</tt> ，并且 <tt>key</tt> 是 <tt>Integer/int</tt> 类型，且一个从1开始一直到{@link Integer#MAX_VALUE}，中间允许有间断，
 * 那么 <tt>HashRangeMap</tt> 在这种情况下，可以被看做是一个有序表。
 * 并且从1开始进行有序的不间断的传入将会使<tt>HashIntegerMap</tt> 类效率达到最高。
 *
 * <p>哈希表的存储横向数组中，每一列都会维持一个B-Tree，由{@link RangeTreeMap}实现。
 *
 * @author Aberic on 2018/10/08 17:13
 * @version 1.0
 * @see TMap
 * @see RangeTreeMap
 * @see HashBlockMap
 * @see HashIntegerMap
 * @since 1.0
 */
public class HashRangeMap<K, V> implements TMap<K, V>, Serializable {

    private static final long serialVersionUID = 4198685711387406481L;

    /** 构造哈希表数组默认大小 */
    private static final int DEFAULT_HASH_LENGTH = 64;
    /** 构造顶级结点范围对象的默认所属子结点数组大小 */
    private static final int DEFAULT_TREE_RANGE_LENGTH = 4;

    /** 当前Hash表中数据大小 */
    private int size;
    /** 构造哈希表数组大小 */
    private int hashLength;
    /**
     * 指定构造顶级结点范围对象的所属子结点数组大小，如未在构造中使用此参数，
     * 则该值将会在无参构造的时候被赋值为{@link #DEFAULT_TREE_RANGE_LENGTH}
     */
    private int rangeLength;
    /** 有序存储于哈希表中的B-tree */
    private RangeTreeMap<K, V>[] treeMaps;

    public HashRangeMap() {
        this(DEFAULT_HASH_LENGTH);
    }

    public HashRangeMap(int hashLength) {
        this(hashLength, DEFAULT_TREE_RANGE_LENGTH);
    }

    @SuppressWarnings("unchecked")
    public HashRangeMap(int hashLength, int rangeLength) {
        if (hashLength <= 0) {
            this.hashLength = DEFAULT_HASH_LENGTH;
        } else {
            this.hashLength = hashLength;
        }
        if (rangeLength <= 0) {
            this.rangeLength = DEFAULT_TREE_RANGE_LENGTH;
        } else {
            this.rangeLength = rangeLength;
        }
        treeMaps = new RangeTreeMap[hashLength];
        for (int i = 0; i < hashLength; i++) {
            RangeTreeMap treeMap = new RangeTreeMap(rangeLength);
            treeMaps[i] = treeMap;
        }
        size = 0;
    }

    /**
     * 构造顶级结点范围对象的所属子结点数组大小
     *
     * @return 构造顶级结点范围对象的所属子结点数组大小
     */
    public int getRangeLength() {
        return rangeLength;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return treeMaps[RangeTreeMap.hash(key) % hashLength].containsKey(key);
    }

    @Override
    public V get(Object key) {
        return treeMaps[RangeTreeMap.hash(key) % hashLength].get(key);
    }

    @Override
    public V put(K key, V value) {
        V v = treeMaps[RangeTreeMap.hash(key) % hashLength].put(key, value);
        size++;
        return v;
    }

}
