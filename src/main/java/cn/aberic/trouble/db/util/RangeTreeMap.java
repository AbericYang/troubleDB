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
 * （除了此类的 <tt>key</tt> 被存入内存，<tt>RangeTreeMap</tt> 类与<tt>BlockTreeMap</tt> 大致相同。）
 *
 * <p>理论上此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
 * 实际实现中，此类在做映射操作的时候，会将传入 <tt>key</tt> 的 <tt>hash</tt> 按照从小到大的顺序做一个类双向链表的方式排列，
 * 可以非常快速的找到第一个和最后一个结点，同时可以根据第一个和最后一个结点进行有序迭代。
 * 注意：这个有序取决于 <tt>key</tt> 的 <tt>hash</tt> 值。
 * 因此，如果自定义 <tt>key</tt> ，并且 <tt>key</tt> 是一个从1开始一直到{@link Integer#MAX_VALUE}，中间允许有间断，
 * 那么 <tt>RangeTreeMap</tt> 在这种情况下，可以被看做是一个有序表。
 * 并且从1开始进行有序的不间断的传入将会使<tt>RangeTreeMap</tt> 类与<tt>BlockTreeMap</tt> 类效率达到最高。
 *
 * <p>哈希表的存储横向数组中，每一列都会维持一个B-Tree，
 *
 * @author Aberic on 2018/10/08 12:04
 * @version 1.0
 * @see
 * @since 1.0
 */
public class RangeTreeMap<K, V> extends AbstractTMap<K, V> implements TMap<K, V>, Serializable {

    private static final long serialVersionUID = 5066322126366247567L;

    /** 指定构造顶级结点范围对象的所属子结点数组大小 */
    private int rangeLength;
    /** 当前结点范围对象的根对象，祖宗结点 */
    private NodeRange<K, V> root;

    /**
     * 指定范围对象中的所属子结点数组大小进行构造，构造中初始化一个顶级结点范围对象
     *
     * <p>顶级结点范围对象为所有结点范围对象的祖宗结点，
     * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行传入赋值或切割，
     * 直到切割数量达到{@code {@link NodeRange#length} + 1}，即满足分裂条件。
     * 当满足分裂条件后，{@code RangeTreeMap.NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
     *
     * <p>初始化时结点所属范围起始位置默认0，结点所属范围终止位置默认无穷大
     *
     * <p>持续分割会维持一个B+Tree的模型。
     *
     * @param length 范围对象中的所属子结点数组大小
     */
    public RangeTreeMap(int length) {
        rangeLength = length;
        root = new NodeRange<>(rangeLength);
    }

    @Override
    public Range<K, V> range() {
        return root;
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 结点对象
     *
     * @author Aberic on 2018/10/7 15:25
     * @version 1.0
     * @see NodeRange
     * @since 1.0
     */
    static class Node<K, V> implements RangePair<K, V> {

        /** 存储key */
        final int hash;
        /** 传入key */
        final K key;
        /** 存储Value */
        V value;
        /** 上一个结点对象的Key，首结点默认上一结点为-1 */
        K preKey;
        /** 下一个结点对象的Key，末结点默认下一结点为-1 */
        K nextKey;

        Node(int hash, K key) {
            this.hash = hash;
            this.key = key;
        }

        @Override
        public final K getKey() { return key; }

        @Override
        public final V getValue() { return value; }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

    }

    static class NodeRange<K, V> implements Range<K, V> {

        /** 范围对象中的所属子结点数组大小 */
        private int length;
        /** 结点所属范围起始位置，默认0 */
        private int start = 0;
        /** 结点所属范围起始位置，默认无穷大 */
        private int end = -1;
        /** 结点数组，不可扩容 */
        private Node<K, V>[] nodes;
        /** 当前范围下的结点总数 */
        private int size;
        /** 当前结点范围的父对象，仅根可为null */
        private NodeRange parent;
        /** 当前结点范围的子对象数组，不可扩容 */
        private NodeRange<K, V>[] children;

        /**
         * 指定范围对象中的所属子结点数组大小进行构造，构造结果为顶级/虚结点范围对象
         *
         * <p>顶级/虚结点范围对象为所有结点范围对象的祖宗，它没有任何实际数据可操作意义，
         * 它的存在就是为了方便构造，但当第一个参数被put的时候，就会将结点的各个范围进行切割，
         * 直到切割数量达到({@code NodeRange#length} + 1)，即满足分裂条件。
         * 当满足分裂条件后，{@code NodeRange}开始诞生一个子结点范围数组对象，并继续按照上述条件进行后续分割操作。
         *
         * <p>持续分割会维持一个B+Tree的模型。
         *
         * @param length 范围对象中的所属子结点数组大小
         */
        public NodeRange(int length) {
            this.length = length;
            init(length, 0, -1, null);
        }

        /**
         * 内部使用结点间范围对象构造
         *
         * @param length    范围对象中的所属结点数组大小
         * @param start     结点所属范围起始位置，初始默认0
         * @param end       结点所属范围起始位置，初始默认无穷大
         * @param nodeRange 当前结点范围对象的父对象，仅根可为null
         */
        NodeRange(int length, int start, int end, NodeRange nodeRange) {
            init(length, start, end, nodeRange);
        }

        /**
         * 结点间范围对象初始化
         *
         * @param length    范围对象中的所属结点数组大小
         * @param start     结点所属范围起始位置，初始默认0
         * @param end       结点所属范围起始位置，初始默认无穷大
         * @param nodeRange 当前结点范围对象的父对象，仅根可为null
         */
        @SuppressWarnings("unchecked")
        void init(int length, int start, int end, NodeRange nodeRange) {
            this.length = length;
            this.start = start;
            this.end = end;
            this.size = 0;
            parent = nodeRange;
            // 结点数组根据设定执行初始化
            nodes = (Node<K, V>[]) new Node[length];
            // 子range集合的数量必为结点集合的大小+1
            children = new NodeRange[length + 1];
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void setStart(int start) {
            if (parent == null) {
                throw new RuntimeException();
            }
            this.start = start;
        }

        @Override
        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public boolean inRange(int key) {
            return key >= start && key < end;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Override
        public V put(K key, V value) {
            return putVal(hash(key), key, value);
        }

        final V putVal(int hash, K key, V value) {
            // TODO: 2018/10/7
            if (null == parent) { // 自身为顶级NodeRange

            } else { // 正常存储直到最底层指定顺序位

            }
            return null;
        }

        @Override
        public boolean contains(Object key) {
            return false;
        }

    }

}
